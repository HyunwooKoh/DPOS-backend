package com.autohrsystem.controller.Dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class JobDto {
    @Getter
    @Setter
    @AllArgsConstructor
    public class JobRequestForm {
        private String reqType;
        private List<MultipartFile> files;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobStatusJto {
        private String uuid;
    }

    @Setter
    @Getter
    public static class UuidsResponse {
        private List<String> uuids;
    }

    @Setter
    @Getter
    public static class StatusResponse {
        private String uuid;
        private String Status;
    }
}
