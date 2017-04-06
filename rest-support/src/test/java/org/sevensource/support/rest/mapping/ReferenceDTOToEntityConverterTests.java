package org.sevensource.support.rest.mapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.sevensource.support.jpa.service.EntityService;
import org.sevensource.support.rest.configuration.CommonMappingConfiguration;
import org.sevensource.support.rest.dto.ReferenceDTO;
import org.sevensource.support.rest.mapping.ReferenceDTOToEntityConverterTests.ReferencingTestEntityMapper;
import org.sevensource.support.rest.mapping.model.ReferencingTestDestination;
import org.sevensource.support.rest.mapping.model.ReferencingTestSource;
import org.sevensource.support.rest.mapping.model.SimpleTestDestination;
import org.sevensource.support.rest.mapping.model.SimpleTestSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={CommonMappingConfiguration.class})
@Import({ReferencingTestEntityMapper.class})
public class ReferenceDTOToEntityConverterTests {
	
	@MockBean
	EntityService<ReferencingTestSource, UUID> refService;
	
	@MockBean
	EntityService<SimpleTestSource, UUID> simpleService;
	
	EnhancedRandom random = EnhancedRandomBuilder.aNewEnhancedRandomBuilder().build();
	
	@Autowired
	ReferencingTestEntityMapper testEntityMapper;
	
	@Autowired
	ModelMapper modelMapper;
	
	@Captor
	ArgumentCaptor<UUID> idCaptor;
	
	@Component
	static class ReferencingTestEntityMapper extends AbstractEntityMapper<ReferencingTestSource, ReferencingTestDestination> {

		@Autowired
		ReferenceDTOToEntityConverter<?> converter;
		
		public ReferencingTestEntityMapper(ModelMapper mapper) {
			super(mapper, ReferencingTestSource.class, ReferencingTestDestination.class);
		}
		
		@Override
		protected PropertyMap<ReferencingTestDestination, ReferencingTestSource> createDtoToEntityMap() {
			return new PropertyMap<ReferencingTestDestination, ReferencingTestSource>(ReferencingTestDestination.class, ReferencingTestSource.class) {
				@Override
				protected void configure() {
					using(converter).map(source.getReference()).setReference(null);
				}
			};
		}
	}
	
	@Before
	public void before() {
		when(refService.supports(any())).thenReturn(false);
		when(simpleService.supports(eq(SimpleTestSource.class))).thenReturn(true);
		when(simpleService.get(idCaptor.capture())).thenAnswer((c) -> {
			UUID id = c.getArgument(0);
			SimpleTestSource s = new SimpleTestSource();
			s.setId(id);
			s.setName("IT WORKS");
			return s;
		});
	}
	
	
	@Test
	public void source_to_destination_works() {
		ReferencingTestSource s = random.random(ReferencingTestSource.class);
		ReferencingTestDestination d = testEntityMapper.toDTO(s);
		
		assertThat(d.getId()).isEqualTo(s.getId());
		assertThat(d.getVersion()).isEqualTo(s.getVersion());
		assertThat(d.getName()).isEqualTo(s.getName());
		assertThat(d.getReference().getId()).isEqualTo(s.getReference().getId());
	}
	
	@Test
	public void destination_to_source_works() {
		ReferencingTestDestination d = random.random(ReferencingTestDestination.class);
		ReferencingTestSource s = testEntityMapper.toEntity(d);

		assertThat(s.getId()).isEqualTo(d.getId());
		assertThat(s.getVersion()).isNull(); //setter is non-existant
		assertThat(s.getName()).isEqualTo(d.getName());
		assertThat(s.getReference().getId()).isEqualTo(d.getReference().getId());
		assertThat(s.getReference().getName()).isEqualTo("IT WORKS");
	}
}
