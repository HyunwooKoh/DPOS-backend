package com.autohrsystem.executer.Render;
import com.autohrsystem.common.CommonApi;

import java.io.File;
import java.io.IOException;

public class PdfRenderer {
    public String m_inputFile;
    private String m_command = "";
    static Runtime runTime = Runtime.getRuntime();
    public PdfRenderer(String inputFile){
        m_inputFile = inputFile;
    }

    public void build() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            m_command += CommonApi.normalizePath(System.getenv("HOME")) + "/module/pdfio.exe";
        } else if (os.contains("linux")) {
            m_command += System.getenv("HOME") + "/module/pdfio";
        }
        m_command += " --task extract --target rendering --option zoom=100,format=jpg,jpeg-quality=h --range all --input " + m_inputFile + " --output "
                + new File(m_inputFile).getParent() + "/src";
    }

    public void render() throws Exception {
        try {
            Process renderer =  runTime.exec(m_command);
            renderer.waitFor();
        } catch (IOException e) {
            throw new IOException(e);
        }
    }
}
