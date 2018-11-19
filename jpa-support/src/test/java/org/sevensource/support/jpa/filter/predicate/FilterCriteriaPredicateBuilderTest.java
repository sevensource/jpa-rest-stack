package org.sevensource.support.jpa.filter.predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sevensource.support.jpa.configuration.JpaTestConfiguration;
import org.sevensource.support.jpa.filter.ComparisonFilterCriteria;
import org.sevensource.support.jpa.filter.ComparisonFilterOperator;
import org.sevensource.support.jpa.filter.FilterCriteria;
import org.sevensource.support.jpa.filter.LogicalFilterCriteria;
import org.sevensource.support.jpa.filter.LogicalFilterOperator;
import org.sevensource.support.jpa.filter.predicate.domain.Customer;
import org.sevensource.support.jpa.filter.predicate.domain.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = JpaTestConfiguration.class)
@EntityScan(basePackageClasses=Customer.class)
@EnableJpaRepositories(basePackageClasses=CustomerRepository.class)
public class FilterCriteriaPredicateBuilderTest {
	
	@Autowired
	CustomerRepository repository;
	
	FilterCriteriaPredicateBuilder<Customer> builder(FilterCriteria criteria) {
		return new FilterCriteriaPredicateBuilder<>(criteria);
	}
	
	@Test
	public void unknown_logicalcriteria_throws() {	
		LogicalFilterCriteria criteria = new LogicalFilterCriteria(null);
		criteria.addChild(new ComparisonFilterCriteria("firstname", ComparisonFilterOperator.EQUAL_TO, null));
		FilterCriteriaPredicateBuilder<Customer> builder = builder(criteria);
		assertThatThrownBy(() -> repository.findAll(builder)).isExactlyInstanceOf(InvalidDataAccessApiUsageException.class);
	}

	@Test
	public void unknown_comparisoncriteria_throws() {		
		FilterCriteria criteria = new FilterCriteria() {};
		FilterCriteriaPredicateBuilder<Customer> builder = builder(criteria);
		assertThatThrownBy(() -> repository.findAll(builder)).isExactlyInstanceOf(InvalidDataAccessApiUsageException.class);
	}
	
	@Test
	public void unknown_comparisonoperator_throws() {		
		FilterCriteria criteria = new ComparisonFilterCriteria("firstname", null, null);
		FilterCriteriaPredicateBuilder<Customer> builder = builder(criteria);
		assertThatThrownBy(() -> repository.findAll(builder)).isExactlyInstanceOf(InvalidDataAccessApiUsageException.class);
	}
	

	@Test
	public void logical_and_works() {
		LogicalFilterCriteria criteria = new LogicalFilterCriteria(LogicalFilterOperator.AND);
		criteria.addChild(new ComparisonFilterCriteria("firstname", ComparisonFilterOperator.EQUAL_TO, "John"));
		criteria.addChild(new ComparisonFilterCriteria("lastname", ComparisonFilterOperator.EQUAL_TO, "Doe"));
		FilterCriteriaPredicateBuilder<Customer> builder = builder(criteria);
		assertThat(repository.findAll(builder)).hasSize(1);
	}

	@Test
	public void logical_or_works() {
		LogicalFilterCriteria criteria = new LogicalFilterCriteria(LogicalFilterOperator.OR);
		criteria.addChild(new ComparisonFilterCriteria("firstname", ComparisonFilterOperator.EQUAL_TO, "John"));
		criteria.addChild(new ComparisonFilterCriteria("lastname", ComparisonFilterOperator.EQUAL_TO, "Blige"));
		FilterCriteriaPredicateBuilder<Customer> builder = builder(criteria);
		assertThat(repository.findAll(builder)).hasSize(2);
	}
	
	@Test
	public void logical_without_comparison_throws() {
		LogicalFilterCriteria criteria = new LogicalFilterCriteria(LogicalFilterOperator.OR);
		FilterCriteriaPredicateBuilder<Customer> builder = builder(criteria);
		assertThatThrownBy(() -> repository.findAll(builder)).isExactlyInstanceOf(InvalidDataAccessApiUsageException.class);
	}
}
