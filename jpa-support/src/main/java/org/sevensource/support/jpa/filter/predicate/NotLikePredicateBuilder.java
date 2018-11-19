package org.sevensource.support.jpa.filter.predicate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

class NotLikePredicateBuilder implements ComparisonFilterCriteriaPredicateBuilder {

	static final NotLikePredicateBuilder INSTANCE = new NotLikePredicateBuilder();
	private NotLikePredicateBuilder() { }
	
	@Override
	public Predicate build(Expression propertyPath, Object argument, CriteriaBuilder builder) {
    	if(argument == null) {
    		return IsNotNullPredicateBuilder.INSTANCE.build(propertyPath, argument, builder);
    	}
		String like = argument.toString().replace(FilterCriteriaPredicateBuilder.LIKE_WILDCARD, '%');
		return builder.or(builder.notLike(builder.lower(propertyPath), like.toLowerCase()), builder.isNull(propertyPath));
	}

}
