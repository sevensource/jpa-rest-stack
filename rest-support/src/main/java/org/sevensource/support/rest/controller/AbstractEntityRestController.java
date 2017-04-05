package org.sevensource.support.rest.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.sevensource.support.jpa.domain.PersistentEntity;
import org.sevensource.support.jpa.service.EntityService;
import org.sevensource.support.rest.dto.IdentifiableDTO;
import org.sevensource.support.rest.mapping.EntityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;


public abstract class AbstractEntityRestController<ID extends Serializable, E extends PersistentEntity<ID>, DTO extends IdentifiableDTO<ID>> {

	private static final Logger logger = LoggerFactory.getLogger(AbstractEntityRestController.class);
		
	private final EntityService<E, ID> entityService;
	private final EntityMapper<E,DTO> mapper;
	
	public AbstractEntityRestController(EntityService<E, ID> entityService, EntityMapper<E,DTO> mapper) {
		this.entityService = entityService;
		this.mapper = mapper;
	}
	
	protected DTO toResource(E entity) {
		return mapper.toDTO(entity);
	}
	
	protected E toEntity(DTO resource) {
		return mapper.toEntity(resource);
	}
	
	protected E toEntity(DTO resource, E destination) {
		return mapper.toEntity(resource, destination);
	}
	
	protected final List<DTO> toResources(Iterable<E> entities) {
		if(entities == null)
			return Collections.emptyList();
					
		List<DTO> list = new ArrayList<>();

		for(E entity : entities) {
			DTO dto = toResource(entity);
			list.add(dto);
		}
		return list;
	}
	
	@GetMapping("")
	@ResponseBody
	public List<DTO> getCollectionResource(Pageable pageable, Sort sort) {

		Iterable<E> results = pageable == null ? entityService.findAll(sort)
				: entityService.findAll(pageable);
		
		List<DTO> dtos = toResources(results);
		return dtos;
	}
	
	@PostMapping("")
	public ResponseEntity<DTO> postResource(@RequestBody DTO objectToSave) {
		final E entityToSave = toEntity(objectToSave);
		final E savedEntity = entityService.create(entityToSave);
		
		final Link selfLink = ControllerLinkBuilder
				.linkTo(this.getClass())
				.slash(savedEntity.getId())
				.withSelfRel();
		
		final String href = selfLink.getHref();
		final DTO dto = toResource(savedEntity);
		
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.header(HttpHeaders.LOCATION, href)
				.body(dto);
	}
	
//	public ResponseEntity<List<String>> postCollectionResource() {
//		return ResponseEntity.status(HttpStatus.CREATED).header(HttpHeaders.LOCATION, "").build();
//	}
	
	@GetMapping("/{id}")
	public ResponseEntity<DTO> getItemResource(@PathVariable ID id) {

		final E domainObj = entityService.get(id);

		if (domainObj == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		DTO dto = toResource(domainObj);
		
		return ResponseEntity.ok().body(dto);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<DTO> putItemResource(@PathVariable ID id, @RequestBody DTO dto) {

		dto.setId(id);

		E entityToSave = entityService.get(id);
		E savedEntity = null;
		HttpStatus status;
		
		if(entityToSave != null) {
			entityToSave = toEntity(dto, entityToSave);
			savedEntity = entityService.update(id, entityToSave);
			status = HttpStatus.OK;
		} else {
			entityToSave = toEntity(dto);
			savedEntity = entityService.create(id, entityToSave);
			status = HttpStatus.CREATED;
		}
		
		final DTO savedDto = toResource(savedEntity);
		return ResponseEntity.status(status).body(savedDto);
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<DTO> patchItemResource(@PathVariable UUID id, DTO objectToSave) {
		throw new RuntimeException("Not yet implemented");
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteItemResource(@PathVariable ID id) {
		
		boolean exists = entityService.exists(id);
		
		if (exists) {
			entityService.delete(id);
			return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
