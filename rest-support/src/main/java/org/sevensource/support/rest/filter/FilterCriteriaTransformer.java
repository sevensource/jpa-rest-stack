package org.sevensource.support.rest.filter;

import org.sevensource.support.jpa.filter.ComparisonFilterCriteria;
import org.sevensource.support.jpa.filter.ComparisonFilterOperator;
import org.sevensource.support.jpa.filter.FilterCriteria;

public interface FilterCriteriaTransformer {
	
	/**
	 * determine if an operation on a given field is allowed
	 * 
	 * @param field the name of the field
	 * @param operation the operation to be applied to the given field
	 * @return true if the operation is allowed
	 */
	boolean isFieldOperationAllowed(String fieldName, ComparisonFilterOperator operation);
	
	/**
	 * optionally transform or replace a ComparisonFilterCriteria
	 * @param criteria the current criteria
	 * @return any FilterCriteria
	 */
	default FilterCriteria transform(ComparisonFilterCriteria criteria) {
		return criteria;
	}
}
