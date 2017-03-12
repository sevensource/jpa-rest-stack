package org.sevensource.support.test.hibernate.unique;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.sevensource.support.jpa.hibernate.unique.UniquePropertyConstraint;
import org.sevensource.support.jpa.hibernate.unique.UniqueProperty;
import org.sevensource.support.jpa.hibernate.unique.UniqueValidation;
import org.sevensource.support.jpa.model.AbstractUUIDEntity;

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
