package org.sevensource.support.test.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sevensource.support.DataProviderRunnerWithSpring;
import org.sevensource.support.jpa.configuration.JpaAuditingTestConfiguration;
import org.sevensource.support.jpa.exception.EntityValidationException;
import org.sevensource.support.jpa.service.AbstractEntityServiceTests;
import org.sevensource.support.test.configuration.MockConfiguration;
import org.sevensource.support.test.model.UUIDTestEntity;
import org.sevensource.support.test.model.UUIDTestReferenceEntity;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunnerWithSpring.class)
@DataJpaTest
@Import({ValidationAutoConfiguration.class})
@ContextConfiguration(classes = {MockConfiguration.class, JpaAuditingTestConfiguration.class})
@EntityScan(basePackageClasses=UUIDTestEntity.class)
@EnableJpaRepositories(basePackageClasses=UUIDTestEntityRepository.class)
@ComponentScan(basePackageClasses={UUIDTestEntityService.class})
public class UUIDTestEntityServiceTests extends AbstractEntityServiceTests<UUIDTestEntity> {

	private final static String UNIQUE_STRING = "A UNIQUE STRING";
	
	@Override
	protected List<UUIDTestEntity> getEntitiesForBusinessValidation() {
		UUIDTestEntity e = populate();
		e.setTitle(UNIQUE_STRING);
		return Arrays.asList(e);
	}
	
	@Override
	protected List<Class<?>> getEntityClassesForDeletion() {
		return Arrays.asList(UUIDTestEntity.class, UUIDTestReferenceEntity.class);
	}
	
	@DataProvider(trimValues=false)
	public static String[] VALID_NAMES() {
		return new String[] { "webSite", "web-sIte", "web:service"};
    }
	
	@DataProvider(trimValues=false)
	public static String[] INVALID_NAMES() {
		return new String[] { "null", "", "a"};
    }
	
	
	public UUIDTestEntityServiceTests() {
		super(UUIDTestEntity.class);
	}
	
	/* **************************************************************** */
	
	@Override
	public void create_with_business_violation() {
		UUIDTestEntity e = createEntity();
		UUIDTestEntity e1 = populate();
		e1.setTitle(e.getTitle());
		e1 = getService().create(e1);
	}

	@Override
	public void create_withId_with_business_violation() {
		UUIDTestEntity e1 = populate();
		e1.setTitle(UNIQUE_STRING);
		e1 = getService().create(UUID.randomUUID(), e1);
	}

	@Override
	public void update_with_business_violation() {		
		UUIDTestEntity e1 = createEntity();
		e1.setTitle(UNIQUE_STRING);
		e1 = getService().update(e1.getId(), e1);
	}
	
	
	// CREATE
	@Test(expected=EntityValidationException.class)
	@UseDataProvider("INVALID_NAMES")
	public void createWithInvalidName(String name) {
		UUIDTestEntity c = populate();
		c.setTitle(name);
		getService().create(c);
	}
	
	@Test
	@UseDataProvider("VALID_NAMES")
	public void createWithValidName(String name) {
		UUIDTestEntity c = populate();
		c.setTitle(name);
		getService().create(c);
	}
	
	// CREATE WITH ID
	@Test(expected=EntityValidationException.class)
	@UseDataProvider("INVALID_NAMES")
	public void createWithIdWithInvalidName(String name) {
		UUIDTestEntity c = populate();
		c.setTitle(name);
		getService().create(UUID.randomUUID(), c);
	}
	
	@Test()
	@UseDataProvider("VALID_NAMES")
	public void createWithIdWithValidName(String name) {
		UUIDTestEntity c = populate();
		c.setTitle(name);
		getService().create(UUID.randomUUID(), c);
	}
	
	/// UPDATE
	@Test(expected=EntityValidationException.class)
	@UseDataProvider("INVALID_NAMES")
	public void updateWithInvalidName(String name) {
		UUIDTestEntity c = createEntity();
		c.setTitle(name);
		getService().update(c.getId(), c);
	}
}
