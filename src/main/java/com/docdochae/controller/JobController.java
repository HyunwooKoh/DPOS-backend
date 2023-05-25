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
import java.util.Objects;

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

    @PostMapping(value = "/submit")
    public JobDto.SubmitResponse submit(@RequestBody JobDto.SubmitRequestJtO dto) {
        JobDto.SubmitResponse res = new JobDto.SubmitResponse();
        res.setStatus("Success");
        String reqType = repoManager.getReqTypeByUuid(dto.getUuid());

        if (Objects.equals(reqType, "Invalid")) {
            res.setStatus("Failure");
            res.setUuids("None");
            res.setErrMsg("Invalid \"uuid\"");
            res.setErrorCode(ErrorCode.UUID_DOES_NOT_EXIST);
        } else {
            try {
                if (reqType.equals(ReqType.REQ_TYPE_Resume)) {
                    repoManager.submitResumeEntity(dto.getUuid(), dto.getData());
                } else if (reqType.equals(ReqType.REQ_TYPE_PrsInfo)) {
                    repoManager.submitPrsInfoEntity(dto.getUuid(), dto.getData());
                }
            } catch (Error e) {
                res.setStatus("Failure");
                res.setUuids(dto.getUuid());
                res.setErrMsg(e.msg());
                res.setErrorCode(e.code());
            }
        }
        return res;
    }

    @GetMapping("/result/{uuid}")
    public JobDto.ResultResponse result(@PathVariable String uuid) {
        JobDto.ResultResponse res = new JobDto.ResultResponse();
        switch (repoManager.getReqTypeByUuid(uuid)) {
            case "Resume" ->res.setResData(repoManager.getResumeResultByUuid(uuid));
            case "PrsInfo" -> res.setResData(repoManager.getPrsResultByUuid(uuid));
        }
        res.setImageUrl("/file/resultImage/" + uuid);
        return res;
    }

    @GetMapping("/status/{uuid}")
    public JobDto.StatusResponse checkTaskStatus(@PathVariable String uuid) {
        JobDto.StatusResponse res = new JobDto.StatusResponse();
        TaskEntity entity = taskRepository.findByUuid(uuid);
        res.setUuid(entity.getUuid());
        res.setStatus(entity.getStatus());
        return res;
    }

    @GetMapping(value = "/error/{uuid}")
    public JobDto.ErrorResponse getErrorInfo(@PathVariable String uuid) {
        TaskEntity entity = taskRepository.findByUuid(uuid);
        JobDto.ErrorResponse res = new JobDto.ErrorResponse();
        res.setUuid(uuid);
        res.setErrorCode(0);

        if (entity == null) {
            res.setErrorMsg("Cannot found the uuid.");
        } else if (!entity.getStatus().equals("Failure")) {
            res.setErrorMsg("The task has not benn failed.");
        } else {
            res.setErrorCode(entity.getErrorCode());
            res.setErrorMsg(entity.getErrorMsg());
        }
        return res;
    }
}
