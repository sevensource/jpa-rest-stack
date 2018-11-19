package org.sevensource.support.jpa.filter.predicate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

class NotEqualPredicateBuilder implements ComparisonFilterCriteriaPredicateBuilder {

	static final NotEqualPredicateBuilder INSTANCE = new NotEqualPredicateBuilder();
	private NotEqualPredicateBuilder() { }
	
	@Override
	public Predicate build(Expression propertyPath, Object argument, CriteriaBuilder builder) {
    	if(argument == null) {
    		return IsNotNullPredicateBuilder.INSTANCE.build(propertyPath, argument, builder);
    	}
    	return builder.or(builder.notEqual(propertyPath, argument), builder.isNull(propertyPath));
	}

}
