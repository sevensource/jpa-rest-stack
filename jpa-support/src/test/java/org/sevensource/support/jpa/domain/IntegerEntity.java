package org.sevensource.support.jpa.domain;

import javax.persistence.Entity;

@Entity
public class IntegerEntity extends AbstractIntegerEntity {

	private String name;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
