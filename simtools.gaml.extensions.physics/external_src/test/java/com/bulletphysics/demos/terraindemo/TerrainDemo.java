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

package com.bulletphysics.demos.terraindemo;

import static com.bulletphysics.demos.opengl.IGL.GL_COLOR_BUFFER_BIT;
import static com.bulletphysics.demos.opengl.IGL.GL_DEPTH_BUFFER_BIT;

import java.nio.ByteBuffer;
import java.util.Random;

import javax.vecmath.Vector3f;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.broadphase.SimpleBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.HeightfieldTerrainShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.demos.opengl.DemoApplication;
import com.bulletphysics.demos.opengl.GLDebugDrawer;
import com.bulletphysics.demos.opengl.IGL;
import com.bulletphysics.demos.opengl.LWJGL;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.VectorUtil;
import com.bulletphysics.util.ObjectArrayList;
import org.lwjgl.LWJGLException;

public class TerrainDemo extends DemoApplication
{
	public static void main(String[] args) throws LWJGLException {
		TerrainDemo terrainDemo = new TerrainDemo(LWJGL.getGL());
		terrainDemo.initPhysics();
		terrainDemo.getDynamicsWorld().setDebugDrawer(new GLDebugDrawer(LWJGL.getGL()));
		LWJGL.main(args, 800, 600, "Bullet Terrain Demo", terrainDemo);
	}

	public TerrainDemo(IGL gl)
	{
		super(gl);
		m_collisionConfiguration = null;
		m_dispatcher = null;
		m_broadphase = null;
		m_solver = null;
		m_upAxis = 1;
		m_type = HeightfieldTerrainShape.PHY_ScalarType.PHY_FLOAT;
		m_model = eTerrainModel.eFractal;
		m_rawHeightfieldData = null;
		m_phase = 0.0f;
		m_isDynamic = true;
	}

	public void initPhysics()
	{
		//	std::cerr << "initializing...\n";

		m_nearClip = 1f;
		m_farClip = 1000f;

		//m_aspect = glutScreenWidth / glutScreenHeight;
		//        m_perspective = Transform.CreatePerspectiveFieldOfView(Math.toRadians(40.0f), m_aspect, m_nearClip, m_farClip);

		// set up basic state
		m_upAxis = 1; // start with Y-axis as "up"
		m_type = HeightfieldTerrainShape.PHY_ScalarType.PHY_FLOAT;
		m_model = eTerrainModel.eFractal;
		m_isDynamic = false;

		// set up the physics world
		m_collisionConfiguration = new DefaultCollisionConfiguration();
		m_dispatcher = new CollisionDispatcher(m_collisionConfiguration);
		//m_broadphase = new AxisSweep3Internal(ref worldMin,ref worldMax);
		m_broadphase = new DbvtBroadphase();
		//m_broadphase = new SimpleBroadphase();

		m_solver = new SequentialImpulseConstraintSolver();
		dynamicsWorld = new DiscreteDynamicsWorld(m_dispatcher, m_broadphase, m_solver, m_collisionConfiguration);

		// initialize axis- or type-dependent physics from here
		clientResetScene();
	}

	/// called whenever key terrain attribute is changed
	public void clientResetScene()
	{
		super.clientResetScene();
		// remove old heightfield
		m_rawHeightfieldData = null;

		// reset gravity to point in appropriate direction
		//m_dynamicsWorld.setGravity(getUpVector(m_upAxis, 0.0f, -s_gravity));
		dynamicsWorld.setGravity(new Vector3f(0f, -10f, 0f));

		// get new heightfield of appropriate type
		m_rawHeightfieldData = getRawHeightfieldData(m_model, m_type, m_minHeight, m_maxHeight);
		assert m_rawHeightfieldData != null : "failed to create raw heightfield";

		if (m_terrainRigidBody != null)
		{
			dynamicsWorld.removeCollisionObject(m_terrainRigidBody);
			m_terrainRigidBody = null;
			m_terrainShape = null;
			m_collisionShapes.remove(m_terrainShape);
		}

		boolean flipQuadEdges = false;
		m_terrainShape = new HeightfieldTerrainShape(s_gridSize, s_gridSize, m_rawHeightfieldData, s_gridHeightScale,
				m_minHeight, m_maxHeight, m_upAxis, m_type, flipQuadEdges);
		assert m_terrainShape != null : "null heightfield";
		//m_terrainShape.setUseDiamondSubdivision(true);
		
		// scale the shape
		Vector3f localScaling = getUpVector(m_upAxis, s_gridSpacing, 1.0f);
		m_terrainShape.setLocalScaling(localScaling);

		// stash this shape away
		m_collisionShapes.add(m_terrainShape);

		// set origin to middle of heightfield
		Transform tr = new Transform();
		tr.setIdentity();
		tr.origin.set(0, -20, 0);

		// create ground object
		float mass = 0.0f;
		m_terrainRigidBody = localCreateRigidBody(mass, tr, m_terrainShape);

//		CollisionShape sphere = new SphereShape(0.5f);
//		tr = Transform.createTranslation(new Vector3f(0, 0, 0));
//
//		localCreateRigidBody(1f, tr, sphere);

	}

