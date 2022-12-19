/*******************************************************************************************************
 *
 * HullResult.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.linearmath.convexhull;

import com.bulletphysics.util.IntArrayList;
import java.util.ArrayList;
import javax.vecmath.Vector3f;

/**
 * Contains resulting polygonal representation.<p>
 * 
 * Depending on the {@link #polygons} flag, array of indices consists of:<br>
 * <b>for triangles:</b> indices are array indexes into the vertex list<br>
 * <b>for polygons:</b> indices are in the form (number of points in face) (p1, p2, p3, ...)
 * 
 * @author jezek2
 */
public class HullResult {
	
	/** True if indices represents polygons, false indices are triangles. */
	public boolean polygons = true;
	
	/** Number of vertices in the output hull. */
	public int numOutputVertices = 0;
	
	/** Array of vertices. */
	public final ArrayList<Vector3f> outputVertices = new ArrayList<Vector3f>();
	
	/** Number of faces produced. */
	public int numFaces = 0;
	
	/** Total number of indices. */
	public int numIndices = 0;
	
	/** Array of indices. */
	public final IntArrayList indices = new IntArrayList();
	
}
