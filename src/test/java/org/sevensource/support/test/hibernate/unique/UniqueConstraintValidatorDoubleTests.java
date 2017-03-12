package org.sevensource.support.test.hibernate.unique;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sevensource.support.jpa.configuration.JpaAuditingTestConfiguration;
import org.sevensource.support.jpa.hibernate.unique.UniqueValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.BeforeTransaction;

import com.google.common.base.Joiner;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = JpaAuditingTestConfiguration.class)
@Import({ValidationAutoConfiguration.class})
public class UniqueConstraintValidatorDoubleTests extends UniqueConstraintValidatorTestSupport<TwoUniqueEntity> {

	private final static String FAIL_VALUE_1 = "XXX";
	private final static String FAIL_VALUE_2 = "YYY";

	protected String getTableName() {
		return "two_unique_entity";
	}
	
	protected String[] getColumns() {
		return new String[]{"title", "name"};
	}
	
	protected String[] getValues() {
		return new String[]{"'"+FAIL_VALUE_1+"'", "'"+FAIL_VALUE_2+"'"};
	}
	
	protected TwoUniqueEntity populate(boolean fail) {
		TwoUniqueEntity e = new TwoUniqueEntity();
		e.setTitle(fail ? FAIL_VALUE_1 : "TITLE");
		e.setName(fail ? FAIL_VALUE_2 : "NAME");
		return e;
	}
	
	protected void touchOne(TwoUniqueEntity e, boolean fail) {
		e.setName(fail ? FAIL_VALUE_2 : "NAME1");
	}
	
}