	public void shutdownDemo()
	{
		// delete raw heightfield data
		m_rawHeightfieldData = null;
	}

	public String getTerrainTypeName(eTerrainModel model)
	{
		switch (model)
		{
		case eRadial:
			return "Radial";

		case eFractal:
			return "Fractal";

		default:
			assert false : "bad terrain model type";
			break;
		}

		return null;
	}

	public String getDataTypeName(HeightfieldTerrainShape.PHY_ScalarType type)
	{
		switch (type)
		{
		case PHY_UCHAR:
			return "UnsignedChar";

		case PHY_SHORT:
			return "Short";

		case PHY_FLOAT:
			return "Float";

		default:
			assert false : "bad heightfield data type";
			break;
		}

		return null;
	}

	public String getUpAxisName(int axis)
	{
		switch (axis)
		{
		case 0:
			return "X";

		case 1:
			return "Y";

		case 2:
			return "Z";

		default:
			assert false : "bad up axis";
			break;
		}

		return null;
	}

	public Vector3f getUpVector(int upAxis, float regularValue, float upValue)
	{
		assert upAxis >= 0 && upAxis <= 2 : "bad up axis";

		Vector3f v = new Vector3f(regularValue, regularValue, regularValue);
		VectorUtil.setCoord(v, upAxis, upValue);

		return v;
	}

	// TODO: it would probably cleaner to have a struct per data type, so
	// 	you could lookup byte sizes, conversion functions, etc.
	public int getByteSize(HeightfieldTerrainShape.PHY_ScalarType type)
	{
		int size = 0;

		switch (type)
		{
		case PHY_FLOAT:
			size = 4;
			break;

		case PHY_UCHAR:
			size = 1;
			break;

		case PHY_SHORT:
			size = 2;
			break;

		default:
			assert false : "Bad heightfield data type";
			break;
		}

		return size;
	}

	public float convertToFloat(byte[] p, int pindex, HeightfieldTerrainShape.PHY_ScalarType type)
	{
		assert p != null;

		switch (type)
		{
		case PHY_FLOAT:
		{
			//return BitConverter.ToSingle(p,pindex);
			int size = 4;
			ByteBuffer bb = ByteBuffer.allocate(size).put(p, pindex, size);
			bb.position(0);
			return bb.getFloat();
		}

		case PHY_UCHAR:
		{
			return p[pindex] * s_gridHeightScale;
		}

		case PHY_SHORT:
		{
			int size = 2;
			ByteBuffer bb = ByteBuffer.allocate(size).put(p, pindex, size);
			bb.position(0);
			short s = bb.getShort();
			return ((s) * s_gridHeightScale);
		}

		default:
			assert false : "bad type";
			break;
		}

		return 0;
	}

	public float getGridHeight(byte[] grid, int i, int j, HeightfieldTerrainShape.PHY_ScalarType type)
	{
		assert grid != null;
		assert i >= 0 && i < s_gridSize;
		assert j >= 0 && j < s_gridSize;

		int bpe = getByteSize(type);
		assert bpe > 0 : "bad bytes per element";

		int idx = (j * s_gridSize) + i;
		int offset = ((int) bpe) * idx;

		//byte_t* p = grid + offset;

		return convertToFloat(grid, offset, type);
	}

	public static void convertFromFloat(byte[] p, int pindex, float value, HeightfieldTerrainShape.PHY_ScalarType type)
	{
		assert p != null : "null";

		switch (type)
		{
		case PHY_FLOAT:
		{
			byte[] temp = ByteBuffer.allocate(4).putFloat(value).array();
			for (int i = 0; i < temp.length; ++i)
			{
				p[pindex + i] = temp[i];
			}

		}
			break;

		case PHY_UCHAR:
		{
			p[pindex] = (byte) (value / s_gridHeightScale);
		}
			break;

		case PHY_SHORT:
		{
			short temp = (short) (value / s_gridHeightScale);
			p[pindex] = (byte) (temp & 0xff);
			p[pindex + 1] = (byte) ((temp >> 8) & 0xff);
		}
			break;

		default:
			assert false : "bad type";
			break;
		}
	}

