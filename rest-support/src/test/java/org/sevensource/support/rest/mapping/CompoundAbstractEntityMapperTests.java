package org.sevensource.support.rest.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.sevensource.support.jpa.domain.AbstractUUIDEntity;
import org.sevensource.support.rest.configuration.CommonMappingConfiguration;
import org.sevensource.support.rest.mapping.CompoundAbstractEntityMapperTests.CompoundTestEntityMapper;
import org.sevensource.support.rest.mapping.CompoundAbstractEntityMapperTests.CompoundTestEntityMapper.TestSource;
import org.sevensource.support.rest.mapping.model.SimpleTestDestination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={CommonMappingConfiguration.class})
@Import({CompoundTestEntityMapper.class})
public class CompoundAbstractEntityMapperTests {
	
	@Component
	static class CompoundTestEntityMapper extends AbstractEntityMapper<TestSource, SimpleTestDestination> {

		public CompoundTestEntityMapper(ModelMapper mapper) {
			super(mapper, TestSource.class, SimpleTestDestination.class);
		}
		
		public static class TestSource extends AbstractUUIDEntity {
			private String lastname;
			private String firstname;
			public String getFirstname() { return firstname; }
			public void setFirstname(String name) { this.firstname = name; }
			public String getLastname() { return lastname; }
			public void setLastname(String lastname) { this.lastname = lastname; }
		}
		
		@Override
		protected PropertyMap<TestSource, SimpleTestDestination> createEntityToDtoMap() {
			return new PropertyMap<TestSource, SimpleTestDestination>(TestSource.class, SimpleTestDestination.class) {
				
				Converter<TestSource, String> toName = new AbstractConverter<TestSource, String>() {
					  protected String convert(TestSource source) {
					    return source == null ? null : source.getFirstname() + " " + source.getLastname();
					  }
					};
					
				@Override
				protected void configure() {
					using(toName).map(source).setName(null);
				}
			};
		}
		
		@Override
		protected PropertyMap<SimpleTestDestination, TestSource> createDtoToEntityMap() {
			return new PropertyMap<SimpleTestDestination, TestSource>(SimpleTestDestination.class, TestSource.class) {
				
				Converter<SimpleTestDestination, String> toFirstname = new AbstractConverter<SimpleTestDestination, String>() {
					  protected String convert(SimpleTestDestination source) {
					    return source == null ? null : source.getName().split(" ")[0];
					  }
					};
					
					Converter<SimpleTestDestination, String> toLastname = new AbstractConverter<SimpleTestDestination, String>() {
						  protected String convert(SimpleTestDestination source) {
						    return source == null ? null : source.getName().split(" ")[1];
						  }
						};
					
				@Override
				protected void configure() {
					using(toFirstname).map(source).setFirstname(null);
					using(toLastname).map(source).setLastname(null);
				}
			};
		}
	}
	
	
	EnhancedRandom random = EnhancedRandomBuilder.aNewEnhancedRandomBuilder().build();
	
	@Autowired
	CompoundTestEntityMapper mapper;
	
	
	private final String firstname = "John";
	private final String lastname = "Doe";
	private final String name = String.join(" ", firstname, lastname);
	
	@Test
	public void source_to_destination_works() {
		TestSource s = random.random(TestSource.class);
		s.setFirstname(firstname);
		s.setLastname(lastname);
		
		SimpleTestDestination d = mapper.toDTO(s);
		
		assertThat(d.getId()).isEqualTo(d.getId());
		assertThat(d.getVersion()).isEqualTo(d.getVersion());
		assertThat(d.getName()).isEqualTo(name);
	}
	
	@Test
	public void destination_to_source_works() {
		SimpleTestDestination d = random.random(SimpleTestDestination.class);
		d.setName(name);
		
		TestSource s = mapper.toEntity(d);

		assertThat(s.getId()).isEqualTo(s.getId());
		assertThat(s.getVersion()).isNull(); //setter is non-existant
		assertThat(s.getFirstname()).isEqualTo(firstname);
		assertThat(s.getLastname()).isEqualTo(lastname);
	}
	
}
