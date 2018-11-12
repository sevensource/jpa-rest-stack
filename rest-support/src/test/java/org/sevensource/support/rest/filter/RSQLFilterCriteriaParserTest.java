package org.sevensource.support.rest.filter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.assertj.core.util.Arrays;
import org.junit.Test;
import org.sevensource.support.jpa.filter.ComparisonFilterCriteria;
import org.sevensource.support.jpa.filter.ComparisonFilterOperator;
import org.sevensource.support.jpa.filter.FilterCriteria;
import org.sevensource.support.jpa.filter.LogicalFilterCriteria;
import org.sevensource.support.jpa.filter.LogicalFilterOperator;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;

public class RSQLFilterCriteriaParserTest {
	
	static class AllAcceptingTransformer implements FilterCriteriaTransformer {

		@Override
		public boolean isFieldOperationAllowed(String field, ComparisonFilterOperator operation) {
			return true;
		}
	}

	@Test
	public void single_value_has_simple_value() {
		String in = "name==John";
		FilterCriteria criteria = RSQLFilterCriteriaParser.parse(in, new AllAcceptingTransformer());
		assertThat(criteria)
			.isExactlyInstanceOf(ComparisonFilterCriteria.class);
		ComparisonFilterCriteria c = (ComparisonFilterCriteria) criteria;
		assertThat(c.getKey()).isEqualTo("name");
		assertThat(c.getOperator()).isEqualTo(ComparisonFilterOperator.EQUAL_TO);
		assertThat(c.getValue()).isInstanceOf(String.class).isEqualTo("John");
	}
	
	@Test
	public void multi_value_has_multivalue() {
		String in = "name=in=(John,Mark)";
		FilterCriteria criteria = RSQLFilterCriteriaParser.parse(in, new AllAcceptingTransformer());
		assertThat(criteria)
			.isExactlyInstanceOf(ComparisonFilterCriteria.class);
		ComparisonFilterCriteria c = (ComparisonFilterCriteria) criteria;
		assertThat(c.getKey()).isEqualTo("name");
		assertThat(c.getOperator()).isEqualTo(ComparisonFilterOperator.IN);
		assertThat(c.getValue()).isInstanceOf(List.class).asList().containsExactly("John","Mark");
	}
	
	@Test
	public void simple_and_logical() {
		String in = "name==John and (lastname=='Marshall' or lastname==Bloggs)";
		FilterCriteria criteria = RSQLFilterCriteriaParser.parse(in, new AllAcceptingTransformer());
		assertThat(criteria)
			.isExactlyInstanceOf(LogicalFilterCriteria.class);
		LogicalFilterCriteria c = (LogicalFilterCriteria) criteria;
		assertThat(c.getLogicalOperator()).isEqualTo(LogicalFilterOperator.AND);
		assertThat(c.getChildren()).hasSize(2);

		assertThat(c.getChildren().get(0)).isExactlyInstanceOf(ComparisonFilterCriteria.class);
		assertThat(c.getChildren().get(1)).isExactlyInstanceOf(LogicalFilterCriteria.class);
		
		ComparisonFilterCriteria c1 = (ComparisonFilterCriteria) c.getChildren().get(0);
		LogicalFilterCriteria c2 = (LogicalFilterCriteria) c.getChildren().get(1);
		
		assertThat(c1.getKey()).isEqualTo("name");
		assertThat(c1.getOperator()).isEqualTo(ComparisonFilterOperator.EQUAL_TO);
		assertThat(c1.getValue()).isEqualTo("John");
		
		assertThat(c2.getChildren()).hasSize(2);
		ComparisonFilterCriteria c2_1 = (ComparisonFilterCriteria) c2.getChildren().get(0);
		ComparisonFilterCriteria c2_2 = (ComparisonFilterCriteria) c2.getChildren().get(1);
		
		assertThat(c2_1.getKey()).isEqualTo("lastname");
		assertThat(c2_1.getOperator()).isEqualTo(ComparisonFilterOperator.EQUAL_TO);
		assertThat(c2_1.getValue()).isEqualTo("Marshall");
		
		assertThat(c2_2.getKey()).isEqualTo("lastname");
		assertThat(c2_2.getOperator()).isEqualTo(ComparisonFilterOperator.EQUAL_TO);
		assertThat(c2_2.getValue()).isEqualTo("Bloggs");
	}
	
