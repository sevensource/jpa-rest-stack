package org.sevensource.support.jpa.domain;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostPersist;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.sevensource.support.jpa.hibernate.AssignedUUIDGenerator;
import org.springframework.util.ClassUtils;

@MappedSuperclass
public abstract class AbstractUUIDEntity extends AbstractPersistentEntity<UUID> { 

	private static final long serialVersionUID = 7109098772597401810L;

	@Transient
	private transient UUID _id = UUID.randomUUID();
	
	@Id
	@GeneratedValue(generator=AssignedUUIDGenerator.NAME)
	@GenericGenerator(name=AssignedUUIDGenerator.NAME,
		strategy=AssignedUUIDGenerator.GENERATOR_CLASS)
	@Column(columnDefinition="uuid", updatable=false, unique=true, nullable=false)
	private UUID id = _id;
	
	@Override
	public UUID getId() {
		return id;
	}

	@Override
	public void setId(UUID id) {
		this.id = id;
	}
	
	@PostPersist
	public void postPersist() {
		this._id = null;
	}
	
	@Transient
	@Override
	public boolean isNew() {
		if(id == null) return true;
		return id.equals(_id);
	}
	
    @Override
    public final boolean equals(Object o) {
        if (this == o) {
        	return true;
        }
        else if (o == null) {
        	return false;
        }
        else if (!getClass().equals(ClassUtils.getUserClass(o))) {
        	return false;
        }
        
		AbstractUUIDEntity other = (AbstractUUIDEntity) o;
		
		if(this.getId() == null || other.getId() == null) {
			return false;
		} else {
			return this.getId().equals(other.getId());
		}
    }
		
		
    @Override
    public final int hashCode() {
    	return (id == null) ? super.hashCode() : id.hashCode();
    }
}
