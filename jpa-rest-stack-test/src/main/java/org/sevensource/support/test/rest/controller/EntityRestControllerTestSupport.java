package org.sevensource.support.test.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
	
	private Map<ID, E> entityMap = new HashMap<>();


	@Before
	public void before() {
		when(getService().get(idCaptor.capture())).thenAnswer(c ->
			entityMap.computeIfAbsent(idCaptor.getValue(), (id) -> {
				if(id.equals(nillId())) return null;
				E entity = mockFactory.on(getEntityClass()).create();
				entity.setId(id);
				return entity;
			})
		);

		when(getService().create(entityCaptor.capture())).thenAnswer(c -> {
			E entity = mockFactory.on(getEntityClass()).create();
			entity.setId(nextId());
			entityMap.put(entity.getId(), entity);
			return entity;
		});

		when(getService().create(idCaptor.capture(), entityCaptor.capture())).thenAnswer(c -> {
			E entity = mockFactory.on(getEntityClass()).create();
			entity.setId(idCaptor.getValue());
			entityMap.put(entity.getId(), entity);
			return entity;
		});

		when(getService().update(idCaptor.capture(), entityCaptor.capture())).thenAnswer(c -> {
			E e = entityCaptor.getValue();
			e.setId(idCaptor.getValue());
			entityMap.put(e.getId(), e);
			return e;
		});

		when(getService().exists(idCaptor.capture())).thenAnswer(c -> 
			! nillId().equals(idCaptor.getValue())
		);

		final List<E> objects = mockFactory.on(getEntityClass()).create(10);

		when(getService().findAll(isNull(), any(Sort.class))).thenReturn(objects);

		when(getService().findAll(isNull(), any(PageRequest.class))).thenAnswer(c -> {
			Pageable pageable = c.getArgument(1);
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

			return new PageImpl<>(objects.subList(start, end), c.getArgument(1), objects.size());
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
			.andExpect(status().isBadRequest());
	}

	@Test
	public void get_existing_resource() throws Exception {
		mvc
			.perform(request("/" + nextId(), HttpMethod.GET))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(header().exists(HttpHeaders.ETAG))
			.andExpect(header().string(HttpHeaders.ETAG, not(isEmptyOrNullString())))
			.andExpect(header().exists(HttpHeaders.LAST_MODIFIED))
			.andExpect(header().string(HttpHeaders.LAST_MODIFIED, not(isEmptyOrNullString())))
			.andExpect(jsonPath("$.id").isNotEmpty())
			.andExpect(jsonPath("$.version").isNotEmpty());
	}
	
	@Test
	public void get_existing_resource_returns_not_modified_with_matching_etag() throws Exception {
		ID id = nextId();
		
		MvcResult result = mvc
			.perform(request("/" + id, HttpMethod.GET))
			.andExpect(status().isOk())
			.andExpect(header().exists(HttpHeaders.ETAG))
			.andExpect(header().string(HttpHeaders.ETAG, not(isEmptyOrNullString())))
			.andReturn();
		
		final String etag = result.getResponse().getHeader(HttpHeaders.ETAG);
		mvc
			.perform(request("/" + id, HttpMethod.GET).header(HttpHeaders.IF_NONE_MATCH, etag))
			.andExpect(status().isNotModified());
	}
	
	@Test
	public void get_existing_resource_returns_not_modified_with_matching_last_modified() throws Exception {
		ID id = nextId();
		
		MvcResult result = mvc
			.perform(request("/" + id, HttpMethod.GET))
			.andExpect(status().isOk())
			.andExpect(header().exists(HttpHeaders.LAST_MODIFIED))
			.andExpect(header().string(HttpHeaders.LAST_MODIFIED, not(isEmptyOrNullString())))
			.andReturn();
		
		final String lastModified = result.getResponse().getHeader(HttpHeaders.LAST_MODIFIED);
		mvc
			.perform(request("/" + id, HttpMethod.GET).header(HttpHeaders.IF_MODIFIED_SINCE, lastModified))
			.andExpect(status().isNotModified());
	}
	
	@Test
	public void get_existing_resource_returns_ok_with_unmatched_etag() throws Exception {
		ID id = nextId();
		
		mvc
			.perform(request("/" + id, HttpMethod.GET))
			.andExpect(status().isOk())
			.andExpect(header().exists(HttpHeaders.ETAG))
			.andExpect(header().string(HttpHeaders.ETAG, not(isEmptyOrNullString())));

		mvc
			.perform(request("/" + id, HttpMethod.GET).header(HttpHeaders.IF_NONE_MATCH, "\"" + UUID.randomUUID() + "\""))
			.andExpect(status().isOk());
	}
	
	@Test
	public void get_existing_resource_returns_ok_with_unmatched_last_modified() throws Exception {
		ID id = nextId();
		
		mvc
			.perform(request("/" + id, HttpMethod.GET))
			.andExpect(status().isOk())
			.andExpect(header().exists(HttpHeaders.LAST_MODIFIED))
			.andExpect(header().string(HttpHeaders.LAST_MODIFIED, not(isEmptyOrNullString())));

		mvc
			.perform(request("/" + id, HttpMethod.GET).header(HttpHeaders.IF_MODIFIED_SINCE, Long.MIN_VALUE))
			.andExpect(status().isOk());
	}
	
	@Test
	public void get_existing_resource_returns_not_modified_with_future_last_modified() throws Exception {
		ID id = nextId();
		
		mvc
			.perform(request("/" + id, HttpMethod.GET))
			.andExpect(status().isOk())
			.andExpect(header().exists(HttpHeaders.LAST_MODIFIED))
			.andExpect(header().string(HttpHeaders.LAST_MODIFIED, not(isEmptyOrNullString())));

		mvc
			.perform(request("/" + id, HttpMethod.GET).header(HttpHeaders.IF_MODIFIED_SINCE, Long.MAX_VALUE))
			.andExpect(status().isNotModified());
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
			.andExpect(header().string(HttpHeaders.ETAG, not(isEmptyOrNullString())))
			.andExpect(header().string(HttpHeaders.LAST_MODIFIED, not(isEmptyOrNullString())))
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
			.andExpect(header().string(HttpHeaders.ETAG, not(isEmptyOrNullString())))
			.andExpect(header().string(HttpHeaders.LAST_MODIFIED, not(isEmptyOrNullString())))
			.andExpect(jsonPath("$.id").isNotEmpty())
			.andExpect(jsonPath("$.id").value(requestedId.toString()))
			.andDo(print())
			.andReturn();
	}
	
	@Test
	public void put_existing_resource_with_matched_etag_works() throws Exception {
		ID requestedId = nextId();
		
		MvcResult result = mvc
			.perform(request("/" + requestedId, HttpMethod.GET))
			.andExpect(status().isOk())
			.andExpect(header().exists(HttpHeaders.ETAG))
			.andExpect(header().string(HttpHeaders.ETAG, not(isEmptyOrNullString())))
			.andReturn();
		
		final String etag = result.getResponse().getHeader(HttpHeaders.ETAG);

		String json = "{\"id\": \"" + requestedId.toString() + "\"}";

		mvc
				.perform(request("/" + requestedId, HttpMethod.PUT)
						.header(HttpHeaders.IF_MATCH, etag)
						.content(json))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(header().string(HttpHeaders.ETAG, not(isEmptyOrNullString())))
				.andExpect(header().string(HttpHeaders.LAST_MODIFIED, not(isEmptyOrNullString())))
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.id").value(requestedId.toString()))
				.andDo(print())
				.andReturn();
	}
	
	@Test
	public void put_existing_resource_with_unmatched_etag_fails() throws Exception {
		ID requestedId = nextId();
		
		mvc
			.perform(request("/" + requestedId, HttpMethod.GET))
			.andExpect(status().isOk())
			.andExpect(header().exists(HttpHeaders.ETAG))
			.andExpect(header().string(HttpHeaders.ETAG, not(isEmptyOrNullString())))
			.andReturn();

		String json = "{\"id\": \"" + requestedId.toString() + "\"}";

		mvc
			.perform(request("/" + requestedId, HttpMethod.PUT)
					.header(HttpHeaders.IF_MATCH, "\"" + UUID.randomUUID() + "\"")
					.content(json))
			.andExpect(status().isPreconditionFailed())
			.andExpect(header().string(HttpHeaders.ETAG, not(isEmptyOrNullString())))
			.andDo(print());
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
		mvc
			.perform(request("/?page=100&size=4", HttpMethod.GET))
			.andExpect(status().isNotFound());
	}

	@Test
	public void get_collection_resource_first_page() throws Exception {
		mvc
				.perform(request("/?page=0&size=4", HttpMethod.GET))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.data", hasSize(4)))
				.andExpect(jsonPath("$", hasKey("page")));
	}

	@Test
	public void get_collection_resource_first_page_with_sort_asc() throws Exception {
		mvc
				.perform(request("/?page=0&size=4&sort=id,ASC", HttpMethod.GET))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$", hasKey("page")))
				.andExpect(jsonPath("$.data", hasSize(4)))
				.andExpect(jsonPath("$.data[0].id").value(nillId().toString()));
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
				.andExpect(jsonPath("$.data[0].id").value(not(nillId().toString())));
	}

}
