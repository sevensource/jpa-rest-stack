package org.sevensource.support.test.jpa.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sevensource.support.test.DataProviderRunnerWithSpring;
import org.sevensource.support.test.jpa.configuration.JpaSupportTestConfiguration;
import org.sevensource.support.test.jpa.domain.UUIDTestEntity;
import org.sevensource.support.test.jpa.domain.mock.UUIDTestEntityMockProvider;
import org.sevensource.support.test.jpa.service.AbstractEntityServiceTests;
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
@ContextConfiguration(classes = {JpaSupportTestConfiguration.class})
@EntityScan(basePackageClasses=UUIDTestEntity.class)
@EnableJpaRepositories(basePackageClasses=UUIDTestEntityRepository.class)
@ComponentScan(basePackageClasses={UUIDTestEntityService.class, UUIDTestEntityMockProvider.class})
public class UUIDTestEntityServiceTest extends AbstractEntityServiceTests<UUIDTestEntity> {

	private final static String UNIQUE_TITLE = "A UNIQUE STRING";
	private final static String[] INVALID_TITLE = new String[] {null, "", "a", UNIQUE_TITLE};
	
	public UUIDTestEntityServiceTest() {
		super(UUIDTestEntity.class);
	}
	
	@Override
	protected List<UUIDTestEntity> getEntitesToPersistBeforeTransaction() {
		UUIDTestEntity e = populate();
		e.setTitle(UNIQUE_TITLE);
		return Arrays.asList(e);
	}
	
	@Override
	protected List<UUIDTestEntity> getEntitiesWithValidationViolations() {
		List<UUIDTestEntity> invalid = new ArrayList<>();
		for(String name : INVALID_TITLE) {
			UUIDTestEntity e = populate();
			e.setTitle(name);
			invalid.add(e);
		}
		return invalid;
	}
	
	@DataProvider(trimValues=false)
	public static String[] VALID_NAMES() {
		return new String[] { "webSite", "web-sIte", "web:service"};
    }
	
	
	/* **************************************************************** */
	@Test
	@UseDataProvider("VALID_NAMES")
	public void createWithValidName(String name) {
		UUIDTestEntity c = populate();
		c.setTitle(name);
		getService().create(c);
		getEntityManager().flush();
	}
}
