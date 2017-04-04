package org.sevensource.support.rest.mapping;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.ValidationException;
import org.sevensource.support.jpa.domain.AbstractUUIDEntity;
import org.sevensource.support.rest.configuration.CommonMappingConfiguration;
import org.sevensource.support.rest.dto.AbstractUUIDDTO;
import org.sevensource.support.rest.mapping.WrongPropertyAbstractEntityMapperTests.WrongPropertyTestEntityMapper.TestDestination;
import org.sevensource.support.rest.mapping.WrongPropertyAbstractEntityMapperTests.WrongPropertyTestEntityMapper.TestSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={CommonMappingConfiguration.class})
public class WrongPropertyAbstractEntityMapperTests {
	
	@Autowired
	ModelMapper mapper;
	
	static class WrongPropertyTestEntityMapper extends AbstractEntityMapper<TestSource, TestDestination> {

		public WrongPropertyTestEntityMapper(ModelMapper mapper) {
			super(mapper, TestSource.class, TestDestination.class);
		}
		
		public static class TestDestination extends AbstractUUIDDTO {
			private String name;
			public String getName() { return name; }
			public void setName(String name) { this.name = name; }
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
