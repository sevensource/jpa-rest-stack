package org.sevensource.support.jpa.hibernate.unique;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

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

        final UniqueConstraintList constraints = getConstraintDescriptors(entityClass, target);
    	final TypedQuery<Tuple> query = buildQuery(constraints, entityClass);
    	final Object resultId = executeQuery(query, entityClass);

    	if(resultId == null) {
    		return true;
    	}

    	final Object entityId = entityManagerFactory.getPersistenceUnitUtil().getIdentifier(target);

    	if(resultId.equals(entityId)) {
    		return true;
    	} else {
    		if (logger.isDebugEnabled()) {
				logger.debug("Validation failed - object returned by ValidationConstraint query does not equal to the one under validation");
			}

    		addConstraintViolation(context, constraints);
        	return false;
    	}
    }

    private UniqueConstraintList getConstraintDescriptors(Class<?> entityClass, Object target) {
        try {
        	final UniqueConstraintList constraintList = new UniqueConstraintList();

        	for(Field field : entityClass.getDeclaredFields()) {
        		final UniqueProperty a = AnnotationUtils.findAnnotation(field, UniqueProperty.class);

        		if(a != null) {
        			final String constraintGroupName = a.constraintGroup();
        			final String fieldName = field.getName();

        	        final PropertyDescriptor desc = new PropertyDescriptor(fieldName, entityClass);
        	        final Object fieldValue = desc.getReadMethod().invoke(target);

        	        final UniqueConstraint descriptor = new UniqueConstraint(fieldName, fieldValue, constraintGroupName);
        	        constraintList.addUniqueConstraint(descriptor);
        		}
        	}

        	return constraintList;
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | IntrospectionException e) {
			throw new IllegalArgumentException(e);
		}
    }

    private TypedQuery<Tuple> buildQuery(UniqueConstraintList constraints, Class<?> entityClass) {
    	CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
    	CriteriaQuery<Tuple> criteriaQuery = builder.createTupleQuery();
        Root<?> root = criteriaQuery.from(entityClass);

        Predicate[] predicates = constraints
        	.stream()
        	.map(constraintGroup -> {
        		Predicate[] groupPredicates = constraintGroup.getConstraints()
        			.stream()
        			.map(constraint -> builder.equal(root.get(constraint.field), constraint.value))
        			.toArray(Predicate[]::new);

        		return builder.and(groupPredicates);
        	})
        	.toArray(Predicate[]::new);

        logQuery(constraints, entityClass);

        String propertyName = getIdPropertyName(entityClass);

    	EntityManager em = entityManagerFactory.createEntityManager();
    	criteriaQuery = criteriaQuery.multiselect(root.get(propertyName));
    	criteriaQuery = criteriaQuery.where(builder.or(predicates));
    	return em.createQuery(criteriaQuery);
    }

    private Object executeQuery(TypedQuery<Tuple> query, Class<?> entityClass) {
    	try {
        	final Object resultId = query.getSingleResult().get(0);
        	return resultId;
        } catch(NoResultException nre) {
        	// no results, we're good to go
        	return null;
        } catch(NonUniqueResultException nure) {
        	final String msg = String.format("UniqueValidation query for class %s returned more than one result", entityClass.getName());
        	logger.error(msg);
        	throw new IllegalArgumentException(msg);
        }
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

    private void addConstraintViolation(ConstraintValidatorContext context, UniqueConstraintList constraintList) {

		final List<UniqueConstraint> constraints = constraintList.stream()
			.flatMap(constraintGroup -> constraintGroup.getConstraints().stream())
			.collect(Collectors.toList());


		NodeBuilderCustomizableContext nodeConstraintBuilder = null;

		for(UniqueConstraint constraint : constraints) {
			if(nodeConstraintBuilder == null) {
		    	final String msg = context.getDefaultConstraintMessageTemplate();
				final ConstraintViolationBuilder constraintBuilder = context.buildConstraintViolationWithTemplate(msg);
				nodeConstraintBuilder = constraintBuilder.addPropertyNode(constraint.field);
			} else {
				nodeConstraintBuilder = nodeConstraintBuilder.addPropertyNode(constraint.field);
			}
		}

		if(nodeConstraintBuilder != null) {
			nodeConstraintBuilder.addConstraintViolation().disableDefaultConstraintViolation();
		}
    }


    private static void logQuery(UniqueConstraintList constraintList, Class<?> entityClass) {
    	if (logger.isDebugEnabled()) {

			final String constraintDescription = constraintList.stream()
				.map(constraintGroup -> {
					return constraintGroup.getConstraints()
						.stream()
						.map(constraint -> String.format("%s='%s'", constraint.field, constraint.value))
						.collect(Collectors.joining(" AND ", "(", ")"));
				})
				.collect(Collectors.joining(" OR "));

			logger.debug("Validating UniqueConstraint [{}] for entity {}", constraintDescription, entityClass );
		}
    }

}