package org.sevensource.support.jpa.filter;

import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

public class FilterCriteriaPredicateBuilder<T> implements Specification<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7837417206326530691L;
	
	public static final char LIKE_WILDCARD = '*';
	
	private final transient FilterCriteria filterCriteria;

	public FilterCriteriaPredicateBuilder(FilterCriteria filterCriteria) {
		this.filterCriteria = filterCriteria;
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
		final List<Predicate> predicates = new ArrayList<>();
		
    	for (FilterCriteria child : criteria.getChildren()) {
    		predicates.add(toPredicate(root, child, criteriaBuilder));
		}
    	
    	switch(criteria.getLogicalOperator()) {
    		case AND:
    			return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    		case OR:
    			return criteriaBuilder.or(predicates.toArray(new Predicate[predicates.size()]));
        	default:
        		throw new IllegalArgumentException("Unknown operator: " + criteria.getLogicalOperator());
        }
	}
	
	private Predicate toComparisonPredicate(Root<T> root, ComparisonFilterCriteria criteria, CriteriaBuilder criteriaBuilder) {
		Path<?> propertyPath = findPropertyPath(root, criteria.getKey());
		
		final Object argument = criteria.getValue();
		
		switch(criteria.getOperator()) {
		case EQUAL_TO:
			if(criteria.getValue() == null) {
				return createIsNull(propertyPath, criteriaBuilder);
			} else {
				return createEqual(propertyPath, argument, criteriaBuilder);
			}
		case NOT_EQUAL_TO:
			if(criteria.getValue() == null) {
				return createIsNotNull(propertyPath, criteriaBuilder);
			} else {
				return createNotEqual(propertyPath, argument, criteriaBuilder);
			}
		case LIKE:
			if(criteria.getValue() == null) {
				return createIsNull(propertyPath, criteriaBuilder);
			} else {
				return createLike(propertyPath, argument, criteriaBuilder);
			}
		case NOT_LIKE:
			if(criteria.getValue() == null) {
				return createIsNotNull(propertyPath, criteriaBuilder);
			} else {
				return createNotLike(propertyPath, argument, criteriaBuilder);
			}
		case IN:
			return createIn(propertyPath, argument, criteriaBuilder);
		case NOT_IN:
			return createNotIn(propertyPath, argument, criteriaBuilder);
		case GREATER_THAN:
			return createGreaterThan(propertyPath, argument, criteriaBuilder);
		case GREATER_THAN_OR_EQUAL:
			return createGreaterThanOrEqualTo(propertyPath, argument, criteriaBuilder);
		case LESS_THAN:
			return createLessThan(propertyPath, argument, criteriaBuilder);
		case LESS_THAN_OR_EQUAL:
			return createLessThanOrEqualTo(propertyPath, argument, criteriaBuilder);
		default:
			throw new IllegalArgumentException("Don't know how to create Predicate for comparator " + criteria.getOperator());
		}
	}
	
	private Path<?> findPropertyPath(Root<T> root, String propertyPath) {
		return root.get(propertyPath);
	}

    private static Predicate createIsNull(Expression<?> propertyPath, CriteriaBuilder builder) {
    	return builder.isNull(propertyPath);
    }
    
    private static Predicate createIsNotNull(Expression<?> propertyPath, CriteriaBuilder builder) {
    	return builder.isNotNull(propertyPath);
    }
    
    private Predicate createEqual(Expression<?> propertyPath, Object argument, CriteriaBuilder builder) {
    	Assert.notNull(argument, "Argument must not be null");
    	return builder.equal(propertyPath, argument);
    }
    
    private static Predicate createNotEqual(Expression<?> propertyPath, Object argument, CriteriaBuilder builder) {
    	Assert.notNull(argument, "Argument must not be null");
    	return builder.or(builder.notEqual(propertyPath, argument), builder.isNull(propertyPath));
    }
    
    private static Predicate createLike(Expression propertyPath, Object argument, CriteriaBuilder builder) {
    	Assert.notNull(argument, "Argument must not be null");
		String like = argument.toString().replace(LIKE_WILDCARD, '%');
	    return builder.like(builder.lower(propertyPath), like.toLowerCase());
    }
    
    private static Predicate createNotLike(Expression propertyPath, Object argument, CriteriaBuilder builder) {
    	Assert.notNull(argument, "Argument must not be null");
		String like = argument.toString().replace(LIKE_WILDCARD, '%');
		return builder.or(builder.notLike(builder.lower(propertyPath), like.toLowerCase()), builder.isNull(propertyPath));
    }
    
    private static Predicate createIn(Expression<?> propertyPath, Object arguments, CriteriaBuilder builder) {
    	Assert.notNull(arguments, "Argument must not be null");
    	
    	if(! (arguments instanceof Collection)) {
    		arguments = Arrays.asList(arguments);
    	}
    	
	    return propertyPath.in((Collection)arguments);
    }
    
    private static Predicate createNotIn(Expression<?> propertyPath, Object arguments, CriteriaBuilder builder) {
    	Assert.notNull(arguments, "Argument must not be null");
    	if(! (arguments instanceof Collection)) {
    		arguments = Arrays.asList(arguments);
    	}
    	
    	return builder.or(propertyPath.isNull(), builder.not(propertyPath.in((Collection)arguments)));
    }
    
    private static Predicate createGreaterThan(Expression propertyPath, Object argument, CriteriaBuilder builder) {
    	Assert.notNull(argument, "Argument must not be null");
    	if(argument instanceof Number) {
    		return builder.gt(propertyPath, (Number) argument);
    	} else if((argument instanceof Temporal && argument instanceof Comparable)) {
    		return builder.greaterThan(propertyPath, (Comparable) argument);
    	} else {
    		throw new IllegalArgumentException("Cannot create greaterThan for type " + argument.getClass());
    	}
    }
    
    private static Predicate createGreaterThanOrEqualTo(Expression propertyPath, Object argument, CriteriaBuilder builder) {
    	Assert.notNull(argument, "Argument must not be null");
    	if(argument instanceof Number) {
    		return builder.ge(propertyPath, (Number) argument);
    	} else if((argument instanceof Temporal && argument instanceof Comparable)) {
    		return builder.greaterThanOrEqualTo(propertyPath, (Comparable) argument);
    	} else {
    		throw new IllegalArgumentException("Cannot create greaterThanOrEqualTo for type " + argument.getClass());
    	}
    }
    
    private static Predicate createLessThan(Expression propertyPath, Object argument, CriteriaBuilder builder) {
    	Assert.notNull(argument, "Argument must not be null");
    	if(argument instanceof Number) {
    		return builder.lt(propertyPath, (Number) argument);
    	} else if((argument instanceof Temporal && argument instanceof Comparable)) {
    		return builder.lessThan(propertyPath, (Comparable) argument);
    	} else {
    		throw new IllegalArgumentException("Cannot create lessThan for type " + argument.getClass());
    	}
    }
    
    private static Predicate createLessThanOrEqualTo(Expression propertyPath, Object argument, CriteriaBuilder builder) {
    	Assert.notNull(argument, "Argument must not be null");
    	if(argument instanceof Number) {
    		return builder.le(propertyPath, (Number) argument);
    	} else if((argument instanceof Temporal && argument instanceof Comparable)) {
    		return builder.lessThanOrEqualTo(propertyPath, (Comparable) argument);
    	} else {
    		throw new IllegalArgumentException("Cannot create lessThanOrEqualTo for type " + argument.getClass());
    	}
    }
}
