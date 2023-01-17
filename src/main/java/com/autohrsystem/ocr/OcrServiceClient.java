package com.autohrsystem.ocr;

import com.autohrsystem.common.Error;
import com.autohrsystem.structure.OcrParams;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;
import java.util.HashMap;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Service;

import io.vertx.core.json.JsonObject;
import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@Service
public abstract class OcrServiceClient {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	public static final String REQ_DOCUMENT = "Document";
	private final OcrParams m_param;

	public OcrServiceClient(OcrParams params) {
		m_param = params;
	}

	public static String appendPath(String url, String path) {
		return url + (url.endsWith("/") ? "" : "/") + path;
	}

    public String register() {
        // TODO: requestOCR - waitPolling - parseResult - insertToDB
        // return : new data's key
        return "";
    }

    public void getResultFile(String resFilePath) {
        // TODO: getResult json file from OCR server
    }

    public Map<String,String> getTargetData(String resFilePath) {
        // TODO: parseData and get target data
        Map<String, String> datas = new HashMap<String, String>();
        datas.put("","");
        return datas;
    }

    public void insertToDB(Map<String,String> data) {
        // TODO: insert data to DB
    }

	/*{"Code": "OK", "Message": "TASK-ID"}*/
    protected String push(String uuid, String url, OcrParams params) throws Error {
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
			// TODO : throw Error
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
								logger.error("API 요청 중 4xx 에러가 발생했습니다. 요청 데이터를 확인해주세요.");
							} else {
								logger.error("API 요청 중 Tree 서버에서 5xx 에러가 발생했습니다.");
							}
							return new JsonObject();
						})
				);
	}

    protected boolean pull(String taskID, int maxRecursion) throws Error {
		File resultJson = new File(m_param.m_outputUri);
		if (resultJson.exists()) {
			// TODO: throw Error
		}

		logger.info("send /pull request...");
		JsonObject response = new JsonObject();
		JsonObject body = new JsonObject();
		body.put("TaskID", taskID);
		int tryCount = 0;
		for (; tryCount < maxRecursion; tryCount++) {
			response = exchangePullRequest(body).block();
			if (response == null || response.isEmpty()) {
				// TODO : throw Error
			} else if (response.getJsonObject("response") == null) {
				// TODO: throw Error
			}

			if (response.getJsonObject("response").getString("status") == "success") {
				break;
			}
		}

		if (tryCount == maxRecursion) {
			// TODO: thrwo Error
		}
		try {
			if (resultJson.createNewFile()) {
				logger.error("Error occur during create file on server, file path : " + resultJson.getAbsolutePath());
				return false;
			}
			FileWriter file = new FileWriter(resultJson.getAbsolutePath());
			file.write(response.toString());
			file.flush();
			file.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return true;
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
								logger.error("API 요청 중 4xx 에러가 발생했습니다. 요청 데이터를 확인해주세요.");
							} else {
								logger.error("API 요청 중 Tree 서버에서 5xx 에러가 발생했습니다.");
							}
							return new JsonObject();
						})
				);
	}
}