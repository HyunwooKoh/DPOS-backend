package com.autohrsystem.Handler.impl;

import com.autohrsystem.Handler.OCRHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.autohrsystem.structure.registRequest;

@RestController
public class OCRHandlerImpl implements OCRHandler {
    @PostMapping("/register")
    public registRequest registNewDocument(@RequestBody registRequest body) {
        return body;
    }

    @Override
    public void requestOcr(String filePath, String jobID) {
        //TODO: do request using file
    }


}