/*******************************************************************************************************
 *
 * RaycastVehicle.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.dynamics.vehicle;

import static com.bulletphysics.Pools.MATRICES;
import static com.bulletphysics.Pools.QUATS;
import static com.bulletphysics.Pools.TRANSFORMS;
import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Matrix3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ContactConstraint;
import com.bulletphysics.dynamics.constraintsolver.TypedConstraint;
import com.bulletphysics.dynamics.constraintsolver.TypedConstraintType;
import com.bulletphysics.linearmath.MatrixUtil;
import com.bulletphysics.linearmath.MiscUtil;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ArrayPool;
import com.bulletphysics.util.FloatArrayList;
import java.util.ArrayList;

/**
 * Raycast vehicle, very special constraint that turn a rigidbody into a vehicle.
 *
 * @author jezek2
 */
public class RaycastVehicle extends TypedConstraint {

	/** The float arrays. */
	private final ArrayPool<float[]> floatArrays = ArrayPool.get(float.class);

	/** The s fixed object. */
	private static RigidBody s_fixedObject = new RigidBody(0, null, null);
	
	/** The Constant sideFrictionStiffness2. */
	private static final float sideFrictionStiffness2 = 1.0f;

	/** The forward WS. */
	protected ArrayList<Vector3f> forwardWS = new ArrayList<>();
	
	/** The axle. */
	protected ArrayList<Vector3f> axle = new ArrayList<>();
	
	/** The forward impulse. */
	protected FloatArrayList forwardImpulse = new FloatArrayList();
	
	/** The side impulse. */
	protected FloatArrayList sideImpulse = new FloatArrayList();

	/** The tau. */
	private float tau;
	
	/** The damping. */
	private float damping;
	
	/** The vehicle raycaster. */
	private final VehicleRaycaster vehicleRaycaster;
	
	/** The pitch control. */
	private float pitchControl = 0f;
	
	/** The steering value. */
	private float steeringValue;
	
	/** The current vehicle speed km hour. */
	private float currentVehicleSpeedKmHour;

	/** The chassis body. */
	private final RigidBody chassisBody;

	/** The index right axis. */
	private int indexRightAxis = 0;
	
	/** The index up axis. */
	private int indexUpAxis = 2;
	
	/** The index forward axis. */
	private int indexForwardAxis = 1;

	/** The wheel info. */
	public ArrayList<WheelInfo> wheelInfo = new ArrayList<>();

	/**
	 * Instantiates a new raycast vehicle.
	 *
	 * @param tuning the tuning
	 * @param chassis the chassis
	 * @param raycaster the raycaster
	 */
	// constructor to create a car from an existing rigidbody
	public RaycastVehicle(final VehicleTuning tuning, final RigidBody chassis, final VehicleRaycaster raycaster) {
		super(TypedConstraintType.VEHICLE_CONSTRAINT_TYPE);
		this.vehicleRaycaster = raycaster;
		this.chassisBody = chassis;
		defaultInit(tuning);
	}

	/**
	 * Default init.
	 *
	 * @param tuning the tuning
	 */
	private void defaultInit(final VehicleTuning tuning) {
		currentVehicleSpeedKmHour = 0f;
		steeringValue = 0f;
	}

	/**
	 * Basically most of the code is general for 2 or 4 wheel vehicles, but some of it needs to be reviewed.
	 */
	public WheelInfo addWheel(final Vector3f connectionPointCS, final Vector3f wheelDirectionCS0,
			final Vector3f wheelAxleCS, final float suspensionRestLength, final float wheelRadius,
			final VehicleTuning tuning, final boolean isFrontWheel) {
		WheelInfoConstructionInfo ci = new WheelInfoConstructionInfo();

		ci.chassisConnectionCS.set(connectionPointCS);
		ci.wheelDirectionCS.set(wheelDirectionCS0);
		ci.wheelAxleCS.set(wheelAxleCS);
		ci.suspensionRestLength = suspensionRestLength;
		ci.wheelRadius = wheelRadius;
		ci.suspensionStiffness = tuning.suspensionStiffness;
		ci.wheelsDampingCompression = tuning.suspensionCompression;
		ci.wheelsDampingRelaxation = tuning.suspensionDamping;
		ci.frictionSlip = tuning.frictionSlip;
		ci.bIsFrontWheel = isFrontWheel;
		ci.maxSuspensionTravelCm = tuning.maxSuspensionTravelCm;

		wheelInfo.add(new WheelInfo(ci));

		WheelInfo wheel = wheelInfo.get(getNumWheels() - 1);

		updateWheelTransformsWS(wheel, false);
		updateWheelTransform(getNumWheels() - 1, false);
		return wheel;
	}

