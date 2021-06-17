/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * Bullet Continuous Collision Detection and Physics Library
 * Ragdoll Demo
 * Copyright (c) 2007 Starbreeze Studios
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
 * 
 * Written by: Marten Svanfeldt
 */

package com.bulletphysics.demos.genericjoint;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.Generic6DofConstraint;
import com.bulletphysics.dynamics.constraintsolver.TypedConstraint;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MatrixUtil;
import com.bulletphysics.linearmath.Transform;
import javax.vecmath.Vector3f;

/**
 *
 * @author jezek2
 */
public class RagDoll {
	
	//protected final BulletStack stack = BulletStack.get();

	public enum BodyPart {
		BODYPART_PELVIS,
		BODYPART_SPINE,
		BODYPART_HEAD,

		BODYPART_LEFT_UPPER_LEG,
		BODYPART_LEFT_LOWER_LEG,

		BODYPART_RIGHT_UPPER_LEG,
		BODYPART_RIGHT_LOWER_LEG,

		BODYPART_LEFT_UPPER_ARM,
		BODYPART_LEFT_LOWER_ARM,

		BODYPART_RIGHT_UPPER_ARM,
		BODYPART_RIGHT_LOWER_ARM,

		BODYPART_COUNT;
	}

	public enum JointType {
		JOINT_PELVIS_SPINE,
		JOINT_SPINE_HEAD,

		JOINT_LEFT_HIP,
		JOINT_LEFT_KNEE,

		JOINT_RIGHT_HIP,
		JOINT_RIGHT_KNEE,

		JOINT_LEFT_SHOULDER,
		JOINT_LEFT_ELBOW,

		JOINT_RIGHT_SHOULDER,
		JOINT_RIGHT_ELBOW,

		JOINT_COUNT
	}

	private DynamicsWorld ownerWorld;
	private CollisionShape[] shapes = new CollisionShape[BodyPart.BODYPART_COUNT.ordinal()];
	private RigidBody[] bodies = new RigidBody[BodyPart.BODYPART_COUNT.ordinal()];
	private TypedConstraint[] joints = new TypedConstraint[JointType.JOINT_COUNT.ordinal()];

	public RagDoll(DynamicsWorld ownerWorld, Vector3f positionOffset) {
		this(ownerWorld, positionOffset, 1.0f);
	}

