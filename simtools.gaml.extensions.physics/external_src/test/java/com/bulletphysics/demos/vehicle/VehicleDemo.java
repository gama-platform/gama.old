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

package com.bulletphysics.demos.vehicle;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.collision.shapes.CylinderShapeX;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.bulletphysics.demos.opengl.DemoApplication;
import com.bulletphysics.demos.opengl.GLDebugDrawer;
import com.bulletphysics.demos.opengl.GLShapeDrawer;
import com.bulletphysics.demos.opengl.IGL;
import com.bulletphysics.demos.opengl.LWJGL;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.dynamics.vehicle.DefaultVehicleRaycaster;
import com.bulletphysics.dynamics.vehicle.RaycastVehicle;
import com.bulletphysics.dynamics.vehicle.VehicleRaycaster;
import com.bulletphysics.dynamics.vehicle.VehicleTuning;
import com.bulletphysics.dynamics.vehicle.WheelInfo;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;
import javax.vecmath.Vector3f;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;

/**
 * VehicleDemo shows how to setup and use the built-in raycast vehicle.
 * 
 * @author jezek2
 */
public class VehicleDemo extends DemoApplication {

	// By default, Bullet Vehicle uses Y as up axis.
	// You can override the up axis, for example Z-axis up. Enable this define to see how to:
	// //#define FORCE_ZAXIS_UP 1
	
	//#ifdef FORCE_ZAXIS_UP
	//int rightIndex = 0; 
	//int upIndex = 2; 
	//int forwardIndex = 1;
	//btVector3 wheelDirectionCS0(0,0,-1);
	//btVector3 wheelAxleCS(1,0,0);
	//#else
	private static final int rightIndex = 0;
	private static final int upIndex = 1;
	private static final int forwardIndex = 2;
	private static final Vector3f wheelDirectionCS0 = new Vector3f(0,-1,0);
	private static final Vector3f wheelAxleCS = new Vector3f(-1,0,0);
	//#endif
	
	private static final int maxProxies = 32766;
	private static final int maxOverlap = 65535;

	// RaycastVehicle is the interface for the constraint that implements the raycast vehicle
	// notice that for higher-quality slow-moving vehicles, another approach might be better
	// implementing explicit hinged-wheel constraints with cylinder collision, rather then raycasts
	private static float gEngineForce = 0.f;
	private static float gBreakingForce = 0.f;

	private static float maxEngineForce = 1000.f;//this should be engine/velocity dependent
	private static float maxBreakingForce = 100.f;

	private static float gVehicleSteering = 0.f;
	private static float steeringIncrement = 0.04f;
	private static float steeringClamp = 0.3f;
	private static float wheelRadius = 0.5f;
	private static float wheelWidth = 0.4f;
	private static float wheelFriction = 1000;//1e30f;
	private static float suspensionStiffness = 20.f;
	private static float suspensionDamping = 2.3f;
	private static float suspensionCompression = 4.4f;
	private static float rollInfluence = 0.1f;//1.0f;

	private static final float suspensionRestLength = 0.6f;

	private static final int CUBE_HALF_EXTENTS = 1;
	
	////////////////////////////////////////////////////////////////////////////
	
	public RigidBody carChassis;
	public ObjectArrayList<CollisionShape> collisionShapes = new ObjectArrayList<CollisionShape>();
	public BroadphaseInterface overlappingPairCache;
	public CollisionDispatcher dispatcher;
	public ConstraintSolver constraintSolver;
	public DefaultCollisionConfiguration collisionConfiguration;
	public TriangleIndexVertexArray indexVertexArrays;

	public ByteBuffer vertices;

	public VehicleTuning tuning = new VehicleTuning();
	public VehicleRaycaster vehicleRayCaster;
	public RaycastVehicle vehicle;

	public float cameraHeight;

	public float minCameraDistance;
	public float maxCameraDistance;

	public VehicleDemo(IGL gl) {
		super(gl);
		carChassis = null;
		cameraHeight = 4f;
		minCameraDistance = 3f;
		maxCameraDistance = 10f;
		indexVertexArrays = null;
		vertices = null;
		vehicle = null;
		cameraPosition.set(30, 30, 30);
	}

