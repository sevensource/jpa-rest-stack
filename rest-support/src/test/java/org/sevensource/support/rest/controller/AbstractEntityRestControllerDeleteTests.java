package org.sevensource.support.rest.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.sevensource.support.jpa.service.EntityService;
import org.sevensource.support.rest.model.SimpleTestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
public class AbstractEntityRestControllerDeleteTests {

	@MockBean
	private EntityService<SimpleTestEntity, UUID> service;
	
	@Autowired
	private MockMvc mvc;
	
	private static String url(String path) {
		return SimpleTestEntityRestController.PATH + path;
	}
	
	@Test
	public void delete_resource_works() throws Exception {
		UUID assignedId = UUID.randomUUID();
		
		when(service.exists(assignedId)).thenReturn(true);
		
		MvcResult result = mvc
		.perform(delete(url("/" + assignedId.toString()))
				.contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(status().isNoContent())
		.andDo(print())
		.andReturn();
		
		verify(service, times(1)).delete(assignedId);
	}
	
	@Test
	public void delete_resource_returns_404() throws Exception {
		UUID assignedId = UUID.randomUUID();
		
		when(service.exists(assignedId)).thenReturn(false);
		
		MvcResult result = mvc
		.perform(delete(url("/" + assignedId.toString()))
				.contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(status().isNotFound())
		.andDo(print())
		.andReturn();
	}
}
