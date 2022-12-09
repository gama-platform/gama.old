/*******************************************************************************************************
 *
 * AttributeDeserializer.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
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
import java.util.Objects;
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
import core.util.exception.GSIllegalRangedData;
import core.util.exception.GenstarException;

/**
 * The Class AttributeDeserializer.
 */
public class AttributeDeserializer extends StdDeserializer<IAttribute<? extends IValue>> {

	/** The des demo attributes. */
	protected static final Map<String, Attribute<? extends IValue>> DES_DEMO_ATTRIBUTES = new HashMap<>();

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new attribute deserializer.
	 */
	public AttributeDeserializer() {
		this(null);
	}

	/**
	 * Instantiates a new attribute deserializer.
	 *
	 * @param vc
	 *            the vc
	 */
	public AttributeDeserializer(final Class<?> vc) {
		super(vc);
	}

	@Override
	public IAttribute<? extends IValue> deserialize(final JsonParser p, final DeserializationContext ctxt)
			throws IOException {
		ObjectMapper om = (ObjectMapper) p.getCodec();
		JsonNode on = om.readTree(p);

		String attributeType = p.getParsingContext().getCurrentName();
		try {
			return switch (attributeType) {
				case Attribute.SELF -> this.deserializeAttribute(on);
				case MappedAttribute.SELF -> {
					String mapperType = on.get(MappedAttribute.MAP).get(IAttributeMapper.TYPE).asText();
					yield switch (mapperType) {
						case IAttributeMapper.REC -> this.deserializeRMA(on);
						case IAttributeMapper.AGG -> this.deserializeAMA(on);
						case IAttributeMapper.UND -> this.deserializeUMA(on);
						default -> throw new IllegalArgumentException(
								"Trying to deserialize unrecognized mapper: " + mapperType);
					};
				}
				case RecordAttribute.SELF -> this.deserializeRA(on);
				case EmergentAttribute.SELF_LABEL -> this.deserializeEA(on);
				default -> throw new IllegalArgumentException(
						"Trying to parse unknown attribute type: " + attributeType);
			};
		} catch (GSIllegalRangedData e) {
			e.printStackTrace();
		}
		throw new GenstarException();
	}

	// ------------------ SPECIFIC DESERIALIZER ------------------ //

	/**
	 * Deserialize attribute.
	 *
	 * @param node
	 *            the node
	 * @return the attribute<? extends I value>
	 * @throws GSIllegalRangedData
	 *             the GS illegal ranged data
	 */
	/*
	 * Deserialize basic attribute
	 */
	private Attribute<? extends IValue> deserializeAttribute(final JsonNode node) throws GSIllegalRangedData {
		String id = this.getName(node);
		if (DES_DEMO_ATTRIBUTES.containsKey(id)) return DES_DEMO_ATTRIBUTES.get(id);
		Attribute<? extends IValue> attribute;
		try {
			attribute = AttributeFactory.getFactory().createAttribute(id, this.getType(node), this.getValues(node),
					this.getRecordMapping(node));
		} catch (IllegalArgumentException e) {
			attribute = AttributeFactory.getFactory().createAttribute(id, this.getType(node), this.getValues(node));
		}
		DES_DEMO_ATTRIBUTES.put(id, attribute);
		return attribute;
	}

	/**
	 * Deserialize EA.
	 *
	 * @param node
	 *            the node
	 * @return the i attribute<? extends I value>
	 */
	/*
	 * Deserialize emergent attribute
	 */
	private IAttribute<? extends IValue> deserializeEA(final JsonNode node) {
		String id = this.getName(node);
		if (DES_DEMO_ATTRIBUTES.containsKey(id)) return DES_DEMO_ATTRIBUTES.get(id);
		EmergentAttribute<? extends IValue, ?, ?> attribute = null;
		try {
			attribute = this.getEmergentAttribute(node);
		} catch (IOException e) {
			e.printStackTrace();
		}
		DES_DEMO_ATTRIBUTES.put(id, attribute);
		return attribute;
	}

