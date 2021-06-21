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

package com.bulletphysics.demos.bsp;

import com.bulletphysics.util.ObjectArrayList;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.demos.opengl.DemoApplication;
import com.bulletphysics.demos.opengl.GLDebugDrawer;
import com.bulletphysics.demos.opengl.IGL;
import com.bulletphysics.demos.opengl.LWJGL;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Transform;
import javax.vecmath.Vector3f;
import static com.bulletphysics.demos.opengl.IGL.*;

/**
 * BspDemo shows the convex collision detection, by converting a Quake BSP file
 * into convex objects and allowing interaction with boxes.
 * 
 * @author jezek2
 */
public class BspDemo extends DemoApplication {

	private static final float CUBE_HALF_EXTENTS = 1;
	private static final float EXTRA_HEIGHT      = -20f;
	
	// keep the collision shapes, for deletion/cleanup
	public ObjectArrayList<CollisionShape> collisionShapes = new ObjectArrayList<CollisionShape>();
	public BroadphaseInterface broadphase;
	public CollisionDispatcher dispatcher;
	public ConstraintSolver solver;
	public DefaultCollisionConfiguration collisionConfiguration;

	public BspDemo(IGL gl) {
		super(gl);
	}
	
	public void initPhysics() throws Exception {
		cameraUp.set(0f, 0f, 1f);
		forwardAxis = 1;

		setCameraDistance(22f);

		// Setup a Physics Simulation Environment

		collisionConfiguration = new DefaultCollisionConfiguration();
		// btCollisionShape* groundShape = new btBoxShape(btVector3(50,3,50));
		dispatcher = new CollisionDispatcher(collisionConfiguration);
		Vector3f worldMin = new Vector3f(-1000f,-1000f,-1000f);
		Vector3f worldMax = new Vector3f(1000f,1000f,1000f);
		//broadphase = new AxisSweep3(worldMin, worldMax);
		//broadphase = new SimpleBroadphase();
		broadphase = new DbvtBroadphase();
		//btOverlappingPairCache* broadphase = new btSimpleBroadphase();
		solver = new SequentialImpulseConstraintSolver();
		//ConstraintSolver* solver = new OdeConstraintSolver;
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher,broadphase,solver,collisionConfiguration);

		Vector3f gravity = new Vector3f();
		gravity.negate(cameraUp);
		gravity.scale(10f);
		dynamicsWorld.setGravity(gravity);

		new BspToBulletConverter().convertBsp(getClass().getResourceAsStream("exported.bsp.txt"));
		
		clientResetScene();
	}
	
	@Override
	public void clientMoveAndDisplay() {
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); 
		float dt = getDeltaTimeMicroseconds() * 0.000001f;

		dynamicsWorld.stepSimulation(dt);

		// optional but useful: debug drawing
		dynamicsWorld.debugDrawWorld();

		renderme();

		//glFlush();
		//glutSwapBuffers();
	}

	@Override
	public void displayCallback() {
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); 

		renderme();

		//glFlush();
		//glutSwapBuffers();
	}

	public static void main(String[] args) throws Exception {
		BspDemo demo = new BspDemo(LWJGL.getGL());
		demo.initPhysics();
		demo.getDynamicsWorld().setDebugDrawer(new GLDebugDrawer(LWJGL.getGL()));

		LWJGL.main(args, 800, 600, "Bullet Physics Demo. http://bullet.sf.net", demo);
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	private class BspToBulletConverter extends BspConverter {
		@Override
		public void addConvexVerticesCollider(ObjectArrayList<Vector3f> vertices) {
			if (vertices.size() > 0) {
				float mass = 0f;
				Transform startTransform = new Transform();
				// can use a shift
				startTransform.setIdentity();
				startTransform.origin.set(0, 0, -10f);
				
				// this create an internal copy of the vertices
				CollisionShape shape = new ConvexHullShape(vertices);
				collisionShapes.add(shape);

				//btRigidBody* body = m_demoApp->localCreateRigidBody(mass, startTransform,shape);
				localCreateRigidBody(mass, startTransform, shape);
			}
		}
	}
	
}
