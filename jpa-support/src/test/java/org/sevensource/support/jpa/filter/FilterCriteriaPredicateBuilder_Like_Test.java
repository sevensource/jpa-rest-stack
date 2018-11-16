package org.sevensource.support.jpa.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = JpaTestConfiguration.class)
@EntityScan(basePackageClasses=Customer.class)
@EnableJpaRepositories(basePackageClasses=CustomerRepository.class)
public class FilterCriteriaPredicateBuilder_Like_Test {

	Customer person1;
	Customer person2;
	Customer person3;
	Customer company;
	Customer enterprise;
	
	@Before
	public void before() {
		repository.deleteAllInBatch();
		person1 = repository.save(new Customer("John", "Doe", CustomerType.PERSON, 35));
		person2 = repository.save(new Customer("Mary", "Blige", CustomerType.PERSON, 35));
		person3 = repository.save(new Customer(null, "Marks", null, 15));
		company = repository.save(new Customer("Michael", "Huntington", CustomerType.COMPANY, 50));
		enterprise = repository.save(new Customer("Bill", "Gates", CustomerType.COMPANY, 65));
		repository.flush();
	}
	
	@Autowired
	CustomerRepository repository;
	
	@Test
	public void string_like() {		
		FilterCriteria criteria = new ComparisonFilterCriteria("firstname", ComparisonFilterOperator.LIKE, "Joh*");
		FilterCriteriaPredicateBuilder<Customer> builder = new FilterCriteriaPredicateBuilder<>(criteria, new DefaultConversionService());
		assertThat(repository.findAll(builder)).hasSize(1);
	}
	
	@Test
	public void string_like_with_integer() {		
		FilterCriteria criteria = new ComparisonFilterCriteria("firstname", ComparisonFilterOperator.LIKE, 2);
		FilterCriteriaPredicateBuilder<Customer> builder = new FilterCriteriaPredicateBuilder<>(criteria, new DefaultConversionService());
		assertThat(repository.findAll(builder)).hasSize(0);
	}
	
	@Test
	public void string_like_with_null() {		
		FilterCriteria criteria = new ComparisonFilterCriteria("firstname", ComparisonFilterOperator.LIKE, null);
		FilterCriteriaPredicateBuilder<Customer> builder = new FilterCriteriaPredicateBuilder<>(criteria, new DefaultConversionService());
		assertThat(repository.findAll(builder)).hasSize(1);
	}
		
	@Test
	public void integer_like_throws() {		
		FilterCriteria criteria = new ComparisonFilterCriteria("age", ComparisonFilterOperator.LIKE, 35);
		FilterCriteriaPredicateBuilder<Customer> builder = new FilterCriteriaPredicateBuilder<>(criteria, new DefaultConversionService());
		assertThatThrownBy(() -> repository.findAll(builder)).isExactlyInstanceOf(InvalidDataAccessApiUsageException.class);
	}
	
	@Test
	public void enum_like_throws() {
		FilterCriteria criteria = new ComparisonFilterCriteria("customerType", ComparisonFilterOperator.LIKE, CustomerType.PERSON);
		FilterCriteriaPredicateBuilder<Customer> builder = new FilterCriteriaPredicateBuilder<>(criteria, new DefaultConversionService());
		assertThatThrownBy(() -> repository.findAll(builder)).isExactlyInstanceOf(InvalidDataAccessApiUsageException.class);
	}
	
	@Test
	public void string_notlike() {		
		FilterCriteria criteria = new ComparisonFilterCriteria("firstname", ComparisonFilterOperator.NOT_LIKE, "Joh*");
		FilterCriteriaPredicateBuilder<Customer> builder = new FilterCriteriaPredicateBuilder<>(criteria, new DefaultConversionService());
		assertThat(repository.findAll(builder)).hasSize(4);
	}
}
