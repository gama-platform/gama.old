/*******************************************************************************************************
 *
 * IEntity.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package core.metamodel.entity;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import core.metamodel.IPopulation;
import core.metamodel.attribute.IAttribute;
import core.metamodel.entity.matcher.MatchType;
import core.metamodel.entity.tag.EntityTag;
import core.metamodel.value.IValue;

/**
 * An entity might represent an household, an individual, or even a geographical entity etc.
 *
 * @author gospl-team
 *
 */
public interface IEntity<A extends IAttribute<? extends IValue>> {

	/**
	 * returns the entity id for the entity. It is unique at the scale of the entity population only. See
	 * {@link EntityUniqueId}. throws an {@link IllegalStateException} if the id was not defined yet by the population
	 *
	 * @return
	 */
	String getEntityId() throws IllegalStateException;

	/**
	 * <b>Internal use only.</b> Called by an {@link IPopulation} when the agent is added into it. See
	 * {@link EntityUniqueId}. Throws an {@link IllegalStateException} if an id was defined already.
	 */
	void _setEntityId(String novelid) throws IllegalStateException;

	/**
	 * <b>internal use only</b>. Returns true if this entity already has a id defined. Used by {@link IPopulation}. See
	 * {@link EntityUniqueId} for details.
	 *
	 * @return
	 */
	boolean _hasEntityId();

	/**
	 * returns the mapped view of attribute / value pairs
	 *
	 * @return
	 */
	Map<A, IValue> getAttributeMap();

	/**
	 * returns the list of the attributes for which the entity might have values
	 *
	 * @return
	 */
	Collection<A> getAttributes();

	/**
	 * Returns true if this entity contains this attribute
	 *
	 * @param a
	 * @return
	 */
	boolean hasAttribute(A a);

	/**
	 * returns values for each attributes of the entity
	 *
	 * @return
	 */
	Collection<? extends IValue> getValues();

	/**
	 * returns the value for an attribute if any; the value might be null if no value is defined; raises an exception if
	 * the attribute is not declared for this entity
	 *
	 * @param attribute
	 * @return
	 */
	IValue getValueForAttribute(A attribute);

	/**
	 * returns the value for an attribute if any, based on attribute name. The name of attribute should be access using
	 * {@link IAttribute#getAttributeName()}
	 * <p>
	 *
	 * @see #getValueForAttribute(IAttribute)
	 *
	 * @param property
	 * @return
	 */
	IValue getValueForAttribute(String property);

	/**
	 * Returns true if this agent has an entity type, that is if getEntityType returns something else than null
	 *
	 * @return
	 */
	boolean hasEntityType();

	/**
	 * Returns the entity type of this agent, or null if none was defined (standard case if there is only one type of
	 * agent)
	 *
	 * @return
	 */
	String getEntityType();

	/**
	 * Sets the type of this agent, without any control
	 *
	 * @param type
	 */
	void setEntityType(String type);

	/**
	 * Returns true if this agent has a parent, that is if getParent does not return null;
	 *
	 * @return
	 */
	boolean hasParent();

	/**
	 * Returns the "parent" entity
	 *
	 * @return
	 */
	IEntity<? extends IAttribute<? extends IValue>> getParent();

	/**
	 * defines the parent. Raises an exception if you try to define an entity as its own parent. Does not detects loops,
	 * which you should still avoid.
	 *
	 * @param e
	 */
	void setParent(IEntity<? extends IAttribute<? extends IValue>> e);

	/**
	 * returns true if this entity has any children, that is if getCountChildren returns more than 1
	 *
	 * @return
	 */
	boolean hasChildren();

	/**
	 * returns the count of children
	 *
	 * @return
	 */
	IValue getCountChildren();

	/**
	 * returns the set of children
	 *
	 * @return
	 */
	Set<IEntity<? extends IAttribute<? extends IValue>>> getChildren();

	/**
	 * Adds a child to the list of children
	 *
	 * @param e
	 */
	void addChild(IEntity<? extends IAttribute<? extends IValue>> e);

	/**
	 * Adds several children to the list of children
	 *
	 * @param e
	 */
	void addChildren(Collection<IEntity<? extends IAttribute<? extends IValue>>> e);

	/**
	 * Returns true if this entity is associated to each tag given in parameter
	 *
	 * @param tags
	 * @return true, if entity contains tags, false otherwise
	 *
	 * @see MatchType
	 */
	boolean hasTags(EntityTag... tags);

	/**
	 * Return the tags associated to this entity
	 *
	 * @return the collection of {@link EntityTag}
	 */
	Collection<EntityTag> getTags();

}
