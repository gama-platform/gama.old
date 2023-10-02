/*******************************************************************************************************
 *
 * AbstractBodyWrapper.java, in simtools.gaml.extensions.physics, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.extensions.physics.common;

import msi.gama.metamodel.agent.IAgent;

/**
 * The Class AbstractBodyWrapper.
 *
 * @param <WorldType>
 *            the generic type
 * @param <BodyType>
 *            the generic type
 * @param <ShapeType>
 *            the generic type
 * @param <VectorType>
 *            the generic type
 */
public abstract class AbstractBodyWrapper<WorldType, BodyType, ShapeType, VectorType>
		implements IBody<WorldType, BodyType, ShapeType, VectorType> {
	// Between GAMA coordinates and engines coordinates, some discrepancies may exist (esp. on spheres, for instance)
	/** The aabb translation. */

	protected final VectorType aabbTranslation = toVector(null);

	/** The visual translation. */
	protected final VectorType visualTranslation = toVector(null);

	/** The is static. */
	public final boolean noNotification, isStatic;

	/** The body. */
	public BodyType body;

	/** The agent. */
	public final IAgent agent;

	/**
	 * Instantiates a new abstract body wrapper.
	 *
	 * @param agent
	 *            the agent
	 * @param world
	 *            the world
	 */
	public AbstractBodyWrapper(final IAgent agent, final IPhysicalWorld<WorldType, ShapeType, VectorType> world) {
		this.noNotification = this.noContactNotificationWanted(agent);
		this.agent = agent;
		isStatic = agent.getSpecies().implementsSkill(STATIC_BODY);
		ShapeType shape = world.getShapeConverter().convertAndTranslate(agent, aabbTranslation, visualTranslation);
		body = createAndInitializeBody(shape, world.getWorld());
		agent.setAttribute(BODY, this);
	}

	@Override
	public final boolean isNoNotification() { return noNotification; }

	@Override
	public final IAgent getAgent() { return agent; }

	@Override
	public final BodyType getBody() { return body; }

}
