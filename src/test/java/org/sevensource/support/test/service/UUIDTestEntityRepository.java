package org.sevensource.support.test.service;

import java.util.UUID;

import org.sevensource.support.test.model.UUIDTestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

interface UUIDTestEntityRepository extends JpaRepository<UUIDTestEntity, UUID> {
	
	@Transactional(readOnly=true)
	UUIDTestEntity findByTitle(String title);
}
