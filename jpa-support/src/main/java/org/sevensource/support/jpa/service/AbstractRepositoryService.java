package org.sevensource.support.jpa.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.sevensource.support.jpa.domain.PersistentEntity;
import org.sevensource.support.jpa.exception.EntityAlreadyExistsException;
import org.sevensource.support.jpa.exception.EntityException;
import org.sevensource.support.jpa.exception.EntityNotFoundException;
import org.sevensource.support.jpa.exception.EntityValidationException;
import org.sevensource.support.jpa.hibernate.unique.UniqueValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

public abstract class AbstractRepositoryService<T extends PersistentEntity<UUID>> implements EntityService<T, UUID> {

	private static final Logger logger = LoggerFactory.getLogger(AbstractRepositoryService.class);
	
	private final JpaRepository<T, UUID> repository;
	private final Validator validator;
	private final Class<T> entityClass;
	
	
	public AbstractRepositoryService(JpaRepository<T, UUID> repository, Validator validator, Class<T> entityClass) {
		this.repository = repository;
		this.validator = validator;
		this.entityClass = entityClass;
	}
	
	@Override
	public boolean supports(Class<?> classToCheck) {
		return this.entityClass.equals(classToCheck);
	}
	
	@Override
	@Transactional(readOnly=true)
	public Page<T> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<T> findAll(Sort sort) {
		return repository.findAll(sort);
	}
	
	@Override
	@Transactional(readOnly=true)
	public T get(UUID id) {
		Assert.notNull(id, "ID must not be null");
		return repository.findOne(id);
	}
	
	@Override
	@Transactional(readOnly=true)
	public boolean exists(UUID id) {
		Assert.notNull(id, "ID must not be null");
		return repository.exists(id);
	}
	
	@Override
	@Transactional(readOnly=false)
	public T create(T entity) {
		Assert.notNull(entity, "Entity must not be null");
		//Assert.isNull(entity.getId(), "ID of entity must be null");
		//return create(null, entity);
		return create(entity.getId(), entity);
	}
	
	@Override
	@Transactional(readOnly=false)
	public T create(UUID id, T entity) {
		Assert.notNull(entity, "Entity must not be null");
		
		if(id == null) {
			entity.setId(null);
		}
		
		if(entity.getId() != null && !entity.getId().equals(id)) {
			throw new EntityValidationException("IDs must match");
		}
		
		if(id != null && exists(id)) {
			final String msg = String.format("Entity with id [%s] already exists", id); 
			throw new EntityAlreadyExistsException(msg);
		}

		entity.setId(id);
		validate(entity);
		return doCreate(entity);
	}
	
	protected T doCreate(T entity) {
		return repository.save(entity);
	}
	
	@Override
	@Transactional(readOnly=false)
	public T update(UUID id, T entity) throws EntityNotFoundException {
		Assert.notNull(id, "ID must not be null");
		Assert.notNull(entity, "Entity must not be null");
		Assert.isTrue(id.equals(entity.getId()), "IDs must match");
		
		validate(entity);
		
		if(! exists(id)) {
			final String msg = String.format("Entity with id [%s] does not exist", id);
			throw new EntityNotFoundException(msg);
		}
		
		return doUpdate(id, entity);
	}
	
	protected T doUpdate(UUID id, T entity) {
		return repository.save(entity);
	}
	
	@Override
	@Transactional(readOnly=false)
	public void delete(UUID id) throws EntityNotFoundException {
		Assert.notNull(id, "ID must not be null");
		
		if(! exists(id)) {
			final String msg = String.format("Entity with id [%s] does not exist", id);
			throw new EntityNotFoundException(msg);
		}
		
		doDelete(id);
	}
	
	protected void doDelete(UUID id) {
		repository.delete(id);
	}
	
	
	@Override
	public boolean validate(T entity) throws EntityValidationException {
		validateJsr310(entity);
		validateUniqueConstraint(entity);
		validateConstraints(entity);
		return true;
	}
	
	protected void validateJsr310(T entity) {
		Set<ConstraintViolation<T>> violations = validator.validate(entity);
		if(violations.isEmpty()) {
			return;
		} else {
			final String message = String.format("Validation of entity %s failed", entityClass.getName());
			throw new EntityValidationException(message, violations);
		}
	}
	
	protected void validateUniqueConstraint(T entity) throws EntityValidationException {
		Set<ConstraintViolation<T>> violations = validator.validate(entity, UniqueValidation.class);
		if(violations.isEmpty()) {
			return;
		} else {
			final String message = String.format("Validation of entity %s failed with a UniqueConstraint", entityClass.getName());
			throw new EntityValidationException(message, violations);
		}
	}
	
	/**
	 * validate business constraints
	 * 
	 * @param entity the entity under inspection
	 * @throws EntityException if a constraint cannot be validated
	 */
	protected abstract void validateConstraints(T entity) throws EntityException;
	
}
