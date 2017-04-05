package org.sevensource.support.rest.mapping;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

public abstract class AbstractEntityMapper<E,D> implements EntityMapper<E,D> {

	private final ModelMapper mapper;
	
	private final Class<E> entityClass;
	private final Class<D> dtoClass;
	
	public AbstractEntityMapper(ModelMapper mapper, Class<E> entityClass, Class<D> dtoClass) {
		this.mapper = mapper;
		this.entityClass = entityClass;
		this.dtoClass = dtoClass;
		
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
	public E toEntity(D dto, E destination) {
		mapper.map(dto, destination);
		return destination;
	}
	
	@Override
	public D toDTO(E entity) {
		return mapper.map(entity, dtoClass);
	}
}
