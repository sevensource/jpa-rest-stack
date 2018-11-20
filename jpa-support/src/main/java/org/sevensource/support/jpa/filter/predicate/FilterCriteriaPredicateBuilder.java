package org.sevensource.support.jpa.filter.predicate;

import java.util.EnumMap;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.sevensource.support.jpa.filter.ComparisonFilterCriteria;
import org.sevensource.support.jpa.filter.ComparisonFilterOperator;
import org.sevensource.support.jpa.filter.FilterCriteria;
import org.sevensource.support.jpa.filter.LogicalFilterCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

public class FilterCriteriaPredicateBuilder<T> implements Specification<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7837417206326530691L;
	
	public static final char LIKE_WILDCARD = '*';
	
	private final transient FilterCriteria filterCriteria;
	
	private final transient Map<ComparisonFilterOperator, ComparisonFilterCriteriaPredicateBuilder> predicateBuilders;

	public FilterCriteriaPredicateBuilder(FilterCriteria filterCriteria) {
		this.filterCriteria = filterCriteria;

		this.predicateBuilders = new EnumMap<>(ComparisonFilterOperator.class);
		this.predicateBuilders.put(ComparisonFilterOperator.EQUAL_TO,				EqualPredicateBuilder.INSTANCE);
		this.predicateBuilders.put(ComparisonFilterOperator.GREATER_THAN, 		GreaterThanPredicateBuilder.INSTANCE);
		this.predicateBuilders.put(ComparisonFilterOperator.GREATER_THAN_OR_EQUAL,GreaterThanOrEqualPredicateBuilder.INSTANCE);
		this.predicateBuilders.put(ComparisonFilterOperator.IN, 					InPredicateBuilder.INSTANCE);
		this.predicateBuilders.put(ComparisonFilterOperator.LESS_THAN, 			LessThanPredicateBuilder.INSTANCE);
		this.predicateBuilders.put(ComparisonFilterOperator.LESS_THAN_OR_EQUAL, 	LessThanOrEqualPredicateBuilder.INSTANCE);
		this.predicateBuilders.put(ComparisonFilterOperator.LIKE, 				LikePredicateBuilder.INSTANCE);
		this.predicateBuilders.put(ComparisonFilterOperator.NOT_EQUAL_TO,			NotEqualPredicateBuilder.INSTANCE);
		this.predicateBuilders.put(ComparisonFilterOperator.NOT_IN, 				NotInPredicateBuilder.INSTANCE);
		this.predicateBuilders.put(ComparisonFilterOperator.NOT_LIKE, 			NotLikePredicateBuilder.INSTANCE);
	}
	
	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
		return toPredicate(root, filterCriteria, criteriaBuilder);
	}
	
	
	private Predicate toPredicate(Root<T> root, FilterCriteria criteria, CriteriaBuilder criteriaBuilder) {
		if(criteria instanceof LogicalFilterCriteria) {
			return toLogicalPredicate(root, (LogicalFilterCriteria) criteria, criteriaBuilder);
		} else if(criteria instanceof ComparisonFilterCriteria) {
			return toComparisonPredicate(root, (ComparisonFilterCriteria) criteria, criteriaBuilder);
		} else {
			throw new IllegalArgumentException("Don't know how to handle criteria of type " + criteria.getClass());
		}
	}
	
	
	private Predicate toLogicalPredicate(Root<T> root, LogicalFilterCriteria criteria, CriteriaBuilder criteriaBuilder) {
		
		Assert.notEmpty(criteria.getChildren(), "ComparisonFilterCriteria must not be empty");
		Assert.notNull(criteria.getLogicalOperator(), "LogicalOperator must not be null");
		
		final Predicate[] predicates = criteria.getChildren()
				.stream()
				.map(fc -> toPredicate(root, fc, criteriaBuilder))
				.toArray(size -> new Predicate[size]);
    	
    	switch(criteria.getLogicalOperator()) {
    		case AND:
    			return criteriaBuilder.and(predicates);
    		case OR:
    			return criteriaBuilder.or(predicates);
        	default:
        		throw new IllegalArgumentException("Unknown operator: " + criteria.getLogicalOperator());
        }
	}
	
	private Predicate toComparisonPredicate(Root<T> root, ComparisonFilterCriteria criteria, CriteriaBuilder criteriaBuilder) {
		Path<?> propertyPath = findPropertyPath(root, criteria.getKey());
		
		final Object argument = criteria.getValue();
		
		ComparisonFilterCriteriaPredicateBuilder predicateBuilder = predicateBuilders.get(criteria.getOperator());
		if(predicateBuilder == null) {
			throw new IllegalArgumentException("Don't know how to create Predicate for comparator " + criteria.getOperator());
		} else {
			return predicateBuilder.build(propertyPath, argument, criteriaBuilder);
		}
	}
	
	private Path<?> findPropertyPath(Root<T> root, String propertyPath) {
		return root.get(propertyPath);
	}
}
