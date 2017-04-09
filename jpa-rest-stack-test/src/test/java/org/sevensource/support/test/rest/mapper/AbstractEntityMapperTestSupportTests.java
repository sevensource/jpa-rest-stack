package org.sevensource.support.test.rest.mapper;

import java.util.UUID;

import org.junit.runner.RunWith;
import org.sevensource.support.jpa.service.EntityService;
import org.sevensource.support.rest.configuration.CommonMappingConfiguration;
import org.sevensource.support.test.jpa.configuration.MockFactoryConfiguration;
import org.sevensource.support.test.jpa.domain.UUIDTestEntity;
import org.sevensource.support.test.jpa.domain.mock.UUIDTestEntityMockProvider;
import org.sevensource.support.test.jpa.domain.mock.UUIDTestReferenceEntityMockProvider;
import org.sevensource.support.test.rest.dto.TestDTO;
import org.sevensource.support.test.rest.mapping.AbstractEntityMapperTestSupport;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={CommonMappingConfiguration.class, MockFactoryConfiguration.class})
@Import({UUIDTestEntityMapperImpl.class,
	UUIDTestReferenceEntityMapperImpl.class,
	UUIDTestEntityMockProvider.class,
	UUIDTestReferenceEntityMockProvider.class})
public class AbstractEntityMapperTestSupportTests extends AbstractEntityMapperTestSupport<UUIDTestEntity, TestDTO> {
	
	@MockBean
	EntityService<UUIDTestEntity, UUID> serviceMock;
	
	
	public AbstractEntityMapperTestSupportTests() {
		super(UUIDTestEntity.class);
	}
}
