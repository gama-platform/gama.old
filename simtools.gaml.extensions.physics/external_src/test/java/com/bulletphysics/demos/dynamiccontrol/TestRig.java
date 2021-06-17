/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 * TestRig port by: Olivier OUDIN / LvR
 * 
 * Bullet Continuous Collision Detection and Physics Library
 * Copyright (c) 2003-2008 Erwin Coumans  http://www.bulletphysics.com/
 * 
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose, 
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package com.bulletphysics.demos.dynamiccontrol;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.demos.genericjoint.RagDoll.BodyPart;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.HingeConstraint;
import com.bulletphysics.dynamics.constraintsolver.TypedConstraint;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MatrixUtil;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 *
 * @author LvR
 */
public class TestRig {
	
	//protected final BulletStack stack = BulletStack.get();

	public static final int NUM_LEGS = 6;
	private static final int BODYPART_COUNT = 2 * NUM_LEGS + 1;
	private static final int JOINT_COUNT = BODYPART_COUNT - 1;
	
	private DynamicsWorld ownerWorld;
	private CollisionShape[] shapes = new CollisionShape[BODYPART_COUNT];
	private RigidBody[] bodies = new RigidBody[BODYPART_COUNT];
	private TypedConstraint[] joints = new TypedConstraint[JOINT_COUNT];

