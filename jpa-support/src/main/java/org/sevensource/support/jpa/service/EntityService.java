package org.sevensource.support.jpa.service;

import java.io.Serializable;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.sevensource.support.jpa.domain.PersistentEntity;
import org.sevensource.support.jpa.exception.EntityAlreadyExistsException;
import org.sevensource.support.jpa.exception.EntityNotFoundException;
import org.sevensource.support.jpa.exception.EntityValidationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface EntityService<T extends PersistentEntity<ID>, ID extends Serializable> {
	
	/**
	 * returns true if the service supports operations on the entityClass
	 * @param classToCheck classToCheck
	 * @return true if the service supports this EntityClass
	 */
	public boolean supports(Class<?> classToCheck);
	
	/**
	 * get an entity by id
	 * @param id must not be null
	 * @return the entity or null if not found
	 * @throws IllegalArgumentException if id was null
	 */
	public T get(ID id);
	
	/**
	 * 
	 * @param id must not be null
	 * @return true if the entity with the given id exists, false otherwise
	 * @throws IllegalArgumentException if id was null
	 */
	public boolean exists(ID id);
	
	/**
	 * Creates a new entity
	 * @param entity must not be null
	 * @return the saved entity
	 * @throws IllegalArgumentException if entity was null
	 * @throws IllegalArgumentException if entity already has an id
	 * @throws DataIntegrityViolationException on error
	 */
	public T create(T entity);

	/**
	 * 
	 * @param id must not be null
	 * @param entity must not be null
	 * @return the saved entity
	 * @throws IllegalArgumentException if entity or id was null
	 * @throws IllegalArgumentException if entity already has an id
	 * @throws EntityAlreadyExistsException if id is already in use
	 */
	public T create(ID id, T entity);
	
	/**
	 * updates an entity
	 * @param id must not be null
	 * @param entity must not be null
	 * @return the saved entity
	 * @throws IllegalArgumentException if id or entity was null
	 * @throws IllegalArgumentException if id and entitys id are not equal
	 * @throws EntityNotFoundException if the entity does not exist
	 * @throws DataIntegrityViolationException on error
	 */
	public T update(ID id, T entity);
	

	/**
	 * validates an entity
	 * 
	 * @param entity the object to be validated
	 * @return true if the validation succeeded
	 * @throws EntityValidationException if an error occurs
	 */
	public boolean validate(T entity) throws ConstraintViolationException, EntityValidationException;
	
	/**
	 * deletes an entity
	 * @param id must not be null
	 * @throws IllegalArgumentException if id was null
	 * @throws EntityNotFoundException if id does not exist
	 */
	public void delete(ID id);
	
	/**
	 * 
	 * @param pageable {@link Pageable}
	 * @return data
	 */
	public Page<T> findAll(Pageable pageable);
	
	/**
	 * 
	 * @param sort {@link Sort}
	 * @return data
	 */
	public List<T> findAll(Sort sort);
	
}
