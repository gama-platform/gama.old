/*******************************************************************************************************
 *
 * CollisionObject.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.dispatch;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseProxy;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.linearmath.Transform;

/**
 * CollisionObject can be used to manage collision detection objects. It maintains all information that is needed for a
 * collision detection: {@link CollisionShape}, {@link Transform} and {@link BroadphaseProxy AABB proxy}. It can be
 * added to {@link CollisionWorld}.
 *
 * @author jezek2
 */
public class CollisionObject {

	// protected final BulletStack stack = BulletStack.get();

	/** The Constant ACTIVE_TAG. */
	// island management, m_activationState1
	public static final int ACTIVE_TAG = 1;
	
	/** The Constant ISLAND_SLEEPING. */
	public static final int ISLAND_SLEEPING = 2;
	
	/** The Constant WANTS_DEACTIVATION. */
	public static final int WANTS_DEACTIVATION = 3;
	
	/** The Constant DISABLE_DEACTIVATION. */
	public static final int DISABLE_DEACTIVATION = 4;
	
	/** The Constant DISABLE_SIMULATION. */
	public static final int DISABLE_SIMULATION = 5;
	
	/** The world transform. */
	protected Transform worldTransform = new Transform();

	/// m_interpolationWorldTransform is used for CCD and interpolation
	/** The interpolation world transform. */
	/// it can be either previous or future (predicted) transform
	protected final Transform interpolationWorldTransform = new Transform();
	// those two are experimental: just added for bullet time effect, so you can still apply impulses (directly
	// modifying velocities)
	/** The interpolation linear velocity. */
	// without destroying the continuous interpolated motion (which uses this interpolation velocities)
	protected final Vector3f interpolationLinearVelocity = new Vector3f();
	
	/** The interpolation angular velocity. */
	protected final Vector3f interpolationAngularVelocity = new Vector3f();
	
	/** The broadphase handle. */
	protected BroadphaseProxy broadphaseHandle;
	
	/** The collision shape. */
	protected CollisionShape collisionShape;

	// rootCollisionShape is temporarily used to store the original collision shape
	// The collisionShape might be temporarily replaced by a child collision shape during collision detection purposes
	/** The root collision shape. */
	// If it is null, the collisionShape is not temporarily replaced.
	protected CollisionShape rootCollisionShape;

	/** The collision flags. */
	protected int collisionFlags;
	
	/** The island tag 1. */
	protected int islandTag1;
	
	/** The companion id. */
	protected int companionId;
	
	/** The activation state 1. */
	protected int activationState1;
	
	/** The deactivation time. */
	protected float deactivationTime;
	
	/** The friction. */
	protected float friction;
	
	/** The restitution. */
	protected float restitution;

	/** The user object pointer. */
	/// users can point to their objects, m_userPointer is not used by Bullet, see setUserPointer/getUserPointer
	protected Object userObjectPointer;

	// internalType is reserved to distinguish Bullet's CollisionObject, RigidBody, SoftBody etc.
	/** The internal type. */
	// do not assign your own internalType unless you write a new dynamics object class.
	protected CollisionObjectType internalType = CollisionObjectType.COLLISION_OBJECT;

	/** The hit fraction. */
	/// time of impact calculation
	protected float hitFraction;
	
	/** The ccd swept sphere radius. */
	/// Swept sphere radius (0.0 by default), see btConvexConvexAlgorithm::
	protected float ccdSweptSphereRadius;

	/** The ccd motion threshold. */
	/// Don't do continuous collision detection if the motion (in one step) is less then ccdMotionThreshold
	protected float ccdMotionThreshold = 0f;
	
	/** The check collide with. */
	/// If some object should have elaborate collision filtering by sub-classes
	protected boolean checkCollideWith;

	/**
	 * Instantiates a new collision object.
	 */
	public CollisionObject() {
		this.collisionFlags = CollisionFlags.STATIC_OBJECT;
		this.islandTag1 = -1;
		this.companionId = -1;
		this.activationState1 = 1;
		this.friction = 0.5f;
		this.hitFraction = 1f;
	}

	/**
	 * Check collide with override.
	 *
	 * @param co the co
	 * @return true, if successful
	 */
	public boolean checkCollideWithOverride(final CollisionObject co) {
		return true;
	}

	/**
	 * Merges simulation islands.
	 *
	 * @return true, if successful
	 */
	public boolean mergesSimulationIslands() {
		/// static objects, kinematic and object without contact response don't merge islands
		return (collisionFlags & (CollisionFlags.STATIC_OBJECT | CollisionFlags.KINEMATIC_OBJECT
				| CollisionFlags.NO_CONTACT_RESPONSE)) == 0;
	}

	/**
	 * Checks if is static object.
	 *
	 * @return true, if is static object
	 */
	public boolean isStaticObject() {
		return (collisionFlags & CollisionFlags.STATIC_OBJECT) != 0;
	}

	/**
	 * Checks if is kinematic object.
	 *
	 * @return true, if is kinematic object
	 */
	public boolean isKinematicObject() {
		return (collisionFlags & CollisionFlags.KINEMATIC_OBJECT) != 0;
	}

