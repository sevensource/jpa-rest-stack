package org.sevensource.support.rest.dto;

import java.util.UUID;

public class ReferenceDTO implements IdentifiableDTO<UUID> {
	private UUID id;
	public UUID getId() {
		return id;
	}
	
	public void setId(UUID id) {
		this.id = id;
	}
}
