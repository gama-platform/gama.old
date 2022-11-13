package core.configuration.jackson;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import core.configuration.GenstarConfigurationFile;
import core.configuration.dictionary.IGenstarDictionary;
import core.metamodel.attribute.Attribute;
import core.metamodel.io.GSSurveyType;
import core.metamodel.io.GSSurveyWrapper;
import core.metamodel.value.IValue;
import core.util.GSKeywords;

public class GenstarConfigurationFileDeserializer extends StdDeserializer<GenstarConfigurationFile> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected GenstarConfigurationFileDeserializer() {
		this(null);
	}
	
	public GenstarConfigurationFileDeserializer(Class<GenstarConfigurationFile> clazz) {
		super(clazz);
	}

	@Override
	public GenstarConfigurationFile deserialize(JsonParser parser, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		GenstarConfigurationFile gcf = new GenstarConfigurationFile();
		
		ObjectMapper om = (ObjectMapper) parser.getCodec();
		JsonNode on = om.readTree(parser);
		JsonNode node = on.get(GenstarConfigurationFile.SELF);
		
		gcf.setBaseDirectory(Paths.get(node.get(GenstarConfigurationFile.BASE_DIR).asText()));
		gcf.setWrappers(this.getWrappers(node.get(GenstarConfigurationFile.INPUT_FILES), ctxt));
		gcf.setDictionaries(this.getDictionaries(node.get(GenstarConfigurationFile.DICOS), parser));
		
		return gcf;
	}

	@SuppressWarnings("unchecked")
	private Set<IGenstarDictionary<Attribute<? extends IValue>>> getDictionaries(JsonNode dictionaries,
			JsonParser parser) throws IOException {
		
		Set<IGenstarDictionary<Attribute<? extends IValue>>> dicos = new HashSet<>();
		
		int i = -1;
		while(dictionaries.has(++i))
			dicos.add(dictionaries.get(i).traverse(parser.getCodec()).readValueAs(IGenstarDictionary.class));
		
		return dicos;
	}

	private Map<GSSurveyWrapper, List<Integer>> getWrappers(JsonNode wrappersNode, DeserializationContext ctxt) throws IOException {
		
		Map<GSSurveyWrapper, List<Integer>> wrappers = new HashMap<>();
		
		if(!wrappersNode.isArray())
			throw new IllegalArgumentException(GenstarConfigurationFile.INPUT_FILES
					+" tag is not parsed correctly: "+wrappersNode.toString());
		
		int i = -1;
		while(wrappersNode.has(++i)) {
			// Layers
			String layers = wrappersNode.get(i++).asText()
					.replace(GenstarConfigurationFile.LAYER, "")
					.split(GSKeywords.SERIALIZE_KEY_VALUE_SEPARATOR)[1]
					.trim();
			
			List<Integer> listLayers = Arrays.asList(layers.split(GSKeywords.SERIALIZE_ELEMENT_SEPARATOR)).stream()
					.map(level -> Integer.valueOf(level.trim())).toList();
			
			// Wrapper
			JsonNode wrap = wrappersNode.get(i);
			GSSurveyWrapper surveyWrapper = new GSSurveyWrapper(
					Paths.get(wrap.get(GSSurveyWrapper.RELATIVE_PATH).asText()), 
					GSSurveyType.valueOf(wrap.get(GSSurveyWrapper.SURVEY_TYPE).asText())
					);
			
			surveyWrapper.setCsvSeparator(wrap.get(GSSurveyWrapper.CSV_SEPARATOR).asText().toCharArray()[0]);
			surveyWrapper.setFirstRowIndex(wrap.get(GSSurveyWrapper.FIRST_ROW_INDEX).asInt());
			surveyWrapper.setFirstColumnIndex(wrap.get(GSSurveyWrapper.FIRST_COLUMN_INDEX).asInt());
			surveyWrapper.setSheetNumber(wrap.get(GSSurveyWrapper.SHEET_NB).asInt());
			surveyWrapper.setStoredInMemory(wrap.get(GSSurveyWrapper.STORE_IN_MEMORY).asBoolean());
			
			wrappers.put(surveyWrapper, listLayers);
		}
		
		return wrappers;
	}

}
