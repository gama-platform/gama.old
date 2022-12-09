/*******************************************************************************************************
 *
 * KinematicCharacterController.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.dynamics.character;

import static com.bulletphysics.Pools.TRANSFORMS;
import static com.bulletphysics.Pools.VECTORS;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.broadphase.BroadphasePair;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.collision.dispatch.GhostObject;
import com.bulletphysics.collision.dispatch.PairCachingGhostObject;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.shapes.ConvexShape;
import com.bulletphysics.dynamics.ActionInterface;
import com.bulletphysics.linearmath.Transform;

/**
 * KinematicCharacterController is an object that supports a sliding motion in a world. It uses a {@link GhostObject}
 * and convex sweep test to test for upcoming collisions. This is combined with discrete collision detection to recover
 * from penetrations.
 * <p>
 *
 * Interaction between KinematicCharacterController and dynamic rigid bodies needs to be explicity implemented by the
 * user.
 *
 * @author tomrbryn
 */
public class KinematicCharacterController extends ActionInterface {

	/** The up axis direction. */
	private static Vector3f[] upAxisDirection = new Vector3f[] { new Vector3f(1.0f, 0.0f, 0.0f),
			new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(0.0f, 0.0f, 1.0f), };

	/** The half height. */
	protected float halfHeight;

	/** The ghost object. */
	protected PairCachingGhostObject ghostObject;

	// is also in ghostObject, but it needs to be convex, so we store it here
	/** The convex shape. */
	// to avoid upcast
	protected ConvexShape convexShape;

	/** The vertical velocity. */
	protected float verticalVelocity;
	
	/** The vertical offset. */
	protected float verticalOffset;

	/** The fall speed. */
	protected float fallSpeed;
	
	/** The jump speed. */
	protected float jumpSpeed;
	
	/** The max jump height. */
	protected float maxJumpHeight;

	/** The max slope radians. */
	protected float maxSlopeRadians; // Slope angle that is set (used for returning the exact value)
	
	/** The max slope cosine. */
	protected float maxSlopeCosine; // Cosine equivalent of m_maxSlopeRadians (calculated once when set, for
									// optimization)

	/** The gravity. */
									protected float gravity;

	/** The turn angle. */
	protected float turnAngle;

	/** The step height. */
	protected float stepHeight;

	/** The added margin. */
	protected float addedMargin; // @todo: remove this and fix the code

	/** The walk direction. */
	// this is the desired walk direction, set by the user
	protected Vector3f walkDirection = new Vector3f();
	
	/** The normalized direction. */
	protected Vector3f normalizedDirection = new Vector3f();

	/** The current position. */
	// some internal variables
	protected Vector3f currentPosition = new Vector3f();
	
	/** The current step offset. */
	protected float currentStepOffset;
	
	/** The target position. */
	protected Vector3f targetPosition = new Vector3f();

	/** The manifold array. */
	// keep track of the contact manifolds
	ArrayList<PersistentManifold> manifoldArray = new ArrayList<>();

	/** The touching contact. */
	protected boolean touchingContact;
	
	/** The touching normal. */
	protected Vector3f touchingNormal = new Vector3f();

	/** The was on ground. */
	protected boolean wasOnGround;
	
	/** The was jumping. */
	protected boolean wasJumping;

	/** The use ghost object sweep test. */
	protected boolean useGhostObjectSweepTest;
	
	/** The use walk direction. */
	protected boolean useWalkDirection;
	
	/** The velocity time interval. */
	protected float velocityTimeInterval;
	
	/** The up axis. */
	protected int upAxis;

	/** The me. */
	protected CollisionObject me;

	/**
	 * Instantiates a new kinematic character controller.
	 *
	 * @param ghostObject the ghost object
	 * @param convexShape the convex shape
	 * @param stepHeight the step height
	 */
	public KinematicCharacterController(final PairCachingGhostObject ghostObject, final ConvexShape convexShape,
			final float stepHeight) {
		this(ghostObject, convexShape, stepHeight, 1);
	}

