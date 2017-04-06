package org.sevensource.support.test.rest.mapper;

import org.modelmapper.ModelMapper;
import org.sevensource.support.rest.mapping.AbstractEntityMapper;
import org.sevensource.support.test.rest.domain.UUIDTestReferenceEntity;
import org.sevensource.support.test.rest.dto.TestRefDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UUIDTestEntityMapper extends AbstractEntityMapper<UUIDTestReferenceEntity, TestRefDTO> {

	@Autowired
	public UUIDTestEntityMapper(ModelMapper mapper) {
		super(mapper, UUIDTestReferenceEntity.class, TestRefDTO.class);
	}
}
