/*******************************************************************************************************
 *
 * RigidBody.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.dynamics;

import static com.bulletphysics.Pools.MATRICES;
import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Matrix3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.broadphase.BroadphaseProxy;
import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.CollisionObjectType;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.constraintsolver.TypedConstraint;
import com.bulletphysics.linearmath.MatrixUtil;
import com.bulletphysics.linearmath.MiscUtil;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.TransformUtil;
import java.util.ArrayList;

/**
 * RigidBody is the main class for rigid body objects. It is derived from {@link CollisionObject}, so it keeps reference
 * to {@link CollisionShape}.
 * <p>
 *
 * It is recommended for performance and memory use to share {@link CollisionShape} objects whenever possible.
 * <p>
 *
 * There are 3 types of rigid bodies:<br>
 * <ol>
 * <li>Dynamic rigid bodies, with positive mass. Motion is controlled by rigid body dynamics.</li>
 * <li>Fixed objects with zero mass. They are not moving (basically collision objects).</li>
 * <li>Kinematic objects, which are objects without mass, but the user can move them. There is on-way interaction, and
 * Bullet calculates a velocity based on the timestep and previous and current world transform.</li>
 * </ol>
 *
 * Bullet automatically deactivates dynamic rigid bodies, when the velocity is below a threshold for a given time.
 * <p>
 *
 * Deactivated (sleeping) rigid bodies don't take any processing time, except a minor broadphase collision detection
 * impact (to allow active objects to activate/wake up sleeping objects).
 *
 * @author jezek2
 */
public class RigidBody extends CollisionObject {

	/** The Constant MAX_ANGVEL. */
	protected static final float MAX_ANGVEL = BulletGlobals.SIMD_HALF_PI;

	/** The inv inertia tensor world. */
	protected final Matrix3f invInertiaTensorWorld = new Matrix3f();
	
	/** The linear velocity. */
	protected final Vector3f linearVelocity = new Vector3f();
	
	/** The angular velocity. */
	protected final Vector3f angularVelocity = new Vector3f();
	
	/** The inverse mass. */
	protected float inverseMass;
	
	/** The angular factor. */
	protected float angularFactor;

	/** The gravity. */
	protected final Vector3f gravity = new Vector3f();
	
	/** The inv inertia local. */
	protected final Vector3f invInertiaLocal = new Vector3f();
	
	/** The total force. */
	protected final Vector3f totalForce = new Vector3f();
	
	/** The total torque. */
	protected final Vector3f totalTorque = new Vector3f();

	/** The linear damping. */
	protected float linearDamping;
	
	/** The angular damping. */
	protected float angularDamping;

	/** The additional damping. */
	protected boolean additionalDamping;
	
	/** The additional damping factor. */
	protected float additionalDampingFactor;
	
	/** The additional linear damping threshold sqr. */
	protected float additionalLinearDampingThresholdSqr;
	
	/** The additional angular damping threshold sqr. */
	protected float additionalAngularDampingThresholdSqr;
	
	/** The additional angular damping factor. */
	protected float additionalAngularDampingFactor;

	/** The linear sleeping threshold. */
	protected float linearSleepingThreshold;
	
	/** The angular sleeping threshold. */
	protected float angularSleepingThreshold;

	/** The optional motion state. */
	// optionalMotionState allows to automatic synchronize the world transform for active objects
	protected MotionState optionalMotionState;

	/** The constraint refs. */
	// keep track of typed constraints referencing this rigid body
	protected final ArrayList<TypedConstraint> constraintRefs = new ArrayList<>();

	/** The contact solver type. */
	// for experimental overriding of friction/contact solver func
	public int contactSolverType;
	
	/** The friction solver type. */
	public int frictionSolverType;

	/** The unique id. */
	protected static int uniqueId = 0;
	
	/** The debug body id. */
	public int debugBodyId;

	/**
	 * Instantiates a new rigid body.
	 *
	 * @param constructionInfo the construction info
	 */
	public RigidBody(final RigidBodyConstructionInfo constructionInfo) {
		setupRigidBody(constructionInfo);
	}

