package org.sevensource.support.jpa.domain;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;
import org.sevensource.support.jpa.hibernate.UseUUIDIdOrGenerate;
import org.springframework.util.ClassUtils;

@MappedSuperclass
public abstract class AbstractUUIDEntity extends AbstractPersistentEntity<UUID> { 

	@Id
	@GeneratedValue(generator=UseUUIDIdOrGenerate.NAME)
	@GenericGenerator(name=UseUUIDIdOrGenerate.NAME,
		strategy=UseUUIDIdOrGenerate.GENERATOR_CLASS)
	@Column(columnDefinition="uuid", updatable=false, unique=true, nullable=false)
	private UUID id = UUID.randomUUID();
	
	@Override
	public UUID getId() {
		return id;
	}

	@Override
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
    	return (id == null) ? super.hashCode() : id.hashCode();
    }
}
