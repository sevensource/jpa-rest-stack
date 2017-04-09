package org.sevensource.support.test.jpa.domain;

import javax.persistence.Entity;

import org.sevensource.support.jpa.domain.AbstractUUIDEntity;

@Entity
public class UUIDTestReferenceEntity extends AbstractUUIDEntity {
	private String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
