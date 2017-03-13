package org.sevensource.support.jpa.model;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.ClassUtils;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Access(AccessType.FIELD)
public abstract class AbstractPersistentEntity<ID extends Serializable> implements PersistentEntity<ID> {

	@Version
	@Column(nullable=false)
	private Integer version;
	
	@CreatedBy
    @Column(nullable=false, updatable=false, length=100)
	private String createdBy;
	
	@CreatedDate
    @Column(nullable=false, updatable=false)
	private Instant createdDate;
	
	@LastModifiedBy
    @Column(nullable=false, length=100)
	private String lastModifiedBy;
	
	@LastModifiedDate
    @Column(nullable=false)
	private Instant lastModifiedDate;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
		if (!getClass().equals(ClassUtils.getUserClass(o))) return false;
		AbstractPersistentEntity<?> other = (AbstractPersistentEntity<?>) o;
		
		if(this.getId() == null && other.getId() == null) {
			return EqualsBuilder.reflectionEquals(this, other);
		} else if(this.getId() == null || other.getId() == null) {
			return false;
		} else {
			return this.getId().equals(other.getId());
		}
    }

    @Override
    public int hashCode() {
    	// always return the same hashCode
    	// although this decreases performance for large hash tables,
    	// this way the JPA contract is correctly followed
        return 31;
    }

	public Integer getVersion() {
		return version;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public Instant getCreatedDate() {
		return createdDate;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public Instant getLastModifiedDate() {
		return lastModifiedDate;
	}
}
