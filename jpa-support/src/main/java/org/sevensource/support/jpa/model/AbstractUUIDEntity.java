package org.sevensource.support.jpa.model;

import java.time.Instant;
import java.util.UUID;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.sevensource.support.jpa.hibernate.UseUUIDIdOrGenerate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.util.ClassUtils;

@MappedSuperclass
public abstract class AbstractUUIDEntity extends AbstractPersistentEntity<UUID> { 

	@Id
	@GeneratedValue(generator=UseUUIDIdOrGenerate.NAME)
	@GenericGenerator(name=UseUUIDIdOrGenerate.NAME,
		strategy=UseUUIDIdOrGenerate.GENERATOR_CLASS)
	@Column(columnDefinition="uuid", updatable=false, unique=true, nullable=false)
	private UUID id = UUID.randomUUID();
	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
	
//	// IMPL with:
//	// private UUID id = UUID.randomUUID();
//    @Override
//    public final boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null) return false;
//		if (!getClass().equals(ClassUtils.getUserClass(o))) return false;
//		AbstractUUIDEntity other = (AbstractUUIDEntity) o;
//		
//		if(this.getId() == null || other.getId() == null) {
//			return EqualsBuilder.reflectionEquals(this, other, "id", "createdBy", "createdDate", "lastModifiedBy", "lastModifiedDate");
//		} else {
//			return this.getId().equals(other.getId());
//		}
//    }
//		
//		
//    @Override
//    public final int hashCode() {
//    	return getId().hashCode();
//    }
	
	
//	// IMPL No 2 with:
//	// private UUID id = UUID.randomUUID();
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
		if (!getClass().equals(ClassUtils.getUserClass(o))) return false;
		AbstractUUIDEntity other = (AbstractUUIDEntity) o;
		
		if(this.getId() == null || other.getId() == null) {
			return false;
		} else {
			return this.getId().equals(other.getId());
		}
    }
		
		
    @Override
    public final int hashCode() {
    	return getId().hashCode();
    }
}
