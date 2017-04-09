package org.sevensource.support.test.rest.mapper;

import org.mapstruct.Mapper;
import org.sevensource.support.rest.mapping.DefaultMappingConfig;
import org.sevensource.support.rest.mapping.EntityMapper;
import org.sevensource.support.test.jpa.domain.UUIDTestReferenceEntity;
import org.sevensource.support.test.rest.dto.TestRefDTO;

@Mapper(config=DefaultMappingConfig.class)
public interface UUIDTestReferenceEntityMapper extends EntityMapper<UUIDTestReferenceEntity, TestRefDTO> {
}
