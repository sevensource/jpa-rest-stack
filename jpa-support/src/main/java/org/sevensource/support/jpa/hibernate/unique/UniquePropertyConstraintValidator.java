package org.sevensource.support.jpa.hibernate.unique;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

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


    @Override
    public void initialize(UniquePropertyConstraint constraintAnnotation) {
    	// nothing to do here
    }


    @Override
    public boolean isValid(Object target, ConstraintValidatorContext context) {
        Class<?> entityClass = target.getClass();

        ConstraintList constraints = null;
        try {
			constraints = getConstraintDescriptors(entityClass, target);
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | IntrospectionException e) {
			throw new IllegalArgumentException(e);
		}

    	TypedQuery<Tuple> query = buildQuery(entityClass, constraints);

        try {
        	final Object resultId = query.getSingleResult().get(0);
        	final Object entityId = entityManagerFactory.getPersistenceUnitUtil().getIdentifier(target);

        	if(resultId.equals(entityId)) {
        		if (logger.isTraceEnabled()) {
					logger.trace("Object returned by ValidationConstraint query is equal to the object under validation");
				}
        		return true;
        	} else {
        		if (logger.isDebugEnabled()) {
					logger.debug("Validation failed - object returned by ValidationConstraint query does not equal to the one under validation");
				}

        		addConstraintViolation(context, constraints);
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

    private ConstraintList getConstraintDescriptors(Class<?> entityClass, Object target)
    		throws IllegalAccessException, IntrospectionException, InvocationTargetException {

    	final ConstraintList constraints = new ConstraintList();

    	for(Field field : entityClass.getDeclaredFields()) {
    		final UniqueProperty a = AnnotationUtils.findAnnotation(field, UniqueProperty.class);

    		if(a != null) {
    			String constraintGroupName = a.constraintGroup();
    			if(! StringUtils.hasLength(constraintGroupName)) {
    				constraintGroupName = null;
    			}
    			final String name = field.getName();

    	        final PropertyDescriptor desc = new PropertyDescriptor(name, entityClass);
    	        final Method readMethod = desc.getReadMethod();
    	        final Object value = readMethod.invoke(target);

				final ConstraintDescriptorGroup group = constraints.getConstraintDescriptorGroup(constraintGroupName);
				final ConstraintDescriptor descriptor = new ConstraintDescriptor(name, value, constraintGroupName);

				if(group == null) {
					constraints.add(new ConstraintDescriptorGroup(constraintGroupName, descriptor));
				} else {
					group.getConstraints().add(descriptor);
				}
    		}
    	}

    	return constraints;
    }

    private TypedQuery<Tuple> buildQuery(Class<?> entityClass, ConstraintList constraints) {
    	CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = builder.createTupleQuery();
        Root<?> root = criteriaQuery.from(entityClass);

        Predicate[] predicates = new Predicate[constraints.size()];

        for(int c=0; c<constraints.size(); c++) {
        	ConstraintDescriptorGroup constraintGroup = constraints.get(c);

        	Predicate[] groupPredicates = new Predicate[constraintGroup.getConstraints().size()];
        	for(int i=0; i<constraintGroup.getConstraints().size(); i++) {
        		final ConstraintDescriptor constraint = constraintGroup.getConstraints().get(i);
        		groupPredicates[i] = builder.equal(root.get(constraint.field), constraint.value);
        	}
        	predicates[c] = builder.and(groupPredicates);
        }

        logQuery(constraints, entityClass);

        String propertyName = getIdPropertyName(entityClass);

    	EntityManager em = entityManagerFactory.createEntityManager();
    	criteriaQuery = criteriaQuery.multiselect(root.get(propertyName));
    	criteriaQuery = criteriaQuery.where(builder.or(predicates));
    	return em.createQuery(criteriaQuery);
    }


	private String getIdPropertyName(Class<?> entityClass) {
		String idPropertyName = null;
        for (SingularAttribute<?,?> sa : entityManagerFactory.getMetamodel().entity(entityClass).getSingularAttributes()) {
           if (sa.isId()) {
        	   Assert.isNull(idPropertyName, "Single @Id expected");
              idPropertyName = sa.getName();
           }
        }
        return idPropertyName;
	}

    private void addConstraintViolation(ConstraintValidatorContext context, ConstraintList constraints) {
    	final String msg = context.getDefaultConstraintMessageTemplate();
		ConstraintViolationBuilder constraintBuilder = context.buildConstraintViolationWithTemplate(msg);
		NodeBuilderCustomizableContext nodeConstraintBuilder = null;

		for(ConstraintDescriptorGroup constraintList : constraints) {
			for(ConstraintDescriptor cd : constraintList.getConstraints()) {
				if(nodeConstraintBuilder == null) {
					nodeConstraintBuilder = constraintBuilder.addPropertyNode(cd.field);
				} else {
					nodeConstraintBuilder = nodeConstraintBuilder.addPropertyNode(cd.field);
				}
			}
		}

		if(nodeConstraintBuilder != null) {
			nodeConstraintBuilder.addConstraintViolation().disableDefaultConstraintViolation();
		}
    }


    private static void logQuery(ConstraintList constraints, Class<?> entityClass) {
    	if (logger.isDebugEnabled()) {
			List<String> tmp = new ArrayList<>();

			for(int c=0; c<constraints.size(); c++) {
				ConstraintDescriptorGroup constraintGroup = constraints.get(c);

				String[] tmpx = new String[constraintGroup.getConstraints().size()];
				for(int i=0; i<constraintGroup.getConstraints().size(); i++) {
					final ConstraintDescriptor constraint = constraintGroup.getConstraints().get(i);
					tmpx[i] = String.format("%s='%s'", constraint.field, constraint.value);
				}

				tmp.add("(" + String.join(" AND ", tmpx) + ")");
			}

			logger.debug("Validating UniqueConstraint [{}] for entity {}", String.join(" OR ", tmp), entityClass );
		}
    }

}