	@Test
	public void logical_and_logical() {
		String in = "(name==John and sex==male) and (lastname=='Marshall' or lastname==Bloggs)";
		FilterCriteria criteria = RSQLFilterCriteriaParser.parse(in, new AllAcceptingTransformer());
		assertThat(criteria)
			.isExactlyInstanceOf(LogicalFilterCriteria.class);
		LogicalFilterCriteria c = (LogicalFilterCriteria) criteria;
		assertThat(c.getLogicalOperator()).isEqualTo(LogicalFilterOperator.AND);
		assertThat(c.getChildren()).hasSize(2);

		assertThat(c.getChildren().get(0)).isExactlyInstanceOf(LogicalFilterCriteria.class);
		assertThat(c.getChildren().get(1)).isExactlyInstanceOf(LogicalFilterCriteria.class);
		
		LogicalFilterCriteria c1 = (LogicalFilterCriteria) c.getChildren().get(0);
		LogicalFilterCriteria c2 = (LogicalFilterCriteria) c.getChildren().get(1);

		assertThat(c1.getChildren()).hasSize(2);
		ComparisonFilterCriteria c1_1 = (ComparisonFilterCriteria) c1.getChildren().get(0);
		ComparisonFilterCriteria c1_2 = (ComparisonFilterCriteria) c1.getChildren().get(1);
		
		assertThat(c1_1.getKey()).isEqualTo("name");
		assertThat(c1_1.getOperator()).isEqualTo(ComparisonFilterOperator.EQUAL_TO);
		assertThat(c1_1.getValue()).isEqualTo("John");
		
		assertThat(c1_2.getKey()).isEqualTo("sex");
		assertThat(c1_2.getOperator()).isEqualTo(ComparisonFilterOperator.EQUAL_TO);
		assertThat(c1_2.getValue()).isEqualTo("male");
		
		assertThat(c2.getChildren()).hasSize(2);
		ComparisonFilterCriteria c2_1 = (ComparisonFilterCriteria) c2.getChildren().get(0);
		ComparisonFilterCriteria c2_2 = (ComparisonFilterCriteria) c2.getChildren().get(1);
		
		assertThat(c2_1.getKey()).isEqualTo("lastname");
		assertThat(c2_1.getOperator()).isEqualTo(ComparisonFilterOperator.EQUAL_TO);
		assertThat(c2_1.getValue()).isEqualTo("Marshall");
		
		assertThat(c2_2.getKey()).isEqualTo("lastname");
		assertThat(c2_2.getOperator()).isEqualTo(ComparisonFilterOperator.EQUAL_TO);
		assertThat(c2_2.getValue()).isEqualTo("Bloggs");
	}
	
	@Test
	public void has_mappings_for_all_rsqloperators() {
		Set<ComparisonOperator> rsqlOperators = RSQLFilterCriteriaParser.getRsqloperators();
		Set<String> stringOperators = rsqlOperators
				.stream()
				.flatMap(o -> Arrays.asList(o.getSymbols()).stream())
				.map(o -> o.toString())
				.collect(Collectors.toSet());
		
		Set<String> mappings = RSQLFilterCriteriaParser.getRsqloperator2filteroperator().keySet();
		
		assertThat(mappings).containsAll(stringOperators);
		assertThat(mappings).containsOnlyElementsOf(stringOperators);
	}
	
	@Test
	public void has_mappings_for_all_filteroperators() {
		Collection<ComparisonFilterOperator> mappings = RSQLFilterCriteriaParser.getRsqloperator2filteroperator().values();
		
		assertThat(mappings).containsAll(EnumSet.allOf(ComparisonFilterOperator.class));
	}
	
	@Test
	public void maps_all_operators() {
		for(ComparisonOperator op : RSQLFilterCriteriaParser.getRsqloperators()) {
			String[] symbols = op.getSymbols();
			for(String symbol : symbols) {
				ComparisonFilterOperator res = 
						RSQLFilterCriteriaParser.getVisitor().mapOperator(new String[] {symbol});
				assertThat(res).isNotNull().isExactlyInstanceOf(ComparisonFilterOperator.class);
			}
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void throws_exception_on_unknown_operator() {
		RSQLFilterCriteriaParser.getVisitor().mapOperator(new String[] {"=isunicorn="});
	}
}
