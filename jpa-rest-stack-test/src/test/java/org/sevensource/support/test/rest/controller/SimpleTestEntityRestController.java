package org.sevensource.support.test.rest.controller;

import java.util.UUID;

import org.sevensource.support.jpa.service.EntityService;
import org.sevensource.support.rest.controller.AbstractEntityRestController;
import org.sevensource.support.test.jpa.domain.UUIDTestEntity;
import org.sevensource.support.test.rest.dto.TestDTO;
import org.sevensource.support.test.rest.mapper.UUIDTestEntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(SimpleTestEntityRestController.PATH)
public class SimpleTestEntityRestController extends AbstractEntityRestController<UUID, UUIDTestEntity, TestDTO>{

	public final static String PATH = "/entity";
	
	@Autowired
	public SimpleTestEntityRestController(EntityService<UUIDTestEntity, UUID> service, UUIDTestEntityMapper mapper, ConversionService conversionService) {
		super(service, mapper, conversionService);
	}
}
