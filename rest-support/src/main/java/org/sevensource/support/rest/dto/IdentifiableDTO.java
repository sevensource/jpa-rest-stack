package org.sevensource.support.rest.dto;

import java.io.Serializable;

public interface IdentifiableDTO<ID extends Serializable> {
	ID getId();
	void setId(ID id);
}
