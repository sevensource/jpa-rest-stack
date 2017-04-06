package org.sevensource.support.rest.mapping.model;

import org.sevensource.support.jpa.domain.AbstractUUIDEntity;

public class SimpleTestSource extends AbstractUUIDEntity {
	private String name;
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
}