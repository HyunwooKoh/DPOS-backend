package com.autohrsystem.executer.task;

import com.autohrsystem.common.CommonApi;
import com.autohrsystem.common.Error;
import com.autohrsystem.file.FileHandler;
import com.autohrsystem.ocr.OcrServiceClient;
import com.autohrsystem.structure.OcrParams;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class OcrTask implements Runnable {
    OcrParams m_ocrParams;
    public OcrTask(OcrParams ocrParams) {
        m_ocrParams = ocrParams;
    }

    @Override
    public void run() {
        OcrServiceClient ocrServiceClient = new OcrServiceClient(m_ocrParams);
        try {
            // TODO: build file handler via ocrParams
            fileHandler.getFile();
            ocrServiceClient.DoTask();
            fileHandler.uploadResult();
            // TODO : parse result
            // map<String, String> targetDatas
            // TODO : db insert
        } catch (Error e) {

        }
    }

}
