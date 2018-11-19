package org.sevensource.support.jpa.filter.predicate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

class IsNotNullPredicateBuilder implements ComparisonFilterCriteriaPredicateBuilder {

	
	static final IsNotNullPredicateBuilder INSTANCE = new IsNotNullPredicateBuilder();
	private IsNotNullPredicateBuilder() { }
	
	
	@Override
	public Predicate build(Expression propertyPath, Object argument, CriteriaBuilder builder) {
		return builder.isNotNull(propertyPath);
	}

}
