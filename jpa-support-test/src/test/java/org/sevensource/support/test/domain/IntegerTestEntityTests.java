package org.sevensource.support.test.domain;

import org.junit.runner.RunWith;
import org.sevensource.support.test.configuration.JpaSupportTestConfiguration;
import org.sevensource.support.test.domain.mock.IntegerTestEntityMockProvider;
import org.sevensource.support.test.jpa.domain.AbstractIntegerEntityTestSupport;
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
