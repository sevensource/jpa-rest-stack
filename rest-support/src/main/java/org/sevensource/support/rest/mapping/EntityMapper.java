package org.sevensource.support.rest.mapping;

public interface EntityMapper<E,D> {
	E toEntity(D dto);
	D toDTO(E entity);
}
