package org.sevensource.support.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.sevensource.support.jpa.service.EntityService;
import org.sevensource.support.rest.configuration.CommonMappingConfiguration;
import org.sevensource.support.rest.configuration.CommonMvcConfiguration;
import org.sevensource.support.rest.controller.TestEntityRestController.TestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers=TestEntityRestController.class)
@ContextConfiguration(classes={CommonMvcConfiguration.class, CommonMappingConfiguration.class, TestEntityRestController.class})
public class AbstractEntityRestControllerPutTests {

	@MockBean
	private EntityService<TestEntity, UUID> service;
	
	@Autowired
	private MockMvc mvc;
	
	@Captor
	ArgumentCaptor<TestEntity> entityCaptor;

	@Captor
	ArgumentCaptor<UUID> idCaptor;
	
	EnhancedRandom random = EnhancedRandomBuilder.aNewEnhancedRandom();
	
	private final static UUID NIL_UUID = new UUID(0,0);
	
	private static String url(String path) {
		return TestEntityRestController.PATH + path;
	}
	
	@Test
	public void put_new_resource_works() throws Exception {
		String json = "{\"name\":	\"test\"}";
		
		when(service.exists(any())).thenReturn(false);
		
		when(service.create(idCaptor.capture(), entityCaptor.capture())).thenAnswer((c) -> {
			TestEntity e = random.nextObject(TestEntity.class);
			e.setId(idCaptor.getValue());
			e.setName(entityCaptor.getValue().getName());
			return e;
		});
		
		UUID requestedId = UUID.randomUUID();
		
		mvc
		.perform(put(url("/" + requestedId.toString()))
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(json))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.id").value(requestedId.toString()))
		.andExpect(jsonPath("$.name").value("test"))
		.andDo(print())
		.andReturn();
		
		assertThat(entityCaptor.getValue().getName()).isEqualTo("test");
	}
	
	@Test
	public void put_existing_resource_works() throws Exception {
		UUID requestedId = UUID.randomUUID();
		
		String json = "{\"id\": \"" + requestedId.toString() + "\", \"name\":	\"test\"}";

		TestEntity e = random.nextObject(TestEntity.class);
		
		when(service.get(eq(requestedId))).thenReturn(e);
		
		when(service.update(eq(requestedId), entityCaptor.capture())).thenAnswer((c) -> {
			e.setName(entityCaptor.getValue().getName());
			return e;
		});
		
		
		mvc
		.perform(put(url("/" + requestedId.toString()))
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(json))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(requestedId.toString()))
		.andExpect(jsonPath("$.name").value("test"))
		.andDo(print())
		.andReturn();
		
		assertThat(entityCaptor.getValue().getName()).isEqualTo("test");
	}
	
	@Test
	public void put_new_resource_with_different_id() throws Exception {
		String json = "{"
				+ "\"name\":	\"test\","
				+ "\"id\":	\"" + NIL_UUID.toString() + "\""
				+ "}";
		
		UUID requestedId = UUID.randomUUID();
		TestEntity e = random.nextObject(TestEntity.class);
		
		when(service.get(eq(requestedId))).thenReturn(e);
		
		when(service.update(eq(requestedId), entityCaptor.capture())).thenAnswer((c) -> {
			e.setName(entityCaptor.getValue().getName());
			return e;
		});
		
		mvc
		.perform(put(url("/" + requestedId.toString()))
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(json))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(requestedId.toString()))
		.andExpect(jsonPath("$.name").value("test"))
		.andDo(print())
		.andReturn();
		
		assertThat(entityCaptor.getValue().getName()).isEqualTo("test");
	}
}
