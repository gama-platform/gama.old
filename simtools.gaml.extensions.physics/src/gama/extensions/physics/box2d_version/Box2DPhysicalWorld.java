package gama.extensions.physics.box2d_version;

import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import gama.extensions.physics.common.AbstractPhysicalWorld;
import gama.extensions.physics.common.IShapeConverter;
import gama.extensions.physics.gaml.PhysicalSimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;

public class Box2DPhysicalWorld extends AbstractPhysicalWorld<World, Shape, Vec2> implements IBox2DPhysicalEntity {

	protected Box2DPhysicalWorld(final PhysicalSimulationAgent physicalSimulationAgent) {
		super(physicalSimulationAgent);
	}

	@Override
	public void registerAgent(final IAgent agent) {
		Box2DBodyWrapper body = new Box2DBodyWrapper(agent, this);
	}

	@Override
	public void unregisterAgent(final IAgent agent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCCD(final boolean ccd) {

	}

	@Override
	public void setGravity(final GamaPoint gravity) {
		if (world != null) { world.setGravity(toVector(gravity)); }
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updatePositionsAndRotations() {
		// TODO Auto-generated method stub

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
		return new Box2DShapeConverter();
	}

	@Override
	protected void updateAgentsShape() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void updateEngine(final Double timeStep, final int maxSubSteps) {
		getWorld().step(timeStep.floatValue(), maxSubSteps, maxSubSteps);
	}

}
