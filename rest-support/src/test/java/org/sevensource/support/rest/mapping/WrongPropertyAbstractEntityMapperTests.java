package org.sevensource.support.rest.mapping;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.ValidationException;
import org.sevensource.support.jpa.domain.AbstractUUIDEntity;
import org.sevensource.support.rest.configuration.CommonMappingConfiguration;
import org.sevensource.support.rest.mapping.WrongPropertyAbstractEntityMapperTests.WrongPropertyTestEntityMapper.TestSource;
import org.sevensource.support.rest.mapping.model.SimpleTestDestination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={CommonMappingConfiguration.class})
public class WrongPropertyAbstractEntityMapperTests {
	
	@Autowired
	ModelMapper mapper;
	
	static class WrongPropertyTestEntityMapper extends AbstractEntityMapper<TestSource, SimpleTestDestination> {

		public WrongPropertyTestEntityMapper(ModelMapper mapper) {
			super(mapper, TestSource.class, SimpleTestDestination.class);
		}
		
		public static class TestSource extends AbstractUUIDEntity {
			private String firstname;
			public String getFirstname() { return firstname; }
			public void setFirstname(String name) { this.firstname = name; }
		}
	}
	
	@Test(expected=ValidationException.class)
	public void shouldnotwork() {
		new WrongPropertyTestEntityMapper(mapper);
	}
}
