package core.configuration.jackson.entity;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import core.metamodel.attribute.IAttribute;
import core.metamodel.entity.matcher.AttributeVectorMatcher;
import core.metamodel.entity.matcher.IGSEntityMatcher;
import core.metamodel.entity.matcher.TagMatcher;
import core.metamodel.value.IValue;

public class EntityMatcherSerializer extends StdSerializer<IGSEntityMatcher<?>> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected EntityMatcherSerializer() {
		this(null);
	}
	
	protected EntityMatcherSerializer(Class<IGSEntityMatcher<?>> t) {
		super(t);
	}

	@Override
	public void serialize(IGSEntityMatcher<?> value, JsonGenerator gen, SerializerProvider provider)
			throws IOException {
		
		
	}
	
	@Override
	public void serializeWithType(IGSEntityMatcher<?> matcher, 
			JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
		
		gen.writeStartObject();
		final String mType = typeSer.getTypeIdResolver().idFromValue(matcher);
		gen.writeStringField(IGSEntityMatcher.TYPE_LABEL, mType);
		
		gen.writeArrayFieldStart(IGSEntityMatcher.VECTOR_LABEL);
		switch (mType) {
		case AttributeVectorMatcher.SELF:
			for(Entry<IAttribute<? extends IValue>, Set<IValue>> entry : 
				((AttributeVectorMatcher) matcher).getMapVector().entrySet()) {
				gen.writeString(entry.getKey().getAttributeName()
						+AttributeVectorMatcher.ATT_SEPRATOR
						+entry.getValue().stream()
							.map(v -> v.getStringValue())
							.collect(Collectors.joining(AttributeVectorMatcher.VAL_SEPRATOR))
						);
			}
			break;
		case TagMatcher.SELF:
		default:
			for(Object value : matcher.getVector())
				gen.writeString(value.toString());
			break;
		}
		gen.writeEndArray();
		gen.writeEndObject();
		
	}

}
