package org.sevensource.support.rest.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sevensource.support.jpa.service.EntityService;
import org.sevensource.support.rest.model.SimpleTestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers=SimpleTestEntityRestController.class)
@ContextConfiguration(classes={AbstractEntityControllerTestsConfiguration.class})
public class AbstractEntityRestControllerFindTests {

	@MockBean
	private EntityService<SimpleTestEntity, UUID> service;
	
	@Autowired
	private MockMvc mvc;
	
	private EnhancedRandom random = EnhancedRandomBuilder.aNewEnhancedRandom();
	
	private final static UUID ONE_UUID = new UUID(0,1);
	private final static UUID TWO_UUID = new UUID(0,2);
	
	
	private static String url(String path) {
		return SimpleTestEntityRestController.PATH + path;
	}
	
	@Before
	public void before() {
		final List<SimpleTestEntity> objects = IntStream
				.range(0, 10)
				.mapToObj(i -> random.nextObject(SimpleTestEntity.class))
				.collect(Collectors.toList());
		
		when(service.findAll((Pageable) any())).thenAnswer((c) -> {
			Pageable pageable = c.getArgument(0);
			int page = pageable.getPageNumber();
			int size = pageable.getPageSize();
			
			int start = (page) * size;
			int end = ((page+1) * size);
			
			if( start + 1 > objects.size()) return null;
			if( end > objects.size() ) end = objects.size();
			
			Sort sort = pageable.getSort();
			if(sort != null && sort.getOrderFor("name") != null) {
				if(sort.getOrderFor("name").isAscending()) objects.get(0).setId(ONE_UUID);
				else objects.get(0).setId(TWO_UUID);
			}
			
			return new PageImpl<>(objects.subList(start, end), c.getArgument(0), objects.size());
		});
		
		when(service.findAll((Sort) any())).thenReturn(objects);
	}

	
	@Test
	public void get_collection_resource_without_parameter() throws Exception {
		mvc
		.perform(get(url("/")))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$", hasSize(10)))
		.andDo(print());
	}
	
	@Test
	public void get_collection_resource_nonexistant_page() throws Exception {
		mvc
		.perform(get(url("/?page=100&size=4")))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$").isEmpty())
		.andDo(print());
	}
	
	@Test
	public void get_collection_resource_first_page() throws Exception {
		mvc
		.perform(get(url("/?page=0&size=4")))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$", hasSize(4)))
		.andDo(print());
	}
	
	@Test
	public void get_collection_resource_first_page_with_sort_asc() throws Exception {
		mvc
		.perform(get(url("/?page=0&size=4&sort=name,ASC")))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$", hasSize(4)))
		.andExpect(jsonPath("$[0].id").value(ONE_UUID.toString()))
		.andDo(print());
	}
	
	@Test
	public void get_collection_resource_first_page_with_sort_desc() throws Exception {
		mvc
		.perform(get(url("/?page=0&size=4&sort=name,DESC")))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$", hasSize(4)))
		.andExpect(jsonPath("$[0].id").value(TWO_UUID.toString()))
		.andDo(print());
	}
	
}
