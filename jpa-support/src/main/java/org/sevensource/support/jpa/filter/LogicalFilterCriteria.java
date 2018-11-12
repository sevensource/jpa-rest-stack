package org.sevensource.support.jpa.filter;

import java.util.ArrayList;
import java.util.List;

public class LogicalFilterCriteria implements FilterCriteria {

	private final LogicalFilterOperator logicalOperator;
	private final List<FilterCriteria> children = new ArrayList<>();
	
	
	public LogicalFilterCriteria(LogicalFilterOperator operator) {
		this.logicalOperator = operator;
	}
	
	public LogicalFilterOperator getLogicalOperator() {
		return logicalOperator;
	}
	public List<FilterCriteria> getChildren() {
		return children;
	}
	public void addChild(FilterCriteria child) {
		this.children.add(child);
	}
	
}
