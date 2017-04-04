package org.sevensource.support.jpa.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import org.sevensource.support.jpa.hibernate.unique.UniquePropertyConstraint;
import org.sevensource.support.jpa.domain.AbstractUUIDEntity;
import org.sevensource.support.jpa.hibernate.unique.UniqueProperty;
import org.sevensource.support.jpa.hibernate.unique.UniqueValidation;

@Entity
@UniquePropertyConstraint(groups={UniqueValidation.class})
public class TwoUniqueEntity extends AbstractUUIDEntity {
	
	@Column(unique=true)
	@UniqueProperty
	private String title;
	
	@Column(unique=true)
	@UniqueProperty
	private String name;
	
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
}
