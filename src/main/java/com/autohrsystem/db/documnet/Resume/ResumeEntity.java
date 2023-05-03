package com.autohrsystem.db.documnet.Resume;

import jakarta.persistence.*;
import lombok.*;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name="Resume")
public class ResumeEntity {
    @Id
    private String uuid;

    @Column(nullable = false)
    private String experienced;

    @Column(nullable = false)
    private float univScore;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 10)
    private String gender;

    @Column(nullable = false, length = 20)
    private String volunteerArea;

    @Column(nullable = false, length = 20)
    private String birth;

    @Column(nullable = false, length = 100)
    private String address;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 100)
    private String email;

    public void setExperienced(String experienced) {
        this.experienced = experienced;
    }

    public void setUnivScore(float univScore) {
        this.univScore = univScore;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setVolunteerArea(String volunteerArea) {
        this.volunteerArea = experienced;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
