package org.sevensource.support.rest.mapping;

import org.mapstruct.MappingTarget;
import org.sevensource.support.jpa.domain.PersistentEntity;
import org.sevensource.support.rest.dto.IdentifiableDTO;

public interface EntityMapper<E extends PersistentEntity<?>, D extends IdentifiableDTO<?>> {
	E toEntity(D dto);
	void toEntity(D dto, @MappingTarget E destination);
	D toDTO(E entity);
}
