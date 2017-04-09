package org.sevensource.support.rest.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.json.JacksonTester;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;


public class ReferenceDTOJacksonTest {

	private JacksonTester<ReferenceDTO> json;
	private ObjectMapper mapper = new ObjectMapper();
	
    @Before
    public void setup() {
        JacksonTester.initFields(this, mapper);
    }
    
    @Test
    public void serialize() throws IOException {
    	ReferenceDTO dto = new ReferenceDTO(UUID.randomUUID());
    	String expected = String.format("\"%s\"", dto.getId());
        assertThat(this.json.write(dto)).isEqualToJson(expected);
    }
    
    @Test
    public void deserialize() throws IOException {
    	UUID id = UUID.randomUUID();
    	String content = String.format("\"%s\"", id);
        assertThat(this.json.parse(content).getObject().getId())
        	.isEqualTo(id);
    }
    
    @Test
    public void deserialize_empty_value_should_be_null() throws IOException {
    	ReferenceDTO dto = mapper.readValue("\"\"", ReferenceDTO.class);
    	assertThat(dto).isNull();
    }
    
    @Test
    public void deserialize_null_value_should_be_null() throws IOException {
    	ReferenceDTO dto = mapper.readValue("null", ReferenceDTO.class);
    	assertThat(dto).isNull();
    }
    
    @Test(expected=InvalidFormatException.class)
    public void deserialize_invalid_value_should_throw() throws IOException {
    	ReferenceDTO dto = mapper.readValue("\"abcd\"", ReferenceDTO.class);
    	assertThat(dto).isNull();
    }
	
}
