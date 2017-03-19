package org.sevensource.support.jpa.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import org.sevensource.support.jpa.hibernate.unique.UniquePropertyConstraint;
import org.sevensource.support.jpa.hibernate.unique.UniqueProperty;
import org.sevensource.support.jpa.hibernate.unique.UniqueValidation;
import org.sevensource.support.jpa.model.AbstractUUIDEntity;

@Entity
@UniquePropertyConstraint(groups={UniqueValidation.class})
public class OneUniqueEntity extends AbstractUUIDEntity {
	
	@Column(unique=true)
	@UniqueProperty
	private String title;
	

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
}
