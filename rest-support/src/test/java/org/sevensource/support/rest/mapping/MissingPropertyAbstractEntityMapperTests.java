package org.sevensource.support.rest.mapping;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.ValidationException;
import org.sevensource.support.jpa.domain.AbstractUUIDEntity;
import org.sevensource.support.rest.configuration.CommonMappingConfiguration;
import org.sevensource.support.rest.mapping.MissingPropertyAbstractEntityMapperTests.MissingPropertyTestEntityMapper.TestSource;
import org.sevensource.support.rest.mapping.model.SimpleTestDestination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={CommonMappingConfiguration.class})
public class MissingPropertyAbstractEntityMapperTests {
	
	@Autowired
	ModelMapper mapper;
	
	static class MissingPropertyTestEntityMapper extends AbstractEntityMapper<TestSource, SimpleTestDestination> {

		public MissingPropertyTestEntityMapper(ModelMapper mapper) {
			super(mapper, TestSource.class, SimpleTestDestination.class);
		}
		
		public static class TestSource extends AbstractUUIDEntity {
		}
	}
	
	@Test(expected=ValidationException.class)
	public void shouldnotwork() {
		new MissingPropertyTestEntityMapper(mapper);
	}
}
