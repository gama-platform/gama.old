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

package com.bulletphysics.demos.concave;

import com.bulletphysics.util.ObjectArrayList;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import com.bulletphysics.BulletGlobals;
import com.bulletphysics.BulletStats;
import com.bulletphysics.ContactAddedCallback;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.collision.shapes.CylinderShapeX;
import com.bulletphysics.collision.shapes.OptimizedBvh;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.bulletphysics.demos.opengl.DemoApplication;
import com.bulletphysics.demos.opengl.GLDebugDrawer;
import com.bulletphysics.demos.opengl.IGL;
import com.bulletphysics.demos.opengl.LWJGL;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import org.lwjgl.LWJGLException;
import static com.bulletphysics.demos.opengl.IGL.*;

/**
 *
 * @author jezek2
 */
public class ConcaveDemo extends DemoApplication {

	// enable to test serialization of BVH to speedup loading:
	private static final boolean TEST_SERIALIZATION = false;
	// set to false to read the BVH from disk (first run the demo once to create the BVH):
	private static final boolean SERIALIZE_TO_DISK  = true;

	private static final boolean USE_BOX_SHAPE = false;

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

	public ConcaveDemo(IGL gl) {
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

		BulletGlobals.setContactAddedCallback(new CustomMaterialCombinerCallback());

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

		if (TEST_SERIALIZATION) {
			if (SERIALIZE_TO_DISK) {
				trimeshShape = new BvhTriangleMeshShape(indexVertexArrays, useQuantizedAabbCompression);
				collisionShapes.add(trimeshShape);

				// we can serialize the BVH data
				try {
					ObjectOutputStream out = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(new File("bvh.bin"))));
					out.writeObject(trimeshShape.getOptimizedBvh());
					out.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
				trimeshShape = new BvhTriangleMeshShape(indexVertexArrays, useQuantizedAabbCompression, false);

				OptimizedBvh bvh = null;
				try {
					ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(new FileInputStream(new File("bvh.bin"))));
					bvh = (OptimizedBvh)in.readObject();
					in.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}

				trimeshShape.setOptimizedBvh(bvh);
				trimeshShape.recalcLocalAabb();
			}
		}
		else {
			trimeshShape = new BvhTriangleMeshShape(indexVertexArrays, useQuantizedAabbCompression);
			collisionShapes.add(trimeshShape);
		}

		CollisionShape groundShape = trimeshShape;

		//#else
		//btCollisionShape* groundShape = new btBoxShape(btVector3(50,3,50));
		//m_collisionShapes.push_back(groundShape);
		//#endif //USE_TRIMESH_SHAPE

		collisionConfiguration = new DefaultCollisionConfiguration();

