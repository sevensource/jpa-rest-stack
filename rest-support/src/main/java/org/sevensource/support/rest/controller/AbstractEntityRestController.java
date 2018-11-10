package org.sevensource.support.rest.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.sevensource.support.jpa.domain.PersistentEntity;
import org.sevensource.support.jpa.service.EntityService;
import org.sevensource.support.rest.dto.IdentifiableDTO;
import org.sevensource.support.rest.dto.PagedCollectionResourceDTO;
import org.sevensource.support.rest.etag.ETag;
import org.sevensource.support.rest.mapping.EntityMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources.PageMetadata;
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
import org.springframework.web.bind.annotation.RequestHeader;


public abstract class AbstractEntityRestController<ID extends Serializable, E extends PersistentEntity<ID>, DTO extends IdentifiableDTO<ID>> {

	private final EntityService<E, ID> entityService;
	private final EntityMapper<E,DTO> mapper;

	public AbstractEntityRestController(EntityService<E, ID> entityService, EntityMapper<E,DTO> mapper) {
		this.entityService = entityService;
		this.mapper = mapper;
	}

	protected E toEntity(DTO resource) {
		return mapper.toEntity(resource);
	}

	protected void toEntity(DTO resource, E destination) {
		mapper.toEntity(resource, destination);
	}

	protected DTO toResource(E entity) {
		return mapper.toDTO(entity);
	}

	protected List<DTO> toResources(Iterable<E> entities) {
		if(entities == null) {
			return Collections.emptyList();
		}

		final List<DTO> list = new ArrayList<>();

		for(E entity : entities) {
			DTO dto = toResource(entity);
			list.add(dto);
		}
		return list;
	}

	@GetMapping("")
	public ResponseEntity<?> getCollectionResource(@PageableDefault(size=100) Pageable pageable, Sort sort) {
		if(pageable.isUnpaged()) {
			final List<E> results = entityService.findAll(sort);
			final List<DTO> dtos = toResources(results);
			return ResponseEntity.ok(dtos);
		} else {
			final Page<E> page = entityService.findAll(pageable);
			if(page == null) {
				return ResponseEntity.notFound().build();
			}
			final List<DTO> dtos = toResources(page);
			final PageMetadata pageMetadata = new PageMetadata(page.getSize(), page.getNumber(), page.getTotalElements(), page.getTotalPages());
			final PagedCollectionResourceDTO<DTO> dto = new PagedCollectionResourceDTO<>(dtos, pageMetadata);
			return ResponseEntity.ok(dto);
		}
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

	@GetMapping("/{id}")
	public ResponseEntity<DTO> getItemResource(@PathVariable ID id, @RequestHeader HttpHeaders requestHeaders) {

		final E domainObj = entityService.get(id);

		if (domainObj == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		final ETag etag = ETag.from(domainObj);
		
		List<String> ifNoneMatch = requestHeaders.getIfNoneMatch();
		if(! ifNoneMatch.isEmpty()) {
			ETag requestETag = ETag.from(ifNoneMatch.get(0));
			if(etag.equals(requestETag)) {
				return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
			}
		}
		
		long ifModifiedSince = requestHeaders.getIfModifiedSince();
		if(ifModifiedSince != -1 && domainObj.getLastModifiedDate().toEpochMilli() <= ifModifiedSince) {
			return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
		}
		
		DTO dto = toResource(domainObj);
		HttpHeaders headers = etag.addTo(new HttpHeaders());
		headers.setLastModified(domainObj.getLastModifiedDate().toEpochMilli());
		
		return ResponseEntity.ok()
				.headers(headers)
				.body(dto);
	}

	@PutMapping("/{id}")
	public ResponseEntity<DTO> putItemResource(@PathVariable ID id, @RequestBody DTO dto, ETag etag, @RequestHeader HttpHeaders requestHeaders) {

		dto.setId(id);

		E entityToSave = entityService.get(id);
		E savedEntity = null;
		HttpStatus status;

		if(entityToSave != null) {
			
			if(etag != null && ! etag.equals(ETag.NO_ETAG)) {
				final ETag expectedETag = ETag.from(entityToSave);
				if(! expectedETag.equals(etag)) {
					HttpHeaders headers = expectedETag.addTo(new HttpHeaders());
					return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).headers(headers).build();
				}
			}
			
			toEntity(dto, entityToSave);
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
		throw new IllegalArgumentException("Not yet implemented");
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteItemResource(@PathVariable ID id) {

		boolean exists = entityService.exists(id);

		if (exists) {
			entityService.delete(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
