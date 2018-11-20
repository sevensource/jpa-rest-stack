package org.sevensource.support.jpa.filter.predicate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.springframework.util.Assert;

class GreaterThanPredicateBuilder implements ComparisonFilterCriteriaPredicateBuilder {

	static final GreaterThanPredicateBuilder INSTANCE = new GreaterThanPredicateBuilder();
	private GreaterThanPredicateBuilder() { }
	
	
	@Override
	public Predicate build(Expression propertyPath, Object argument, CriteriaBuilder builder) {
    	Assert.notNull(argument, "Argument must not be null");
    	if(argument instanceof Number) {
    		return builder.gt(propertyPath, (Number) argument);
    	} else if(argument instanceof Comparable) {
    		return builder.greaterThan(propertyPath, (Comparable) argument);
    	} else {
    		throw new IllegalArgumentException("Cannot create greaterThan for type " + argument.getClass());
    	}
	}

}
