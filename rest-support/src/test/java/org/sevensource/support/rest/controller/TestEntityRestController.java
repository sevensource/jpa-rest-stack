package org.sevensource.support.rest.controller;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.sevensource.support.jpa.domain.AbstractUUIDEntity;
import org.sevensource.support.jpa.service.EntityService;
import org.sevensource.support.rest.controller.TestEntityRestController.TestDTO;
import org.sevensource.support.rest.controller.TestEntityRestController.TestEntity;
import org.sevensource.support.rest.dto.AbstractUUIDDTO;
import org.sevensource.support.rest.mapping.AbstractEntityMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(TestEntityRestController.PATH)
public class TestEntityRestController extends AbstractEntityRestController<UUID, TestEntity, TestDTO>{

	public final static String PATH = "/entity";

	public static class TestEntity extends AbstractUUIDEntity {
		private String name;
		public String getName() { return name; }
		public void setName(String name) { this.name = name; }
	}
	
	public static class TestDTO extends AbstractUUIDDTO {
		private String name;
		public String getName() { return name; }
		public void setName(String name) { this.name = name; }
	}
	
	public static class TestEntityMapper extends AbstractEntityMapper<TestEntity, TestDTO> {
		public TestEntityMapper(ModelMapper mapper) {
			super(mapper, TestEntity.class, TestDTO.class);
		}
	}
	
	public TestEntityRestController(EntityService<TestEntity, UUID> service, ModelMapper modelMapper) {
		super(service, new TestEntityMapper(modelMapper));
	}
}
