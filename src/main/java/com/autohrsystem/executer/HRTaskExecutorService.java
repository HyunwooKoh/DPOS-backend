package com.autohrsystem.executer;

import java.util.Queue;
import java.util.concurrent.ThreadPoolExecutor;

import com.autohrsystem.common.CommonApi;
import com.autohrsystem.executer.task.OcrTask;
import com.autohrsystem.structure.OcrParams;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class HRTaskExecutorService {
    @Autowired
    private Environment env;
    // TODO : make request option as class
    private final JsonObject TYPE_1_REQ_OPT = new JsonObject("");
    private final JsonObject TYPE_2_REQ_OPT = new JsonObject("");

    Queue<OcrTask> m_tasks;
    ThreadPoolTaskExecutor m_taskExecutor;

    public HRTaskExecutorService() {
        m_taskExecutor = new ThreadPoolTaskExecutor();
        m_taskExecutor.setCorePoolSize(1);
        m_taskExecutor.setMaxPoolSize(1);
        m_taskExecutor.setQueueCapacity(100);
        m_taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        m_taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        m_taskExecutor.setAwaitTerminationSeconds(60);
    }

    public void addTask(String uuid, String reqType, String ext) {
        m_tasks.add(new OcrTask(buildOcrParam(uuid, reqType, ext)));
    }

    private OcrParams buildOcrParam(String uuid, String reqType, String ext) {
        String inputFilePath = CommonApi.getTempDir(uuid) + "origin" + ext;
        String outputFilePath = CommonApi.getTempDir(uuid) + "result.json";

        //FileHandler fileHandler = new FileHandler(m_uuid, m_ext, inputFilePath, outputFilePath);
        OcrParams ocrParam = new OcrParams(inputFilePath, outputFilePath, env.getProperty("OCR_SERVER_URL"));
        if (reqType == "type1") {
            ocrParam.setReqOption(TYPE_1_REQ_OPT);
        } else if (reqType == "type2") {
            ocrParam.setReqOption(TYPE_2_REQ_OPT);
        } else {
            // TODO: throw Error - invalid type
        }
        return ocrParam;
    }

    @Scheduled(fixedDelay = 100)
    private void pollingTask() {
        if (m_tasks.size() == 0) {
            return;
        } else if (m_taskExecutor.getPoolSize() == m_taskExecutor.getMaxPoolSize()) {
            // TODO: throw Error
        }


    }
}