	public void initPhysics() {
		//#ifdef FORCE_ZAXIS_UP
		//m_cameraUp = btVector3(0,0,1);
		//m_forwardAxis = 1;
		//#endif

		CollisionShape groundShape = new BoxShape(new Vector3f(50, 3, 50));
		collisionShapes.add(groundShape);
		collisionConfiguration = new DefaultCollisionConfiguration();
		dispatcher = new CollisionDispatcher(collisionConfiguration);
		Vector3f worldMin = new Vector3f(-1000, -1000, -1000);
		Vector3f worldMax = new Vector3f(1000, 1000, 1000);
		//overlappingPairCache = new AxisSweep3(worldMin, worldMax);
		//overlappingPairCache = new SimpleBroadphase();
		overlappingPairCache = new DbvtBroadphase();
		constraintSolver = new SequentialImpulseConstraintSolver();
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, constraintSolver, collisionConfiguration);
		//#ifdef FORCE_ZAXIS_UP
		//dynamicsWorld.setGravity(new Vector3f(0, 0, -10));
		//#endif 

		//m_dynamicsWorld->setGravity(btVector3(0,0,0));
		Transform tr = new Transform();
		tr.setIdentity();

		// either use heightfield or triangle mesh
		//#define  USE_TRIMESH_GROUND 1
		//#ifdef USE_TRIMESH_GROUND

		final float TRIANGLE_SIZE = 20f;

		// create a triangle-mesh ground
		int vertStride = 4 * 3 /* sizeof(btVector3) */;
		int indexStride = 3 * 4 /* 3*sizeof(int) */;

		final int NUM_VERTS_X = 20;
		final int NUM_VERTS_Y = 20;
		final int totalVerts = NUM_VERTS_X * NUM_VERTS_Y;

		final int totalTriangles = 2 * (NUM_VERTS_X - 1) * (NUM_VERTS_Y - 1);

		vertices = ByteBuffer.allocateDirect(totalVerts * vertStride).order(ByteOrder.nativeOrder());
		ByteBuffer gIndices = ByteBuffer.allocateDirect(totalTriangles * 3 * 4).order(ByteOrder.nativeOrder());

		Vector3f tmp = new Vector3f();
		for (int i = 0; i < NUM_VERTS_X; i++) {
			for (int j = 0; j < NUM_VERTS_Y; j++) {
				float wl = 0.2f;
				// height set to zero, but can also use curved landscape, just uncomment out the code
				float height = 0f; // 20f * (float)Math.sin(i * wl) * (float)Math.cos(j * wl);
				
				//#ifdef FORCE_ZAXIS_UP
				//m_vertices[i+j*NUM_VERTS_X].setValue(
				//	(i-NUM_VERTS_X*0.5f)*TRIANGLE_SIZE,
				//	(j-NUM_VERTS_Y*0.5f)*TRIANGLE_SIZE,
				//	height
				//	);
				//#else
				tmp.set(
						(i - NUM_VERTS_X * 0.5f) * TRIANGLE_SIZE,
						height,
						(j - NUM_VERTS_Y * 0.5f) * TRIANGLE_SIZE);

				int index = i + j * NUM_VERTS_X;
				vertices.putFloat((index * 3 + 0) * 4, tmp.x);
				vertices.putFloat((index * 3 + 1) * 4, tmp.y);
				vertices.putFloat((index * 3 + 2) * 4, tmp.z);
				//#endif
			}
		}

		//int index=0;
		gIndices.clear();
		for (int i = 0; i < NUM_VERTS_X - 1; i++) {
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
				totalVerts, vertices, vertStride);

		boolean useQuantizedAabbCompression = true;
		groundShape = new BvhTriangleMeshShape(indexVertexArrays, useQuantizedAabbCompression);

		tr.origin.set(0, -4.5f, 0);

		//#else
//		//testing btHeightfieldTerrainShape
//		int width=128;
//		int length=128;
//		unsigned char* heightfieldData = new unsigned char[width*length];
//		{
//			for (int i=0;i<width*length;i++)
//			{
//				heightfieldData[i]=0;
//			}
//		}
//
//		char*	filename="heightfield128x128.raw";
//		FILE* heightfieldFile = fopen(filename,"r");
//		if (!heightfieldFile)
//		{
//			filename="../../heightfield128x128.raw";
//			heightfieldFile = fopen(filename,"r");
//		}
//		if (heightfieldFile)
//		{
//			int numBytes =fread(heightfieldData,1,width*length,heightfieldFile);
//			//btAssert(numBytes);
//			if (!numBytes)
//			{
//				printf("couldn't read heightfield at %s\n",filename);
//			}
//			fclose (heightfieldFile);
//		}
//
//
//		btScalar maxHeight = 20000.f;
//
//		bool useFloatDatam=false;
//		bool flipQuadEdges=false;
//
//		btHeightfieldTerrainShape* heightFieldShape = new btHeightfieldTerrainShape(width,length,heightfieldData,maxHeight,upIndex,useFloatDatam,flipQuadEdges);;
//		groundShape = heightFieldShape;
//
//		heightFieldShape->setUseDiamondSubdivision(true);
//
//		btVector3 localScaling(20,20,20);
//		localScaling[upIndex]=1.f;
//		groundShape->setLocalScaling(localScaling);
//
//		tr.setOrigin(btVector3(0,-64.5f,0));
//
		//#endif

