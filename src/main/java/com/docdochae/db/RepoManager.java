package com.docdochae.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.docdochae.common.Error.Error;
import com.docdochae.common.Error.ErrorCode;
import com.docdochae.db.documnet.PrsInfo.PrsInfoRepository;
import com.docdochae.db.documnet.PrsInfo.PrsInfoEntity;
import com.docdochae.db.documnet.Resume.ResumeEntity;
import com.docdochae.db.documnet.Resume.ResumeRepository;
import com.docdochae.db.task.TaskEntity;
import com.docdochae.db.task.TaskRepository;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

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

    public boolean taskSuccessed(String uuid) {
        TaskEntity entity = findTaskEntityByUuid(uuid);
        return entity != null && entity.getStatus().equals("Success");
    }

    public String getReqTypeByUuid(String uuid) {
        TaskEntity entity = findTaskEntityByUuid(uuid);
        if (entity == null) {
            return "Invalid";
        }
        return entity.getReqType();
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

    public JsonObject getPrsResultByUuid(String uuid) {
        PrsInfoEntity entity = prsInfoRepository.findByUuid(uuid);
        return buildPrsInfoEntityJson(entity);
    }

    public JsonObject getResumeResultByUuid(String uuid) {
        ResumeEntity entity = resumeRepository.findByUuid(uuid);
        return buildResumeInfoEntityJson(entity);
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

    public void submitResumeEntity(String uuid, Map<String, String> data) throws Error {
        ResumeEntity entity = resumeRepository.findByUuid(uuid);
        if (entity == null) {
            throw new Error(ErrorCode.INVALID_UUID, "Invalid uuid, UUid" + uuid);
        }
        entity.setExperienced(data.getOrDefault("experienced", entity.getExperienced()));
        entity.setUnivScore(Float.parseFloat(data.getOrDefault("univScore", String.valueOf(entity.getUnivScore()))));
        entity.setName(data.getOrDefault("name", entity.getName()));
        entity.setGender(data.getOrDefault("gender", entity.getGender()));
        entity.setVolunteerArea(data.getOrDefault("volunteerArea", entity.getVolunteerArea()));
        entity.setBirth(data.getOrDefault("birth", entity.getBirth()));
        entity.setAddress(data.getOrDefault("address", entity.getAddress()));
        entity.setPhone(data.getOrDefault("phone", entity.getPhone()));
        entity.setEmail(data.getOrDefault("email", entity.getEmail()));
        resumeRepository.save(entity);
    }

    public void submitPrsInfoEntity(String uuid, Map<String, String> data) {
        PrsInfoEntity entity = prsInfoRepository.findByUuid(uuid);
        if (entity == null) {
            throw new Error(ErrorCode.INVALID_UUID, "Invalid uuid, UUid" + uuid);
        }
        entity.setStudentID(Long.parseLong(data.getOrDefault("studentID", String.valueOf(entity.getStudentID()))));
        entity.setCollege(data.getOrDefault("college", entity.getCollege()));
        entity.setDepartment(data.getOrDefault("department", entity.getDepartment()));
        entity.setKorName(data.getOrDefault("korName", entity.getKorName()));
        entity.setEngName(data.getOrDefault("engName", entity.getEngName()));
        entity.setBirth(data.getOrDefault("birth", entity.getBirth()));
        entity.setPhone(data.getOrDefault("phone", entity.getPhone()));
        entity.setBeforeRevise(data.getOrDefault("beforeRevise", entity.getBeforeRevise()));
        entity.setAfterRevise(data.getOrDefault("afterRevise", entity.getAfterRevise()));
        prsInfoRepository.save(entity);
    }

    private JsonObject buildResumeInfoEntityJson(ResumeEntity entity) {
        JsonObject json = new JsonObject();
        json.put("Experienced", entity.getExperienced());
        json.put("UnivScore", entity.getUnivScore());
        json.put("Name", entity.getName());
        json.put("Gender", entity.getGender());
        json.put("VolunteerArea", entity.getVolunteerArea());
        json.put("Birth", entity.getBirth());
        json.put("Address", entity.getAddress());
        json.put("Phone", entity.getPhone());
        json.put("Email", entity.getEmail());
        return json;
    }

    private JsonObject buildPrsInfoEntityJson(PrsInfoEntity entity) {
        JsonObject json = new JsonObject();
        json.put("StudentID", entity.getStudentID());
        json.put("Department", entity.getDepartment());
        json.put("KorName", entity.getKorName());
        json.put("EngName", entity.getEngName());
        json.put("Birth", entity.getBirth());
        json.put("Phone", entity.getPhone());
        json.put("BeforeRevise", entity.getBeforeRevise());
        json.put("AfterRevise", entity.getAfterRevise());
        return json;
    }
}
