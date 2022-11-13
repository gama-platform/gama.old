package core.configuration.jackson.attribute;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import core.metamodel.attribute.emergent.filter.IGSEntitySelector;

public class EmergentTransposerSerializer extends StdSerializer<IGSEntitySelector<?,?>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected EmergentTransposerSerializer() {
		this(null);
	}

	protected EmergentTransposerSerializer(Class<IGSEntitySelector<?, ?>> t) {
		super(t);
	}

	@Override
	public void serialize(IGSEntitySelector<?,?> arg0, JsonGenerator arg1, SerializerProvider arg2) throws IOException {
		
		
	}
	
	@Override
	public void serializeWithType(IGSEntitySelector<?, ?> transposer,
			JsonGenerator gen, SerializerProvider serializer, TypeSerializer typeSer) throws IOException {
		
		//gen.writeFieldName(EmergentAttribute.TRANSPOSER);
		gen.writeStartObject();
		gen.writeStringField(IGSEntitySelector.TYPE, typeSer.getTypeIdResolver().idFromValue(transposer));
		
		gen.writeObjectField(IGSEntitySelector.COMPARATOR, transposer.getComparator());
		
		/*
		gen.writeFieldName(IGSEntityTransposer.COMPARATOR);
		EntityComparatorSerializer ecs = new EntityComparatorSerializer();
		ecs.serialize(transposer.getComparator(), gen, serializer);
		*/
		
		gen.writeStringField(IGSEntitySelector.MATCH_TYPE, transposer.getMatchType().toString());
		
		gen.writeObjectField(IGSEntitySelector.MATCHERS, transposer.getMatcher());
		
		/*
		gen.writeFieldName(IGSEntityTransposer.MATCHERS);
		EntityMatcherSerializer ems = new EntityMatcherSerializer();
		ems.serializeWithType(transposer.getMatcher(), gen, serializer, typeSer);
		*/
		
		gen.writeEndObject();
		
		
	}

}
