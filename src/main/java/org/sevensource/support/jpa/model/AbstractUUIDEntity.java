package org.sevensource.support.jpa.model;

import java.util.Objects;
import java.util.UUID;

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
	@Column(columnDefinition="uuid", updatable=false, unique=true, nullable=false)
	private UUID id;
	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
	
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof AbstractUUIDEntity)) {
        	return false;
        }
        if(! getClass().isAssignableFrom(o.getClass())) {
        	return false;
        }
//        if(getClass() != o.getClass()) {
//        	return false;
//        }
        AbstractUUIDEntity other = (AbstractUUIDEntity) o;
        if(getId() == null || other.getId() == null) {
        	return false;
        }
        boolean x = getId().equals(other.getId());
        return x;
        //return getId() != null && Objects.equals(getId(), other.getId());
    }
}
