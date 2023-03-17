package com.autohrsystem.db.documnet.Issue;


import jakarta.persistence.*;
import lombok.*;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name="Issue")
public class IssueEntity {
    @Id
    private String uuid;
    @Column(nullable = false)
    private String allText;
    private String errorCode;
    @Column
    private String errorMsg;
}