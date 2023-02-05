package com.autohrsystem.executer.task;

import com.autohrsystem.db.task.TaskEntity;
import com.autohrsystem.db.task.TaskRepository;
import com.autohrsystem.file.FileHandler;
import com.autohrsystem.common.Error;
import com.autohrsystem.ocr.OcrServiceClient;
import com.autohrsystem.ocr.OcrParams;
import org.springframework.beans.factory.annotation.Autowired;

public class OcrTask implements Runnable {
    @Autowired
    TaskRepository taskRepository;
    private final OcrParams m_ocrParams;
    private final FileHandler m_fileHandler;
    private final String m_uuid;
    public OcrTask(OcrParams ocrParams, FileHandler handler, String uuid) {
        m_ocrParams = ocrParams;
        m_fileHandler = handler;
        m_uuid = uuid;
    }

    public String getUuid() {
        return this.m_uuid;
    }

    @Override
    public void run() {
        OcrServiceClient ocrServiceClient = new OcrServiceClient(m_ocrParams);
        TaskEntity entity = taskRepository.findByUuid(m_uuid);
        try {
            // TODO: build file handler via ocrParams
            ocrServiceClient.DoTask();
            m_fileHandler.uploadResult();
            // TODO : parse result
            // map<String, String> targetDatas
            entity.setStatus("Success");
        } catch (Error e) {
            entity.setStatus("Failure");
            entity.setErrorCode(e.code());
            entity.setErrorMsg(e.getMessage());
        }
        taskRepository.save(entity);
    }

}
