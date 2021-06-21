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

package com.bulletphysics.demos.concaveconvexcast;

import com.bulletphysics.util.ObjectArrayList;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import com.bulletphysics.BulletStats;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.bulletphysics.demos.opengl.DemoApplication;
import com.bulletphysics.demos.opengl.GLDebugDrawer;
import com.bulletphysics.demos.opengl.IGL;
import com.bulletphysics.demos.opengl.LWJGL;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Transform;
import javax.vecmath.Vector3f;
import org.lwjgl.LWJGLException;
import static com.bulletphysics.demos.opengl.IGL.*;

/**
 * 
 * @author jezek2
 */
public class ConcaveConvexcastDemo extends DemoApplication {

	// keep the collision shapes, for deletion/cleanup
	private ObjectArrayList<CollisionShape> collisionShapes = new ObjectArrayList<CollisionShape>();
	private TriangleIndexVertexArray indexVertexArrays;
	private BroadphaseInterface broadphase;
	private CollisionDispatcher dispatcher;
	private ConstraintSolver solver;
	private DefaultCollisionConfiguration collisionConfiguration;
	private boolean animatedMesh = false;
	
	private static ByteBuffer gVertices;
	private static ByteBuffer gIndices;
	private static BvhTriangleMeshShape trimeshShape;
	private static RigidBody staticBody;
	private static float waveheight = 5.f;

	private static final float TRIANGLE_SIZE=8.f;
	private static int NUM_VERTS_X = 30;
	private static int NUM_VERTS_Y = 30;
	private static int totalVerts = NUM_VERTS_X*NUM_VERTS_Y;

	private ConvexcastBatch convexcastBatch;

	public ConcaveConvexcastDemo(IGL gl) {
		super(gl);
	}

	public void setVertexPositions(float waveheight, float offset) {
		int i;
		int j;
		Vector3f tmp = new Vector3f();

		for (i = 0; i < NUM_VERTS_X; i++) {
			for (j = 0; j < NUM_VERTS_Y; j++) {
				tmp.set(
						(i - NUM_VERTS_X * 0.5f) * TRIANGLE_SIZE,
						//0.f,
						waveheight * (float) Math.sin((float) i + offset) * (float) Math.cos((float) j + offset),
						(j - NUM_VERTS_Y * 0.5f) * TRIANGLE_SIZE);

				int index = i + j * NUM_VERTS_X;
				gVertices.putFloat((index*3 + 0) * 4, tmp.x);
				gVertices.putFloat((index*3 + 1) * 4, tmp.y);
				gVertices.putFloat((index*3 + 2) * 4, tmp.z);
			}
		}
	}

	@Override
	public void keyboardCallback(char key, int x, int y, int modifiers) {
		if (key == 'g') {
			animatedMesh = !animatedMesh;
			if (animatedMesh) {
				staticBody.setCollisionFlags(staticBody.getCollisionFlags() | CollisionFlags.KINEMATIC_OBJECT);
				staticBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
			}
			else {
				staticBody.setCollisionFlags(staticBody.getCollisionFlags() & ~CollisionFlags.KINEMATIC_OBJECT);
				staticBody.forceActivationState(CollisionObject.ACTIVE_TAG);
			}
		}

		super.keyboardCallback(key, x, y, modifiers);
	}

