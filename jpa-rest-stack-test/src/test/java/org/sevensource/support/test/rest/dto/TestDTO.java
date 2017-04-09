package org.sevensource.support.test.rest.dto;

import org.sevensource.support.rest.dto.AbstractUUIDDTO;

public class TestDTO extends AbstractUUIDDTO {
	private String title;
	private TestRefDTO ref;
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public TestRefDTO getRef() {
		return ref;
	}
	
	public void setRef(TestRefDTO ref) {
		this.ref = ref;
	}
}
