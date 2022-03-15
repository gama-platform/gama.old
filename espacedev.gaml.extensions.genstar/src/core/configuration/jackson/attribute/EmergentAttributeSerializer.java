package core.configuration.jackson.attribute;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import core.metamodel.attribute.EmergentAttribute;
import core.metamodel.attribute.IAttribute;
import core.metamodel.value.IValue;

public class EmergentAttributeSerializer extends StdSerializer<EmergentAttribute<? extends IValue, ?, ?>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String EMPTY_REFERENT = "NONE";

	public EmergentAttributeSerializer() {
		this(null);
	}
	
	protected EmergentAttributeSerializer(Class<EmergentAttribute<? extends IValue, ?, ?>> t) {
		super(t);
	}

	@Override
	public void serialize(
			EmergentAttribute<? extends IValue, ?, ?> attribute,
			JsonGenerator gen, SerializerProvider provider) throws IOException {
		// DO NOTHING
	}
	
	@Override
	public void serializeWithType(EmergentAttribute<? extends IValue, ?, ?> attribute,
			JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
		
		gen.writeStartObject();
		gen.writeFieldName(typeSer.getTypeIdResolver().idFromValue(attribute));
		gen.writeStartObject();
		gen.writeStringField(IAttribute.NAME, attribute.getAttributeName());
		
		//gen.writeObjectFieldStart(EmergentAttribute.FUNCTION);
		EmergentFunctionSerializer efs = new EmergentFunctionSerializer();
		efs.serializeWithType(attribute.getFunction(), gen, serializers, typeSer);
		//gen.writeEndObject();
		
		//gen.writeObjectFieldStart(EmergentAttribute.TRANSPOSER);
		EmergentTransposerSerializer ets = new EmergentTransposerSerializer();
		ets.serializeWithType(attribute.getTransposer(), gen, serializers, typeSer);
		//gen.writeEndObject();
		
		gen.writeEndObject();
		
	}

}
