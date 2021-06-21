/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
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

package com.bulletphysics.demos.movingconcave;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.bulletphysics.demos.opengl.DemoApplication;
import com.bulletphysics.demos.opengl.GLDebugDrawer;
import com.bulletphysics.demos.opengl.IGL;
import com.bulletphysics.demos.opengl.LWJGL;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.extras.gimpact.GImpactCollisionAlgorithm;
import com.bulletphysics.extras.gimpact.GImpactMeshShape;
import com.bulletphysics.linearmath.Transform;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import org.lwjgl.LWJGLException;
import static com.bulletphysics.demos.opengl.IGL.*;

/**
 * 
 * 
 * @author jezek2
 */
public class MovingConcaveDemo extends DemoApplication {
	
	private BroadphaseInterface overlappingPairCache;
	private CollisionDispatcher dispatcher;
	private ConstraintSolver solver;
	private DefaultCollisionConfiguration collisionConfiguration;
	
	private CollisionShape trimeshShape;

	public MovingConcaveDemo(IGL gl) {
		super(gl);
	}
	
	@Override
	public void clientMoveAndDisplay() {
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// simple dynamics world doesn't handle fixed-time-stepping
		float ms = getDeltaTimeMicroseconds();

		// step the simulation
		if (dynamicsWorld != null) {
			dynamicsWorld.stepSimulation(ms / 1000000f);
			// optional but useful: debug drawing
			dynamicsWorld.debugDrawWorld();
		}

		renderme();

		//glFlush();
		//glutSwapBuffers();
	}

	@Override
	public void displayCallback() {
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		renderme();

		// optional but useful: debug drawing to detect problems
		if (dynamicsWorld != null) {
			dynamicsWorld.debugDrawWorld();
		}

		//glFlush();
		//glutSwapBuffers();
	}

	public void initGImpactCollision() {
		// create trimesh
		TriangleIndexVertexArray indexVertexArrays = new TriangleIndexVertexArray(
				Bunny.NUM_TRIANGLES, Bunny.getIndexBuffer(), 4 * 3,
				Bunny.NUM_VERTICES, Bunny.getVertexBuffer(), 4 * 3);

		GImpactMeshShape trimesh = new GImpactMeshShape(indexVertexArrays);
		trimesh.setLocalScaling(new Vector3f(4f, 4f, 4f));
		trimesh.updateBound();
		trimeshShape = trimesh;

		// register algorithm
		GImpactCollisionAlgorithm.registerAlgorithm(dispatcher);
	}
	
	public void initPhysics() {
		setCameraDistance(30f);

		// collision configuration contains default setup for memory, collision setup
		collisionConfiguration = new DefaultCollisionConfiguration();

		// use the default collision dispatcher. For parallel processing you can use a diffent dispatcher (see Extras/BulletMultiThreaded)
		dispatcher = new CollisionDispatcher(collisionConfiguration);

		overlappingPairCache = new DbvtBroadphase();

		// the default constraint solver. For parallel processing you can use a different solver (see Extras/BulletMultiThreaded)
		SequentialImpulseConstraintSolver sol = new SequentialImpulseConstraintSolver();
		solver = sol;
		
		// TODO: needed for SimpleDynamicsWorld
		//sol.setSolverMode(sol.getSolverMode() & ~SolverMode.SOLVER_CACHE_FRIENDLY.getMask());
		
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);
		//dynamicsWorld = new SimpleDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);

		dynamicsWorld.setGravity(new Vector3f(0f, -10f, 0f));
		
		initGImpactCollision();

		float mass = 0f;
		Transform startTransform = new Transform();
		startTransform.setIdentity();

