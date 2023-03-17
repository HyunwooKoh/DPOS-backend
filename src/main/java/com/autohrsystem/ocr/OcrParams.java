package com.autohrsystem.ocr;

import io.vertx.core.json.JsonObject;

public class OcrParams {
    public String m_inputUri;
    public String m_outputUri;
    public String m_serverUrl;
    public JsonObject m_reqOption;

    private static final String REQ_TYPE_1 = "Type1";
    private static final String REQ_TYPE_2 = "Type2";
    private static final String REQ_TYPE_3 = "Type3";

    public OcrParams(String inputUri, String outputUri, String serverUrl) {
        m_inputUri = inputUri;
        m_outputUri = outputUri;
        m_serverUrl = serverUrl;
    }

    public boolean isValidReqType(String reqType) {
        return switch (reqType) {
            case REQ_TYPE_1, REQ_TYPE_2, REQ_TYPE_3 -> true;
            default -> false;
        };
    }

    public void setReqOption(String reqType) {
        // TODO: set region searchAPI by label me
        if (reqType.equals(REQ_TYPE_1)) {
            m_reqOption = new JsonObject();
        } else if (reqType.equals(REQ_TYPE_2)) {
            m_reqOption = new JsonObject();
        } else if (reqType.equals(REQ_TYPE_3)) {
            String jsonString = """
                    "SearchAPI" : [
                    {
                         "Type" : "FindLine",
                         "Name" : "ErrorCode",
                         "Key"  : ["BEP"]
                    }]
                    """;
            m_reqOption = new JsonObject(jsonString);
        }
    }
}
