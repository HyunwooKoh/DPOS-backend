package com.autohrsystem.executer.diffImg;

import com.autohrsystem.common.CommonApi;
import com.autohrsystem.common.Error.Error;
import com.autohrsystem.common.Error.ErrorCode;
import lombok.SneakyThrows;

import java.io.IOException;

public class ImgDrawer {
    private final String m_workingDir;
    private final String m_inputImg;
    private String m_command = "";
    static Runtime runTime = Runtime.getRuntime();
    public ImgDrawer(String inputImg, String dir){
        m_inputImg = inputImg;
        m_workingDir = dir;
    }

    public void build() {
        m_command += CommonApi.normalizePath(System.getenv("HOME")) + "/module/diffImg/draw_diff.py ";
        m_command += m_workingDir + "/" + m_inputImg;
        m_command += " " + m_workingDir + "/res.json";
        m_command += " " + m_workingDir + "/diff.png";

    }

    @SneakyThrows
    public void drawCompareImg() throws Error {
        try {
            Process renderer =  runTime.exec(m_command);
            renderer.waitFor();
        } catch (IOException e) {
            throw new Error(ErrorCode.Diff_Drawing_ERROR, "Error occur during drawing diff image");
        }
    }
}
