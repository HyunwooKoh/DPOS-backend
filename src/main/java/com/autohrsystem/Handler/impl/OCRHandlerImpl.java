package com.autohrsystem.Handler.impl;

import com.autohrsystem.Handler.OCRHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.autohrsystem.structure.registRequest;

import java.util.HashMap;
import java.util.Map;

@RestController
public class OCRHandlerImpl implements OCRHandler {
    @PostMapping("/register")
    public String registNewDocument(@RequestBody registRequest body) {
        // TODO: requestOCR - waitPolling - parseResult - insertToDB
        // return : new data's key
        return "";
    }

    @Override
    public void requestOcr(String filePath, String jobID) {
        // TODO: do request using file
    }

    @Override
    public boolean checkStatus(String taskID) {
        // TODO: check how does the task working
        // return: if finished ? true : false;
        return false;
    }

    @Override
    public void getResultFile(String resFilePath) {
        // TODO: getResult json file from OCR server
    }

    @Override
    public Map<String,String> getTargetData(String resFilePath) {
        // TODO: parseData and get target data
        Map<String, String> datas = new HashMap<String, String>();
        datas.put("","");
        return datas;
    }

    @Override
    public void insertToDB(Map<String,String> data) {
        // TODO: insert data to DB
    }

    @Override
    public String generateKey() {
        // TODO : generate new key for new data
        return "";
    }

}