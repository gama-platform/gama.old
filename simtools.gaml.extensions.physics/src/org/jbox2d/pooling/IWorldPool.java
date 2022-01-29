/*******************************************************************************************************
 *
 * IWorldPool.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.pooling;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.Collision;
import org.jbox2d.collision.Distance;
import org.jbox2d.collision.TimeOfImpact;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.Mat33;
import org.jbox2d.common.Rot;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.Vec3;
import org.jbox2d.dynamics.contacts.Contact;

/**
 * World pool interface
 * @author Daniel
 *
 */
public interface IWorldPool {

	/**
	 * Gets the poly contact stack.
	 *
	 * @return the poly contact stack
	 */
	public IDynamicStack<Contact> getPolyContactStack();

	/**
	 * Gets the circle contact stack.
	 *
	 * @return the circle contact stack
	 */
	public IDynamicStack<Contact> getCircleContactStack();

	/**
	 * Gets the poly circle contact stack.
	 *
	 * @return the poly circle contact stack
	 */
	public IDynamicStack<Contact> getPolyCircleContactStack();
	
    /**
     * Gets the edge circle contact stack.
     *
     * @return the edge circle contact stack
     */
    public IDynamicStack<Contact> getEdgeCircleContactStack();
    
    /**
     * Gets the edge poly contact stack.
     *
     * @return the edge poly contact stack
     */
    public IDynamicStack<Contact> getEdgePolyContactStack();

    /**
     * Gets the chain circle contact stack.
     *
     * @return the chain circle contact stack
     */
    public IDynamicStack<Contact> getChainCircleContactStack();
    
    /**
     * Gets the chain poly contact stack.
     *
     * @return the chain poly contact stack
     */
    public IDynamicStack<Contact> getChainPolyContactStack();
    
	/**
	 * Pop vec 2.
	 *
	 * @return the vec 2
	 */
	public Vec2 popVec2();

	/**
	 * Pop vec 2.
	 *
	 * @param num the num
	 * @return the vec 2 []
	 */
	public Vec2[] popVec2(int num);

	/**
	 * Push vec 2.
	 *
	 * @param num the num
	 */
	public void pushVec2(int num);

	/**
	 * Pop vec 3.
	 *
	 * @return the vec 3
	 */
	public Vec3 popVec3();

	/**
	 * Pop vec 3.
	 *
	 * @param num the num
	 * @return the vec 3 []
	 */
	public Vec3[] popVec3(int num);

	/**
	 * Push vec 3.
	 *
	 * @param num the num
	 */
	public void pushVec3(int num);

	/**
	 * Pop mat 22.
	 *
	 * @return the mat 22
	 */
	public Mat22 popMat22();

	/**
	 * Pop mat 22.
	 *
	 * @param num the num
	 * @return the mat 22 []
	 */
	public Mat22[] popMat22(int num);

	/**
	 * Push mat 22.
	 *
	 * @param num the num
	 */
	public void pushMat22(int num);
	
	/**
	 * Pop mat 33.
	 *
	 * @return the mat 33
	 */
	public Mat33 popMat33();
	
	/**
	 * Push mat 33.
	 *
	 * @param num the num
	 */
	public void pushMat33(int num);

	/**
	 * Pop AABB.
	 *
	 * @return the aabb
	 */
	public AABB popAABB();

	/**
	 * Pop AABB.
	 *
	 * @param num the num
	 * @return the AAB b[]
	 */
	public AABB[] popAABB(int num);

	/**
	 * Push AABB.
	 *
	 * @param num the num
	 */
	public void pushAABB(int num);
	
	/**
	 * Pop rot.
	 *
	 * @return the rot
	 */
	public Rot popRot();

	/**
	 * Push rot.
	 *
	 * @param num the num
	 */
	public void pushRot(int num);
	
	/**
	 * Gets the collision.
	 *
	 * @return the collision
	 */
	public Collision getCollision();

	/**
	 * Gets the time of impact.
	 *
	 * @return the time of impact
	 */
	public TimeOfImpact getTimeOfImpact();

	/**
	 * Gets the distance.
	 *
	 * @return the distance
	 */
	public Distance getDistance();

	/**
	 * Gets the float array.
	 *
	 * @param argLength the arg length
	 * @return the float array
	 */
	public float[] getFloatArray(int argLength);

	/**
	 * Gets the int array.
	 *
	 * @param argLength the arg length
	 * @return the int array
	 */
	public int[] getIntArray(int argLength);

	/**
	 * Gets the vec 2 array.
	 *
	 * @param argLength the arg length
	 * @return the vec 2 array
	 */
	public Vec2[] getVec2Array(int argLength);
}
