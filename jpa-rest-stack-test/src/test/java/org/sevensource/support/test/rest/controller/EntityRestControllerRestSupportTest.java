package org.sevensource.support.test.rest.controller;

import java.util.UUID;

import org.junit.runner.RunWith;
import org.sevensource.support.jpa.service.EntityService;
import org.sevensource.support.test.jpa.domain.UUIDTestEntity;
import org.sevensource.support.test.jpa.domain.mock.UUIDTestEntityMockProvider;
import org.sevensource.support.test.jpa.domain.mock.UUIDTestReferenceEntityMockProvider;
import org.sevensource.support.test.jpa.service.UUIDTestEntityService;
import org.sevensource.support.test.rest.configuration.RestTestConfiguration;
import org.sevensource.support.test.rest.mapper.UUIDTestEntityMapperImpl;
import org.sevensource.support.test.rest.mapper.UUIDTestReferenceEntityMapperImpl;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers=SimpleTestEntityRestController.class)
@ContextConfiguration(classes={RestTestConfiguration.class})
@Import({SimpleTestEntityRestController.class,
	UUIDTestEntityMapperImpl.class,
	UUIDTestReferenceEntityMapperImpl.class,
	UUIDTestEntityMockProvider.class,
	UUIDTestReferenceEntityMockProvider.class})
public class EntityRestControllerRestSupportTest extends UUIDEntityRestControllerTestSupport<UUIDTestEntity> {

	@MockBean
	private UUIDTestEntityService service;
	
	@Override
	protected EntityService<UUIDTestEntity, UUID> getService() {
		return service;
	}
	
	@Override
	protected String getRootPath() {
		return SimpleTestEntityRestController.PATH;
	}
	
	@Override
	protected Class<UUIDTestEntity> getEntityClass() {
		return UUIDTestEntity.class;
	}
}
