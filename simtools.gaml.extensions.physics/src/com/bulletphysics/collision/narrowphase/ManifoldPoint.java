/*******************************************************************************************************
 *
 * ManifoldPoint.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.narrowphase;

import javax.vecmath.Vector3f;

/**
 * ManifoldPoint collects and maintains persistent contactpoints. Used to improve
 * stability and performance of rigidbody dynamics response.
 * 
 * @author jezek2
 */
public class ManifoldPoint {

	/** The local point A. */
	public final Vector3f localPointA = new Vector3f();
	
	/** The local point B. */
	public final Vector3f localPointB = new Vector3f();
	
	/** The position world on B. */
	public final Vector3f positionWorldOnB = new Vector3f();
	
	/** The position world on A. */
	///m_positionWorldOnA is redundant information, see getPositionWorldOnA(), but for clarity
	public final Vector3f positionWorldOnA = new Vector3f();
	
	/** The normal world on B. */
	public final Vector3f normalWorldOnB = new Vector3f();
	
	/** The distance 1. */
	public float distance1;
	
	/** The combined friction. */
	public float combinedFriction;
	
	/** The combined restitution. */
	public float combinedRestitution;
	
	/** The part id 0. */
	// BP mod, store contact triangles.
	public int partId0;
	
	/** The part id 1. */
	public int partId1;
	
	/** The index 0. */
	public int index0;
	
	/** The index 1. */
	public int index1;
	
	/** The user persistent data. */
	public Object userPersistentData;
	
	/** The applied impulse. */
	public float appliedImpulse;
	
	/** The lateral friction initialized. */
	public boolean lateralFrictionInitialized;
	
	/** The applied impulse lateral 1. */
	public float appliedImpulseLateral1;
	
	/** The applied impulse lateral 2. */
	public float appliedImpulseLateral2;
	
	/** The life time. */
	public int lifeTime; //lifetime of the contactpoint in frames

	/** The lateral friction dir 1. */
	public final Vector3f lateralFrictionDir1 = new Vector3f();
	
	/** The lateral friction dir 2. */
	public final Vector3f lateralFrictionDir2 = new Vector3f();
	
	/**
	 * Instantiates a new manifold point.
	 */
	public ManifoldPoint() {
		this.userPersistentData = null;
		this.appliedImpulse = 0f;
		this.lateralFrictionInitialized = false;
		this.lifeTime = 0;
	}
	
	/**
	 * Instantiates a new manifold point.
	 *
	 * @param pointA the point A
	 * @param pointB the point B
	 * @param normal the normal
	 * @param distance the distance
	 */
	public ManifoldPoint(Vector3f pointA, Vector3f pointB, Vector3f normal, float distance) {
		init(pointA, pointB, normal, distance);
	}

	/**
	 * Inits the.
	 *
	 * @param pointA the point A
	 * @param pointB the point B
	 * @param normal the normal
	 * @param distance the distance
	 */
	public void init(Vector3f pointA, Vector3f pointB, Vector3f normal, float distance) {
		this.localPointA.set(pointA);
		this.localPointB.set(pointB);
		this.normalWorldOnB.set(normal);
		this.distance1 = distance;
		this.combinedFriction = 0f;
		this.combinedRestitution = 0f;
		this.userPersistentData = null;
		this.appliedImpulse = 0f;
		this.lateralFrictionInitialized = false;
		this.appliedImpulseLateral1 = 0f;
		this.appliedImpulseLateral2 = 0f;
		this.lifeTime = 0;
	}

	/**
	 * Gets the distance.
	 *
	 * @return the distance
	 */
	public float getDistance() {
		return distance1;
	}

	/**
	 * Gets the life time.
	 *
	 * @return the life time
	 */
	public int getLifeTime() {
		return lifeTime;
	}
	
	/**
	 * Sets the.
	 *
	 * @param p the p
	 */
	public void set(ManifoldPoint p) {
		localPointA.set(p.localPointA);
		localPointB.set(p.localPointB);
		positionWorldOnA.set(p.positionWorldOnA);
		positionWorldOnB.set(p.positionWorldOnB);
		normalWorldOnB.set(p.normalWorldOnB);
		distance1 = p.distance1;
		combinedFriction = p.combinedFriction;
		combinedRestitution = p.combinedRestitution;
		partId0 = p.partId0;
		partId1 = p.partId1;
		index0 = p.index0;
		index1 = p.index1;
		userPersistentData = p.userPersistentData;
		appliedImpulse = p.appliedImpulse;
		lateralFrictionInitialized = p.lateralFrictionInitialized;
		appliedImpulseLateral1 = p.appliedImpulseLateral1;
		appliedImpulseLateral2 = p.appliedImpulseLateral2;
		lifeTime = p.lifeTime;
		lateralFrictionDir1.set(p.lateralFrictionDir1);
		lateralFrictionDir2.set(p.lateralFrictionDir2);
	}
	
	/**
	 * Gets the position world on A.
	 *
	 * @param out the out
	 * @return the position world on A
	 */
	public Vector3f getPositionWorldOnA(Vector3f out) {
		out.set(positionWorldOnA);
		return out;
		//return m_positionWorldOnB + m_normalWorldOnB * m_distance1;
	}

	/**
	 * Gets the position world on B.
	 *
	 * @param out the out
	 * @return the position world on B
	 */
	public Vector3f getPositionWorldOnB(Vector3f out) {
		out.set(positionWorldOnB);
		return out;
	}

	/**
	 * Sets the distance.
	 *
	 * @param dist the new distance
	 */
	public void setDistance(float dist) {
		distance1 = dist;
	}
	
}
