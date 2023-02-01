package com.autohrsystem.executer;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.autohrsystem.common.CommonApi;
import com.autohrsystem.common.Error;
import com.autohrsystem.executer.task.OcrTask;
import com.autohrsystem.file.FileHandler;
import com.autohrsystem.ocr.OcrParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class HRTaskExecutorService {
    @Autowired
    private Environment env;
    @Autowired
    @Qualifier("OCRTaskExecutor")
    ThreadPoolTaskExecutor m_taskExecutor;
    Queue<OcrTask> m_tasks;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public HRTaskExecutorService() {
        m_tasks = new LinkedList<>();
    }

    @Async
    public void addTask(String uuid, String ext, String reqType) {
        String inputFilePath = CommonApi.getTempDir(uuid) + "origin." + ext;
        String outputFilePath = CommonApi.getTempDir(uuid) + "result.json";
        FileHandler fileHandler = new FileHandler(uuid, ext, inputFilePath, outputFilePath);
        OcrParams param = new OcrParams(inputFilePath, outputFilePath, env.getProperty("OCR_SERVER_URL"));

        if (!reqType.isEmpty() && param.isValidReqType(reqType)) {
            logger.error("Invalid reqType. uuid : " + uuid + ", reqType : " + reqType);
            // TODO : insert task in db with failure status - input file error
            return;
        }

        try {
            // TODO : use fileHandler job as threadPoolExecutor
            fileHandler.getFile();
        } catch (Error e) {
            // TODO : insert task in db with failure status - input file error
        }

        // TODO : insert task in db with waiting status
        OcrTask task = new OcrTask(param, fileHandler);
        if (m_taskExecutor.getQueueCapacity() == m_taskExecutor.getQueueCapacity()) {
            logger.info(getClass().toString() + ": m_taskExecutor reached max queue size. doubled size");
            m_taskExecutor.setQueueCapacity(m_taskExecutor.getQueueCapacity() * 2);
        }
        m_tasks.add(task);
    }

    @Scheduled(fixedDelay = 100)
    private void pollingTask() {
        if (!m_tasks.isEmpty()) {
            m_taskExecutor.execute(Objects.requireNonNull(m_tasks.peek()));
        }
    }
}