	// creates a radially-varying heightfield
	public void setRadial(byte[] grid, int bytesPerElement, HeightfieldTerrainShape.PHY_ScalarType type)
	{
		setRadial(grid, bytesPerElement, type, 0.0f);
	}

	public void setRadial(byte[] grid, int bytesPerElement, HeightfieldTerrainShape.PHY_ScalarType type, float phase)
	{
		assert (grid != null);
		assert (bytesPerElement > 0);

		// min/max
		float period = 0.5f / s_gridSpacing;
		float floor = 0.0f;
		float min_r = (float) (3.0f * Math.sqrt(s_gridSpacing));
		float magnitude = (float) (50.0f * Math.sqrt(s_gridSpacing));

		// pick a base_phase such that phase = 0 results in max height
		//   (this way, if you create a heightfield with phase = 0,
		//    you can rely on the min/max heights that result)
		float base_phase = (0.5f * BulletGlobals.SIMD_PI) - (period * min_r);
		phase += base_phase;

		// center of grid
		float cx = 0.5f * s_gridSize * s_gridSpacing;
		float cy = cx; // assume square grid
		byte[] p = grid;
		int pindex = 0;
		for (int i = 0; i < s_gridSize; ++i)
		{
			float x = i * s_gridSpacing;
			for (int j = 0; j < s_gridSize; ++j)
			{
				float y = j * s_gridSpacing;

				float dx = x - cx;
				float dy = y - cy;

				float r = (float) Math.sqrt((dx * dx) + (dy * dy));

				float z = period;
				if (r < min_r)
				{
					r = min_r;
				}
				z = (float) ((1.0f / r) * Math.sin(period * r + phase));
				if (z > period)
				{
					z = period;
				}
				else if (z < -period)
				{
					z = -period;
				}
				z = floor + magnitude * z;

				convertFromFloat(p, pindex, z, type);
				pindex += bytesPerElement;
			}
		}
	}

	public float randomHeight(int step)
	{
		return (0.33f * s_gridSpacing * s_gridSize * step * (m_random.nextInt(m_randomMax) - (0.5f * m_randomMax)))
				/ (1.0f * m_randomMax * s_gridSize);
	}

	public void updateHeight(byte[] p, int index, float new_val, HeightfieldTerrainShape.PHY_ScalarType type)
	{
		float old_val = convertToFloat(p, index, type);
		//if (old_val != 0.0f)
		{
			convertFromFloat(p, index, new_val, type);
		}
	}

	// creates a random, fractal heightfield
	public void setFractal(byte[] grid, int gridIndex, int bytesPerElement,
			HeightfieldTerrainShape.PHY_ScalarType type, int step)
	{
		assert (grid != null);
		assert (bytesPerElement > 0);
		assert (step > 0);
		assert (step < s_gridSize);

		int newStep = step / 2;
		//	std::cerr << "Computing grid with step = " << step << ": before\n";
		//	dumpGrid(grid, bytesPerElement, type, step + 1);

		// special case: starting (must set four corners)
		if (s_gridSize - 1 == step)
		{
			// pick a non-zero (possibly negative) base elevation for testing
			float baseValue = randomHeight(step / 2);

			convertFromFloat(grid, gridIndex, baseValue, type);
			convertFromFloat(grid, gridIndex + step * bytesPerElement, baseValue, type);
			convertFromFloat(grid, gridIndex + step * s_gridSize * bytesPerElement, baseValue, type);
			convertFromFloat(grid, gridIndex + (step * s_gridSize + step) * bytesPerElement, baseValue, type);
		}

		// determine elevation of each corner
		float c00 = convertToFloat(grid, gridIndex, type);
		float c01 = convertToFloat(grid, gridIndex + step * bytesPerElement, type);
		float c10 = convertToFloat(grid, gridIndex + (step * s_gridSize) * bytesPerElement, type);
		float c11 = convertToFloat(grid, gridIndex + (step * s_gridSize + step) * bytesPerElement, type);

		// set top middle
		updateHeight(grid, gridIndex + newStep * bytesPerElement, 0.5f * (c00 + c01) + randomHeight(step), type);

		// set left middle
		updateHeight(grid, gridIndex + (newStep * s_gridSize) * bytesPerElement, 0.5f * (c00 + c10)
				+ randomHeight(step), type);

		// set right middle
		updateHeight(grid, gridIndex + (newStep * s_gridSize + step) * bytesPerElement, 0.5f * (c01 + c11)
				+ randomHeight(step), type);

		// set bottom middle
		updateHeight(grid, gridIndex + (step * s_gridSize + newStep) * bytesPerElement, 0.5f * (c10 + c11)
				+ randomHeight(step), type);

		// set middle
		updateHeight(grid, gridIndex + (newStep * s_gridSize + newStep) * bytesPerElement, 0.25f
				* (c00 + c01 + c10 + c11) + randomHeight(step), type);

		//	std::cerr << "Computing grid with step = " << step << ": after\n";
		//	dumpGrid(grid, bytesPerElement, type, step + 1);

		// terminate?
		if (newStep < 2)
		{
			return;
		}

		// recurse
		setFractal(grid, gridIndex, bytesPerElement, type, newStep);
		setFractal(grid, gridIndex + newStep * bytesPerElement, bytesPerElement, type, newStep);
		setFractal(grid, gridIndex + (newStep * s_gridSize) * bytesPerElement, bytesPerElement, type, newStep);
		setFractal(grid, gridIndex + ((newStep * s_gridSize) + newStep) * bytesPerElement, bytesPerElement, type,
				newStep);
	}

