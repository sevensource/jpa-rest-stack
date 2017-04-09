package org.sevensource.support.test.jpa.service;

import javax.validation.Validator;

import org.sevensource.support.jpa.exception.EntityException;
import org.sevensource.support.jpa.exception.EntityValidationException;
import org.sevensource.support.jpa.service.AbstractRepositoryService;
import org.sevensource.support.test.jpa.domain.UUIDTestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UUIDTestEntityService extends AbstractRepositoryService<UUIDTestEntity> {

	private final UUIDTestEntityRepository repository;
	
	@Autowired
	public UUIDTestEntityService(UUIDTestEntityRepository repository, Validator validator) {
		super(repository, validator, UUIDTestEntity.class);
		this.repository = repository;
	}
	
	@Override
	protected void validateConstraints(UUIDTestEntity entity) throws EntityException {
		if(entity.getRef() == null) {
			throw new EntityValidationException("Ref is null");
		}
		UUIDTestEntity existing = repository.findByTitle(entity.getTitle());
		if(existing != null && (entity.getId() == null || ! entity.getId().equals(existing.getId()))) {
			throw new EntityValidationException("Entity with same title exists");
		}
	}
}
