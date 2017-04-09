package org.sevensource.support.rest.model;

import org.sevensource.support.rest.dto.AbstractUUIDDTO;

public class SimpleTestDTO extends AbstractUUIDDTO {
	private String name;
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
}