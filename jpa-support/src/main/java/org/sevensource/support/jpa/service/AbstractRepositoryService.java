package org.sevensource.support.jpa.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.sevensource.support.jpa.domain.PersistentEntity;
import org.sevensource.support.jpa.exception.EntityAlreadyExistsException;
import org.sevensource.support.jpa.exception.EntityException;
import org.sevensource.support.jpa.exception.EntityNotFoundException;
import org.sevensource.support.jpa.exception.EntityValidationException;
import org.sevensource.support.jpa.filter.FilterCriteria;
import org.sevensource.support.jpa.filter.predicate.FilterCriteriaPredicateBuilder;
import org.sevensource.support.jpa.hibernate.unique.UniqueValidation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

public abstract class AbstractRepositoryService<T extends PersistentEntity<UUID>, R extends JpaRepository<T, UUID> & JpaSpecificationExecutor<T>> implements EntityService<T, UUID> {

	private static final String ENTITY_MUST_NOT_BE_NULL = "Entity must not be null";
	private static final String ENTITY_WITH_ID_S_DOES_NOT_EXIST = "Entity with id [%s] does not exist";
	private static final String ID_MUST_NOT_BE_NULL = "ID must not be null";

	private final R repository;
	private final Validator validator;
	private final Class<T> entityClass;


	protected AbstractRepositoryService(R repository, Validator validator, Class<T> entityClass) {
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
	public Page<T> findAll(Pageable pageable, FilterCriteria filterCriteria) {
		if(filterCriteria == null) {
			return repository.findAll(pageable);	
		} else {
			final FilterCriteriaPredicateBuilder<T> spec = new FilterCriteriaPredicateBuilder<>(filterCriteria);
			return repository.findAll(spec, pageable);
		}
	}

	@Override
	@Transactional(readOnly=true)
	public List<T> findAll(Sort sort, FilterCriteria filterCriteria) {
		if(filterCriteria == null) {
			return repository.findAll(sort);	
		} else {
			final FilterCriteriaPredicateBuilder<T> spec = new FilterCriteriaPredicateBuilder<>(filterCriteria);
			return repository.findAll(spec, sort);
		}
	}

	@Override
	@Transactional(readOnly=true)
	public T get(UUID id) {
		Assert.notNull(id, ID_MUST_NOT_BE_NULL);
		final Optional<T> e = repository.findById(id);
		return e.orElse(null);
	}

	@Override
	@Transactional(readOnly=true)
	public boolean exists(UUID id) {
		Assert.notNull(id, ID_MUST_NOT_BE_NULL);
		return repository.existsById(id);
	}

	@Override
	@Transactional(readOnly=false)
	public T create(T entity) {
		Assert.notNull(entity, ENTITY_MUST_NOT_BE_NULL);
		return create(entity.getId(), entity);
	}

	@Override
	@Transactional(readOnly=false)
	public T create(UUID id, T entity) {
		Assert.notNull(entity, ENTITY_MUST_NOT_BE_NULL);

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
		Assert.notNull(id, ID_MUST_NOT_BE_NULL);
		Assert.notNull(entity, ENTITY_MUST_NOT_BE_NULL);
		Assert.isTrue(id.equals(entity.getId()), "IDs must match");

		validate(entity);

		if(! exists(id)) {
			final String msg = String.format(ENTITY_WITH_ID_S_DOES_NOT_EXIST, id);
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
		Assert.notNull(id, ID_MUST_NOT_BE_NULL);

		if(! exists(id)) {
			final String msg = String.format(ENTITY_WITH_ID_S_DOES_NOT_EXIST, id);
			throw new EntityNotFoundException(msg);
		}

		doDelete(id);
	}

	protected void doDelete(UUID id) {
		repository.deleteById(id);
	}


	@Override
	public boolean validate(T entity) throws EntityValidationException {
		validateJsr310(entity);
		validateUniqueConstraint(entity);
		validateConstraints(entity);
		return true;
	}

	protected void validateJsr310(T entity) {
		final Set<ConstraintViolation<T>> violations = validator.validate(entity);
		if(! violations.isEmpty()) {
			final String message = String.format("Validation of entity %s failed", entityClass.getName());
			throw new EntityValidationException(message, violations);
		}
	}

	protected void validateUniqueConstraint(T entity) throws EntityValidationException {
		final Set<ConstraintViolation<T>> violations = validator.validate(entity, UniqueValidation.class);
		if(! violations.isEmpty()) {
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