	public byte[] getRawHeightfieldData(eTerrainModel model, HeightfieldTerrainShape.PHY_ScalarType type,
			float minHeight, float maxHeight)
	{
		//	std::cerr << "\nRegenerating terrain\n";
		//	std::cerr << "  model = " << model << "\n";
		//	std::cerr << "  type = " << type << "\n";

		int nElements = s_gridSize * s_gridSize;
		//	std::cerr << "  nElements = " << nElements << "\n";

		int bytesPerElement = getByteSize(type);
		//	std::cerr << "  bytesPerElement = " << bytesPerElement << "\n";
		assert bytesPerElement > 0 : "bad bytes per element";

		int nBytes = nElements * bytesPerElement;
		//	std::cerr << "  nBytes = " << nBytes << "\n";
		byte[] raw = new byte[nBytes];
		assert raw != null : "out of memory";

		// reseed randomization every 30 seconds
		//	srand(time(NULL) / 30);

		// populate based on model
		switch (model)
		{
		case eRadial:
			setRadial(raw, bytesPerElement, type);
			break;

		case eFractal:
			for (int i = 0; i < nBytes; i++)
			{
				raw[i] = 0;
			}
			setFractal(raw, 0, bytesPerElement, type, s_gridSize - 1);
			break;

		default:
			assert false : "bad model type";
			break;
		}

		if (false)
		{
			// inside if(0) so it keeps compiling but isn't
			// 	exercised and doesn't cause warnings
			//		std::cerr << "final grid:\n";
			dumpGrid(raw, bytesPerElement, type, s_gridSize - 1);
		}

		// find min/max
		for (int i = 0; i < s_gridSize; ++i)
		{
			for (int j = 0; j < s_gridSize; ++j)
			{
				float z = getGridHeight(raw, i, j, type);
				//			std::cerr << "i=" << i << ", j=" << j << ": z=" << z << "\n";

				// update min/max
				if (i == 0 && j == 0)
				{
					minHeight = z;
					maxHeight = z;
				}
				else
				{
					if (z < minHeight)
					{
						minHeight = z;
					}
					if (z > maxHeight)
					{
						maxHeight = z;
					}
				}
			}
		}

		if (maxHeight < -minHeight)
		{
			maxHeight = -minHeight;
		}
		if (minHeight > -maxHeight)
		{
			minHeight = -maxHeight;
		}

		m_minHeight = minHeight;
		m_maxHeight = maxHeight;
		
		
		//	std::cerr << "  minHeight = " << minHeight << "\n";
		//	std::cerr << "  maxHeight = " << maxHeight << "\n";

		return raw;
	}

	public void dumpGrid(byte[] grid, int bytesPerElement, HeightfieldTerrainShape.PHY_ScalarType type, int max)
	{
		//std::cerr << "Grid:\n";
		for (int j = 0; j < max; ++j)
		{
			for (int i = 0; i < max; ++i)
			{
				long offset = j * s_gridSize + i;
				float z = convertToFloat(grid, (int) (offset * bytesPerElement), type);
				//sprintf(buffer, "%6.2f", z);
				//std::cerr << "  " << buffer;
			}
			//std::cerr << "\n";
		}
	}

