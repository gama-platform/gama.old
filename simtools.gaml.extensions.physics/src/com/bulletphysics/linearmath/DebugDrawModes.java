/*******************************************************************************************************
 *
 * DebugDrawModes.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.linearmath;

/**
 * Debug draw modes, used by demo framework.
 * 
 * @author jezek2
 */
public class DebugDrawModes {
	
	/** The Constant NO_DEBUG. */
	public static final int NO_DEBUG              = 0;
	
	/** The Constant DRAW_WIREFRAME. */
	public static final int DRAW_WIREFRAME        = 1;
	
	/** The Constant DRAW_AABB. */
	public static final int DRAW_AABB             = 2;
	
	/** The Constant DRAW_FEATURES_TEXT. */
	public static final int DRAW_FEATURES_TEXT    = 4;
	
	/** The Constant DRAW_CONTACT_POINTS. */
	public static final int DRAW_CONTACT_POINTS   = 8;
	
	/** The Constant NO_DEACTIVATION. */
	public static final int NO_DEACTIVATION       = 16;
	
	/** The Constant NO_HELP_TEXT. */
	public static final int NO_HELP_TEXT          = 32;
	
	/** The Constant DRAW_TEXT. */
	public static final int DRAW_TEXT             = 64;
	
	/** The Constant PROFILE_TIMINGS. */
	public static final int PROFILE_TIMINGS       = 128;
	
	/** The Constant ENABLE_SAT_COMPARISON. */
	public static final int ENABLE_SAT_COMPARISON = 256;
	
	/** The Constant DISABLE_BULLET_LCP. */
	public static final int DISABLE_BULLET_LCP    = 512;
	
	/** The Constant ENABLE_CCD. */
	public static final int ENABLE_CCD            = 1024;
	
	/** The Constant MAX_DEBUG_DRAW_MODE. */
	public static final int MAX_DEBUG_DRAW_MODE   = 1025;
	
}
