package com.autohrsystem.ocr;

import com.autohrsystem.common.Error;
import com.autohrsystem.structure.OcrParams;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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
		return response.getString("id");
	}

	private Mono<JsonObject> exchangePushRequest(MultipartBodyBuilder bodyBuilder) throws Error{
		return WebClient
				.create()
				.method(HttpMethod.POST)
				.uri(m_param.m_serverUrl)
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

    protected CompletableFuture<JsonObject> pull(final String url, final String OCRId, int maxRecursion) {
		CompletableFuture<JsonObject> pullFuture = new CompletableFuture<>();
		logger.info("send /pull request...");
		pull(pullFuture, url, OCRId, 0, maxRecursion);
		return pullFuture;
	}

    protected void pull(final CompletableFuture<JsonObject> pullFuture, final String url, final String OCRId,
						int recursion, int maxRecursion) {
		MultipartForm form = MultipartForm.create().attribute("TaskID", OCRId);
		client.postAbs(appendPath(url, "pull"))
			.putHeader("content-type", "multipart/form-data")
			.sendMultipartForm(form, ar -> handlePull(ar, url, OCRId, pullFuture, recursion, maxRecursion));
	}

    protected abstract boolean handlePull(String id, JsonObject body, CompletableFuture<JsonObject> pullFuture);

    protected void handlePull(final AsyncResult<HttpResponse<Buffer>> ar,
							  final String url, final String OCRId,
							  final CompletableFuture<JsonObject> pullFuture,
							  int recursion, int maxRecursion) {
		logger.info("received /pull response... : {}", ar.result());

		if (ar.succeeded()) {
			JsonObject body = ar.result().bodyAsJsonObject();
			logger.debug("OCR polling succeeded : {}", body);
			if (handlePull(OCRId, body, pullFuture)) { return; }
		}
		if (recursion >= maxRecursion) {
			// TODO: implement throw
			return;
		}
		try { TimeUnit.SECONDS.sleep(3); } catch (InterruptedException ignored) { }
		logger.info("resend /pull request... : {}", recursion + 1);
		pull(pullFuture, url, OCRId, recursion + 1, maxRecursion);
	}

}