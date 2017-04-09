package org.sevensource.support.test.jdbc.configuration;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.sevensource.support.test.jdbc.SimpleQueryLogEntryCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener;
import net.ttddyy.dsproxy.support.ProxyDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

@Configuration
public class ProxyDatasourceConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(ProxyDatasourceConfiguration.class);

	
	static ProxyDataSource getProxyDataSource(DataSource ds, String name) {
			SLF4JQueryLoggingListener loggingListener = new SLF4JQueryLoggingListener();
			loggingListener.setLogLevel(SLF4JLogLevel.INFO);
			loggingListener.setLogger("DS");
			loggingListener.setQueryLogEntryCreator(new SimpleQueryLogEntryCreator(false));
			
			return ProxyDataSourceBuilder
					.create(name, ds)
					.multiline()
					.listener(loggingListener)
					.countQuery()
					.build();
	}

	@Bean
	public static BeanPostProcessor proxyDataSourceBeanPostProcessor() {
		return new BeanPostProcessor() {

			@Override
			public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
				return bean;
			}

			@Override
			public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
				if (bean instanceof DataSource && !(bean instanceof FactoryBean)
						&& !(bean instanceof ProxyDataSource)) {
					ProxyDataSource pds = getProxyDataSource((DataSource) bean, beanName);
					logger.warn("Replacing bean '{}' with a different bean: replacing [{}] with [{}]", beanName,
							bean.getClass().getName(), pds.getClass().getName());
					return pds;
				} else {
					return bean;
				}
			}
		};
	}
}
