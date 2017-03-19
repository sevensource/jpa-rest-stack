package org.sevensource.support.jpa.model;

import java.io.Serializable;
import java.time.Instant;

public interface PersistentEntity<ID extends Serializable> {
	public ID getId();
	public void setId(ID id);
	public Integer getVersion();
	public String getCreatedBy();
	public Instant getCreatedDate();
	public String getLastModifiedBy();
	public Instant getLastModifiedDate();
}
