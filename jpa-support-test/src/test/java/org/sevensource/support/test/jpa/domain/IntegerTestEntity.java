package org.sevensource.support.test.jpa.domain;

import javax.persistence.Entity;

import org.sevensource.support.jpa.domain.AbstractIntegerEntity;

@Entity
public class IntegerTestEntity extends AbstractIntegerEntity {

	private String name;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
