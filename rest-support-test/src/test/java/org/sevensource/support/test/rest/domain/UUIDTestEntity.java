package org.sevensource.support.test.rest.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.sevensource.support.jpa.domain.AbstractUUIDEntity;
import org.sevensource.support.jpa.hibernate.unique.UniqueProperty;
import org.sevensource.support.jpa.hibernate.unique.UniquePropertyConstraint;
import org.sevensource.support.jpa.hibernate.unique.UniqueValidation;

@Entity
@UniquePropertyConstraint(groups={UniqueValidation.class})
public class UUIDTestEntity extends AbstractUUIDEntity {
	
	@Column(unique=true)
	@UniqueProperty
	@NotNull
	@Size(min=2, max=200)
	private String title;
	
	@OneToOne
	private UUIDTestReferenceEntity ref;
	
	protected UUIDTestEntity() {}
	
	public UUIDTestEntity(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public UUIDTestReferenceEntity getRef() {
		return ref;
	}
	
	public void setRef(UUIDTestReferenceEntity ref) {
		this.ref = ref;
	}
}
