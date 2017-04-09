package org.sevensource.support.test.rest.dto;

import org.sevensource.support.rest.dto.AbstractUUIDDTO;

public class TestRefDTO extends AbstractUUIDDTO {
	private String name;
	
	public String getName() {
		return name;
	}
	
	public void setName(String title) {
		this.name = title;
	}
}