	/**
	 * Checks if is static or kinematic object.
	 *
	 * @return true, if is static or kinematic object
	 */
	public boolean isStaticOrKinematicObject() {
		return (collisionFlags & (CollisionFlags.KINEMATIC_OBJECT | CollisionFlags.STATIC_OBJECT)) != 0;
	}

	/**
	 * Checks for contact response.
	 *
	 * @return true, if successful
	 */
	public boolean hasContactResponse() {
		return (collisionFlags & CollisionFlags.NO_CONTACT_RESPONSE) == 0;
	}

	/**
	 * Gets the collision shape.
	 *
	 * @return the collision shape
	 */
	public CollisionShape getCollisionShape() {
		return collisionShape;
	}

	/**
	 * Sets the collision shape.
	 *
	 * @param collisionShape the new collision shape
	 */
	public void setCollisionShape(final CollisionShape collisionShape) {
		this.collisionShape = collisionShape;
		this.rootCollisionShape = collisionShape;
	}

	/**
	 * Gets the root collision shape.
	 *
	 * @return the root collision shape
	 */
	public CollisionShape getRootCollisionShape() {
		return rootCollisionShape;
	}

	/**
	 * Avoid using this internal API call. internalSetTemporaryCollisionShape is used to temporary replace the actual
	 * collision shape by a child collision shape.
	 */
	public void internalSetTemporaryCollisionShape(final CollisionShape collisionShape) {
		this.collisionShape = collisionShape;
	}

	/**
	 * Gets the activation state.
	 *
	 * @return the activation state
	 */
	public int getActivationState() {
		return activationState1;
	}

	/**
	 * Sets the activation state.
	 *
	 * @param newState the new activation state
	 */
	public void setActivationState(final int newState) {
		if (activationState1 != DISABLE_DEACTIVATION && activationState1 != DISABLE_SIMULATION) {
			this.activationState1 = newState;
		}
	}

	/**
	 * Gets the deactivation time.
	 *
	 * @return the deactivation time
	 */
	public float getDeactivationTime() {
		return deactivationTime;
	}

	/**
	 * Sets the deactivation time.
	 *
	 * @param deactivationTime the new deactivation time
	 */
	public void setDeactivationTime(final float deactivationTime) {
		this.deactivationTime = deactivationTime;
	}

	/**
	 * Force activation state.
	 *
	 * @param newState the new state
	 */
	public void forceActivationState(final int newState) {
		this.activationState1 = newState;
	}

	/**
	 * Activate.
	 */
	public void activate() {
		activate(false);
	}

	/**
	 * Activate.
	 *
	 * @param forceActivation the force activation
	 */
	public void activate(final boolean forceActivation) {
		if (forceActivation
				|| (collisionFlags & (CollisionFlags.STATIC_OBJECT | CollisionFlags.KINEMATIC_OBJECT)) == 0) {
			setActivationState(ACTIVE_TAG);
			deactivationTime = 0f;
		}
	}

	/**
	 * Checks if is active.
	 *
	 * @return true, if is active
	 */
	public boolean isActive() {
		return getActivationState() != ISLAND_SLEEPING && getActivationState() != DISABLE_SIMULATION;
	}

	/**
	 * Gets the restitution.
	 *
	 * @return the restitution
	 */
	public float getRestitution() {
		return restitution;
	}

	/**
	 * Sets the restitution.
	 *
	 * @param restitution the new restitution
	 */
	public void setRestitution(final float restitution) {
		this.restitution = restitution;
	}

	/**
	 * Gets the friction.
	 *
	 * @return the friction
	 */
	public float getFriction() {
		return friction;
	}

	/**
	 * Sets the friction.
	 *
	 * @param friction the new friction
	 */
	public void setFriction(final float friction) {
		this.friction = friction;
	}

	/**
	 * Gets the internal type.
	 *
	 * @return the internal type
	 */
	// reserved for Bullet internal usage
	public CollisionObjectType getInternalType() {
		return internalType;
	}

	/**
	 * Gets the world transform.
	 *
	 * @param out the out
	 * @return the world transform
	 */
	public Transform getWorldTransform(final Transform out) {
		out.set(worldTransform);
		return out;
	}

	/**
	 * Sets the world transform.
	 *
	 * @param worldTransform the new world transform
	 */
	public void setWorldTransform(final Transform worldTransform) {
		this.worldTransform.set(worldTransform);
	}

	/**
	 * Gets the broadphase handle.
	 *
	 * @return the broadphase handle
	 */
	public BroadphaseProxy getBroadphaseHandle() {
		return broadphaseHandle;
	}

	/**
	 * Sets the broadphase handle.
	 *
	 * @param broadphaseHandle the new broadphase handle
	 */
	public void setBroadphaseHandle(final BroadphaseProxy broadphaseHandle) {
		this.broadphaseHandle = broadphaseHandle;
	}

