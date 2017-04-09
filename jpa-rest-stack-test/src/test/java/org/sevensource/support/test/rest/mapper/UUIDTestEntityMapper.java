package org.sevensource.support.test.rest.mapper;

import org.mapstruct.Mapper;
import org.sevensource.support.rest.mapping.DefaultMappingConfig;
import org.sevensource.support.rest.mapping.EntityMapper;
import org.sevensource.support.test.jpa.domain.UUIDTestEntity;
import org.sevensource.support.test.rest.dto.TestDTO;

@Mapper(config=DefaultMappingConfig.class)
public interface UUIDTestEntityMapper extends EntityMapper<UUIDTestEntity, TestDTO> {
}
