package com.autohrsystem;

import com.autohrsystem.common.CommonApi;
import com.autohrsystem.executer.HRTaskExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.Queue;

public class HRSystemController {
    private Queue<HRTaskExecutor> m_taskQueue;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @PostMapping("/job/extract")
    public void extract(@RequestBody Map<String, Object> param) {
        String uuid = (String)param.get("uuid");
        String reqType = (String)param.get("Type");
        String ext = (String)param.get("ext");

        HRTaskExecutor task = new HRTaskExecutor(uuid, reqType, ext);
        m_taskQueue.add(task);
    }

    @GetMapping("/common/getUuid")
    public String generateUuid() {
        String uuid = CommonApi.generateUuid();
        logger.info("generated uuid : " + uuid);
        return uuid;
    }

    @Scheduled(fixedDelay = 100)
    private void pollingTask() {
        if (m_taskQueue.size() == 0) return;

        // TODO : get first element and run task as taskExecutor
    }
}
