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
import org.sevensource.support.jpa.exception.EntityNotFoundException;
import org.sevensource.support.jpa.service.EntityService;
import org.sevensource.support.rest.configuration.CommonMappingConfiguration;
import org.sevensource.support.rest.model.ReferencingTestDTO;
import org.sevensource.support.rest.model.ReferencingTestEntity;
import org.sevensource.support.rest.model.SimpleTestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={CommonMappingConfiguration.class})
@Import({ReferencingTestEntityMapperImpl.class})
public class ReferenceDTOEntityMapperTest {

	@MockBean
	EntityService<ReferencingTestEntity, UUID> refService;

	@MockBean
	EntityService<SimpleTestEntity, UUID> simpleService;

	@Captor
	ArgumentCaptor<UUID> idCaptor;

	@Autowired
	ReferencingTestEntityMapper mapper;

	EnhancedRandom random = EnhancedRandomBuilder.aNewEnhancedRandomBuilder().build();

	@Before
	public void before() {
		when(refService.supports(eq(ReferencingTestEntity.class))).thenReturn(true);
		when(simpleService.supports(eq(SimpleTestEntity.class))).thenReturn(true);
		when(simpleService.get(idCaptor.capture())).thenAnswer((c) -> {
			UUID id = c.getArgument(0);
			SimpleTestEntity s = new SimpleTestEntity();
			s.setId(id);
			s.setName("IT WORKS");
			return s;
		});
	}

	@Test
	public void source_to_destination_works() {
		ReferencingTestEntity s = random.nextObject(ReferencingTestEntity.class);
		ReferencingTestDTO d = mapper.toDTO(s);

		assertThat(d.getId()).isEqualTo(s.getId());
		assertThat(d.getVersion()).isEqualTo(s.getVersion());
		assertThat(d.getName()).isEqualTo(s.getName());
		assertThat(d.getReference().getId()).isEqualTo(s.getReference().getId());
	}

	@Test
	public void destination_to_source_works() {
		ReferencingTestDTO d = random.nextObject(ReferencingTestDTO.class);
		ReferencingTestEntity s = mapper.toEntity(d);

		assertThat(s.getId()).isEqualTo(d.getId());
		assertThat(s.getName()).isEqualTo(d.getName());
		assertThat(s.getReference().getId()).isEqualTo(d.getReference().getId());
		assertThat(s.getReference().getName()).isEqualTo("IT WORKS");
	}

	@Test(expected=EntityNotFoundException.class)
	public void unknown_reference_throws_entityNotFoundException() {
		when(simpleService.get(any())).thenReturn(null);
		ReferencingTestDTO d = random.nextObject(ReferencingTestDTO.class);
		mapper.toEntity(d);
	}

	@Test
	public void null_reference_resolves_as_null() {
		ReferencingTestDTO d = random.nextObject(ReferencingTestDTO.class);
		d.setReference(null);

		ReferencingTestEntity s = mapper.toEntity(d);
		assertThat(s.getReference()).isNull();
	}
}
