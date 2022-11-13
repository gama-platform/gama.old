package core.configuration.jackson.attribute.value;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import core.metamodel.attribute.mapper.value.EncodedValueMapper;
import core.metamodel.value.IValue;
import core.metamodel.value.categoric.NominalValue;
import core.util.GSKeywords;

/**
 * Transpose {@link EncodedValueMapper} into Json 
 * 
 * @author kevinchapuis
 *
 * @param <K>
 */
public class RecordValueSerializer<K extends IValue> extends StdSerializer<EncodedValueMapper<K>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected RecordValueSerializer() {
		this(null);
	}
	
	protected RecordValueSerializer(Class<EncodedValueMapper<K>> t) {
		super(t);
	}

	@Override
	public void serialize(EncodedValueMapper<K> mapper, JsonGenerator gen, SerializerProvider provider)
			throws IOException {
		//gen.writeFieldName(EncodedValueMapper.SELF);
		gen.writeStartObject(mapper);
		gen.writeArrayFieldStart(EncodedValueMapper.MAPPING);
		for(String entry : this.getRecordList(mapper)) {
			gen.writeString(entry);
		}
		gen.writeEndArray();
		gen.writeEndObject();
	}

	@Override
	public void serializeWithType(EncodedValueMapper<K> mapper,
			JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
		
	}
	
	/*
	 * return a list view of a record value mapper (a list of pair)
	 */
	private List<String> getRecordList(EncodedValueMapper<K> mapper){
		Map<K, Collection<NominalValue>> res = new HashMap<>();
		for(NominalValue rec : mapper.getRecords()) {
			K val = mapper.getRelatedValue(rec);
			if(!res.containsKey(val))
				res.put(val, new HashSet<>());
			res.get(val).add(rec);
		}
		
		return res.entrySet().stream().map(e -> e.getKey().getStringValue()
				+GSKeywords.SERIALIZE_KEY_VALUE_SEPARATOR
				+e.getValue().stream().map(r -> r.getStringValue())
					.collect(Collectors.joining(GSKeywords.SERIALIZE_ELEMENT_SEPARATOR)))
				.toList();
	}
	
}
