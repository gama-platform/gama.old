/*******************************************************************************************************
 *
 * SimplexSolverInterface.java, in simtools.gaml.extensions.physics, is part of the source code of the
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
 * SimplexSolverInterface can incrementally calculate distance between origin and
 * up to 4 vertices. Used by GJK or Linear Casting. Can be implemented by the
 * Johnson-algorithm or alternative approaches based on voronoi regions or barycentric
 * coordinates.
 * 
 * @author jezek2
 */
public abstract class SimplexSolverInterface {

	/**
	 * Reset.
	 */
	public abstract void reset();

	/**
	 * Adds the vertex.
	 *
	 * @param w the w
	 * @param p the p
	 * @param q the q
	 */
	public abstract void addVertex(Vector3f w, Vector3f p, Vector3f q);
	
	/**
	 * Closest.
	 *
	 * @param v the v
	 * @return true, if successful
	 */
	public abstract boolean closest(Vector3f v);

	/**
	 * Max vertex.
	 *
	 * @return the float
	 */
	public abstract float maxVertex();

	/**
	 * Full simplex.
	 *
	 * @return true, if successful
	 */
	public abstract boolean fullSimplex();

	/**
	 * Gets the simplex.
	 *
	 * @param pBuf the buf
	 * @param qBuf the q buf
	 * @param yBuf the y buf
	 * @return the simplex
	 */
	public abstract int getSimplex(Vector3f[] pBuf, Vector3f[] qBuf, Vector3f[] yBuf);

	/**
	 * In simplex.
	 *
	 * @param w the w
	 * @return true, if successful
	 */
	public abstract boolean inSimplex(Vector3f w);
	
	/**
	 * Backup closest.
	 *
	 * @param v the v
	 */
	public abstract void backup_closest(Vector3f v);

	/**
	 * Empty simplex.
	 *
	 * @return true, if successful
	 */
	public abstract boolean emptySimplex();

	/**
	 * Compute points.
	 *
	 * @param p1 the p 1
	 * @param p2 the p 2
	 */
	public abstract void compute_points(Vector3f p1, Vector3f p2);

	/**
	 * Num vertices.
	 *
	 * @return the int
	 */
	public abstract int numVertices();
	
}
