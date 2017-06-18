package org.sevensource.support.test.rest.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Test;
import org.sevensource.support.jpa.domain.PersistentEntity;
import org.sevensource.support.rest.dto.IdentifiableDTO;
import org.sevensource.support.rest.mapping.EntityMapper;
import org.sevensource.support.test.jpa.domain.mock.MockFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractEntityMapperTestSupport<E extends PersistentEntity<?>,D extends IdentifiableDTO<?>> {
	
	protected static final String[] EXCLUDE_FIELDS = new String[] {
			"version", "lastModifiedDate", "lastModifiedBy", "createdBy", "createdDate"
	};
	
	@Autowired
	MockFactory<?> mockFactory;
	
	@Autowired
	EntityMapper<E,D> entityMapper;
	
	Class<E> entityClass;
	
	public AbstractEntityMapperTestSupport(Class<E> entityClass) {
		this.entityClass = entityClass;
	}
	
	protected String[] getExcludeFields() {
		return EXCLUDE_FIELDS;
	}
	
	protected E populate() {
		return mockFactory.on(entityClass).create();
	}
	
	protected EntityMapper<E, D> mapper() {
		return entityMapper;
	}
	
	@Test
	public void reflectiveEquality() {
		
		E entity = populate();
		
		D dto = entityMapper.toDTO(entity);
		E entity2 = entityMapper.toEntity(dto);
		
		boolean result = EqualsBuilder
			.reflectionEquals(entity, entity2, false, null, getExcludeFields());
		
		assertThat(result).isTrue();
	}
}
