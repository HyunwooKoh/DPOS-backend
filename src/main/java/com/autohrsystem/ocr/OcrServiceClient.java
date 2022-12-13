package com.autohrsystem.ocr;

import com.autohrsystem.structure.OcrSubTaskArguments;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.multipart.MultipartForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.autohrsystem.structure.registRequest;

import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@RestController
@Service
public abstract class OcrServiceClient {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private static JsonObject mReqOption;
	public static final String REQ_DOCUMENT = "Document";
	protected final WebClient client;

	public OcrServiceClient(Vertx vertx) {
		this.client = WebClient.create(vertx);
	}

	public static String appendPath(String url, String path) {
		return url + (url.endsWith("/") ? "" : "/") + path;
	}

	public static void setReqOption(JsonObject json) {
		mReqOption = json;
	}

	@PostMapping("/register")
    public String registNewDocument(@RequestBody registRequest body) {
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

    public String generateKey() {
        // TODO : generate new key for new data
        return "";
    }

    protected Future<String> push(String uuid, String url, OcrSubTaskArguments params) {
		// TODO : get MultipartForm form fileHandler
		//  MultipartForm form =
		//	MultipartForm.create().binaryFileUpload("ReqFile", resource.getName(), resource.getAbsolutePath(),
		//		"image/jpeg");
		
		MultipartForm form = null;
		form.attribute("ReqType", params.get("reqType"));
		form.attribute("ReqOption", mReqOption.toString());
		CompletableFuture<String> pushFuture = new CompletableFuture<>();
		logger.info("send /push request...");
		client.postAbs(appendPath(url, "push"))
			.putHeader("content-type", "multipart/form-data")
			.sendMultipartForm(form, asyncResult -> handlePush(asyncResult, new File(/*TODO: get file path form db by uuid*/""), pushFuture));
		return pushFuture;
	}

    protected void handlePush(final AsyncResult<HttpResponse<Buffer>> asyncResult, final File resource, final CompletableFuture<String> pushFuture) {
		logger.info("received /push response... : {}", asyncResult.result());
		if (asyncResult.succeeded()) {
			JsonObject jsonObject = null;
			try {
				jsonObject = asyncResult.result().bodyAsJsonObject();
			} catch (Exception e) {
				throwPushError("OCR Server의 응답을 JsonObject로 파싱하던 중 에러가 발생했습니다.", asyncResult.result(), resource);
				return;
			}
			String code = jsonObject.getString("Code");
			if ("Error".equalsIgnoreCase(code)) {
				String message = jsonObject.getString("Message");
				throwPushError("[push response] " + message, asyncResult.result(), resource);
				return;
			}
			String OCRId = jsonObject.getString("Message");
			logger.info("OCRId: {}", OCRId);
			pushFuture.complete(OCRId);
		} else {
			throwPushError("파일 전송 중 에러가 발생했습니다.", asyncResult.result(), resource);
		}
	}

    protected void throwPushError(String message, HttpResponse<Buffer> result, File resource) {
		//TODO : implement throw
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