	/**
	 * Instantiates a new rigid body.
	 *
	 * @param mass the mass
	 * @param motionState the motion state
	 * @param collisionShape the collision shape
	 */
	public RigidBody(final float mass, final MotionState motionState, final CollisionShape collisionShape) {
		this(mass, motionState, collisionShape, new Vector3f(0f, 0f, 0f));
	}

	/**
	 * Instantiates a new rigid body.
	 *
	 * @param mass the mass
	 * @param motionState the motion state
	 * @param collisionShape the collision shape
	 * @param localInertia the local inertia
	 */
	public RigidBody(final float mass, final MotionState motionState, final CollisionShape collisionShape,
			final Vector3f localInertia) {
		RigidBodyConstructionInfo cinfo =
				new RigidBodyConstructionInfo(mass, motionState, collisionShape, localInertia);
		setupRigidBody(cinfo);
	}

	/**
	 * Sets the up rigid body.
	 *
	 * @param constructionInfo the new up rigid body
	 */
	protected void setupRigidBody(final RigidBodyConstructionInfo constructionInfo) {
		internalType = CollisionObjectType.RIGID_BODY;

		linearVelocity.set(0f, 0f, 0f);
		angularVelocity.set(0f, 0f, 0f);
		angularFactor = 1f;
		gravity.set(0f, 0f, 0f);
		totalForce.set(0f, 0f, 0f);
		totalTorque.set(0f, 0f, 0f);
		linearDamping = 0f;
		angularDamping = 0.5f;
		linearSleepingThreshold = constructionInfo.linearSleepingThreshold;
		angularSleepingThreshold = constructionInfo.angularSleepingThreshold;
		optionalMotionState = constructionInfo.motionState;
		contactSolverType = 0;
		frictionSolverType = 0;
		additionalDamping = constructionInfo.additionalDamping;
		additionalDampingFactor = constructionInfo.additionalDampingFactor;
		additionalLinearDampingThresholdSqr = constructionInfo.additionalLinearDampingThresholdSqr;
		additionalAngularDampingThresholdSqr = constructionInfo.additionalAngularDampingThresholdSqr;
		additionalAngularDampingFactor = constructionInfo.additionalAngularDampingFactor;

		if (optionalMotionState != null) {
			optionalMotionState.getWorldTransform(worldTransform);
		} else {
			worldTransform.set(constructionInfo.startWorldTransform);
		}

		interpolationWorldTransform.set(worldTransform);
		interpolationLinearVelocity.set(0f, 0f, 0f);
		interpolationAngularVelocity.set(0f, 0f, 0f);

		// moved to CollisionObject
		friction = constructionInfo.friction;
		restitution = constructionInfo.restitution;

		setCollisionShape(constructionInfo.collisionShape);
		debugBodyId = uniqueId++;

		setMassProps(constructionInfo.mass, constructionInfo.localInertia);
		setDamping(constructionInfo.linearDamping, constructionInfo.angularDamping);
		updateInertiaTensor();
	}

	/**
	 * Destroy.
	 */
	public void destroy() {
		// No constraints should point to this rigidbody
		// Remove constraints from the dynamics world before you delete the related rigidbodies.
		assert constraintRefs.size() == 0;
	}

	/**
	 * Proceed to transform.
	 *
	 * @param newTrans the new trans
	 */
	public void proceedToTransform(final Transform newTrans) {
		setCenterOfMassTransform(newTrans);
	}

	/**
	 * To keep collision detection and dynamics separate we don't store a rigidbody pointer, but a rigidbody is derived
	 * from CollisionObject, so we can safely perform an upcast.
	 */
	public static RigidBody upcast(final CollisionObject colObj) {
		if (colObj.getInternalType() == CollisionObjectType.RIGID_BODY) return (RigidBody) colObj;
		return null;
	}

	/**
	 * Continuous collision detection needs prediction.
	 */
	public void predictIntegratedTransform(final float timeStep, final Transform predictedTransform) {
		TransformUtil.integrateTransform(worldTransform, linearVelocity, angularVelocity, timeStep, predictedTransform);
	}