		collisionShapes.add(groundShape);

		// create ground object
		localCreateRigidBody(0, tr, groundShape);

		//#ifdef FORCE_ZAXIS_UP
		//	//   indexRightAxis = 0; 
		//	//   indexUpAxis = 2; 
		//	//   indexForwardAxis = 1; 
		//btCollisionShape* chassisShape = new btBoxShape(btVector3(1.f,2.f, 0.5f));
		//btCompoundShape* compound = new btCompoundShape();
		//btTransform localTrans;
		//localTrans.setIdentity();
		// //localTrans effectively shifts the center of mass with respect to the chassis
		//localTrans.setOrigin(btVector3(0,0,1));
		//#else
		CollisionShape chassisShape = new BoxShape(new Vector3f(1.0f, 0.5f, 2.0f));
		collisionShapes.add(chassisShape);

		CompoundShape compound = new CompoundShape();
		collisionShapes.add(compound);
		Transform localTrans = new Transform();
		localTrans.setIdentity();
		// localTrans effectively shifts the center of mass with respect to the chassis
		localTrans.origin.set(0, 1, 0);
		//#endif

		compound.addChildShape(localTrans, chassisShape);

		tr.origin.set(0, 0, 0);

		carChassis = localCreateRigidBody(800, tr, compound); //chassisShape);
		//m_carChassis->setDamping(0.2,0.2);

		clientResetScene();

		// create vehicle
		{
			vehicleRayCaster = new DefaultVehicleRaycaster(dynamicsWorld);
			vehicle = new RaycastVehicle(tuning, carChassis, vehicleRayCaster);

			// never deactivate the vehicle
			carChassis.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

			dynamicsWorld.addVehicle(vehicle);

			float connectionHeight = 1.2f;

			boolean isFrontWheel = true;

			// choose coordinate system
			vehicle.setCoordinateSystem(rightIndex, upIndex, forwardIndex);

			//#ifdef FORCE_ZAXIS_UP
			//btVector3 connectionPointCS0(CUBE_HALF_EXTENTS-(0.3*wheelWidth),2*CUBE_HALF_EXTENTS-wheelRadius, connectionHeight);
			//#else
			Vector3f connectionPointCS0 = new Vector3f(CUBE_HALF_EXTENTS - (0.3f * wheelWidth), connectionHeight, 2f * CUBE_HALF_EXTENTS - wheelRadius);
			//#endif

			vehicle.addWheel(connectionPointCS0, wheelDirectionCS0, wheelAxleCS, suspensionRestLength, wheelRadius, tuning, isFrontWheel);
			//#ifdef FORCE_ZAXIS_UP
			//connectionPointCS0 = btVector3(-CUBE_HALF_EXTENTS+(0.3*wheelWidth),2*CUBE_HALF_EXTENTS-wheelRadius, connectionHeight);
			//#else
			connectionPointCS0.set(-CUBE_HALF_EXTENTS + (0.3f * wheelWidth), connectionHeight, 2f * CUBE_HALF_EXTENTS - wheelRadius);
			//#endif

			vehicle.addWheel(connectionPointCS0, wheelDirectionCS0, wheelAxleCS, suspensionRestLength, wheelRadius, tuning, isFrontWheel);
			//#ifdef FORCE_ZAXIS_UP
			//connectionPointCS0 = btVector3(-CUBE_HALF_EXTENTS+(0.3*wheelWidth),-2*CUBE_HALF_EXTENTS+wheelRadius, connectionHeight);
			//#else
			connectionPointCS0.set(-CUBE_HALF_EXTENTS + (0.3f * wheelWidth), connectionHeight, -2f * CUBE_HALF_EXTENTS + wheelRadius);
			//#endif //FORCE_ZAXIS_UP
			isFrontWheel = false;
			vehicle.addWheel(connectionPointCS0, wheelDirectionCS0, wheelAxleCS, suspensionRestLength, wheelRadius, tuning, isFrontWheel);
			//#ifdef FORCE_ZAXIS_UP
			//connectionPointCS0 = btVector3(CUBE_HALF_EXTENTS-(0.3*wheelWidth),-2*CUBE_HALF_EXTENTS+wheelRadius, connectionHeight);
			//#else
			connectionPointCS0.set(CUBE_HALF_EXTENTS - (0.3f * wheelWidth), connectionHeight, -2f * CUBE_HALF_EXTENTS + wheelRadius);
			//#endif
			vehicle.addWheel(connectionPointCS0, wheelDirectionCS0, wheelAxleCS, suspensionRestLength, wheelRadius, tuning, isFrontWheel);

			for (int i = 0; i < vehicle.getNumWheels(); i++) {
				WheelInfo wheel = vehicle.getWheelInfo(i);
				wheel.suspensionStiffness = suspensionStiffness;
				wheel.wheelsDampingRelaxation = suspensionDamping;
				wheel.wheelsDampingCompression = suspensionCompression;
				wheel.frictionSlip = wheelFriction;
				wheel.rollInfluence = rollInfluence;
			}
		}