	/**
	 * Instantiates a new kinematic character controller.
	 *
	 * @param ghostObject the ghost object
	 * @param convexShape the convex shape
	 * @param stepHeight the step height
	 * @param upAxis the up axis
	 */
	public KinematicCharacterController(final PairCachingGhostObject ghostObject, final ConvexShape convexShape,
			final float stepHeight, final int upAxis) {
		this.upAxis = upAxis;
		this.addedMargin = 0.02f;
		this.walkDirection.set(0, 0, 0);
		this.useGhostObjectSweepTest = true;
		this.ghostObject = ghostObject;
		this.stepHeight = stepHeight;
		this.turnAngle = 0.0f;
		this.convexShape = convexShape;
		this.useWalkDirection = true;
		this.velocityTimeInterval = 0.0f;
		this.verticalVelocity = 0.0f;
		this.verticalOffset = 0.0f;
		this.gravity = 9.8f * 3; // 1G acceleration
		this.fallSpeed = 55.0f; // Terminal velocity of a sky diver in m/s.
		this.jumpSpeed = 10.0f; // ?
		this.wasOnGround = false;
		this.wasJumping = false;
		setMaxSlope((float) (45.0f / 180.0f * Math.PI));
	}

	/**
	 * Gets the ghost object.
	 *
	 * @return the ghost object
	 */
	private PairCachingGhostObject getGhostObject() {
		return ghostObject;
	}

	// ActionInterface interface
	@Override
	public void updateAction(final CollisionWorld collisionWorld, final float deltaTime) {
		preStep(collisionWorld);
		playerStep(collisionWorld, deltaTime);
	}

	// ActionInterface interface
	// @Override
	// public void debugDraw(final IDebugDraw debugDrawer) {}

	/**
	 * Sets the up axis.
	 *
	 * @param axis the new up axis
	 */
	public void setUpAxis(int axis) {
		if (axis < 0) { axis = 0; }
		if (axis > 2) { axis = 2; }
		upAxis = axis;
	}

	/**
	 * This should probably be called setPositionIncrementPerSimulatorStep. This is neither a direction nor a velocity,
	 * but the amount to increment the position each simulation iteration, regardless of dt.
	 * <p>
	 *
	 * This call will reset any velocity set by {@link #setVelocityForTimeInterval}.
	 */
	public void setWalkDirection(final Vector3f walkDirection) {
		useWalkDirection = true;
		this.walkDirection.set(walkDirection);
		normalizedDirection.set(getNormalizedVector(walkDirection, VECTORS.get()));
	}

	/**
	 * Caller provides a velocity with which the character should move for the given time period. After the time period,
	 * velocity is reset to zero. This call will reset any walk direction set by {@link #setWalkDirection}. Negative
	 * time intervals will result in no motion.
	 */
	public void setVelocityForTimeInterval(final Vector3f velocity, final float timeInterval) {
		useWalkDirection = false;
		walkDirection.set(velocity);
		normalizedDirection.set(getNormalizedVector(walkDirection, VECTORS.get()));
		velocityTimeInterval = timeInterval;
	}

	/**
	 * Reset.
	 */
	public void reset() {}

	/**
	 * Warp.
	 *
	 * @param origin the origin
	 */
	public void warp(final Vector3f origin) {
		Transform xform = TRANSFORMS.get();
		xform.setIdentity();
		xform.origin.set(origin);
		ghostObject.setWorldTransform(xform);
		TRANSFORMS.release(xform);
	}

	/**
	 * Pre step.
	 *
	 * @param collisionWorld the collision world
	 */
	public void preStep(final CollisionWorld collisionWorld) {
		int numPenetrationLoops = 0;
		touchingContact = false;
		while (recoverFromPenetration(collisionWorld)) {
			numPenetrationLoops++;
			touchingContact = true;
			if (numPenetrationLoops > 4) {
				// printf("character could not recover from penetration = %d\n", numPenetrationLoops);
				break;
			}
		}
		Transform xform = ghostObject.getWorldTransform(TRANSFORMS.get());
		currentPosition.set(xform.origin);
		targetPosition.set(currentPosition);
		TRANSFORMS.release(xform);

		// printf("m_targetPosition=%f,%f,%f\n",m_targetPosition[0],m_targetPosition[1],m_targetPosition[2]);
	}

