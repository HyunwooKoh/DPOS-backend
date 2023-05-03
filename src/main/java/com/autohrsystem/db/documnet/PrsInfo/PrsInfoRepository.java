package com.autohrsystem.db.documnet.PrsInfo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PrsInfoRepository extends JpaRepository<PrsInfoEntity, String> {
    PrsInfoEntity findByUuid(String uuid);
}