	public RagDoll(DynamicsWorld ownerWorld, Vector3f positionOffset, float scale_ragdoll) {
		this.ownerWorld = ownerWorld;

		Transform tmpTrans = new Transform();
		Vector3f tmp = new Vector3f();

		// Setup the geometry
		shapes[BodyPart.BODYPART_PELVIS.ordinal()] = new CapsuleShape(scale_ragdoll * 0.15f, scale_ragdoll * 0.20f);
		shapes[BodyPart.BODYPART_SPINE.ordinal()] = new CapsuleShape(scale_ragdoll * 0.15f, scale_ragdoll * 0.28f);
		shapes[BodyPart.BODYPART_HEAD.ordinal()] = new CapsuleShape(scale_ragdoll * 0.10f, scale_ragdoll * 0.05f);
		shapes[BodyPart.BODYPART_LEFT_UPPER_LEG.ordinal()] = new CapsuleShape(scale_ragdoll * 0.07f, scale_ragdoll * 0.45f);
		shapes[BodyPart.BODYPART_LEFT_LOWER_LEG.ordinal()] = new CapsuleShape(scale_ragdoll * 0.05f, scale_ragdoll * 0.37f);
		shapes[BodyPart.BODYPART_RIGHT_UPPER_LEG.ordinal()] = new CapsuleShape(scale_ragdoll * 0.07f, scale_ragdoll * 0.45f);
		shapes[BodyPart.BODYPART_RIGHT_LOWER_LEG.ordinal()] = new CapsuleShape(scale_ragdoll * 0.05f, scale_ragdoll * 0.37f);
		shapes[BodyPart.BODYPART_LEFT_UPPER_ARM.ordinal()] = new CapsuleShape(scale_ragdoll * 0.05f, scale_ragdoll * 0.33f);
		shapes[BodyPart.BODYPART_LEFT_LOWER_ARM.ordinal()] = new CapsuleShape(scale_ragdoll * 0.04f, scale_ragdoll * 0.25f);
		shapes[BodyPart.BODYPART_RIGHT_UPPER_ARM.ordinal()] = new CapsuleShape(scale_ragdoll * 0.05f, scale_ragdoll * 0.33f);
		shapes[BodyPart.BODYPART_RIGHT_LOWER_ARM.ordinal()] = new CapsuleShape(scale_ragdoll * 0.04f, scale_ragdoll * 0.25f);

		// Setup all the rigid bodies
		Transform offset = new Transform();
		offset.setIdentity();
		offset.origin.set(positionOffset);

		Transform transform = new Transform();
		transform.setIdentity();
		transform.origin.set(0f, scale_ragdoll * 1f, 0f);
		tmpTrans.mul(offset, transform);
		bodies[BodyPart.BODYPART_PELVIS.ordinal()] = localCreateRigidBody(1f, tmpTrans, shapes[BodyPart.BODYPART_PELVIS.ordinal()]);

		transform.setIdentity();
		transform.origin.set(0f, scale_ragdoll * 1.2f, 0f);
		tmpTrans.mul(offset, transform);
		bodies[BodyPart.BODYPART_SPINE.ordinal()] = localCreateRigidBody(1f, tmpTrans, shapes[BodyPart.BODYPART_SPINE.ordinal()]);

		transform.setIdentity();
		transform.origin.set(0f, scale_ragdoll * 1.6f, 0f);
		tmpTrans.mul(offset, transform);
		bodies[BodyPart.BODYPART_HEAD.ordinal()] = localCreateRigidBody(1f, tmpTrans, shapes[BodyPart.BODYPART_HEAD.ordinal()]);

		transform.setIdentity();
		transform.origin.set(-0.18f * scale_ragdoll, 0.65f * scale_ragdoll, 0f);
		tmpTrans.mul(offset, transform);
		bodies[BodyPart.BODYPART_LEFT_UPPER_LEG.ordinal()] = localCreateRigidBody(1f, tmpTrans, shapes[BodyPart.BODYPART_LEFT_UPPER_LEG.ordinal()]);

		transform.setIdentity();
		transform.origin.set(-0.18f * scale_ragdoll, 0.2f * scale_ragdoll, 0f);
		tmpTrans.mul(offset, transform);
		bodies[BodyPart.BODYPART_LEFT_LOWER_LEG.ordinal()] = localCreateRigidBody(1f, tmpTrans, shapes[BodyPart.BODYPART_LEFT_LOWER_LEG.ordinal()]);

		transform.setIdentity();
		transform.origin.set(0.18f * scale_ragdoll, 0.65f * scale_ragdoll, 0f);
		tmpTrans.mul(offset, transform);
		bodies[BodyPart.BODYPART_RIGHT_UPPER_LEG.ordinal()] = localCreateRigidBody(1f, tmpTrans, shapes[BodyPart.BODYPART_RIGHT_UPPER_LEG.ordinal()]);

		transform.setIdentity();
		transform.origin.set(0.18f * scale_ragdoll, 0.2f * scale_ragdoll, 0f);
		tmpTrans.mul(offset, transform);
		bodies[BodyPart.BODYPART_RIGHT_LOWER_LEG.ordinal()] = localCreateRigidBody(1f, tmpTrans, shapes[BodyPart.BODYPART_RIGHT_LOWER_LEG.ordinal()]);

		transform.setIdentity();
		transform.origin.set(-0.35f * scale_ragdoll, 1.45f * scale_ragdoll, 0f);
		MatrixUtil.setEulerZYX(transform.basis, 0, 0, BulletGlobals.SIMD_HALF_PI);
		tmpTrans.mul(offset, transform);
		bodies[BodyPart.BODYPART_LEFT_UPPER_ARM.ordinal()] = localCreateRigidBody(1f, tmpTrans, shapes[BodyPart.BODYPART_LEFT_UPPER_ARM.ordinal()]);

		transform.setIdentity();
		transform.origin.set(-0.7f * scale_ragdoll, 1.45f * scale_ragdoll, 0f);
		MatrixUtil.setEulerZYX(transform.basis, 0, 0, BulletGlobals.SIMD_HALF_PI);
		tmpTrans.mul(offset, transform);
		bodies[BodyPart.BODYPART_LEFT_LOWER_ARM.ordinal()] = localCreateRigidBody(1f, tmpTrans, shapes[BodyPart.BODYPART_LEFT_LOWER_ARM.ordinal()]);

		transform.setIdentity();
		transform.origin.set(0.35f * scale_ragdoll, 1.45f * scale_ragdoll, 0f);
		MatrixUtil.setEulerZYX(transform.basis, 0, 0, -BulletGlobals.SIMD_HALF_PI);
		tmpTrans.mul(offset, transform);
		bodies[BodyPart.BODYPART_RIGHT_UPPER_ARM.ordinal()] = localCreateRigidBody(1f, tmpTrans, shapes[BodyPart.BODYPART_RIGHT_UPPER_ARM.ordinal()]);

		transform.setIdentity();
		transform.origin.set(0.7f * scale_ragdoll, 1.45f * scale_ragdoll, 0f);
		MatrixUtil.setEulerZYX(transform.basis, 0, 0, -BulletGlobals.SIMD_HALF_PI);
		tmpTrans.mul(offset, transform);
		bodies[BodyPart.BODYPART_RIGHT_LOWER_ARM.ordinal()] = localCreateRigidBody(1f, tmpTrans, shapes[BodyPart.BODYPART_RIGHT_LOWER_ARM.ordinal()]);

		// Setup some damping on the m_bodies
		for (int i = 0; i < BodyPart.BODYPART_COUNT.ordinal(); ++i) {
			bodies[i].setDamping(0.05f, 0.85f);
			bodies[i].setDeactivationTime(0.8f);
			bodies[i].setSleepingThresholds(1.6f, 2.5f);
		}

		///////////////////////////// SETTING THE CONSTRAINTS /////////////////////////////////////////////7777
		// Now setup the constraints
		Generic6DofConstraint joint6DOF;
		Transform localA = new Transform(), localB = new Transform();
		boolean useLinearReferenceFrameA = true;
		/// ******* SPINE HEAD ******** ///
		{
			localA.setIdentity();
			localB.setIdentity();

			localA.origin.set(0f, 0.30f * scale_ragdoll, 0f);

			localB.origin.set(0f, -0.14f * scale_ragdoll, 0f);

			joint6DOF = new Generic6DofConstraint(bodies[BodyPart.BODYPART_SPINE.ordinal()], bodies[BodyPart.BODYPART_HEAD.ordinal()], localA, localB, useLinearReferenceFrameA);

			//#ifdef RIGID
			//joint6DOF->setAngularLowerLimit(btVector3(-SIMD_EPSILON,-SIMD_EPSILON,-SIMD_EPSILON));
			//joint6DOF->setAngularUpperLimit(btVector3(SIMD_EPSILON,SIMD_EPSILON,SIMD_EPSILON));
			//#else
			tmp.set(-BulletGlobals.SIMD_PI * 0.3f, -BulletGlobals.FLT_EPSILON, -BulletGlobals.SIMD_PI * 0.3f);
			joint6DOF.setAngularLowerLimit(tmp);
			tmp.set(BulletGlobals.SIMD_PI * 0.5f, BulletGlobals.FLT_EPSILON, BulletGlobals.SIMD_PI * 0.3f);
			joint6DOF.setAngularUpperLimit(tmp);
			//#endif
			joints[JointType.JOINT_SPINE_HEAD.ordinal()] = joint6DOF;
			ownerWorld.addConstraint(joints[JointType.JOINT_SPINE_HEAD.ordinal()], true);
		}
		/// *************************** ///

		/// ******* LEFT SHOULDER ******** ///
		{
			localA.setIdentity();
			localB.setIdentity();

			localA.origin.set(-0.2f * scale_ragdoll, 0.15f * scale_ragdoll, 0f);

			MatrixUtil.setEulerZYX(localB.basis, BulletGlobals.SIMD_HALF_PI, 0, -BulletGlobals.SIMD_HALF_PI);
			localB.origin.set(0f, -0.18f * scale_ragdoll, 0f);

			joint6DOF = new Generic6DofConstraint(bodies[BodyPart.BODYPART_SPINE.ordinal()], bodies[BodyPart.BODYPART_LEFT_UPPER_ARM.ordinal()], localA, localB, useLinearReferenceFrameA);

			//#ifdef RIGID
			//joint6DOF->setAngularLowerLimit(btVector3(-SIMD_EPSILON,-SIMD_EPSILON,-SIMD_EPSILON));
			//joint6DOF->setAngularUpperLimit(btVector3(SIMD_EPSILON,SIMD_EPSILON,SIMD_EPSILON));
			//#else
			tmp.set(-BulletGlobals.SIMD_PI * 0.8f, -BulletGlobals.FLT_EPSILON, -BulletGlobals.SIMD_PI * 0.5f);
			joint6DOF.setAngularLowerLimit(tmp);
			tmp.set(BulletGlobals.SIMD_PI * 0.8f, BulletGlobals.FLT_EPSILON, BulletGlobals.SIMD_PI * 0.5f);
			joint6DOF.setAngularUpperLimit(tmp);
			//#endif
			joints[JointType.JOINT_LEFT_SHOULDER.ordinal()] = joint6DOF;
			ownerWorld.addConstraint(joints[JointType.JOINT_LEFT_SHOULDER.ordinal()], true);
		}
		/// *************************** ///

		/// ******* RIGHT SHOULDER ******** ///
		{
			localA.setIdentity();
			localB.setIdentity();

			localA.origin.set(0.2f * scale_ragdoll, 0.15f * scale_ragdoll, 0f);
			MatrixUtil.setEulerZYX(localB.basis, 0, 0, BulletGlobals.SIMD_HALF_PI);
			localB.origin.set(0f, -0.18f * scale_ragdoll, 0f);
			joint6DOF = new Generic6DofConstraint(bodies[BodyPart.BODYPART_SPINE.ordinal()], bodies[BodyPart.BODYPART_RIGHT_UPPER_ARM.ordinal()], localA, localB, useLinearReferenceFrameA);

			//#ifdef RIGID
			//joint6DOF->setAngularLowerLimit(btVector3(-SIMD_EPSILON,-SIMD_EPSILON,-SIMD_EPSILON));
			//joint6DOF->setAngularUpperLimit(btVector3(SIMD_EPSILON,SIMD_EPSILON,SIMD_EPSILON));
			//#else
			tmp.set(-BulletGlobals.SIMD_PI * 0.8f, -BulletGlobals.SIMD_EPSILON, -BulletGlobals.SIMD_PI * 0.5f);
			joint6DOF.setAngularLowerLimit(tmp);
			tmp.set(BulletGlobals.SIMD_PI * 0.8f, BulletGlobals.SIMD_EPSILON, BulletGlobals.SIMD_PI * 0.5f);
			joint6DOF.setAngularUpperLimit(tmp);
			//#endif
			joints[JointType.JOINT_RIGHT_SHOULDER.ordinal()] = joint6DOF;
			ownerWorld.addConstraint(joints[JointType.JOINT_RIGHT_SHOULDER.ordinal()], true);
		}
		/// *************************** ///

		/// ******* LEFT ELBOW ******** ///
		{
			localA.setIdentity();
			localB.setIdentity();

			localA.origin.set(0f, 0.18f * scale_ragdoll, 0f);
			localB.origin.set(0f, -0.14f * scale_ragdoll, 0f);
			joint6DOF = new Generic6DofConstraint(bodies[BodyPart.BODYPART_LEFT_UPPER_ARM.ordinal()], bodies[BodyPart.BODYPART_LEFT_LOWER_ARM.ordinal()], localA, localB, useLinearReferenceFrameA);

			//#ifdef RIGID
			//joint6DOF->setAngularLowerLimit(btVector3(-SIMD_EPSILON,-SIMD_EPSILON,-SIMD_EPSILON));
			//joint6DOF->setAngularUpperLimit(btVector3(SIMD_EPSILON,SIMD_EPSILON,SIMD_EPSILON));
			//#else
			tmp.set(-BulletGlobals.SIMD_EPSILON, -BulletGlobals.SIMD_EPSILON, -BulletGlobals.SIMD_EPSILON);
			joint6DOF.setAngularLowerLimit(tmp);
			tmp.set(BulletGlobals.SIMD_PI * 0.7f, BulletGlobals.SIMD_EPSILON, BulletGlobals.SIMD_EPSILON);
			joint6DOF.setAngularUpperLimit(tmp);
			//#endif
			joints[JointType.JOINT_LEFT_ELBOW.ordinal()] = joint6DOF;
			ownerWorld.addConstraint(joints[JointType.JOINT_LEFT_ELBOW.ordinal()], true);
		}
		/// *************************** ///

		/// ******* RIGHT ELBOW ******** ///
		{
			localA.setIdentity();
			localB.setIdentity();

			localA.origin.set(0f, 0.18f * scale_ragdoll, 0f);
			localB.origin.set(0f, -0.14f * scale_ragdoll, 0f);
			joint6DOF = new Generic6DofConstraint(bodies[BodyPart.BODYPART_RIGHT_UPPER_ARM.ordinal()], bodies[BodyPart.BODYPART_RIGHT_LOWER_ARM.ordinal()], localA, localB, useLinearReferenceFrameA);

			//#ifdef RIGID
			//joint6DOF->setAngularLowerLimit(btVector3(-SIMD_EPSILON,-SIMD_EPSILON,-SIMD_EPSILON));
			//joint6DOF->setAngularUpperLimit(btVector3(SIMD_EPSILON,SIMD_EPSILON,SIMD_EPSILON));
			//#else
			tmp.set(-BulletGlobals.SIMD_EPSILON, -BulletGlobals.SIMD_EPSILON, -BulletGlobals.SIMD_EPSILON);
			joint6DOF.setAngularLowerLimit(tmp);
			tmp.set(BulletGlobals.SIMD_PI * 0.7f, BulletGlobals.SIMD_EPSILON, BulletGlobals.SIMD_EPSILON);
			joint6DOF.setAngularUpperLimit(tmp);
			//#endif

			joints[JointType.JOINT_RIGHT_ELBOW.ordinal()] = joint6DOF;
			ownerWorld.addConstraint(joints[JointType.JOINT_RIGHT_ELBOW.ordinal()], true);
		}
		/// *************************** ///


		/// ******* PELVIS ******** ///
		{
			localA.setIdentity();
			localB.setIdentity();

			MatrixUtil.setEulerZYX(localA.basis, 0, BulletGlobals.SIMD_HALF_PI, 0);
			localA.origin.set(0f, 0.15f * scale_ragdoll, 0f);
			MatrixUtil.setEulerZYX(localB.basis, 0, BulletGlobals.SIMD_HALF_PI, 0);
			localB.origin.set(0f, -0.15f * scale_ragdoll, 0f);
			joint6DOF = new Generic6DofConstraint(bodies[BodyPart.BODYPART_PELVIS.ordinal()], bodies[BodyPart.BODYPART_SPINE.ordinal()], localA, localB, useLinearReferenceFrameA);

			//#ifdef RIGID
			//joint6DOF->setAngularLowerLimit(btVector3(-SIMD_EPSILON,-SIMD_EPSILON,-SIMD_EPSILON));
			//joint6DOF->setAngularUpperLimit(btVector3(SIMD_EPSILON,SIMD_EPSILON,SIMD_EPSILON));
			//#else
			tmp.set(-BulletGlobals.SIMD_PI * 0.2f, -BulletGlobals.SIMD_EPSILON, -BulletGlobals.SIMD_PI * 0.3f);
			joint6DOF.setAngularLowerLimit(tmp);
			tmp.set(BulletGlobals.SIMD_PI * 0.2f, BulletGlobals.SIMD_EPSILON, BulletGlobals.SIMD_PI * 0.6f);
			joint6DOF.setAngularUpperLimit(tmp);
			//#endif
			joints[JointType.JOINT_PELVIS_SPINE.ordinal()] = joint6DOF;
			ownerWorld.addConstraint(joints[JointType.JOINT_PELVIS_SPINE.ordinal()], true);
		}
		/// *************************** ///

		/// ******* LEFT HIP ******** ///
		{
			localA.setIdentity();
			localB.setIdentity();

			localA.origin.set(-0.18f * scale_ragdoll, -0.10f * scale_ragdoll, 0f);

			localB.origin.set(0f, 0.225f * scale_ragdoll, 0f);

			joint6DOF = new Generic6DofConstraint(bodies[BodyPart.BODYPART_PELVIS.ordinal()], bodies[BodyPart.BODYPART_LEFT_UPPER_LEG.ordinal()], localA, localB, useLinearReferenceFrameA);

			//#ifdef RIGID
			//joint6DOF->setAngularLowerLimit(btVector3(-SIMD_EPSILON,-SIMD_EPSILON,-SIMD_EPSILON));
			//joint6DOF->setAngularUpperLimit(btVector3(SIMD_EPSILON,SIMD_EPSILON,SIMD_EPSILON));
			//#else
			tmp.set(-BulletGlobals.SIMD_HALF_PI * 0.5f, -BulletGlobals.SIMD_EPSILON, -BulletGlobals.SIMD_EPSILON);
			joint6DOF.setAngularLowerLimit(tmp);
			tmp.set(BulletGlobals.SIMD_HALF_PI * 0.8f, BulletGlobals.SIMD_EPSILON, BulletGlobals.SIMD_HALF_PI * 0.6f);
			joint6DOF.setAngularUpperLimit(tmp);
			//#endif
			joints[JointType.JOINT_LEFT_HIP.ordinal()] = joint6DOF;
			ownerWorld.addConstraint(joints[JointType.JOINT_LEFT_HIP.ordinal()], true);
		}
		/// *************************** ///


		/// ******* RIGHT HIP ******** ///
		{
			localA.setIdentity();
			localB.setIdentity();

			localA.origin.set(0.18f * scale_ragdoll, -0.10f * scale_ragdoll, 0f);
			localB.origin.set(0f, 0.225f * scale_ragdoll, 0f);

			joint6DOF = new Generic6DofConstraint(bodies[BodyPart.BODYPART_PELVIS.ordinal()], bodies[BodyPart.BODYPART_RIGHT_UPPER_LEG.ordinal()], localA, localB, useLinearReferenceFrameA);

			//#ifdef RIGID
			//joint6DOF->setAngularLowerLimit(btVector3(-SIMD_EPSILON,-SIMD_EPSILON,-SIMD_EPSILON));
			//joint6DOF->setAngularUpperLimit(btVector3(SIMD_EPSILON,SIMD_EPSILON,SIMD_EPSILON));
			//#else
			tmp.set(-BulletGlobals.SIMD_HALF_PI * 0.5f, -BulletGlobals.SIMD_EPSILON, -BulletGlobals.SIMD_HALF_PI * 0.6f);
			joint6DOF.setAngularLowerLimit(tmp);
			tmp.set(BulletGlobals.SIMD_HALF_PI * 0.8f, BulletGlobals.SIMD_EPSILON, BulletGlobals.SIMD_EPSILON);
			joint6DOF.setAngularUpperLimit(tmp);
			//#endif
			joints[JointType.JOINT_RIGHT_HIP.ordinal()] = joint6DOF;
			ownerWorld.addConstraint(joints[JointType.JOINT_RIGHT_HIP.ordinal()], true);
		}
		/// *************************** ///


		/// ******* LEFT KNEE ******** ///
		{
			localA.setIdentity();
			localB.setIdentity();

			localA.origin.set(0f, -0.225f * scale_ragdoll, 0f);
			localB.origin.set(0f, 0.185f * scale_ragdoll, 0f);
			joint6DOF = new Generic6DofConstraint(bodies[BodyPart.BODYPART_LEFT_UPPER_LEG.ordinal()], bodies[BodyPart.BODYPART_LEFT_LOWER_LEG.ordinal()], localA, localB, useLinearReferenceFrameA);
			//
			//#ifdef RIGID
			//joint6DOF->setAngularLowerLimit(btVector3(-SIMD_EPSILON,-SIMD_EPSILON,-SIMD_EPSILON));
			//joint6DOF->setAngularUpperLimit(btVector3(SIMD_EPSILON,SIMD_EPSILON,SIMD_EPSILON));
			//#else
			tmp.set(-BulletGlobals.SIMD_EPSILON, -BulletGlobals.SIMD_EPSILON, -BulletGlobals.SIMD_EPSILON);
			joint6DOF.setAngularLowerLimit(tmp);
			tmp.set(BulletGlobals.SIMD_PI * 0.7f, BulletGlobals.SIMD_EPSILON, BulletGlobals.SIMD_EPSILON);
			joint6DOF.setAngularUpperLimit(tmp);
			//#endif
			joints[JointType.JOINT_LEFT_KNEE.ordinal()] = joint6DOF;
			ownerWorld.addConstraint(joints[JointType.JOINT_LEFT_KNEE.ordinal()], true);
		}
		/// *************************** ///

		/// ******* RIGHT KNEE ******** ///
		{
			localA.setIdentity();
			localB.setIdentity();

			localA.origin.set(0f, -0.225f * scale_ragdoll, 0f);
			localB.origin.set(0f, 0.185f * scale_ragdoll, 0f);
			joint6DOF = new Generic6DofConstraint(bodies[BodyPart.BODYPART_RIGHT_UPPER_LEG.ordinal()], bodies[BodyPart.BODYPART_RIGHT_LOWER_LEG.ordinal()], localA, localB, useLinearReferenceFrameA);

			//#ifdef RIGID
			//joint6DOF->setAngularLowerLimit(btVector3(-SIMD_EPSILON,-SIMD_EPSILON,-SIMD_EPSILON));
			//joint6DOF->setAngularUpperLimit(btVector3(SIMD_EPSILON,SIMD_EPSILON,SIMD_EPSILON));
			//#else
			tmp.set(-BulletGlobals.SIMD_EPSILON, -BulletGlobals.SIMD_EPSILON, -BulletGlobals.SIMD_EPSILON);
			joint6DOF.setAngularLowerLimit(tmp);
			tmp.set(BulletGlobals.SIMD_PI * 0.7f, BulletGlobals.SIMD_EPSILON, BulletGlobals.SIMD_EPSILON);
			joint6DOF.setAngularUpperLimit(tmp);
			//#endif
			joints[JointType.JOINT_RIGHT_KNEE.ordinal()] = joint6DOF;
			ownerWorld.addConstraint(joints[JointType.JOINT_RIGHT_KNEE.ordinal()], true);
		}
		/// *************************** ///
	}

	public void destroy() {
		int i;

		// Remove all constraints
		for (i = 0; i < JointType.JOINT_COUNT.ordinal(); ++i) {
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
		rbInfo.additionalDamping = true;
		RigidBody body = new RigidBody(rbInfo);

		ownerWorld.addRigidBody(body);

		return body;
	}
	
}
