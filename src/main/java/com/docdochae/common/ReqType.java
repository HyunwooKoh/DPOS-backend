package com.docdochae.common;

public class ReqType {
    public static final String REQ_TYPE_PrsInfo = "PrsInfo";
    public static final String REQ_TYPE_Resume = "Resume";
    public static final String REQ_TYPE_3 = "Type3";

    public static boolean isValidReqType(String req) {
        return switch (req) {
            case REQ_TYPE_PrsInfo, REQ_TYPE_Resume, REQ_TYPE_3 -> true;
            default -> false;
        };
    }
}
