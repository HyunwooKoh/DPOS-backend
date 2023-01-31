package com.autohrsystem.common;

import com.autohrsystem.common.ErrorCode;

public class Error extends Throwable {
    private final int m_errorCode;
    private final String m_msg;
    public Error(int errorCode, String msg) {
        m_errorCode = errorCode;
        m_msg = msg;
    }

    public int code() {
        return m_errorCode;
    }

    public String msg() {
        return m_msg;
    }
}
