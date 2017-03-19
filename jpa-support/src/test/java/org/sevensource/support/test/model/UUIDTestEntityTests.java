package org.sevensource.support.test.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sevensource.support.jdbc.DataSourceAssertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sevensource.support.jdbc.configuration.ProxyDatasourceConfiguration;
import org.sevensource.support.jpa.configuration.JpaAuditingTestConfiguration;
import org.sevensource.support.jpa.model.AbstractUUIDEntityTestSupport;
import org.sevensource.support.test.configuration.MockConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;
import net.ttddyy.dsproxy.support.ProxyDataSource;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = {JpaAuditingTestConfiguration.class, MockConfiguration.class, ProxyDatasourceConfiguration.class})
public class UUIDTestEntityTests extends AbstractUUIDEntityTestSupport<UUIDTestEntity> {

	@PersistenceContext
	EntityManager em;
	
	@Autowired
	DataSource ds;
	
	public UUIDTestEntityTests() {
		super(UUIDTestEntity.class);
	}
	
	@SuppressWarnings("unused")
	@Test
	public void x() throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		Constructor c = UUIDTestEntity.class.getDeclaredConstructor();
		if(! c.isAccessible())
			c.setAccessible(true);
		c.newInstance();
	}
	
	private void y(Class clazz) throws InstantiationException, IllegalAccessException {
		Object o = clazz.newInstance();
		o.equals(null);
	}
	
	private QueryCount getQueryCount() {
		if(! (ds instanceof ProxyDataSource)) {
			throw new IllegalArgumentException();
		}
		return QueryCountHolder.get( ((ProxyDataSource) ds).getDataSourceName());
	}
	
	@Before
	public void beforeEach() {
		QueryCountHolder.clear();
	}

	@Test
	public void test_persist_with_reference() {
		
		UUIDTestEntity e = new UUIDTestEntity("Hello World 1");
		UUIDTestReferenceEntity r = new UUIDTestReferenceEntity();
		em.persist(r);
		e.setRef(r);
		em.persist(e);
		em.flush();
		
		assertThat(insertCount()).isEqualTo(2);
		
		
		em.detach(r);
		em.detach(e);
		e = em.find(e.getClass(), e.getId());
		assertThat(e.getId()).isNotNull();
		assertThat(e.getRef()).isNotNull();
		assertThat(e.getRef().getId()).isNotNull();
		assertThat(e.getRef()).isEqualTo(r);
		
		assertThat(selectCount()).isEqualTo(1);
		assertThat(updateCount()).isEqualTo(0);
	}
}
