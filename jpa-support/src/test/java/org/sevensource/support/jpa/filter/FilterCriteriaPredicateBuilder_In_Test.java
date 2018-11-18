package org.sevensource.support.jpa.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sevensource.support.jpa.configuration.JpaTestConfiguration;
import org.sevensource.support.jpa.filter.domain.Customer;
import org.sevensource.support.jpa.filter.domain.CustomerRepository;
import org.sevensource.support.jpa.filter.domain.CustomerType;
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
public class FilterCriteriaPredicateBuilder_In_Test {

	Customer person1;
	Customer person2;
	Customer person3;
	Customer company;
	Customer enterprise;
	
	final Instant instant0 = Instant.ofEpochMilli(0);
	final Instant instant2000 = ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.of("Z")).toInstant();
	
	final ZonedDateTime dateTime1900 = ZonedDateTime.of(1900, 12, 24, 20, 0, 0, 0, ZoneId.of("Z"));
	final ZonedDateTime dateTime2000 = ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.of("Z"));
	
	@Before
	public void before() {
		
		repository.deleteAllInBatch();
		person1 = repository.save(new Customer("John", "Doe", CustomerType.PERSON, 35, instant0, dateTime1900));
		person2 = repository.save(new Customer("Mary", "Blige", CustomerType.PERSON, 35, instant0, dateTime1900));
		person3 = repository.save(new Customer(null, "Marks", null, 15, null, null));
		company = repository.save(new Customer("Michael", "Huntington", CustomerType.COMPANY, 50, instant2000, dateTime2000));
		enterprise = repository.save(new Customer("Bill", "Gates", CustomerType.COMPANY, 65, instant2000, dateTime2000));
		repository.flush();
	}
	
	@Autowired
	CustomerRepository repository;
	
	FilterCriteriaPredicateBuilder<Customer> builder(FilterCriteria criteria) {
		return new FilterCriteriaPredicateBuilder<>(criteria);
	}
	
	@Test
	public void string_in() {
		FilterCriteria criteria = new ComparisonFilterCriteria("firstname", ComparisonFilterOperator.IN, Arrays.asList("John", "Mary"));
		FilterCriteriaPredicateBuilder<Customer> builder = builder(criteria);
		assertThat(repository.findAll(builder)).hasSize(2);
	}
	
	@Test
	public void integer_in() {
		FilterCriteria criteria = new ComparisonFilterCriteria("age", ComparisonFilterOperator.IN, Arrays.asList(35, 50, 10));
		FilterCriteriaPredicateBuilder<Customer> builder = builder(criteria);
		assertThat(repository.findAll(builder)).hasSize(3);
	}
	
	@Test
	public void instant_in() {
		FilterCriteria criteria = new ComparisonFilterCriteria("registered", ComparisonFilterOperator.IN, Arrays.asList(instant0, Instant.now()));
		FilterCriteriaPredicateBuilder<Customer> builder = builder(criteria);
		assertThat(repository.findAll(builder)).hasSize(2);
	}
	
	@Test
	public void in_with_null_args_throws() {		
		FilterCriteria criteria = new ComparisonFilterCriteria("age", ComparisonFilterOperator.IN, null);
		FilterCriteriaPredicateBuilder<Customer> builder = builder(criteria);
		assertThatThrownBy(() -> repository.findAll(builder)).isExactlyInstanceOf(InvalidDataAccessApiUsageException.class);
	}
	
	@Test
	public void in_with_non_collection_arg_works() {		
		FilterCriteria criteria = new ComparisonFilterCriteria("registered", ComparisonFilterOperator.IN, instant0);
		FilterCriteriaPredicateBuilder<Customer> builder = builder(criteria);
		assertThat(repository.findAll(builder)).hasSize(2);
	}
	
	
	@Test
	public void string_not_in() {
		FilterCriteria criteria = new ComparisonFilterCriteria("firstname", ComparisonFilterOperator.NOT_IN, Arrays.asList("John", "Mary"));
		FilterCriteriaPredicateBuilder<Customer> builder = builder(criteria);
		assertThat(repository.findAll(builder)).hasSize(3);
	}
	
	@Test
	public void integer_not_in() {
		FilterCriteria criteria = new ComparisonFilterCriteria("age", ComparisonFilterOperator.NOT_IN, Arrays.asList(35, 50, 10));
		FilterCriteriaPredicateBuilder<Customer> builder = builder(criteria);
		assertThat(repository.findAll(builder)).hasSize(2);
	}
	
	@Test
	public void instant_not_in() {
		FilterCriteria criteria = new ComparisonFilterCriteria("registered", ComparisonFilterOperator.NOT_IN, Arrays.asList(instant0, Instant.now()));
		FilterCriteriaPredicateBuilder<Customer> builder = builder(criteria);
		assertThat(repository.findAll(builder)).hasSize(3);
	}
	
	@Test
	public void not_in_with_null_args_throws() {		
		FilterCriteria criteria = new ComparisonFilterCriteria("age", ComparisonFilterOperator.NOT_IN, null);
		FilterCriteriaPredicateBuilder<Customer> builder = builder(criteria);
		assertThatThrownBy(() -> repository.findAll(builder)).isExactlyInstanceOf(InvalidDataAccessApiUsageException.class);
	}
	
	@Test
	public void not_in_with_non_collection_arg_works() {		
		FilterCriteria criteria = new ComparisonFilterCriteria("registered", ComparisonFilterOperator.NOT_IN, instant0);
		FilterCriteriaPredicateBuilder<Customer> builder = builder(criteria);
		assertThat(repository.findAll(builder)).hasSize(3);
	}
	
}
