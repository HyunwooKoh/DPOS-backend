package com.autohrsystem.controller;

import com.autohrsystem.common.CommonApi;
import com.autohrsystem.db.task.TaskEntity;
import com.autohrsystem.db.task.TaskRepository;
import com.autohrsystem.executer.OCRTaskExecutorService;
import com.autohrsystem.controller.Dto.JobDto;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
                    // TODO: commonAPI :: getExt()
                    uuids.add(uuid);
                    ocrTaskExecutorService.addTask(uuid, ext, reqType);
                });
        JobDto.UuidsResponse res = new JobDto.UuidsResponse();
        res.setUuids(uuids);
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
        } else if (entity.getStatus().equals("Failure")) {
            res.setUuid(entity.getUuid());
            res.setErrorMsg("The task has benn succeed.");
        } else {
            res.setUuid(entity.getUuid());
            res.setErrorCode(entity.getErrorCode());
            res.setErrorMsg(entity.getErrorMsg());
        }
        return res;
    }
}
