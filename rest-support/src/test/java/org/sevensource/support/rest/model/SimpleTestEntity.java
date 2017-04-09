package org.sevensource.support.rest.model;

import org.sevensource.support.jpa.domain.AbstractUUIDEntity;

public class SimpleTestEntity extends AbstractUUIDEntity {
	private String name;
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
}