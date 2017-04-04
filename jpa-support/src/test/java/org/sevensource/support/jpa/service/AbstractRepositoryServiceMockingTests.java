package org.sevensource.support.jpa.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.sevensource.support.jpa.exception.EntityException;
import org.sevensource.support.jpa.model.SimpleEntity;
import org.sevensource.support.jpa.service.AbstractRepositoryService;
import org.springframework.data.jpa.repository.JpaRepository;

public class AbstractRepositoryServiceMockingTests {

	@Mock
	JpaRepository<SimpleEntity, UUID> repository;
	
	@Mock
	Validator validator;
	
	SimpleRepositoryService service;
	
	@Before
	public void setup() {
		service = new SimpleRepositoryService(repository, validator);
	}
	
	class SimpleRepositoryService extends AbstractRepositoryService<SimpleEntity> {

		public SimpleRepositoryService(JpaRepository<SimpleEntity, UUID> repository, Validator validator) {
			super(repository, validator, SimpleEntity.class);
		}

		@Override
		protected void validateConstraints(SimpleEntity entity) throws EntityException {
			return;
		}
		
	}
	
	@Test
	public void supports_works() {
		assertThat(service.supports(SimpleEntity.class)).isTrue();
		assertThat(service.supports(Object.class)).isFalse();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void get_does_not_allow_null() {
		service.get(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void exists_does_not_allow_null() {
		service.exists(null);
	}
	
	
}
