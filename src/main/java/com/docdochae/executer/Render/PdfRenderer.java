package com.docdochae.executer.Render;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.docdochae.common.CommonApi;
import com.docdochae.common.Error.Error;
import com.docdochae.common.Error.ErrorCode;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdfRenderer {
    public String m_inputFile;
    private String m_command = "";
    static Runtime runTime = Runtime.getRuntime();
    public PdfRenderer(String inputFile){
        m_inputFile = inputFile;
    }

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public void build() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            m_command += CommonApi.normalizePath(System.getenv("HOME")) + "/module/render/pdfio.exe";
        } else if (os.contains("linux")) {
            m_command += System.getenv("HOME") + "/module/render/pdfio";
        }
        m_command += " --task extract --target rendering --option zoom=100,format=jpg,jpeg-quality=h --range all --input " + m_inputFile + " --output "
                + new File(m_inputFile).getParent() + "/src";
        logger.info("rendering command : " + m_command);
    }

    @SneakyThrows
    public void render() throws Error {
        try {
            Process renderer =  runTime.exec(m_command);
            renderer.waitFor();
        } catch (IOException e) {
            logger.error("[ModuleError] Rendering Error, " + Arrays.toString(e.getStackTrace()));
            throw new Error(ErrorCode.RENDERING_ERROR, "Error occur during rendering pdf");
        }
    }

    public boolean createCompareImg() {
        return true;
    }
}
