package org.sevensource.support.rest.controller;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.sevensource.support.jpa.domain.PersistentEntity;
import org.sevensource.support.jpa.filter.FilterCriteria;
import org.sevensource.support.jpa.service.EntityService;
import org.sevensource.support.rest.dto.IdentifiableDTO;
import org.sevensource.support.rest.dto.PagedCollectionResourceDTO;
import org.sevensource.support.rest.etag.ETag;
import org.sevensource.support.rest.filter.AnnotationBasedFilterCriteriaTransformer;
import org.sevensource.support.rest.filter.FilterCriteriaTransformer;
import org.sevensource.support.rest.filter.RSQLFilterCriteriaParser;
import org.sevensource.support.rest.mapping.EntityMapper;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.convert.ConversionService;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


public abstract class AbstractEntityRestController<ID extends Serializable, E extends PersistentEntity<ID>, DTO extends IdentifiableDTO<ID>> {

	private final EntityService<E, ID> entityService;
	private final EntityMapper<E,DTO> mapper;
	private final FilterCriteriaTransformer filterCriteriaTransformer; 
	
	public static final String FILTER_PARAM_NAME = "query";

	public AbstractEntityRestController(EntityService<E, ID> entityService, EntityMapper<E,DTO> mapper) {
		this.entityService = entityService;
		this.mapper = mapper;
		this.filterCriteriaTransformer = buildFilterCriteriaTransformer();
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
	public ResponseEntity<?> getCollectionResource(
			@RequestParam(required=false, name=FILTER_PARAM_NAME) String queryFilter,
			@PageableDefault(size=100) Pageable pageable,
			Sort sort) {
		
		FilterCriteria filterCriteria = null;
		if(StringUtils.hasText(queryFilter)) {
			filterCriteria = RSQLFilterCriteriaParser.parse(queryFilter, filterCriteriaTransformer);
		}
		
		
		if(pageable.isUnpaged()) {
			final List<E> results = entityService.findAll(filterCriteria, sort);
			final List<DTO> dtos = toResources(results);
			return ResponseEntity.ok(dtos);
		} else {
			final Page<E> page = entityService.findAll(filterCriteria, pageable);
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

		final E entity = entityService.get(id);

		if (entity == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		if(resourceIsValid(entity, requestHeaders)) {
			return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
		}
		
		DTO dto = toResource(entity);
		HttpHeaders headers = ETag.from(entity).addTo(new HttpHeaders());
		headers.setLastModified(entity.getLastModifiedDate().toEpochMilli());
		
		return ResponseEntity.ok()
				.headers(headers)
				.body(dto);
	}
	
	private boolean resourceIsValid(E entity, HttpHeaders requestHeaders) {
		List<String> ifNoneMatch = requestHeaders.getIfNoneMatch();
		if(! ifNoneMatch.isEmpty()) {
			ETag expectedEtag = ETag.from(entity);
			ETag requestedETag = ETag.from(ifNoneMatch.get(0));
			return expectedEtag.equals(requestedETag);
		}
		
		final long requestedIfModifiedSince = requestHeaders.getIfModifiedSince();
		if(requestedIfModifiedSince != -1) {
			final long lastModified = entity.getLastModifiedDate().toEpochMilli();
			return lastModified <= requestedIfModifiedSince;
		}
		
		return false;
	}

	@PutMapping("/{id}")
	public ResponseEntity<DTO> putItemResource(@PathVariable ID id, @RequestBody DTO dto, ETag etag, @RequestHeader HttpHeaders requestHeaders) {

		dto.setId(id);

		E entityToSave = entityService.get(id);
		E savedEntity = null;
		HttpStatus status;

		if(entityToSave != null) {
			if(! matchesPrecondition(entityToSave, etag)) {
				HttpHeaders headers = ETag.from(entityToSave).addTo(new HttpHeaders());
				return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).headers(headers).build();				
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
		HttpHeaders headers = ETag.from(savedEntity).addTo(new HttpHeaders());
		headers.setLastModified(savedEntity.getLastModifiedDate().toEpochMilli());
		
		return ResponseEntity.status(status).headers(headers).body(savedDto);
	}
	
	private boolean matchesPrecondition(E entity, ETag requestedETag) {
		if(ETag.NO_ETAG.equals(requestedETag)) {
			return true;
		}
		
		return ETag.from(entity).equals(requestedETag);
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
	
	protected FilterCriteriaTransformer buildFilterCriteriaTransformer() {
		Class<?> queryFilterClass = getQueryFilterClass();
		return new AnnotationBasedFilterCriteriaTransformer(queryFilterClass, getConversionService());
	}
	
	@SuppressWarnings("rawtypes")
	protected Class<?> getQueryFilterClass() {
		Map<TypeVariable, Type> typeVariableMap = GenericTypeResolver.getTypeVariableMap(getClass());
		return typeVariableMap
			.values()
			.stream()
			.map(c -> GenericTypeResolver.resolveType(c, typeVariableMap))
			.filter(IdentifiableDTO.class::isAssignableFrom)
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("Cannot infer queryFilterClass"));
	}
	
	protected ConversionService getConversionService() {
		return ApplicationConversionService.getSharedInstance();
	}
}
