package com.docdochae.db.task;

import lombok.*;
import jakarta.persistence.*;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name="OCRTask")
public class TaskEntity {
    @Id
    private String uuid;

    @Column(nullable = false, length = 10)
    private String status;

    @Column(nullable = false, length = 200)
    private String inputFilePath;

    @Column(nullable = false, length = 100)
    private String outputFilePath;

    @Column(nullable = true)
    private int errorCode;

    @Column(nullable = true)
    private String errorMsg;

    public TaskEntity(String uuid, String status, String inputFilePath, String outputFilePath) {
        this.uuid = uuid;
        this.status = status;
        this.inputFilePath = inputFilePath;
        this.outputFilePath = outputFilePath;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
