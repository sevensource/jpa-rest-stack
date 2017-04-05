package org.sevensource.support.jpa.domain;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.springframework.core.style.ToStringCreator;
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

	private static final long serialVersionUID = 4692410111632259138L;

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
	@Transient
	public boolean isNew() {
		return getId() == null;
	}
	
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
		if (!getClass().equals(ClassUtils.getUserClass(o))) return false;
		AbstractPersistentEntity<?> other = (AbstractPersistentEntity<?>) o;
		
		if(this.getId() == null || other.getId() == null) {
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
        //return 31;
    	
		int hashCode = 17;
		hashCode += null == getId() ? 0 : getId().hashCode() * 31;
		return hashCode;
    }

	@Override
	public Integer getVersion() {
		return version;
	}

	@Override
	public String getCreatedBy() {
		return createdBy;
	}

	@Override
	public Instant getCreatedDate() {
		return createdDate;
	}

	@Override
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	@Override
	public Instant getLastModifiedDate() {
		return lastModifiedDate;
	}
	
	@Override
	public String toString() {
		return new ToStringCreator(this)
			.append("id", getId())
			.append("version", getVersion())
			.toString();
	}
}
