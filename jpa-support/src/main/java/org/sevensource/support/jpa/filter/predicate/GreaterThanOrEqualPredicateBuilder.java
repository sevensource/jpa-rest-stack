package org.sevensource.support.jpa.filter.predicate;

import java.time.temporal.Temporal;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.springframework.util.Assert;

class GreaterThanOrEqualPredicateBuilder implements ComparisonFilterCriteriaPredicateBuilder {

	static final GreaterThanOrEqualPredicateBuilder INSTANCE = new GreaterThanOrEqualPredicateBuilder();
	private GreaterThanOrEqualPredicateBuilder() { }
	
	@Override
	public Predicate build(Expression propertyPath, Object argument, CriteriaBuilder builder) {
    	Assert.notNull(argument, "Argument must not be null");
    	if(argument instanceof Number) {
    		return builder.ge(propertyPath, (Number) argument);
    	} else if((argument instanceof Temporal && argument instanceof Comparable)) {
    		return builder.greaterThanOrEqualTo(propertyPath, (Comparable) argument);
    	} else {
    		throw new IllegalArgumentException("Cannot create greaterThanOrEqualTo for type " + argument.getClass());
    	}
	}

}