	/**
	 * Gets the wheel transform WS.
	 *
	 * @param wheelIndex the wheel index
	 * @param out the out
	 * @return the wheel transform WS
	 */
	public Transform getWheelTransformWS(final int wheelIndex, final Transform out) {
		assert wheelIndex < getNumWheels();
		WheelInfo wheel = wheelInfo.get(wheelIndex);
		out.set(wheel.worldTransform);
		return out;
	}

	/**
	 * Update wheel transform.
	 *
	 * @param wheelIndex the wheel index
	 */
	public void updateWheelTransform(final int wheelIndex) {
		updateWheelTransform(wheelIndex, true);
	}

	/**
	 * Update wheel transform.
	 *
	 * @param wheelIndex the wheel index
	 * @param interpolatedTransform the interpolated transform
	 */
	public void updateWheelTransform(final int wheelIndex, final boolean interpolatedTransform) {
		WheelInfo wheel = wheelInfo.get(wheelIndex);
		updateWheelTransformsWS(wheel, interpolatedTransform);
		Vector3f up = VECTORS.get();
		up.negate(wheel.raycastInfo.wheelDirectionWS);
		Vector3f right = wheel.raycastInfo.wheelAxleWS;
		Vector3f fwd = VECTORS.get();
		fwd.cross(up, right);
		fwd.normalize();
		// up = right.cross(fwd);
		// up.normalize();

		// rotate around steering over de wheelAxleWS
		float steering = wheel.steering;

		Quat4f steeringOrn = QUATS.get();
		QuaternionUtil.setRotation(steeringOrn, up, steering); // wheel.m_steering);
		Matrix3f steeringMat = MATRICES.get();
		MatrixUtil.setRotation(steeringMat, steeringOrn);

		Quat4f rotatingOrn = QUATS.get();
		QuaternionUtil.setRotation(rotatingOrn, right, -wheel.rotation);
		Matrix3f rotatingMat = MATRICES.get();
		MatrixUtil.setRotation(rotatingMat, rotatingOrn);

		Matrix3f basis2 = MATRICES.get();
		basis2.setRow(0, right.x, fwd.x, up.x);
		basis2.setRow(1, right.y, fwd.y, up.y);
		basis2.setRow(2, right.z, fwd.z, up.z);

		Matrix3f wheelBasis = wheel.worldTransform.basis;
		wheelBasis.mul(steeringMat, rotatingMat);
		wheelBasis.mul(basis2);

		wheel.worldTransform.origin.scaleAdd(wheel.raycastInfo.suspensionLength, wheel.raycastInfo.wheelDirectionWS,
				wheel.raycastInfo.hardPointWS);
		QUATS.release(rotatingOrn, steeringOrn);
		VECTORS.release(up, fwd);
		MATRICES.release(basis2);
	}

	/**
	 * Reset suspension.
	 */
	public void resetSuspension() {
		int i;
		for (i = 0; i < wheelInfo.size(); i++) {
			WheelInfo wheel = wheelInfo.get(i);
			wheel.raycastInfo.suspensionLength = wheel.getSuspensionRestLength();
			wheel.suspensionRelativeVelocity = 0f;

			wheel.raycastInfo.contactNormalWS.negate(wheel.raycastInfo.wheelDirectionWS);
			// wheel_info.setContactFriction(btScalar(0.0));
			wheel.clippedInvContactDotSuspension = 1f;
		}
	}

