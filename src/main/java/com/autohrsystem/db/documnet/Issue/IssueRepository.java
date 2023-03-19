package com.autohrsystem.db.documnet.Issue;

import com.autohrsystem.db.documnet.Issue.IssueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<IssueEntity, String> {
}