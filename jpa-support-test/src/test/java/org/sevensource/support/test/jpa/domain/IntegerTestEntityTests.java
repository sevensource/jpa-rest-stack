package org.sevensource.support.test.jpa.domain;

import org.junit.runner.RunWith;
import org.sevensource.support.test.jpa.configuration.JpaSupportTestConfiguration;
import org.sevensource.support.test.jpa.domain.AbstractIntegerEntityTestSupport;
import org.sevensource.support.test.jpa.domain.mock.IntegerTestEntityMockProvider;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = {JpaSupportTestConfiguration.class})
@ComponentScan(basePackageClasses={IntegerTestEntityMockProvider.class})
public class IntegerTestEntityTests extends AbstractIntegerEntityTestSupport<IntegerTestEntity> {

	public IntegerTestEntityTests() {
		super(IntegerTestEntity.class);
	}
}