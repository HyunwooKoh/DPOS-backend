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

}
