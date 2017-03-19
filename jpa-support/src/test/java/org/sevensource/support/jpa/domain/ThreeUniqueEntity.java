package org.sevensource.support.jpa.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import org.sevensource.support.jpa.hibernate.unique.UniquePropertyConstraint;
import org.sevensource.support.jpa.hibernate.unique.UniqueProperty;
import org.sevensource.support.jpa.hibernate.unique.UniqueValidation;
import org.sevensource.support.jpa.model.AbstractUUIDEntity;

@Entity
@UniquePropertyConstraint(groups={UniqueValidation.class})
public class ThreeUniqueEntity extends AbstractUUIDEntity {
	
	@Column(unique=true)
	@UniqueProperty
	private String title;
	
	@Column(unique=true)
	@UniqueProperty(constraintGroup="fullname")
	private String name;
	
	@Column(unique=true)
	@UniqueProperty(constraintGroup="fullname")
	private String lastname;
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getLastname() {
		return lastname;
	}
	
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
}
