package com.autohrsystem.db.documnet.PrsInfo;


import jakarta.persistence.*;
import lombok.*;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name="PrsInfo")
public class PrsInfoEntity {
    @Id
    private String uuid;

    @Column(nullable = false)
    private long studentID;

    @Column(nullable = false, length = 20)
    private String college;

    @Column(nullable = false, length = 20)
    private String department;

    @Column(nullable = false, length = 20)
    private String korName;

    @Column(nullable = false, length = 20)
    private String engName;

    @Column(nullable = false, length = 20)
    private String birth;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 20)
    private String beforeRevise;

    @Column(nullable = false, length = 20)
    private String afterRevise;

    public void setStudentID(long studentID) {
        this.studentID = studentID;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setKorName(String korName) {
        this.korName = korName;
    }

    public void setEngName(String engName) {
        this.engName = engName;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setBeforeRevise(String beforeRevise) {
        this.beforeRevise = beforeRevise;
    }

    public void setAfterRevise(String afterRevise) {
        this.afterRevise = afterRevise;
    }
}
