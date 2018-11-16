package org.sevensource.support.rest.filter;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.sevensource.support.jpa.filter.ComparisonFilterOperator;
import org.sevensource.support.jpa.filter.FilterCriteria;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.RSQLOperators;

public class RSQLFilterCriteriaParser {
	
	private static final Set<ComparisonOperator> rsqlOperators;
	
	static {
		final Set<ComparisonOperator> operators = new HashSet<>();
		operators.addAll(RSQLOperators.defaultOperators());
		operators.add(new ComparisonOperator("=like=", false));
		operators.add(new ComparisonOperator("=notlike=", false));
		rsqlOperators = Collections.unmodifiableSet(operators);
	}
	
	private static final Map<String, ComparisonFilterOperator> rsqlOperator2FilterOperator;
	
	static {
		final Map<String, ComparisonFilterOperator> mappings = new HashMap<>();
		mappings.put("==", ComparisonFilterOperator.EQUAL_TO);
		mappings.put("!=", ComparisonFilterOperator.NOT_EQUAL_TO);
		mappings.put(">", ComparisonFilterOperator.GREATER_THAN);
		mappings.put(">=", ComparisonFilterOperator.GREATER_THAN_OR_EQUAL);
		mappings.put("<", ComparisonFilterOperator.LESS_THAN);
		mappings.put("<=", ComparisonFilterOperator.LESS_THAN_OR_EQUAL);

		mappings.put("=like=", ComparisonFilterOperator.LIKE);
		mappings.put("=notlike=", ComparisonFilterOperator.NOT_LIKE);
		
		mappings.put("=gt=", ComparisonFilterOperator.GREATER_THAN);
		mappings.put("=ge=", ComparisonFilterOperator.GREATER_THAN_OR_EQUAL);
		mappings.put("=lt=", ComparisonFilterOperator.LESS_THAN);
		mappings.put("=le=", ComparisonFilterOperator.LESS_THAN_OR_EQUAL);
		mappings.put("=in=", ComparisonFilterOperator.IN);
		mappings.put("=out=", ComparisonFilterOperator.NOT_IN);
		
		rsqlOperator2FilterOperator = Collections.unmodifiableMap(mappings);
	}
	
	private static final RSQLParser PARSER = new RSQLParser(rsqlOperators);
	private static final RSQLFilterCriteriaVisitor VISITOR = new RSQLFilterCriteriaVisitor(rsqlOperator2FilterOperator);
	
	private RSQLFilterCriteriaParser() {}

	public static FilterCriteria parse(String in, FilterCriteriaTransformer transformer) {
		final Node rootNode = PARSER.parse(in);
		return rootNode.accept(VISITOR, transformer);
	}
	
	static RSQLFilterCriteriaVisitor getVisitor() {
		return VISITOR;
	}
	
	static Map<String, ComparisonFilterOperator> getRsqloperator2filteroperator() {
		return rsqlOperator2FilterOperator;
	}
	
	static Set<ComparisonOperator> getRsqloperators() {
		return rsqlOperators;
	}
}
