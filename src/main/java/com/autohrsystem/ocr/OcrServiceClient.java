package com.autohrsystem.ocr;

import com.autohrsystem.common.Error;
import com.autohrsystem.common.ErrorCode;
import io.vertx.core.json.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;

import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

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

    public <R> R DoTask(Function<JsonObject, R> handler) throws Error {
        String taskID = push();
        return handler.apply(pull(taskID));
    }

    /*{"Code": "OK", "Message": "TASK-ID"}*/
    protected String push() throws Error {
        File inputFile = new File(m_param.m_inputUri);
        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("ReqFile", new FileSystemResource(inputFile));
        requestBody.add("ReqType", REQ_DOCUMENT);
        requestBody.add("ReqOption", m_param.m_reqOption.toString());

        logger.info("send /push request...");
        JsonObject response = exchangePushRequest(requestBody);
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

    private JsonObject exchangePushRequest(MultiValueMap<String, Object> bodyBuilder) throws Error {
        UriComponents uriBuilder = UriComponentsBuilder.fromUriString("http://" + m_param.m_serverUrl + "/push")
                .build();
        RequestEntity<MultiValueMap<String, Object>> entity = RequestEntity.post(uriBuilder.toUriString())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(bodyBuilder);

        RestTemplate restTemplate = new RestTemplate();
        return new JsonObject(restTemplate.exchange(entity, String.class).getBody());
    }

    protected JsonObject pull(String taskID) throws Error {
        JsonObject response = new JsonObject();
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("TaskID", taskID);
        int tryCount = 0;
        for (; tryCount < MAX_RECURSION; tryCount++) {
            logger.info("send /pull request... tryCount : " + tryCount);
            response = exchangePullRequest(body);
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
        return response;
    }

    protected JsonObject exchangePullRequest(MultiValueMap<String, Object> bodyBuilder) {
        UriComponents uriBuilder = UriComponentsBuilder.fromUriString("http://" + m_param.m_serverUrl + "/pull")
                .build();
        RequestEntity<MultiValueMap<String, Object>> entity = RequestEntity.post(uriBuilder.toUriString())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(bodyBuilder);

        RestTemplate restTemplate = new RestTemplate();
        return new JsonObject(restTemplate.exchange(entity, String.class).getBody());
    }
}