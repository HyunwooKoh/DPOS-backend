package com.autohrsystem.db.task;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<TaskEntity, String> {
    TaskEntity findByUuid(String uuid);
    TaskEntity getTypeByUuid(String uuid);
}
