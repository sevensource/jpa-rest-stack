package org.sevensource.support.rest.filter;

import org.sevensource.support.jpa.filter.ComparisonFilterOperator;

public interface FilterCriteriaTransformer {
	
	/**
	 * specifies if an operation is allowed
	 * 
	 * @param field the *unmapped* named of the field
	 * @param operation the operation to be taken on the specified field
	 * @return true if the operation is allowed
	 */
	boolean isFieldOperationAllowed(String field, ComparisonFilterOperator operation);
	
	/**
	 * maps a fieldName to an Entity fieldName
	 * 
	 * @param fieldName the *unmapped* fieldName
	 * @return the entityFieldName
	 */
	default String mapFieldName(String fieldName) {
		return fieldName;
	}
	
	/**
	 * transforms field values to an entityValue 
	 * 
	 * @param fieldName the *unmapped* fieldName
	 * @param value the value
	 * @return the entityValue
	 */
	default Object mapFieldValue(String fieldName, Object value) {
		return value;
	}
}