	/**
	 * Player step.
	 *
	 * @param collisionWorld the collision world
	 * @param dt the dt
	 */
	public void playerStep(final CollisionWorld collisionWorld, final float dt) {
		// printf("playerStep(): ");
		// printf(" dt = %f", dt);

		// quick check...
		if (!useWalkDirection && velocityTimeInterval <= 0.0f) // printf("\n");
			return; // no motion

		wasOnGround = onGround();

		// Update fall velocity.
		verticalVelocity -= gravity * dt;
		if (verticalVelocity > 0.0 && verticalVelocity > jumpSpeed) { verticalVelocity = jumpSpeed; }
		if (verticalVelocity < 0.0 && Math.abs(verticalVelocity) > Math.abs(fallSpeed)) {
			verticalVelocity = -Math.abs(fallSpeed);
		}
		verticalOffset = verticalVelocity * dt;

		Transform xform = ghostObject.getWorldTransform(TRANSFORMS.get());

		// printf("walkDirection(%f,%f,%f)\n",walkDirection[0],walkDirection[1],walkDirection[2]);
		// printf("walkSpeed=%f\n",walkSpeed);

		stepUp(collisionWorld);
		if (useWalkDirection) {
			// System.out.println("playerStep 3");
			stepForwardAndStrafe(collisionWorld, walkDirection);
		} else {
			System.out.println("playerStep 4");
			// printf(" time: %f", m_velocityTimeInterval);

			// still have some time left for moving!
			float dtMoving = dt < velocityTimeInterval ? dt : velocityTimeInterval;
			velocityTimeInterval -= dt;

			// how far will we move while we are moving?
			Vector3f move = VECTORS.get();
			move.scale(dtMoving, walkDirection);

			// printf(" dtMoving: %f", dtMoving);

			// okay, step
			stepForwardAndStrafe(collisionWorld, move);
		}
		stepDown(collisionWorld, dt);

		// printf("\n");

		xform.origin.set(currentPosition);
		ghostObject.setWorldTransform(xform);
		TRANSFORMS.release(xform);
	}

	/**
	 * Sets the fall speed.
	 *
	 * @param fallSpeed the new fall speed
	 */
	public void setFallSpeed(final float fallSpeed) {
		this.fallSpeed = fallSpeed;
	}

	/**
	 * Sets the jump speed.
	 *
	 * @param jumpSpeed the new jump speed
	 */
	public void setJumpSpeed(final float jumpSpeed) {
		this.jumpSpeed = jumpSpeed;
	}

	/**
	 * Sets the max jump height.
	 *
	 * @param maxJumpHeight the new max jump height
	 */
	public void setMaxJumpHeight(final float maxJumpHeight) {
		this.maxJumpHeight = maxJumpHeight;
	}

	/**
	 * Can jump.
	 *
	 * @return true, if successful
	 */
	public boolean canJump() {
		return onGround();
	}

	/**
	 * Jump.
	 */
	public void jump() {
		if (!canJump()) return;

		verticalVelocity = jumpSpeed;
		wasJumping = true;

		// #if 0
		// currently no jumping.
		// btTransform xform;
		// m_rigidBody->getMotionState()->getWorldTransform (xform);
		// btVector3 up = xform.getBasis()[1];
		// up.normalize ();
		// btScalar magnitude = (btScalar(1.0)/m_rigidBody->getInvMass()) * btScalar(8.0);
		// m_rigidBody->applyCentralImpulse (up * magnitude);
		// #endif
	}

	/**
	 * Sets the gravity.
	 *
	 * @param gravity the new gravity
	 */
	public void setGravity(final float gravity) {
		this.gravity = gravity;
	}

	/**
	 * Gets the gravity.
	 *
	 * @return the gravity
	 */
	public float getGravity() {
		return gravity;
	}

	/**
	 * Sets the max slope.
	 *
	 * @param slopeRadians the new max slope
	 */
	public void setMaxSlope(final float slopeRadians) {
		maxSlopeRadians = slopeRadians;
		maxSlopeCosine = (float) Math.cos(slopeRadians);
	}

	/**
	 * Gets the max slope.
	 *
	 * @return the max slope
	 */
	public float getMaxSlope() {
		return maxSlopeRadians;
	}

	/**
	 * On ground.
	 *
	 * @return true, if successful
	 */
	public boolean onGround() {
		return verticalVelocity == 0.0f && verticalOffset == 0.0f;
	}

	/**
	 * Gets the normalized vector.
	 *
	 * @param v the v
	 * @param out the out
	 * @return the normalized vector
	 */
	// static helper method
	private static Vector3f getNormalizedVector(final Vector3f v, final Vector3f out) {
		out.set(v);
		out.normalize();
		if (out.length() < BulletGlobals.SIMD_EPSILON) { out.set(0, 0, 0); }
		return out;
	}

