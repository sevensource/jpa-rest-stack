package org.sevensource.support.jpa.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
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
        if (!(o instanceof AbstractPersistentEntity)) return false;
        AbstractPersistentEntity<?> other = (AbstractPersistentEntity<?>) o;
        return getId() != null && Objects.equals(getId(), other.getId());
    }

    @Override
    public final int hashCode() {
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
