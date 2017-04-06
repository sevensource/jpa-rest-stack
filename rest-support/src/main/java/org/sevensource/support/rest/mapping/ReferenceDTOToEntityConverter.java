package org.sevensource.support.rest.mapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.sevensource.support.jpa.domain.PersistentEntity;
import org.sevensource.support.jpa.service.EntityService;
import org.sevensource.support.rest.dto.ReferenceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReferenceDTOToEntityConverter<T extends PersistentEntity<? extends Serializable>> implements Converter<ReferenceDTO, T> {

	@Autowired(required=false)
	private List<EntityService<?, ? extends Serializable>> entityServices = new ArrayList<>();
	
	@Override
	public T convert(
			MappingContext<ReferenceDTO, T> context) {
		
		ReferenceDTO dto = context.getSource();
		if(dto == null || dto.getId() == null || dto.getId().toString().length() == 0) {
			return null;
		}
		
		Class<T> destinationClass = context.getDestinationType();
		EntityService<?, ?> service = getEntityService(destinationClass);
		
		if(dto.getId() instanceof UUID) {
			EntityService<?, UUID> s = (EntityService<?, UUID>) service;
			return (T) s.get(dto.getId());
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	private EntityService<T, ?> getEntityService(Class<T> destinationClass) {
		for(EntityService<?, ?> s : entityServices) {
			if(s.supports(destinationClass)) {
				return (EntityService<T, ?>) s;
			}
		}
		
		throw new IllegalArgumentException("Cannot find a service suitable for " + destinationClass);
	}
}
