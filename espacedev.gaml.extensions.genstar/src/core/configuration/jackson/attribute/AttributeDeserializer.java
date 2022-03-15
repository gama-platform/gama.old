package core.configuration.jackson.attribute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.AttributeFactory;
import core.metamodel.attribute.EmergentAttribute;
import core.metamodel.attribute.IAttribute;
import core.metamodel.attribute.MappedAttribute;
import core.metamodel.attribute.emergent.AggregateValueFunction;
import core.metamodel.attribute.emergent.CountValueFunction;
import core.metamodel.attribute.emergent.EntityValueFunction;
import core.metamodel.attribute.emergent.IGSValueFunction;
import core.metamodel.attribute.emergent.aggregator.IAggregatorValueFunction;
import core.metamodel.attribute.emergent.filter.GSMatchFilter;
import core.metamodel.attribute.emergent.filter.GSMatchSelection;
import core.metamodel.attribute.emergent.filter.GSNoFilter;
import core.metamodel.attribute.emergent.filter.IGSEntitySelector;
import core.metamodel.attribute.mapper.IAttributeMapper;
import core.metamodel.attribute.mapper.value.EncodedValueMapper;
import core.metamodel.attribute.record.RecordAttribute;
import core.metamodel.entity.IEntity;
import core.metamodel.entity.comparator.ImplicitEntityComparator;
import core.metamodel.entity.comparator.function.IComparatorFunction;
import core.metamodel.entity.matcher.AttributeVectorMatcher;
import core.metamodel.entity.matcher.IGSEntityMatcher;
import core.metamodel.entity.matcher.MatchType;
import core.metamodel.entity.matcher.TagMatcher;
import core.metamodel.entity.tag.EntityTag;
import core.metamodel.value.IValue;
import core.metamodel.value.IValueSpace;
import core.metamodel.value.categoric.NominalValue;
import core.metamodel.value.categoric.OrderedValue;
import core.metamodel.value.numeric.RangeValue;
import core.util.GSKeywords;
import core.util.data.GSEnumDataType;
import core.util.excpetion.GSIllegalRangedData;

public class AttributeDeserializer extends StdDeserializer<IAttribute<? extends IValue>> {
	
	public static Map<String, Attribute<? extends IValue>> DES_DEMO_ATTRIBUTES = new HashMap<>();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AttributeDeserializer() {
		this(null);
	}
	
	public AttributeDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public IAttribute<? extends IValue> deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		ObjectMapper om = (ObjectMapper) p.getCodec();
		JsonNode on = om.readTree(p);
		
