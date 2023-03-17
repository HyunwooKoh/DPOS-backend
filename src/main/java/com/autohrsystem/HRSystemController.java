package com.autohrsystem;

import com.autohrsystem.common.CommonApi;

import com.autohrsystem.common.ExtractBody;
import com.autohrsystem.executer.OCRTaskExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
public class HRSystemController {
    private final OCRTaskExecutorService m_taskExecutor = new OCRTaskExecutorService();
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @PostMapping(value = "/job/extract", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public void extract(@ModelAttribute ExtractBody dto) {
        System.out.println(dto);
        String uuid = CommonApi.generateUuid();
        String reqType = dto.getReqType();
        String ext = dto.getExt();
            dto.getFiles()
                    .map(MultipartFile::getName)
                    .subscribe(System.out::println);
//                    .transferTo(new File(CommonApi.getTempDir(uuid) + "origin" + "ext"));

        m_taskExecutor.addTask(uuid, ext, reqType);
    }

    @GetMapping("/common/getUuid")
    public String generateUuid() {
        String uuid = CommonApi.generateUuid();
        logger.info("generated uuid : " + uuid);
        return uuid;
    }

}
