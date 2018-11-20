package org.sevensource.support.test.jpa.service;

import java.util.UUID;

import org.sevensource.support.test.jpa.domain.UUIDTestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

interface UUIDTestEntityRepository extends JpaSpecificationExecutor<UUIDTestEntity>, JpaRepository<UUIDTestEntity, UUID> {
	
	@Transactional(readOnly=true)
	UUIDTestEntity findByTitle(String title);
}