	/**
	 * Deserialize UMA.
	 *
	 * @param node
	 *            the node
	 * @return the attribute<? extends I value>
	 * @throws GSIllegalRangedData
	 *             the GS illegal ranged data
	 */
	/*
	 * Deserialize undirected mapped attribute
	 */
	private Attribute<? extends IValue> deserializeUMA(final JsonNode node) throws GSIllegalRangedData {
		String id = this.getName(node);
		if (DES_DEMO_ATTRIBUTES.containsKey(id)) return DES_DEMO_ATTRIBUTES.get(id);
		Attribute<? extends IValue> attribute;
		try {
			attribute = AttributeFactory.getFactory().createSTSMappedAttribute(this.getName(node), this.getType(node),
					this.getReferentAttribute(node), this.getOrderedMapper(node), this.getRecordMapping(node));
		} catch (IllegalArgumentException e) {
			attribute = AttributeFactory.getFactory().createSTSMappedAttribute(this.getName(node), this.getType(node),
					this.getReferentAttribute(node), this.getOrderedMapper(node));
		}
		DES_DEMO_ATTRIBUTES.put(id, attribute);
		return attribute;
	}

	/**
	 * Deserialize RMA.
	 *
	 * @param node
	 *            the node
	 * @return the attribute<? extends I value>
	 * @throws GSIllegalRangedData
	 *             the GS illegal ranged data
	 */
	/*
	 * Deserialize record mapped attribute
	 */
	private Attribute<? extends IValue> deserializeRMA(final JsonNode node) throws GSIllegalRangedData {
		String id = this.getName(node);
		if (DES_DEMO_ATTRIBUTES.containsKey(id)) return DES_DEMO_ATTRIBUTES.get(id);
		Attribute<? extends IValue> attribute = AttributeFactory.getFactory().createSTOMappedAttribute(
				this.getName(node), this.getType(node), this.getReferentAttribute(node), this.getOrderedRecord(node));
		DES_DEMO_ATTRIBUTES.put(id, attribute);
		return attribute;
	}

	/**
	 * Deserialize AMA.
	 *
	 * @param node
	 *            the node
	 * @return the attribute<? extends I value>
	 * @throws GSIllegalRangedData
	 *             the GS illegal ranged data
	 */
	/*
	 * Deserialize aggregated attribute
	 */
	private Attribute<? extends IValue> deserializeAMA(final JsonNode node) throws GSIllegalRangedData {
		String id = this.getName(node);
		if (DES_DEMO_ATTRIBUTES.containsKey(id)) return DES_DEMO_ATTRIBUTES.get(id);

		MappedAttribute<? extends IValue, ? extends IValue> attribute = null;
		Map<String, Collection<String>> map = this.getOrderedAggregate(node);
		switch (this.getType(node)) {
			case Range:
				try {
					attribute = AttributeFactory.getFactory().createRangeAggregatedAttribute(id,
							this.deserializeAttribute(RangeValue.class, node.findValue(MappedAttribute.REF)), map,
							this.getRecordMapping(node));
				} catch (IllegalArgumentException e) {
					attribute = AttributeFactory.getFactory().createRangeAggregatedAttribute(id,
							this.deserializeAttribute(RangeValue.class, node.findValue(MappedAttribute.REF)), map);
				}

				break;
			case Nominal:
				try {
					attribute = AttributeFactory.getFactory().createNominalAggregatedAttribute(id,
							this.deserializeAttribute(NominalValue.class, node.findValue(MappedAttribute.REF)), map,
							this.getRecordMapping(node));
				} catch (IllegalArgumentException e) {
					attribute = AttributeFactory.getFactory().createNominalAggregatedAttribute(id,
							this.deserializeAttribute(NominalValue.class, node.findValue(MappedAttribute.REF)), map);
				}
				break;
			case Order:
				try {
					attribute = AttributeFactory.getFactory().createOrderedAggregatedAttribute(id,
							this.deserializeAttribute(OrderedValue.class, node.findValue(MappedAttribute.REF)),
							map.entrySet().stream().collect(Collectors.toMap(Entry::getKey,
									entry -> new ArrayList<>(entry.getValue()), (e1, e2) -> e1, LinkedHashMap::new)),
							this.getRecordMapping(node));
				} catch (IllegalArgumentException e) {
					attribute = AttributeFactory.getFactory().createOrderedAggregatedAttribute(id,
							this.deserializeAttribute(OrderedValue.class, node.findValue(MappedAttribute.REF)),
							map.entrySet().stream().collect(Collectors.toMap(Entry::getKey,
									entry -> new ArrayList<>(entry.getValue()), (e1, e2) -> e1, LinkedHashMap::new)));
				}
				break;
			default:
				throw new IllegalArgumentException("Trying to parse unknown value type: " + this.getType(node));
		}
		DES_DEMO_ATTRIBUTES.put(id, attribute);
		return attribute;
	}

