package com.autohrsystem.common;

public class ErrorCode {

    // Common
    public static int FILE_NOT_FOUND = -100;
    public static int FILE_REMOVE_FAILED = -101;

    // File Server Error
    public static int FSV_FILE_NOT_FOUND = -200;
    public static int FSV_FILE_CHECK_ERROR = -201;
    public static int FSV_FILE_ALREADY_EXIST = -202;
    public static int FSV_FILE_DOWNLOAD_FAILED = -203;
    public static int FSV_FILE_UPLOAD_FAILED = -204;

    // OCR
    public static int OCR_RESULT_EXIST = -300;
    public static int OCR_RESULT_SAVE = -302;
    public static int OCR_PULL_ERROR = -301;
    public static int OCR_PUSH_ERROR = -303;
    public static int OCR_INVALID_REQ_TYPE = -304;
}
