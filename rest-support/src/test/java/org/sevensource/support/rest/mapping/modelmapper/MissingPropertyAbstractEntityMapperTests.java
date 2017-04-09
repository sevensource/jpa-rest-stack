//package org.sevensource.support.rest.mapping.modelmapper;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.modelmapper.ModelMapper;
//import org.modelmapper.ValidationException;
//import org.modelmapper.config.Configuration.AccessLevel;
//import org.modelmapper.convention.MatchingStrategies;
//import org.sevensource.support.jpa.domain.AbstractUUIDEntity;
//import org.sevensource.support.rest.configuration.CommonMappingConfiguration;
//import org.sevensource.support.rest.mapping.model.SimpleTestDestination;
//import org.sevensource.support.rest.mapping.modelmapper.MissingPropertyAbstractEntityMapperTests.MissingPropertyTestEntityMapper.TestSource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import io.github.benas.randombeans.EnhancedRandomBuilder;
//import io.github.benas.randombeans.api.EnhancedRandom;
//
//@RunWith(SpringRunner.class)
//@ContextConfiguration(classes={CommonMappingConfiguration.class})
//public class MissingPropertyAbstractEntityMapperTests {
//	
//	@Autowired
//	ModelMapper mapper;
//	
//	
//	
//	static class MissingPropertyTestEntityMapper extends AbstractEntityModelMapper<TestSource, SimpleTestDestination> {
//
//		public MissingPropertyTestEntityMapper(ModelMapper mapper) {
//			super(mapper, TestSource.class, SimpleTestDestination.class);
//		}
//		
//		public static class TestSource extends AbstractUUIDEntity {
//			public String firstname;
//			public String getFirstname() {
//				return firstname;
//			}
//			public void setFirstname(String firstname) {
//				this.firstname = firstname;
//			}
//		}
//	}
//	
//	EnhancedRandom random = EnhancedRandomBuilder.aNewEnhancedRandomBuilder().build();
//	
//	//@Test(expected=ValidationException.class)
//	@Test
//	public void shouldnotwork() {
////		mapper
////		.getConfiguration()
////		.setAmbiguityIgnored(false)
////		.setMethodAccessLevel(AccessLevel.PACKAGE_PRIVATE)
////		.setMatchingStrategy(MatchingStrategies.STRICT);
//		
//		mapper.validate();
//		
//		MissingPropertyTestEntityMapper em = new MissingPropertyTestEntityMapper(mapper);
//		TestSource s = random.nextObject(TestSource.class);
//		SimpleTestDestination d = em.toDTO(s);
//		
//		assertThat(s.getId()).isEqualTo(d.getId());
//		assertThat(d.getName()).isNull();
//	}
//}
