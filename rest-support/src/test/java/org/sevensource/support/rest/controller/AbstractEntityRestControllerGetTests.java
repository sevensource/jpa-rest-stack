package org.sevensource.support.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sevensource.support.jpa.service.EntityService;
import org.sevensource.support.rest.model.SimpleTestEntity;
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
@WebMvcTest(controllers=SimpleTestEntityRestController.class)
@ContextConfiguration(classes={AbstractEntityControllerTestsConfiguration.class})
public class AbstractEntityRestControllerGetTests {

	@MockBean
	private EntityService<SimpleTestEntity, UUID> service;
	
	@Autowired
	private MockMvc mvc;
	
	EnhancedRandom random = EnhancedRandomBuilder.aNewEnhancedRandom();
	
	private final static UUID NIL_UUID = new UUID(0,0);
	
	private static String url(String path) {
		return SimpleTestEntityRestController.PATH + path;
	}
	
	@Before
	public void before() {
		when(service.get(any())).thenAnswer((c) -> {
			UUID arg = c.getArgument(0);
			
			if(NIL_UUID.equals(arg)) return null;
			
			SimpleTestEntity e = random.nextObject(SimpleTestEntity.class);
			e.setId( c.getArgument(0) );
			return e;
		});
	}
	
	@Test
	public void get_resource_with_bad_uuid() throws Exception {
		mvc
		.perform(get(url("/abcd")))
		.andExpect(status().isBadRequest())
		.andDo(print());
	}
	
	@Test
	public void get_existing_resource() throws Exception {
		mvc
		.perform(get(url("/" + UUID.randomUUID())))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.id").isNotEmpty())
		.andExpect(jsonPath("$.version").isNotEmpty())
		.andExpect(jsonPath("$.name").isNotEmpty())
		.andDo(print());
	}
	
	@Test
	public void get_nonexisting_resource() throws Exception {
		mvc
		.perform(get(url("/" + NIL_UUID)))
		.andExpect(status().isNotFound())
		.andDo(print());
	}
}
