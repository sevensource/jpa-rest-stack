package org.sevensource.support.jpa.filter.predicate;

import java.util.Arrays;
import java.util.Collection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.springframework.util.Assert;

class NotInPredicateBuilder implements ComparisonFilterCriteriaPredicateBuilder {

	static final NotInPredicateBuilder INSTANCE = new NotInPredicateBuilder();
	private NotInPredicateBuilder() { }
	
	@Override
	public Predicate build(Expression propertyPath, Object arguments, CriteriaBuilder builder) {
    	Assert.notNull(arguments, "Argument must not be null");
    	if(! (arguments instanceof Collection)) {
    		arguments = Arrays.asList(arguments);
    	}
    	
    	return builder.or(propertyPath.isNull(), builder.not(propertyPath.in((Collection)arguments)));
	}

}
