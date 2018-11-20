package org.sevensource.support.jpa.filter.predicate;

import java.util.Arrays;
import java.util.Collection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.springframework.util.Assert;

class InPredicateBuilder implements ComparisonFilterCriteriaPredicateBuilder {

	
	static final InPredicateBuilder INSTANCE = new InPredicateBuilder();
	private InPredicateBuilder() { }
	
	
	@Override
	public Predicate build(Expression propertyPath, Object arguments, CriteriaBuilder builder) {
    	Assert.notNull(arguments, "Argument must not be null");
    	
    	if(! (arguments instanceof Collection)) {
    		arguments = Arrays.asList(arguments);
    	}
    	
	    return propertyPath.in((Collection)arguments);
	}

}
