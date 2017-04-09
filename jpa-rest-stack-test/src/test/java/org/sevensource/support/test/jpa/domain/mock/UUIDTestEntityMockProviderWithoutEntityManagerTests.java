package org.sevensource.support.test.jpa.domain.mock;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sevensource.support.test.jpa.configuration.MockFactoryConfiguration;
import org.sevensource.support.test.jpa.domain.UUIDTestEntity;
import org.sevensource.support.test.jpa.domain.mock.MockFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {MockFactoryConfiguration.class})
@ComponentScan(basePackageClasses={UUIDTestEntityMockProvider.class})
@Import(UUIDTestEntityMockProvider.class)
public class UUIDTestEntityMockProviderWithoutEntityManagerTests {
	
	@Autowired
	MockFactory<?> mockFactory;
	
	@Test
	public void create_mock() {
		UUIDTestEntity e = mockFactory.on(UUIDTestEntity.class).create();
		assertThat(e.getId()).isNotNull();
	}
	
	@Test
	public void create_many_mock() {
		List<UUIDTestEntity> e = mockFactory.on(UUIDTestEntity.class).create(10);
		assertThat(e).hasSize(10);
		for(int i=0; i<10; i++) {
			assertThat(e.get(i).getId()).isNotNull();	
		}
	}
	
	@Test
	public void populate_mock() {
		UUIDTestEntity e = mockFactory.on(UUIDTestEntity.class).populate();
		assertThat(e).isNotNull();
	}
	
	@Test
	public void touch_mock() {
		UUIDTestEntity e = mockFactory.on(UUIDTestEntity.class).create();
		String t = e.getTitle();
		assertThat(t).isNotNull();
		
	    mockFactory.on(UUIDTestEntity.class).touch(e);
	    assertThat(e.getTitle()).isNotNull();
	    assertThat(e.getTitle()).isNotEqualTo(t);
	}
	
	@Test
	public void create_mock_check_reference() {
		UUIDTestEntity e = mockFactory.on(UUIDTestEntity.class).create();
		assertThat(e.getRef()).isNotNull();
	}
}
