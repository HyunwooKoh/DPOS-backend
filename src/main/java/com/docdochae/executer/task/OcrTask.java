package com.docdochae.executer.task;

import com.docdochae.common.CommonApi;
import com.docdochae.common.Error.ErrorCode;
import com.docdochae.common.ReqType;

import com.docdochae.db.RepoManager;
import com.docdochae.db.task.*;
import com.docdochae.executer.Render.PdfRenderer;
import com.docdochae.executer.diffImg.ImgDrawer;

import com.docdochae.common.Error.Error;
import com.docdochae.ocr.OcrServiceClient;
import com.docdochae.ocr.OcrParams;
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
            } catch (Error e) {
                entity.setStatus("Failure");
                entity.setErrorCode(ErrorCode.RENDERING_ERROR);
                entity.setErrorMsg(e.msg());
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
            entity.setErrorMsg(e.msg());
        }
        repoManager.saveTaskEntity(entity);
    }

    private void exportFile(JsonObject response) throws Error {
        File resultJson = null;
        resultJson = new File(m_ocrParams.m_outputUri);
        if (resultJson.exists()) {
            throw new Error(ErrorCode.OCR_RESULT_EXIST, "Result json already exist. path : " + resultJson.getAbsolutePath());
        }
        try {
            if (resultJson.createNewFile()) {
                FileWriter file = new FileWriter(resultJson.getAbsolutePath());
                file.write(response.toString());
                file.flush();
                file.close();
            }
        } catch (IOException ioE) {
            logger.error("Error Occur during save ocr result json. path : " + resultJson.getAbsolutePath());
            throw new Error(ErrorCode.OCR_RESULT_SAVE,ioE.getMessage());
        }
    }

    private void drawDiffImage() throws Error {
        ImgDrawer drawer = new ImgDrawer(m_ocrParams.m_inputUri, CommonApi.getDirectoryOfFile(m_ocrParams.m_outputUri));
        drawer.build();
        drawer.drawCompareImg();
    }

    private Void HandleResult(JsonObject response) throws Error {
        exportFile(response);
        drawDiffImage();
        Map<String, String> res = repoManager.parse(response.toString(), m_reqType);
        switch (m_reqType) {
            case ReqType.REQ_TYPE_PrsInfo -> repoManager.buildPrsInfoEntity(m_uuid, res);
            case ReqType.REQ_TYPE_Resume -> repoManager.buildResumeEntity(m_uuid, res);
        }
        return null;
    }
}