	/**
	 * Deserialize attribute.
	 *
	 * @param <V>
	 *            the value type
	 * @param clazz
	 *            the clazz
	 * @param node
	 *            the node
	 * @return the attribute
	 * @throws GSIllegalRangedData
	 *             the GS illegal ranged data
	 */
	/*
	 *
	 */
	@SuppressWarnings ("unchecked")
	private <V extends IValue> Attribute<V> deserializeAttribute(final Class<V> clazz, final JsonNode node)
			throws GSIllegalRangedData {
		if (!node.asText().isEmpty()) {
			Attribute<? extends IValue> attribute = DES_DEMO_ATTRIBUTES.get(node.asText());
			if (attribute.getValueSpace().getType().equals(GSEnumDataType.getType(clazz)))
				return (Attribute<V>) attribute;
			throw new IllegalStateException("Trying to deserialize attribute \"" + node.asText() + "\" of type "
					+ attribute.getValueSpace().getType() + " as a " + clazz.getCanonicalName() + " attribute type");
		}
		Attribute<V> attribute = AttributeFactory.getFactory().createAttribute(
				this.getName(node.findValue(Attribute.SELF)), this.getValues(node.findValue(Attribute.SELF)), clazz);
		DES_DEMO_ATTRIBUTES.put(attribute.getAttributeName(), attribute);
		return attribute;
	}

	/**
	 * Deserialize RA.
	 *
	 * @param node
	 *            the node
	 * @return the record attribute<? extends I attribute<? extends I value>,? extends I attribute<? extends I value>>
	 * @throws GSIllegalRangedData
	 *             the GS illegal ranged data
	 */
	/*
	 * Deserialize record attribute
	 */
	private RecordAttribute<? extends IAttribute<? extends IValue>, ? extends IAttribute<? extends IValue>>
			deserializeRA(final JsonNode node) throws GSIllegalRangedData {
		return AttributeFactory.getFactory().createRecordAttribute(this.getName(node),
				GSEnumDataType.valueOf(node.findValue(RecordAttribute.PROXY_TYPE).asText()),
				this.getReferentAttribute(node));
	}

	// ------------------ BASIC INNER UTILITIES ------------------ //

	// ATTRIBUTE FIELD

	/**
	 * Gets the name.
	 *
	 * @param attributeNode
	 *            the attribute node
	 * @return the name
	 */
	/*
	 * Get the name of an attribute describes as a json node
	 *
	 */
	private String getName(final JsonNode attributeNode) {
		return attributeNode.get(IAttribute.NAME).asText();
	}

	/**
	 * Gets the type.
	 *
	 * @param attributeNode
	 *            the attribute node
	 * @return the type
	 */
	/*
	 * Get the type of value within the attribute describes as a json node
	 *
	 */
	private GSEnumDataType getType(final JsonNode attributeNode) {
		return GSEnumDataType.valueOf(attributeNode.findValue(IValueSpace.TYPE_LABEL).asText());
	}

	/**
	 * Gets the values.
	 *
	 * @param attributeNode
	 *            the attribute node
	 * @return the values
	 */
	/*
	 * Get values from an attribute describes as a json node WARNING: only useful for demographic attribute
	 */
	private List<String> getValues(final JsonNode attributeNode) {
		return attributeNode.findValue(IAttribute.VALUE_SPACE).findValue(IValueSpace.VALUES_LABEL)
				.findValues(IValue.VALUE_LABEL).stream().map(JsonNode::asText).toList();
	}

	// REFERENT (DEMOGRAHIC) ATTRIBUTE

	/**
	 * Gets the referent attribute.
	 *
	 * @param attributeNode
	 *            the attribute node
	 * @return the referent attribute
	 * @throws GSIllegalRangedData
	 *             the GS illegal ranged data
	 */
	/*
	 * Get the referent attribute within the attribute describes as a json node WARNING: only functional for
	 * MappedAttribute
	 */
	private Attribute<? extends IValue> getReferentAttribute(final JsonNode attributeNode) throws GSIllegalRangedData {
		JsonNode referent = attributeNode.findValue(MappedAttribute.REF);
		if (JsonNodeType.STRING.equals(referent.getNodeType())) return DES_DEMO_ATTRIBUTES.get(referent.asText());
		return this.deserializeAttribute(attributeNode.findValue(MappedAttribute.REF).findValue(Attribute.SELF));
	}

