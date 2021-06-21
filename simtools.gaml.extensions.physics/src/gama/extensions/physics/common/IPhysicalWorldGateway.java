package gama.extensions.physics.common;

import gama.extensions.physics.gaml.PhysicalSimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;

/**
 * The link between a simulation agent and the physical world / space, as defined by Bullet.
 *
 * @author drogoul
 *
 */
public interface IPhysicalWorldGateway<WorldType, ShapeType, VectorType> extends IPhysicalConstants {

	IShapeConverter<ShapeType, VectorType> getShapeConverter();

	void doStep(Double timeStep, int maxSubSteps);

	void registerAgent(IAgent agent);

	void unregisterAgent(IAgent agent);

	void updateAgentShape(IAgent agent);

	void setCCD(boolean ccd);

	void setGravity(GamaPoint gravity);

	void dispose();

	WorldType getWorld();

	PhysicalSimulationAgent getSimulation();

	void updatePositionsAndRotations();

}