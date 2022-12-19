/*******************************************************************************************************
 *
 * ADemoEntity.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package core.metamodel.entity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.EmergentAttribute;
import core.metamodel.attribute.IAttribute;
import core.metamodel.entity.tag.EntityTag;
import core.metamodel.value.IValue;
import core.metamodel.value.numeric.IntegerSpace;
import core.metamodel.value.numeric.IntegerValue;

/**
 * The higher order abstraction for demographic entity. Manage basic attribute / value relationship and parent /
 * children relationship.
 *
 * @author kevinchapuis
 *
 */
public abstract class ADemoEntity implements IEntity<Attribute<? extends IValue>> {

	/**
	 * The unique identifier of the entity. See {@link EntityUniqueId}
	 */
	private String id = null;

	/**
	 * The map of attribute / value ! What characterize the entity: vector of value, one value for each attribute.
	 */
	protected Map<Attribute<? extends IValue>, IValue> attributes;

	/**
	 * The type of the agent (like "household" or "building"), or null if undefined
	 */
	private String type;

	/**
	 * Creates a population entity by defining directly the attribute values
	 *
	 * @param attributes
	 */
	protected ADemoEntity(final Map<Attribute<? extends IValue>, IValue> attributes) {
		this.attributes = attributes;
	}

	/**
	 * creates a population entity without defining its population attributes
	 *
	 * @param attributes
	 */
	protected ADemoEntity() {
		this.attributes = new HashMap<>();
	}

	/**
	 * Clone like constructor.
	 *
	 * @param e
	 */
	protected ADemoEntity(final ADemoEntity e) {
		this.attributes = new HashMap<>(e.getAttributeMap());
		this.type = e.type;
	}

	/**
	 * creates a population entity by defining the attributes it will contain without attributing any value
	 *
	 * @param attributes
	 */
	protected ADemoEntity(final Collection<Attribute<IValue>> attributes) {
		this.attributes = attributes.stream().collect(Collectors.toMap(Function.identity(), att -> null));
	}

	/**
	 * Clone returns a similar population entity whose values might be modified without modifying the parent one.
	 */
	@Override
	public abstract ADemoEntity clone();

	@Override
	public final void _setEntityId(final String novelid) throws IllegalStateException {
		if (this.id != null) throw new IllegalArgumentException("cannot change the identifier of an agent; "
				+ "this agent already had id " + this.id + " but we were asked " + "to change it for " + novelid);
		this.id = novelid;
	}

	@Override
	public final String getEntityId() throws IllegalStateException {
		if (this.id == null) throw new IllegalStateException("no id is defined yet for agent " + this.toString());
		return this.id;
	}

	@Override
	public final boolean _hasEntityId() {
		return this.id != null;
	}

	// ---------------------------------------------------------------------- //

	/**
	 * sets the value for the attribute or updates this value
	 *
	 * @param attribute
	 * @param value
	 */
	public void setAttributeValue(final Attribute<? extends IValue> attribute, final IValue value) {
		this.attributes.put(attribute, value);
	}

	/**
	 * sets the value for the attribute or updates this value
	 *
	 * @param attributeName
	 * @param value
	 */
	public void setAttributeValue(final String attributeName, final IValue value) {

		if (attributes.isEmpty()) throw new IllegalArgumentException("there is no attribute defined for this entity");

		Optional<Attribute<? extends IValue>> opAtt =
				attributes.keySet().stream().filter(att -> att.getAttributeName().equals(attributeName)).findAny();

		if (!opAtt.isPresent()) throw new IllegalArgumentException(
				"there is no attribute named " + attributeName + " defined for this entity");

		this.attributes.put(opAtt.get(), value);
	}

	// ----------------------------------------------------------------------- //

	@Override
	public boolean hasAttribute(final Attribute<? extends IValue> a) {
		return attributes.containsKey(a);
	}

	@Override
	public IValue getValueForAttribute(final Attribute<? extends IValue> attribute) {
		return attributes.get(attribute);
	}

