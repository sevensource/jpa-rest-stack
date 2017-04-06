package org.sevensource.support.rest.mapping.model;

import org.sevensource.support.jpa.domain.AbstractUUIDEntity;

public class ReferencingTestSource extends AbstractUUIDEntity {

	private String name;
	private SimpleTestSource reference;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public SimpleTestSource getReference() {
		return reference;
	}
	public void setReference(SimpleTestSource reference) {
		this.reference = reference;
	}

}