	/**
	 * Returns the reflection direction of a ray going 'direction' hitting a surface with normal 'normal'.
	 * <p>
	 *
	 * From: http://www-cs-students.stanford.edu/~adityagp/final/node3.html
	 */
	protected Vector3f computeReflectionDirection(final Vector3f direction, final Vector3f normal, final Vector3f out) {
		// return direction - (btScalar(2.0) * direction.dot(normal)) * normal;
		out.set(normal);
		out.scale(-2.0f * direction.dot(normal));
		out.add(direction);
		return out;
	}

	/**
	 * Returns the portion of 'direction' that is parallel to 'normal'
	 */
	protected Vector3f parallelComponent(final Vector3f direction, final Vector3f normal, final Vector3f out) {
		// btScalar magnitude = direction.dot(normal);
		// return normal * magnitude;
		out.set(normal);
		out.scale(direction.dot(normal));
		return out;
	}

	/**
	 * Returns the portion of 'direction' that is perpindicular to 'normal'
	 */
	protected Vector3f perpindicularComponent(final Vector3f direction, final Vector3f normal, final Vector3f out) {
		// return direction - parallelComponent(direction, normal);
		Vector3f perpendicular = parallelComponent(direction, normal, out);
		perpendicular.scale(-1);
		perpendicular.add(direction);
		return perpendicular;
	}

	/**
	 * Recover from penetration.
	 *
	 * @param collisionWorld the collision world
	 * @return true, if successful
	 */
	protected boolean recoverFromPenetration(final CollisionWorld collisionWorld) {
		boolean penetration = false;

		collisionWorld.getDispatcher().dispatchAllCollisionPairs(ghostObject.getOverlappingPairCache(),
				collisionWorld.getDispatchInfo(), collisionWorld.getDispatcher());
		Transform xform = ghostObject.getWorldTransform(TRANSFORMS.get());
		currentPosition.set(xform.origin);

		float maxPen = 0.0f;
		for (int i = 0; i < ghostObject.getOverlappingPairCache().getNumOverlappingPairs(); i++) {
			manifoldArray.clear();

			BroadphasePair collisionPair = ghostObject.getOverlappingPairCache().getOverlappingPairArray().get(i);
			// XXX: added no contact response
			if (!((CollisionObject) collisionPair.pProxy0.clientObject).hasContactResponse()
					|| !((CollisionObject) collisionPair.pProxy1.clientObject).hasContactResponse()) {
				continue;
			}
			if (collisionPair.algorithm != null) { collisionPair.algorithm.getAllContactManifolds(manifoldArray); }

			for (PersistentManifold manifold : manifoldArray) {
				float directionSign = manifold.getBody0() == ghostObject ? -1.0f : 1.0f;
				for (int p = 0; p < manifold.getNumContacts(); p++) {
					ManifoldPoint pt = manifold.getContactPoint(p);

					float dist = pt.getDistance();
					if (dist < 0.0f) {
						if (dist < maxPen) {
							maxPen = dist;
							touchingNormal.set(pt.normalWorldOnB);// ??
							touchingNormal.scale(directionSign);
						}

						currentPosition.scaleAdd(directionSign * dist * 0.2f, pt.normalWorldOnB, currentPosition);

						penetration = true;
					} else {
						// printf("touching %f\n", dist);
					}
				}

				// manifold->clearManifold();
			}
		}

		Transform newTrans = ghostObject.getWorldTransform(TRANSFORMS.get());
		newTrans.origin.set(currentPosition);
		ghostObject.setWorldTransform(newTrans);
		// printf("m_touchingNormal = %f,%f,%f\n",m_touchingNormal[0],m_touchingNormal[1],m_touchingNormal[2]);

		// System.out.println("recoverFromPenetration "+penetration+" "+touchingNormal);
		TRANSFORMS.release(xform, newTrans);
		return penetration;
	}

