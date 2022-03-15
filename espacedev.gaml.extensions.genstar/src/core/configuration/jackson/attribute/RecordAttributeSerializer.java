package core.configuration.jackson.attribute;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import core.metamodel.attribute.IAttribute;
import core.metamodel.attribute.MappedAttribute;
import core.metamodel.attribute.record.RecordAttribute;
import core.metamodel.value.IValue;

public class RecordAttributeSerializer extends StdSerializer<RecordAttribute<? extends IAttribute<? extends IValue>, 
		? extends IAttribute<? extends IValue>>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected RecordAttributeSerializer() {
		this(null);
	}
	
	protected RecordAttributeSerializer(
			Class<RecordAttribute<? extends IAttribute<? extends IValue>, ? extends IAttribute<? extends IValue>>> t) {
		super(t);
	}

	@Override
	public void serialize(
			RecordAttribute<? extends IAttribute<? extends IValue>, ? extends IAttribute<? extends IValue>> record,
			JsonGenerator gen, SerializerProvider sp) throws IOException {
		// DO NOTHING => delegate to #serializeWithType because of polymorphism
		
	}
	
	@Override
	public void serializeWithType(RecordAttribute<? extends IAttribute<? extends IValue>, ? extends IAttribute<? extends IValue>> record,
			JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
		
		gen.writeStartObject();
		gen.writeFieldName(typeSer.getTypeIdResolver().idFromValue(record));
		gen.writeStartObject();
		gen.writeStringField(IAttribute.NAME, record.getAttributeName());
		gen.writeStringField(RecordAttribute.PROXY_TYPE, record.getProxyAttribute().getValueSpace().getType().toString());
		gen.writeStringField(MappedAttribute.REF, record.getReferentAttribute().getAttributeName());
		gen.writeEndObject();
		gen.writeEndObject();
		
	}

}
