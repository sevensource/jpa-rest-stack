package org.sevensource.support.jpa.hibernate;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.UUIDGenerator;
import org.sevensource.support.jpa.domain.AbstractUUIDEntity;

/**
 * UUID ID generator, which preserves existing IDs instead of always generating a new one.
 * 
 * @author pgaschuetz
 *
 */
public class UseUUIDIdOrGenerate extends UUIDGenerator {
	
	public final static String NAME = "useUuidIdOrGenerate";
	public final static String GENERATOR_CLASS = "org.sevensource.support.jpa.hibernate.UseUUIDIdOrGenerate";
	
	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
		if(object instanceof AbstractUUIDEntity) {
			UUID id = ((AbstractUUIDEntity) object).getId();
			if( id != null) {
				return id;
			}
		}
		
		return super.generate(session, object);
	}
}