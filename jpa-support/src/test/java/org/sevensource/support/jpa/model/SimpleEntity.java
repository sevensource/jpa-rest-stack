package org.sevensource.support.jpa.model;

import javax.persistence.Entity;

import org.sevensource.support.jpa.model.AbstractUUIDEntity;

@Entity
public class SimpleEntity extends AbstractUUIDEntity {

	private String name;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
