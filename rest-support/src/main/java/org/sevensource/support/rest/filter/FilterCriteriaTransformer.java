package org.sevensource.support.rest.filter;

import org.sevensource.support.jpa.filter.ComparisonFilterCriteria;
import org.sevensource.support.jpa.filter.ComparisonFilterOperator;
import org.sevensource.support.jpa.filter.FilterCriteria;

public interface FilterCriteriaTransformer {
	
	/**
	 * specifies if an operation is allowed
	 * 
	 * @param field the *unmapped* named of the field
	 * @param operation the operation to be taken on the specified field
	 * @return true if the operation is allowed
	 */
	default boolean isFieldOperationAllowed(String fieldName, ComparisonFilterOperator operation) {
		return false;
	}
	
	/**
	 * optionally transform or replace a ComparisonFilterCriteria
	 * @param criteria the current criteria
	 * @return any FilterCriteria
	 */
	default FilterCriteria transform(ComparisonFilterCriteria criteria) {
		return criteria;
	}
}
