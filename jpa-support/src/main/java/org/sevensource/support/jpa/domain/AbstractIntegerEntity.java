package org.sevensource.support.jpa.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;
import org.sevensource.support.jpa.hibernate.AssignedIdentityGenerator;

@MappedSuperclass
public abstract class AbstractIntegerEntity extends AbstractPersistentEntity<Integer> {

	@Id
	@Column(updatable=false, unique=true, nullable=false)
	@GeneratedValue(generator=AssignedIdentityGenerator.NAME)
	@GenericGenerator(name=AssignedIdentityGenerator.NAME, strategy=AssignedIdentityGenerator.GENERATOR_CLASS)
	private Integer id;

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}
}
