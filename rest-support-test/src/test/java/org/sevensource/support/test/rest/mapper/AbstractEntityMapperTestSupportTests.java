package org.sevensource.support.test.rest.mapper;

import org.junit.runner.RunWith;
import org.sevensource.support.rest.configuration.CommonMappingConfiguration;
import org.sevensource.support.test.jpa.configuration.MockFactoryConfiguration;
import org.sevensource.support.test.rest.domain.UUIDTestEntity;
import org.sevensource.support.test.rest.dto.TestDTO;
import org.sevensource.support.test.rest.mapping.AbstractEntityMapperTestSupport;
import org.sevensource.support.test.rest.mock.UUIDTestEntityMockProvider;
import org.sevensource.support.test.rest.mock.UUIDTestReferenceEntityMockProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={CommonMappingConfiguration.class, MockFactoryConfiguration.class})
@Import({UUIDTestEntityMapper.class,
	UUIDTestReferenceEntityMapper.class,
	UUIDTestEntityMockProvider.class,
	UUIDTestReferenceEntityMockProvider.class})
public class AbstractEntityMapperTestSupportTests extends AbstractEntityMapperTestSupport<UUIDTestEntity, TestDTO> {

	@Autowired
	UUIDTestEntityMapper mapper;
	
	public AbstractEntityMapperTestSupportTests() {
		super(UUIDTestEntity.class);
	}
}
