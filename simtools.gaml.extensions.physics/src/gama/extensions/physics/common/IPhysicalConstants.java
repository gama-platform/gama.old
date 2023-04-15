/*******************************************************************************************************
 *
 * IPhysicalConstants.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.extensions.physics.common;

/**
 * The Interface IPhysicalConstants.
 *
 * @param <VectorType> the generic type
 */
public interface IPhysicalConstants<VectorType> {

	/** The eps. */
	double EPS = 0.000001;

	/*
	 * Species and skills names
	 */

	/** The physical world. */
	String PHYSICAL_WORLD = "physical_world";
	
	/** The static body. */
	String STATIC_BODY = "static_body";
	
	/** The dynamic body. */
	String DYNAMIC_BODY = "dynamic_body";

	/*
	 * Action names
	 */

	/** The register. */
	String REGISTER = "register";
	
	/** The apply. */
	String APPLY = "apply";
	
	/** The contact added. */
	String CONTACT_ADDED = "contact_added_with";
	
	/** The contact removed. */
	String CONTACT_REMOVED = "contact_removed_with";
	
	/** The update body. */
	String UPDATE_BODY = "update_body";
	
	/** The other. */
	/*
	 * Arguments to actions
	 */
	String OTHER = "other";
	
	/** The bodies. */
	String BODIES = "bodies";
	
	/** The clearance. */
	String CLEARANCE = "clearance";
	
	/** The impulse. */
	String IMPULSE = "impulse";
	
	/** The force. */
	String FORCE = "force";
	
	/** The torque. */
	String TORQUE = "torque";

	/** The use native. */
	/*
	 * Attributes
	 */
	String USE_NATIVE = "use_native";
	
	/** The library name. */
	String LIBRARY_NAME = "library";
	
	/** The bullet library name. */
	String BULLET_LIBRARY_NAME = "bullet";
	
	/** The box2d library name. */
	String BOX2D_LIBRARY_NAME = "box2D";
	
	/** The rotation. */
	String ROTATION = "rotation";
	
	/** The velocity. */
	String VELOCITY = "velocity";
	
	/** The friction. */
	String FRICTION = "friction";
	
	/** The restitution. */
	String RESTITUTION = "restitution";
	
	/** The damping. */
	String DAMPING = "damping";
	
	/** The angular damping. */
	String ANGULAR_DAMPING = "angular_damping";
	
	/** The contact damping. */
	String CONTACT_DAMPING = "contact_damping";
	
	/** The angular velocity. */
	String ANGULAR_VELOCITY = "angular_velocity";
	
	/** The mass. */
	String MASS = "mass";
	
	/** The body. */
	String BODY = "%%rigid_body%%";
	
	/** The terrain. */
	String TERRAIN = "terrain";
	
	/** The aabb. */
	String AABB = "aabb";
	
	/** The gravity. */
	String GRAVITY = "gravity";
	
	/** The automated registration. */
	String AUTOMATED_REGISTRATION = "automated_registration";
	
	/** The max substeps. */
	String MAX_SUBSTEPS = "max_substeps";
	
	/** The accurate collision detection. */
	String ACCURATE_COLLISION_DETECTION = "accurate_collision_detection";

}
