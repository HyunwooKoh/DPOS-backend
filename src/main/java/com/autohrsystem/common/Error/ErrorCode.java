package com.autohrsystem.common.Error;

public class ErrorCode {

    // Common
    public static int FILE_NOT_FOUND = -100;
    public static int FILE_REMOVE_FAILED = -101;
    public static int INVALID_UUID = -102;
    public static int UUID_DOES_NOT_EXIST = -103;
    public static int INVALID_REQ_TYPE = -104;

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

    // Rendering
    public static int RENDERING_ERROR = -400;

    // Entity
    public static int CONVERT_ERROR = -500;
    public static int INVALID_RESUME_DATA = -501;
    public static int INVALID_PRSINFO_DATA = -502;
}