	/**
	 * Save kinematic state.
	 *
	 * @param timeStep the time step
	 */
	public void saveKinematicState(final float timeStep) {
		// todo: clamp to some (user definable) safe minimum timestep, to limit maximum angular/linear velocities
		if (timeStep != 0f) {
			// if we use motionstate to synchronize world transforms, get the new kinematic/animated world transform
			if (getMotionState() != null) { getMotionState().getWorldTransform(worldTransform); }
			// Vector3f linVel = new Vector3f(), angVel = new Vector3f();

			TransformUtil.calculateVelocity(interpolationWorldTransform, worldTransform, timeStep, linearVelocity,
					angularVelocity);
			interpolationLinearVelocity.set(linearVelocity);
			interpolationAngularVelocity.set(angularVelocity);
			interpolationWorldTransform.set(worldTransform);
			// printf("angular = %f %f
			// %f\n",m_angularVelocity.getX(),m_angularVelocity.getY(),m_angularVelocity.getZ());
		}
	}

	/**
	 * Apply gravity.
	 */
	public void applyGravity() {
		if (isStaticOrKinematicObject()) return;

		applyCentralForce(gravity);
	}

	/**
	 * Sets the gravity.
	 *
	 * @param acceleration the new gravity
	 */
	public void setGravity(final Vector3f acceleration) {
		if (inverseMass != 0f) { gravity.scale(1f / inverseMass, acceleration); }
	}

	/**
	 * Gets the gravity.
	 *
	 * @param out the out
	 * @return the gravity
	 */
	public Vector3f getGravity(final Vector3f out) {
		out.set(gravity);
		return out;
	}

	/**
	 * Sets the damping.
	 *
	 * @param lin_damping the lin damping
	 * @param ang_damping the ang damping
	 */
	public void setDamping(final float lin_damping, final float ang_damping) {
		linearDamping = MiscUtil.GEN_clamped(lin_damping, 0f, 1f);
		angularDamping = MiscUtil.GEN_clamped(ang_damping, 0f, 1f);
	}

	/**
	 * Gets the linear damping.
	 *
	 * @return the linear damping
	 */
	public float getLinearDamping() {
		return linearDamping;
	}

	/**
	 * Gets the angular damping.
	 *
	 * @return the angular damping
	 */
	public float getAngularDamping() {
		return angularDamping;
	}

	/**
	 * Gets the linear sleeping threshold.
	 *
	 * @return the linear sleeping threshold
	 */
	public float getLinearSleepingThreshold() {
		return linearSleepingThreshold;
	}

	/**
	 * Gets the angular sleeping threshold.
	 *
	 * @return the angular sleeping threshold
	 */
	public float getAngularSleepingThreshold() {
		return angularSleepingThreshold;
	}

	/**
	 * Damps the velocity, using the given linearDamping and angularDamping.
	 */
	public void applyDamping(final float timeStep) {
		// On new damping: see discussion/issue report here: http://code.google.com/p/bullet/issues/detail?id=74
		// todo: do some performance comparisons (but other parts of the engine are probably bottleneck anyway

		// #define USE_OLD_DAMPING_METHOD 1
		// #ifdef USE_OLD_DAMPING_METHOD
		// linearVelocity.scale(MiscUtil.GEN_clamped((1f - timeStep * linearDamping), 0f, 1f));
		// angularVelocity.scale(MiscUtil.GEN_clamped((1f - timeStep * angularDamping), 0f, 1f));
		// #else
		linearVelocity.scale((float) Math.pow(1f - linearDamping, timeStep));
		angularVelocity.scale((float) Math.pow(1f - angularDamping, timeStep));
		// #endif

		if (additionalDamping) {
			// Additional damping can help avoiding lowpass jitter motion, help stability for ragdolls etc.
			// Such damping is undesirable, so once the overall simulation quality of the rigid body dynamics system has
			// improved, this should become obsolete
			if (angularVelocity.lengthSquared() < additionalAngularDampingThresholdSqr
					&& linearVelocity.lengthSquared() < additionalLinearDampingThresholdSqr) {
				angularVelocity.scale(additionalDampingFactor);
				linearVelocity.scale(additionalDampingFactor);
			}

			float speed = linearVelocity.length();
			if (speed < linearDamping) {
				float dampVel = 0.005f;
				if (speed > dampVel) {
					Vector3f dir = VECTORS.get(linearVelocity);
					dir.normalize();
					dir.scale(dampVel);
					linearVelocity.sub(dir);
					VECTORS.release(dir);
				} else {
					linearVelocity.set(0f, 0f, 0f);
				}
			}

			float angSpeed = angularVelocity.length();
			if (angSpeed < angularDamping) {
				float angDampVel = 0.005f;
				if (angSpeed > angDampVel) {
					Vector3f dir = VECTORS.get(angularVelocity);
					dir.normalize();
					dir.scale(angDampVel);
					angularVelocity.sub(dir);
					VECTORS.release(dir);
				} else {
					angularVelocity.set(0f, 0f, 0f);
				}
			}
		}
	}