		setCameraDistance(26.f);
	}
	
	// to be implemented by the demo
	@Override
	public void renderme() {
		updateCamera();

		CylinderShapeX wheelShape = new CylinderShapeX(new Vector3f(wheelWidth, wheelRadius, wheelRadius));
		Vector3f wheelColor = new Vector3f(1, 0, 0);

		for (int i = 0; i < vehicle.getNumWheels(); i++) {
			// synchronize the wheels with the (interpolated) chassis worldtransform
			vehicle.updateWheelTransform(i, true);
			// draw wheels (cylinders)
			Transform trans = vehicle.getWheelInfo(i).worldTransform;
			GLShapeDrawer.drawOpenGL(gl, trans, wheelShape, wheelColor, getDebugMode());
		}

		super.renderme();
	}
	
	@Override
	public void clientMoveAndDisplay() {
		gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT); 

		{			
			int wheelIndex = 2;
			vehicle.applyEngineForce(gEngineForce,wheelIndex);
			vehicle.setBrake(gBreakingForce,wheelIndex);
			wheelIndex = 3;
			vehicle.applyEngineForce(gEngineForce,wheelIndex);
			vehicle.setBrake(gBreakingForce,wheelIndex);

			wheelIndex = 0;
			vehicle.setSteeringValue(gVehicleSteering,wheelIndex);
			wheelIndex = 1;
			vehicle.setSteeringValue(gVehicleSteering,wheelIndex);
		}

		float dt = getDeltaTimeMicroseconds() * 0.000001f;
		
		if (dynamicsWorld != null)
		{
			// during idle mode, just run 1 simulation step maximum
			int maxSimSubSteps = idle ? 1 : 2;
			if (idle)
				dt = 1f/420f;

			int numSimSteps = dynamicsWorld.stepSimulation(dt,maxSimSubSteps);

			//#define VERBOSE_FEEDBACK
			//#ifdef VERBOSE_FEEDBACK
			//if (!numSimSteps)
			//	printf("Interpolated transforms\n");
			//else
			//{
			//	if (numSimSteps > maxSimSubSteps)
			//	{
			//		//detect dropping frames
			//		printf("Dropped (%i) simulation steps out of %i\n",numSimSteps - maxSimSubSteps,numSimSteps);
			//	} else
			//	{
			//		printf("Simulated (%i) steps\n",numSimSteps);
			//	}
			//}
			//#endif //VERBOSE_FEEDBACK
		}

		//#ifdef USE_QUICKPROF 
		//btProfiler::beginBlock("render"); 
		//#endif //USE_QUICKPROF 

		renderme(); 
		
		// optional but useful: debug drawing
		if (dynamicsWorld != null) {
			dynamicsWorld.debugDrawWorld();
		}

		//#ifdef USE_QUICKPROF 
		//btProfiler::endBlock("render"); 
		//#endif 
	}

	@Override
	public void displayCallback() {
		gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT);

		renderme();
		
		// optional but useful: debug drawing
		if (dynamicsWorld != null) {
			dynamicsWorld.debugDrawWorld();
		}
	}

	@Override
	public void clientResetScene() {
		gVehicleSteering = 0f;
		Transform tr = new Transform();
		tr.setIdentity();
		carChassis.setCenterOfMassTransform(tr);
		carChassis.setLinearVelocity(new Vector3f(0, 0, 0));
		carChassis.setAngularVelocity(new Vector3f(0, 0, 0));
		dynamicsWorld.getBroadphase().getOverlappingPairCache().cleanProxyFromPairs(carChassis.getBroadphaseHandle(), getDynamicsWorld().getDispatcher());
		if (vehicle != null) {
			vehicle.resetSuspension();
			for (int i = 0; i < vehicle.getNumWheels(); i++) {
				// synchronize the wheels with the (interpolated) chassis worldtransform
				vehicle.updateWheelTransform(i, true);
			}
		}
	}

	@Override
	public void specialKeyboardUp(int key, int x, int y, int modifiers) {
		switch (key) {
			case Keyboard.KEY_UP: {
				gEngineForce = 0f;
				break;
			}
			case Keyboard.KEY_DOWN: {
				gBreakingForce = 0f;
				break;
			}
			default:
				super.specialKeyboardUp(key, x, y, modifiers);
				break;
		}
	}

	@Override
	public void specialKeyboard(int key, int x, int y, int modifiers) {
		//	printf("key = %i x=%i y=%i\n",key,x,y);

		switch (key) {
			case Keyboard.KEY_LEFT: {
				gVehicleSteering += steeringIncrement;
				if (gVehicleSteering > steeringClamp) {
					gVehicleSteering = steeringClamp;
				}
				break;
			}
			case Keyboard.KEY_RIGHT: {
				gVehicleSteering -= steeringIncrement;
				if (gVehicleSteering < -steeringClamp) {
					gVehicleSteering = -steeringClamp;
				}
				break;
			}
			case Keyboard.KEY_UP: {
				gEngineForce = maxEngineForce;
				gBreakingForce = 0.f;
				break;
			}
			case Keyboard.KEY_DOWN: {
				gBreakingForce = maxBreakingForce;
				gEngineForce = 0.f;
				break;
			}
			default:
				super.specialKeyboard(key, x, y, modifiers);
				break;
		}

		//glutPostRedisplay();
	}

	@Override
	public void updateCamera()
	{

		// //#define DISABLE_CAMERA 1
		//#ifdef DISABLE_CAMERA
		//DemoApplication::updateCamera();
		//return;
		//#endif //DISABLE_CAMERA

		gl.glMatrixMode(gl.GL_PROJECTION);
		gl.glLoadIdentity();

		Transform chassisWorldTrans = new Transform();

		// look at the vehicle
		carChassis.getMotionState().getWorldTransform(chassisWorldTrans);
		cameraTargetPosition.set(chassisWorldTrans.origin);

		// interpolate the camera height
		//#ifdef FORCE_ZAXIS_UP
		//m_cameraPosition[2] = (15.0*m_cameraPosition[2] + m_cameraTargetPosition[2] + m_cameraHeight)/16.0;
		//#else
		cameraPosition.y = (15.0f*cameraPosition.y + cameraTargetPosition.y + cameraHeight) / 16.0f;
		//#endif

		Vector3f camToObject = new Vector3f();
		camToObject.sub(cameraTargetPosition, cameraPosition);

		// keep distance between min and max distance
		float cameraDistance = camToObject.length();
		float correctionFactor = 0f;
		if (cameraDistance < minCameraDistance)
		{
			correctionFactor = 0.15f*(minCameraDistance-cameraDistance)/cameraDistance;
		}
		if (cameraDistance > maxCameraDistance)
		{
			correctionFactor = 0.15f*(maxCameraDistance-cameraDistance)/cameraDistance;
		}
		Vector3f tmp = new Vector3f();
		tmp.scale(correctionFactor, camToObject);
		cameraPosition.sub(tmp);

		// update OpenGL camera settings
		gl.glFrustum(-1.0, 1.0, -1.0, 1.0, 1.0, 10000.0);

		gl.glMatrixMode(IGL.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.gluLookAt(cameraPosition.x,cameraPosition.y,cameraPosition.z,
				  cameraTargetPosition.x,cameraTargetPosition.y, cameraTargetPosition.z,
				  cameraUp.x,cameraUp.y,cameraUp.z);
	}
	
	public static void main(String[] args) throws LWJGLException {
		VehicleDemo vehicleDemo = new VehicleDemo(LWJGL.getGL());
		vehicleDemo.initPhysics();
		vehicleDemo.getDynamicsWorld().setDebugDrawer(new GLDebugDrawer(LWJGL.getGL()));

		LWJGL.main(args, 800, 600, "Bullet Vehicle Demo", vehicleDemo);
	}
	
}
