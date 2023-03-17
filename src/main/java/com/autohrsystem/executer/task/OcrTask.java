package com.autohrsystem.executer.task;

import com.autohrsystem.common.ErrorCode;
import com.autohrsystem.db.RepoManager;
import com.autohrsystem.db.task.*;

import com.autohrsystem.common.Error;
import com.autohrsystem.ocr.OcrServiceClient;
import com.autohrsystem.ocr.OcrParams;
import io.vertx.core.json.JsonObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OcrTask implements Runnable {
    Logger logger = LoggerFactory.getLogger(getClass());
    private final RepoManager repoManager;
    private final OcrParams m_ocrParams;
    private final String m_uuid;
    private final String m_reqType;
    public OcrTask(OcrParams ocrParams, String uuid, String reqType, RepoManager parser) {
        m_ocrParams = ocrParams;
        m_uuid = uuid;
        m_reqType = reqType;
        repoManager = parser;
    }

    public String getUuid() {
        return this.m_uuid;
    }

    @Override
    public void run() {
        OcrServiceClient ocrServiceClient = new OcrServiceClient(m_ocrParams);
        TaskEntity entity = repoManager.findTaskEntityByUuid(m_uuid);
        try {
            // TODO: build file handler via ocrParams
            ocrServiceClient.DoTask(this::exportFile);
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
            entity.setStatus("Failure");
            entity.setErrorCode(e.code());
            entity.setErrorMsg(e.getMessage());
        }
        repoManager.saveTaskEntity(entity);
    }

    private Void exportFile(JsonObject response) {
        File resultJson = null;
        try {
            resultJson = new File(m_ocrParams.m_outputUri);
            if (resultJson.exists()) {
                throw new Error(ErrorCode.OCR_RESULT_EXIST, "Result json already exist. path : " + resultJson.getAbsolutePath());
            }

            if (!resultJson.createNewFile()) {
                throw new Error(ErrorCode.OCR_RESULT_SAVE, "Error occur during create file. path : " + resultJson.getAbsolutePath());
            }
            FileWriter file = new FileWriter(resultJson.getAbsolutePath());
            file.write(response.toString());
            file.flush();
            file.close();
        } catch (IOException | Error e) {
            logger.error("Error Occur during save ocr result json. path : " + resultJson.getAbsolutePath());
            throw new RuntimeException(e);
        }
        return null;
    }

}