	/**
	 * Sets the mass props.
	 *
	 * @param mass the mass
	 * @param inertia the inertia
	 */
	public void setMassProps(final float mass, final Vector3f inertia) {
		if (mass == 0f) {
			collisionFlags |= CollisionFlags.STATIC_OBJECT;
			inverseMass = 0f;
		} else {
			collisionFlags &= ~CollisionFlags.STATIC_OBJECT;
			inverseMass = 1f / mass;
		}

		invInertiaLocal.set(inertia.x != 0f ? 1f / inertia.x : 0f, inertia.y != 0f ? 1f / inertia.y : 0f,
				inertia.z != 0f ? 1f / inertia.z : 0f);
	}

	/**
	 * Gets the inv mass.
	 *
	 * @return the inv mass
	 */
	public float getInvMass() {
		return inverseMass;
	}

	/**
	 * Gets the inv inertia tensor world.
	 *
	 * @param out the out
	 * @return the inv inertia tensor world
	 */
	public Matrix3f getInvInertiaTensorWorld(final Matrix3f out) {
		out.set(invInertiaTensorWorld);
		return out;
	}

	/**
	 * Integrate velocities.
	 *
	 * @param step the step
	 */
	public void integrateVelocities(final float step) {
		if (isStaticOrKinematicObject()) return;

		linearVelocity.scaleAdd(inverseMass * step, totalForce, linearVelocity);
		Vector3f tmp = VECTORS.get(totalTorque);
		invInertiaTensorWorld.transform(tmp);
		angularVelocity.scaleAdd(step, tmp, angularVelocity);
		VECTORS.release(tmp);
		// clamp angular velocity. collision calculations will fail on higher angular velocities
		float angvel = angularVelocity.length();
		if (angvel * step > MAX_ANGVEL) { angularVelocity.scale(MAX_ANGVEL / step / angvel); }
	}

	/**
	 * Sets the center of mass transform.
	 *
	 * @param xform the new center of mass transform
	 */
	public void setCenterOfMassTransform(final Transform xform) {
		if (isStaticOrKinematicObject()) {
			interpolationWorldTransform.set(worldTransform);
		} else {
			interpolationWorldTransform.set(xform);
		}
		getLinearVelocity(interpolationLinearVelocity);
		getAngularVelocity(interpolationAngularVelocity);
		worldTransform.set(xform);
		updateInertiaTensor();
	}

	/**
	 * Apply central force.
	 *
	 * @param force the force
	 */
	public void applyCentralForce(final Vector3f force) {
		totalForce.add(force);
	}

	/**
	 * Gets the inv inertia diag local.
	 *
	 * @param out the out
	 * @return the inv inertia diag local
	 */
	public Vector3f getInvInertiaDiagLocal(final Vector3f out) {
		out.set(invInertiaLocal);
		return out;
	}

	/**
	 * Sets the inv inertia diag local.
	 *
	 * @param diagInvInertia the new inv inertia diag local
	 */
	public void setInvInertiaDiagLocal(final Vector3f diagInvInertia) {
		invInertiaLocal.set(diagInvInertia);
	}

