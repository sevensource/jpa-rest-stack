package org.sevensource.support.rest.mapping;

import javax.annotation.PostConstruct;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.sevensource.support.jpa.domain.PersistentEntity;
import org.sevensource.support.rest.dto.IdentifiableDTO;

public abstract class AbstractEntityMapper<E extends PersistentEntity<?>, D extends IdentifiableDTO<?>>
	implements EntityMapper<E,D> {

	private final ModelMapper mapper;
	
	private final Class<E> entityClass;
	private final Class<D> dtoClass;
	
	public AbstractEntityMapper(ModelMapper mapper, Class<E> entityClass, Class<D> dtoClass) {
		this.mapper = mapper;
		this.entityClass = entityClass;
		this.dtoClass = dtoClass;
	}
	
	@PostConstruct
	public void postConstruct() {
		addMappings(mapper);
	}
	
	private void addMappings(ModelMapper mapper) {
		
		mapper.addMappings(createEntityToDtoMap());
		mapper.addMappings(createDtoToEntityMap());
		
		configureModelMapper(mapper);
		
		mapper.validate();
	}
	
	protected void configureModelMapper(ModelMapper mapper) {
	}
	
	protected PropertyMap<E, D> createEntityToDtoMap() {
		return new PropertyMap<E, D>(entityClass, dtoClass) {
			@Override
			protected void configure() {
			}
		};
	}
	
	protected PropertyMap<D, E> createDtoToEntityMap() {
		return new PropertyMap<D, E>(dtoClass, entityClass) {
			@Override
			protected void configure() {
			}
		};
	}
	
	@Override
	public E toEntity(D dto) {
		return mapper.map(dto, entityClass);
	}
	
	@Override
	public void toEntity(D dto, E destination) {
		mapper.map(dto, destination);
	}
	
	@Override
	public D toDTO(E entity) {
		return mapper.map(entity, dtoClass);
	}
}
