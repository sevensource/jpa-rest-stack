package org.sevensource.support.jpa.filter.predicate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

class EqualPredicateBuilder implements ComparisonFilterCriteriaPredicateBuilder {
	
	static final EqualPredicateBuilder INSTANCE = new EqualPredicateBuilder();
	private EqualPredicateBuilder() { }
	
	@Override
	public Predicate build(Expression propertyPath, Object argument, CriteriaBuilder builder) {
    	if(argument == null) {
    		return IsNullPredicateBuilder.INSTANCE.build(propertyPath, argument, builder);
    	}
    	return builder.equal(propertyPath, argument);
	}

}
