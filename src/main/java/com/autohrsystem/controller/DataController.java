package com.autohrsystem.controller;

import com.autohrsystem.db.RepoManager;
import com.autohrsystem.db.documnet.Issue.IssueEntity;
import com.autohrsystem.db.task.TaskRepository;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
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

    @GetMapping("/issue/all")
    public String getAllIssueRecords() {
        List<IssueEntity> entityList = repoManager.getAllIssueEntity();
        JsonArray array = new JsonArray(entityList);
        return array.toString();
    }


}
