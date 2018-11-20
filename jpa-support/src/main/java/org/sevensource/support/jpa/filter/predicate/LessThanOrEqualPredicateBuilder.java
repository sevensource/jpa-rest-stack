package org.sevensource.support.jpa.filter.predicate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.springframework.util.Assert;

class LessThanOrEqualPredicateBuilder implements ComparisonFilterCriteriaPredicateBuilder {

	static final LessThanOrEqualPredicateBuilder INSTANCE = new LessThanOrEqualPredicateBuilder();
	private LessThanOrEqualPredicateBuilder() { }
	
	@Override
	public Predicate build(Expression propertyPath, Object argument, CriteriaBuilder builder) {
    	Assert.notNull(argument, "Argument must not be null");
    	if(argument instanceof Number) {
    		return builder.le(propertyPath, (Number) argument);
    	} else if(argument instanceof Comparable) {
    		return builder.lessThanOrEqualTo(propertyPath, (Comparable) argument);
    	} else {
    		throw new IllegalArgumentException("Cannot create lessThanOrEqualTo for type " + argument.getClass());
    	}
	}

}