	/**
	 * Update wheel transforms WS.
	 *
	 * @param wheel the wheel
	 */
	public void updateWheelTransformsWS(final WheelInfo wheel) {
		updateWheelTransformsWS(wheel, true);
	}

	/**
	 * Update wheel transforms WS.
	 *
	 * @param wheel the wheel
	 * @param interpolatedTransform the interpolated transform
	 */
	public void updateWheelTransformsWS(final WheelInfo wheel, final boolean interpolatedTransform) {
		wheel.raycastInfo.isInContact = false;

		Transform chassisTrans = getChassisWorldTransform(TRANSFORMS.get());
		if (interpolatedTransform && getRigidBody().getMotionState() != null) {
			getRigidBody().getMotionState().getWorldTransform(chassisTrans);
		}

		wheel.raycastInfo.hardPointWS.set(wheel.chassisConnectionPointCS);
		chassisTrans.transform(wheel.raycastInfo.hardPointWS);

		wheel.raycastInfo.wheelDirectionWS.set(wheel.wheelDirectionCS);
		chassisTrans.basis.transform(wheel.raycastInfo.wheelDirectionWS);

		wheel.raycastInfo.wheelAxleWS.set(wheel.wheelAxleCS);
		chassisTrans.basis.transform(wheel.raycastInfo.wheelAxleWS);
		TRANSFORMS.release(chassisTrans);
	}

	/**
	 * Ray cast.
	 *
	 * @param wheel the wheel
	 * @return the float
	 */
	public float rayCast(final WheelInfo wheel) {
		updateWheelTransformsWS(wheel, false);

		float depth = -1f;

		float raylen = wheel.getSuspensionRestLength() + wheel.wheelsRadius;

		Vector3f rayvector = VECTORS.get();
		rayvector.scale(raylen, wheel.raycastInfo.wheelDirectionWS);
		Vector3f source = wheel.raycastInfo.hardPointWS;
		wheel.raycastInfo.contactPointWS.add(source, rayvector);
		Vector3f target = wheel.raycastInfo.contactPointWS;

		float param = 0f;

		VehicleRaycasterResult rayResults = new VehicleRaycasterResult();

		assert vehicleRaycaster != null;

		Object object = vehicleRaycaster.castRay(source, target, rayResults);

		wheel.raycastInfo.groundObject = null;

		if (object != null) {
			param = rayResults.distFraction;
			depth = raylen * rayResults.distFraction;
			wheel.raycastInfo.contactNormalWS.set(rayResults.hitNormalInWorld);
			wheel.raycastInfo.isInContact = true;

			wheel.raycastInfo.groundObject = s_fixedObject; // todo for driving on dynamic/movable objects!;
			// wheel.m_raycastInfo.m_groundObject = object;

			float hitDistance = param * raylen;
			wheel.raycastInfo.suspensionLength = hitDistance - wheel.wheelsRadius;
			// clamp on max suspension travel

			float minSuspensionLength = wheel.getSuspensionRestLength() - wheel.maxSuspensionTravelCm * 0.01f;
			float maxSuspensionLength = wheel.getSuspensionRestLength() + wheel.maxSuspensionTravelCm * 0.01f;
			if (wheel.raycastInfo.suspensionLength < minSuspensionLength) {
				wheel.raycastInfo.suspensionLength = minSuspensionLength;
			}
			if (wheel.raycastInfo.suspensionLength > maxSuspensionLength) {
				wheel.raycastInfo.suspensionLength = maxSuspensionLength;
			}

			wheel.raycastInfo.contactPointWS.set(rayResults.hitPointInWorld);

			float denominator = wheel.raycastInfo.contactNormalWS.dot(wheel.raycastInfo.wheelDirectionWS);

			Vector3f chassis_velocity_at_contactPoint = VECTORS.get();
			Vector3f relpos = VECTORS.get();
			relpos.sub(wheel.raycastInfo.contactPointWS, getRigidBody().getCenterOfMassPosition(VECTORS.get()));

			getRigidBody().getVelocityInLocalPoint(relpos, chassis_velocity_at_contactPoint);

			float projVel = wheel.raycastInfo.contactNormalWS.dot(chassis_velocity_at_contactPoint);

			if (denominator >= -0.1f) {
				wheel.suspensionRelativeVelocity = 0f;
				wheel.clippedInvContactDotSuspension = 1f / 0.1f;
			} else {
				float inv = -1f / denominator;
				wheel.suspensionRelativeVelocity = projVel * inv;
				wheel.clippedInvContactDotSuspension = inv;
			}

		} else {
			// put wheel info as in rest position
			wheel.raycastInfo.suspensionLength = wheel.getSuspensionRestLength();
			wheel.suspensionRelativeVelocity = 0f;
			wheel.raycastInfo.contactNormalWS.negate(wheel.raycastInfo.wheelDirectionWS);
			wheel.clippedInvContactDotSuspension = 1f;
		}

		return depth;
	}

