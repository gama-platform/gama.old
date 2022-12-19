/*******************************************************************************************************
 *
 * IQueryablePopulation.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package core.metamodel;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import core.metamodel.attribute.IAttribute;
import core.metamodel.entity.IEntity;
import core.metamodel.value.IValue;

/**
 * The Interface IQueryablePopulation.
 *
 * @param <E> the element type
 * @param <A> the generic type
 */
public interface IQueryablePopulation<E extends IEntity<A>, A extends IAttribute<? extends IValue>>
		extends IPopulation<E, A> {

	/**
	 * Gets the entity for id.
	 *
	 * @param id the id
	 * @return the entity for id
	 */
	E getEntityForId(String id);

	/**
	 * Gets the entities for ids.
	 *
	 * @param ids the ids
	 * @return the entities for ids
	 */
	Iterator<E> getEntitiesForIds(String... ids);

	/**
	 * Returns the count of entities which have for this attribute one of the given values (OR)
	 *
	 * @param attribute
	 * @param values
	 * @return
	 */
	int getCountHavingValues(A attribute, IValue... values);

	/**
	 * Returns the count of entities which have one of these values for these attributes (a AND clause of OR clause)
	 *
	 * @param attribute2values
	 * @return
	 */
	int getCountHavingValues(Map<A, Collection<IValue>> attribute2values);

	/**
	 * Gets the count having coordinate.
	 *
	 * @param attribute2value the attribute 2 value
	 * @return the count having coordinate
	 */
	int getCountHavingCoordinate(Map<A, IValue> attribute2value);

	/**
	 * Gets the entities having values.
	 *
	 * @param attribute the attribute
	 * @param values the values
	 * @return the entities having values
	 */
	Iterator<E> getEntitiesHavingValues(A attribute, IValue... values);

	/**
	 * Gets the entities having values.
	 *
	 * @param attribute2values the attribute 2 values
	 * @return the entities having values
	 */
	Iterator<E> getEntitiesHavingValues(Map<A, Collection<IValue>> attribute2values);

}
