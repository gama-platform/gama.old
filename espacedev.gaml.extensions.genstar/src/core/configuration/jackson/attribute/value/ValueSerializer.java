package core.configuration.jackson.attribute.value;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import core.metamodel.value.IValue;

public class ValueSerializer extends StdSerializer<IValue> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected ValueSerializer() {
		this(null);
	}
	
	protected ValueSerializer(Class<IValue> t) {
		super(t);
	}

	@Override
	public void serialize(IValue value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStartObject();
		gen.writeStringField(IValue.VALUE_LABEL, value.getStringValue());
		gen.writeEndObject();		
	}

}