		String attributeType = p.getParsingContext().getCurrentName();
		try {
			switch (attributeType) {
			case Attribute.SELF:
				return this.deserializeAttribute(on);
			case MappedAttribute.SELF:
				String mapperType = on.get(MappedAttribute.MAP)
					.get(IAttributeMapper.TYPE).asText();
				switch (mapperType) {
				case IAttributeMapper.REC:
					return this.deserializeRMA(on);
				case IAttributeMapper.AGG:
					return this.deserializeAMA(on);
				case IAttributeMapper.UND:
					return this.deserializeUMA(on);
				default:
					throw new IllegalArgumentException("Trying to deserialize unrecognized mapper: "+mapperType);
				}
			case RecordAttribute.SELF:
				return this.deserializeRA(on);
			case EmergentAttribute.SELF:
				return this.deserializeEA(on);
			default:
				throw new IllegalArgumentException("Trying to parse unknown attribute type: "+attributeType); 
			}
		} catch (GSIllegalRangedData e) {
			e.printStackTrace();
		}
		throw new RuntimeException();
	}
	
	// ------------------ SPECIFIC DESERIALIZER ------------------ //

	/*
	 * Deserialize basic attribute
	 */
	private Attribute<? extends IValue> deserializeAttribute(JsonNode node) 
			throws GSIllegalRangedData {
		String id = this.getName(node);
		if(DES_DEMO_ATTRIBUTES.containsKey(id))
			return DES_DEMO_ATTRIBUTES.get(id);
		Attribute<? extends IValue> attribute; 
		try {
			attribute = AttributeFactory.getFactory()
					.createAttribute(id, this.getType(node), this.getValues(node), 
							this.getRecordMapping(node));
		} catch (IllegalArgumentException e) {
			attribute = AttributeFactory.getFactory()
					.createAttribute(id, this.getType(node), this.getValues(node));
		}
		DES_DEMO_ATTRIBUTES.put(id, attribute);
		return attribute;
	}
	
	/*
	 * Deserialize emergent attribute
	 */
	private IAttribute<? extends IValue> deserializeEA(JsonNode node) {
		String id = this.getName(node);
		if(DES_DEMO_ATTRIBUTES.containsKey(id))
			return DES_DEMO_ATTRIBUTES.get(id);
		EmergentAttribute<? extends IValue, ?, ?> attribute = null;
		try {
			attribute = this.getEmergentAttribute(node);
		} catch (IOException e) {
			e.printStackTrace();
		}
		DES_DEMO_ATTRIBUTES.put(id, attribute);
		return attribute;
	}
		
	/*
	 * Deserialize undirected mapped attribute
	 */
	private Attribute<? extends IValue> deserializeUMA(JsonNode node) throws GSIllegalRangedData {
		String id = this.getName(node);
		if(DES_DEMO_ATTRIBUTES.containsKey(id))
			return DES_DEMO_ATTRIBUTES.get(id);
		Attribute<? extends IValue> attribute;
		try {
			attribute = AttributeFactory.getFactory()
					.createSTSMappedAttribute(this.getName(node), this.getType(node), 
							this.getReferentAttribute(node), this.getOrderedMapper(node),
							this.getRecordMapping(node));
		} catch (IllegalArgumentException e) {
			attribute = AttributeFactory.getFactory()
					.createSTSMappedAttribute(this.getName(node), this.getType(node), 
							this.getReferentAttribute(node), this.getOrderedMapper(node));
		}
		DES_DEMO_ATTRIBUTES.put(id, attribute);
		return attribute;
	}

	/*
	 * Deserialize record mapped attribute
	 */
	private Attribute<? extends IValue> deserializeRMA(JsonNode node) throws GSIllegalRangedData {
		String id = this.getName(node);
		if(DES_DEMO_ATTRIBUTES.containsKey(id))
			return DES_DEMO_ATTRIBUTES.get(id);
		Attribute<? extends IValue> attribute = AttributeFactory.getFactory()
				.createSTOMappedAttribute(this.getName(node), this.getType(node), 
						this.getReferentAttribute(node), this.getOrderedRecord(node));
		DES_DEMO_ATTRIBUTES.put(id, attribute);
		return attribute;
	}

	/*
	 * Deserialize aggregated attribute
	 */
	private Attribute<? extends IValue> deserializeAMA(JsonNode node) 
			throws GSIllegalRangedData{
		String id = this.getName(node);
		if(DES_DEMO_ATTRIBUTES.containsKey(id))
			return DES_DEMO_ATTRIBUTES.get(id);
		
		MappedAttribute<? extends IValue, ? extends IValue> attribute = null;
		Map<String, Collection<String>> map = this.getOrderedAggregate(node);
		switch (this.getType(node)) {
		case Range:
			try {
				attribute = AttributeFactory.getFactory()
						.createRangeAggregatedAttribute(id, 
								this.deserializeAttribute(RangeValue.class, node.findValue(MappedAttribute.REF)),
									map, this.getRecordMapping(node));
			} catch (IllegalArgumentException e) {
				attribute = AttributeFactory.getFactory()
						.createRangeAggregatedAttribute(id, 
								this.deserializeAttribute(RangeValue.class, node.findValue(MappedAttribute.REF)),
									map);
			} 
			
			break;
		case Nominal:
			try {
				attribute = AttributeFactory.getFactory()
						.createNominalAggregatedAttribute(id, 
								this.deserializeAttribute(NominalValue.class, node.findValue(MappedAttribute.REF)), 
								map, this.getRecordMapping(node));
			} catch (IllegalArgumentException e) {
				attribute = AttributeFactory.getFactory()
						.createNominalAggregatedAttribute(id, 
								this.deserializeAttribute(NominalValue.class, node.findValue(MappedAttribute.REF)),
									map);
			}
			break;
		case Order:
			try {
				attribute = AttributeFactory.getFactory()
						.createOrderedAggregatedAttribute(id, 
								this.deserializeAttribute(OrderedValue.class, node.findValue(MappedAttribute.REF)),
									map.entrySet().stream().collect(Collectors.toMap(
											Entry::getKey, 
											entry -> new ArrayList<>(entry.getValue()),
											(e1, e2) -> e1,
											LinkedHashMap::new)),
									this.getRecordMapping(node));
			} catch (IllegalArgumentException e) {
				attribute = AttributeFactory.getFactory()
						.createOrderedAggregatedAttribute(id, 
								this.deserializeAttribute(OrderedValue.class, node.findValue(MappedAttribute.REF)),
									map.entrySet().stream().collect(Collectors.toMap(
											Entry::getKey, 
											entry -> new ArrayList<>(entry.getValue()),
											(e1, e2) -> e1,
											LinkedHashMap::new)));
			}
			break;
		default:
			throw new IllegalArgumentException("Trying to parse unknown value type: "+this.getType(node));
		}
		DES_DEMO_ATTRIBUTES.put(id, attribute);
		return attribute;
	}
	
	/*
	 * 
	 */
	@SuppressWarnings("unchecked")
	private <V extends IValue> Attribute<V> deserializeAttribute(Class<V> clazz, JsonNode node) 
			throws GSIllegalRangedData {
		if(!node.asText().isEmpty()) {
			Attribute<? extends IValue> attribute = DES_DEMO_ATTRIBUTES.get(node.asText());
			if(attribute.getValueSpace().getType().equals(GSEnumDataType.getType(clazz)))
				return (Attribute<V>) attribute;
			throw new IllegalStateException("Trying to deserialize attribute \""+node.asText()+"\" of type "
					+ attribute.getValueSpace().getType() + " as a " + clazz.getCanonicalName() +" attribute type");
		}
		Attribute<V> attribute = AttributeFactory.getFactory().createAttribute(
				this.getName(node.findValue(Attribute.SELF)), 
				this.getValues(node.findValue(Attribute.SELF)), clazz);
		DES_DEMO_ATTRIBUTES.put(attribute.getAttributeName(), attribute);
		return attribute;
	}
	
	/*
	 * Deserialize record attribute
	 */
	private RecordAttribute<? extends IAttribute<? extends IValue>, ? extends IAttribute<? extends IValue>> deserializeRA(JsonNode node) 
			throws GSIllegalRangedData { 
		return AttributeFactory.getFactory()
				.createRecordAttribute(this.getName(node), 
						GSEnumDataType.valueOf(node.findValue(RecordAttribute.PROXY_TYPE).asText()), 
						this.getReferentAttribute(node));
	}
	
	// ------------------ BASIC INNER UTILITIES ------------------ //

	// ATTRIBUTE FIELD
	
	/*
	 * Get the name of an attribute describes as a json node
	 * 
	 */
	private String getName(JsonNode attributeNode) {
		return attributeNode.get(IAttribute.NAME).asText();
	}
	
	/*
	 * Get the type of value within the attribute describes as a json node
	 * 
	 */
	private GSEnumDataType getType(JsonNode attributeNode) {
		return GSEnumDataType.valueOf(attributeNode.findValue(IValueSpace.TYPE).asText());
	}
	
	/*
	 * Get values from an attribute describes as a json node
	 * WARNING: only useful for demographic attribute
	 */
	private List<String> getValues(JsonNode attributeNode){
		return attributeNode.findValue(IAttribute.VALUE_SPACE)
				.findValue(IValueSpace.VALUES)
				.findValues(IValue.VALUE).stream()
				.map(val -> val.asText()).collect(Collectors.toList());
	}
	
	// REFERENT (DEMOGRAHIC) ATTRIBUTE

	/*
	 * Get the referent attribute within the attribute describes as a json node
	 * WARNING: only functional for MappedAttribute
	 */
	private Attribute<? extends IValue> getReferentAttribute(JsonNode attributeNode) 
			throws GSIllegalRangedData {
		JsonNode referent = attributeNode.findValue(MappedAttribute.REF); 
		if(referent.getNodeType().equals(JsonNodeType.STRING))
			return DES_DEMO_ATTRIBUTES.get(referent.asText());
		return this.deserializeAttribute(attributeNode
				.findValue(MappedAttribute.REF)
				.findValue(Attribute.SELF));
	}
	
	// MAPPER
	
	private Map<String, String> getRecordMapping(JsonNode node) {
		JsonNode mapping = node.findValue(EncodedValueMapper.SELF);
		if(mapping == null || !mapping.has(EncodedValueMapper.MAPPING))
			throw new IllegalArgumentException("Trying to unmap the mapper but cannot access array mapping: "
					+ "node type instade is "+node.getNodeType());
		Map<String, String> records = new HashMap<>();
		JsonNode themap = mapping.get(EncodedValueMapper.MAPPING);
		int i = 0;
		while(themap.has(i)) {
			String[] keyVal = themap.get(i++).asText()
					.split(GSKeywords.SERIALIZE_KEY_VALUE_SEPARATOR);
			for(String record : keyVal[1].split(GSKeywords.SERIALIZE_ELEMENT_SEPARATOR)) {
				records.put(record, keyVal[0].trim());
			}
		}
		return records;
	}
	
	/*
	 * Get the record map for mapped demographic attribute
	 */
	private LinkedHashMap<String, String> getOrderedRecord(JsonNode node) {
		JsonNode mapArray = this.validateMapper(node);
		LinkedHashMap<String, String> record = new LinkedHashMap<>();
		int i = 0;
		while(mapArray.has(i)) {
			String[] keyVal = mapArray.get(i++).asText()
					.split(GSKeywords.SERIALIZE_KEY_VALUE_SEPARATOR);
			if(keyVal.length != 2)
				throw new IllegalArgumentException("Not a key / value match but has "+keyVal.length+" match");
			record.put(keyVal[0].trim(), keyVal[1].trim());
		}
		return record;
	}
	
	/*
	 * Get ordered undirected map for mapped demographic attribute
	 */
	private Map<Collection<String>, Collection<String>> getOrderedMapper(JsonNode node){
		JsonNode mapArray = this.validateMapper(node);
		Map<Collection<String>, Collection<String>> mapper = new LinkedHashMap<>();
		int i = 0;
		while(mapArray.has(i)) {
			String[] keyVal = mapArray.get(i++).asText()
					.split(GSKeywords.SERIALIZE_KEY_VALUE_SEPARATOR);
			if(keyVal.length != 2)
				throw new IllegalArgumentException("Not a key / value match but has "+keyVal.length+" match");
			mapper.put(Arrays.asList(keyVal[0].split(GSKeywords.SERIALIZE_ELEMENT_SEPARATOR)).stream()
						.map(key -> key.trim()).collect(Collectors.toList()), 
					Arrays.asList(keyVal[1].split(GSKeywords.SERIALIZE_ELEMENT_SEPARATOR)).stream()
						.map(val -> val.trim()).collect(Collectors.toList()));
		}
		return mapper;
	}
	
	/*
	 * Get the aggregate map for mapped demographic attribute
	 */
	private Map<String, Collection<String>> getOrderedAggregate(JsonNode node) {
		JsonNode mapArray = this.validateMapper(node);
		LinkedHashMap<String, Collection<String>> mapper = new LinkedHashMap<>();
		int i = 0;
		while(mapArray.has(i)) {
			String[] keyVal = mapArray.get(i++).asText()
					.split(GSKeywords.SERIALIZE_KEY_VALUE_SEPARATOR);
			if(keyVal.length != 2)
				throw new IllegalArgumentException("Not a key / value match but has "+keyVal.length+" match");
			mapper.put(keyVal[0].trim(), 
					Arrays.asList(keyVal[1].split(GSKeywords.SERIALIZE_ELEMENT_SEPARATOR)).stream()
						.map(val -> val.trim()).collect(Collectors.toList()));
		}
		return mapper;
	}
	
	/*
	 * Check if given node is a proper mapper Object
	 */
	private JsonNode validateMapper(JsonNode node) {
		JsonNode mapArray = node.findValue(IAttributeMapper.THE_MAP);
		if(!mapArray.isArray())
			throw new IllegalArgumentException("Trying to unmap the mapper but cannot access array mapping: "
					+ "node type instade is "+mapArray.getNodeType());
		return mapArray;
	}
	
	
	/*  -------- *
	 *  EMERGENT *
	 *  -------- */
	
	
	/*
	 * Build emergent attribute 
	 */
	private EmergentAttribute<? extends IValue, ?, ?> getEmergentAttribute(
			JsonNode node) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		
		String name = this.getName(node);
		
		JsonNode function = node.get(EmergentAttribute.FUNCTION);
		JsonNode transposer = node.get(EmergentAttribute.TRANSPOSER);
		JsonNode mapping = function.get(IGSValueFunction.MAPPING);
			
		EmergentAttribute<? extends IValue, ?, ?> att = null;
		
		String type = function.get(IGSValueFunction.ID).textValue();
		Attribute<? extends IValue> refAtt = null;
		if(function.has(MappedAttribute.REF))
			refAtt = DES_DEMO_ATTRIBUTES.get(function.get(MappedAttribute.REF).asText());
		
		switch (type) {
		case CountValueFunction.SELF:
			if(refAtt == null)
				att = AttributeFactory.getFactory().createSizeAttribute(name);
			else
				att = AttributeFactory.getFactory().createCountAttribute(name, 
						this.getValues(node.findValue(Attribute.SELF)),
						this.getCountMapping(mapping), getCollectionTransposer(transposer));
			break;
		case AggregateValueFunction.SELF:
			att = AttributeFactory.getFactory().createAggregatedValueOfAttribute(name, refAtt, 
					this.getAggrgatorFunction(function.get(AggregateValueFunction.AGG)), 
					getCollectionTransposer(transposer));
			break;
		case EntityValueFunction.SELF:
			if(mapping.get(0).asText().equals(GSKeywords.IDENTITY)) {
				att = AttributeFactory.getFactory().createValueOfAttribute(name, refAtt, 
						getEntityTransposer(transposer));
			} else {
				att = AttributeFactory.getFactory().createValueOfAttribute(name, refAtt, 
						this.getValues(node.findValue(Attribute.SELF)),
						this.getMapping(mapping), getEntityTransposer(transposer));
			}
			break;
		default:
			throw new IllegalStateException("Emergent function type "
					+function.get(IGSValueFunction.ID).textValue()+" is unrecognized");
		}
		
		return att;
	}
	
	/*
	 * 
	 */
	private Map<String, String> getMapping(JsonNode mapping) {
		if(!mapping.isArray())
			throw new RuntimeException("Error when deserializing mapping: "+mapping.toString());
		Map<String, String> outputMap = new HashMap<>();
		int i = 0;
		while(mapping.has(i)) {
			String[] keyVal = mapping.get(i++).asText()
					.split(GSKeywords.SERIALIZE_KEY_VALUE_SEPARATOR);
			if(keyVal.length != 2)
				throw new IllegalArgumentException("Not a key / value match but has "+keyVal.length+" match");
			outputMap.put(keyVal[0].trim(), keyVal[1].trim());
		}
		return outputMap;
	}

	/*
	 * 
	 */
	private Map<Integer, String> getCountMapping(JsonNode mapping) 
			throws JsonParseException, JsonMappingException, IOException {
		if(!mapping.isArray())
			throw new RuntimeException("Error when deserializing mapping: "+mapping.toString());
		Map<Integer, String> outputMap = new HashMap<>();
		int i = 0;
		while(mapping.has(i)) {
			String[] keyVal = mapping.get(i++).asText()
					.split(GSKeywords.SERIALIZE_KEY_VALUE_SEPARATOR);
			if(keyVal.length != 2)
				throw new IllegalArgumentException("Not a key / value match but has "+keyVal.length+" match");
			outputMap.put(Integer.valueOf(keyVal[0].trim()), keyVal[1].trim());
		}
		return outputMap;
	}

	@SuppressWarnings("unchecked")
	private <V extends IValue> IAggregatorValueFunction<V> getAggrgatorFunction(JsonNode function) 
			throws JsonParseException, JsonMappingException, IOException {
		return new ObjectMapper().readValue(function.toString(), IAggregatorValueFunction.class);
	}

	/*
	 * Retrieve a collection transposer
	 */
	private IGSEntitySelector<Collection<IEntity<? extends IAttribute<? extends IValue>>>, ?> 
		getCollectionTransposer(JsonNode node) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		
		IGSEntitySelector<Collection<IEntity<? extends IAttribute<? extends IValue>>>, ?> transposer = null;
		
		String type = node.get(IGSEntitySelector.TYPE).asText();
		
		JsonNode transNode = node.get(IGSEntitySelector.MATCHERS);
		String transType = transNode.get(IGSEntityMatcher.TYPE).asText();
		
		MatchType matchType = MatchType.valueOf(node.get(IGSEntitySelector.MATCH_TYPE).asText());
		ImplicitEntityComparator comparator = this.getComparator(node.get(IGSEntitySelector.COMPARATOR)); 
		
		if(type == GSMatchSelection.SELF)
			throw new IllegalArgumentException("Trying to deserialize transposer to collection of sub entities with a selection filter");
		
		switch (type) {
		case GSMatchFilter.SELF:
			if(transType == AttributeVectorMatcher.SELF)
				transposer =  new GSMatchFilter<>(new AttributeVectorMatcher(this.getValueMatchers(node.get(IGSEntityMatcher.VECTOR))), matchType);
			else if(transType == TagMatcher.SELF)
				transposer = new GSMatchFilter<>(new TagMatcher(this.getTagMatchers(node.get(IGSEntityMatcher.VECTOR))), matchType);
			if(comparator == null)
				transposer.setComparator(comparator);
			return transposer;
		case GSNoFilter.SELF:
			return new GSNoFilter();
		default:
			throw new RuntimeException("Deserialization failed to create IGSEntityTransposer of type "+type);
		}
		
	}
	
	/*
	 * Retrieve entity transposer
	 */
	private IGSEntitySelector<IEntity<? extends IAttribute<? extends IValue>>, ?>
		getEntityTransposer(JsonNode node) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		
		IGSEntitySelector<IEntity<? extends IAttribute<? extends IValue>>, ?> transposer = null;
		
		String tType = node.get(IGSEntitySelector.TYPE).asText();
		
		JsonNode nodeMatcher = node.get(IGSEntitySelector.MATCHERS); 
		String mType = nodeMatcher.get(IGSEntityMatcher.TYPE).asText();
		
		MatchType matchType = MatchType.valueOf(node.get(IGSEntitySelector.MATCH_TYPE).asText());
		ImplicitEntityComparator comparator = this.getComparator(node.get(IGSEntitySelector.COMPARATOR)); 
		
		if(!tType.equals(GSMatchSelection.SELF))
			throw new IllegalArgumentException("Trying to deserialize entity transposer of sub entities with a filter mechanism : "
					+tType);
		
		switch (mType) {
		case AttributeVectorMatcher.SELF:
			transposer =  new GSMatchSelection<>(new AttributeVectorMatcher(
					this.getValueMatchers(nodeMatcher.get(IGSEntityMatcher.VECTOR))), matchType);
			break;
		case TagMatcher.SELF:
			transposer = new GSMatchSelection<>(new TagMatcher(
					this.getTagMatchers(nodeMatcher.get(IGSEntityMatcher.VECTOR))), matchType);
			break;
		default:
			throw new RuntimeException("Trying to deserialize "+IGSEntityMatcher.class.getSimpleName()
					+" with unkown match type: "+mType);
		}
		
		transposer.setComparator(comparator);
		
		return transposer;
	}

	/*
	 * Retrieve the value in a matcher array
	 */
	private IValue[] getValueMatchers(JsonNode ArrayNode) {
		if(!ArrayNode.isArray())
			throw new IllegalArgumentException("This node is not an array of matchers (node type is "
					+ArrayNode.getNodeType()+")");
		List<IValue> values = new ArrayList<>();
		int i = 0;
		while(ArrayNode.has(i)) {
			String val = ArrayNode.get(i++).asText();
			IValue value = DES_DEMO_ATTRIBUTES.values().stream()
					.filter(att -> att.getValueSpace().contains(val))
					.findFirst().get().getValueSpace().getValue(val);
			values.add(value);
		}
		return values.toArray(new IValue[values.size()]);
	}
	
	private EntityTag[] getTagMatchers(JsonNode ArrayNode) {
		if(!ArrayNode.isArray())
			throw new IllegalArgumentException("This node is not an array of matchers (node type is "
					+ArrayNode.getNodeType()+")");
		List<EntityTag> tags = new ArrayList<>();
		int i = 0;
		while(ArrayNode.has(i)) {
			String val = ArrayNode.get(i++).asText();
			tags.add(EntityTag.valueOf(val));
		}
		return tags.toArray(new EntityTag[tags.size()]);
	}
	
	/*
	 * TODO: enable deserialization of Hamming distance based comparator
	 */
	private ImplicitEntityComparator getComparator(JsonNode comparatorNode) throws JsonParseException, JsonMappingException, IllegalArgumentException, IOException {		
		
		ImplicitEntityComparator comparator = new ImplicitEntityComparator();
		
		if(comparatorNode.has(GSKeywords.CONTENT) && 
				comparatorNode.get(GSKeywords.CONTENT).asText().equals(GSKeywords.DEFAULT))
			return comparator;
		
		/*
		 * Attribute and relationship (reverse or not) to comparison process
		 */
		JsonNode arrayAttributes = comparatorNode.get(ImplicitEntityComparator.ATTRIBUTES_REF);
		int index = -1;
		Map<IAttribute<? extends IValue>, Boolean> attributes = new HashMap<>();
		while(arrayAttributes.has(++index)) {
			String[] entry = arrayAttributes.get(index).asText()
					.split(GSKeywords.SERIALIZE_KEY_VALUE_SEPARATOR);
			attributes.put(
					DES_DEMO_ATTRIBUTES.get(entry[0]), 
					Boolean.valueOf(entry[1]));
		}
		
		attributes.entrySet().stream().forEach(entry -> comparator.setAttribute(entry.getKey(), entry.getValue()));
		
		/*
		 * Custom comparison function for specific value type
		 */
		JsonNode arrayFunctions = comparatorNode.get(ImplicitEntityComparator.COMP_FUNCTIONS);
		index = -1;
		Collection<IComparatorFunction<? extends IValue>> functions = new HashSet<>();
		ObjectMapper om = new ObjectMapper();
		while(arrayFunctions.has(++index))
			functions.add(om.readerFor(IComparatorFunction.class)
					.readValue(arrayFunctions.get(index).asText()));
		
		functions.stream().forEach(function -> comparator.setComparatorFunction(function));
		
		return comparator;
	}
	
}
