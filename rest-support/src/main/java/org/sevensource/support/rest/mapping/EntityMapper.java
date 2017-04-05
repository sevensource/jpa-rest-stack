package org.sevensource.support.rest.mapping;

public interface EntityMapper<E,D> {
	E toEntity(D dto);
	E toEntity(D dto, E destination);
	D toDTO(E entity);
}
