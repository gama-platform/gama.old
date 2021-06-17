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

package com.bulletphysics.linearmath;

/**
 * Debug draw modes, used by demo framework.
 * 
 * @author jezek2
 */
public class DebugDrawModes {
	
	public static final int NO_DEBUG              = 0;
	public static final int DRAW_WIREFRAME        = 1;
	public static final int DRAW_AABB             = 2;
	public static final int DRAW_FEATURES_TEXT    = 4;
	public static final int DRAW_CONTACT_POINTS   = 8;
	public static final int NO_DEACTIVATION       = 16;
	public static final int NO_HELP_TEXT          = 32;
	public static final int DRAW_TEXT             = 64;
	public static final int PROFILE_TIMINGS       = 128;
	public static final int ENABLE_SAT_COMPARISON = 256;
	public static final int DISABLE_BULLET_LCP    = 512;
	public static final int ENABLE_CCD            = 1024;
	public static final int MAX_DEBUG_DRAW_MODE   = 1025;
	
}