	// MAPPER

	/**
	 * Gets the record mapping.
	 *
	 * @param node
	 *            the node
	 * @return the record mapping
	 */
	private Map<String, String> getRecordMapping(final JsonNode node) {
		JsonNode mapping = node.findValue(EncodedValueMapper.SELF);
		if (mapping == null || !mapping.has(EncodedValueMapper.MAPPING))
			throw new IllegalArgumentException("Trying to unmap the mapper but cannot access array mapping: "
					+ "node type instade is " + node.getNodeType());
		Map<String, String> records = new HashMap<>();
		JsonNode themap = mapping.get(EncodedValueMapper.MAPPING);
		int i = 0;
		while (themap.has(i)) {
			String[] keyVal = themap.get(i++).asText().split(GSKeywords.SERIALIZE_KEY_VALUE_SEPARATOR);
			for (String rec : keyVal[1].split(GSKeywords.SERIALIZE_ELEMENT_SEPARATOR)) {
				records.put(rec, keyVal[0].trim());
			}
		}
		return records;
	}

	/**
	 * Gets the ordered record.
	 *
	 * @param node
	 *            the node
	 * @return the ordered record
	 */
	/*
	 * Get the record map for mapped demographic attribute
	 */
	private LinkedHashMap<String, String> getOrderedRecord(final JsonNode node) {
		JsonNode mapArray = this.validateMapper(node);
		LinkedHashMap<String, String> records = new LinkedHashMap<>();
		int i = 0;
		while (mapArray.has(i)) {
			String[] keyVal = mapArray.get(i++).asText().split(GSKeywords.SERIALIZE_KEY_VALUE_SEPARATOR);
			if (keyVal.length != 2)
				throw new IllegalArgumentException("Not a key / value match but has " + keyVal.length + " match");
			records.put(keyVal[0].trim(), keyVal[1].trim());
		}
		return records;
	}

	/**
	 * Gets the ordered mapper.
	 *
	 * @param node
	 *            the node
	 * @return the ordered mapper
	 */
	/*
	 * Get ordered undirected map for mapped demographic attribute
	 */
	private Map<Collection<String>, Collection<String>> getOrderedMapper(final JsonNode node) {
		JsonNode mapArray = this.validateMapper(node);
		Map<Collection<String>, Collection<String>> mapper = new LinkedHashMap<>();
		int i = 0;
		while (mapArray.has(i)) {
			String[] keyVal = mapArray.get(i++).asText().split(GSKeywords.SERIALIZE_KEY_VALUE_SEPARATOR);
			if (keyVal.length != 2)
				throw new IllegalArgumentException("Not a key / value match but has " + keyVal.length + " match");
			mapper.put(
					Arrays.asList(keyVal[0].split(GSKeywords.SERIALIZE_ELEMENT_SEPARATOR)).stream().map(String::trim)
							.toList(),
					Arrays.asList(keyVal[1].split(GSKeywords.SERIALIZE_ELEMENT_SEPARATOR)).stream().map(String::trim)
							.toList());
		}
		return mapper;
	}

	/**
	 * Gets the ordered aggregate.
	 *
	 * @param node
	 *            the node
	 * @return the ordered aggregate
	 */
	/*
	 * Get the aggregate map for mapped demographic attribute
	 */
	private Map<String, Collection<String>> getOrderedAggregate(final JsonNode node) {
		JsonNode mapArray = this.validateMapper(node);
		LinkedHashMap<String, Collection<String>> mapper = new LinkedHashMap<>();
		int i = 0;
		while (mapArray.has(i)) {
			String[] keyVal = mapArray.get(i++).asText().split(GSKeywords.SERIALIZE_KEY_VALUE_SEPARATOR);
			if (keyVal.length != 2)
				throw new IllegalArgumentException("Not a key / value match but has " + keyVal.length + " match");
			mapper.put(keyVal[0].trim(), Arrays.asList(keyVal[1].split(GSKeywords.SERIALIZE_ELEMENT_SEPARATOR)).stream()
					.map(String::trim).toList());
		}
		return mapper;
	}

