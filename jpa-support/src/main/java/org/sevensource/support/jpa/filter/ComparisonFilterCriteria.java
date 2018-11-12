package org.sevensource.support.jpa.filter;

public class ComparisonFilterCriteria implements FilterCriteria {
	private final String key;
	private final ComparisonFilterOperator operator;
	private final Object value;
	
	public ComparisonFilterCriteria(String key, ComparisonFilterOperator operator, Object value) {
		this.key = key;
		this.operator = operator;
		this.value = value;
	}

	public String getKey() {
		return key;
	}
	public ComparisonFilterOperator getOperator() {
		return operator;
	}
	public Object getValue() {
		return value;
	}
}