	/**
	 * Gets the chassis world transform.
	 *
	 * @param out the out
	 * @return the chassis world transform
	 */
	public Transform getChassisWorldTransform(final Transform out) {
		/*
		 * if (getRigidBody()->getMotionState()) { btTransform chassisWorldTrans;
		 * getRigidBody()->getMotionState()->getWorldTransform(chassisWorldTrans); return chassisWorldTrans; }
		 */

		return getRigidBody().getCenterOfMassTransform(out);
	}

	/**
	 * Update vehicle.
	 *
	 * @param step the step
	 */
	public void updateVehicle(final float step) {
		for (int i = 0; i < getNumWheels(); i++) {
			updateWheelTransform(i, false);
		}

		Vector3f tmp = VECTORS.get();

		currentVehicleSpeedKmHour = 3.6f * getRigidBody().getLinearVelocity(tmp).length();

		Transform chassisTrans = getChassisWorldTransform(TRANSFORMS.get());

		Vector3f forwardW = VECTORS.get();
		forwardW.set(chassisTrans.basis.getElement(0, indexForwardAxis),
				chassisTrans.basis.getElement(1, indexForwardAxis), chassisTrans.basis.getElement(2, indexForwardAxis));

		if (forwardW.dot(getRigidBody().getLinearVelocity(tmp)) < 0f) { currentVehicleSpeedKmHour *= -1f; }

		//
		// simulate suspension
		//

		// int i = 0;
		// for (i = 0; i < wheelInfo.size(); i++) {
		// float depth;
		// depth = rayCast(wheelInfo.get(i));
		// }

		updateSuspension(step);

		for (int i = 0; i < wheelInfo.size(); i++) {
			// apply suspension force
			WheelInfo wheel = wheelInfo.get(i);

			float suspensionForce = wheel.wheelsSuspensionForce;

			if (suspensionForce > wheel.maxSuspensionForce) { suspensionForce = wheel.maxSuspensionForce; }
			Vector3f impulse = VECTORS.get();
			impulse.scale(suspensionForce * step, wheel.raycastInfo.contactNormalWS);
			Vector3f relpos = VECTORS.get();
			relpos.sub(wheel.raycastInfo.contactPointWS, getRigidBody().getCenterOfMassPosition(tmp));

			getRigidBody().applyImpulse(impulse, relpos);
		}

		updateFriction(step);

		for (int i = 0; i < wheelInfo.size(); i++) {
			WheelInfo wheel = wheelInfo.get(i);
			Vector3f relpos = VECTORS.get();
			relpos.sub(wheel.raycastInfo.hardPointWS, getRigidBody().getCenterOfMassPosition(tmp));
			Vector3f vel = getRigidBody().getVelocityInLocalPoint(relpos, VECTORS.get());

			if (wheel.raycastInfo.isInContact) {
				Transform chassisWorldTransform = getChassisWorldTransform(TRANSFORMS.get());

				Vector3f fwd = VECTORS.get();
				fwd.set(chassisWorldTransform.basis.getElement(0, indexForwardAxis),
						chassisWorldTransform.basis.getElement(1, indexForwardAxis),
						chassisWorldTransform.basis.getElement(2, indexForwardAxis));

				float proj = fwd.dot(wheel.raycastInfo.contactNormalWS);
				tmp.scale(proj, wheel.raycastInfo.contactNormalWS);
				fwd.sub(tmp);

				float proj2 = fwd.dot(vel);

				wheel.deltaRotation = proj2 * step / wheel.wheelsRadius;
				wheel.rotation += wheel.deltaRotation;
				TRANSFORMS.release(chassisWorldTransform);
			} else {
				wheel.rotation += wheel.deltaRotation;
			}

			wheel.deltaRotation *= 0.99f; // damping of rotation when not in contact
		}
		TRANSFORMS.release(chassisTrans);
	}

