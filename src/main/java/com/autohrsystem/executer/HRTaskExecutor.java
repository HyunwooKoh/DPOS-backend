package com.autohrsystem.executer;

import com.autohrsystem.common.Error;
import io.vertx.core.json.JsonObject;
import com.autohrsystem.structure.OcrParams;
import com.autohrsystem.common.CommonApi;
import com.autohrsystem.file.FileHandler;
import com.autohrsystem.ocr.OcrServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class HRTaskExecutor {

    @Autowired
    private Environment env;
    // TODO : make request option as class
    private final JsonObject TYPE_1_REQ_OPT = new JsonObject("");
    private final JsonObject TYPE_2_REQ_OPT = new JsonObject("");
    private final String m_uuid;
    private final String m_type;
    private final String m_ext;

    public HRTaskExecutor(String uuid, String type, String ext) {
        m_uuid = uuid;
        m_type = type;
        m_ext = ext;
    }

    public void run() {
        String inputFilePath = CommonApi.getTempDir(m_uuid) + "origin" + m_ext;
        String outputFilePath = CommonApi.getTempDir(m_uuid) + "result.json";

        FileHandler fileHandler = new FileHandler(m_uuid, m_ext, inputFilePath, outputFilePath);
        OcrParams ocrParams = new OcrParams(inputFilePath, outputFilePath, env.getProperty("OCR_SERVER_URL"));
        if (m_type == "type1") {
            ocrParams.setReqOption(TYPE_1_REQ_OPT);
        } else if (m_type == "type2") {
            ocrParams.setReqOption(TYPE_2_REQ_OPT);
        } else {
            // TODO: throw Error - invalid type
        }

        OcrServiceClient ocrServiceClient = new OcrServiceClient(ocrParams);
        try {
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
