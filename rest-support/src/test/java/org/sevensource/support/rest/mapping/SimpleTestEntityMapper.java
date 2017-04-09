package org.sevensource.support.rest.mapping;

import org.mapstruct.Mapper;
import org.sevensource.support.rest.model.ReferencingTestDTO;
import org.sevensource.support.rest.model.ReferencingTestEntity;
import org.sevensource.support.rest.model.SimpleTestDTO;
import org.sevensource.support.rest.model.SimpleTestEntity;

@Mapper(config=DefaultMappingConfig.class)
public interface SimpleTestEntityMapper extends EntityMapper<SimpleTestEntity, SimpleTestDTO> {

	
}
