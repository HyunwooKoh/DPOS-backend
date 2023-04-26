package com.autohrsystem.controller;

import com.autohrsystem.db.RepoManager;
import com.autohrsystem.db.documnet.PrsInfo.PrsInfoEntity;
import com.autohrsystem.db.documnet.Resume.ResumeEntity;

import io.vertx.core.json.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/data")
public class DataController {
    @Autowired
    RepoManager repoManager;

    @GetMapping("/prsInfo/all")
    public String getAllIssueRecords() {
        List<PrsInfoEntity> entityList = repoManager.getAllPrsInfoEntity();
        JsonArray array = new JsonArray(entityList);
        return array.toString();
    }

    @GetMapping("/resume/all")
    public String getAllResumeRecords() {
        List<ResumeEntity> entityList = repoManager.getAllResumeEntity();
        JsonArray array = new JsonArray(entityList);
        return array.toString();
    }
}
