package com.autohrsystem.executer.task;

import com.autohrsystem.file.FileHandler;
import com.autohrsystem.common.Error;
import com.autohrsystem.ocr.OcrServiceClient;
import com.autohrsystem.ocr.OcrParams;

public class OcrTask implements Runnable {
    OcrParams m_ocrParams;
    FileHandler m_fileHandler;
    public OcrTask(OcrParams ocrParams, FileHandler handler) {
        m_ocrParams = ocrParams;
        m_fileHandler = handler;
    }

    @Override
    public void run() {
        OcrServiceClient ocrServiceClient = new OcrServiceClient(m_ocrParams);
        try {
            // TODO: build file handler via ocrParams
            ocrServiceClient.DoTask();
            m_fileHandler.uploadResult();
            // TODO : parse result
            // map<String, String> targetDatas
            // TODO : db insert
        } catch (Error e) {

        }
    }

}
