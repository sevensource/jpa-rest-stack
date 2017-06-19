package org.sevensource.support.jpa.liquibase;

import javax.sql.DataSource;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;


public abstract class AbstractLiquibaseRunner<T extends InitializingBean & ResourceLoaderAware> implements LiquibaseRunner {
	
	private String beanName;
	private ResourceLoader resourceLoader;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		update();
	}
	
	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}
	
	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
	
	protected abstract T createInstance();
	protected abstract DataSource getDataSource();
	
	@Override
	public void update() throws Exception {
		if(getDataSource() == null) {
			throw new IllegalStateException("dataSource cannot be null");
		}
		
		T liquibase = createInstance();
		if(liquibase instanceof BeanNameAware) {
			((BeanNameAware)liquibase).setBeanName(beanName);
		}
		liquibase.setResourceLoader(resourceLoader);
		liquibase.afterPropertiesSet();
	}
}