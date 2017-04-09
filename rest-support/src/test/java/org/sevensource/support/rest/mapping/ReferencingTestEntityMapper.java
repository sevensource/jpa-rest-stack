package org.sevensource.support.rest.mapping;

import org.mapstruct.Mapper;
import org.sevensource.support.rest.model.ReferencingTestDTO;
import org.sevensource.support.rest.model.ReferencingTestEntity;

@Mapper(config=DefaultMappingConfig.class,
		uses=ReferenceDTOEntityMapper.class)
public interface ReferencingTestEntityMapper extends EntityMapper<ReferencingTestEntity, ReferencingTestDTO> {

	
}