	/**
	 * Step up.
	 *
	 * @param world the world
	 */
	protected void stepUp(final CollisionWorld world) {
		// phase 1: up
		Transform start = TRANSFORMS.get();
		Transform end = TRANSFORMS.get();
		targetPosition.scaleAdd(stepHeight + (verticalOffset > 0.0 ? verticalOffset : 0.0f), upAxisDirection[upAxis],
				currentPosition);

		start.setIdentity();
		end.setIdentity();

		/* FIXME: Handle penetration properly */
		start.origin.scaleAdd(convexShape.getMargin() + addedMargin, upAxisDirection[upAxis], currentPosition);
		end.origin.set(targetPosition);

		// Find only sloped/flat surface hits, avoid wall and ceiling hits...
		Vector3f up = VECTORS.get();
		up.scale(-1f, upAxisDirection[upAxis]);
		KinematicClosestNotMeConvexResultCallback callback =
				new KinematicClosestNotMeConvexResultCallback(ghostObject, up, 0.7071f);
		callback.collisionFilterGroup = getGhostObject().getBroadphaseHandle().collisionFilterGroup;
		callback.collisionFilterMask = getGhostObject().getBroadphaseHandle().collisionFilterMask;

		if (useGhostObjectSweepTest) {
			ghostObject.convexSweepTest(convexShape, start, end, callback,
					world.getDispatchInfo().allowedCcdPenetration);
		} else {
			world.convexSweepTest(convexShape, start, end, callback);
		}

		if (callback.hasHit()) {
			// Only modify the position if the hit was a slope and not a wall or ceiling.
			if (callback.hitNormalWorld.dot(upAxisDirection[upAxis]) > 0.0) {
				// we moved up only a fraction of the step height
				currentStepOffset = stepHeight * callback.closestHitFraction;
				currentPosition.interpolate(currentPosition, targetPosition, callback.closestHitFraction);
				verticalVelocity = 0.0f;
				verticalOffset = 0.0f;
			}
		} else {
			currentStepOffset = stepHeight;
			currentPosition.set(targetPosition);
		}
		TRANSFORMS.release(start, end);
		VECTORS.release(up);
	}

	/**
	 * Update target position based on collision.
	 *
	 * @param hitNormal the hit normal
	 */
	protected void updateTargetPositionBasedOnCollision(final Vector3f hitNormal) {
		updateTargetPositionBasedOnCollision(hitNormal, 0f, 1f);
	}

	/**
	 * Update target position based on collision.
	 *
	 * @param hitNormal the hit normal
	 * @param tangentMag the tangent mag
	 * @param normalMag the normal mag
	 */
	protected void updateTargetPositionBasedOnCollision(final Vector3f hitNormal, final float tangentMag,
			final float normalMag) {
		Vector3f movementDirection = VECTORS.get();
		movementDirection.sub(targetPosition, currentPosition);
		float movementLength = movementDirection.length();
		if (movementLength > BulletGlobals.SIMD_EPSILON) {
			movementDirection.normalize();

			Vector3f reflectDir = computeReflectionDirection(movementDirection, hitNormal, VECTORS.get());
			reflectDir.normalize();

			Vector3f parallelDir = parallelComponent(reflectDir, hitNormal, VECTORS.get());
			Vector3f perpindicularDir = perpindicularComponent(reflectDir, hitNormal, VECTORS.get());

			targetPosition.set(currentPosition);
			if (false) // tangentMag != 0.0)
			{
				Vector3f parComponent = VECTORS.get();
				parComponent.scale(tangentMag * movementLength, parallelDir);
				// printf("parComponent=%f,%f,%f\n",parComponent[0],parComponent[1],parComponent[2]);
				targetPosition.add(parComponent);
				VECTORS.release(parComponent);
			}

			if (normalMag != 0.0f) {
				Vector3f perpComponent = VECTORS.get();
				perpComponent.scale(normalMag * movementLength, perpindicularDir);
				// printf("perpComponent=%f,%f,%f\n",perpComponent[0],perpComponent[1],perpComponent[2]);
				targetPosition.add(perpComponent);
				VECTORS.release(perpComponent);
			}
		} else {
			// printf("movementLength don't normalize a zero vector\n");
		}
		VECTORS.release(movementDirection);
	}

