/*******************************************************************************************************
 *
 * ImplicitEntityComparator.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package core.metamodel.entity.comparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import core.configuration.jackson.entity.EntityComparatorSerializer;
import core.metamodel.IPopulation;
import core.metamodel.attribute.IAttribute;
import core.metamodel.entity.IEntity;
import core.metamodel.entity.comparator.function.IComparatorFunction;
import core.metamodel.value.IValue;
import core.util.data.GSEnumDataType;

/**
 * Implicit comparator based on value space type of attributes passed as argument of class constructor. Any implicit
 * comparator that lead to equality of values will rely on default comparator process. Each provided attribute is tested
 * following insertion order; comparison is carried on until an ordered is reached, that is comparison on a given
 * attribute leads to -1 or 1. Ultimately, default comparator unsure 0 value means equality of {@link IEntity} within a
 * given {@link IPopulation}
 * <p>
 * WARNING: implies dirty cast & unboxing process
 *
 * @author kevinchapuis
 *
 */
@JsonSerialize (
		using = EntityComparatorSerializer.class)
public class ImplicitEntityComparator implements Comparator<IEntity<? extends IAttribute<? extends IValue>>> {

	/** The Constant ATTRIBUTES_REF. */
	public static final String ATTRIBUTES_REF = "LISTED ATTRIBUTES";
	
	/** The Constant COMP_FUNCTIONS. */
	public static final String COMP_FUNCTIONS = "CUSTOM COMPARATOR FUNCTIONS";

	/** The default comparator. */
	@JsonIgnore private final IDComparator defaultComparator;
	
	/** The functions. */
	private final EnumMap<GSEnumDataType, IComparatorFunction<? extends IValue>> functions;

	/** The attributes. */
	private final List<IAttribute<? extends IValue>> attributes;
	
	/** The revers map. */
	private final Map<String, Integer> reversMap;

	/**
	 * Instantiates a new implicit entity comparator.
	 *
	 * @param attributes the attributes
	 */
	public ImplicitEntityComparator(final List<IAttribute<? extends IValue>> attributes) {
		this.attributes = attributes;
		if (this.attributes.isEmpty()) {
			reversMap = new HashMap<>();
		} else {
			reversMap = this.attributes.stream().collect(Collectors.toMap(IAttribute::getAttributeName, a -> 1));
		}
		this.defaultComparator = IDComparator.getInstance();
		this.functions = new EnumMap<>(Stream.of(GSEnumDataType.values())
				.collect(Collectors.toMap(Function.identity(), IComparatorFunction::getDefaultFunction)));
	}

	/**
	 * Instantiates a new implicit entity comparator.
	 *
	 * @param attributes the attributes
	 */
	@SafeVarargs
	public ImplicitEntityComparator(final IAttribute<? extends IValue>... attributes) {
		this(new ArrayList<>(Arrays.asList(attributes)));
	}

	// ------------------------------------------------------------------------------------------ //

	@Override
	public int compare(final IEntity<? extends IAttribute<? extends IValue>> o1,
			final IEntity<? extends IAttribute<? extends IValue>> o2) {
		int res;
		int idx = 0;
		do {
			IAttribute<? extends IValue> att = attributes.get(idx++);
			res = this.compare(o1, o2, att) * reversMap.get(att.getAttributeName());
		} while (idx < attributes.size());
		return res == 0 ? this.defaultComparator.compare(o1, o2) : res;
	}

	// ------------------------------------------------------------------------------------------ //

	/**
	 * The attributes over which comparison will be made
	 *
	 * @return
	 */
	public List<IAttribute<? extends IValue>> getAttributes() { return attributes; }

	/**
	 * Add attributes to based comparison on. Insertion order will drive comparison order: as soon as an order is find
	 * over an attribute it is returned
	 *
	 * @param attributesArray
	 */
	public ImplicitEntityComparator
			addAttributes(@SuppressWarnings ("unchecked") final IAttribute<? extends IValue>... attributesArray) {
		List<IAttribute<? extends IValue>> atts = Arrays.asList(attributesArray);
		this.attributes.addAll(atts);
		atts.forEach(a -> reversMap.put(a.getAttributeName(), 1));
		return this;
	}

	/**
	 * Add attribute to based comparison on with an optional reverse effect
	 *
	 * @param attribute
	 * @param reverse
	 */
	public ImplicitEntityComparator setAttribute(final IAttribute<? extends IValue> attribute, final boolean reverse) {
		this.attributes.add(attribute);
		this.reversMap.put(attribute.getAttributeName(), reverse ? -1 : 1);
		return this;
	}

	/**
	 * Whether given attribute has reverse relationship with order
	 *
	 * @param attribute
	 * @return
	 */
	public boolean isReverseAttribute(final IAttribute<? extends IValue> attribute) {
		if (!reversMap.containsKey(attribute.getAttributeName())) throw new IllegalArgumentException(
				"This comparator have no reference to the attribute " + attribute.getAttributeName());
		return (reversMap.get(attribute.getAttributeName()) == -1) == true;
	}

	/**
	 * Comparison function used when it comes to compare attribute of value {@code type}
	 *
	 * @param type
	 * @return
	 */
	public IComparatorFunction<? extends IValue> getComparatorFunction(final GSEnumDataType type) {
		return functions.get(type);
	}

	/**
	 * Set a comparison function to be used for a given value type
	 *
	 * @param function
	 */
	public ImplicitEntityComparator setComparatorFunction(final IComparatorFunction<? extends IValue> function) {
		this.functions.put(function.getType(), function);
		return this;
	}

	// ------------------------------------------------------------------------------------------- //
	// INNER UTILITIES //

	/**
	 * Compare.
	 *
	 * @param o1 the o 1
	 * @param o2 the o 2
	 * @param attribute the attribute
	 * @return the int
	 */
	/*
	 * could return zero value, with possible equality inconsistency that is resolve in #compare(o1,o2) method be using
	 * default comparator
	 */
	private int compare(final IEntity<? extends IAttribute<? extends IValue>> o1,
			final IEntity<? extends IAttribute<? extends IValue>> o2, final IAttribute<? extends IValue> attribute) {
		GSEnumDataType type = attribute.getValueSpace().getType();
		if (!functions.containsKey(type)) throw new IllegalArgumentException("Unknown attribute value type: " + type);
		return functions.get(type).compare(o1.getValueForAttribute(attribute.getAttributeName()),
				o2.getValueForAttribute(attribute.getAttributeName()), attribute.getValueSpace().getEmptyValue());
	}

}