	/**
	 * Sets the sleeping thresholds.
	 *
	 * @param linear the linear
	 * @param angular the angular
	 */
	public void setSleepingThresholds(final float linear, final float angular) {
		linearSleepingThreshold = linear;
		angularSleepingThreshold = angular;
	}

	/**
	 * Apply torque.
	 *
	 * @param torque the torque
	 */
	public void applyTorque(final Vector3f torque) {
		totalTorque.add(torque);
	}

	/**
	 * Apply force.
	 *
	 * @param force the force
	 * @param rel_pos the rel pos
	 */
	public void applyForce(final Vector3f force, final Vector3f rel_pos) {
		applyCentralForce(force);

		Vector3f tmp = VECTORS.get();
		tmp.cross(rel_pos, force);
		tmp.scale(angularFactor);
		applyTorque(tmp);
		VECTORS.release(tmp);
	}

	/**
	 * Apply central impulse.
	 *
	 * @param impulse the impulse
	 */
	public void applyCentralImpulse(final Vector3f impulse) {
		System.out.println("Ask to apply impulse : " + impulse);
		Vector3f result = new Vector3f();
		result.add(linearVelocity);
		Vector3f scaledImpulse =
				new Vector3f(impulse.x * inverseMass, impulse.y * inverseMass, impulse.z * inverseMass);
		result.add(scaledImpulse);

		linearVelocity.set(result.x, result.y, result.z);
		// linearVelocity.scaleAdd(inverseMass, impulse, linearVelocity);
	}

	/**
	 * Apply torque impulse.
	 *
	 * @param torque the torque
	 */
	public void applyTorqueImpulse(final Vector3f torque) {
		Vector3f tmp = VECTORS.get(torque);
		invInertiaTensorWorld.transform(tmp);
		angularVelocity.add(tmp);
		VECTORS.release(tmp);
	}

	/**
	 * Apply impulse.
	 *
	 * @param impulse the impulse
	 * @param rel_pos the rel pos
	 */
	public void applyImpulse(final Vector3f impulse, final Vector3f rel_pos) {
		if (inverseMass != 0f) {
			applyCentralImpulse(impulse);
			if (angularFactor != 0f) {
				Vector3f tmp = VECTORS.get();
				tmp.cross(rel_pos, impulse);
				tmp.scale(angularFactor);
				applyTorqueImpulse(tmp);
				VECTORS.release(tmp);
			}
		}
	}

	/**
	 * Optimization for the iterative solver: avoid calculating constant terms involving inertia, normal, relative
	 * position.
	 */
	public void internalApplyImpulse(final Vector3f linearComponent, final Vector3f angularComponent,
			final float impulseMagnitude) {
		if (inverseMass != 0f) {
			linearVelocity.scaleAdd(impulseMagnitude, linearComponent, linearVelocity);
			if (angularFactor != 0f) {
				angularVelocity.scaleAdd(impulseMagnitude * angularFactor, angularComponent, angularVelocity);
			}
		}
	}

	/**
	 * Clear forces.
	 */
	public void clearForces() {
		totalForce.set(0f, 0f, 0f);
		totalTorque.set(0f, 0f, 0f);
	}

	/**
	 * Update inertia tensor.
	 */
	public void updateInertiaTensor() {
		Matrix3f mat1 = MATRICES.get();
		MatrixUtil.scale(mat1, worldTransform.basis, invInertiaLocal);

		Matrix3f mat2 = MATRICES.get(worldTransform.basis);
		mat2.transpose();

		invInertiaTensorWorld.mul(mat1, mat2);
		MATRICES.release(mat1, mat2);
	}

	/**
	 * Gets the center of mass position.
	 *
	 * @param out the out
	 * @return the center of mass position
	 */
	public Vector3f getCenterOfMassPosition(final Vector3f out) {
		out.set(worldTransform.origin);
		return out;
	}

	/**
	 * Gets the orientation.
	 *
	 * @param out the out
	 * @return the orientation
	 */
	public Quat4f getOrientation(final Quat4f out) {
		MatrixUtil.getRotation(worldTransform.basis, out);
		return out;
	}

