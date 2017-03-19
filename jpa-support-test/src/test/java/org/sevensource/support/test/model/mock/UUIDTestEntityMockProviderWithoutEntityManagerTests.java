package org.sevensource.support.test.model.mock;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sevensource.support.test.jpa.configuration.MockFactoryConfiguration;
import org.sevensource.support.test.jpa.model.mock.MockFactory;
import org.sevensource.support.test.model.UUIDTestEntity;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {MockFactoryConfiguration.class})
@ComponentScan(basePackageClasses={UUIDTestEntityMockProvider.class})
public class UUIDTestEntityMockProviderWithoutEntityManagerTests {
	
	@Test
	public void create_mock() {
		UUIDTestEntity e = MockFactory.on(UUIDTestEntity.class).create();
		assertThat(e.getId()).isNotNull();
	}
	
	@Test
	public void create_many_mock() {
		List<UUIDTestEntity> e = MockFactory.on(UUIDTestEntity.class).create(10);
		assertThat(e).hasSize(10);
		for(int i=0; i<10; i++) {
			assertThat(e.get(i).getId()).isNotNull();	
		}
	}
	
	@Test
	public void populate_mock() {
		UUIDTestEntity e = MockFactory.on(UUIDTestEntity.class).populate();
		assertThat(e).isNotNull();
	}
	
	@Test
	public void touch_mock() {
		UUIDTestEntity e = MockFactory.on(UUIDTestEntity.class).create();
		String t = e.getTitle();
		assertThat(t).isNotNull();
		
	    MockFactory.on(UUIDTestEntity.class).touch(e);
	    assertThat(e.getTitle()).isNotNull();
	    assertThat(e.getTitle()).isNotEqualTo(t);
	}
	
	@Test
	public void create_mock_check_reference() {
		UUIDTestEntity e = MockFactory.on(UUIDTestEntity.class).create();
		assertThat(e.getRef()).isNotNull();
	}
}
