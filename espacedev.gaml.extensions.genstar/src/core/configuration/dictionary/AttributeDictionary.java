package core.configuration.dictionary;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.EmergentAttribute;
import core.metamodel.attribute.IAttribute;
import core.metamodel.attribute.record.RecordAttribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.entity.IEntity;
import core.metamodel.value.IValue;
import core.util.GSKeywords;

/**
 * Encapsulate the whole set of a given attribute type: i.e. It describes all {@link Attribute}
 * used to model a {@link IPopulation} of {@link ADemoEntity} 
 * 
 * @author kevinchapuis
 * @author Samuel Thiriot
 *
 * @param <A>
 */
@JsonTypeName(value = IGenstarDictionary.SELF_LABEL)
/*
@JsonPropertyOrder({ IGenstarDictionary.LEVEL, 
	IGenstarDictionary.SERIAL, IGenstarDictionary.WEIGHT, 
	IGenstarDictionary.SIZE, IGenstarDictionary.RECORDS,
	IGenstarDictionary.ATTRIBUTES})
	*/
public class AttributeDictionary implements IGenstarDictionary<Attribute<? extends IValue>> {
	
	private Set<Attribute<? extends IValue>> attributes;
	private Map<String,Attribute<? extends IValue>> name2attribute;
	
	private Set<RecordAttribute<Attribute<? extends IValue>, Attribute<? extends IValue>>> records;
	
	private EmergentAttribute<? extends IValue, Collection<IEntity<? extends IAttribute<? extends IValue>>>, ?> sizeAttribute;
	
	private String weightAttribute;
	private String identifierAttribute;
	private int level;
	
	public AttributeDictionary() {
		this.attributes = new LinkedHashSet<>();
		this.records = new HashSet<>();
		this.name2attribute = new HashMap<>();
	}
	
	/**
	 * Clone constructor
	 * @param d
	 */
	public AttributeDictionary(IGenstarDictionary<Attribute<? extends IValue>> d) {
		this(d.getAttributes(), d.getRecords(), d.getSizeAttribute(), "", "", 0);
	}
	
	public AttributeDictionary(Collection<Attribute<? extends IValue>> attributes) {
		this(attributes, Collections.emptySet(), null, "", "", 0);
	}
	
	@JsonCreator
	public AttributeDictionary(
			@JsonProperty(IGenstarDictionary.ATTRIBUTES_LABEL) Collection<Attribute<? extends IValue>> attributes,
			@JsonProperty(IGenstarDictionary.RECORDS_LABEL) Collection<RecordAttribute<Attribute<? extends IValue>, Attribute<? extends IValue>>> records,
			@JsonProperty(IGenstarDictionary.SIZE_LABEL) EmergentAttribute<? extends IValue, Collection<IEntity<? extends IAttribute<? extends IValue>>>, ?> sizeAttribute,
			@JsonProperty(IGenstarDictionary.WEIGHT_LABEL) String weight,
			@JsonProperty(IGenstarDictionary.SERIAL_LABEL) String serial,
			@JsonProperty(IGenstarDictionary.LEVEL_LABEL) int level) {
		
		if (records == null)
			records = Collections.emptyList();
		
		this.attributes = new LinkedHashSet<>(attributes);
		this.records = new HashSet<>(records);
		this.sizeAttribute = sizeAttribute;
		this.name2attribute = attributes.stream()
				.collect(Collectors.toMap(
								IAttribute::getAttributeName,
								Function.identity()));
		this.name2attribute.put(GSKeywords.ENTITY_SIZE_ATTRIBUTE, sizeAttribute);
	}
	
	// ---------------------------- ADDERS ---------------------------- //
	
