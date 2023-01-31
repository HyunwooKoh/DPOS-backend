package com.autohrsystem.executer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ThreadPoolExecutor;

import com.autohrsystem.common.CommonApi;
import com.autohrsystem.common.Error;
import com.autohrsystem.executer.task.OcrTask;
import com.autohrsystem.file.FileHandler;
import com.autohrsystem.structure.OcrParams;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class HRTaskExecutorService {
    @Autowired
    private Environment env;
    // TODO : make request option as class

    @Autowired
    @Qualifier("OCRTaskExecutor")
    ThreadPoolTaskExecutor m_taskExecutor;
    private final JsonObject TYPE_1_REQ_OPT = new JsonObject("");
    private final JsonObject TYPE_2_REQ_OPT = new JsonObject("");

    Queue<OcrTask> m_tasks;

    public HRTaskExecutorService() {
        m_tasks = new LinkedList<>();
    }

    public void addTask(String uuid, String reqType, String ext) {
        String inputFilePath = CommonApi.getTempDir(uuid) + "origin." + ext;
        String outputFilePath = CommonApi.getTempDir(uuid) + "result.json";
        FileHandler fileHandler = new FileHandler(uuid, ext, inputFilePath, outputFilePath);
        OcrTask task = new OcrTask(buildOcrParam(uuid, reqType, ext), fileHandler);

        try {
            fileHandler.getFile();
        } catch (Error e) {
            // TODO : insert task in db with failure status - input file error
        }
        // TODO : insert task in db with waiting status
        m_tasks.add(task);
    }

    private OcrParams buildOcrParam(String inputFilePath, String outputFilePath, String reqType) {
        OcrParams ocrParam = new OcrParams(inputFilePath, outputFilePath, env.getProperty("OCR_SERVER_URL"));
        if (reqType == "type1") {
            ocrParam.setReqOption(TYPE_1_REQ_OPT);
        } else if (reqType == "type2") {
            ocrParam.setReqOption(TYPE_2_REQ_OPT);
        } else {
            // TODO : insert task in db with failure status - invalid type
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
