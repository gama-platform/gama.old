package gama.extensions.physics.common;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.species.ISpecies;

/*
 * An abstraction of a physical "bullet" body for GAMA agents
 *
 * @author Alexis Drogoul 2021
 */
public interface IBody<WorldType, BodyType, ShapeType, VectorType> extends IPhysicalEntity<VectorType> {

	default float clamp(final Double value) {
		float result = value == null ? 0f : value.floatValue();
		return result < 0 ? 0f : result > 1 ? 1f : result;
	}

	BodyType getBody();

	BodyType createAndInitializeBody(final ShapeType shape, WorldType world);

	default boolean noContactNotificationWanted(final IAgent agent) {
		ISpecies species = agent.getSpecies();
		SpeciesDescription desc = species.getDescription();
		return desc.getAction(CONTACT_ADDED).isBuiltIn() && desc.getAction(CONTACT_REMOVED).isBuiltIn();
	}

	/**
	 * Read values from the physical body
	 */

	default void transferLocationAndRotationToAgent() {}

	float getMass();

	float getFriction();

	float getRestitution();

	float getLinearDamping();

	float getAngularDamping();

	float getContactDamping();

	GamaPoint getAngularVelocity(GamaPoint v);

	GamaPoint getLinearVelocity(GamaPoint v);

	IShape getAABB();

	/**
	 * Set properties of the physical body
	 */

	void setMass(Double mass);

	void setCCD(boolean v);

	void setFriction(Double friction);

	void setRestitution(Double restitution);

	void setDamping(Double damping);

	void setAngularDamping(Double damping);

	void setContactDamping(Double damping);

	void setAngularVelocity(GamaPoint angularVelocity);

	void setLinearVelocity(GamaPoint linearVelocity);

	void setLocation(GamaPoint loc);

	/**
	 * Changes the collision shape of the body to match with the shape of the agent
	 */
	// void updateShape(IAgent a, DiscreteDynamicsWorld world);

	/**
	 * Apply and clear forces
	 */

	void clearForces();

	void applyImpulse(GamaPoint impulse);

	void applyTorque(GamaPoint torque);

	void applyForce(GamaPoint force);

	default boolean isNoNotification() {
		return true;
	}

	IAgent getAgent();

}