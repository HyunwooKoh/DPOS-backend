package com.autohrsystem.executer.task;

import com.autohrsystem.db.documnet.ResumeEntity;
import com.autohrsystem.db.task.TaskEntity;
import com.autohrsystem.db.task.TaskRepository;
import com.autohrsystem.file.FileHandler;
import com.autohrsystem.common.Error;
import com.autohrsystem.ocr.OcrServiceClient;
import com.autohrsystem.ocr.OcrParams;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

public class OcrTask implements Runnable {
    private final TaskRepository m_taskRepository;
    private final OcrParams m_ocrParams;
    private final String m_uuid;
    private final String m_reqType;
    public OcrTask(OcrParams ocrParams, String uuid, String reqType, TaskRepository taskRepository) {
        m_ocrParams = ocrParams;
        m_uuid = uuid;
        m_reqType = reqType;
        m_taskRepository = taskRepository;
    }

    public String getUuid() {
        return this.m_uuid;
    }

    @Override
    public void run() {
        OcrServiceClient ocrServiceClient = new OcrServiceClient(m_ocrParams);
        TaskEntity entity = m_taskRepository.findByUuid(m_uuid);
        try {
            // TODO: build file handler via ocrParams
            ocrServiceClient.DoTask();
            // TODO : m_fileHandler.uploadResult();
            // TODO : parse result
            if (m_reqType.equals("Type1")) {
                // map<String, String> targetDatas
                // ResumeEntity result = new ResumeEntity("")
            } else if (m_reqType.equals("Type2")) {

            } else if (m_reqType.equals("Type3")) {

            }
            entity.setStatus("Success");
        } catch (Error e) {
            // TODO: entity null check
            entity.setStatus("Failure");
            entity.setErrorCode(e.code());
            entity.setErrorMsg(e.getMessage());
        }
        m_taskRepository.save(entity);
    }

}
