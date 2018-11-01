package org.sevensource.support.test.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.sevensource.support.jpa.domain.PersistentEntity;
import org.sevensource.support.jpa.service.EntityService;
import org.sevensource.support.test.jpa.domain.mock.MockFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public abstract class EntityRestControllerTestSupport<E extends PersistentEntity<ID>, ID extends Serializable> {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private MockFactory mockFactory;

	@Captor
	private ArgumentCaptor<E> entityCaptor;

	@Captor
	private ArgumentCaptor<ID> idCaptor;

	private ObjectMapper mapper = new ObjectMapper();


	@Before
	public void before() {
		when(getService().get(idCaptor.capture())).thenAnswer(c -> {
			if(idCaptor.getValue().equals(nillId())) {
				return null;
			}
			E entity = mockFactory.on(getEntityClass()).create();
			entity.setId(idCaptor.getValue());
			return entity;
		});

		when(getService().create(entityCaptor.capture())).thenAnswer(c -> {
			E entity = mockFactory.on(getEntityClass()).create();
			entity.setId(nextId());
			return entity;
		});

		when(getService().create(idCaptor.capture(), entityCaptor.capture())).thenAnswer(c -> {
			E entity = mockFactory.on(getEntityClass()).create();
			entity.setId(idCaptor.getValue());
			return entity;
		});

		when(getService().update(idCaptor.capture(), entityCaptor.capture())).thenAnswer(c -> {
			E e = entityCaptor.getValue();
			e.setId(idCaptor.getValue());
			return e;
		});

		when(getService().exists(idCaptor.capture())).thenAnswer(c -> {
			return ! nillId().equals(idCaptor.getValue());
		});

		final List<E> objects = mockFactory.on(getEntityClass()).create(10);

		when(getService().findAll(any(Sort.class))).thenReturn(objects);

		when(getService().findAll(any(PageRequest.class))).thenAnswer(c -> {
			Pageable pageable = c.getArgument(0);
			int page = pageable.getPageNumber();
			int size = pageable.getPageSize();

			int start = (page) * size;
			int end = ((page+1) * size);

			if( start + 1 > objects.size()) {
				return null;
			}
			if( end > objects.size() ) {
				end = objects.size();
			}

			Sort sort = pageable.getSort();
			if(sort != null && sort.getOrderFor("id") != null) {
				if(sort.getOrderFor("id").isAscending()) {
					objects.get(0).setId(nillId());
				} else {
					objects.get(0).setId(nextId());
				}
			}

			return new PageImpl<>(objects.subList(start, end), c.getArgument(0), objects.size());
		});
	}

	protected abstract String getRootPath();
	protected abstract ID nextId();
	protected abstract ID nillId();
	protected abstract Serializable invalidId();
	protected abstract Class<E> getEntityClass();
	protected abstract EntityService<E, ID> getService();


	private MockHttpServletRequestBuilder request(String url, HttpMethod method) {
		URI uri = URI.create(getRootPath() + url);
		return MockMvcRequestBuilders.request(method, uri)
			.contentType(MediaType.APPLICATION_JSON_UTF8);
	}

	@Test
	public void get_resource_with_bad_id() throws Exception {
		mvc
		.perform(request("/" + invalidId(), HttpMethod.GET))
		.andExpect(status().isBadRequest())
		.andDo(print());
	}

	@Test
	public void get_existing_resource() throws Exception {
		mvc
		.perform(request("/" + nextId(), HttpMethod.GET))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.id").isNotEmpty())
		.andExpect(jsonPath("$.version").isNotEmpty())
		.andDo(print());
	}

	@Test
	public void get_nonexisting_resource() throws Exception {
		mvc
		.perform(request("/" + nillId(), HttpMethod.GET))
		.andExpect(status().isNotFound())
		.andDo(print());
	}


	@Test
	public void delete_resource_works() throws Exception {
		ID assignedId = nextId();

		mvc
			.perform(request("/" + assignedId, HttpMethod.DELETE))
			.andExpect(status().isNoContent())
			.andDo(print())
			.andReturn();

		verify(getService(), times(1)).delete(assignedId);
	}

	@Test
	public void delete_non_existing_resource_returns_404() throws Exception {
		mvc
			.perform(request("/" + nillId(), HttpMethod.DELETE))
			.andExpect(status().isNotFound())
			.andDo(print())
			.andReturn();
	}

	@Test
	public void post_resource_works() throws Exception {
		String json = "{ }";

		MvcResult result = mvc
			.perform(request("/", HttpMethod.POST).content(json))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(header().string(HttpHeaders.LOCATION, not(isEmptyOrNullString())))
			.andExpect(jsonPath("$.id").isNotEmpty())
			.andDo(print())
			.andReturn();

		final ObjectReader reader = mapper.readerFor(Map.class);
		final Map<String, Object> map = reader.readValue(result.getResponse().getContentAsByteArray());
		final Serializable id = (Serializable) map.get("id");

		String link = result.getResponse().getHeader(HttpHeaders.LOCATION);
		assertThat(link).endsWith(getRootPath() + "/" + id);
	}

	@Test
	public void put_new_resource_works() throws Exception {
		String json = "{ }";

		ID requestedId = nillId();

		mvc
				.perform(request("/" + requestedId, HttpMethod.PUT).content(json))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.id").value(requestedId.toString()))
				.andDo(print())
				.andReturn();
	}

	@Test
	public void put_existing_resource_works() throws Exception {
		ID requestedId = nextId();

		String json = "{\"id\": \"" + requestedId.toString() + "\"}";

		mvc
				.perform(request("/" + requestedId, HttpMethod.PUT).content(json))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.id").value(requestedId.toString()))
				.andDo(print())
				.andReturn();
	}

	@Test
	public void put_new_resource_with_different_id() throws Exception {
		String json = "{"
				+ "\"id\":	\"" + nillId().toString() + "\""
				+ "}";

		ID requestedId = nextId();

		mvc
				.perform(request("/" + requestedId, HttpMethod.PUT).content(json))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.id").value(requestedId.toString()))
				.andDo(print())
				.andReturn();
	}

	@Test
	public void get_collection_resource_without_parameter() throws Exception {
		mvc
				.perform(request("/", HttpMethod.GET))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(mvcResult -> {
					boolean paginatedMatched = false;

					try {
						jsonPath("$.data").exists();
						paginatedMatched = true;
					} catch(Exception e) {
						// do nothing
					}

					if(paginatedMatched) {
						jsonPath("$.data", hasSize(10)).match(mvcResult);
						jsonPath("$.page").exists().match(mvcResult);
					} else {
						jsonPath("$", hasSize(10)).match(mvcResult);
					}
				})
				.andDo(print())
				.andReturn();
	}

	@Test
	public void get_collection_resource_nonexistant_page() throws Exception {
//		mvc
//				.perform(request("/?page=100&size=4", HttpMethod.GET))
//				.andExpect(status().isOk())
//				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
//				.andExpect(jsonPath("$.data").isEmpty())
//				.andExpect(jsonPath("$", hasKey("page")))
//				.andDo(print())
//				.andReturn();
		mvc
		.perform(request("/?page=100&size=4", HttpMethod.GET))
		.andExpect(status().isNotFound())
		.andDo(print())
		.andReturn();
	}

	@Test
	public void get_collection_resource_first_page() throws Exception {
		mvc
				.perform(request("/?page=0&size=4", HttpMethod.GET))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.data", hasSize(4)))
				.andExpect(jsonPath("$", hasKey("page")))
				.andDo(print())
				.andReturn();
	}

	@Test
	public void get_collection_resource_first_page_with_sort_asc() throws Exception {
		mvc
				.perform(request("/?page=0&size=4&sort=id,ASC", HttpMethod.GET))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$", hasKey("page")))
				.andExpect(jsonPath("$.data", hasSize(4)))
				.andExpect(jsonPath("$.data[0].id").value(nillId().toString()))
				.andDo(print())
				.andReturn();
	}

	@Test
	public void get_collection_resource_first_page_with_sort_desc() throws Exception {
		mvc
				.perform(request("/?page=0&size=4&sort=id,DESC", HttpMethod.GET))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$", hasKey("page")))
				.andExpect(jsonPath("$.data", hasSize(4)))
				.andExpect(jsonPath("$.data[0].id").isNotEmpty())
				.andExpect(jsonPath("$.data[0].id").value(not(nillId().toString())))
				.andDo(print())
				.andReturn();
	}

}