	/**
	 * Validate mapper.
	 *
	 * @param node
	 *            the node
	 * @return the json node
	 */
	/*
	 * Check if given node is a proper mapper Object
	 */
	private JsonNode validateMapper(final JsonNode node) {
		JsonNode mapArray = node.findValue(IAttributeMapper.THE_MAP);
		if (!mapArray.isArray())
			throw new IllegalArgumentException("Trying to unmap the mapper but cannot access array mapping: "
					+ "node type instade is " + mapArray.getNodeType());
		return mapArray;
	}

	/*
	 * -------- * EMERGENT * --------
	 */

	/**
	 * Gets the emergent attribute.
	 *
	 * @param node
	 *            the node
	 * @return the emergent attribute
	 * @throws JsonParseException
	 *             the json parse exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws JsonProcessingException
	 *             the json processing exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	/*
	 * Build emergent attribute
	 */
	private EmergentAttribute<? extends IValue, ?, ?> getEmergentAttribute(final JsonNode node) throws IOException {

		String name = this.getName(node);

		JsonNode function = node.get(EmergentAttribute.FUNCTION_LABEL);
		JsonNode transposer = node.get(EmergentAttribute.TRANSPOSER_LABEL);
		JsonNode mapping = function.get(IGSValueFunction.MAPPING);

		EmergentAttribute<? extends IValue, ?, ?> att = null;

		String type = function.get(IGSValueFunction.ID).textValue();
		Attribute<? extends IValue> refAtt = null;
		if (function.has(MappedAttribute.REF)) {
			refAtt = DES_DEMO_ATTRIBUTES.get(function.get(MappedAttribute.REF).asText());
		}

		switch (type) {
			case CountValueFunction.SELF:
				if (refAtt == null) {
					att = AttributeFactory.getFactory().createSizeAttribute(name);
				} else {
					att = AttributeFactory.getFactory().createCountAttribute(name,
							this.getValues(node.findValue(Attribute.SELF)), this.getCountMapping(mapping),
							getCollectionTransposer(transposer));
				}
				break;
			case AggregateValueFunction.SELF:
				att = AttributeFactory.getFactory().createAggregatedValueOfAttribute(name, refAtt,
						this.getAggrgatorFunction(function.get(AggregateValueFunction.AGG)),
						getCollectionTransposer(transposer));
				break;
			case EntityValueFunction.SELF:
				if (GSKeywords.IDENTITY.equals(mapping.get(0).asText())) {
					att = AttributeFactory.getFactory().createValueOfAttribute(name, refAtt,
							getEntityTransposer(transposer));
				} else {
					att = AttributeFactory.getFactory().createValueOfAttribute(name, refAtt,
							this.getValues(node.findValue(Attribute.SELF)), this.getMapping(mapping),
							getEntityTransposer(transposer));
				}
				break;
			default:
				throw new IllegalStateException(
						"Emergent function type " + function.get(IGSValueFunction.ID).textValue() + " is unrecognized");
		}

		return att;
	}

	/**
	 * Gets the mapping.
	 *
	 * @param mapping
	 *            the mapping
	 * @return the mapping
	 */
	/*
	 *
	 */
	private Map<String, String> getMapping(final JsonNode mapping) {
		if (!mapping.isArray()) throw new GenstarException("Error when deserializing mapping: " + mapping.toString());
		Map<String, String> outputMap = new HashMap<>();
		int i = 0;
		while (mapping.has(i)) {
			String[] keyVal = mapping.get(i++).asText().split(GSKeywords.SERIALIZE_KEY_VALUE_SEPARATOR);
			if (keyVal.length != 2)
				throw new IllegalArgumentException("Not a key / value match but has " + keyVal.length + " match");
			outputMap.put(keyVal[0].trim(), keyVal[1].trim());
		}
		return outputMap;
	}

	/**
	 * Gets the count mapping.
	 *
	 * @param mapping
	 *            the mapping
	 * @return the count mapping
	 * @throws JsonParseException
	 *             the json parse exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	/*
	 *
	 */
	private Map<Integer, String> getCountMapping(final JsonNode mapping) {
		if (!mapping.isArray()) throw new GenstarException("Error when deserializing mapping: " + mapping.toString());
		Map<Integer, String> outputMap = new HashMap<>();
		int i = 0;
		while (mapping.has(i)) {
			String[] keyVal = mapping.get(i++).asText().split(GSKeywords.SERIALIZE_KEY_VALUE_SEPARATOR);
			if (keyVal.length != 2)
				throw new IllegalArgumentException("Not a key / value match but has " + keyVal.length + " match");
			outputMap.put(Integer.valueOf(keyVal[0].trim()), keyVal[1].trim());
		}
		return outputMap;
	}

