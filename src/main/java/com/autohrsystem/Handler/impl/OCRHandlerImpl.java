package com.autohrsystem.Handler.impl;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.autohrsystem.structure.registRequest;

@RestController
public class OCRHandlerImpl {
    @PostMapping("/register")
    public registRequest test(@RequestBody registRequest body) {
        return body;
    }

    private boolean requestOcr(String localFilePath) {
        //TODO: do request for detect data from image or pdf
        return true;
    }


}