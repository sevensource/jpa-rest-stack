package org.sevensource.support.rest.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.sevensource.support.rest.configuration.CommonMappingConfiguration;
import org.sevensource.support.rest.dto.AbstractUUIDDTO;
import org.sevensource.support.rest.mapping.PrivateConstructorAbstractEntityMapperTests.PrivateConstructorTestEntityMapper;
import org.sevensource.support.rest.mapping.PrivateConstructorAbstractEntityMapperTests.PrivateConstructorTestEntityMapper.TestDestination;
import org.sevensource.support.rest.mapping.model.SimpleTestSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={CommonMappingConfiguration.class})
@Import({PrivateConstructorTestEntityMapper.class})
public class PrivateConstructorAbstractEntityMapperTests {
	
	@Autowired
	ModelMapper mapper;
	
	@Component
	static class PrivateConstructorTestEntityMapper extends AbstractEntityMapper<SimpleTestSource, TestDestination> {

		public PrivateConstructorTestEntityMapper(ModelMapper mapper) {
			super(mapper, SimpleTestSource.class, TestDestination.class);
		}
		
		public static class TestDestination extends AbstractUUIDDTO {
			private String name;
			private TestDestination() {}
			public TestDestination(String name) { this.name = name; }
			public String getName() { return name; }
			public void setName(String name) { this.name = name; }
		}
	}
	
	EnhancedRandom random = EnhancedRandomBuilder
			.aNewEnhancedRandomBuilder()
			.build();
	
	@Autowired
	PrivateConstructorTestEntityMapper testEntityMapper;
	
	@Test
	public void source_to_destination_works() {
		SimpleTestSource s = random.random(SimpleTestSource.class);
		TestDestination d = testEntityMapper.toDTO(s);
		
		assertThat(d.getId()).isEqualTo(s.getId());
		assertThat(d.getVersion()).isEqualTo(s.getVersion());
		assertThat(d.getName()).isEqualTo(s.getName());
	}
	
	@Test
	public void destination_to_source_works() {
		TestDestination d = random.random(TestDestination.class);
		SimpleTestSource s = testEntityMapper.toEntity(d);

		assertThat(s.getId()).isEqualTo(d.getId());
		assertThat(s.getVersion()).isNull(); //setter is non-existant
		assertThat(s.getName()).isEqualTo(d.getName());
	}
}