	/**
	 * Gets the center of mass transform.
	 *
	 * @param out the out
	 * @return the center of mass transform
	 */
	public Transform getCenterOfMassTransform(final Transform out) {
		out.set(worldTransform);
		return out;
	}

	/**
	 * Gets the linear velocity.
	 *
	 * @param out the out
	 * @return the linear velocity
	 */
	public Vector3f getLinearVelocity(final Vector3f out) {
		out.set(linearVelocity);
		return out;
	}

	/**
	 * Gets the angular velocity.
	 *
	 * @param out the out
	 * @return the angular velocity
	 */
	public Vector3f getAngularVelocity(final Vector3f out) {
		out.set(angularVelocity);
		return out;
	}

	/**
	 * Sets the linear velocity.
	 *
	 * @param lin_vel the new linear velocity
	 */
	public void setLinearVelocity(final Vector3f lin_vel) {
		assert collisionFlags != CollisionFlags.STATIC_OBJECT;
		linearVelocity.set(lin_vel);
	}

	/**
	 * Sets the angular velocity.
	 *
	 * @param ang_vel the new angular velocity
	 */
	public void setAngularVelocity(final Vector3f ang_vel) {
		assert collisionFlags != CollisionFlags.STATIC_OBJECT;
		angularVelocity.set(ang_vel);
	}

	/**
	 * Gets the velocity in local point.
	 *
	 * @param rel_pos the rel pos
	 * @param out the out
	 * @return the velocity in local point
	 */
	public Vector3f getVelocityInLocalPoint(final Vector3f rel_pos, final Vector3f out) {
		// we also calculate lin/ang velocity for kinematic objects
		Vector3f vec = out;
		vec.cross(angularVelocity, rel_pos);
		vec.add(linearVelocity);
		return out;

		// for kinematic objects, we could also use use:
		// return (m_worldTransform(rel_pos) - m_interpolationWorldTransform(rel_pos)) / m_kinematicTimeStep;
	}

	/**
	 * Translate.
	 *
	 * @param v the v
	 */
	public void translate(final Vector3f v) {
		worldTransform.origin.add(v);
	}

	/**
	 * Gets the aabb.
	 *
	 * @param aabbMin the aabb min
	 * @param aabbMax the aabb max
	 * @return the aabb
	 */
	public void getAabb(final Vector3f aabbMin, final Vector3f aabbMax) {
		getCollisionShape().getAabb(worldTransform, aabbMin, aabbMax);
	}

	/**
	 * Compute impulse denominator.
	 *
	 * @param pos the pos
	 * @param normal the normal
	 * @return the float
	 */
	public float computeImpulseDenominator(final Vector3f pos, final Vector3f normal) {
		Vector3f r0 = VECTORS.get();
		r0.sub(pos, getCenterOfMassPosition(VECTORS.get()));

		Vector3f c0 = VECTORS.get();
		c0.cross(r0, normal);

		Vector3f tmp = VECTORS.get();
		Matrix3f mmp = MATRICES.get();
		MatrixUtil.transposeTransform(tmp, c0, getInvInertiaTensorWorld(mmp));

		Vector3f vec = VECTORS.get();
		vec.cross(tmp, r0);

		float result = inverseMass + normal.dot(vec);
		VECTORS.release(r0, c0, tmp, vec);
		MATRICES.release(mmp);
		return result;
	}

	/**
	 * Compute angular impulse denominator.
	 *
	 * @param axis the axis
	 * @return the float
	 */
	public float computeAngularImpulseDenominator(final Vector3f axis) {
		Vector3f vec = VECTORS.get();
		Matrix3f mmp = MATRICES.get();
		MatrixUtil.transposeTransform(vec, axis, getInvInertiaTensorWorld(mmp));
		float result = axis.dot(vec);
		VECTORS.release(vec);
		MATRICES.release(mmp);
		return result;
	}