	/**
	 * Sets the steering value.
	 *
	 * @param steering the steering
	 * @param wheel the wheel
	 */
	public void setSteeringValue(final float steering, final int wheel) {
		assert wheel >= 0 && wheel < getNumWheels();

		WheelInfo wheel_info = getWheelInfo(wheel);
		wheel_info.steering = steering;
	}

	/**
	 * Gets the steering value.
	 *
	 * @param wheel the wheel
	 * @return the steering value
	 */
	public float getSteeringValue(final int wheel) {
		return getWheelInfo(wheel).steering;
	}

	/**
	 * Apply engine force.
	 *
	 * @param force the force
	 * @param wheel the wheel
	 */
	public void applyEngineForce(final float force, final int wheel) {
		assert wheel >= 0 && wheel < getNumWheels();
		WheelInfo wheel_info = getWheelInfo(wheel);
		wheel_info.engineForce = force;
	}

	/**
	 * Gets the wheel info.
	 *
	 * @param index the index
	 * @return the wheel info
	 */
	public WheelInfo getWheelInfo(final int index) {
		assert index >= 0 && index < getNumWheels();

		return wheelInfo.get(index);
	}

	/**
	 * Sets the brake.
	 *
	 * @param brake the brake
	 * @param wheelIndex the wheel index
	 */
	public void setBrake(final float brake, final int wheelIndex) {
		assert wheelIndex >= 0 && wheelIndex < getNumWheels();
		getWheelInfo(wheelIndex).brake = brake;
	}

	/**
	 * Update suspension.
	 *
	 * @param deltaTime the delta time
	 */
	public void updateSuspension(final float deltaTime) {
		float chassisMass = 1f / chassisBody.getInvMass();

		for (int w_it = 0; w_it < getNumWheels(); w_it++) {
			WheelInfo wheel_info = wheelInfo.get(w_it);

			if (wheel_info.raycastInfo.isInContact) {
				float force;
				// Spring
				{
					float susp_length = wheel_info.getSuspensionRestLength();
					float current_length = wheel_info.raycastInfo.suspensionLength;

					float length_diff = susp_length - current_length;

					force = wheel_info.suspensionStiffness * length_diff * wheel_info.clippedInvContactDotSuspension;
				}

				// Damper
				{
					float projected_rel_vel = wheel_info.suspensionRelativeVelocity;
					{
						float susp_damping;
						if (projected_rel_vel < 0f) {
							susp_damping = wheel_info.wheelsDampingCompression;
						} else {
							susp_damping = wheel_info.wheelsDampingRelaxation;
						}
						force -= susp_damping * projected_rel_vel;
					}
				}

				// RESULT
				wheel_info.wheelsSuspensionForce = force * chassisMass;
				if (wheel_info.wheelsSuspensionForce < 0f) { wheel_info.wheelsSuspensionForce = 0f; }
			} else {
				wheel_info.wheelsSuspensionForce = 0f;
			}
		}
	}

