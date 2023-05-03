package com.autohrsystem.db;

import com.autohrsystem.common.Error.Error;
import com.autohrsystem.common.Error.ErrorCode;
import com.autohrsystem.db.documnet.PrsInfo.PrsInfoRepository;
import com.autohrsystem.db.documnet.PrsInfo.PrsInfoEntity;
import com.autohrsystem.db.documnet.Resume.ResumeEntity;
import com.autohrsystem.db.documnet.Resume.ResumeRepository;
import com.autohrsystem.db.task.TaskEntity;
import com.autohrsystem.db.task.TaskRepository;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RepoManager {
    @Autowired
    private final TaskRepository taskRepository;
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

    public List<PrsInfoEntity> getAllPrsInfoEntity() {
        return prsInfoRepository.findAll();
    }

    public List<ResumeEntity> getAllResumeEntity() {
        return resumeRepository.findAll();
    }

    public Map<String, String> parse(String data, String reqType) {
        Map<String, String> res = new HashMap<String, String>();
        JsonObject obj = new JsonObject(data);
        JsonObject fields =  obj.getJsonObject("document").getJsonArray("pages").getJsonObject(0).getJsonObject("fields");
        fields.forEach(item -> {
            String val = "";
            JsonArray array = new JsonArray(item.getValue().toString());
            for (int i = 0 ; i < array.size(); i++) {
                val += array.getJsonObject(i).getJsonObject("recognition").getString("content") + " ";
            }
            if (!val.equals("")) {
                val = val.substring(0, val.length() - 1);
            }
            res.put(item.getKey(), val);
        });
        return res;
    }

    public void buildPrsInfoEntity(String uuid, Map<String, String> data) throws Error {
        try {
            PrsInfoEntity entity = new PrsInfoEntity(uuid,
                    Long.parseLong(data.get("studentID")),
                    data.get("college"),
                    data.get("department"),
                    data.get("korName"),
                    data.get("engName"),
                    data.get("birth"),
                    data.get("phone"),
                    data.get("beforeRevise"),
                    data.get("afterRevise"));
            prsInfoRepository.save(entity);
        } catch (NumberFormatException e) {
            throw new Error(ErrorCode.CONVERT_ERROR, "Occur Error during converting value to insert to DB");
        }
    }

    public void buildResumeEntity (String uuid, Map<String, String> data) throws Error {
        try {
            ResumeEntity entity = new ResumeEntity(uuid,
                    data.get("experienced"),
                    Float.parseFloat(data.get("univScore")),
                    data.get("name"),
                    data.get("gender"),
                    data.get("volunteerArea"),
                    data.get("birth"),
                    data.get("address"),
                    data.get("phone"),
                    data.get("email"));
            resumeRepository.save(entity);
        } catch (NumberFormatException e) {
            throw new Error(ErrorCode.CONVERT_ERROR, "Occur Error during converting value to insert to DB");
        }
    }

    public void submitResumeEntity(JsonObject data) throws Error {
        ResumeEntity entity = resumeRepository.findByUuid(data.getString("uuid"));
        if (entity == null) {
            throw new Error(ErrorCode.INVALID_UUID, "");
        }
        if (!validateResumeData(data)) {
            throw new Error(ErrorCode.INVALID_RESUME_DATA, "Invalid resume data");
        }
        entity.setExperienced(data.getString("experienced"));
        entity.setUnivScore(data.getFloat("univScore"));
        entity.setName(data.getString("name"));
        entity.setGender(data.getString("gender"));
        entity.setVolunteerArea(data.getString("volunteerArea"));
        entity.setBirth(data.getString("birth"));
        entity.setAddress(data.getString("address"));
        entity.setPhone(data.getString("phone"));
        entity.setEmail(data.getString("email"));
        resumeRepository.save(entity);
    }

    private boolean validateResumeData(JsonObject data) {
        if(!data.containsKey("experienced")) {
            return false;
        } else if (!data.containsKey("univScore")) {
            return false;
        } else if (!data.containsKey("name")) {
            return false;
        } else if (!data.containsKey("gender")) {
            return false;
        } else if (!data.containsKey("volunteerArea")) {
            return false;
        } else if (!data.containsKey("birth")) {
            return false;
        } else if (!data.containsKey("address")) {
            return false;
        } else if (!data.containsKey("phone")) {
            return false;
        } else if (!data.containsKey("email")) {
            return false;
        }
        return true;
    }
}
