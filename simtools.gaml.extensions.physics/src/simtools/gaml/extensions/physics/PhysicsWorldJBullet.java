/*********************************************************************************************
 * 
 *
 * 'PhysicsWorldJBullet.java', in plugin 'simtools.gaml.extensions.physics', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package simtools.gaml.extensions.physics;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;



/**
 * This is a basic Test of jbullet-jme functions
 *
 * @author normenhansen
 */
public class PhysicsWorldJBullet {

    DiscreteDynamicsWorld dynamicsWorld;

    public PhysicsWorldJBullet(boolean gravity){
    	
		// collision configuration contains default setup for memory, collision
		// setup. Advanced users can create their own configuration.
		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();

		// use the default collision dispatcher. For parallel processing you
		// can use a diffent dispatcher (see Extras/BulletMultiThreaded)
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);

		// the maximum size of the collision world. Make sure objects stay
		// within these boundaries
		// Don't make the world AABB size too large, it will harm simulation
		// quality and performance
		Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000);
		Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);
		int maxProxies = 1024;
		AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax, maxProxies);
		

		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
		

		dynamicsWorld = new DiscreteDynamicsWorld(
				dispatcher, overlappingPairCache, solver,
				collisionConfiguration);
		if(gravity == true){
			dynamicsWorld.setGravity(new Vector3f(0.0f,0.0f,-9.81f));
		}
		else{
		dynamicsWorld.setGravity(new Vector3f(0.0f,0.0f,0.0f));
		}
    }
    
    
    public RigidBody addCollisionObject(CollisionShape shape, float mass, Vector3f position, Vector3f velocity){
    	
		Transform startTransform = new Transform();
		startTransform.setIdentity();
		startTransform.origin.set(position);
		boolean isDynamic = (mass != 0f);
		Vector3f localInertia = new Vector3f(0, 0, 0);
		if (isDynamic) {
			shape.calculateLocalInertia(mass, localInertia);
		}
		// using motionstate is recommended, it provides
		// interpolation capabilities, and only synchronizes
		// 'active' objects
		DefaultMotionState myMotionState = new DefaultMotionState(startTransform);

		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(
				mass, myMotionState, shape, localInertia);
		RigidBody body = new RigidBody(rbInfo);
//		System.out.println(velocity);
//		body.applyCentralForce(velocity);
		body.setLinearVelocity(velocity);
		dynamicsWorld.addRigidBody(body);

		return body;
        
    }
    
    public Vector3f getNodePosition(RigidBody body){
		Vector3f result = null;
		if (body != null && body.getMotionState() != null) {
			Transform trans = new Transform();
			body.getMotionState().getWorldTransform(trans);
			result = new Vector3f(trans.origin.x, trans.origin.y, trans.origin.z);
		}
		return result;
    }
    

    public void update(float ts){
		dynamicsWorld.stepSimulation(ts, 1);

    }
/**
    public void removeNode(Node node){
    	this.getPhysicsSpace().remove(node);
    }
    **/
}
