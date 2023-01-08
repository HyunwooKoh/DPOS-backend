package com.autohrsystem.structure;

import io.vertx.core.json.JsonObject;

public class OcrParams {
    public String m_inputUri;
    public String m_outputUri;
    public String m_serverUri;
    public JsonObject m_reqOption;

    public OcrParams(String inputUri, String outputUri, String serverUri) {
        m_inputUri = inputUri;
        m_outputUri = outputUri;
        m_serverUri = serverUri;
    }

    public void setReqOption(JsonObject opt) {m_reqOption = opt;}
}
