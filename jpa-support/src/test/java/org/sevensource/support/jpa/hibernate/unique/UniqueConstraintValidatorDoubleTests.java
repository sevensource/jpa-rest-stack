package org.sevensource.support.jpa.hibernate.unique;

import org.junit.runner.RunWith;
import org.sevensource.support.jpa.configuration.JpaTestConfiguration;
import org.sevensource.support.jpa.domain.TwoUniqueEntity;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = JpaTestConfiguration.class)
@Import({ValidationAutoConfiguration.class})
public class UniqueConstraintValidatorDoubleTests extends UniqueConstraintValidatorTestSupport<TwoUniqueEntity> {

	private final static String FAIL_VALUE_1 = "XXX";
	private final static String FAIL_VALUE_2 = "YYY";

	@Override
	protected String getTableName() {
		return "two_unique_entity";
	}
	
	@Override
	protected String[] getColumns() {
		return new String[]{"title", "name"};
	}
	
	@Override
	protected String[] getValues() {
		return new String[]{"'"+FAIL_VALUE_1+"'", "'"+FAIL_VALUE_2+"'"};
	}
	
	@Override
	protected TwoUniqueEntity populate(boolean fail) {
		TwoUniqueEntity e = new TwoUniqueEntity();
		e.setTitle(fail ? FAIL_VALUE_1 : "TITLE");
		e.setName(fail ? FAIL_VALUE_2 : "NAME");
		return e;
	}
	
	@Override
	protected void touchOne(TwoUniqueEntity e, boolean fail) {
		e.setName(fail ? FAIL_VALUE_2 : "NAME1");
	}
	
}