	/**
	 * Step forward and strafe.
	 *
	 * @param world the world
	 * @param walkMove the walk move
	 */
	protected void stepForwardAndStrafe(final CollisionWorld world, final Vector3f walkMove) {
		// printf("m_normalizedDirection=%f,%f,%f\n",
		// m_normalizedDirection[0],m_normalizedDirection[1],m_normalizedDirection[2]);
		// phase 2: forward and strafe
		Transform start = TRANSFORMS.get();
		Transform end = TRANSFORMS.get();
		targetPosition.add(currentPosition, walkMove);
		start.setIdentity();
		end.setIdentity();

		float fraction = 1.0f;
		Vector3f distance2Vec = VECTORS.get();
		distance2Vec.sub(currentPosition, targetPosition);
		float distance2 = distance2Vec.lengthSquared();
		// printf("distance2=%f\n",distance2);

		if (touchingContact) {
			if (normalizedDirection.dot(touchingNormal) > 0.0f) {
				updateTargetPositionBasedOnCollision(touchingNormal);
			}
		}

		int maxIter = 10;
		Vector3f hitDistanceVec = VECTORS.get();
		Vector3f currentDir = VECTORS.get();
		while (fraction > 0.01f && maxIter-- > 0) {
			start.origin.set(currentPosition);
			end.origin.set(targetPosition);
			Vector3f sweepDirNegative = VECTORS.get();
			sweepDirNegative.sub(currentPosition, targetPosition);

			KinematicClosestNotMeConvexResultCallback callback =
					new KinematicClosestNotMeConvexResultCallback(ghostObject, sweepDirNegative, -1.0f);

			callback.collisionFilterGroup = getGhostObject().getBroadphaseHandle().collisionFilterGroup;
			callback.collisionFilterMask = getGhostObject().getBroadphaseHandle().collisionFilterMask;

			float margin = convexShape.getMargin();
			convexShape.setMargin(margin + addedMargin);

			if (useGhostObjectSweepTest) {
				ghostObject.convexSweepTest(convexShape, start, end, callback,
						world.getDispatchInfo().allowedCcdPenetration);
			} else {
				world.convexSweepTest(convexShape, start, end, callback);
			}

			convexShape.setMargin(margin);

			fraction -= callback.closestHitFraction;

			if (callback.hasHit()) {
				// we moved only a fraction

				hitDistanceVec.sub(callback.hitPointWorld, currentPosition);
				// float hitDistance = hitDistanceVec.length();

				// if the distance is farther than the collision margin, move
				// if (hitDistance > addedMargin) {
				// //printf("callback.m_closestHitFraction=%f\n",callback.m_closestHitFraction);
				// currentPosition.interpolate(currentPosition, targetPosition, callback.closestHitFraction);
				// }

				updateTargetPositionBasedOnCollision(callback.hitNormalWorld);

				currentDir.sub(targetPosition, currentPosition);
				distance2 = currentDir.lengthSquared();
				if (distance2 > BulletGlobals.SIMD_EPSILON) {
					currentDir.normalize();
					// see Quake2: "If velocity is against original velocity, stop ead to avoid tiny oscilations in
					// sloping corners."
					if (currentDir.dot(normalizedDirection) <= 0.0f) { break; }
				} else {
					// printf("currentDir: don't normalize a zero vector\n");
					break;
				}
			} else {
				// we moved whole way
				currentPosition.set(targetPosition);
			}

			// if (callback.m_closestHitFraction == 0.f)
			// break;
		}
		TRANSFORMS.release(start, end);
		VECTORS.release(currentDir, hitDistanceVec);
	}

