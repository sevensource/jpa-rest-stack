//package org.sevensource.support.rest.mapping.modelmapper;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.modelmapper.AbstractProvider;
//import org.modelmapper.ModelMapper;
//import org.sevensource.support.rest.configuration.CommonMappingConfiguration;
//import org.sevensource.support.rest.dto.AbstractUUIDDTO;
//import org.sevensource.support.rest.mapping.model.SimpleTestSource;
//import org.sevensource.support.rest.mapping.modelmapper.MissingNoArgsConstructorAbstractEntityMapperTests.MissingNoArgsConstructorTestEntityMapper;
//import org.sevensource.support.rest.mapping.modelmapper.MissingNoArgsConstructorAbstractEntityMapperTests.MissingNoArgsConstructorTestEntityMapper.TestDestination;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Import;
//import org.springframework.stereotype.Component;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import io.github.benas.randombeans.EnhancedRandomBuilder;
//import io.github.benas.randombeans.api.EnhancedRandom;
//
//@RunWith(SpringRunner.class)
//@ContextConfiguration(classes={CommonMappingConfiguration.class})
//@Import({MissingNoArgsConstructorTestEntityMapper.class})
//public class MissingNoArgsConstructorAbstractEntityMapperTests {
//	
//	@Autowired
//	ModelMapper mapper;
//	
//	@Component
//	static class MissingNoArgsConstructorTestEntityMapper extends AbstractEntityModelMapper<SimpleTestSource, TestDestination> {
//
//		public MissingNoArgsConstructorTestEntityMapper(ModelMapper mapper) {
//			super(mapper, SimpleTestSource.class, TestDestination.class);
//		}
//		
//		public static class TestDestination extends AbstractUUIDDTO {
//			private String name;
//			public TestDestination(String name) { this.name = name; }
//			public String getName() { return name; }
//			public void setName(String name) { this.name = name; }
//		}
//		
//		@Override
//		protected void configureModelMapper(ModelMapper mapper) {
//			mapper.getTypeMap(SimpleTestSource.class, TestDestination.class).setProvider(
//					new AbstractProvider<TestDestination>() {
//
//				@Override
//				protected TestDestination get() {
//					return new TestDestination("");
//				}
//			});
//		}
//	}
//	
//	EnhancedRandom random = EnhancedRandomBuilder
//			.aNewEnhancedRandomBuilder()
//			.build();
//	
//	@Autowired
//	MissingNoArgsConstructorTestEntityMapper testEntityMapper;
//	
//	@Test
//	public void source_to_destination_works() {
//		SimpleTestSource s = random.nextObject(SimpleTestSource.class);
//		TestDestination d = testEntityMapper.toDTO(s);
//		
//		assertThat(d.getId()).isEqualTo(s.getId());
//		assertThat(d.getVersion()).isEqualTo(s.getVersion());
//		assertThat(d.getName()).isEqualTo(s.getName());
//	}
//	
//	@Test
//	public void destination_to_source_works() {
//		TestDestination d = random.nextObject(TestDestination.class);
//		SimpleTestSource s = testEntityMapper.toEntity(d);
//
//		assertThat(s.getId()).isEqualTo(d.getId());
//		assertThat(s.getVersion()).isNull(); //setter is non-existant
//		assertThat(s.getName()).isEqualTo(d.getName());
//	}
//}
