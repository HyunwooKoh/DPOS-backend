package com.autohrsystem;

import com.autohrsystem.common.CommonApi;
import com.autohrsystem.executer.OCRTaskExecutorService;

import java.io.File;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class HRSystemController {

    private final OCRTaskExecutorService ocrTaskExecutorService;

    @Autowired
    public HRSystemController(OCRTaskExecutorService ocrTaskExecutorService) {
        this.ocrTaskExecutorService = ocrTaskExecutorService;
    }

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Getter
    @Setter
    @AllArgsConstructor
    public class ExtractBody {
        private String reqType;
        private List<MultipartFile> files;
    }

    @PostMapping(value = "/job/extract", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void extract(@ModelAttribute ExtractBody dto) {
        String reqType = dto.getReqType();
        dto.getFiles().stream()
                .forEach(multipartFile -> {
                    String uuid = CommonApi.generateUuid();
                    String fileName = multipartFile.getOriginalFilename();
                    String ext = fileName.substring(fileName.lastIndexOf('.'));
                    transferTo(multipartFile, uuid, ext);
                    // TODO: commonAPI :: getExt()
                    ocrTaskExecutorService.addTask(uuid, ext, reqType);
                });
    }

    @SneakyThrows
    private void transferTo(MultipartFile multipartFile, String uuid, String ext) {
        multipartFile.transferTo(new File(CommonApi.getAndCreateTempDir(uuid) + "origin" + ext ));
    }

    @GetMapping("/common/getUuid")
    public String generateUuid() {
        String uuid = CommonApi.generateUuid();
        logger.info("generated uuid : " + uuid);
        return uuid;
    }

}
