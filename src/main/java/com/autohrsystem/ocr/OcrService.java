package com.autohrsystem.ocr;

import com.autohrsystem.ocr.OcrServiceClient;
import com.autohrsystem.structure.OcrSubTaskArguments;

import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class OcrService {
    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    Vertx vertx;

    private Map<String, String> versionMap = new HashMap<>();
    private JsonObject mReqOption = new JsonObject();

    public void buildJsonObject(String version) {
        // TODO: build json Object for each version
        // mReqOption.put("","");
    }

    public OcrServiceClient create(String version) {
        int major = Integer.parseInt(version.split("\\.")[0]);
        return applicationContext.getBean(OcrServiceClient.class);
    }

    public String getVersion(OcrSubTaskArguments params) {
        String url = params.get("url");
        if (versionMap.get(url) == null) {
            String serviceInfo = OcrServiceClient.appendPath(url, "service_info");
            CompletableFuture<String> versionFuture = new CompletableFuture<>();

            WebClient.create(vertx).getAbs(serviceInfo)
                    .send(event -> {
                        if (event.succeeded() && completeVersionFuture(event, versionFuture)) { return; }
                        versionFuture.complete("1.0.0");
                    });
            try {
                versionMap.put(url, versionFuture.get(30, TimeUnit.SECONDS));
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }
        return versionMap.getOrDefault(url, "1.0.0");
    }

    private boolean completeVersionFuture(AsyncResult<HttpResponse<Buffer>> event, CompletableFuture<String> versionFuture) {
        HttpResponse<Buffer> result = event.result();
        if (result.statusCode() < 400) {
            String build = result.bodyAsJsonObject().getString("TXS.Build");
            int buildNo = Integer.parseInt(build.substring(1));
            if (buildNo >= 20617) {
                versionFuture.complete("2.0.0");
                return true;
            }
        }
        return false;
    }
}
