/*******************************************************************************************************
 *
 * WheelInfo.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.dynamics.vehicle;

import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Vector3f;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;

/**
 * WheelInfo contains information per wheel about friction and suspension.
 *
 * @author jezek2
 */
public class WheelInfo {

	// protected final BulletStack stack = BulletStack.get();

	/** The raycast info. */
	public final RaycastInfo raycastInfo = new RaycastInfo();

	/** The world transform. */
	public final Transform worldTransform = new Transform();

	/** The chassis connection point CS. */
	public final Vector3f chassisConnectionPointCS = new Vector3f(); // const
	
	/** The wheel direction CS. */
	public final Vector3f wheelDirectionCS = new Vector3f(); // const
	
	/** The wheel axle CS. */
	public final Vector3f wheelAxleCS = new Vector3f(); // const or modified by steering
	
	/** The suspension rest length 1. */
	public float suspensionRestLength1; // const
	
	/** The max suspension travel cm. */
	public float maxSuspensionTravelCm;
	
	/** The max suspension force. */
	public float maxSuspensionForce;
	
	/** The wheels radius. */
	public float wheelsRadius; // const
	
	/** The suspension stiffness. */
	public float suspensionStiffness; // const
	
	/** The wheels damping compression. */
	public float wheelsDampingCompression; // const
	
	/** The wheels damping relaxation. */
	public float wheelsDampingRelaxation; // const
	
	/** The friction slip. */
	public float frictionSlip;
	
	/** The steering. */
	public float steering;
	
	/** The rotation. */
	public float rotation;
	
	/** The delta rotation. */
	public float deltaRotation;
	
	/** The roll influence. */
	public float rollInfluence;

	/** The engine force. */
	public float engineForce;

	/** The brake. */
	public float brake;

	/** The b is front wheel. */
	public boolean bIsFrontWheel;

	/** The client info. */
	public Object clientInfo; // can be used to store pointer to sync transforms...

	/** The clipped inv contact dot suspension. */
	public float clippedInvContactDotSuspension;
	
	/** The suspension relative velocity. */
	public float suspensionRelativeVelocity;
	
	/** The wheels suspension force. */
	// calculated by suspension
	public float wheelsSuspensionForce;
	
	/** The skid info. */
	public float skidInfo;

	/**
	 * Instantiates a new wheel info.
	 *
	 * @param ci the ci
	 */
	public WheelInfo(final WheelInfoConstructionInfo ci) {
		suspensionRestLength1 = ci.suspensionRestLength;
		maxSuspensionTravelCm = ci.maxSuspensionTravelCm;
		maxSuspensionForce = ci.maxSuspensionForce;

		wheelsRadius = ci.wheelRadius;
		suspensionStiffness = ci.suspensionStiffness;
		wheelsDampingCompression = ci.wheelsDampingCompression;
		wheelsDampingRelaxation = ci.wheelsDampingRelaxation;
		chassisConnectionPointCS.set(ci.chassisConnectionCS);
		wheelDirectionCS.set(ci.wheelDirectionCS);
		wheelAxleCS.set(ci.wheelAxleCS);
		frictionSlip = ci.frictionSlip;
		steering = 0f;
		engineForce = 0f;
		rotation = 0f;
		deltaRotation = 0f;
		brake = 0f;
		rollInfluence = 0.1f;
		bIsFrontWheel = ci.bIsFrontWheel;
	}

	/**
	 * Gets the suspension rest length.
	 *
	 * @return the suspension rest length
	 */
	public float getSuspensionRestLength() {
		return suspensionRestLength1;
	}

	/**
	 * Update wheel.
	 *
	 * @param chassis the chassis
	 * @param raycastInfo the raycast info
	 */
	public void updateWheel(final RigidBody chassis, final RaycastInfo raycastInfo) {
		if (raycastInfo.isInContact) {
			float project = raycastInfo.contactNormalWS.dot(raycastInfo.wheelDirectionWS);
			Vector3f chassis_velocity_at_contactPoint = VECTORS.get();
			Vector3f relpos = VECTORS.get();
			relpos.sub(raycastInfo.contactPointWS, chassis.getCenterOfMassPosition(VECTORS.get()));
			chassis.getVelocityInLocalPoint(relpos, chassis_velocity_at_contactPoint);
			float projVel = raycastInfo.contactNormalWS.dot(chassis_velocity_at_contactPoint);
			if (project >= -0.1f) {
				suspensionRelativeVelocity = 0f;
				clippedInvContactDotSuspension = 1f / 0.1f;
			} else {
				float inv = -1f / project;
				suspensionRelativeVelocity = projVel * inv;
				clippedInvContactDotSuspension = inv;
			}
		} else {
			// Not in contact : position wheel in a nice (rest length) position
			raycastInfo.suspensionLength = getSuspensionRestLength();
			suspensionRelativeVelocity = 0f;
			raycastInfo.contactNormalWS.negate(raycastInfo.wheelDirectionWS);
			clippedInvContactDotSuspension = 1f;
		}
	}

	////////////////////////////////////////////////////////////////////////////

	/**
	 * The Class RaycastInfo.
	 */
	public static class RaycastInfo {
		
		/** The contact normal WS. */
		// set by raycaster
		public final Vector3f contactNormalWS = new Vector3f(); // contactnormal
		
		/** The contact point WS. */
		public final Vector3f contactPointWS = new Vector3f(); // raycast hitpoint
		
		/** The suspension length. */
		public float suspensionLength;
		
		/** The hard point WS. */
		public final Vector3f hardPointWS = new Vector3f(); // raycast starting point
		
		/** The wheel direction WS. */
		public final Vector3f wheelDirectionWS = new Vector3f(); // direction in worldspace
		
		/** The wheel axle WS. */
		public final Vector3f wheelAxleWS = new Vector3f(); // axle in worldspace
		
		/** The is in contact. */
		public boolean isInContact;
		
		/** The ground object. */
		public Object groundObject; // could be general void* ptr
	}

}
