package com.autohrsystem.db;

import com.autohrsystem.db.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<TaskEntity, String> {
    TaskEntity findByUuid(String uuid);
}
