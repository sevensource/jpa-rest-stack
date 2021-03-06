package org.sevensource.support.rest.model;

import org.sevensource.support.rest.dto.AbstractUUIDDTO;
import org.sevensource.support.rest.dto.ReferenceDTO;

public class ReferencingTestDTO extends AbstractUUIDDTO {
	private String name;
	private ReferenceDTO reference;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ReferenceDTO getReference() {
		return reference;
	}
	public void setReference(ReferenceDTO reference) {
		this.reference = reference;
	}
	
	
}
