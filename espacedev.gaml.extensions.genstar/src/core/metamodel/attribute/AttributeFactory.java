/*******************************************************************************************************
 *
 * AttributeFactory.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package core.metamodel.attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import core.metamodel.attribute.emergent.AggregateValueFunction;
import core.metamodel.attribute.emergent.CompositeValueFunction;
import core.metamodel.attribute.emergent.CountValueFunction;
import core.metamodel.attribute.emergent.EntityValueFunction;
import core.metamodel.attribute.emergent.aggregator.IAggregatorValueFunction;
import core.metamodel.attribute.emergent.filter.GSMatchFilter;
import core.metamodel.attribute.emergent.filter.GSMatchSelection;
import core.metamodel.attribute.emergent.filter.GSNoFilter;
import core.metamodel.attribute.emergent.filter.IGSEntitySelector;
import core.metamodel.attribute.emergent.filter.predicate.GSMatchPredicate;
import core.metamodel.attribute.mapper.AggregateMapper;
import core.metamodel.attribute.mapper.RecordMapper;
import core.metamodel.attribute.mapper.UndirectedMapper;
import core.metamodel.attribute.mapper.value.EncodedValueMapper;
import core.metamodel.attribute.mapper.value.NumericValueMapper;
import core.metamodel.attribute.record.RecordAttribute;
import core.metamodel.entity.IEntity;
import core.metamodel.entity.comparator.ImplicitEntityComparator;
import core.metamodel.entity.matcher.AttributeVectorMatcher;
import core.metamodel.entity.matcher.MatchType;
import core.metamodel.entity.matcher.TagMatcher;
import core.metamodel.entity.tag.EntityTag;
import core.metamodel.value.IValue;
import core.metamodel.value.IValueSpace;
import core.metamodel.value.binary.BinarySpace;
import core.metamodel.value.binary.BooleanValue;
import core.metamodel.value.categoric.NominalSpace;
import core.metamodel.value.categoric.NominalValue;
import core.metamodel.value.categoric.OrderedSpace;
import core.metamodel.value.categoric.OrderedValue;
import core.metamodel.value.categoric.template.GSCategoricTemplate;
import core.metamodel.value.numeric.ContinuousSpace;
import core.metamodel.value.numeric.ContinuousValue;
import core.metamodel.value.numeric.IntegerSpace;
import core.metamodel.value.numeric.IntegerValue;
import core.metamodel.value.numeric.RangeSpace;
import core.metamodel.value.numeric.RangeValue;
import core.metamodel.value.numeric.RangeValue.RangeBound;
import core.metamodel.value.numeric.template.GSRangeTemplate;
import core.util.GSKeywords;
import core.util.data.GSDataParser;
import core.util.data.GSEnumDataType;
import core.util.exception.GSIllegalRangedData;
import core.util.exception.GenstarException;

/**
 * Main factory to build attribute. Anyone that wants to build attribute should refers to one method below:
 * <p>
 * 1: The 3 methods that return general unidentified value type attribute (i.e. attribute that contains IValue) <br>
 * 2: Each local methods that return specific value type attribute
 *
 * @author kevinchapuis
 *
 */
public class AttributeFactory {

	/** The gaf. */
	private static AttributeFactory gaf = new AttributeFactory();

	/** The NI us. */
	public static Map<String, IAttribute<? extends IValue>> NIUs = new HashMap<>();

	/** The size att. */
	public static Map<String, EmergentAttribute<IntegerValue, Collection<IEntity<? extends IAttribute<? extends IValue>>>, ?>> SIZE_ATT =
			new HashMap<>();

	/**
	 * Instantiates a new attribute factory.
	 */
	private AttributeFactory() {}

	/**
	 * Singleton pattern to setup factory
	 *
	 * @return
	 */
	public static AttributeFactory getFactory() { return gaf; }

	/**
	 * Static way to innitialize <i> not in universe <i/> attribute
	 * <p>
	 * WARNING: should not be used in any population generation, but just to manipulate {@link IValue}
	 *
	 * @param type
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	public static <V extends IValue> Attribute<V> createNIU(final Class<V> type) {
		String name = type.getSimpleName() + GSKeywords.NIU;
		if (NIUs.containsKey(name)) return (Attribute<V>) NIUs.get(name);
		if (GSEnumDataType.Integer.getGenstarType().equals(type)) {
			Attribute<IntegerValue> att = new Attribute<>(name);
			att.setValueSpace(new IntegerSpace(att));
			return (Attribute<V>) NIUs.put(name, att);
		}
		if (GSEnumDataType.Continue.getGenstarType().equals(type)) {
			if (NIUs.containsKey(name)) return (Attribute<V>) NIUs.get(name);
			return (Attribute<V>) NIUs.put(name, new Attribute<ContinuousValue>(name));
		}
		if (!GSEnumDataType.Order.getGenstarType().equals(type) && !GSEnumDataType.Nominal.getGenstarType().equals(type)
				&& !GSEnumDataType.Boolean.getGenstarType().equals(type)
				&& !GSEnumDataType.Range.getGenstarType().equals(type))
			throw new GenstarException(type.getCanonicalName() + " has not any "
					+ GSEnumDataType.class.getCanonicalName() + " equivalent");
		if (NIUs.containsKey(name)) return (Attribute<V>) NIUs.get(name);
		return (Attribute<V>) NIUs.put(name, new Attribute<V>(name));
	}

	/**
	 * Main method to create attribute with default parameters
	 *
	 * @param name
	 * @param dataType
	 * @return
	 * @throws GSIllegalRangedData
	 */
	public Attribute<? extends IValue> createAttribute(final String name, final GSEnumDataType dataType)
			throws GSIllegalRangedData {
		// Attribute<? extends IValue> attribute = null;
		return switch (dataType) {
			case Integer -> createIntegerAttribute(name);
			case Continue -> createContinueAttribute(name);
			case Order -> createOrderedAttribute(name, new GSCategoricTemplate());
			case Nominal -> createNominalAttribute(name, new GSCategoricTemplate());
			case Range -> throw new IllegalArgumentException("Cannot create range without values to setup template");
			case Boolean -> createBooleanAttribute(name);
			default -> throw new GenstarException("Creation attribute failure");
		};
	}

	/**
	 * Main method to create attribute with default parameters
	 *
	 * @param name
	 * @param dataType
	 * @param values
	 * @return
	 * @throws GSIllegalRangedData
	 */
	public Attribute<? extends IValue> createAttribute(final String name, final GSEnumDataType dataType,
			final List<String> values, final List<Object> actualValues) throws GSIllegalRangedData {

		if (actualValues == null) return createAttribute(name, dataType, values);

		assert values.size() == actualValues.size();

		Attribute<? extends IValue> attribute = null;
		try {
			attribute = this.createAttribute(name, dataType);
		} catch (IllegalArgumentException e) {
			attribute = this.createRangeAttribute(name, new GSDataParser().getRangeTemplate(values));
		}
		final IValueSpace<? extends IValue> vs = attribute.getValueSpace();

		for (int i = 0; i < values.size(); i++) {
			IValue val = vs.addValue(values.get(i));
			val.setActualValue(actualValues.get(i));
		}
		// System.err.println("["+AttributeFactory.class.getSimpleName()+"#createAttribute(...)] => "+name+"
		// "+dataType);
		return attribute;
	}

	/**
	 * Main method to create an attribute with default parameters
	 *
	 * @param name
	 * @param dataType
	 * @param values
	 * @return
	 * @throws GSIllegalRangedData
	 */
	public Attribute<? extends IValue> createAttribute(final String name, final GSEnumDataType dataType,
			final List<String> values) throws GSIllegalRangedData {

		Attribute<? extends IValue> attribute = null;
		try {
			attribute = this.createAttribute(name, dataType);
		} catch (IllegalArgumentException e) {
			attribute = this.createRangeAttribute(name, new GSDataParser().getRangeTemplate(values));
		}
		final IValueSpace<? extends IValue> vs = attribute.getValueSpace();
		values.stream().forEach(val -> vs.addValue(val));
		// System.err.println("["+AttributeFactory.class.getSimpleName()+"#createAttribute(...)] => "+name+"
		// "+dataType);
		return attribute;
	}

	/**
	 * Unsafe cast based creator
	 * <p>
	 * WARNING: all as possible, trying not to use this nasty creator !!!
	 *
	 * @param name
	 * @param type
	 * @return
	 * @throws GSIllegalRangedData
	 */
	@SuppressWarnings ("unchecked")
	public <V extends IValue> Attribute<V> createAttribute(final String name, final List<String> values,
			final Class<V> type) throws GSIllegalRangedData {
		Attribute<V> attribute = null;
		if (GSEnumDataType.Integer.getGenstarType().equals(type)) {
			attribute = (Attribute<V>) createIntegerAttribute(name);
		} else if (GSEnumDataType.Continue.getGenstarType().equals(type)) {
			attribute = (Attribute<V>) createContinueAttribute(name);
		} else if (GSEnumDataType.Order.getGenstarType().equals(type)) {
			attribute = (Attribute<V>) createOrderedAttribute(name, new GSCategoricTemplate());
		} else if (GSEnumDataType.Nominal.getGenstarType().equals(type)) {
			attribute = (Attribute<V>) createNominalAttribute(name, new GSCategoricTemplate());
		} else if (GSEnumDataType.Boolean.getGenstarType().equals(type)) {
			attribute = (Attribute<V>) createBooleanAttribute(name);
		} else if (GSEnumDataType.Range.getGenstarType().equals(type)) {
			attribute = (Attribute<V>) createRangeAttribute(name, values);
		} else
			throw new GenstarException(type.getCanonicalName() + " has not any "
					+ GSEnumDataType.class.getCanonicalName() + " equivalent");
		final IValueSpace<V> vs = attribute.getValueSpace();
		values.stream().forEach(val -> vs.addValue(val));
		return attribute;
	}

