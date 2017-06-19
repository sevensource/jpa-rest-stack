package org.sevensource.support.rest.dto;

import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using=ReferenceDTOSerializer.class)
@JsonDeserialize(using=ReferenceDTODeserializer.class)
public class ReferenceDTO implements IdentifiableDTO<UUID> {
	private UUID id;
	
	public ReferenceDTO(UUID uuid) {
		this.id = uuid;
	}
	
	@Override
	public UUID getId() {
		return id;
	}

//	@Override
//	public void setId(UUID id) {
//		this.id = id;
//	}
}