	/**
	 * Gets the interpolation world transform.
	 *
	 * @param out the out
	 * @return the interpolation world transform
	 */
	public Transform getInterpolationWorldTransform(final Transform out) {
		out.set(interpolationWorldTransform);
		return out;
	}

	/**
	 * Sets the interpolation world transform.
	 *
	 * @param interpolationWorldTransform the new interpolation world transform
	 */
	public void setInterpolationWorldTransform(final Transform interpolationWorldTransform) {
		this.interpolationWorldTransform.set(interpolationWorldTransform);
	}

	/**
	 * Sets the interpolation linear velocity.
	 *
	 * @param linvel the new interpolation linear velocity
	 */
	public void setInterpolationLinearVelocity(final Vector3f linvel) {
		interpolationLinearVelocity.set(linvel);
	}

	/**
	 * Sets the interpolation angular velocity.
	 *
	 * @param angvel the new interpolation angular velocity
	 */
	public void setInterpolationAngularVelocity(final Vector3f angvel) {
		interpolationAngularVelocity.set(angvel);
	}

	/**
	 * Gets the interpolation linear velocity.
	 *
	 * @param out the out
	 * @return the interpolation linear velocity
	 */
	public Vector3f getInterpolationLinearVelocity(final Vector3f out) {
		out.set(interpolationLinearVelocity);
		return out;
	}

	/**
	 * Gets the interpolation angular velocity.
	 *
	 * @param out the out
	 * @return the interpolation angular velocity
	 */
	public Vector3f getInterpolationAngularVelocity(final Vector3f out) {
		out.set(interpolationAngularVelocity);
		return out;
	}

	/**
	 * Gets the island tag.
	 *
	 * @return the island tag
	 */
	public int getIslandTag() {
		return islandTag1;
	}

	/**
	 * Sets the island tag.
	 *
	 * @param islandTag the new island tag
	 */
	public void setIslandTag(final int islandTag) {
		this.islandTag1 = islandTag;
	}

	/**
	 * Gets the companion id.
	 *
	 * @return the companion id
	 */
	public int getCompanionId() {
		return companionId;
	}

	/**
	 * Sets the companion id.
	 *
	 * @param companionId the new companion id
	 */
	public void setCompanionId(final int companionId) {
		this.companionId = companionId;
	}

	/**
	 * Gets the hit fraction.
	 *
	 * @return the hit fraction
	 */
	public float getHitFraction() {
		return hitFraction;
	}

	/**
	 * Sets the hit fraction.
	 *
	 * @param hitFraction the new hit fraction
	 */
	public void setHitFraction(final float hitFraction) {
		this.hitFraction = hitFraction;
	}

	/**
	 * Gets the collision flags.
	 *
	 * @return the collision flags
	 */
	public int getCollisionFlags() {
		return collisionFlags;
	}

	/**
	 * Sets the collision flags.
	 *
	 * @param collisionFlags the new collision flags
	 */
	public void setCollisionFlags(final int collisionFlags) {
		this.collisionFlags = collisionFlags;
	}

	/**
	 * Gets the ccd swept sphere radius.
	 *
	 * @return the ccd swept sphere radius
	 */
	// Swept sphere radius (0.0 by default), see btConvexConvexAlgorithm::
	public float getCcdSweptSphereRadius() {
		return ccdSweptSphereRadius;
	}

	/**
	 * Sets the ccd swept sphere radius.
	 *
	 * @param ccdSweptSphereRadius the new ccd swept sphere radius
	 */
	// Swept sphere radius (0.0 by default), see btConvexConvexAlgorithm::
	public void setCcdSweptSphereRadius(final float ccdSweptSphereRadius) {
		this.ccdSweptSphereRadius = ccdSweptSphereRadius;
	}

	/**
	 * Gets the ccd motion threshold.
	 *
	 * @return the ccd motion threshold
	 */
	public float getCcdMotionThreshold() {
		return ccdMotionThreshold;
	}

	/**
	 * Gets the ccd square motion threshold.
	 *
	 * @return the ccd square motion threshold
	 */
	public float getCcdSquareMotionThreshold() {
		return ccdMotionThreshold * ccdMotionThreshold;
	}

	/**
	 * Sets the ccd motion threshold.
	 *
	 * @param ccdMotionThreshold the new ccd motion threshold
	 */
	// Don't do continuous collision detection if the motion (in one step) is less then ccdMotionThreshold
	public void setCcdMotionThreshold(final float ccdMotionThreshold) {
		this.ccdMotionThreshold = ccdMotionThreshold;
	}

	/**
	 * Gets the user pointer.
	 *
	 * @return the user pointer
	 */
	public Object getUserPointer() {
		return userObjectPointer;
	}

	/**
	 * Sets the user pointer.
	 *
	 * @param userObjectPointer the new user pointer
	 */
	public void setUserPointer(final Object userObjectPointer) {
		this.userObjectPointer = userObjectPointer;
	}

	/**
	 * Check collide with.
	 *
	 * @param co the co
	 * @return true, if successful
	 */
	public boolean checkCollideWith(final CollisionObject co) {
		if (checkCollideWith) return checkCollideWithOverride(co);

		return true;
	}
}