	public void initPhysics() {
		final float TRISIZE = 10f;

		//#define USE_TRIMESH_SHAPE 1
		//#ifdef USE_TRIMESH_SHAPE

		int vertStride = 3 * 4;
		int indexStride = 3 * 4;

		int totalTriangles = 2 * (NUM_VERTS_X - 1) * (NUM_VERTS_Y - 1);

		gVertices = ByteBuffer.allocateDirect(totalVerts * 3 * 4).order(ByteOrder.nativeOrder());
		gIndices = ByteBuffer.allocateDirect(totalTriangles * 3 * 4).order(ByteOrder.nativeOrder());

		int i;

		setVertexPositions(waveheight, 0.f);

		//int index=0;
		gIndices.clear();
		for (i = 0; i < NUM_VERTS_X - 1; i++) {
			for (int j = 0; j < NUM_VERTS_Y - 1; j++) {
				gIndices.putInt(j * NUM_VERTS_X + i);
				gIndices.putInt(j * NUM_VERTS_X + i + 1);
				gIndices.putInt((j + 1) * NUM_VERTS_X + i + 1);

				gIndices.putInt(j * NUM_VERTS_X + i);
				gIndices.putInt((j + 1) * NUM_VERTS_X + i + 1);
				gIndices.putInt((j + 1) * NUM_VERTS_X + i);
			}
		}
		gIndices.flip();

		indexVertexArrays = new TriangleIndexVertexArray(totalTriangles,
				gIndices,
				indexStride,
				totalVerts, gVertices, vertStride);

		boolean useQuantizedAabbCompression = true;

		//comment out the next line to read the BVH from disk (first run the demo once to create the BVH)
		//#define SERIALIZE_TO_DISK 1
		//#ifdef SERIALIZE_TO_DISK
		trimeshShape = new BvhTriangleMeshShape(indexVertexArrays, useQuantizedAabbCompression);
		collisionShapes.add(trimeshShape);

		CollisionShape groundShape = trimeshShape;

		collisionConfiguration = new DefaultCollisionConfiguration();

		dispatcher = new CollisionDispatcher(collisionConfiguration);

		Vector3f worldMin = new Vector3f(-1000f, -1000f, -1000f);
		Vector3f worldMax = new Vector3f(1000f, 1000f, 1000f);
		//broadphase = new AxisSweep3(worldMin, worldMax);
		broadphase = new DbvtBroadphase();
		solver = new SequentialImpulseConstraintSolver();
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);

		// JAVA NOTE: added
		dynamicsWorld.setDebugDrawer(new GLDebugDrawer(gl));
		
		float mass = 0f;
		Transform startTransform = new Transform();
		startTransform.setIdentity();
		startTransform.origin.set(0f, -2f, 0f);

		CollisionShape colShape = new BoxShape(new Vector3f(1f, 1f, 1f));
		collisionShapes.add(colShape);

		{
			for (i = 0; i < 10; i++) {
				//btCollisionShape* colShape = new btCapsuleShape(0.5,2.0);//boxShape = new btSphereShape(1.f);
				startTransform.origin.set(2f * i, 10f, 1f);
				localCreateRigidBody(1f, startTransform, colShape);
			}
		}

		startTransform.setIdentity();
		staticBody = localCreateRigidBody(mass, startTransform, groundShape);

		staticBody.setCollisionFlags(staticBody.getCollisionFlags() | CollisionFlags.STATIC_OBJECT);

		// enable custom material callback
		staticBody.setCollisionFlags(staticBody.getCollisionFlags() | CollisionFlags.CUSTOM_MATERIAL_CALLBACK);

		convexcastBatch = new ConvexcastBatch(40f, 0f, -10f, 10f);
	}

	private static float offset = 0f;
	
	@Override
	public void clientMoveAndDisplay() {
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		float dt = getDeltaTimeMicroseconds() * 0.000001f;

		if (animatedMesh) {
			long t0 = System.nanoTime();
			
			offset += 0.01f;

			setVertexPositions(waveheight, offset);

			// JAVA NOTE: 2.70b1: replace with proper code
			trimeshShape.refitTree(null, null);

			// clear all contact points involving mesh proxy. Note: this is a slow/unoptimized operation.
			dynamicsWorld.getBroadphase().getOverlappingPairCache().cleanProxyFromPairs(staticBody.getBroadphaseHandle(), getDynamicsWorld().getDispatcher());
			
			BulletStats.updateTime = (System.nanoTime() - t0) / 1000000;
		}

		dynamicsWorld.stepSimulation(dt);

		// optional but useful: debug drawing
		dynamicsWorld.debugDrawWorld();

		convexcastBatch.move(dt);
		convexcastBatch.cast(dynamicsWorld);

		renderme();
		convexcastBatch.draw(gl);

		//glFlush();
		//glutSwapBuffers();
	}

	@Override
	public void displayCallback() {
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		renderme();
		convexcastBatch.draw(gl);

		// optional but useful: debug drawing
		if (dynamicsWorld != null) {
			dynamicsWorld.debugDrawWorld();
		}
		
		//glFlush();
		//glutSwapBuffers();
	}
	
	public static void main(String[] args) throws LWJGLException {
		ConcaveConvexcastDemo concaveConvexcastDemo = new ConcaveConvexcastDemo(LWJGL.getGL());
		concaveConvexcastDemo.initPhysics();
		concaveConvexcastDemo.setCameraDistance(30f);

		LWJGL.main(args, 800, 600, "Concave Convexcast Demo", concaveConvexcastDemo);
	}
	
}
