package org.sevensource.support.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.sevensource.support.jpa.service.EntityService;
import org.sevensource.support.rest.model.SimpleTestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers=SimpleTestEntityRestController.class)
@ContextConfiguration(classes={AbstractEntityControllerTestsConfiguration.class})
public class AbstractEntityRestControllerPostTests {

	@MockBean
	private EntityService<SimpleTestEntity, UUID> service;
	
	@Autowired
	private MockMvc mvc;
	
	@Captor
	private ArgumentCaptor<SimpleTestEntity> entityCaptor;

	@Captor
	private ArgumentCaptor<UUID> idCaptor;
	
	private EnhancedRandom random = EnhancedRandomBuilder.aNewEnhancedRandom();
	
	private static String url(String path) {
		return SimpleTestEntityRestController.PATH + path;
	}
	
	@Test
	public void post_resource_works() throws Exception {
		String json = "{\"name\":	\"test\"}";
		
		UUID assignedId = UUID.randomUUID();
		
		when(service.create(entityCaptor.capture())).thenAnswer((c) -> {
			SimpleTestEntity e = random.nextObject(SimpleTestEntity.class);
			e.setId(assignedId);
			e.setName(entityCaptor.getValue().getName());
			return e;
		});
		
		
		
		MvcResult result = mvc
		.perform(post(url(""))
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(json))
		.andExpect(status().isCreated())
		.andExpect(header().string(HttpHeaders.LOCATION, not(isEmptyOrNullString())))
		.andExpect(jsonPath("$.id").isNotEmpty())
		.andDo(print())
		.andReturn();
		
		assertThat(entityCaptor.getValue().getName()).isEqualTo("test");
		
		String link = result.getResponse().getHeader(HttpHeaders.LOCATION);
		assertThat(link).endsWith(SimpleTestEntityRestController.PATH + "/" + assignedId.toString());
	}
}
