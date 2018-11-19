package org.sevensource.support.jpa.filter.predicate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

@FunctionalInterface
public interface ComparisonFilterCriteriaPredicateBuilder {
	
	Predicate build(Expression<?> propertyPath, Object argument, CriteriaBuilder builder);
}