//		//#ifdef USE_PARALLEL_DISPATCHER
//		#ifdef USE_WIN32_THREADING
//
//		int maxNumOutstandingTasks = 4;//number of maximum outstanding tasks
//		Win32ThreadSupport* threadSupport = new Win32ThreadSupport(Win32ThreadSupport::Win32ThreadConstructionInfo(
//									"collision",
//									processCollisionTask,
//									createCollisionLocalStoreMemory,
//									maxNumOutstandingTasks));
//		#else
//		///todo other platform threading
//		///Playstation 3 SPU (SPURS)  version is available through PS3 Devnet
//		///Libspe2 SPU support will be available soon
//		///pthreads version
//		///you can hook it up to your custom task scheduler by deriving from btThreadSupportInterface
//		#endif
//
//		m_dispatcher = new	SpuGatheringCollisionDispatcher(threadSupport,maxNumOutstandingTasks,m_collisionConfiguration);
//		#else
		dispatcher = new CollisionDispatcher(collisionConfiguration);
		//#endif//USE_PARALLEL_DISPATCHER

		Vector3f worldMin = new Vector3f(-1000f, -1000f, -1000f);
		Vector3f worldMax = new Vector3f(1000f, 1000f, 1000f);
		//broadphase = new AxisSweep3(worldMin, worldMax);
		broadphase = new DbvtBroadphase();
		//broadphase = new SimpleBroadphase();
		solver = new SequentialImpulseConstraintSolver();
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
		//#ifdef USE_PARALLEL_DISPATCHER
		//m_dynamicsWorld->getDispatchInfo().m_enableSPU=true;
		//#endif //USE_PARALLEL_DISPATCHER

		// JAVA NOTE: added
		dynamicsWorld.setDebugDrawer(new GLDebugDrawer(gl));
		
		float mass = 0f;
		Transform startTransform = new Transform();
		startTransform.setIdentity();
		startTransform.origin.set(0f, -2f, 0f);

		CollisionShape colShape;

		if (USE_BOX_SHAPE) {
			colShape = new BoxShape(new Vector3f(1f, 1f, 1f));
		}
		else {
			colShape = new CompoundShape();
			CollisionShape cylinderShape = new CylinderShapeX(new Vector3f(4, 1, 1));
			CollisionShape boxShape = new BoxShape(new Vector3f(4f, 1f, 1f));
			Transform localTransform = new Transform();
			localTransform.setIdentity();
			((CompoundShape)colShape).addChildShape(localTransform, boxShape);
			Quat4f orn = new Quat4f();
			QuaternionUtil.setEuler(orn, BulletGlobals.SIMD_HALF_PI, 0f, 0f);
			localTransform.setRotation(orn);
			((CompoundShape)colShape).addChildShape(localTransform, cylinderShape);
		}

		collisionShapes.add(colShape);

		{
			for (i = 0; i < 10; i++) {
				//btCollisionShape* colShape = new btCapsuleShape(0.5,2.0);//boxShape = new btSphereShape(1.f);
				startTransform.origin.set(2f, 10f + i*2f, 1f);
				localCreateRigidBody(1f, startTransform, colShape);
			}
		}

		startTransform.setIdentity();
		staticBody = localCreateRigidBody(mass, startTransform, groundShape);

		staticBody.setCollisionFlags(staticBody.getCollisionFlags() | CollisionFlags.STATIC_OBJECT);

		// enable custom material callback
		staticBody.setCollisionFlags(staticBody.getCollisionFlags() | CollisionFlags.CUSTOM_MATERIAL_CALLBACK);
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

		renderme();

		//glFlush();
		//glutSwapBuffers();
	}

	@Override
	public void displayCallback() {
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		renderme();

		// optional but useful: debug drawing
		if (dynamicsWorld != null) {
			dynamicsWorld.debugDrawWorld();
		}
		
		//glFlush();
		//glutSwapBuffers();
	}

	/**
	 * User can override this material combiner by implementing gContactAddedCallback
	 * and setting body0->m_collisionFlags |= btCollisionObject::customMaterialCallback
	 */
	private static float calculateCombinedFriction(float friction0, float friction1) {
		float friction = friction0 * friction1;

		float MAX_FRICTION = 10f;
		if (friction < -MAX_FRICTION) {
			friction = -MAX_FRICTION;
		}
		if (friction > MAX_FRICTION) {
			friction = MAX_FRICTION;
		}
		return friction;
	}

	private static float calculateCombinedRestitution(float restitution0, float restitution1) {
		return restitution0 * restitution1;
	}
	
	private static class CustomMaterialCombinerCallback extends ContactAddedCallback {
		public boolean contactAdded(ManifoldPoint cp, CollisionObject colObj0, int partId0, int index0, CollisionObject colObj1, int partId1, int index1) {
			float friction0 = colObj0.getFriction();
			float friction1 = colObj1.getFriction();
			float restitution0 = colObj0.getRestitution();
			float restitution1 = colObj1.getRestitution();

			if ((colObj0.getCollisionFlags() & CollisionFlags.CUSTOM_MATERIAL_CALLBACK) != 0) {
				friction0 = 1f; //partId0,index0
				restitution0 = 0f;
			}
			if ((colObj1.getCollisionFlags() & CollisionFlags.CUSTOM_MATERIAL_CALLBACK) != 0) {
				if ((index1 & 1) != 0) {
					friction1 = 1f; //partId1,index1
				}
				else {
					friction1 = 0f;
				}
				restitution1 = 0f;
			}

			cp.combinedFriction = calculateCombinedFriction(friction0, friction1);
			cp.combinedRestitution = calculateCombinedRestitution(restitution0, restitution1);

			// this return value is currently ignored, but to be on the safe side: return false if you don't calculate friction
			return true;
		}
	}
	
	public static void main(String[] args) throws LWJGLException {
		ConcaveDemo concaveDemo = new ConcaveDemo(LWJGL.getGL());
		concaveDemo.initPhysics();
		concaveDemo.setCameraDistance(30f);

		LWJGL.main(args, 800, 600, "Static Concave Mesh Demo", concaveDemo);
	}
	
}