		CollisionShape staticboxShape1 = new BoxShape(new Vector3f(200f, 1f, 200f)); // floor
		CollisionShape staticboxShape2 = new BoxShape(new Vector3f(1f, 50f, 200f)); // left wall
		CollisionShape staticboxShape3 = new BoxShape(new Vector3f(1f, 50f, 200f)); // right wall
		CollisionShape staticboxShape4 = new BoxShape(new Vector3f(200f, 50f, 1f)); // front wall
		CollisionShape staticboxShape5 = new BoxShape(new Vector3f(200f, 50f, 1f)); // back wall

		CompoundShape staticScenario = new CompoundShape(); // static scenario

		startTransform.origin.set(0f, 0f, 0f);
		staticScenario.addChildShape(startTransform, staticboxShape1);
		startTransform.origin.set(-200f, 25f, 0f);
		staticScenario.addChildShape(startTransform, staticboxShape2);
		startTransform.origin.set(200f, 25f, 0f);
		staticScenario.addChildShape(startTransform, staticboxShape3);
		startTransform.origin.set(0f, 25f, 200f);
		staticScenario.addChildShape(startTransform, staticboxShape4);
		startTransform.origin.set(0f, 25f, -200f);
		staticScenario.addChildShape(startTransform, staticboxShape5);

		startTransform.origin.set(0f, 0f, 0f);

		RigidBody staticBody = localCreateRigidBody(mass, startTransform, staticScenario);

		staticBody.setCollisionFlags(staticBody.getCollisionFlags() | CollisionFlags.STATIC_OBJECT);

		// enable custom material callback
		//staticBody.setCollisionFlags(staticBody.getCollisionFlags() | CollisionFlags.CUSTOM_MATERIAL_CALLBACK);

		// static plane
		Vector3f normal = new Vector3f(0.4f, 1.5f, -0.4f);
		normal.normalize();
		CollisionShape staticplaneShape6 = new StaticPlaneShape(normal, 0f); // A plane

		startTransform.origin.set(0f, 0f, 0f);

		RigidBody staticBody2 = localCreateRigidBody(mass, startTransform, staticplaneShape6);

		staticBody2.setCollisionFlags(staticBody2.getCollisionFlags() | CollisionFlags.STATIC_OBJECT);

		for (int i=0; i<9; i++) {
			CollisionShape boxShape = new BoxShape(new Vector3f(1f, 1f, 1f));
			startTransform.origin.set(2f * i - 5f, 2f, -3f);
			localCreateRigidBody(1, startTransform, boxShape);
		}
	}
	
	public void shootTrimesh(Vector3f destination) {
		if (dynamicsWorld != null) {
			float mass = 4f;
			Transform startTransform = new Transform();
			startTransform.setIdentity();
			Vector3f camPos = getCameraPosition();
			startTransform.origin.set(camPos);

			RigidBody body = localCreateRigidBody(mass, startTransform, trimeshShape);

			Vector3f linVel = new Vector3f(destination.x - camPos.x, destination.y - camPos.y, destination.z - camPos.z);
			linVel.normalize();
			linVel.scale(ShootBoxInitialSpeed * 0.25f);

			Transform tr = new Transform();
			tr.origin.set(camPos);
			tr.setRotation(new Quat4f(0f, 0f, 0f, 1f));
			body.setWorldTransform(tr);

			body.setLinearVelocity(linVel);
			body.setAngularVelocity(new Vector3f(0f, 0f, 0f));
		}
	}

	@Override
	public void keyboardCallback(char key, int x, int y, int modifiers) {
		switch (key) {
			case '.':
				shootTrimesh(getCameraTargetPosition());
				break;

			default:
				super.keyboardCallback(key, x, y, modifiers);
		}
	}
	
	public static void main(String[] args) throws LWJGLException {
		MovingConcaveDemo concaveDemo = new MovingConcaveDemo(LWJGL.getGL());
		concaveDemo.initPhysics();
		concaveDemo.getDynamicsWorld().setDebugDrawer(new GLDebugDrawer(LWJGL.getGL()));

		LWJGL.main(args, 800, 600, "Moving Concave Mesh Demo", concaveDemo);
	}
	
}
