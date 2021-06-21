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

package com.bulletphysics.collision.dispatch;

import com.bulletphysics.ContactAddedCallback;

/**
 * Flags for collision objects.
 * 
 * @author jezek2
 */
public class CollisionFlags {

	/** Sets this collision object as static. */
	public static final int STATIC_OBJECT            = 1;
	
	/** Sets this collision object as kinematic. */
	public static final int KINEMATIC_OBJECT         = 2;
	
	/** Disables contact response. */
	public static final int NO_CONTACT_RESPONSE      = 4;
	
	/**
	 * Enables calling {@link ContactAddedCallback} for collision objects. This
	 * allows per-triangle material (friction/restitution).
	 */
	public static final int CUSTOM_MATERIAL_CALLBACK = 8;
	
	public static final int CHARACTER_OBJECT         = 16;
	
}
