package org.sevensource.support.jpa.domain;

import java.io.Serializable;
import java.time.Instant;

import org.springframework.data.domain.Persistable;

public interface PersistentEntity<ID extends Serializable> extends Persistable<ID> {
	public ID getId();
	public void setId(ID id);
	public Integer getVersion();
	public String getCreatedBy();
	public Instant getCreatedDate();
	public String getLastModifiedBy();
	public Instant getLastModifiedDate();
}