	/**
	 * Calc rolling friction.
	 *
	 * @param contactPoint the contact point
	 * @return the float
	 */
	private float calcRollingFriction(final WheelContactPoint contactPoint) {
		Vector3f tmp = VECTORS.get();

		float j1 = 0f;

		Vector3f contactPosWorld = contactPoint.frictionPositionWorld;

		Vector3f rel_pos1 = VECTORS.get();
		rel_pos1.sub(contactPosWorld, contactPoint.body0.getCenterOfMassPosition(tmp));
		Vector3f rel_pos2 = VECTORS.get();
		rel_pos2.sub(contactPosWorld, contactPoint.body1.getCenterOfMassPosition(tmp));

		float maxImpulse = contactPoint.maxImpulse;

		Vector3f vel1 = contactPoint.body0.getVelocityInLocalPoint(rel_pos1, VECTORS.get());
		Vector3f vel2 = contactPoint.body1.getVelocityInLocalPoint(rel_pos2, VECTORS.get());
		Vector3f vel = VECTORS.get();
		vel.sub(vel1, vel2);

		float vrel = contactPoint.frictionDirectionWorld.dot(vel);

		// calculate j that moves us to zero relative velocity
		j1 = -vrel * contactPoint.jacDiagABInv;
		j1 = Math.min(j1, maxImpulse);
		j1 = Math.max(j1, -maxImpulse);

		return j1;
	}

