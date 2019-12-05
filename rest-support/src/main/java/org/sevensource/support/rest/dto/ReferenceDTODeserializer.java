package org.sevensource.support.rest.dto;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public class ReferenceDTODeserializer extends StdDeserializer<ReferenceDTO> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(ReferenceDTODeserializer.class);

	public ReferenceDTODeserializer() {
        this(null);
    }
   
    public ReferenceDTODeserializer(Class<ReferenceDTO> t) {
        super(t);
    }
    
	@Override
	public ReferenceDTO deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		final String value = p.getValueAsString();
		
		if(StringUtils.isEmpty(value))
			return null;
		else {
			try {
				UUID id = UUID.fromString(value);
				return new ReferenceDTO(id);
			} catch(IllegalArgumentException e) {
				logger.error("Cannot parse value {} into UUID", value);
				throw new InvalidFormatException(p, "Cannot parse value as UUID", value, UUID.class);
			}
		}
	}
}