	/**
	 * Create an attribute with encoded form (OTO mapping without being a mapped attribute). Values can have several
	 * encoding form - one main and other string based value using {@link EncodedValueMapper} \p WARNING: default
	 * records are not provided for integer and continuous value attribute
	 *
	 * @param name:
	 *            the name of the attribute
	 * @param dataType:
	 *            the type of the attribute values
	 * @param values:
	 *            the values of the attribute
	 * @param record:
	 *            the mapping between encoded form and corresponding value
	 * @return
	 * @throws GSIllegalRangedData
	 */
	public Attribute<? extends IValue> createAttribute(final String name, final GSEnumDataType dataType,
			final List<String> values, final Map<String, String> record) throws GSIllegalRangedData {
		return switch (dataType) {
			case Order -> createOrderedAttribute(name, new GSCategoricTemplate(), values, record);
			case Nominal -> createNominalAttribute(name, new GSCategoricTemplate(), record);
			case Range -> createRangeAttribute(name, record);
			case Boolean -> createBooleanAttribute(name, record);
			default -> throw new IllegalArgumentException(
					"Cannot create record attribute for " + dataType + " type of value attribute");
		};
	}

	/**
	 * Main method to create mapped (STS) attribute
	 *
	 * @see UndirectedMapper
	 *
	 * @param string
	 * @param type
	 * @param record
	 * @param referent
	 * @param map
	 * @return
	 * @throws GSIllegalRangedData
	 */
	public <V extends IValue> MappedAttribute<? extends IValue, V> createSTSMappedAttribute(final String name,
			final GSEnumDataType dataType, final Attribute<V> referent,
			final Map<Collection<String>, Collection<String>> map) throws GSIllegalRangedData {
		// MappedAttribute<? extends IValue, V> attribute = null;
		return switch (dataType) {
			case Integer -> createIntegerAttribute(name, referent, map);
			case Continue -> createContinueAttribute(name, referent, map);
			case Order -> createOrderedAttribute(name, referent,
					map.entrySet().stream().collect(Collectors.toMap(entry -> new ArrayList<>(entry.getKey()),
							Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)));
			case Nominal -> createNominalAttribute(name, new GSCategoricTemplate(), referent, map);
			case Range -> createRangeAttribute(name,
					new GSDataParser().getRangeTemplate(map.keySet().stream().flatMap(Collection::stream).toList()),
					referent, map);
			case Boolean -> createBooleanAttribute(name, referent, map);
			default -> throw new GenstarException("Cannot instanciate " + dataType + " data type mapped attribute");
		};
	}

	/**
	 * Main method to create mapped (STS) attribute with encoded forms for values using {@link EncodedValueMapper}
	 *
	 * @param name:
	 *            the name of the attribute
	 * @param dataType:
	 *            the type of attribute's value
	 * @param referent:
	 *            the referent attribute for mapping
	 * @param map:
	 *            the mapping between values
	 * @param record:
	 *            the endoded forms of values
	 * @return
	 * @throws GSIllegalRangedData
	 */
	public <V extends IValue> MappedAttribute<? extends IValue, V> createSTSMappedAttribute(final String name,
			final GSEnumDataType dataType, final Attribute<V> referent,
			final Map<Collection<String>, Collection<String>> map, final Map<String, String> record)
			throws GSIllegalRangedData {
		MappedAttribute<? extends IValue, V> att = this.createSTSMappedAttribute(name, dataType, referent, map);
		for (String rec : record.keySet()) { att.addRecords(record.get(rec), rec); }
		return att;
	}