	/**
	 * Add attributes to this dictionary
	 * @param attributes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public AttributeDictionary addAttributes(Attribute<? extends IValue>... attributes) {
		this.attributes.addAll(Arrays.asList(attributes));
		this.name2attribute.putAll(Arrays.asList(attributes).stream()
				.collect(Collectors.toMap(
								IAttribute::getAttributeName,
								Function.identity())));
		return this;
	}
	
	@Override
	public AttributeDictionary addAttributes(Collection<Attribute<? extends IValue>> attributes) {
		this.attributes.addAll(attributes);
		this.name2attribute.putAll(
				attributes.stream()
							.collect(Collectors.toMap(
								IAttribute::getAttributeName,
								Function.identity())));
		return this;
	}

	// ---------------- RECORDS
	
	@SuppressWarnings("unchecked")
	@Override
	public IGenstarDictionary<Attribute<? extends IValue>> addRecords(
			RecordAttribute<Attribute<? extends IValue>, Attribute<? extends IValue>>... records) {
		this.records.addAll(Arrays.asList(records));
		return this;
	}
	
	@Override
	public Collection<RecordAttribute<Attribute<? extends IValue>, Attribute<? extends IValue>>> getRecords() {
		return Collections.unmodifiableSet(records);
	}
	
	// -----------------
	
	@Override
	public String getWeightAttributeName() {
		return this.weightAttribute;
	}

	@Override
	public void setWeightAttributeName(String weigthAttribute) {
		this.weightAttribute = weigthAttribute;
	}

	@Override
	public String getIdentifierAttributeName() {
		return this.identifierAttribute;
	}

	@Override
	public void setIdentifierAttributeName(String identifierAttribute) {
		this.identifierAttribute = identifierAttribute;
	}
	
	@Override
	public int getLevel() {
		return this.level;
	}
	
	@Override
	public void setLevel(int level) {
		this.level = level;
	}
	
	// ----------------- SIZE
	
	public void setSizeAttribute(EmergentAttribute<? extends IValue, Collection<IEntity<? extends IAttribute<? extends IValue>>>, ?> sizeAttribute) {
		this.sizeAttribute = sizeAttribute;
		this.name2attribute.put(GSKeywords.ENTITY_SIZE_ATTRIBUTE, sizeAttribute);
	}
	
	@Override
	public EmergentAttribute<? extends IValue, Collection<IEntity<? extends IAttribute<? extends IValue>>>, ?> getSizeAttribute() {
		return this.sizeAttribute;
	}

	// ---------------------------- ACCESSORS ---------------------------- //
	
	@Override
	public Collection<Attribute<? extends IValue>> getAttributes() {
		return Collections.unmodifiableSet(attributes);
	}
	
	@Override
	public Attribute<? extends IValue> getAttribute(String string) {
		Attribute<? extends IValue> a = name2attribute.get(string);
		if (a == null)
			throw new NullPointerException("This dictionary contains no reference to the attribute with name "+string);
		return a;
	}
	
	@Override
	public IValue getValue(String value) {
		Optional<Attribute<? extends IValue>> attribute = attributes.stream()
				.filter(a -> a.getValueSpace().contains(value))
				.findFirst();
		if(attribute.isPresent())
			return attribute.get().getValueSpace().getValue(value);
		throw new NullPointerException("This dictionary contains no reference to the value "+value);
	}
	
	// ------------------- UTILITIES
	
	public boolean containsAttribute(String name) {
		return name2attribute.containsKey(name);
	}
	
	public boolean containsRecord(String name) {
		return records.stream().anyMatch(rec -> rec.getAttributeName().equals(name));
	}

	@Override
	public boolean containsValue(String valueStr) {
		for (Attribute<? extends IValue> a: attributes) {
			if (a.getValueSpace().contains(valueStr))
				return true;
		}
		return false;
	}
	
	@Override
	public Collection<IAttribute<? extends IValue>> getAttributeAndRecord() {
		return Stream.concat(attributes.stream(), records.stream())
				.collect(Collectors.toCollection(HashSet::new));
	}
	
	@Override
	public IGenstarDictionary<Attribute<? extends IValue>> merge(IGenstarDictionary<Attribute<? extends IValue>> dictionnary) {
		IGenstarDictionary<Attribute<? extends IValue>> d = new AttributeDictionary(this);
		d.addAttributes(dictionnary.getAttributes());
		return d;
	}
	
	@Override
	public int size() {
		return attributes.size();
	}
	
}
