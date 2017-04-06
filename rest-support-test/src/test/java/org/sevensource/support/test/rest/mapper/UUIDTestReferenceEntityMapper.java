package org.sevensource.support.test.rest.mapper;

import org.modelmapper.ModelMapper;
import org.sevensource.support.rest.mapping.AbstractEntityMapper;
import org.sevensource.support.test.rest.domain.UUIDTestEntity;
import org.sevensource.support.test.rest.dto.TestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UUIDTestReferenceEntityMapper extends AbstractEntityMapper<UUIDTestEntity, TestDTO> {

	@Autowired
	public UUIDTestReferenceEntityMapper(ModelMapper mapper) {
		super(mapper, UUIDTestEntity.class, TestDTO.class);
	}
}
