package org.sevensource.support.rest.model;

import org.sevensource.support.jpa.domain.AbstractUUIDEntity;

public class ReferencingTestEntity extends AbstractUUIDEntity {

	private String name;
	private SimpleTestEntity reference;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public SimpleTestEntity getReference() {
		return reference;
	}
	public void setReference(SimpleTestEntity reference) {
		this.reference = reference;
	}

}