	/**
	 * Gets the aggrgator function.
	 *
	 * @param <V>
	 *            the value type
	 * @param function
	 *            the function
	 * @return the aggrgator function
	 * @throws JsonParseException
	 *             the json parse exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings ("unchecked")
	private <V extends IValue> IAggregatorValueFunction<V> getAggrgatorFunction(final JsonNode function)
			throws IOException {
		return new ObjectMapper().readValue(function.toString(), IAggregatorValueFunction.class);
	}

	/**
	 * Gets the collection transposer.
	 *
	 * @param node
	 *            the node
	 * @return the collection transposer
	 * @throws JsonParseException
	 *             the json parse exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws JsonProcessingException
	 *             the json processing exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	/*
	 * Retrieve a collection transposer
	 */
	private IGSEntitySelector<Collection<IEntity<? extends IAttribute<? extends IValue>>>, ?>
			getCollectionTransposer(final JsonNode node) throws IOException {

		IGSEntitySelector<Collection<IEntity<? extends IAttribute<? extends IValue>>>, ?> transposer = null;

		String type = node.get(IGSEntitySelector.TYPE).asText();

		JsonNode transNode = node.get(IGSEntitySelector.MATCHERS);
		String transType = transNode.get(IGSEntityMatcher.TYPE_LABEL).asText();

		MatchType matchType = MatchType.valueOf(node.get(IGSEntitySelector.MATCH_TYPE).asText());
		ImplicitEntityComparator comparator = this.getComparator(node.get(IGSEntitySelector.COMPARATOR));

		if (Objects.equals(type, GSMatchSelection.SELF)) throw new IllegalArgumentException(
				"Trying to deserialize transposer to collection of sub entities with a selection filter");

		switch (type) {
			case GSMatchFilter.SELF:
				if (Objects.equals(transType, AttributeVectorMatcher.SELF)) {
					transposer = new GSMatchFilter<>(
							new AttributeVectorMatcher(this.getValueMatchers(node.get(IGSEntityMatcher.VECTOR_LABEL))),
							matchType);
				} else if (Objects.equals(transType, TagMatcher.SELF)) {
					transposer = new GSMatchFilter<>(
							new TagMatcher(this.getTagMatchers(node.get(IGSEntityMatcher.VECTOR_LABEL))), matchType);
				}
				if (comparator == null && transposer != null) { transposer.setComparator(comparator); }
				return transposer;
			case GSNoFilter.SELF:
				return new GSNoFilter();
			default:
				throw new GenstarException("Deserialization failed to create IGSEntityTransposer of type " + type);
		}

	}

	/**
	 * Gets the entity transposer.
	 *
	 * @param node
	 *            the node
	 * @return the entity transposer
	 * @throws JsonParseException
	 *             the json parse exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws JsonProcessingException
	 *             the json processing exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	/*
	 * Retrieve entity transposer
	 */
	private IGSEntitySelector<IEntity<? extends IAttribute<? extends IValue>>, ?>
			getEntityTransposer(final JsonNode node) throws IOException {

		IGSEntitySelector<IEntity<? extends IAttribute<? extends IValue>>, ?> transposer = null;

		String tType = node.get(IGSEntitySelector.TYPE).asText();

		JsonNode nodeMatcher = node.get(IGSEntitySelector.MATCHERS);
		String mType = nodeMatcher.get(IGSEntityMatcher.TYPE_LABEL).asText();

		MatchType matchType = MatchType.valueOf(node.get(IGSEntitySelector.MATCH_TYPE).asText());
		ImplicitEntityComparator comparator = this.getComparator(node.get(IGSEntitySelector.COMPARATOR));

		if (!GSMatchSelection.SELF.equals(tType)) throw new IllegalArgumentException(
				"Trying to deserialize entity transposer of sub entities with a filter mechanism : " + tType);

		transposer = switch (mType) {
			case AttributeVectorMatcher.SELF -> new GSMatchSelection<>(
					new AttributeVectorMatcher(this.getValueMatchers(nodeMatcher.get(IGSEntityMatcher.VECTOR_LABEL))),
					matchType);
			case TagMatcher.SELF -> new GSMatchSelection<>(
					new TagMatcher(this.getTagMatchers(nodeMatcher.get(IGSEntityMatcher.VECTOR_LABEL))), matchType);
			default -> throw new GenstarException("Trying to deserialize " + IGSEntityMatcher.class.getSimpleName()
					+ " with unkown match type: " + mType);
		};

		transposer.setComparator(comparator);

		return transposer;
	}

