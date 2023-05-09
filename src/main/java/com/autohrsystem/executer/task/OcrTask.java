package com.autohrsystem.executer.task;

import com.autohrsystem.common.CommonApi;
import com.autohrsystem.common.Error.ErrorCode;
import com.autohrsystem.common.ReqType;

import com.autohrsystem.db.RepoManager;
import com.autohrsystem.db.task.*;
import com.autohrsystem.executer.Render.PdfRenderer;

import com.autohrsystem.common.Error.Error;
import com.autohrsystem.ocr.OcrServiceClient;
import com.autohrsystem.ocr.OcrParams;
import io.vertx.core.json.JsonObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

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
        TaskEntity entity = repoManager.findTaskEntityByUuid(m_uuid);
        if (this.m_ocrParams.m_ext.equalsIgnoreCase(".pdf")) {
            PdfRenderer renderer = new PdfRenderer(m_ocrParams.m_inputUri);
            renderer.build();
            try {
                renderer.render();
                File image = new File(CommonApi.getDirectoryOfFile(m_ocrParams.m_inputUri) + "/src/rendering." + m_ocrParams.targetPage(m_reqType) +".100.h.jpg");
                if (!image.exists()) {
                    entity.setStatus("Failure");
                    entity.setErrorCode(ErrorCode.RENDERING_ERROR);
                    entity.setErrorMsg("Cannot find rendered image file");
                    repoManager.saveTaskEntity(entity);
                    return;
                } else {
                    m_ocrParams.m_inputUri = CommonApi.getDirectoryOfFile(m_ocrParams.m_inputUri) + "/src/rendering." + m_ocrParams.targetPage(m_reqType) +".100.h.jpg";
                }
            } catch (Exception e) {
                entity.setStatus("Failure");
                entity.setErrorCode(ErrorCode.RENDERING_ERROR);
                entity.setErrorMsg(e.getMessage());
                repoManager.saveTaskEntity(entity);
                return;
            }
        }

        OcrServiceClient ocrServiceClient = new OcrServiceClient(m_ocrParams);
        try {
            ocrServiceClient.DoTask(this::HandleResult);

            // TODO: build file handler via ocrParams
            // TODO : m_fileHandler.uploadResult();
            entity.setStatus("Success");
        } catch (Error e) {
            entity.setStatus("Failure");
            entity.setErrorCode(e.code());
            entity.setErrorMsg(e.getMessage());
        }
        repoManager.saveTaskEntity(entity);
    }

    private void exportFile(JsonObject response) {
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
            assert resultJson != null;
            logger.error("Error Occur during save ocr result json. path : " + resultJson.getAbsolutePath());
            throw new RuntimeException(e);
        }
    }

    private Void HandleResult(JsonObject response) {
        exportFile(response);
        Map<String, String> res = repoManager.parse(response.toString(), m_reqType);
        switch (m_reqType) {
            case ReqType.REQ_TYPE_PrsInfo -> repoManager.buildPrsInfoEntity(m_uuid, res);
            case ReqType.REQ_TYPE_Resume -> repoManager.buildResumeEntity(m_uuid, res);
        }
        return null;
    }
}
