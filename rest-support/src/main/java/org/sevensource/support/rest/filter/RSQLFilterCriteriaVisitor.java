package org.sevensource.support.rest.filter;

import java.util.Map;

import org.sevensource.support.jpa.filter.ComparisonFilterCriteria;
import org.sevensource.support.jpa.filter.ComparisonFilterOperator;
import org.sevensource.support.jpa.filter.FilterCriteria;
import org.sevensource.support.jpa.filter.LogicalFilterCriteria;
import org.sevensource.support.jpa.filter.LogicalFilterOperator;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.LogicalNode;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;

class RSQLFilterCriteriaVisitor implements RSQLVisitor<FilterCriteria, FilterCriteriaTransformer> {
 	
	private final Map<String, ComparisonFilterOperator> rsqlOperator2FilterOperator;
	
	public RSQLFilterCriteriaVisitor(Map<String, ComparisonFilterOperator> rsqlOperator2FilterOperator) {
		this.rsqlOperator2FilterOperator = rsqlOperator2FilterOperator;
	}
	
    @Override
    public FilterCriteria visit(ComparisonNode node, FilterCriteriaTransformer transformer) {
    	
    	final String key = node.getSelector();
    	final ComparisonFilterOperator operator = mapOperator(node.getOperator().getSymbols());
    	
    	if(! transformer.isFieldOperationAllowed(key, operator)) {
    		throw new InvalidFilterCriteriaException("Operation " + operator.name() + " not allowed on field " + key);
    	}
    	
    	final String mappedKey = transformer.mapFieldName(key);
    	final Object value = node.getOperator().isMultiValue() ? node.getArguments() : node.getArguments().get(0);
    	final Object mappedValue = transformer.mapFieldValue(key, value);
    	
    	return new ComparisonFilterCriteria(mappedKey, operator, mappedValue);
    }
	
    @Override
    public FilterCriteria visit(AndNode node, FilterCriteriaTransformer transformer) {
    	return visit(node, LogicalFilterOperator.AND, transformer);
    }
 
    @Override
    public FilterCriteria visit(OrNode node, FilterCriteriaTransformer transformer) {
    	return visit(node, LogicalFilterOperator.OR, transformer);
    }
    
	private LogicalFilterCriteria visit(LogicalNode node, LogicalFilterOperator operator, FilterCriteriaTransformer transformer) {
    	LogicalFilterCriteria wrapper = new LogicalFilterCriteria(operator);
    	for(Node childNode : node.getChildren()) {
    		FilterCriteria criteria = visit(childNode, transformer);
    		wrapper.addChild(criteria);
    	}
    	return wrapper;
	}
    
	private FilterCriteria visit(Node node, FilterCriteriaTransformer transformer) {
		if(node instanceof AndNode) {
			return visit( (AndNode) node, transformer);
		} else if(node instanceof OrNode) {
			return visit( (OrNode) node, transformer);
		} else if(node instanceof ComparisonNode) {
			return visit( (ComparisonNode) node, transformer);
		} else {
			throw new IllegalArgumentException("Don't know how to handle node of type " + node.getClass());
		}
	}
    
    ComparisonFilterOperator mapOperator(String[] operators) {
    	for(String operator : operators) {
    		if(rsqlOperator2FilterOperator.containsKey(operator)) {
    			return rsqlOperator2FilterOperator.get(operator);
    		}
    	}
    	
    	throw new IllegalArgumentException("Don't know how to map operators " + String.join(",", operators) + " to FilterOperation");
    }
}