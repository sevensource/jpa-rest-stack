package org.sevensource.support.jpa.filter.domain;

import java.time.Instant;
import java.time.ZonedDateTime;

import javax.persistence.Entity;

import org.sevensource.support.jpa.domain.AbstractIntegerEntity;

@Entity
public class Customer extends AbstractIntegerEntity {
	private String firstname;
	private String lastname;
	private CustomerType customerType;
	private int age;
	private Instant registered;
	private ZonedDateTime localTime;
	
	public Customer() {
		
	}
	
	public Customer(String firstname, String lastname, CustomerType customerType, int age, Instant registered, ZonedDateTime localTime) {
		super();
		this.firstname = firstname;
		this.lastname = lastname;
		this.customerType = customerType;
		this.age = age;
		this.registered = registered;
		this.localTime = localTime;
	}
	
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public CustomerType getCustomerType() {
		return customerType;
	}
	public void setCustomerType(CustomerType customerType) {
		this.customerType = customerType;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public ZonedDateTime getLocalTime() {
		return localTime;
	}
	public void setLocalTime(ZonedDateTime localTime) {
		this.localTime = localTime;
	}
	public Instant getRegistered() {
		return registered;
	}
	public void setRegistered(Instant registered) {
		this.registered = registered;
	}
}