package org.sevensource.support.rest.dto;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ReferenceDTOSerializer extends StdSerializer<ReferenceDTO> {
	private static final long serialVersionUID = 1L;

	public ReferenceDTOSerializer() {
        this(null);
    }
   
    public ReferenceDTOSerializer(Class<ReferenceDTO> t) {
        super(t);
    }

	@Override
	public void serialize(ReferenceDTO value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeString(value.getId().toString());
	}	
}