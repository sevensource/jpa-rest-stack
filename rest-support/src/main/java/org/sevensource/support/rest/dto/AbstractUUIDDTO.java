package org.sevensource.support.rest.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

public abstract class AbstractUUIDDTO implements IdentifiableDTO<UUID> {
	
	private UUID id;
	
	@JsonProperty(access=Access.READ_ONLY)
	private Integer version;
	
	@Override
	public UUID getId() {
		return id;
	}

	@Override
	public void setId(UUID id) {
		this.id = id;
	}
	
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}
