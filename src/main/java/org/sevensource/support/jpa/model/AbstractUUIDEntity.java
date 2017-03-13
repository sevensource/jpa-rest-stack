package org.sevensource.support.jpa.model;

import java.util.UUID;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;
import org.sevensource.support.jpa.hibernate.UseUUIDIdOrGenerate;

@MappedSuperclass
public abstract class AbstractUUIDEntity extends AbstractPersistentEntity<UUID> { 

	@Id
	@GeneratedValue(generator=UseUUIDIdOrGenerate.NAME)
	@GenericGenerator(name=UseUUIDIdOrGenerate.NAME,
		strategy=UseUUIDIdOrGenerate.GENERATOR_CLASS)
	@Access(AccessType.PROPERTY)
	@Column(columnDefinition="uuid", updatable=false, unique=true, nullable=false)
	private UUID id;
	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
}