	/**
	 * Update deactivation.
	 *
	 * @param timeStep the time step
	 */
	public void updateDeactivation(final float timeStep) {
		if (getActivationState() == ISLAND_SLEEPING || getActivationState() == DISABLE_DEACTIVATION) return;
		Vector3f lin = getLinearVelocity(VECTORS.get());
		Vector3f ang = getAngularVelocity(VECTORS.get());
		if (lin.lengthSquared() < linearSleepingThreshold * linearSleepingThreshold
				&& ang.lengthSquared() < angularSleepingThreshold * angularSleepingThreshold) {
			deactivationTime += timeStep;
		} else {
			deactivationTime = 0f;
			setActivationState(0);
		}
		VECTORS.release(lin, ang);
	}

	/**
	 * Wants sleeping.
	 *
	 * @return true, if successful
	 */
	public boolean wantsSleeping() {
		if (getActivationState() == DISABLE_DEACTIVATION) return false;

		// disable deactivation
		if (BulletGlobals.isDeactivationDisabled() || BulletGlobals.getDeactivationTime() == 0f) return false;

		if (getActivationState() == ISLAND_SLEEPING || getActivationState() == WANTS_DEACTIVATION) return true;

		if (deactivationTime > BulletGlobals.getDeactivationTime()) return true;
		return false;
	}

	/**
	 * Gets the broadphase proxy.
	 *
	 * @return the broadphase proxy
	 */
	public BroadphaseProxy getBroadphaseProxy() {
		return broadphaseHandle;
	}

	/**
	 * Sets the new broadphase proxy.
	 *
	 * @param broadphaseProxy the new new broadphase proxy
	 */
	public void setNewBroadphaseProxy(final BroadphaseProxy broadphaseProxy) {
		this.broadphaseHandle = broadphaseProxy;
	}

	/**
	 * Gets the motion state.
	 *
	 * @return the motion state
	 */
	public MotionState getMotionState() {
		return optionalMotionState;
	}

	/**
	 * Sets the motion state.
	 *
	 * @param motionState the new motion state
	 */
	public void setMotionState(final MotionState motionState) {
		this.optionalMotionState = motionState;
		if (optionalMotionState != null) { motionState.getWorldTransform(worldTransform); }
	}

	/**
	 * Sets the angular factor.
	 *
	 * @param angFac the new angular factor
	 */
	public void setAngularFactor(final float angFac) {
		angularFactor = angFac;
	}

	/**
	 * Gets the angular factor.
	 *
	 * @return the angular factor
	 */
	public float getAngularFactor() {
		return angularFactor;
	}

	/**
	 * Is this rigidbody added to a CollisionWorld/DynamicsWorld/Broadphase?
	 */
	public boolean isInWorld() {
		return getBroadphaseProxy() != null;
	}

	@Override
	public boolean checkCollideWithOverride(final CollisionObject co) {
		// TODO: change to cast
		RigidBody otherRb = RigidBody.upcast(co);
		if (otherRb == null) return true;

		for (int i = 0; i < constraintRefs.size(); ++i) {
			TypedConstraint c = constraintRefs.get(i);
			if (c.getRigidBodyA() == otherRb || c.getRigidBodyB() == otherRb) return false;
		}

		return true;
	}

	/**
	 * Adds the constraint ref.
	 *
	 * @param c the c
	 */
	public void addConstraintRef(final TypedConstraint c) {
		int index = constraintRefs.indexOf(c);
		if (index == -1) { constraintRefs.add(c); }

		checkCollideWith = true;
	}

	/**
	 * Removes the constraint ref.
	 *
	 * @param c the c
	 */
	public void removeConstraintRef(final TypedConstraint c) {
		constraintRefs.remove(c);
		checkCollideWith = constraintRefs.size() > 0;
	}

	/**
	 * Gets the constraint ref.
	 *
	 * @param index the index
	 * @return the constraint ref
	 */
	public TypedConstraint getConstraintRef(final int index) {
		return constraintRefs.get(index);
	}

	/**
	 * Gets the num constraint refs.
	 *
	 * @return the num constraint refs
	 */
	public int getNumConstraintRefs() {
		return constraintRefs.size();
	}

}