	@Override
	public IValue getValueForAttribute(final String property) {
		for (Entry<Attribute<? extends IValue>, IValue> entry : this.attributes.entrySet()) {
			if (entry.getKey().getAttributeName().equals(property)) return entry.getValue();
		}
		return null;
	}

	@Override
	public Map<Attribute<? extends IValue>, IValue> getAttributeMap() {
		return Collections.unmodifiableMap(attributes);
	}

	@Override
	public Collection<Attribute<? extends IValue>> getAttributes() { return attributes.keySet(); }

	@Override
	public Collection<IValue> getValues() { return Collections.unmodifiableCollection(attributes.values()); }

	@Override
	public String toString() {
		return "Entity [" + this.id + "] "
				+ attributes.entrySet().stream()
						.map(e -> e.getKey().getAttributeName() + ":" + e.getValue().getStringValue())
						.collect(Collectors.joining(",\t"));
	}

	@Override
	public final boolean hasEntityType() {
		return type != null;
	}

	@Override
	public final String getEntityType() { return type; }

	@Override
	public void setEntityType(final String type) { this.type = type; }

	// ------------ MULTI-LAYER ENTITY ATTRIBUTES ------------ //

	/**
	 * The parent entity, if any
	 */
	private IEntity<? extends IAttribute<? extends IValue>> parent = null;

	/**
	 * The set of children, if any. Else remains null (lazy creation)
	 */
	private Set<IEntity<? extends IAttribute<? extends IValue>>> children = null;

	/** The size attribute. */
	private EmergentAttribute<? extends IValue, Collection<IEntity<? extends IAttribute<? extends IValue>>>, ?> sizeAttribute;

	@Override
	public final boolean hasParent() {
		return parent != null;
	}

	@Override
	public final IEntity<? extends IAttribute<? extends IValue>> getParent() { return parent; }

	@Override
	public final void setParent(final IEntity<? extends IAttribute<? extends IValue>> e) { this.parent = e; }

	@Override
	public final boolean hasChildren() {
		return children != null && !children.isEmpty();
	}

	@Override
	public final Set<IEntity<? extends IAttribute<? extends IValue>>> getChildren() {
		if (children == null) return Collections.emptySet();
		return Collections.unmodifiableSet(children);
	}

	@Override
	public final void addChild(final IEntity<? extends IAttribute<? extends IValue>> e) {
		if (children == null) { children = new HashSet<>(); }
		children.add(e);
	}

	@Override
	public final void addChildren(final Collection<IEntity<? extends IAttribute<? extends IValue>>> e) {
		if (children == null) { children = new HashSet<>(); }
		children.addAll(e);
	}

	@Override
	public IValue getCountChildren() {
		return sizeAttribute == null ? new IntegerSpace(null).proposeValue(Integer.toString(this.getChildren().size()))
				: sizeAttribute.getEmergentValue(this);
	}

	/**
	 * Set the attribute
	 *
	 * @param sizeAttribute
	 */
	public void setCountAttribute(
			final EmergentAttribute<IntegerValue, Collection<IEntity<? extends IAttribute<? extends IValue>>>, ?> sizeAttribute) {
		this.sizeAttribute = sizeAttribute;
	}

	// ---------------- TAGS and WEIGTH ------------------- //

	/**
	 * The class of attribute to characterize within or between layer relationship, e.g. parent, child, head of
	 * household
	 */
	private Set<EntityTag> tags;

	/** The weight. */
	private double weight = 1d;

	@Override
	public boolean hasTags(final EntityTag... tagsArray) {
		List<EntityTag> theTags = Arrays.asList(tagsArray);
		return this.tags.stream().allMatch(theTags::contains);
	}

	@Override
	public Collection<EntityTag> getTags() { return Collections.unmodifiableSet(this.tags); }

	/**
	 * The weight often used in association with record of a sample
	 *
	 * @return double weight value attached to the entity
	 */
	public double getWeight() { return weight; }

	/**
	 * Set the weight of the entity
	 *
	 * @param weight
	 */
	public void setWeight(final double weight) { this.weight = weight; }

}
