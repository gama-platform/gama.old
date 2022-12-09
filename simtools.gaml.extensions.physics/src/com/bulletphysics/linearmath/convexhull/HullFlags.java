/*******************************************************************************************************
 *
 * HullFlags.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.linearmath.convexhull;

/**
 * Flags that affects convex hull generation, used in {@link HullDesc#flags}.
 * 
 * @author jezek2
 */
public class HullFlags {
	
	/** The triangles. */
	public static int TRIANGLES     = 1 << 0; // report results as triangles, not polygons.
	
	/** The reverse order. */
	public static int REVERSE_ORDER = 1 << 1; // reverse order of the triangle indices.
	
	/** The default. */
	public static int DEFAULT       = TRIANGLES;
	
}
