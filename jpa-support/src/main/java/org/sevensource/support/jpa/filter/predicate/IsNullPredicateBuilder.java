package org.sevensource.support.jpa.filter.predicate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

class IsNullPredicateBuilder implements ComparisonFilterCriteriaPredicateBuilder {

	static final IsNullPredicateBuilder INSTANCE = new IsNullPredicateBuilder();
	private IsNullPredicateBuilder() { }
	
	@Override
	public Predicate build(Expression propertyPath, Object argument, CriteriaBuilder builder) {
		return builder.isNull(propertyPath);
	}

}
