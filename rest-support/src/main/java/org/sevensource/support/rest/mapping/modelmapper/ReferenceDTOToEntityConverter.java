package org.sevensource.support.rest.mapping.modelmapper;
//package org.sevensource.support.rest.mapping;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//import javax.persistence.EntityNotFoundException;
//
//import org.modelmapper.Converter;
//import org.modelmapper.spi.MappingContext;
//import org.sevensource.support.jpa.domain.PersistentEntity;
//import org.sevensource.support.jpa.service.EntityService;
//import org.sevensource.support.rest.dto.ReferenceDTO;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class ReferenceDTOToEntityConverter<T extends PersistentEntity<? extends Serializable>> implements Converter<ReferenceDTO, T> {
//
//	@Autowired(required=false)
//	private List<EntityService<?, ? extends Serializable>> entityServices = new ArrayList<>();
//	
//	@Override
//	public T convert(
//			MappingContext<ReferenceDTO, T> context) {
//		
//		ReferenceDTO dto = context.getSource();
//		if(dto == null || dto.getId() == null || dto.getId().toString().length() == 0) {
//			return null;
//		}
//		
//		Class<T> destinationClass = context.getDestinationType();
//		EntityService<?, ?> service = getEntityService(destinationClass);
//		
//		if(dto.getId() instanceof UUID) {
//			EntityService<?, UUID> s = (EntityService<?, UUID>) service;
//			T entity = (T) s.get(dto.getId());
//			if(entity == null) {
//				final String message = String.format("%s with id %s does not exist", destinationClass.getSimpleName(), dto.getId());
//				throw new EntityNotFoundException(message);
//			}
//			return entity;
//		} else {
//			throw new IllegalStateException("Don't know how to handle id of type " + dto.getId().getClass());
//		}
//	}
//	
//	private EntityService<T, ?> getEntityService(Class<T> destinationClass) {
//		for(EntityService<?, ?> s : entityServices) {
//			if(s.supports(destinationClass)) {
//				return (EntityService<T, ?>) s;
//			}
//		}
//		
//		throw new IllegalStateException("Cannot find a service suitable for " + destinationClass);
//	}
//}
