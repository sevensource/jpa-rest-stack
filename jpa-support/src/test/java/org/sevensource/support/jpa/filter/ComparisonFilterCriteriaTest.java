package org.sevensource.support.jpa.filter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ComparisonFilterCriteriaTest {

	@Test
	public void test_with_new_value() {
		ComparisonFilterCriteria criteria = new ComparisonFilterCriteria("test", ComparisonFilterOperator.EQUAL_TO, "one");
		ComparisonFilterCriteria criteria2 = criteria.withNewValue("two");
		assertThat(criteria.getKey()).isEqualTo(criteria2.getKey());
		assertThat(criteria.getOperator()).isEqualTo(criteria2.getOperator());
		assertThat(criteria.getValue()).isEqualTo("one");
		assertThat(criteria2.getValue()).isEqualTo("two");
	}

}
