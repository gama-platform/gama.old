/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * Bullet Continuous Collision Detection and Physics Library Copyright (c) 2003-2008 Erwin Coumans
 * http://www.bulletphysics.com/
 *
 * This software is provided 'as-is', without any express or implied warranty. In no event will the authors be held
 * liable for any damages arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose, including commercial applications, and to alter
 * it and redistribute it freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not claim that you wrote the original software.
 * If you use this software in a product, an acknowledgment in the product documentation would be appreciated but is not
 * required. 2. Altered source versions must be plainly marked as such, and must not be misrepresented as being the
 * original software. 3. This notice may not be removed or altered from any source distribution.
 */

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

	protected static final float MAX_ANGVEL = BulletGlobals.SIMD_HALF_PI;

	protected final Matrix3f invInertiaTensorWorld = new Matrix3f();
	protected final Vector3f linearVelocity = new Vector3f();
	protected final Vector3f angularVelocity = new Vector3f();
	protected float inverseMass;
	protected float angularFactor;

	protected final Vector3f gravity = new Vector3f();
	protected final Vector3f invInertiaLocal = new Vector3f();
	protected final Vector3f totalForce = new Vector3f();
	protected final Vector3f totalTorque = new Vector3f();

	protected float linearDamping;
	protected float angularDamping;

	protected boolean additionalDamping;
	protected float additionalDampingFactor;
	protected float additionalLinearDampingThresholdSqr;
	protected float additionalAngularDampingThresholdSqr;
	protected float additionalAngularDampingFactor;

	protected float linearSleepingThreshold;
	protected float angularSleepingThreshold;

	// optionalMotionState allows to automatic synchronize the world transform for active objects
	protected MotionState optionalMotionState;

	// keep track of typed constraints referencing this rigid body
	protected final ArrayList<TypedConstraint> constraintRefs = new ArrayList<>();

	// for experimental overriding of friction/contact solver func
	public int contactSolverType;
	public int frictionSolverType;

	protected static int uniqueId = 0;
	public int debugBodyId;

	public RigidBody(final RigidBodyConstructionInfo constructionInfo) {
		setupRigidBody(constructionInfo);
	}

	public RigidBody(final float mass, final MotionState motionState, final CollisionShape collisionShape) {
		this(mass, motionState, collisionShape, new Vector3f(0f, 0f, 0f));
	}

	public RigidBody(final float mass, final MotionState motionState, final CollisionShape collisionShape,
			final Vector3f localInertia) {
		RigidBodyConstructionInfo cinfo =
				new RigidBodyConstructionInfo(mass, motionState, collisionShape, localInertia);
		setupRigidBody(cinfo);
	}

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

	public void destroy() {
		// No constraints should point to this rigidbody
		// Remove constraints from the dynamics world before you delete the related rigidbodies.
		assert constraintRefs.size() == 0;
	}

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

	public void applyGravity() {
		if (isStaticOrKinematicObject()) return;

		applyCentralForce(gravity);
	}

	public void setGravity(final Vector3f acceleration) {
		if (inverseMass != 0f) { gravity.scale(1f / inverseMass, acceleration); }
	}

	public Vector3f getGravity(final Vector3f out) {
		out.set(gravity);
		return out;
	}

	public void setDamping(final float lin_damping, final float ang_damping) {
		linearDamping = MiscUtil.GEN_clamped(lin_damping, 0f, 1f);
		angularDamping = MiscUtil.GEN_clamped(ang_damping, 0f, 1f);
	}

	public float getLinearDamping() {
		return linearDamping;
	}

	public float getAngularDamping() {
		return angularDamping;
	}

	public float getLinearSleepingThreshold() {
		return linearSleepingThreshold;
	}

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

	public float getInvMass() {
		return inverseMass;
	}

	public Matrix3f getInvInertiaTensorWorld(final Matrix3f out) {
		out.set(invInertiaTensorWorld);
		return out;
	}

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

	public void applyCentralForce(final Vector3f force) {
		totalForce.add(force);
	}

	public Vector3f getInvInertiaDiagLocal(final Vector3f out) {
		out.set(invInertiaLocal);
		return out;
	}

	public void setInvInertiaDiagLocal(final Vector3f diagInvInertia) {
		invInertiaLocal.set(diagInvInertia);
	}

	public void setSleepingThresholds(final float linear, final float angular) {
		linearSleepingThreshold = linear;
		angularSleepingThreshold = angular;
	}

	public void applyTorque(final Vector3f torque) {
		totalTorque.add(torque);
	}

	public void applyForce(final Vector3f force, final Vector3f rel_pos) {
		applyCentralForce(force);

		Vector3f tmp = VECTORS.get();
		tmp.cross(rel_pos, force);
		tmp.scale(angularFactor);
		applyTorque(tmp);
		VECTORS.release(tmp);
	}

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

	public void applyTorqueImpulse(final Vector3f torque) {
		Vector3f tmp = VECTORS.get(torque);
		invInertiaTensorWorld.transform(tmp);
		angularVelocity.add(tmp);
		VECTORS.release(tmp);
	}

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

	public void clearForces() {
		totalForce.set(0f, 0f, 0f);
		totalTorque.set(0f, 0f, 0f);
	}

	public void updateInertiaTensor() {
		Matrix3f mat1 = MATRICES.get();
		MatrixUtil.scale(mat1, worldTransform.basis, invInertiaLocal);

		Matrix3f mat2 = MATRICES.get(worldTransform.basis);
		mat2.transpose();

		invInertiaTensorWorld.mul(mat1, mat2);
		MATRICES.release(mat1, mat2);
	}

	public Vector3f getCenterOfMassPosition(final Vector3f out) {
		out.set(worldTransform.origin);
		return out;
	}

	public Quat4f getOrientation(final Quat4f out) {
		MatrixUtil.getRotation(worldTransform.basis, out);
		return out;
	}

	public Transform getCenterOfMassTransform(final Transform out) {
		out.set(worldTransform);
		return out;
	}

	public Vector3f getLinearVelocity(final Vector3f out) {
		out.set(linearVelocity);
		return out;
	}

	public Vector3f getAngularVelocity(final Vector3f out) {
		out.set(angularVelocity);
		return out;
	}

	public void setLinearVelocity(final Vector3f lin_vel) {
		assert collisionFlags != CollisionFlags.STATIC_OBJECT;
		linearVelocity.set(lin_vel);
	}

	public void setAngularVelocity(final Vector3f ang_vel) {
		assert collisionFlags != CollisionFlags.STATIC_OBJECT;
		angularVelocity.set(ang_vel);
	}

	public Vector3f getVelocityInLocalPoint(final Vector3f rel_pos, final Vector3f out) {
		// we also calculate lin/ang velocity for kinematic objects
		Vector3f vec = out;
		vec.cross(angularVelocity, rel_pos);
		vec.add(linearVelocity);
		return out;

		// for kinematic objects, we could also use use:
		// return (m_worldTransform(rel_pos) - m_interpolationWorldTransform(rel_pos)) / m_kinematicTimeStep;
	}

	public void translate(final Vector3f v) {
		worldTransform.origin.add(v);
	}

	public void getAabb(final Vector3f aabbMin, final Vector3f aabbMax) {
		getCollisionShape().getAabb(worldTransform, aabbMin, aabbMax);
	}

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

	public float computeAngularImpulseDenominator(final Vector3f axis) {
		Vector3f vec = VECTORS.get();
		Matrix3f mmp = MATRICES.get();
		MatrixUtil.transposeTransform(vec, axis, getInvInertiaTensorWorld(mmp));
		float result = axis.dot(vec);
		VECTORS.release(vec);
		MATRICES.release(mmp);
		return result;
	}

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

	public boolean wantsSleeping() {
		if (getActivationState() == DISABLE_DEACTIVATION) return false;

		// disable deactivation
		if (BulletGlobals.isDeactivationDisabled() || BulletGlobals.getDeactivationTime() == 0f) return false;

		if (getActivationState() == ISLAND_SLEEPING || getActivationState() == WANTS_DEACTIVATION) return true;

		if (deactivationTime > BulletGlobals.getDeactivationTime()) return true;
		return false;
	}

	public BroadphaseProxy getBroadphaseProxy() {
		return broadphaseHandle;
	}

	public void setNewBroadphaseProxy(final BroadphaseProxy broadphaseProxy) {
		this.broadphaseHandle = broadphaseProxy;
	}

	public MotionState getMotionState() {
		return optionalMotionState;
	}

	public void setMotionState(final MotionState motionState) {
		this.optionalMotionState = motionState;
		if (optionalMotionState != null) { motionState.getWorldTransform(worldTransform); }
	}

	public void setAngularFactor(final float angFac) {
		angularFactor = angFac;
	}

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

	public void addConstraintRef(final TypedConstraint c) {
		int index = constraintRefs.indexOf(c);
		if (index == -1) { constraintRefs.add(c); }

		checkCollideWith = true;
	}

	public void removeConstraintRef(final TypedConstraint c) {
		constraintRefs.remove(c);
		checkCollideWith = constraintRefs.size() > 0;
	}

	public TypedConstraint getConstraintRef(final int index) {
		return constraintRefs.get(index);
	}

	public int getNumConstraintRefs() {
		return constraintRefs.size();
	}

}
