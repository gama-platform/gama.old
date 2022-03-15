package core.configuration.jackson.entity;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import core.metamodel.attribute.IAttribute;
import core.metamodel.entity.comparator.HammingEntityComparator;
import core.metamodel.entity.comparator.ImplicitEntityComparator;
import core.metamodel.entity.comparator.function.IComparatorFunction;
import core.metamodel.entity.matcher.AttributeVectorMatcher;
import core.metamodel.entity.matcher.IGSEntityMatcher;
import core.metamodel.value.IValue;
import core.util.GSKeywords;
import core.util.data.GSEnumDataType;

public class EntityComparatorSerializer extends StdSerializer<ImplicitEntityComparator> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected EntityComparatorSerializer() {
		this(null);
	}

	protected EntityComparatorSerializer(Class<ImplicitEntityComparator> t) {
		super(t);
	}

	@Override
	public void serialize(ImplicitEntityComparator comparator,
			JsonGenerator gen, SerializerProvider serPro) throws IOException {
		List<IComparatorFunction<? extends IValue>> customFunctions = Stream.of(GSEnumDataType.values())
				.map(t -> comparator.getComparatorFunction(t))
				.filter(function -> function.getName().startsWith(IComparatorFunction.CUSTOM_TAG))
				.collect(Collectors.toList());
		List<IAttribute<? extends IValue>> attributes = comparator.getAttributes();
		AttributeVectorMatcher avm = comparator instanceof HammingEntityComparator ? 
				((HammingEntityComparator) comparator).getVectorMatcher() : null;
				
		gen.writeStartObject();
				
		if(customFunctions.isEmpty() && attributes.isEmpty() && (avm == null || avm.getMapVector().isEmpty())) {
			gen.writeStringField(GSKeywords.CONTENT, GSKeywords.DEFAULT);
		} else {

			gen.writeArrayFieldStart(ImplicitEntityComparator.ATTRIBUTES_REF);
			for(IAttribute<? extends IValue> attribute : attributes)
				gen.writeString(attribute.getAttributeName()+GSKeywords.SERIALIZE_KEY_VALUE_SEPARATOR+comparator.isReverseAttribute(attribute));
			gen.writeEndArray();
			gen.writeArrayFieldStart(ImplicitEntityComparator.COMP_FUNCTIONS);
			for(IComparatorFunction<? extends IValue> function : customFunctions)
				gen.writeObject(function);
			gen.writeEndArray();

			if(comparator instanceof HammingEntityComparator) {
				gen.writeFieldName(HammingEntityComparator.HAMMING_VECTOR);
				EntityMatcherSerializer ems = new EntityMatcherSerializer(); 
				ems.serializeWithType(avm, gen, serPro, serPro.findTypeSerializer(serPro.constructType(IGSEntityMatcher.class)));
			} else {
				gen.writeStringField(HammingEntityComparator.HAMMING_VECTOR, GSKeywords.EMPTY);
			}
			
		}
		gen.writeEndObject();

	}


}
