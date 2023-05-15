package com.docdochae.db.documnet.Resume;


import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<ResumeEntity, String> {
    ResumeEntity findByUuid(String uuid);
}
