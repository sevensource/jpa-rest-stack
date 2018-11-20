package org.sevensource.support.jpa.filter.predicate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

class LikePredicateBuilder implements ComparisonFilterCriteriaPredicateBuilder {

	static final LikePredicateBuilder INSTANCE = new LikePredicateBuilder();
	private LikePredicateBuilder() { }
	
	@Override
	public Predicate build(Expression propertyPath, Object argument, CriteriaBuilder builder) {
    	if(argument == null) {
    		return IsNullPredicateBuilder.INSTANCE.build(propertyPath, argument, builder);
    	}
		String like = argument.toString().replace(FilterCriteriaPredicateBuilder.LIKE_WILDCARD, '%');
	    return builder.like(builder.lower(propertyPath), like.toLowerCase());
	}

}
