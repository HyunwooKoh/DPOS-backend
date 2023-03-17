package com.autohrsystem.db;

import com.autohrsystem.common.CommonApi;
import com.autohrsystem.db.documnet.Issue.IssueRepository;
import com.autohrsystem.db.documnet.Issue.IssueEntity;
import com.autohrsystem.db.documnet.PrsInfo.PrsInfoRepository;
import com.autohrsystem.db.documnet.Resume.ResumeRepository;
import com.autohrsystem.db.task.TaskEntity;
import com.autohrsystem.db.task.TaskRepository;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RepoManager {
    @Autowired
    private final TaskRepository taskRepository;
    @Autowired
    private final IssueRepository issueRepository;
    @Autowired
    private final PrsInfoRepository prsInfoRepository;
    @Autowired
    private final ResumeRepository resumeRepository;

    public TaskEntity findTaskEntityByUuid(String uuid) {
        return taskRepository.findByUuid(uuid);
    }

    public TaskEntity saveTaskEntity(TaskEntity entity) {
        return taskRepository.save(entity);
    }

    public Map<String, String> parse(String data, String reqType) {
        if (reqType.equals("Type1")) {

        } else if (reqType.equals("Type2")) {

        } else if (reqType.equals("Type3")) {
            return parseIssueData(data);
        }
        return new HashMap<String,String>();
    }

    public Map<String, String> parseResumeData(String data) {
        Map<String, String> res = new HashMap<String, String>();
        return res;
    }

    public Map<String, String> parsePrsInfoData(String data) {
        Map<String, String> res = new HashMap<String, String>();
        return res;
    }

    public Map<String, String> parseIssueData(String data) {
        Map<String, String> res = new HashMap<String, String>();
        JsonObject obj = new JsonObject(data);
        if (obj.containsKey("document")) {
            String uuid, content, errorCode = null, errorMsg = null;
            uuid = CommonApi.generateUuid();
            content = obj.getJsonObject("document").getJsonArray("pages").getJsonObject(0).getString("content");
            JsonArray fields =  obj.getJsonObject("document").getJsonArray("pages").getJsonObject(0).getJsonObject("fields").getJsonArray("ErrorCode");
            for(int i = 0 ; i < fields.size(); i++) {
                String fieldContent = fields.getJsonObject(i).getString("content");
                if (fieldContent.contains("BSD")) {
                    int startIdx = fieldContent.indexOf("BSD");
                    errorCode = fieldContent.substring(startIdx, startIdx + 9);
                    if (startIdx + 10 < fieldContent.length()) {
                        errorMsg = fieldContent.substring(startIdx + 10);
                    }
                    break;
                }
            }
            res.put("uuid", uuid);
            res.put("content", content);
            res.put("ErrorCode", errorCode);
            res.put("ErrorMessage", errorMsg);
            IssueEntity entity = new IssueEntity(uuid, errorCode, errorMsg);
            issueRepository.save(entity);
        }
        return res;
    }
}
