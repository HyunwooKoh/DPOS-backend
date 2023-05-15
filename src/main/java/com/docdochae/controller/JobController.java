package com.docdochae.controller;

import com.docdochae.common.CommonApi;
import com.docdochae.common.Error.ErrorCode;
import com.docdochae.common.ReqType;
import com.docdochae.common.Error.Error;
import com.docdochae.db.RepoManager;
import com.docdochae.db.task.TaskEntity;
import com.docdochae.db.task.TaskRepository;
import com.docdochae.executer.OCRTaskExecutorService;
import com.docdochae.controller.Dto.JobDto;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/job")
public class JobController {
    private final OCRTaskExecutorService ocrTaskExecutorService;
    @Autowired
    public JobController(OCRTaskExecutorService ocrTaskExecutorService) {
        this.ocrTaskExecutorService = ocrTaskExecutorService;
    }
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    TaskRepository taskRepository;

    @Autowired
    RepoManager repoManager;

    @SneakyThrows
    private void transferTo(MultipartFile multipartFile, String uuid, String ext) {
        multipartFile.transferTo(new File(CommonApi.CreateAndGetTempDir(uuid) + "origin" + ext ));
    }

    @PostMapping(value = "/extract", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public JobDto.UuidsResponse extract(@ModelAttribute JobDto.JobRequestForm dto) {
        String reqType = dto.getReqType();
        List<String> uuids = new ArrayList<>();
        dto.getFiles().stream()
                .forEach(multipartFile -> {
                    String uuid = CommonApi.generateUuid();
                    String fileName = multipartFile.getOriginalFilename();
                    String ext = fileName.substring(fileName.lastIndexOf('.'));
                    transferTo(multipartFile, uuid, ext);
                    uuids.add(uuid);
                    ocrTaskExecutorService.addTask(uuid, ext, reqType);
                });
        JobDto.UuidsResponse res = new JobDto.UuidsResponse();
        res.setUuids(uuids);
        return res;
    }

    // TODO: Refact to doesn't use reqType -> use query with uuid to get reqType
    @PostMapping(value = "/submit", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public JobDto.SubmitResponse submit(@ModelAttribute JobDto.SubmitRequestForm dto) {
        String reqType = dto.getReqType();
        JobDto.SubmitResponse res = new JobDto.SubmitResponse();
        res.setStatus("Success");
        if (!dto.getData().containsKey("uuid")) {
            res.setStatus("Failure");
            res.setUuids("None");
            res.setErrMsg("Data must contain \"uuid\"");
            res.setErrorCode(ErrorCode.UUID_DOES_NOT_EXIST);
        } else {
            try {
                if (reqType.equals(ReqType.REQ_TYPE_Resume)) {
                    repoManager.submitResumeEntity(dto.getData());
                } else if (reqType.equals(ReqType.REQ_TYPE_PrsInfo)) {
                    repoManager.submitPrsInfoEntity(dto.getData());
                } else {
                    res.setStatus("Failure");
                    res.setUuids(dto.getData().getString("uuid"));
                    res.setErrMsg("Invalid UUid");
                    res.setErrorCode(ErrorCode.INVALID_REQ_TYPE);
                }
            } catch (Error e) {
                res.setStatus("Failure");
                res.setUuids(dto.getData().getString("uuid"));
                res.setErrMsg(e.msg());
                res.setErrorCode(e.code());
            }
        }
        return res;
    }

    // TODO: Refact to doesn't use reqType -> use query with uuid to get reqType
    @GetMapping(value = "/result")
    public JobDto.ResultResponse result(@RequestBody JobDto.ResultRequestJTO dto) {
        JobDto.ResultResponse res = new JobDto.ResultResponse();
        switch (dto.getReqType()) {
            case "Resume" ->res.setResData(repoManager.getResumeResultByUuid(dto.getUuid()));
            case "PrsInfo" -> res.setResData(repoManager.getPrsResultByUuid(dto.getUuid()));
        }
        res.setImageUrl("/file/resultImage/" + dto.getUuid());
        return res;
    }

    @GetMapping(value = "/status")
    public JobDto.StatusResponse checkTaskStatus(@RequestBody JobDto.JobStatusJto dto) {
        JobDto.StatusResponse res = new JobDto.StatusResponse();
        TaskEntity entity = taskRepository.findByUuid(dto.getUuid());
        res.setUuid(entity.getUuid());
        res.setStatus(entity.getStatus());
        return res;
    }

    @GetMapping(value = "/error")
    public JobDto.ErrorResponse getErrorInfo(@RequestBody JobDto.JobStatusJto dto) {
        TaskEntity entity = taskRepository.findByUuid(dto.getUuid());
        JobDto.ErrorResponse res = new JobDto.ErrorResponse();
        res.setUuid("");
        res.setErrorCode(0);

        if (entity == null) {
            res.setErrorMsg("Cannot found the uuid.");
        } else if (!entity.getStatus().equals("Failure")) {
            res.setUuid(entity.getUuid());
            res.setErrorMsg("The task has not benn failed.");
        } else {
            res.setUuid(entity.getUuid());
            res.setErrorCode(entity.getErrorCode());
            res.setErrorMsg(entity.getErrorMsg());
        }
        return res;
    }
}
