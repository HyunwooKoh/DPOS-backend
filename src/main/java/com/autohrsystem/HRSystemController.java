package com.autohrsystem;

import com.autohrsystem.common.CommonApi;

import com.autohrsystem.executer.HRTaskExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HRSystemController {
    private final HRTaskExecutorService m_taskExecutor = new HRTaskExecutorService();
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @PostMapping("/job/extract")
    public void extract(@RequestBody Map<String, Object> param) {
        String uuid = (String)param.get("uuid");
        String reqType = (String)param.get("Type");
        String ext = (String)param.get("ext");

        m_taskExecutor.addTask(uuid, reqType, ext);
    }

    @GetMapping("/common/getUuid")
    public String generateUuid() {
        String uuid = CommonApi.generateUuid();
        logger.info("generated uuid : " + uuid);
        return uuid;
    }

}
