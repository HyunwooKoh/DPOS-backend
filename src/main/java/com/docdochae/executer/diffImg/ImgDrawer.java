package com.docdochae.executer.diffImg;

import java.io.IOException;
import java.util.Arrays;

import com.docdochae.common.CommonApi;
import com.docdochae.common.Error.Error;
import com.docdochae.common.Error.ErrorCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.SneakyThrows;

public class ImgDrawer {
    private final String m_workingDir;
    private final String m_inputImg;
    private String m_command = "";
    static Runtime runTime = Runtime.getRuntime();
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public ImgDrawer(String inputImg, String dir){
        m_inputImg = inputImg;
        m_workingDir = dir;
    }

    public void build() {
        m_command += "python3 " + CommonApi.normalizePath(System.getenv("HOME")) + "/module/diffImg/draw_diff.py ";
        m_command +=  m_inputImg;
        m_command += " " + m_workingDir + "/result.json";
        m_command += " " + m_workingDir + "/diff.png";
        logger.info("Drawing diff image command : " + m_command);
    }

    @SneakyThrows
    public void drawCompareImg() throws Error {
        try {
            Process drawer =  runTime.exec(m_command);
            drawer.waitFor();
        } catch (IOException e) {
            logger.error("[ModuleError] Drawing diff Error, " + Arrays.toString(e.getStackTrace()));
            throw new Error(ErrorCode.Diff_Drawing_ERROR, "Error occur during drawing diff image");
        }
    }
}
