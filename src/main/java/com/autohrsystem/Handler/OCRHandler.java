package com.autohrsystem.Handler;

import java.util.Map;

public interface OCRHandler {
    public void requestOcr(String filePath, String jobID);
    public boolean checkStatus(String taskID);
    public void getResultFile(String resFilePath);
    public Map<String,String> getTargetData(String resFilePath);
    public void insertToDB(Map<String,String> data);
    public String generateKey();
}