	/**
	 * Update friction.
	 *
	 * @param timeStep the time step
	 */
	public void updateFriction(final float timeStep) {
		// calculate the impulse, so that the wheels don't move sidewards
		int numWheel = getNumWheels();
		if (numWheel == 0) return;

		MiscUtil.resize(forwardWS, numWheel, Vector3f.class);
		MiscUtil.resize(axle, numWheel, Vector3f.class);
		MiscUtil.resize(forwardImpulse, numWheel, 0f);
		MiscUtil.resize(sideImpulse, numWheel, 0f);

		Vector3f tmp = VECTORS.get();

		// int numWheelsOnGround = 0;

		// collapse all those loops into one!
		for (int i = 0; i < getNumWheels(); i++) {
			// WheelInfo wheel_info = wheelInfo.get(i);
			// RigidBody groundObject = (RigidBody) wheel_info.raycastInfo.groundObject;
			// if (groundObject != null) { numWheelsOnGround++; }
			sideImpulse.set(i, 0f);
			forwardImpulse.set(i, 0f);
		}

		{
			Transform wheelTrans = TRANSFORMS.get();
			for (int i = 0; i < getNumWheels(); i++) {

				WheelInfo wheel_info = wheelInfo.get(i);

				RigidBody groundObject = (RigidBody) wheel_info.raycastInfo.groundObject;

				if (groundObject != null) {
					getWheelTransformWS(i, wheelTrans);

					Matrix3f wheelBasis0 = MATRICES.get(wheelTrans.basis);
					axle.get(i).set(wheelBasis0.getElement(0, indexRightAxis),
							wheelBasis0.getElement(1, indexRightAxis), wheelBasis0.getElement(2, indexRightAxis));

					Vector3f surfNormalWS = wheel_info.raycastInfo.contactNormalWS;
					float proj = axle.get(i).dot(surfNormalWS);
					tmp.scale(proj, surfNormalWS);
					axle.get(i).sub(tmp);
					axle.get(i).normalize();

					forwardWS.get(i).cross(surfNormalWS, axle.get(i));
					forwardWS.get(i).normalize();

					float[] floatPtr = floatArrays.getFixed(1);
					ContactConstraint.resolveSingleBilateral(chassisBody, wheel_info.raycastInfo.contactPointWS,
							groundObject, wheel_info.raycastInfo.contactPointWS, 0f, axle.get(i), floatPtr,
							timeStep);
					sideImpulse.set(i, floatPtr[0]);
					floatArrays.release(floatPtr);

					sideImpulse.set(i, sideImpulse.get(i) * sideFrictionStiffness2);
					VECTORS.release(surfNormalWS);
					MATRICES.release(wheelBasis0);
				}
			}
			TRANSFORMS.release(wheelTrans);
		}

		float sideFactor = 1f;
		float fwdFactor = 0.5f;

		boolean sliding = false;
		{
			for (int wheel = 0; wheel < getNumWheels(); wheel++) {
				WheelInfo wheel_info = wheelInfo.get(wheel);
				RigidBody groundObject = (RigidBody) wheel_info.raycastInfo.groundObject;

				float rollingFriction = 0f;

				if (groundObject != null) {
					if (wheel_info.engineForce != 0f) {
						rollingFriction = wheel_info.engineForce * timeStep;
					} else {
						float defaultRollingFrictionImpulse = 0f;
						float maxImpulse = wheel_info.brake != 0f ? wheel_info.brake : defaultRollingFrictionImpulse;
						WheelContactPoint contactPt = new WheelContactPoint(chassisBody, groundObject,
								wheel_info.raycastInfo.contactPointWS, forwardWS.get(wheel), maxImpulse);
						rollingFriction = calcRollingFriction(contactPt);
					}
				}

				// switch between active rolling (throttle), braking and non-active rolling friction (no throttle/break)

				forwardImpulse.set(wheel, 0f);
				wheelInfo.get(wheel).skidInfo = 1f;

				if (groundObject != null) {
					wheelInfo.get(wheel).skidInfo = 1f;

					float maximp = wheel_info.wheelsSuspensionForce * timeStep * wheel_info.frictionSlip;
					float maximpSide = maximp;

					float maximpSquared = maximp * maximpSide;

					forwardImpulse.set(wheel, rollingFriction); // wheelInfo.m_engineForce* timeStep;

					float x = forwardImpulse.get(wheel) * fwdFactor;
					float y = sideImpulse.get(wheel) * sideFactor;

					float impulseSquared = x * x + y * y;

					if (impulseSquared > maximpSquared) {
						sliding = true;

						float factor = maximp / (float) Math.sqrt(impulseSquared);

						wheelInfo.get(wheel).skidInfo *= factor;
					}
				}

			}
		}

		if (sliding) {
			for (int wheel = 0; wheel < getNumWheels(); wheel++) {
				if (sideImpulse.get(wheel) != 0f) {
					if (wheelInfo.get(wheel).skidInfo < 1f) {
						forwardImpulse.set(wheel, forwardImpulse.get(wheel) * wheelInfo.get(wheel).skidInfo);
						sideImpulse.set(wheel, sideImpulse.get(wheel) * wheelInfo.get(wheel).skidInfo);
					}
				}
			}
		}

		// apply the impulses
		{
			for (int wheel = 0; wheel < getNumWheels(); wheel++) {
				WheelInfo wheel_info = wheelInfo.get(wheel);

				Vector3f rel_pos = VECTORS.get();
				rel_pos.sub(wheel_info.raycastInfo.contactPointWS, chassisBody.getCenterOfMassPosition(tmp));

				if (forwardImpulse.get(wheel) != 0f) {
					tmp.scale(forwardImpulse.get(wheel), forwardWS.get(wheel));
					chassisBody.applyImpulse(tmp, rel_pos);
				}
				if (sideImpulse.get(wheel) != 0f) {
					RigidBody groundObject = (RigidBody) wheelInfo.get(wheel).raycastInfo.groundObject;

					Vector3f rel_pos2 = VECTORS.get();
					rel_pos2.sub(wheel_info.raycastInfo.contactPointWS, groundObject.getCenterOfMassPosition(tmp));

					Vector3f sideImp = VECTORS.get();
					sideImp.scale(sideImpulse.get(wheel), axle.get(wheel));

					rel_pos.z *= wheel_info.rollInfluence;
					chassisBody.applyImpulse(sideImp, rel_pos);

					// apply friction impulse on the ground
					tmp.negate(sideImp);
					groundObject.applyImpulse(tmp, rel_pos2);
				}
			}
		}
	}

