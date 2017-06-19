package org.sevensource.support.jpa.hibernate;

import java.io.Serializable;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentityGenerator;
import org.sevensource.support.jpa.domain.AbstractIntegerEntity;

public class AssignedIdentityGenerator extends IdentityGenerator {
 
	public static final String NAME = "assignedIdentityGenerator";
	public static final String GENERATOR_CLASS = "org.sevensource.support.jpa.hibernate.AssignedIdentityGenerator";
	
	@Override
	public Serializable generate(SharedSessionContractImplementor s, Object obj) {
		if(obj instanceof AbstractIntegerEntity) {
			Integer id = ((AbstractIntegerEntity) obj).getId();
			if( id != null) {
				return id;
			}
		}
		
		return super.generate(s, obj);
	}
}