package org.sevensource.support.jpa.hibernate.unique;

import org.junit.runner.RunWith;
import org.sevensource.support.jpa.configuration.JpaTestConfiguration;
import org.sevensource.support.jpa.model.ThreeUniqueEntity;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = JpaTestConfiguration.class)
@Import({ValidationAutoConfiguration.class})
public class UniqueConstraintValidatorTripleTests extends UniqueConstraintValidatorTestSupport<ThreeUniqueEntity> {

	private final static String FAIL_VALUE_1 = "XXX";
	private final static String FAIL_VALUE_2 = "YYY";
	private final static String FAIL_VALUE_3 = "ZZZ";

	@Override
	protected String getTableName() {
		return "three_unique_entity";
	}
	
	@Override
	protected String[] getColumns() {
		return new String[]{"title", "name", "lastname"};
	}
	
	@Override
	protected String[] getValues() {
		return new String[]{"'"+FAIL_VALUE_1+"'", "'"+FAIL_VALUE_2+"'", "'"+FAIL_VALUE_3+"'"};
	}
	
	@Override
	protected ThreeUniqueEntity populate(boolean fail) {
		ThreeUniqueEntity e = new ThreeUniqueEntity();
		e.setTitle("TITLE");
		e.setName(fail ? FAIL_VALUE_2 : "NAME");
		e.setLastname(fail ? FAIL_VALUE_3 : "LASTNAME");
		return e;
	}
	
	@Override
	protected void touchOne(ThreeUniqueEntity e, boolean fail) {
		e.setTitle(fail ? FAIL_VALUE_1 : "NAME");
		e.setLastname(fail ? FAIL_VALUE_3 : "LASTNAME");
	}
	
}
