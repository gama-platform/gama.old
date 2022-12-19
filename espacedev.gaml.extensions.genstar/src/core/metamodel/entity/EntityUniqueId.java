/*******************************************************************************************************
 *
 * EntityUniqueId.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package core.metamodel.entity;

import java.util.HashMap;
import java.util.Map;

import core.metamodel.IPopulation;

/**
 * This class provides utilities to generate unique ids for entities.
 *
 * An ID is unique at the scale of a population. It does not make sense to compare IDs from different populations. It
 * means you can never find an entity in a population based on the ID of an entity you found in another population. If
 * you find an entity bearing the same ID, it does not mean it is the same entity.
 *
 * An entity ID does not change during the lifetime of an Entity.
 *
 * The definition of the ID of an agent is part of the responsibility of its population {@link IPopulation}, which will
 * call {@link EntityUniqueId.createNextId } to generate this ID.
 *
 * The ID tries to be human readable, in the form "individual_2343" or "building_42". Yet if the type of the agent was
 * defined or changed after its creation, the ID might not reflect the actual type.
 *
 * This class is thread safe.
 *
 * @author Samuel Thiriot
 */
public class EntityUniqueId {

	/**
	 * Instantiates a new entity unique id.
	 */
	private EntityUniqueId() {}

	/**
	 * Returns a novel ID for a given scope {@link IPopulation} and a given type
	 *
	 * @param pop
	 *            the IPopulation in which the identifier should be unique
	 * @param type
	 * @return
	 */
	public static synchronized String createNextId(final Object pop, final String type) {
		if (type == null) return "untyped_" + Long.toString(createNextNumericId(pop));
		return type + "_" + Long.toString(createNextNumericId(pop));
	}

	/**
	 * Returns a novel ID for a given scope {@link IPopulation}.
	 *
	 * @param pop
	 *            the IPopulation in which the identifier should be unique
	 * @return
	 */
	public static String createNextId(final Object pop) {
		return createNextId(pop, null);
	}

	/**
	 * The last numeric identifier component generated at the scale of the
	 */
	private static Map<Object, Long> scope2lastId = new HashMap<>();

	/**
	 * Returns a novel long identifier that is unique independently of the entity types for a given scope.
	 *
	 * @param scope
	 * @return
	 */
	private static long createNextNumericId(final Object scope) {
		final Long lastLong = scope2lastId.get(scope);
		Long nextLong;
		if (lastLong == null) {
			nextLong = 1l;
		} else {
			nextLong = lastLong + 1l;
		}
		scope2lastId.put(scope, nextLong);
		return nextLong;
	}

}
