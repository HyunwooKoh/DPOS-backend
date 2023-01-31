package com.autohrsystem.ocr;

import com.autohrsystem.common.Error;
import com.autohrsystem.common.ErrorCode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import io.vertx.core.json.JsonObject;
import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OcrServiceClient {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private static final String REQ_DOCUMENT = "Document";
	private static final int MAX_RECURSION = 30;
	private final OcrParams m_param;

	public OcrServiceClient(OcrParams params) {
		m_param = params;
	}

	public static String appendPath(String url, String path) {
		return url + (url.endsWith("/") ? "" : "/") + path;
	}

    public void DoTask() throws Error{
		String taskID = push();
		pull(taskID);
    }

	/*{"Code": "OK", "Message": "TASK-ID"}*/
    protected String push() throws Error {
		File inputFile = new File(m_param.m_inputUri);
		MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
		try {
			bodyBuilder.part("ReqFile", new ByteArrayResource(Files.readAllBytes(inputFile.toPath())));
		} catch (IOException ioError) {
			throw new RuntimeException(ioError);
		}
		bodyBuilder.part("ReqType", REQ_DOCUMENT);
		bodyBuilder.part("ReqOption", m_param.m_reqOption);

		logger.info("send /push request...");
		JsonObject response = exchangePushRequest(bodyBuilder).block();
		if (response == null || response.isEmpty()) {
			throw new Error(ErrorCode.OCR_PUSH_ERROR, "Push response is null or empty.");
		} else if (response.getString("Code") == null || response.getString("Message") == null) {
			throw new Error(ErrorCode.OCR_PUSH_ERROR, "Invalid push response json. response : " + response.toString());
		} else if (!Objects.equals(response.getString("Code"), "OK")) {
			String responseCode = response.getString("Code");
			// TODO: Specify error message
			String msg = "";
			if (Objects.equals(responseCode, "")) {
				msg = "";
			}
			throw new Error(ErrorCode.OCR_PUSH_ERROR, "Error occur during push request. message : " + msg);
		}
		return response.getString("Message");
	}

	private Mono<JsonObject> exchangePushRequest(MultipartBodyBuilder bodyBuilder) throws Error{
		return WebClient
				.create()
				.method(HttpMethod.POST)
				.uri(m_param.m_serverUrl + "/push")
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.body(BodyInserters.fromMultipartData(bodyBuilder.build()))
				.exchangeToMono(clientResponse -> clientResponse.bodyToMono(JsonObject.class)
						.map(validReqVo -> {
							if (clientResponse.statusCode().is2xxSuccessful()) {
								logger.info("");
								return validReqVo;
							} else if (clientResponse.statusCode().is4xxClientError()) {
								logger.error("4xx error occur during pull request.");
							} else {
								logger.error("5xx error occur during pull request.");
							}
							return new JsonObject();
						})
				);
	}

    protected void pull(String taskID) throws Error {
		File resultJson = new File(m_param.m_outputUri);
		if (resultJson.exists()) {
			throw new Error(ErrorCode.OCR_RESULT_EXIST, "Result json already exist. path : " + resultJson.getAbsolutePath());
		}

		JsonObject response = new JsonObject();
		JsonObject body = new JsonObject();
		body.put("TaskID", taskID);
		int tryCount = 0;
		for (; tryCount < MAX_RECURSION; tryCount++) {
			logger.info("send /pull request... tryCount : " + tryCount);
			response = exchangePullRequest(body).block();
			if (response == null || response.isEmpty()) {
				throw new Error(ErrorCode.OCR_PULL_ERROR, "Pull response is null or empty.");
			} else if (response.getJsonObject("response") == null) {
				throw new Error(ErrorCode.OCR_PULL_ERROR, "Invalid pull response json, response : " + response.toString());
			} else if (response.getJsonObject("response").getString("status").equals("failure")) {
				throw new Error(ErrorCode.OCR_PULL_ERROR, "Pull response return fail, msg : " + response.getJsonObject("response").getString("message"));
			}

			if (response.getJsonObject("response").getString("status").equals("success")) {
				break;
			}
		}

		if (tryCount == MAX_RECURSION) {
			throw new Error(ErrorCode.OCR_PULL_ERROR, "Pull request max tried. task ID : " + taskID);
		}

		try {
			if (!resultJson.createNewFile()) {
				throw new Error(ErrorCode.OCR_RESULT_SAVE, "Error occur during create file. path : " + resultJson.getAbsolutePath());
			}
			FileWriter file = new FileWriter(resultJson.getAbsolutePath());
			file.write(response.toString());
			file.flush();
			file.close();
		} catch (IOException e) {
			logger.error("Error Occur during save ocr result json. path : " + resultJson.getAbsolutePath());
			throw new RuntimeException(e);
		}
	}

    protected Mono<JsonObject> exchangePullRequest(JsonObject body) {
		return WebClient
				.create()
				.method(HttpMethod.POST)
				.uri(m_param.m_serverUrl + "/pull")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(body)
				.exchangeToMono(clientResponse -> clientResponse.bodyToMono(JsonObject.class)
						.map(validReqVo -> {
							if (clientResponse.statusCode().is2xxSuccessful()) {
								return validReqVo;
							} else if (clientResponse.statusCode().is4xxClientError()) {
								logger.error("4xx error occur during pull request.");
							} else {
								logger.error("5xx error occur during pull request.");
							}
							return new JsonObject();
						})
				);
	}
}