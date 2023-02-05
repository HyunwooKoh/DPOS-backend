package com.autohrsystem.db.documnet;

import jakarta.persistence.*;
import lombok.*;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name="Resume")
public class ResumeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long PId;

    @Column(nullable = false)
    private boolean experienced;

    @Column(nullable = false)
    private long univScore;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 10)
    private String sex;

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
}
