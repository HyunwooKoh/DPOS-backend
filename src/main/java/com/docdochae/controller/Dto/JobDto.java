package com.docdochae.controller.Dto;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public class JobDto {
    @Getter
    @Setter
    @AllArgsConstructor
    public static class JobRequestForm {
        private String reqType;
        private List<MultipartFile> files;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class SubmitRequestJtO {
        private String uuid;
        private Map<String, String> data;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultRequestJTO {
        private String uuid;
        private String reqType;
    }

    @Setter
    @Getter
    public static class UuidsResponse {
        private List<String> uuids;
    }

    @Setter
    @Getter
    public static class SubmitResponse {
        private String uuids;
        private String status;
        private String errMsg;
        private int errorCode;
    }

    @Setter
    @Getter
    public static class ResultResponse {
        String imageUrl;
        JsonObject resData;
    }

    @Setter
    @Getter
    public static class StatusResponse {
        private String uuid;
        private String Status;
    }

    @Setter
    @Getter
    public static class ErrorResponse {
        private String uuid;
        private String ErrorMsg;
        private int errorCode;
    }
}
