package org.sevensource.support.jpa.hibernate.unique;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Validates UniqueConstraints by querying the underlying persistence layer in a new session
 * Cannot use the same session, because it may cause the JPA provider to flush and thereby violating 
 * a database UNIQUE constraint 
 * 
 * @author pgaschuetz
 *
 */
public class UniquePropertyConstraintValidator implements ConstraintValidator<UniquePropertyConstraint, Object> {

	private static final Logger logger = LoggerFactory.getLogger(UniquePropertyConstraintValidator.class);
	
	@Autowired
    private EntityManagerFactory entityManagerFactory;

    
    static class ConstraintDescriptor {
    	final String field;
    	final Object value;
    	final String group;
    	
    	ConstraintDescriptor(String field, Object value, String group) {
    		this.field = field;
    		this.value = value;
    		this.group = group;
		}
    }
	
    @Override
    public void initialize(UniquePropertyConstraint constraintAnnotation) {
    	if (logger.isDebugEnabled()) {
			logger.debug("Initializing UniqueConstraintValidator");
		} 
    }
    
    private List<List<ConstraintDescriptor>> getConstraintDescriptors(Class<?> entityClass, Object target)
    		throws IllegalArgumentException, IllegalAccessException, IntrospectionException, InvocationTargetException {
    	
    	final List<List<ConstraintDescriptor>> constraints = new ArrayList<>();
    	
    	for(Field field : entityClass.getDeclaredFields()) {
    		final UniqueProperty a = AnnotationUtils.findAnnotation(field, UniqueProperty.class);
    		
    		if(a != null) {
    			final String constraintGroup = a.constraintGroup();
    			final String name = field.getName();
    			
    	        PropertyDescriptor desc = new PropertyDescriptor(name, entityClass);
    	        Method readMethod = desc.getReadMethod();
    	        final Object value = readMethod.invoke(target);
    			
    			if(! StringUtils.hasLength(constraintGroup)) {
    				constraints.add( Arrays.asList(new ConstraintDescriptor(name, value, null)));
    			} else {
    				boolean added = false;
    				Iterator<List<ConstraintDescriptor>> it = constraints.iterator();
    				while(!added && it.hasNext()) {
    					List<ConstraintDescriptor> cl = it.next();
    					if(cl.size() > 0 && constraintGroup.equals(cl.get(0).group)) {
    						cl.add( new ConstraintDescriptor(name, value, constraintGroup));
    						added = true;
    					}
    				}
    				if(! added) {
    					constraints.add( Lists.newArrayList(new ConstraintDescriptor(name, value, constraintGroup)));
    				}
    			}
    		}
    	}
    	
    	return constraints;
    }
    

    @Override
    public boolean isValid(Object target, ConstraintValidatorContext context) {
        Class<?> entityClass = target.getClass();
        
        List<List<ConstraintDescriptor>> constraints = null;
        try {
			constraints = getConstraintDescriptors(entityClass, target);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException | IntrospectionException e) {
			throw new RuntimeException(e);
		}
        
        CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<?> criteriaQuery = builder.createQuery(entityClass);
        Root<?> root = criteriaQuery.from(entityClass);
        
        Predicate[] predicates = new Predicate[constraints.size()];
        
        for(int c=0; c<constraints.size(); c++) {
        	List<ConstraintDescriptor> constraintGroup = constraints.get(c);
        	
        	Predicate[] groupPredicates = new Predicate[constraintGroup.size()];
        	for(int i=0; i<constraintGroup.size(); i++) {
        		final ConstraintDescriptor constraint = constraintGroup.get(i);
        		groupPredicates[i] = builder.equal(root.get(constraint.field), constraint.value);
        	}
        	predicates[c] = builder.and(groupPredicates);
        }
        	
		if (logger.isDebugEnabled()) {
			List<String> tmp = new ArrayList<>();
			
			for(int c=0; c<constraints.size(); c++) {
				List<ConstraintDescriptor> constraintGroup = constraints.get(c);
				
				String[] tmpx = new String[constraintGroup.size()];
				for(int i=0; i<constraintGroup.size(); i++) {
					final ConstraintDescriptor constraint = constraintGroup.get(i);
					tmpx[i] = String.format("%s='%s'", constraint.field, constraint.value);
				}
				tmp.add("(" + Joiner.on(" AND ").join(tmpx) + ")");
			}
			
			logger.debug("Validating UniqueConstraint [{}] for entity {}", Joiner.on(" OR ").join(tmp), entityClass );
		}
        	
    	EntityManager em = entityManagerFactory.createEntityManager();
    	criteriaQuery = criteriaQuery.where(builder.or(predicates));
    	TypedQuery<?> query = em.createQuery(criteriaQuery);
            
            
        try {
        	Object o = query.getSingleResult();
        	if(o.equals(target)) {
        		if (logger.isTraceEnabled()) {
					logger.trace("Object returned by ValidationConstraint query is equal to the object under validation");
				}
        		return true;
        	} else {
        		if (logger.isDebugEnabled()) {
					logger.debug("Validation failed - object returned by ValidationConstraint query does not equal to the one under validation");
				} 
	        	return false;
        	}
        } catch(NoResultException nre) {
        	if (logger.isTraceEnabled()) {
				logger.trace("ValidationConstraint query returned no results");
			}
        	return true;
        } catch(NonUniqueResultException nure) {
        	String msg = String.format("UniqueValidation query for class %s returned more than one result", entityClass.getName());
        	logger.error(msg);
        	throw new IllegalArgumentException(msg);
        }
    }

}