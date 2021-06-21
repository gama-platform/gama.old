/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 * DynamicControlDemo port by: Olivier OUDIN / LvR
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
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.demos.opengl.DemoApplication;
import com.bulletphysics.demos.opengl.IGL;
import com.bulletphysics.demos.opengl.LWJGL;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.HingeConstraint;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;
import javax.vecmath.Vector3f;
import org.lwjgl.LWJGLException;
import static com.bulletphysics.demos.opengl.IGL.*;

/**
 *
 * @author LvR
 */
public class DynamicControlDemo extends DemoApplication {
	
	private	float time;
	private float cyclePeriod; // in milliseconds
	private float muscleStrength;
	
	private ObjectArrayList<TestRig> rigs = new ObjectArrayList<TestRig>();
	
	public DynamicControlDemo(IGL gl) {
		super(gl);
	}

	public void initPhysics() {
		// Setup the basic world
		time = 0.0f;
		cyclePeriod = 2000.0f; // in milliseconds
		muscleStrength = 0.05f;

		setCameraDistance(5.0f);
		
		DefaultCollisionConfiguration collision_config = new DefaultCollisionConfiguration();

		CollisionDispatcher dispatcher = new CollisionDispatcher(collision_config);

		Vector3f worldAabbMin = new Vector3f(-10000,-10000,-10000);
		Vector3f worldAabbMax = new Vector3f(10000,10000,10000);
		//BroadphaseInterface overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax);
		//BroadphaseInterface overlappingPairCache = new SimpleBroadphase();
		BroadphaseInterface overlappingPairCache = new DbvtBroadphase();

		ConstraintSolver constraintSolver = new SequentialImpulseConstraintSolver();

		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, constraintSolver, collision_config);

		// Setup a big ground box
		{
			CollisionShape groundShape = new BoxShape(new Vector3f(200f, 10f, 200f));
			// TODO
			//m_collisionShapes.push_back(groundShape);
			Transform groundTransform = new Transform();
			groundTransform.setIdentity();
			groundTransform.origin.set(0f, -10f, 0f);
			localCreateRigidBody(0f, groundTransform, groundShape);
		}

		// Spawn one TestRig
		Vector3f startOffset = new Vector3f(1.0f, 0.5f, 0.0f);
		spawnTestRig(startOffset, false);
		startOffset.set(-2.0f, 0.5f, 0.0f);
		spawnTestRig(startOffset, true);

		clientResetScene();
	}

	public void spawnTestRig(Vector3f startOffset, boolean fixed) {
		TestRig rig = new TestRig(dynamicsWorld, startOffset, fixed);
		rigs.add(rig);
	}
	
	@Override
	public void clientMoveAndDisplay() {
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// simple dynamics world doesn't handle fixed-time-stepping
		float ms = getDeltaTimeMicroseconds();
		float minFPS = 1000000f / 60f;
		if (ms > minFPS) {
			ms = minFPS;
		}

		time+=ms;
		
		//
		// set per-frame sinusoidal position targets using angular motor (hacky?)
		//
		for (int r=0; r<rigs.size(); r++) {
			for (int i=0; i<2*TestRig.NUM_LEGS; i++) {
				HingeConstraint hingeC = (HingeConstraint)(rigs.getQuick(r).getJoints()[i]);
				float curAngle = hingeC.getHingeAngle();

				float targetPercent = ((int)(time / 1000) % (int)(cyclePeriod)) / cyclePeriod;
				float targetAngle = 0.5f * (1.0f + (float)Math.sin(BulletGlobals.SIMD_2_PI * targetPercent));
				float targetLimitAngle = hingeC.getLowerLimit() + targetAngle * (hingeC.getUpperLimit() - hingeC.getLowerLimit());
				float angleError = targetLimitAngle - curAngle;
				float desiredAngularVel = 1000000.f * angleError/ms;
				hingeC.enableAngularMotor(true, desiredAngularVel, muscleStrength);
			}
		}
		
		if (dynamicsWorld != null) {
			dynamicsWorld.stepSimulation(ms / 1000000.f);
			// optional but useful: debug drawing
			dynamicsWorld.debugDrawWorld();
		}

		for (int i=2; i>=0; i--) {
			CollisionObject obj = dynamicsWorld.getCollisionObjectArray().getQuick(i);
			RigidBody body = RigidBody.upcast(obj);
			drawFrame(body.getWorldTransform(new Transform()));
		}
		
		renderme();

		//glFlush();
		//glutSwapBuffers();
	}

	@Override
	public void displayCallback() {
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		if (dynamicsWorld != null) {
			dynamicsWorld.debugDrawWorld();
		}

		renderme();

		//glFlush();
		//glutSwapBuffers();
	}

	@Override
	public void keyboardCallback(char key, int x, int y, int modifiers) {
		switch (key) {
			case '+':
			case '=':
				cyclePeriod /= 1.1f;
				if (cyclePeriod < 1.f) {
					cyclePeriod = 1.f;
				}
				break;
			case '-':
			case '_':
				cyclePeriod *= 1.1f;
				break;
			case '[':
				muscleStrength /= 1.1f;
				break;
			case ']':
				muscleStrength *= 1.1f;
				break;
			default:
				super.keyboardCallback(key, x, y, modifiers);
		}
	}

	private void vertex(Vector3f v) {
		gl.glVertex3f(v.x, v.y, v.z);
	}

	private void drawFrame(Transform tr) {
		final float size = 1.0f;
		
		gl.glBegin(GL_LINES);

		// x
		gl.glColor3f(255.f,0,0);
		Vector3f vX = new Vector3f();
		vX.set(size,0,0);
		tr.transform(vX);
		vertex(tr.origin);
		vertex(vX);

		// y
		gl.glColor3f(0,255.f,0);
		Vector3f vY = new Vector3f();
		vY.set(0,size,0);
		tr.transform(vY);
		vertex(tr.origin);
		vertex(vY);

		// z
		gl.glColor3f(0,0,255.f);
		Vector3f vZ = new Vector3f();
		vZ.set(0,0,size);
		tr.transform(vZ);
		vertex(tr.origin);
		vertex(vZ);

		gl.glEnd();
	}

	public static void main(String[] args) throws LWJGLException {
		DynamicControlDemo demoApp = new DynamicControlDemo(LWJGL.getGL());
		demoApp.initPhysics();

		LWJGL.main(args, 800, 600, "Bullet Physics Demo. http://bullet.sf.net", demoApp);
	}
	
}
