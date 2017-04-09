package org.sevensource.support.rest.mapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.TargetType;
import org.sevensource.support.jpa.domain.PersistentEntity;
import org.sevensource.support.jpa.exception.EntityNotFoundException;
import org.sevensource.support.jpa.service.EntityService;
import org.sevensource.support.rest.dto.ReferenceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@MapperConfig(unmappedTargetPolicy=ReportingPolicy.IGNORE)
public class ReferenceDTOEntityMapper {
	
	@Autowired
	private List<EntityService<?, ? extends Serializable>> entityServices = new ArrayList<>();
	
	
    public <T extends PersistentEntity<UUID>> ReferenceDTO toDTO(T entity) {
        return entity != null ? new ReferenceDTO( entity.getId() ) : null;
    }
    
    public <T extends PersistentEntity<UUID>> T resolve(ReferenceDTO reference, @TargetType Class<T> entityClass) {
    	if(reference == null || reference.getId() == null) {
    		return null;
    	}
    	
    	EntityService<T, UUID> service = findEntityService(entityClass);
		T entity = service.get(reference.getId());
		
		if(entity == null) {
			final String message = String.format("%s with id %s does not exist", entityClass.getSimpleName(), reference.getId());
			throw new EntityNotFoundException(message);
		}
		return entity;
    }
    
	private <T extends PersistentEntity<UUID>> EntityService<T, UUID> findEntityService(Class<T> destinationClass) {
		for(EntityService<?, ?> s : entityServices) {
			if(s.supports(destinationClass)) {
				return (EntityService<T, UUID>) s;
			}
		}
		
		throw new IllegalStateException("Cannot find a service suitable for " + destinationClass);
	}
}