	public void keyboardCallback(char key, int x, int y, int modifiers)
	{

		if (',' == key)
		{
			// increment model
			m_model = (eTerrainModel.eFractal == m_model) ? eTerrainModel.eRadial : eTerrainModel.eFractal;
			clientResetScene();
		}
		else if ('?' == key)
		{
			// increment type
			m_type = nextType(m_type);
			clientResetScene();
		}
		else if ('\\' == key)
		{
			// increment axis
			m_upAxis++;
			if (m_upAxis > 2)
			{
				m_upAxis = 0;
			}
			clientResetScene();
		}
		else if ('[' == key)
		{
			// toggle dynamics
			m_isDynamic = !m_isDynamic;
		}

		// let demo base class handle!
		super.keyboardCallback(key, x, y, modifiers);
	}

	public void clientMoveAndDisplay()
	{
		if (m_rawHeightfieldData != null && m_isDynamic && eTerrainModel.eRadial == m_model)
		{
			//float ms = (float)gameTime.ElapsedGameTime.TotalSeconds;

			m_phase += s_deltaPhase * 1 / 60.0f;
			if (m_phase > 2.0f * BulletGlobals.SIMD_PI)
			{
				m_phase -= 2.0f * BulletGlobals.SIMD_PI;
			}
			int bpe = getByteSize(m_type);
			assert bpe > 0 : "Bad bytes per element";
			setRadial(m_rawHeightfieldData, bpe, m_type, m_phase);
		}

		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// simple dynamics world doesn't handle fixed-time-stepping
		float ms = getDeltaTimeMicroseconds();

		// step the simulation
		if (dynamicsWorld != null)
		{
			dynamicsWorld.stepSimulation(ms / 1000000f);
			// optional but useful: debug drawing
			dynamicsWorld.debugDrawWorld();
		}

		renderme();

		//super.clientMoveAndDisplay();
	}

	public void displayCallback()
	{
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		renderme();

		// optional but useful: debug drawing to detect problems
		if (dynamicsWorld != null)
		{
			dynamicsWorld.debugDrawWorld();
		}

		//glFlush();
		//glutSwapBuffers();
	}

	public HeightfieldTerrainShape.PHY_ScalarType nextType(HeightfieldTerrainShape.PHY_ScalarType type)
	{
		switch (type)
		{
		case PHY_FLOAT:
			return HeightfieldTerrainShape.PHY_ScalarType.PHY_SHORT;
		case PHY_SHORT:
			return HeightfieldTerrainShape.PHY_ScalarType.PHY_UCHAR;
		case PHY_UCHAR:
			return HeightfieldTerrainShape.PHY_ScalarType.PHY_FLOAT;
		}
		return HeightfieldTerrainShape.PHY_ScalarType.PHY_FLOAT;
	}

	//    static void main(string[] args)
	//    {
	//        using (TerrainDemo game = new TerrainDemo())
	//        {
	//            game.Run();
	//        }
	//    }

	private ObjectArrayList<CollisionShape> m_collisionShapes = new ObjectArrayList<CollisionShape>();
	private BroadphaseInterface m_broadphase;
	private CollisionDispatcher m_dispatcher;
	private ConstraintSolver m_solver;
	private DefaultCollisionConfiguration m_collisionConfiguration;

	private int m_upAxis;
	private HeightfieldTerrainShape.PHY_ScalarType m_type;
	private eTerrainModel m_model;
	private byte[] m_rawHeightfieldData;
	private float m_minHeight;
	private float m_maxHeight;
	private float m_phase; // for dynamics
	private boolean m_isDynamic;
	private HeightfieldTerrainShape m_terrainShape = null;
	private RigidBody m_terrainRigidBody = null;

	float m_nearClip;
	float m_farClip;
	float m_aspect;

	private Random m_random = new Random();
	int m_randomMax = 0x7fff;
	int s_gridSize = 64 + 1; // must be (2^N) + 1
	float s_gridSpacing =5.0f;

	static float s_gridHeightScale = 0.2f;

	// the singularity at the center of the radial model means we need a lot of
	//   finely-spaced time steps to get the physics right.
	// These numbers are probably too aggressive for a real game!
	int s_requestedHz = 180;
	float s_engineTimeStep = 1.0f / s_requestedHz;

	// delta phase: radians per second
	float s_deltaPhase = 0.25f * 2.0f * BulletGlobals.SIMD_PI;

	// what type of terrain is generated?
	public enum eTerrainModel
	{
		eRadial, // deterministic
		eFractal // random
	}

}
