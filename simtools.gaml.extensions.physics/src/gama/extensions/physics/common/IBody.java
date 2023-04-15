/*******************************************************************************************************
 *
 * IBody.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.extensions.physics.common;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.species.ISpecies;

/**
 * The Interface IBody.
 *
 * @param <WorldType> the generic type
 * @param <BodyType> the generic type
 * @param <ShapeType> the generic type
 * @param <VectorType> the generic type
 */
/*
 * An abstraction of a physical "bullet" body for GAMA agents
 *
 * @author Alexis Drogoul 2021
 */
public interface IBody<WorldType, BodyType, ShapeType, VectorType> extends IPhysicalEntity<VectorType> {

	/**
	 * Clamp.
	 *
	 * @param value the value
	 * @return the float
	 */
	default float clamp(final Double value) {
		float result = value == null ? 0f : value.floatValue();
		return result < 0 ? 0f : result > 1 ? 1f : result;
	}

	/**
	 * Gets the body.
	 *
	 * @return the body
	 */
	BodyType getBody();

	/**
	 * Creates the and initialize body.
	 *
	 * @param shape the shape
	 * @param world the world
	 * @return the body type
	 */
	BodyType createAndInitializeBody(final ShapeType shape, WorldType world);

	/**
	 * No contact notification wanted.
	 *
	 * @param agent the agent
	 * @return true, if successful
	 */
	default boolean noContactNotificationWanted(final IAgent agent) {
		ISpecies species = agent.getSpecies();
		SpeciesDescription desc = species.getDescription();
		return desc.getAction(CONTACT_ADDED).isBuiltIn() && desc.getAction(CONTACT_REMOVED).isBuiltIn();
	}

	/**
	 * Read values from the physical body
	 */

	default void transferLocationAndRotationToAgent() {}

	/**
	 * Gets the mass.
	 *
	 * @return the mass
	 */
	float getMass();

	/**
	 * Gets the friction.
	 *
	 * @return the friction
	 */
	float getFriction();

	/**
	 * Gets the restitution.
	 *
	 * @return the restitution
	 */
	float getRestitution();

	/**
	 * Gets the linear damping.
	 *
	 * @return the linear damping
	 */
	float getLinearDamping();

	/**
	 * Gets the angular damping.
	 *
	 * @return the angular damping
	 */
	float getAngularDamping();

	/**
	 * Gets the contact damping.
	 *
	 * @return the contact damping
	 */
	float getContactDamping();

	/**
	 * Gets the angular velocity.
	 *
	 * @param v the v
	 * @return the angular velocity
	 */
	GamaPoint getAngularVelocity(GamaPoint v);

	/**
	 * Gets the linear velocity.
	 *
	 * @param v the v
	 * @return the linear velocity
	 */
	GamaPoint getLinearVelocity(GamaPoint v);

	/**
	 * Gets the aabb.
	 *
	 * @return the aabb
	 */
	IShape getAABB();

	/**
	 * Set properties of the physical body
	 */

	void setMass(Double mass);

	/**
	 * Sets the ccd.
	 *
	 * @param v the new ccd
	 */
	void setCCD(boolean v);

	/**
	 * Sets the friction.
	 *
	 * @param friction the new friction
	 */
	void setFriction(Double friction);

	/**
	 * Sets the restitution.
	 *
	 * @param restitution the new restitution
	 */
	void setRestitution(Double restitution);

	/**
	 * Sets the damping.
	 *
	 * @param damping the new damping
	 */
	void setDamping(Double damping);

	/**
	 * Sets the angular damping.
	 *
	 * @param damping the new angular damping
	 */
	void setAngularDamping(Double damping);

	/**
	 * Sets the contact damping.
	 *
	 * @param damping the new contact damping
	 */
	void setContactDamping(Double damping);

	/**
	 * Sets the angular velocity.
	 *
	 * @param angularVelocity the new angular velocity
	 */
	void setAngularVelocity(GamaPoint angularVelocity);

	/**
	 * Sets the linear velocity.
	 *
	 * @param linearVelocity the new linear velocity
	 */
	void setLinearVelocity(GamaPoint linearVelocity);

	/**
	 * Sets the location.
	 *
	 * @param loc the new location
	 */
	void setLocation(GamaPoint loc);

	/**
	 * Changes the collision shape of the body to match with the shape of the agent
	 */
	// void updateShape(IAgent a, DiscreteDynamicsWorld world);

	/**
	 * Apply and clear forces
	 */

	void clearForces();

	/**
	 * Apply impulse.
	 *
	 * @param impulse the impulse
	 */
	void applyImpulse(GamaPoint impulse);

	/**
	 * Apply torque.
	 *
	 * @param torque the torque
	 */
	void applyTorque(GamaPoint torque);

	/**
	 * Apply force.
	 *
	 * @param force the force
	 */
	void applyForce(GamaPoint force);

	/**
	 * Checks if is no notification.
	 *
	 * @return true, if is no notification
	 */
	default boolean isNoNotification() {
		return true;
	}

	/**
	 * Gets the agent.
	 *
	 * @return the agent
	 */
	IAgent getAgent();

}