package org.sevensource.support.jpa.hibernate.unique;

import org.junit.runner.RunWith;
import org.sevensource.support.jpa.configuration.JpaTestConfiguration;
import org.sevensource.support.jpa.domain.OneUniqueEntity;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = JpaTestConfiguration.class)
@Import({ValidationAutoConfiguration.class})
public class UniqueConstraintValidatorSimpleTest extends UniqueConstraintValidatorTestSupport<OneUniqueEntity> {

	private final static String FAIL_VALUE_1 = "XXX";

	@Override
	protected String getTableName() {
		return "one_unique_entity";
	}
	
	@Override
	protected String[] getColumns() {
		return new String[]{"title"};
	}
	
	@Override
	protected String[] getValues() {
		return new String[]{"'"+FAIL_VALUE_1+"'"};
	}
	
	@Override
	protected OneUniqueEntity populate(boolean fail) {
		OneUniqueEntity e = new OneUniqueEntity();
		e.setTitle(fail ? FAIL_VALUE_1 : "TITLE");
		return e;
	}
	@Override
	protected void touchOne(OneUniqueEntity e, boolean fail) {
		e.setTitle(fail ? FAIL_VALUE_1 : "TITLE1");
	}
	
}
