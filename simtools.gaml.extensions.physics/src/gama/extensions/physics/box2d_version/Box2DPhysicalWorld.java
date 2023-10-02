/*******************************************************************************************************
 *
 * Box2DPhysicalWorld.java, in simtools.gaml.extensions.physics, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.extensions.physics.box2d_version;

import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import gama.extensions.physics.common.AbstractPhysicalWorld;
import gama.extensions.physics.common.IBody;
import gama.extensions.physics.common.IShapeConverter;
import gama.extensions.physics.gaml.PhysicalSimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;

/**
 * The Class Box2DPhysicalWorld.
 */
public class Box2DPhysicalWorld extends AbstractPhysicalWorld<World, Shape, Vec2> implements IBox2DPhysicalEntity {

	/** The scale. */
	float scale;

	/** The target. */
	static float TARGET = 10;

	/**
	 * Instantiates a new box 2 D physical world.
	 *
	 * @param sim
	 *            the physical simulation agent
	 */
	public Box2DPhysicalWorld(final PhysicalSimulationAgent sim) {
		super(sim);
		double w = sim.getWidth();
		double h = sim.getHeight();
		scale = (float) (TARGET / Math.max(w, h));
	}

	@SuppressWarnings ("unused")
	@Override
	public void registerAgent(final IAgent agent) {
		new Box2DBodyWrapper(agent, this);
	}

	@Override
	public void unregisterAgent(final IAgent agent) {
		Body body = ((Box2DBodyWrapper) agent.getAttribute(BODY)).body;
		getWorld().destroyBody(body);

	}

	@Override
	public void setCCD(final boolean ccd) {}

	@Override
	public void setGravity(final GamaPoint gravity) {
		if (world != null) { world.setGravity(toVector(gravity)); }
	}

	@Override
	public void dispose() {
		if (world != null) {
			Body b = world.getBodyList();
			while (b != null) {
				world.destroyBody(b);
				b = b.getNext();
			}
			world = null;
		}
	}

	@Override
	public void updatePositionsAndRotations() {
		Body b = world.getBodyList();
		while (b != null) {
			IBody body = (IBody) b.getUserData();
			if (b.isActive()) { body.transferLocationAndRotationToAgent(); }
			b = b.getNext();
		}
	}

	@Override
	protected World createWorld() {
		GamaPoint p = simulation.getGravity(simulation.getScope());
		World result = new World(toVector(p));
		result.setAutoClearForces(true);
		result.setContactListener(contactListener);
		return result;
	}

	@Override
	protected IShapeConverter<Shape, Vec2> createShapeConverter() {
		return new Box2DShapeConverter(scale);
	}

	@Override
	protected void updateAgentsShape() {
		// We update the agents
		for (IAgent a : updatableAgents) { unregisterAgent(a); }
		for (IAgent a : updatableAgents) { registerAgent(a); }
		updatableAgents.clear();
	}

	@Override
	protected void updateEngine(final Double timeStep, final int maxSubSteps) {
		int steps = maxSubSteps == 0 ? 1 : maxSubSteps;
		getWorld().step(timeStep.floatValue(), steps, steps);
	}

	@Override
	public float getScale() { return scale; }

}