	/**
	 * Gets the value matchers.
	 *
	 * @param ArrayNode
	 *            the array node
	 * @return the value matchers
	 */
	/*
	 * Retrieve the value in a matcher array
	 */
	private IValue[] getValueMatchers(final JsonNode ArrayNode) {
		if (!ArrayNode.isArray()) throw new IllegalArgumentException(
				"This node is not an array of matchers (node type is " + ArrayNode.getNodeType() + ")");
		List<IValue> values = new ArrayList<>();
		int i = 0;
		while (ArrayNode.has(i)) {
			String val = ArrayNode.get(i++).asText();
			Attribute<? extends IValue> attribute = DES_DEMO_ATTRIBUTES.values().stream()
					.filter(att -> att.getValueSpace().contains(val)).findFirst().orElse(null);
			if (attribute != null) {
				IValue value = attribute.getValueSpace().getValue(val);
				values.add(value);
			}
		}
		return values.toArray(new IValue[values.size()]);
	}

	/**
	 * Gets the tag matchers.
	 *
	 * @param ArrayNode
	 *            the array node
	 * @return the tag matchers
	 */
	private EntityTag[] getTagMatchers(final JsonNode ArrayNode) {
		if (!ArrayNode.isArray()) throw new IllegalArgumentException(
				"This node is not an array of matchers (node type is " + ArrayNode.getNodeType() + ")");
		List<EntityTag> tags = new ArrayList<>();
		int i = 0;
		while (ArrayNode.has(i)) {
			String val = ArrayNode.get(i++).asText();
			tags.add(EntityTag.valueOf(val));
		}
		return tags.toArray(new EntityTag[tags.size()]);
	}

	/**
	 * Gets the comparator.
	 *
	 * @param comparatorNode
	 *            the comparator node
	 * @return the comparator
	 * @throws JsonParseException
	 *             the json parse exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IllegalArgumentException
	 *             the illegal argument exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	/*
	 * TODO: enable deserialization of Hamming distance based comparator
	 */
	private ImplicitEntityComparator getComparator(final JsonNode comparatorNode)
			throws IllegalArgumentException, IOException {

		ImplicitEntityComparator comparator = new ImplicitEntityComparator();

		if (comparatorNode.has(GSKeywords.CONTENT)
				&& GSKeywords.DEFAULT.equals(comparatorNode.get(GSKeywords.CONTENT).asText()))
			return comparator;

		/*
		 * Attribute and relationship (reverse or not) to comparison process
		 */
		JsonNode arrayAttributes = comparatorNode.get(ImplicitEntityComparator.ATTRIBUTES_REF);
		int index = -1;
		Map<IAttribute<? extends IValue>, Boolean> attributes = new HashMap<>();
		while (arrayAttributes.has(++index)) {
			String[] entry = arrayAttributes.get(index).asText().split(GSKeywords.SERIALIZE_KEY_VALUE_SEPARATOR);
			attributes.put(DES_DEMO_ATTRIBUTES.get(entry[0]), Boolean.valueOf(entry[1]));
		}

		attributes.entrySet().stream().forEach(entry -> comparator.setAttribute(entry.getKey(), entry.getValue()));

		/*
		 * Custom comparison function for specific value type
		 */
		JsonNode arrayFunctions = comparatorNode.get(ImplicitEntityComparator.COMP_FUNCTIONS);
		index = -1;
		Collection<IComparatorFunction<? extends IValue>> functions = new HashSet<>();
		ObjectMapper om = new ObjectMapper();
		while (arrayFunctions.has(++index)) {
			functions.add(om.readerFor(IComparatorFunction.class).readValue(arrayFunctions.get(index).asText()));
		}

		functions.stream().forEach(comparator::setComparatorFunction);

		return comparator;
	}

}
