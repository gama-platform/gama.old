package core.configuration.jackson;

import java.io.IOException;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import core.configuration.GenstarConfigurationFile;
import core.configuration.dictionary.IGenstarDictionary;
import core.metamodel.attribute.Attribute;
import core.metamodel.io.GSSurveyWrapper;
import core.metamodel.value.IValue;
import core.util.GSKeywords;

public class GenstarConfigurationFileSerializer extends StdSerializer<GenstarConfigurationFile> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected GenstarConfigurationFileSerializer() {
		this(null);
	}

	protected GenstarConfigurationFileSerializer(Class<GenstarConfigurationFile> clazz) {
		super(clazz);
	}

	@Override
	public void serialize(GenstarConfigurationFile file, JsonGenerator gen, SerializerProvider provider)
			throws IOException {
		
		gen.writeStartObject();
		gen.writeFieldName(GenstarConfigurationFile.SELF);
		gen.writeStartObject();
		gen.writeStringField(GenstarConfigurationFile.BASE_DIR, file.getBaseDirectory().toString());
		gen.writeArrayFieldStart(GenstarConfigurationFile.INPUT_FILES);
		for(GSSurveyWrapper wrapper : file.getWrappers().keySet()) {
			gen.writeString(GenstarConfigurationFile.LAYER
					+GSKeywords.SERIALIZE_KEY_VALUE_SEPARATOR+
					file.getWrappers().get(wrapper).stream().map(i -> i.toString())
						.collect(Collectors.joining(GSKeywords.SERIALIZE_ELEMENT_SEPARATOR)));
			gen.writeObject(wrapper);
		}
		gen.writeEndArray();
		
		gen.writeArrayFieldStart(GenstarConfigurationFile.DICOS);
		for(IGenstarDictionary<Attribute<? extends IValue>> dico : file.getDictionaries())
			gen.writeObject(dico);
		gen.writeEndArray();
		
		gen.writeEndObject();
		
	}

}
