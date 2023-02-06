package com.autohrsystem.db.documnet;


import com.autohrsystem.db.documnet.ResumeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<ResumeEntity, String> {
}