	public TestRig(DynamicsWorld ownerWorld, Vector3f positionOffset, boolean fixed) {
		this.ownerWorld = ownerWorld;

		Transform tmpTrans = new Transform();

		Vector3f up = new Vector3f();
		up.set(0.0f, 1.0f, 0.0f);

		//
		// Setup geometry
		//
		float bodySize  = 0.25f;
		float legLength = 0.45f;
		float foreLegLength = 0.75f;
		shapes[0] = new CapsuleShape(bodySize, 0.10f);
		int i;
		for ( i=0; i<NUM_LEGS; i++)
		{
			shapes[1 + 2*i] = new CapsuleShape(0.10f, legLength);
			shapes[2 + 2*i] = new CapsuleShape(0.08f, foreLegLength);
		}

		//
		// Setup rigid bodies
		//
		float height = 0.5f;
		Transform offset = new Transform();
		offset.setIdentity();
		offset.origin.set(positionOffset);

		// root
		Vector3f root = new Vector3f();
		root.set(0.0f, height, 0.0f);
		Transform transform = new Transform();
		transform.setIdentity();
		transform.origin.set(root);
		tmpTrans.mul(offset, transform);
		if (fixed) {
			bodies[0] = localCreateRigidBody(0.0f, tmpTrans, shapes[0]);
		} else {
			bodies[0] = localCreateRigidBody(1.0f, tmpTrans, shapes[0]);
		}
		// legs
		for ( i=0; i<NUM_LEGS; i++)
		{
			float angle = BulletGlobals.SIMD_2_PI * i / NUM_LEGS;
			float sin = (float)Math.sin(angle);
			float cos = (float)Math.cos(angle);

			transform.setIdentity();
			Vector3f boneOrigin = new Vector3f();
			boneOrigin.set(cos*(bodySize+0.5f*legLength), height, sin*(bodySize+0.5f*legLength));
			transform.origin.set(boneOrigin);

			// thigh
			Vector3f toBone = new Vector3f(boneOrigin);
			toBone.sub(root);
			toBone.normalize();
			Vector3f axis = new Vector3f();
			axis.cross(toBone,up);
			Quat4f q = new Quat4f();
			QuaternionUtil.setRotation(q, axis, BulletGlobals.SIMD_HALF_PI);
			transform.setRotation(q);
			tmpTrans.mul(offset, transform);
			bodies[1+2*i] = localCreateRigidBody(1.0f, tmpTrans, shapes[1+2*i]);

			// shin
			transform.setIdentity();
			transform.origin.set(cos*(bodySize+legLength), height-0.5f*foreLegLength, sin*(bodySize+legLength));
			tmpTrans.mul(offset, transform);
			bodies[2+2*i] = localCreateRigidBody(1.0f, tmpTrans, shapes[2+2*i]);
		}

		// Setup some damping on the bodies
		for (i = 0; i < BODYPART_COUNT; ++i) {
			bodies[i].setDamping(0.05f, 0.85f);
			bodies[i].setDeactivationTime(0.8f);
			bodies[i].setSleepingThresholds(1.6f, 2.5f);
		}

		//
		// Setup the constraints
		//
		HingeConstraint hingeC;
		//ConeTwistConstraint* coneC;

		Transform localA = new Transform();
		Transform localB = new Transform();
		Transform localC = new Transform();

		for ( i=0; i<NUM_LEGS; i++)
		{
			float angle = BulletGlobals.SIMD_2_PI * i / NUM_LEGS;
			float sin = (float)Math.sin(angle);
			float cos = (float)Math.cos(angle);

			// hip joints
			localA.setIdentity();
			localB.setIdentity();
			MatrixUtil.setEulerZYX(localA.basis, 0, -angle,0);
			localA.origin.set(cos*bodySize, 0.0f, sin*bodySize);
			tmpTrans.inverse(bodies[1+2*i].getWorldTransform(new Transform()));
			tmpTrans.mul(tmpTrans, bodies[0].getWorldTransform(new Transform()));
			localB.mul(tmpTrans, localA);
			hingeC = new HingeConstraint(bodies[0], bodies[1+2*i], localA, localB);
			hingeC.setLimit(-0.75f * BulletGlobals.SIMD_2_PI * 0.125f, BulletGlobals.SIMD_2_PI * 0.0625f);
			//hingeC.setLimit(-0.1f, 0.1f);
			joints[2*i] = hingeC;
			ownerWorld.addConstraint(joints[2*i], true);

			// knee joints
			localA.setIdentity();
			localB.setIdentity();
			localC.setIdentity();
			MatrixUtil.setEulerZYX(localA.basis, 0, -angle,0);
			localA.origin.set(cos*(bodySize+legLength), 0.0f, sin*(bodySize+legLength));
			tmpTrans.inverse(bodies[1+2*i].getWorldTransform(new Transform()));
			tmpTrans.mul(tmpTrans, bodies[0].getWorldTransform(new Transform()));
			localB.mul(tmpTrans, localA) ;
			tmpTrans.inverse(bodies[2+2*i].getWorldTransform(new Transform()));
			tmpTrans.mul(tmpTrans, bodies[0].getWorldTransform(new Transform()));
			localC.mul(tmpTrans, localA) ;
			hingeC = new HingeConstraint(bodies[1+2*i], bodies[2+2*i], localB, localC);
			//hingeC.setLimit(-0.01f, 0.01f);
			hingeC.setLimit(- BulletGlobals.SIMD_2_PI * 0.0625f, 0.2f);
			joints[1+2*i] = hingeC;
			ownerWorld.addConstraint(joints[1+2*i], true);
		}
	}

	public void destroy() {
		int i;

		// Remove all constraints
		for (i = 0; i < JOINT_COUNT; ++i) {
			ownerWorld.removeConstraint(joints[i]);
			//joints[i].destroy();
			joints[i] = null;
		}

		// Remove all bodies and shapes
		for (i = 0; i < BodyPart.BODYPART_COUNT.ordinal(); ++i) {
			ownerWorld.removeRigidBody(bodies[i]);

			//bodies[i].getMotionState().destroy();

			bodies[i].destroy();
			bodies[i] = null;

			//shapes[i].destroy();
			shapes[i] = null;
		}
	}
	
	private RigidBody localCreateRigidBody(float mass, Transform startTransform, CollisionShape shape) {
		boolean isDynamic = (mass != 0f);

		Vector3f localInertia = new Vector3f();
		localInertia.set(0f, 0f, 0f);
		if (isDynamic) {
			shape.calculateLocalInertia(mass, localInertia);
		}

		DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, shape, localInertia);
		RigidBody body = new RigidBody(rbInfo);

		ownerWorld.addRigidBody(body);

		return body;
	}

	public TypedConstraint[] getJoints() {
		return joints;
	}
}
