package com.autohrsystem.controller;

import com.autohrsystem.common.CommonApi;
import com.autohrsystem.executer.OCRTaskExecutorService;
import com.autohrsystem.controller.Dto.JobDto;

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

    @SneakyThrows
    private void transferTo(MultipartFile multipartFile, String uuid, String ext) {
        multipartFile.transferTo(new File(CommonApi.getAndCreateTempDir(uuid) + "origin" + ext ));
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
    public JobDto.StatusResponse checkTaskStatus(@RequestBody JobDto.JobStatusDto dto) {
        JobDto.StatusResponse res = new JobDto.StatusResponse();
        res.setUuid("");
        res.setStatus("");
        return res;
    }
}
