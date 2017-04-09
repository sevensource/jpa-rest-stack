package org.sevensource.support.rest.controller;

import java.util.UUID;

import org.sevensource.support.jpa.service.EntityService;
import org.sevensource.support.rest.mapping.SimpleTestEntityMapper;
import org.sevensource.support.rest.model.SimpleTestDTO;
import org.sevensource.support.rest.model.SimpleTestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(SimpleTestEntityRestController.PATH)
public class SimpleTestEntityRestController extends AbstractEntityRestController<UUID, SimpleTestEntity, SimpleTestDTO>{

	public final static String PATH = "/entity";
	
	@Autowired
	public SimpleTestEntityRestController(EntityService<SimpleTestEntity, UUID> service, SimpleTestEntityMapper mapper) {
		super(service, mapper);
	}
}