	/**
	 * Step down.
	 *
	 * @param collisionWorld the collision world
	 * @param dt the dt
	 */
	protected void stepDown(final CollisionWorld collisionWorld, final float dt) {
		Transform start = TRANSFORMS.get();
		Transform end = TRANSFORMS.get();

		// phase 3: down
		// float additionalDownStep = (wasOnGround /*&& !onGround()*/) ? stepHeight : 0.0f;
		// Vector3f step_drop = VECTORS.get();;
		// step_drop.scale(currentStepOffset + additionalDownStep, upAxisDirection[upAxis]);
		// float downVelocity = (additionalDownStep == 0.0f && verticalVelocity<0.0f?-verticalVelocity:0.0f) * dt;
		// Vector3f gravity_drop = VECTORS.get();;
		// gravity_drop.scale(downVelocity, upAxisDirection[upAxis]);
		// targetPosition.sub(step_drop);
		// targetPosition.sub(gravity_drop);

		float downVelocity = (verticalVelocity < 0.0f ? -verticalVelocity : 0.0f) * dt;
		if (downVelocity > 0.0 && downVelocity < stepHeight && (wasOnGround || !wasJumping)) {
			downVelocity = stepHeight;
		}
		Vector3f step_drop = VECTORS.get();
		step_drop.scale(currentStepOffset + downVelocity, upAxisDirection[upAxis]);
		targetPosition.sub(step_drop);

		start.setIdentity();
		end.setIdentity();

		start.origin.set(currentPosition);
		end.origin.set(targetPosition);

		KinematicClosestNotMeConvexResultCallback callback =
				new KinematicClosestNotMeConvexResultCallback(ghostObject, upAxisDirection[upAxis], maxSlopeCosine);
		callback.collisionFilterGroup = getGhostObject().getBroadphaseHandle().collisionFilterGroup;
		callback.collisionFilterMask = getGhostObject().getBroadphaseHandle().collisionFilterMask;

		if (useGhostObjectSweepTest) {
			ghostObject.convexSweepTest(convexShape, start, end, callback,
					collisionWorld.getDispatchInfo().allowedCcdPenetration);
		} else {
			collisionWorld.convexSweepTest(convexShape, start, end, callback);
		}

		if (callback.hasHit()) {
			// we dropped a fraction of the height -> hit floor
			currentPosition.interpolate(currentPosition, targetPosition, callback.closestHitFraction);
			verticalVelocity = 0.0f;
			verticalOffset = 0.0f;
			wasJumping = false;
		} else {
			// we dropped the full height
			currentPosition.set(targetPosition);
		}
		TRANSFORMS.release(start, end);
		VECTORS.release(step_drop);
	}

	////////////////////////////////////////////////////////////////////////////

	// private static class KinematicClosestNotMeRayResultCallback extends CollisionWorld.ClosestRayResultCallback {
	// protected CollisionObject me;
	//
	// public KinematicClosestNotMeRayResultCallback(final CollisionObject me) {
	// super(new Vector3f(), new Vector3f());
	// this.me = me;
	// }
	//
	// @Override
	// public float addSingleResult(final CollisionWorld.LocalRayResult rayResult, final boolean normalInWorldSpace) {
	// if (rayResult.collisionObject == me) return 1.0f;
	//
	// return super.addSingleResult(rayResult, normalInWorldSpace);
	// }
	// }

	////////////////////////////////////////////////////////////////////////////

	/**
	 * The Class KinematicClosestNotMeConvexResultCallback.
	 */
	private static class KinematicClosestNotMeConvexResultCallback extends CollisionWorld.ClosestConvexResultCallback {
		
		/** The me. */
		protected CollisionObject me;
		
		/** The up. */
		protected final Vector3f up;
		
		/** The min slope dot. */
		protected float minSlopeDot;

		/**
		 * Instantiates a new kinematic closest not me convex result callback.
		 *
		 * @param me the me
		 * @param up the up
		 * @param minSlopeDot the min slope dot
		 */
		public KinematicClosestNotMeConvexResultCallback(final CollisionObject me, final Vector3f up,
				final float minSlopeDot) {
			super(new Vector3f(), new Vector3f());
			this.me = me;
			this.up = up;
			this.minSlopeDot = minSlopeDot;
		}

		@Override
		public float addSingleResult(final CollisionWorld.LocalConvexResult convexResult,
				final boolean normalInWorldSpace) {
			// XXX: no contact response
			if (!convexResult.hitCollisionObject.hasContactResponse()) return 1.0f;
			if (convexResult.hitCollisionObject == me) return 1.0f;

			float dotUp;
			if (normalInWorldSpace) {
				Vector3f hitNormalWorld = convexResult.hitNormalLocal;
				dotUp = up.dot(hitNormalWorld);
			} else {
				// need to transform normal into worldspace
				Vector3f hitNormalWorld = VECTORS.get();
				Transform tmp = convexResult.hitCollisionObject.getWorldTransform(TRANSFORMS.get());
				tmp.basis.transform(convexResult.hitNormalLocal, hitNormalWorld);
				dotUp = up.dot(hitNormalWorld);
				VECTORS.release(hitNormalWorld);
				TRANSFORMS.release(tmp);
			}

			if (dotUp < minSlopeDot) return 1.0f;

			return super.addSingleResult(convexResult, normalInWorldSpace);
		}
	}

}