	/**
	 * Main method to create mapped disaggregated attribute: several-to-one value relationship (STO)
	 *
	 * @param name
	 * @param dataType
	 * @param referentAttribute
	 * @param record
	 *            : several key can be bound to one value
	 * @return
	 * @throws GSIllegalRangedData
	 */
	public <V extends IValue> MappedAttribute<? extends IValue, V> createSTOMappedAttribute(final String name,
			final GSEnumDataType dataType, final Attribute<V> referentAttribute, final Map<String, String> record)
			throws GSIllegalRangedData {
		// MappedAttribute<? extends IValue, V> attribute = null;
		return switch (dataType) {
			case Integer -> createIntegerRecordAttribute(name, referentAttribute, record);
			case Continue -> createContinueRecordAttribute(name, referentAttribute, record);
			case Order -> createOrderedRecordAttribute(name, new GSCategoricTemplate(), referentAttribute,
					record.entrySet().stream().collect(
							Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)));
			case Nominal -> createNominalRecordAttribute(name, referentAttribute, record);
			case Range -> createRangeRecordAttribute(name, referentAttribute, record);
			case Boolean -> createBooleanRecordAttribute(name, referentAttribute, record);
			default -> throw new GenstarException("Cannot instanciate " + dataType + " data type mapped attribute");
		};
	}

	/**
	 * Create integer record attribute
	 *
	 * @see RecordAttribute
	 *
	 * @param name
	 * @param referentAttribute
	 * @return
	 * @throws GSIllegalRangedData
	 */
	public RecordAttribute<Attribute<? extends IValue>, Attribute<? extends IValue>> createRecordAttribute(
			final String name, final GSEnumDataType dataType, final Attribute<? extends IValue> referentAttribute)
			throws GSIllegalRangedData {
		return switch (dataType) {
			case Integer -> new RecordAttribute<>(name,
					this.createIntegerAttribute(name + GSKeywords.RECORD_NAME_EXTENSION), referentAttribute);
			case Continue -> new RecordAttribute<>(name,
					this.createContinueAttribute(name + GSKeywords.RECORD_NAME_EXTENSION), referentAttribute);
			default -> throw new IllegalArgumentException("Cannot create " + dataType
					+ " record attribute - suppose to be " + GSEnumDataType.Integer + " or " + GSEnumDataType.Continue);
		};
	}

	// ------------------------------------------------------------- //
	// BUILD METHOD //

	/*
	 * ----------------- * Integer attribute * -----------------
	 */

	/**
	 * Create integer value attribute
	 *
	 * @see IntegerSpace
	 * @see IntegerValue
	 *
	 * @param name
	 * @return
	 */
	public Attribute<IntegerValue> createIntegerAttribute(final String name) {
		Attribute<IntegerValue> attribute = new Attribute<>(name);
		attribute.setValueSpace(new IntegerSpace(attribute));
		return attribute;
	}

	/**
	 * Create integer mapped value attribute
	 *
	 * @see IntegerSpace
	 * @see IntegerValue
	 *
	 * @param name
	 * @return
	 */
	public <V extends IValue> MappedAttribute<IntegerValue, V> createIntegerAttribute(final String name,
			final Attribute<V> referentAttribute, final Map<Collection<String>, Collection<String>> map) {
		UndirectedMapper<IntegerValue, V> mapper = new UndirectedMapper<>();
		MappedAttribute<IntegerValue, V> attribute = new MappedAttribute<>(name, referentAttribute, mapper);
		attribute.setValueSpace(new IntegerSpace(attribute));
		mapper.setMapper(map.keySet().stream().collect(Collectors.toMap(
				key -> key.stream().map(val -> attribute.getValueSpace().addValue(val)).collect(Collectors.toSet()),
				key -> map.get(key).stream().map(val -> referentAttribute.getValueSpace().getValue(val))
						.collect(Collectors.toSet()))));
		mapper.setRelatedAttribute(attribute);
		return attribute;
	}

	/**
	 * Create integer record value attribute with given record mapping
	 *
	 * @param name
	 * @param referentAttribute
	 * @param mapper
	 * @return
	 */
	public <V extends IValue> MappedAttribute<IntegerValue, V> createIntegerRecordAttribute(final String name,
			final Attribute<V> referentAttribute, final Map<String, String> record) {
		MappedAttribute<IntegerValue, V> attribute =
				new MappedAttribute<>(name, referentAttribute, new RecordMapper<>());
		attribute.getAttributeMapper().setRelatedAttribute(attribute);
		attribute.setValueSpace(new IntegerSpace(attribute));

		for (Entry<String, String> entry : record.entrySet()) {
			IntegerValue val1 = attribute.getValueSpace().addValue(entry.getKey());
			V val2 = referentAttribute.getValueSpace().getValue(entry.getValue());
			attribute.addMappedValue(val1, val2);
		}
		return attribute;
	}

	/*
	 * ------------------ * Continue attribute * ------------------
	 */

	/**
	 * Create continued value attribute
	 *
	 * @see ContinuousSpace
	 * @see ContinuousValue
	 *
	 * @param name
	 * @return
	 */
	public Attribute<ContinuousValue> createContinueAttribute(final String name) {
		Attribute<ContinuousValue> ca = new Attribute<>(name);
		ca.setValueSpace(new ContinuousSpace(ca));
		return ca;
	}

	/**
	 * Create continued mapped value attribute
	 *
	 * @see IntegerSpace
	 * @see IntegerValue
	 *
	 * @param name
	 *            : name of the attribute to be created
	 * @param referentAttribute
	 *            : the attribute to map created attribute with
	 * @param mapper
	 *            : the map between values - must be a one (aggregated) to several (disaggregated) relationship
	 * @return
	 */
	public <V extends IValue> MappedAttribute<ContinuousValue, V> createContinueAttribute(final String name,
			final Attribute<V> referentAttribute, final Map<Collection<String>, Collection<String>> map) {
		UndirectedMapper<ContinuousValue, V> mapper = new UndirectedMapper<>();
		MappedAttribute<ContinuousValue, V> attribute = new MappedAttribute<>(name, referentAttribute, mapper);
		attribute.setValueSpace(new ContinuousSpace(attribute));
		mapper.setRelatedAttribute(attribute);
		mapper.setMapper(map.keySet().stream().collect(Collectors.toMap(
				key -> key.stream().map(val -> attribute.getValueSpace().addValue(val)).collect(Collectors.toSet()),
				key -> map.get(key).stream().map(val -> referentAttribute.getValueSpace().getValue(val))
						.collect(Collectors.toSet()))));
		return attribute;
	}

	/**
	 * Create continued aggregated value attribute
	 *
	 * @param name
	 * @param referentAttribute
	 * @param record
	 * @param mapper
	 * @return
	 */
	public MappedAttribute<ContinuousValue, ContinuousValue> createContinuedAgregatedAttribute(final String name,
			final Attribute<ContinuousValue> referentAttribute, final Map<String, Set<String>> map) {
		AggregateMapper<ContinuousValue> mapper = new AggregateMapper<>();
		MappedAttribute<ContinuousValue, ContinuousValue> attribute =
				new MappedAttribute<>(name, referentAttribute, mapper);
		attribute.setValueSpace(new ContinuousSpace(attribute));
		mapper.setRelatedAttribute(attribute);
		mapper.setMapper(map.keySet().stream()
				.collect(Collectors.toMap(key -> attribute.getValueSpace().addValue(key), key -> map.get(key).stream()
						.map(val -> referentAttribute.getValueSpace().getValue(val)).collect(Collectors.toSet()))));
		return attribute;
	}

	/**
	 * Create continued record value attribute with given record
	 *
	 * @param name
	 * @param referentAttribute
	 * @return
	 */
	public <V extends IValue> MappedAttribute<ContinuousValue, V> createContinueRecordAttribute(final String name,
			final Attribute<V> referentAttribute, final Map<String, String> record) {
		MappedAttribute<ContinuousValue, V> attribute =
				new MappedAttribute<>(name, referentAttribute, new RecordMapper<>());
		attribute.getAttributeMapper().setRelatedAttribute(attribute);
		attribute.setValueSpace(new ContinuousSpace(attribute));
		record.keySet().stream().forEach(key -> attribute.addMappedValue(attribute.getValueSpace().addValue(key),
				referentAttribute.getValueSpace().getValue(record.get(key))));
		return attribute;
	}

	/*
	 * ----------------- * Boolean attribute * -----------------
	 */

	/**
	 * Create boolean attribute
	 *
	 * @see BinarySpace
	 * @see BinaryValue
	 *
	 * @param name
	 * @return
	 */
	public Attribute<BooleanValue> createBooleanAttribute(final String name) {
		Attribute<BooleanValue> ba = new Attribute<>(name);
		ba.setValueSpace(new BinarySpace(ba));
		return ba;
	}

	/**
	 * Create boolean attribute with several encoded forms for values using {@link EncodedValueMapper}
	 *
	 * @param name:
	 *            the name of the attribute
	 * @param record:
	 *            the encoded form of values
	 * @return
	 */
	public Attribute<BooleanValue> createBooleanAttribute(final String name, final Map<String, String> record) {
		Attribute<BooleanValue> attB = this.createBooleanAttribute(name);
		for (String rec : record.keySet()) { attB.addRecords(record.get(rec), rec); }
		return attB;
	}

	// AGG ----------------

	/**
	 * Create boolean mapped value attribute
	 *
	 * @param name
	 * @param referentAttribute
	 * @param mapper
	 * @return
	 */
	public <V extends IValue> MappedAttribute<BooleanValue, V> createBooleanAttribute(final String name,
			final Attribute<V> referentAttribute, final Map<Collection<String>, Collection<String>> map) {
		UndirectedMapper<BooleanValue, V> mapper = new UndirectedMapper<>();
		MappedAttribute<BooleanValue, V> attribute = new MappedAttribute<>(name, referentAttribute, mapper);
		attribute.setValueSpace(new BinarySpace(attribute));
		mapper.setRelatedAttribute(attribute);
		mapper.setMapper(map.keySet().stream().collect(Collectors.toMap(
				key -> key.stream().map(val -> attribute.getValueSpace().addValue(val)).collect(Collectors.toSet()),
				key -> map.get(key).stream().map(val -> referentAttribute.getValueSpace().getValue(val))
						.collect(Collectors.toSet()))));
		return attribute;
	}

	/**
	 * Create boolean mapped attribute with several encoded forms for values using {@link EncodedValueMapper}
	 *
	 * @param name:
	 *            the name of the attribute
	 * @param referentAttribute:
	 *            the referent attribute with desaggregated values
	 * @param map:
	 *            the mapping between aggregated and desaggregated data
	 * @param record:
	 *            the encoded forms of values
	 * @return {@link MappedAttribute}
	 */
	public <V extends IValue> MappedAttribute<BooleanValue, V> createBooleanAttribute(final String name,
			final Attribute<V> referentAttribute, final Map<Collection<String>, Collection<String>> map,
			final Map<String, String> record) {
		MappedAttribute<BooleanValue, V> attribute = this.createBooleanAttribute(name, referentAttribute, map);
		for (String rec : record.keySet()) { attribute.addRecords(record.get(rec), rec); }
		return attribute;
	}

	// REC ----------------

	/**
	 * Create boolean record value attribute with given record
	 *
	 * @param name
	 * @param referentAttribute
	 * @return
	 */
	public <V extends IValue> MappedAttribute<BooleanValue, V> createBooleanRecordAttribute(final String name,
			final Attribute<V> referentAttribute, final Map<String, String> record) {
		MappedAttribute<BooleanValue, V> attribute =
				new MappedAttribute<>(name, referentAttribute, new RecordMapper<>());
		attribute.getAttributeMapper().setRelatedAttribute(attribute);
		attribute.setValueSpace(new BinarySpace(attribute));
		record.keySet().stream().forEach(key -> attribute.addMappedValue(attribute.getValueSpace().addValue(key),
				referentAttribute.getValueSpace().getValue(record.get(key))));
		return attribute;
	}

	/*
	 * ------------------------- * Ordered nominal attribute * -------------------------
	 */

	/**
	 * Create ordered value attribute
	 *
	 * @see OrderedSpace
	 * @see OrderedValue
	 *
	 * @param name
	 * @param ct
	 * @return
	 */
	public Attribute<OrderedValue> createOrderedAttribute(final String name, final GSCategoricTemplate ct) {
		Attribute<OrderedValue> oa = new Attribute<>(name);
		oa.setValueSpace(new OrderedSpace(oa, ct));
		return oa;
	}

	/**
	 * Create ordered value attribute with given values
	 *
	 * @see OrderedSpace
	 * @see OrderedValue
	 *
	 * @param name
	 * @param ct
	 * @return
	 */
	public Attribute<OrderedValue> createOrderedAttribute(final String name, final GSCategoricTemplate ct,
			final List<String> values) {
		Attribute<OrderedValue> oa = new Attribute<>(name);
		oa.setValueSpace(new OrderedSpace(oa, ct));
		values.stream().forEach(value -> oa.getValueSpace().addValue(value));
		return oa;
	}

	/**
	 * Create ordered attribute with several encoded forms (records) using {@link EncodedValueMapper}
	 *
	 * @param name:
	 *            the name of the attribute
	 * @param gsCategoricTemplate:
	 *            the template that enable new value to match a given pattern
	 * @param values:
	 *            the values
	 * @param record:
	 *            the encoded forms of value (records)
	 * @return
	 */
	public Attribute<OrderedValue> createOrderedAttribute(final String name,
			final GSCategoricTemplate gsCategoricTemplate, final List<String> values,
			final Map<String, String> record) {
		Attribute<OrderedValue> attO = this.createOrderedAttribute(name, gsCategoricTemplate, values);
		for (String rec : record.keySet()) { attO.addRecords(record.get(rec), rec); }
		return attO;
	}

	// AGG -----------------------

	/**
	 * Create ordered mapped value attribute
	 *
	 * @param name
	 * @param gsCategoricTemplate
	 * @param referentAttribute
	 * @param mapper
	 * @return
	 */
	public <V extends IValue> MappedAttribute<OrderedValue, V> createOrderedAttribute(final String name,
			final GSCategoricTemplate gsCategoricTemplate, final Attribute<V> referentAttribute,
			final LinkedHashMap<List<String>, Collection<String>> map) {
		UndirectedMapper<OrderedValue, V> mapper = new UndirectedMapper<>();
		MappedAttribute<OrderedValue, V> attribute = new MappedAttribute<>(name, referentAttribute, mapper);
		attribute.setValueSpace(new OrderedSpace(attribute, gsCategoricTemplate));
		mapper.setRelatedAttribute(attribute);

		LinkedHashMap<Collection<OrderedValue>, Collection<V>> newMap = new LinkedHashMap<>();
		for (Entry<List<String>, Collection<String>> entry : map.entrySet()) {
			List<OrderedValue> keys =
					entry.getKey().stream().map(val -> attribute.getValueSpace().addValue(val)).toList();
			List<V> values =
					entry.getValue().stream().map(val -> referentAttribute.getValueSpace().getValue(val)).toList();
			newMap.put(keys, values);
		}
		mapper.setMapper(newMap);
		return attribute;
	}

	/**
	 * Create ordered mapped value attribute
	 *
	 * @param name
	 * @param referentAttribute
	 * @param mapper
	 * @return
	 */
	public <V extends IValue> MappedAttribute<OrderedValue, V> createOrderedAttribute(final String name,
			final Attribute<V> referentAttribute, final LinkedHashMap<List<String>, Collection<String>> mapper) {
		return this.createOrderedAttribute(name, new GSCategoricTemplate(), referentAttribute, mapper);
	}

	/**
	 * Create ordered aggregated value attribute
	 *
	 * @param name
	 * @param referentAttribute
	 * @param record
	 * @param mapper
	 * @return
	 */
	public MappedAttribute<OrderedValue, OrderedValue> createOrderedAggregatedAttribute(final String name,
			final GSCategoricTemplate gsCategoricTemplate, final Attribute<OrderedValue> referentAttribute,
			final LinkedHashMap<String, List<String>> map) {
		AggregateMapper<OrderedValue> mapper = new AggregateMapper<>();
		MappedAttribute<OrderedValue, OrderedValue> attribute = new MappedAttribute<>(name, referentAttribute, mapper);
		attribute.setValueSpace(new OrderedSpace(attribute, gsCategoricTemplate));
		mapper.setRelatedAttribute(attribute);
		mapper.setMapper(map.keySet().stream().collect(Collectors.toMap(key -> attribute.getValueSpace().addValue(key),
				key -> map.get(key).stream().map(val -> referentAttribute.getValueSpace().getValue(val)).toList())));
		return attribute;
	}

	/**
	 * Create oredered aggregated value attribute
	 *
	 * @param name
	 * @param referentAttribute
	 * @param record
	 * @param mapper
	 * @return
	 */
	public MappedAttribute<OrderedValue, OrderedValue> createOrderedAggregatedAttribute(final String name,
			final Attribute<OrderedValue> referentAttribute, final LinkedHashMap<String, List<String>> mapper) {
		return this.createOrderedAggregatedAttribute(name, new GSCategoricTemplate(), referentAttribute, mapper);
	}

	/**
	 * Create oredered aggregated attribute with several encoded forms for values using {@link EncodedValueMapper}
	 *
	 * @param name:
	 *            the name of the attribute
	 * @param referentAttribute:
	 *            the referent attribute with disaggregated values
	 * @param mapper:
	 *            the mapping between aggregated and disaggregated values
	 * @param record:
	 *            the various encoded forms of values
	 * @return
	 */
	public MappedAttribute<OrderedValue, OrderedValue> createOrderedAggregatedAttribute(final String name,
			final Attribute<OrderedValue> referentAttribute, final LinkedHashMap<String, List<String>> mapper,
			final Map<String, String> record) {
		MappedAttribute<OrderedValue, OrderedValue> attribute =
				this.createOrderedAggregatedAttribute(name, new GSCategoricTemplate(), referentAttribute, mapper);
		for (String rec : record.keySet()) { attribute.addRecords(record.get(rec), rec); }
		return attribute;
	}

	/**
	 * Create ordered attribute with mapping to numerical data (int, float or range). The map should record mapping as
	 * follow:
	 * <ul>
	 * <li>{@link IntegerValue} : an unique int in the list at the first index</li>
	 * <li>{@link ContinuousValue} : an unique double in the list at the first index</li>
	 * <li>{@link RangeValue} : two int/double value making a range. null value represent bottom or top value range</li>
	 * </ul>
	 *
	 * @param name
	 * @param referentAttribute
	 * @param mapper
	 * @return
	 */
	public MappedAttribute<IValue, OrderedValue> createOrderedToNumericAttribute(final String name,
			final Attribute<OrderedValue> referentAttribute, final LinkedHashMap<String, List<Number>> map) {
		NumericValueMapper<IValue> mapper = new NumericValueMapper<>();
		MappedAttribute<IValue, OrderedValue> attribute = new MappedAttribute<>(name, referentAttribute, mapper);
		for (String ov : map.keySet()) {
			OrderedValue value = referentAttribute.getValueSpace().getValue(ov);
			List<Number> num = map.get(ov);
			if (num.size() == 1) {
				mapper.add(value, num.get(0));
			} else if (num.get(0) == null) {
				mapper.add(value, num.get(1), RangeBound.LOWER);
			} else if (num.get(1) == null) {
				mapper.add(value, num.get(0), RangeBound.UPPER);
			} else {
				mapper.add(value, num.get(0), num.get(1));
			}

		}
		return attribute;
	}

	// REC ---------------

	/**
	 * Create ordered record value attribute with given record
	 *
	 * @param name
	 * @param gsCategoricTemplate
	 * @param referentAttribute
	 * @param mapper
	 * @return
	 */
	public <V extends IValue> MappedAttribute<OrderedValue, V> createOrderedRecordAttribute(final String name,
			final GSCategoricTemplate gsCategoricTemplate, final Attribute<V> referentAttribute,
			final LinkedHashMap<String, String> record) {
		MappedAttribute<OrderedValue, V> attribute =
				new MappedAttribute<>(name, referentAttribute, new RecordMapper<>());
		attribute.setValueSpace(new OrderedSpace(attribute, gsCategoricTemplate));
		attribute.getAttributeMapper().setRelatedAttribute(attribute);
		record.keySet().stream().forEach(key -> attribute.addMappedValue(attribute.getValueSpace().getValue(key),
				referentAttribute.getValueSpace().getValue(record.get(key))));
		return attribute;
	}

	/**
	 * Create ordered record value attribute with given record and default template
	 *
	 * @param name
	 * @param gsCategoricTemplate
	 * @param referentAttribute
	 * @param mapper
	 * @return
	 */
	public <V extends IValue> MappedAttribute<OrderedValue, V> createOrderedRecordAttribute(final String name,
			final Attribute<V> referentAttribute, final LinkedHashMap<String, String> record) {
		return this.createOrderedRecordAttribute(name, new GSCategoricTemplate(), referentAttribute, record);
	}

	/*
	 * ----------------- * Nominal attribute * -----------------
	 */

	/**
	 * Create nominal value attribute
	 *
	 * @see NominalSpace
	 * @see NominalValue
	 *
	 * @param name
	 * @param ct
	 * @return
	 */
	public Attribute<NominalValue> createNominalAttribute(final String name, final GSCategoricTemplate ct) {
		Attribute<NominalValue> na = new Attribute<>(name);
		na.setValueSpace(new NominalSpace(na, ct));
		return na;
	}

	/**
	 * Create a nominal attribute with several encoded forms for values using {@link EncodedValueMapper}
	 *
	 * @param name:
	 *            the name of the attribute
	 * @param gsCategoricTemplate:
	 *            the template to match string value to a given pattern
	 * @param record:
	 *            the encoded forms of the value (records)
	 * @return
	 */
	public Attribute<NominalValue> createNominalAttribute(final String name,
			final GSCategoricTemplate gsCategoricTemplate, final Map<String, String> record) {
		Attribute<NominalValue> attN = this.createNominalAttribute(name, new GSCategoricTemplate());
		record.values().stream().forEach(v -> attN.getValueSpace().addValue(v));
		for (String rec : record.keySet()) { attN.addRecords(record.get(rec), rec); }
		return attN;
	}

	// AGG -----------------------

	/**
	 * Create nominal mapped value attribute
	 *
	 * @param name
	 * @param gsCategoricTemplate
	 * @param vs
	 * @param mapper
	 * @return
	 */
	public <V extends IValue> MappedAttribute<NominalValue, V> createNominalAttribute(final String name,
			final GSCategoricTemplate gsCategoricTemplate, final Attribute<V> referentAttribute,
			final Map<Collection<String>, Collection<String>> map) {
		UndirectedMapper<NominalValue, V> mapper = new UndirectedMapper<>();
		MappedAttribute<NominalValue, V> attribute = new MappedAttribute<>(name, referentAttribute, mapper);
		attribute.setValueSpace(new NominalSpace(attribute, gsCategoricTemplate));
		attribute.getAttributeMapper().setRelatedAttribute(attribute);
		mapper.setMapper(map.keySet().stream().collect(Collectors.toMap(
				key -> key.stream().map(val -> attribute.getValueSpace().addValue(val)).toList(),
				key -> map.get(key).stream().map(val -> referentAttribute.getValueSpace().getValue(val)).toList())));
		return attribute;
	}

	/**
	 * Create nominal aggregated value attribute
	 *
	 * @param name
	 * @param gsCategoricTemplate
	 * @param vs
	 * @param mapper
	 * @return
	 */
	public MappedAttribute<NominalValue, NominalValue> createNominalAggregatedAttribute(final String name,
			final GSCategoricTemplate gsCategoricTemplate, final Attribute<NominalValue> referentAttribute,
			final Map<String, Collection<String>> map) {
		AggregateMapper<NominalValue> mapper = new AggregateMapper<>();
		MappedAttribute<NominalValue, NominalValue> attribute = new MappedAttribute<>(name, referentAttribute, mapper);
		attribute.setValueSpace(new NominalSpace(attribute, gsCategoricTemplate));
		attribute.getAttributeMapper().setRelatedAttribute(attribute);
		mapper.setMapper(map.keySet().stream().collect(Collectors.toMap(key -> attribute.getValueSpace().addValue(key),
				key -> map.get(key).stream().map(val -> referentAttribute.getValueSpace().getValue(val)).toList())));
		return attribute;
	}

	/**
	 * Create nominal aggregated value attribute
	 *
	 * @param name
	 * @param referentAttribute
	 * @param mapper
	 * @return
	 */
	public MappedAttribute<NominalValue, NominalValue> createNominalAggregatedAttribute(final String name,
			final Attribute<NominalValue> referentAttribute, final Map<String, Collection<String>> mapper) {
		return this.createNominalAggregatedAttribute(name, new GSCategoricTemplate(), referentAttribute, mapper);
	}

	/**
	 * Create nominal aggregated attribute with several encoded forms for value using {@link EncodedValueMapper}
	 *
	 * @param name
	 * @param referentAttribute
	 * @param mapper
	 * @param record
	 * @return
	 */
	public MappedAttribute<NominalValue, NominalValue> createNominalAggregatedAttribute(final String name,
			final Attribute<NominalValue> referentAttribute, final Map<String, Collection<String>> mapper,
			final Map<String, String> record) {
		MappedAttribute<NominalValue, NominalValue> attribute =
				this.createNominalAggregatedAttribute(name, new GSCategoricTemplate(), referentAttribute, mapper);
		for (String rec : record.keySet()) { attribute.addRecords(record.get(rec), rec); }
		return attribute;
	}

	// REC ----------------------

	/**
	 * Create a nominal record value attribute with given mapping
	 *
	 * @param string
	 * @param attCouple
	 * @param mapper
	 * @return
	 */
	public <V extends IValue> MappedAttribute<NominalValue, V> createNominalRecordAttribute(final String name,
			final GSCategoricTemplate gsCategoricTemplate, final Attribute<V> referentAttribute,
			final Map<String, String> map) {
		MappedAttribute<NominalValue, V> attribute =
				new MappedAttribute<>(name, referentAttribute, new RecordMapper<>());
		attribute.setValueSpace(new NominalSpace(attribute, gsCategoricTemplate));
		attribute.getAttributeMapper().setRelatedAttribute(attribute);
		map.keySet().stream().forEach(key -> attribute.addMappedValue(attribute.getValueSpace().addValue(key),
				referentAttribute.getValueSpace().getValue(map.get(key))));
		return attribute;
	}

	/**
	 * Create a nominal record value attribute with given record and default template
	 *
	 * @param name
	 * @param referentAttribute
	 * @param mapper
	 * @return
	 */
	public <V extends IValue> MappedAttribute<NominalValue, V> createNominalRecordAttribute(final String name,
			final Attribute<V> referentAttribute, final Map<String, String> record) {
		return this.createNominalRecordAttribute(name, new GSCategoricTemplate(), referentAttribute, record);
	}

	/*
	 * ----------- * Range value * -----------
	 */

	/**
	 * Create range value attribute
	 *
	 * @see RangeSpace
	 * @see RangeValue
	 *
	 * @param name
	 * @param rt
	 * @return
	 */
	public Attribute<RangeValue> createRangeAttribute(final String name, final GSRangeTemplate rt) {
		Attribute<RangeValue> ra = new Attribute<>(name);
		ra.setValueSpace(new RangeSpace(ra, rt));
		return ra;
	}

	/**
	 * Create range value attribute with custom bottom and top bounds
	 *
	 * @see #createRangeAttribute(String, GSRangeTemplate)
	 *
	 * @param name
	 * @param rt
	 * @param bottomBound
	 * @param topBound
	 * @return
	 */
	public Attribute<RangeValue> createRangeAttribute(final String name, final GSRangeTemplate rt,
			final Number bottomBound, final Number topBound) {
		Attribute<RangeValue> ra = new Attribute<>(name);
		ra.setValueSpace(new RangeSpace(ra, rt, bottomBound, topBound));
		return ra;
	}

	/**
	 * Create range value attribute based on a list of range value
	 *
	 * @param name
	 * @param ranges
	 * @return
	 * @throws GSIllegalRangedData
	 */
	public Attribute<RangeValue> createRangeAttribute(final String name, final List<String> ranges)
			throws GSIllegalRangedData {
		Attribute<RangeValue> attribute = this.createRangeAttribute(name, new GSDataParser().getRangeTemplate(ranges));
		ranges.stream().forEach(value -> attribute.getValueSpace().addValue(value));
		((RangeSpace) attribute.getValueSpace()).consolidateRanges();
		return attribute;
	}

	/**
	 * Create range value attribute based on a list of range value
	 *
	 * @param name
	 * @param ranges
	 * @return
	 * @throws GSIllegalRangedData
	 */
	public Attribute<RangeValue> createRangeAttribute(final String name, final List<String> ranges,
			final Number bottomBound, final Number topBound) throws GSIllegalRangedData {
		Attribute<RangeValue> attribute =
				this.createRangeAttribute(name, new GSDataParser().getRangeTemplate(ranges), bottomBound, topBound);
		ranges.stream().forEach(value -> attribute.getValueSpace().addValue(value));
		((RangeSpace) attribute.getValueSpace()).consolidateRanges();
		return attribute;
	}

	/**
	 * Create range attribute with several encoded forms for values using {@link EncodedValueMapper}
	 *
	 * @param name:
	 *            the name of the attribute
	 * @param record:
	 *            the encoded forms of values (records)
	 * @return
	 * @throws GSIllegalRangedData
	 */
	public Attribute<RangeValue> createRangeAttribute(final String name, final Map<String, String> record)
			throws GSIllegalRangedData {
		Attribute<RangeValue> attR = this.createRangeAttribute(name, new ArrayList<>(record.values()));
		for (String rec : record.keySet()) { attR.addRecords(record.get(rec), rec); }
		return attR;
	}

	// AGG -----------------------

	/**
	 * Create mapped (STS) range value attribute
	 *
	 * @param name
	 * @param rangeTemplate
	 * @param vs
	 * @param mapper
	 * @return
	 */
	public <V extends IValue> MappedAttribute<RangeValue, V> createRangeAttribute(final String name,
			final GSRangeTemplate rangeTemplate, final Attribute<V> referentAttribute,
			final Map<Collection<String>, Collection<String>> map) {
		UndirectedMapper<RangeValue, V> mapper = new UndirectedMapper<>();
		MappedAttribute<RangeValue, V> attribute = new MappedAttribute<>(name, referentAttribute, mapper);
		attribute.setValueSpace(new RangeSpace(attribute, rangeTemplate));
		attribute.getAttributeMapper().setRelatedAttribute(attribute);
		mapper.setMapper(map.keySet().stream().collect(Collectors.toMap(
				key -> key.stream().map(val -> attribute.getValueSpace().addValue(val)).toList(),
				key -> map.get(key).stream().map(val -> referentAttribute.getValueSpace().getValue(val)).toList())));
		return attribute;
	}

	/**
	 * Create range aggregated (OTS) value attribute
	 *
	 * @param name
	 * @param gsCategoricTemplate
	 * @param vs
	 * @param mapper
	 * @return
	 */
	public MappedAttribute<RangeValue, RangeValue> createRangeAggregatedAttribute(final String name,
			final GSRangeTemplate rangeTemplate, final Attribute<RangeValue> referentAttribute,
			final Map<String, Collection<String>> map) {
		AggregateMapper<RangeValue> mapper = new AggregateMapper<>();
		MappedAttribute<RangeValue, RangeValue> attribute = new MappedAttribute<>(name, referentAttribute, mapper);
		RangeSpace refRs = (RangeSpace) referentAttribute.getValueSpace();
		attribute.setValueSpace(new RangeSpace(attribute, rangeTemplate, refRs.getMin(), refRs.getMax()));
		attribute.getAttributeMapper().setRelatedAttribute(attribute);
		mapper.setMapper(map.keySet().stream().collect(Collectors.toMap(key -> attribute.getValueSpace().addValue(key),
				key -> map.get(key).stream().map(val -> referentAttribute.getValueSpace().getValue(val)).toList())));
		return attribute;
	}

	/**
	 * Create range aggregated (OTS) value attribute with several encoded forms for values using
	 * {@link EncodedValueMapper}
	 *
	 * @param name
	 * @param rangeTemplate
	 * @param referentAttribute
	 * @param map
	 * @param record
	 * @return
	 */
	public MappedAttribute<RangeValue, RangeValue> createRangeAggregatedAttribute(final String name,
			final GSRangeTemplate rangeTemplate, final Attribute<RangeValue> referentAttribute,
			final Map<String, Collection<String>> map, final Map<String, String> record) {
		MappedAttribute<RangeValue, RangeValue> attribute =
				this.createRangeAggregatedAttribute(name, rangeTemplate, referentAttribute, map);
		for (String rec : record.keySet()) { attribute.addRecords(record.get(rec), rec); }
		return attribute;
	}

	/**
	 * Create range aggregated (OTS) value attribute
	 *
	 * @param name
	 * @param vs
	 * @param mapper
	 * @return
	 * @throws GSIllegalRangedData
	 */
	public MappedAttribute<RangeValue, RangeValue> createRangeAggregatedAttribute(final String name,
			final Attribute<RangeValue> referentAttribute, final Map<String, Collection<String>> map)
			throws GSIllegalRangedData {
		return this.createRangeAggregatedAttribute(name,
				new GSDataParser().getRangeTemplate(new ArrayList<>(map.keySet())), referentAttribute, map);
	}

	/**
	 * Create range aggregated (OTS) attribute with several encoded forms for values using {@link EncodedValueMapper}
	 *
	 * @param name
	 * @param referentAttribute
	 * @param map
	 * @param record
	 * @return
	 * @throws GSIllegalRangedData
	 */
	public MappedAttribute<RangeValue, RangeValue> createRangeAggregatedAttribute(final String name,
			final Attribute<RangeValue> referentAttribute, final Map<String, Collection<String>> map,
			final Map<String, String> record) throws GSIllegalRangedData {
		MappedAttribute<RangeValue, RangeValue> attribute =
				this.createRangeAggregatedAttribute(name, referentAttribute, map);
		for (String rec : record.keySet()) { attribute.addRecords(record.get(rec), rec); }
		return attribute;
	}

	/**
	 * Map to unknown referent attribute
	 *
	 * @param <V>
	 * @param name
	 * @param referentIntegers
	 * @param records
	 * @return
	 * @throws GSIllegalRangedData
	 */
	public <V extends IValue> MappedAttribute<RangeValue, V> createRangeAggregatedRecordAttribute(final String name,
			final Attribute<V> referent, final Map<String, Collection<String>> records) throws GSIllegalRangedData {
		UndirectedMapper<RangeValue, V> mapper = new UndirectedMapper<>();
		MappedAttribute<RangeValue, V> attribute = new MappedAttribute<>(name, referent, mapper);
		attribute.setValueSpace(
				new RangeSpace(attribute, new GSDataParser().getRangeTemplate(new ArrayList<>(records.keySet()))));
		attribute.getAttributeMapper().setRelatedAttribute(attribute);
		mapper.setMapper(records.keySet().stream()
				.collect(Collectors.toMap(key -> Arrays.asList(attribute.getValueSpace().addValue(key)),
						key -> records.get(key).stream().map(k -> referent.getValueSpace().getValue(k)).toList())));
		return attribute;
	}

	// REC

	/**
	 * Map a range attribute with a integer value based referent
	 *
	 * @param name
	 * @param referentIntegers
	 * @param records
	 * @return
	 * @throws GSIllegalRangedData
	 */
	public MappedAttribute<RangeValue, IntegerValue> createRangeToIntegerAggregateAttribute(final String name,
			final Attribute<IntegerValue> referentIntegers, final Map<String, Collection<String>> records)
			throws GSIllegalRangedData {

		UndirectedMapper<RangeValue, IntegerValue> mapper = new UndirectedMapper<>();

		MappedAttribute<RangeValue, IntegerValue> attribute = new MappedAttribute<>(name, referentIntegers, mapper);

		IntegerSpace refRs = (IntegerSpace) referentIntegers.getValueSpace();
		attribute.setValueSpace(
				new RangeSpace(attribute, new GSDataParser().getRangeTemplate(new ArrayList<>(records.keySet())),
						refRs.getMin(), refRs.getMax()));

		attribute.getAttributeMapper().setRelatedAttribute(attribute);
		mapper.setMapper(records.keySet().stream().collect(Collectors.toMap(
				key -> Arrays.asList(attribute.getValueSpace().addValue(key)),
				key -> records.get(key).stream().map(k -> referentIntegers.getValueSpace().getValue(k)).toList())));
		return attribute;
	}

	/*
	 * ----------------------- * EMERGENT ATTRIBUTE * -----------------------
	 */

	// EMERGENT COUNT

	/**
	 * Attribute for size of super-entitys
	 *
	 * @param name
	 *            : the name of the attribute
	 * @return
	 */
	public EmergentAttribute<IntegerValue, Collection<IEntity<? extends IAttribute<? extends IValue>>>, ?>
			createSizeAttribute(final String name) {

		if (!SIZE_ATT.containsKey(name)) {
			EmergentAttribute<IntegerValue, Collection<IEntity<? extends IAttribute<? extends IValue>>>, Object> attribute =
					new EmergentAttribute<>(name);
			attribute.setValueSpace(new IntegerSpace(attribute));
			attribute.setFunction(new CountValueFunction<>(attribute));
			attribute.setTransposer(new GSNoFilter());
			SIZE_ATT.put(name, attribute);
		}

		return SIZE_ATT.get(name);

	}

	/**
	 * Same as {@link #createCountAttribute(String)} but with a mapped attribute
	 *
	 * @param name
	 *            : the name of the attribute
	 * @param values
	 *            : the ordered collection of values
	 * @param mapping
	 *            : the mapping between count (int) and ordered values
	 * @return
	 */
	public EmergentAttribute<OrderedValue, Collection<IEntity<? extends IAttribute<? extends IValue>>>, ?>
			createCountAttribute(final String name, final List<String> values, final Map<Integer, String> mapping) {

		return this.createCountAttribute(name, values, mapping, new GSNoFilter());

	}

	/**
	 * Attribute that will count the number of sub-entities. As for any emergent attribute it is possible to filter
	 * agent before counting; makes it possible to number sub-entities that match any {@link IValue} predicate
	 *
	 * @param name
	 *            : the name of the attribute
	 * @param values
	 *            : the ordered collection of values
	 * @param mapping
	 *            : the mapping between count (int) and ordered values
	 * @param matches
	 *            : the matches to filter the sub-entities to be counted
	 * @return
	 */
	public EmergentAttribute<OrderedValue, Collection<IEntity<? extends IAttribute<? extends IValue>>>, IValue>
			createCountAttribute(final String name, final List<String> values, final Map<Integer, String> mapping,
					final IValue... matches) {

		return this.createCountAttribute(name, values, mapping,
				new GSMatchFilter<>(new AttributeVectorMatcher(matches), MatchType.getDefault()));

	}

	/**
	 * Attribute that will count the number of sub-entities. As for any emergent attribute it is possible to filter
	 * agent before counting; makes it possible to number sub-entities that match any {@link EntityTag} predicate
	 *
	 * @param name
	 *            : the name of the attribute
	 * @param values
	 *            : the ordered collection of values
	 * @param mapping
	 *            : the mapping between count (int) and ordered values
	 * @param matches
	 *            : the matches to filter the sub-entities to be counted
	 * @return
	 */
	public EmergentAttribute<OrderedValue, Collection<IEntity<? extends IAttribute<? extends IValue>>>, EntityTag>
			createCountAttribute(final String name, final List<String> values, final Map<Integer, String> mapping,
					final EntityTag... matches) {

		return this.createCountAttribute(name, values, mapping,
				new GSMatchFilter<>(new TagMatcher(matches), MatchType.getDefault()));

	}

	/**
	 * Attribute that will count the number of sub-entities. Sub entities can be filtered and selected using any
	 * {@link IGSEntitySelector} that will transpose a super-entity into a collection of sub-entity
	 *
	 * @param name
	 * @param referent
	 * @param mapper
	 * @param transposer
	 * @return
	 */
	public <T, U> EmergentAttribute<OrderedValue, Collection<IEntity<? extends IAttribute<? extends IValue>>>, T>
			createCountAttribute(final String name, final List<String> values, final Map<Integer, String> mapping,
					final IGSEntitySelector<Collection<IEntity<? extends IAttribute<? extends IValue>>>, T> transposer) {
		EmergentAttribute<OrderedValue, Collection<IEntity<? extends IAttribute<? extends IValue>>>, T> attribute =
				new EmergentAttribute<>(name);
		attribute.setValueSpace(new OrderedSpace(attribute, new GSCategoricTemplate()));
		values.forEach(value -> attribute.getValueSpace().addValue(value));
		Map<Integer, OrderedValue> mapper = mapping.keySet().stream().collect(
				Collectors.toMap(Function.identity(), k -> attribute.getValueSpace().getValue(mapping.get(k))));
		attribute.setFunction(new CountValueFunction<>(attribute, mapper));
		attribute.setTransposer(transposer);
		return attribute;
	}

	// EMERGENT VALUE FOR ATTRIBUTE

	/**
	 * Attribute that will retrieve the value of one particular sub-entity attribute. This is done following a
	 * {@link EntityTag} based filter
	 *
	 * @param name
	 *            : the name of the attribute
	 * @param referent
	 *            : the referent attribute (attribute of sub-entity to retrieve value from)
	 * @param tags
	 *            : the tags that will identify the individual to pick
	 * @return
	 */
	public <V extends IValue> EmergentAttribute<V, IEntity<? extends IAttribute<? extends IValue>>, EntityTag>
			createValueOfAttribute(final String name, final Attribute<V> referent, final EntityTag... tags) {

		return this.createValueOfAttribute(name, referent,
				new GSMatchSelection<>(new TagMatcher(tags), MatchType.getDefault()));

	}

	/**
	 * Attribute that will retrieve the value of one particular sub-entity attribute. This is done following a
	 * {@link IValue} based filter
	 *
	 * @param name
	 *            : the name of the attribute
	 * @param referent
	 *            : the referent attribute (attribute of sub-entity to retrieve value from)
	 * @param comparator
	 *            : the comparator to sort sub-entities tag matches and pick the first one
	 * @param tags
	 *            : the tags that will identify the individual to pick
	 *
	 * @param <V>
	 *            : the type of value this attribute is made of
	 *
	 * @return
	 */
	public <V extends IValue> EmergentAttribute<V, IEntity<? extends IAttribute<? extends IValue>>, IValue>
			createValueOfAttribute(final String name, final Attribute<V> referent, final IValue... matches) {

		return this.createValueOfAttribute(name, referent,
				new GSMatchSelection<>(new AttributeVectorMatcher(matches), MatchType.getDefault()));

	}

	/**
	 * Attribute that will retrieve the value of one particular sub-entity attribute.
	 *
	 * @param name
	 *            : the name of the attribute
	 * @param referent
	 *            : the referent attribute (attribute of sub-entity to retrieve value from)
	 * @param transposer
	 *            : filter that will select one sub-entity to be transposed
	 *
	 * @param <V>
	 *            : the type of value this attribute is made of
	 * @param <T>
	 *            : the predicate type to filter sub-entities
	 *
	 * @return
	 */
	public <V extends IValue, T> EmergentAttribute<V, IEntity<? extends IAttribute<? extends IValue>>, T>
			createValueOfAttribute(final String name, final Attribute<V> referent,
					final IGSEntitySelector<IEntity<? extends IAttribute<? extends IValue>>, T> transposer) {

		EmergentAttribute<V, IEntity<? extends IAttribute<? extends IValue>>, T> eAttribute =
				new EmergentAttribute<>(name);

		eAttribute.setValueSpace(referent.getValueSpace());
		eAttribute.setFunction(new EntityValueFunction<>(referent));
		eAttribute.setTransposer(transposer);

		return eAttribute;

	}

	/**
	 * Attribute that will get the value of one particular sub-entities. The selection is a two step process:
	 * <ul>
	 * <li>Select sub entities according to predicate: e.g. sub-entities with a particular attribute value or tagged as
	 * {@link EntityTag#Parent}
	 * <li>If several predicate matches, then sort them according to an {@link ImplicitEntityComparator} and pick the
	 * first one
	 * </ul>
	 *
	 *
	 * @param name
	 *            : the name of the attribute
	 * @param referent
	 *            : the referent attribute (attribute of sub-entity to retrieve value from)
	 * @param mapping
	 *            : the mapping between super-attribute and sub-attribute
	 * @param transposer
	 *            : the filter that will transpose super entity to one sub entity {@link IGSEntitySelector}
	 *
	 * @param <V>
	 *            : the type of value this attribute is made of
	 * @param <T>
	 *            : either {@link IValue} or {@link EntityTag}
	 *
	 * @return
	 */
	public <V extends IValue, T> EmergentAttribute<V, IEntity<? extends IAttribute<? extends IValue>>, T>
			createValueOfAttribute(final String name, final Attribute<V> referent, final List<String> values,
					final Map<String, String> mapping,
					final IGSEntitySelector<IEntity<? extends IAttribute<? extends IValue>>, T> transposer) {

		if (values.isEmpty() || mapping.isEmpty()) return this.createValueOfAttribute(name, referent, transposer);

		EmergentAttribute<V, IEntity<? extends IAttribute<? extends IValue>>, T> eAttribute =
				new EmergentAttribute<>(name);

		if (mapping.keySet().stream().anyMatch(key -> !referent.getValueSpace().contains(key)))
			throw new IllegalArgumentException("Trying to setup a irregular mapping: key(s) is (are) missing: "
					+ mapping.keySet().stream().filter(key -> !referent.getValueSpace().contains(key))
							.collect(Collectors.joining(GSKeywords.SERIALIZE_ELEMENT_SEPARATOR)));

		eAttribute.setValueSpace(referent.getValueSpace().clone(eAttribute));
		values.forEach(value -> eAttribute.getValueSpace().addValue(value));
		Map<V, V> mapper = mapping.keySet().stream().collect(Collectors.toMap(k -> referent.getValueSpace().getValue(k),
				k -> eAttribute.getValueSpace().getValue(mapping.get(k))));
		eAttribute.setFunction(new EntityValueFunction<>(eAttribute, referent, mapper));

		eAttribute.setTransposer(transposer);
		return eAttribute;

	}

	// EMERGENT AGGREGATE

	/**
	 * Attribute that aggregate input values into single output value based on a default aggregator. For example, it can
	 * be used to sum up the revenue of all individual of a household (and works even if it is integer, continuous or
	 * range value)
	 *
	 * see {@link IAggregatorValueFunction#getDefaultAggregator(Class)}
	 *
	 * @param name
	 *            : name of the attribute
	 * @param referent
	 *            : the input attribute
	 * @param matches
	 *            : the {@link IValue} matches to setup transposer
	 *
	 * @param <V>
	 *            : the value type of the attribute
	 *
	 * @return
	 */
	public <V extends IValue> EmergentAttribute<V, Collection<IEntity<? extends IAttribute<? extends IValue>>>, IValue>
			createAggregatedValueOfAttribute(final String name, final Attribute<V> inputAttribute,
					final IValue... matches) {

		return this.createAggregatedValueOfAttribute(name, inputAttribute,
				IAggregatorValueFunction.getDefaultAggregator(inputAttribute.getValueSpace().getTypeClass()),
				new GSMatchFilter<>(new AttributeVectorMatcher(matches), MatchType.getDefault()));

	}

	/**
	 * Attribute that aggregate input values into single output value based on a default aggregator. For example, it can
	 * be used to sum up the revenue of all individual of a household (and works even if it is integer, continuous or
	 * range value)
	 *
	 * see {@link IAggregatorValueFunction#getDefaultAggregator(Class)}
	 *
	 * @param name
	 *            : name of the attribute
	 * @param referent
	 *            : the input attribute
	 * @param matches
	 *            : the {@link EntityTag} matches to setup transposer
	 *
	 * @param <V>
	 *            : the value type of the attribute
	 *
	 * @return
	 */
	public <V extends IValue>
			EmergentAttribute<V, Collection<IEntity<? extends IAttribute<? extends IValue>>>, EntityTag>
			createAggregatedValueOfAttribute(final String name, final Attribute<V> inputAttribute,
					final EntityTag... matches) {

		return this.createAggregatedValueOfAttribute(name, inputAttribute,
				IAggregatorValueFunction.getDefaultAggregator(inputAttribute.getValueSpace().getTypeClass()),
				new GSMatchFilter<>(new TagMatcher(matches), MatchType.getDefault()));

	}

	/**
	 * Attribute that aggregate input values into single output value of same type based on a custom aggregator and a
	 * custom transposer
	 *
	 * @param name
	 *            : name of the attribute
	 * @param inputAttribute
	 *            : the input attribute
	 * @param aggFunction
	 *            : the custom function
	 * @param transposer
	 *            : the transposer
	 *
	 * @param <V>
	 *            The input and output value type
	 * @param <T>
	 *            The return type of the transposer
	 *
	 * @return
	 */
	public <V extends IValue, T> EmergentAttribute<V, Collection<IEntity<? extends IAttribute<? extends IValue>>>, T>
			createAggregatedValueOfAttribute(final String name, final Attribute<V> inputAttribute,
					final IAggregatorValueFunction<V> aggFunction,
					final IGSEntitySelector<Collection<IEntity<? extends IAttribute<? extends IValue>>>, T> transposer) {
		EmergentAttribute<V, Collection<IEntity<? extends IAttribute<? extends IValue>>>, T> eAttribute =
				new EmergentAttribute<>(name);
		eAttribute.setValueSpace(inputAttribute.getValueSpace().clone(eAttribute));
		eAttribute.setFunction(new AggregateValueFunction<>(aggFunction, inputAttribute));
		eAttribute.setTransposer(transposer);
		return eAttribute;
	}

	// EMERGENT COMPOSITE

	/**
	 * Creates a new Attribute object.
	 *
	 * @param name
	 *            the name
	 * @param values
	 *            the values
	 * @param type
	 *            the type
	 * @param predicates
	 *            the predicates
	 * @return the emergent attribute<? extends I value,?,?>
	 */
	public EmergentAttribute<? extends IValue, ?, ?> createTransposedValuesAttribute(final String name,
			final Collection<String> values, final GSEnumDataType type,
			final Map<Collection<GSMatchPredicate<?, ?>>, String> predicates) {
		return switch (type) {
			case Boolean -> createEmergentBoolean(name, values, predicates);
			case Continue -> createEmergentContinue(name, values, predicates);
			case Integer -> createEmergentInteger(name, values, predicates);
			case Nominal -> createEmergentNominal(name, values, predicates);
			case Order -> createEmergentOrder(name, values, predicates);
			case Range -> createEmergentRange(name, values, predicates);
			default -> throw new GenstarException("Creation attribute failure");
		};
	}

	/**
	 * Creates a new Attribute object.
	 *
	 * @param name
	 *            the name
	 * @param values
	 *            the values
	 * @param predicates
	 *            the predicates
	 * @return the emergent attribute<? extends I value,?,?>
	 */
	/*
	 * private emergent range attribute
	 */
	private EmergentAttribute<? extends IValue, ?, ?> createEmergentRange(final String name,
			final Collection<String> values, final Map<Collection<GSMatchPredicate<?, ?>>, String> predicates) {
		EmergentAttribute<RangeValue, Collection<IEntity<? extends IAttribute<? extends IValue>>>, ?> att =
				new EmergentAttribute<>(name);
		IValueSpace<RangeValue> vs = null;
		try {
			vs = new RangeSpace(att, new GSDataParser().getRangeTemplate(new ArrayList<>(values)));
		} catch (GSIllegalRangedData e) {
			e.printStackTrace();
		}
		if (vs != null) {
			att.setValueSpace(vs);
			for (String value : values) { vs.addValue(value); }
			CompositeValueFunction<RangeValue> cvf = new CompositeValueFunction<>(att);
			predicates.entrySet().stream().forEach(collect -> collect.getKey().stream().forEach(
					predicate -> cvf.addPredicate(predicate, att.getValueSpace().getValue(collect.getValue()))));
		}
		return att;
	}

	/**
	 * Creates a new Attribute object.
	 *
	 * @param name
	 *            the name
	 * @param values
	 *            the values
	 * @param predicates
	 *            the predicates
	 * @return the emergent attribute<? extends I value,?,?>
	 */
	/*
	 * private emergent ordered attribute
	 */
	private EmergentAttribute<? extends IValue, ?, ?> createEmergentOrder(final String name,
			final Collection<String> values, final Map<Collection<GSMatchPredicate<?, ?>>, String> predicates) {
		EmergentAttribute<OrderedValue, Collection<IEntity<? extends IAttribute<? extends IValue>>>, ?> att =
				new EmergentAttribute<>(name);
		IValueSpace<OrderedValue> vs = new OrderedSpace(att, new GSCategoricTemplate());
		att.setValueSpace(vs);
		values.stream().forEach(value -> vs.addValue(value));
		CompositeValueFunction<OrderedValue> cvf = new CompositeValueFunction<>(att);
		predicates.entrySet().stream().forEach(collect -> collect.getKey().stream()
				.forEach(predicate -> cvf.addPredicate(predicate, att.getValueSpace().getValue(collect.getValue()))));
		return att;
	}

	/**
	 * Creates a new Attribute object.
	 *
	 * @param name
	 *            the name
	 * @param values
	 *            the values
	 * @param predicates
	 *            the predicates
	 * @return the emergent attribute<? extends I value,?,?>
	 */
	/*
	 * private emergent nominal attribute
	 */
	private EmergentAttribute<? extends IValue, ?, ?> createEmergentNominal(final String name,
			final Collection<String> values, final Map<Collection<GSMatchPredicate<?, ?>>, String> predicates) {
		EmergentAttribute<NominalValue, Collection<IEntity<? extends IAttribute<? extends IValue>>>, ?> att =
				new EmergentAttribute<>(name);
		IValueSpace<NominalValue> vs = new NominalSpace(att, new GSCategoricTemplate());
		att.setValueSpace(vs);
		values.stream().forEach(value -> vs.addValue(value));
		CompositeValueFunction<NominalValue> cvf = new CompositeValueFunction<>(att);
		predicates.entrySet().stream().forEach(collect -> collect.getKey().stream()
				.forEach(predicate -> cvf.addPredicate(predicate, att.getValueSpace().getValue(collect.getValue()))));
		return att;
	}

	/**
	 * Creates a new Attribute object.
	 *
	 * @param name
	 *            the name
	 * @param values
	 *            the values
	 * @param predicates
	 *            the predicates
	 * @return the emergent attribute<? extends I value,?,?>
	 */
	/*
	 * private emergent integer attribute
	 */
	private EmergentAttribute<? extends IValue, ?, ?> createEmergentInteger(final String name,
			final Collection<String> values, final Map<Collection<GSMatchPredicate<?, ?>>, String> predicates) {
		EmergentAttribute<IntegerValue, Collection<IEntity<? extends IAttribute<? extends IValue>>>, ?> att =
				new EmergentAttribute<>(name);
		IValueSpace<IntegerValue> vs = new IntegerSpace(att);
		att.setValueSpace(vs);
		values.stream().forEach(value -> vs.addValue(value));
		CompositeValueFunction<IntegerValue> cvf = new CompositeValueFunction<>(att);
		predicates.entrySet().stream().forEach(collect -> collect.getKey().stream()
				.forEach(predicate -> cvf.addPredicate(predicate, att.getValueSpace().getValue(collect.getValue()))));
		return att;
	}

	/**
	 * Creates a new Attribute object.
	 *
	 * @param name
	 *            the name
	 * @param values
	 *            the values
	 * @param predicates
	 *            the predicates
	 * @return the emergent attribute<? extends I value,?,?>
	 */
	/*
	 * private emergent continuous attribute
	 */
	private EmergentAttribute<? extends IValue, ?, ?> createEmergentContinue(final String name,
			final Collection<String> values, final Map<Collection<GSMatchPredicate<?, ?>>, String> predicates) {
		EmergentAttribute<ContinuousValue, Collection<IEntity<? extends IAttribute<? extends IValue>>>, ?> att =
				new EmergentAttribute<>(name);
		IValueSpace<ContinuousValue> vs = new ContinuousSpace(att);
		att.setValueSpace(vs);
		values.stream().forEach(value -> vs.addValue(value));
		CompositeValueFunction<ContinuousValue> cvf = new CompositeValueFunction<>(att);
		predicates.entrySet().stream().forEach(collect -> collect.getKey().stream()
				.forEach(predicate -> cvf.addPredicate(predicate, att.getValueSpace().getValue(collect.getValue()))));
		return att;
	}

	/**
	 * Creates a new Attribute object.
	 *
	 * @param name
	 *            the name
	 * @param values
	 *            the values
	 * @param predicates
	 *            the predicates
	 * @return the emergent attribute<? extends I value,?,?>
	 */
	/*
	 * private emergent boolean attribute
	 */
	private EmergentAttribute<? extends IValue, ?, ?> createEmergentBoolean(final String name,
			final Collection<String> values, final Map<Collection<GSMatchPredicate<?, ?>>, String> predicates) {
		EmergentAttribute<BooleanValue, Collection<IEntity<? extends IAttribute<? extends IValue>>>, ?> att =
				new EmergentAttribute<>(name);
		IValueSpace<BooleanValue> vs = new BinarySpace(att);
		att.setValueSpace(vs);
		values.stream().forEach(value -> vs.addValue(value));
		CompositeValueFunction<BooleanValue> cvf = new CompositeValueFunction<>(att);
		predicates.entrySet().stream().forEach(collect -> collect.getKey().stream()
				.forEach(predicate -> cvf.addPredicate(predicate, att.getValueSpace().getValue(collect.getValue()))));
		return att;
	}

	/*
	 * --------------------- * RECORD ATTRIBUTE * ---------------------
	 */

	/**
	 * Create range record value attribute
	 *
	 * @param name
	 * @param referentAttribute
	 * @param record
	 * @return
	 * @throws GSIllegalRangedData
	 */
	public <V extends IValue> MappedAttribute<RangeValue, V> createRangeRecordAttribute(final String name,
			final Attribute<V> referentAttribute, final Map<String, String> record) throws GSIllegalRangedData {
		MappedAttribute<RangeValue, V> attribute = new MappedAttribute<>(name, referentAttribute, new RecordMapper<>());
		attribute.setValueSpace(
				new RangeSpace(attribute, new GSDataParser().getRangeTemplate(new ArrayList<>(record.keySet()))));
		attribute.getAttributeMapper().setRelatedAttribute(attribute);
		record.keySet().stream().forEach(key -> attribute.addMappedValue(attribute.getValueSpace().addValue(key),
				referentAttribute.getValueSpace().getValue(record.get(key))));
		return attribute;
	}

	// :: UTILES -------------------- //

	/**
	 * Postponed the referent attribution
	 *
	 * @param referee
	 * @param referent
	 */
	public <V extends IValue> void setReferent(final MappedAttribute<? extends IValue, V> referee,
			final Attribute<V> referent) {
		referee.setReferentAttribute(referent);
	}

}
