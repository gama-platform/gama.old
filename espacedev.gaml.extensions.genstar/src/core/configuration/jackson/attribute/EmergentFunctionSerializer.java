/*******************************************************************************************************
 *
 * EmergentFunctionSerializer.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package core.configuration.jackson.attribute;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import core.metamodel.attribute.MappedAttribute;
import core.metamodel.attribute.emergent.AggregateValueFunction;
import core.metamodel.attribute.emergent.CountValueFunction;
import core.metamodel.attribute.emergent.EntityValueFunction;
import core.metamodel.attribute.emergent.IGSValueFunction;
import core.metamodel.attribute.emergent.aggregator.IAggregatorValueFunction;
import core.metamodel.value.IValue;
import core.util.GSKeywords;

/**
 * The Class EmergentFunctionSerializer.
 */
public class EmergentFunctionSerializer extends StdSerializer<IGSValueFunction<?, ? extends IValue>> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new emergent function serializer.
	 */
	protected EmergentFunctionSerializer() {
		this(null);
	}

	/**
	 * Instantiates a new emergent function serializer.
	 *
	 * @param t the t
	 */
	protected EmergentFunctionSerializer(final Class<IGSValueFunction<?, ? extends IValue>> t) {
		super(t);
	}

	@Override
	public void serialize(final IGSValueFunction<?, ? extends IValue> arg0, final JsonGenerator arg1,
			final SerializerProvider arg2) throws IOException {
		// DO NOTHING => delegate to #serializeWithType because of polymorphism
	}

	@SuppressWarnings ({ "rawtypes", "unchecked" })
	@Override
	public void serializeWithType(final IGSValueFunction<?, ? extends IValue> function, final JsonGenerator gen,
			final SerializerProvider serializer, final TypeSerializer typeSer) throws IOException {

		String type = typeSer.getTypeIdResolver().idFromValue(function);

		// gen.writeFieldName(EmergentAttribute.FUNCTION);
		gen.writeStartObject();

		// TYPE
		gen.writeStringField(IGSValueFunction.ID, type);
		gen.writeStringField(MappedAttribute.REF, function.getReferent().getAttributeName());

		// AGG
		if (AggregateValueFunction.SELF.equals(type)) {
			gen.writeFieldName(AggregateValueFunction.AGG);
			gen.writeStartObject();
			gen.writeStringField(IAggregatorValueFunction.TYPE,
					typeSer.getTypeIdResolver().idFromValue(((AggregateValueFunction) function).getAggregator()));
			gen.writeEndObject();
		} else {
			Map<?, ? extends IValue> mapping = null;
			boolean identity = false;
			if (CountValueFunction.SELF.equals(type)) {
				mapping = ((CountValueFunction) function).getMapping();
			} else if (EntityValueFunction.SELF.equals(type)) {
				EntityValueFunction theFunction = (EntityValueFunction) function;
				mapping = theFunction.getMapping();
				identity = mapping.entrySet().stream().allMatch(e -> e.getKey().equals(e.getValue()));
			}
			gen.writeArrayFieldStart(IGSValueFunction.MAPPING);
			if (identity) {
				gen.writeString(GSKeywords.IDENTITY);
			} else if (mapping != null) {
				for (Entry<?, ? extends IValue> entry : mapping.entrySet()) {
					String k = entry.getKey().getClass().getSuperclass().equals(IValue.class)
							? ((IValue) entry.getKey()).getStringValue() : entry.getKey().toString();
					String v = entry.getValue().getStringValue();
					gen.writeString(k + GSKeywords.SERIALIZE_KEY_VALUE_SEPARATOR + v);
				}
			}
			gen.writeEndArray();
		}

		gen.writeEndObject();

	}

}
