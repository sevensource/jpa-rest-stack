package org.sevensource.support.rest.mapping;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.ValidationException;
import org.sevensource.support.jpa.domain.AbstractUUIDEntity;
import org.sevensource.support.rest.configuration.CommonMappingConfiguration;
import org.sevensource.support.rest.dto.AbstractUUIDDTO;
import org.sevensource.support.rest.mapping.MissingPropertyAbstractEntityMapperTests.MissingPropertyTestEntityMapper.TestDestination;
import org.sevensource.support.rest.mapping.MissingPropertyAbstractEntityMapperTests.MissingPropertyTestEntityMapper.TestSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={CommonMappingConfiguration.class})
public class MissingPropertyAbstractEntityMapperTests {
	
	@Autowired
	ModelMapper mapper;
	
	static class MissingPropertyTestEntityMapper extends AbstractEntityMapper<TestSource, TestDestination> {

		public MissingPropertyTestEntityMapper(ModelMapper mapper) {
			super(mapper, TestSource.class, TestDestination.class);
		}
		
		public static class TestDestination extends AbstractUUIDDTO {
			private String name;
			public String getName() { return name; }
			public void setName(String name) { this.name = name; }
		}
		
		public static class TestSource extends AbstractUUIDEntity {
		}
	}
	
	@Test(expected=ValidationException.class)
	public void shouldnotwork() {
		new MissingPropertyTestEntityMapper(mapper);
	}
}