	@Override
	public void buildJacobian() {
		// not yet
	}

	@Override
	public void solveConstraint(final float timeStep) {
		// not yet
	}

	/**
	 * Gets the num wheels.
	 *
	 * @return the num wheels
	 */
	public int getNumWheels() {
		return wheelInfo.size();
	}

	/**
	 * Sets the pitch control.
	 *
	 * @param pitch the new pitch control
	 */
	public void setPitchControl(final float pitch) {
		this.pitchControl = pitch;
	}

	/**
	 * Gets the rigid body.
	 *
	 * @return the rigid body
	 */
	public RigidBody getRigidBody() {
		return chassisBody;
	}

	/**
	 * Gets the right axis.
	 *
	 * @return the right axis
	 */
	public int getRightAxis() {
		return indexRightAxis;
	}

	/**
	 * Gets the up axis.
	 *
	 * @return the up axis
	 */
	public int getUpAxis() {
		return indexUpAxis;
	}

	/**
	 * Gets the forward axis.
	 *
	 * @return the forward axis
	 */
	public int getForwardAxis() {
		return indexForwardAxis;
	}

	/**
	 * Worldspace forward vector.
	 */
	public Vector3f getForwardVector(final Vector3f out) {
		Transform chassisTrans = getChassisWorldTransform(TRANSFORMS.get());

		out.set(chassisTrans.basis.getElement(0, indexForwardAxis), chassisTrans.basis.getElement(1, indexForwardAxis),
				chassisTrans.basis.getElement(2, indexForwardAxis));
		TRANSFORMS.release(chassisTrans);
		return out;
	}

	/**
	 * Velocity of vehicle (positive if velocity vector has same direction as foward vector).
	 */
	public float getCurrentSpeedKmHour() {
		return currentVehicleSpeedKmHour;
	}

	/**
	 * Sets the coordinate system.
	 *
	 * @param rightIndex the right index
	 * @param upIndex the up index
	 * @param forwardIndex the forward index
	 */
	public void setCoordinateSystem(final int rightIndex, final int upIndex, final int forwardIndex) {
		this.indexRightAxis = rightIndex;
		this.indexUpAxis = upIndex;
		this.indexForwardAxis = forwardIndex;
	}

	////////////////////////////////////////////////////////////////////////////

	/**
	 * The Class WheelContactPoint.
	 */
	private static class WheelContactPoint {
		
		/** The body 0. */
		public RigidBody body0;
		
		/** The body 1. */
		public RigidBody body1;
		
		/** The friction position world. */
		public final Vector3f frictionPositionWorld = new Vector3f();
		
		/** The friction direction world. */
		public final Vector3f frictionDirectionWorld = new Vector3f();
		
		/** The jac diag AB inv. */
		public float jacDiagABInv;
		
		/** The max impulse. */
		public float maxImpulse;

		/**
		 * Instantiates a new wheel contact point.
		 *
		 * @param body0 the body 0
		 * @param body1 the body 1
		 * @param frictionPosWorld the friction pos world
		 * @param frictionDirectionWorld the friction direction world
		 * @param maxImpulse the max impulse
		 */
		public WheelContactPoint(final RigidBody body0, final RigidBody body1, final Vector3f frictionPosWorld,
				final Vector3f frictionDirectionWorld, final float maxImpulse) {
			this.body0 = body0;
			this.body1 = body1;
			this.frictionPositionWorld.set(frictionPosWorld);
			this.frictionDirectionWorld.set(frictionDirectionWorld);
			this.maxImpulse = maxImpulse;

			float denom0 = body0.computeImpulseDenominator(frictionPosWorld, frictionDirectionWorld);
			float denom1 = body1.computeImpulseDenominator(frictionPosWorld, frictionDirectionWorld);
			float relaxation = 1f;
			jacDiagABInv = relaxation / (denom0 + denom1);
		}
	}

}
