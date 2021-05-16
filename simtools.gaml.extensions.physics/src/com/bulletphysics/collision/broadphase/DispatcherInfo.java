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

package com.bulletphysics.collision.broadphase;

import com.bulletphysics.linearmath.IDebugDraw;

/**
 * Current state of {@link Dispatcher}.
 * 
 * @author jezek2
 */
public class DispatcherInfo {

	public float timeStep;
	public int stepCount;
	public DispatchFunc dispatchFunc;
	public float timeOfImpact;
	public boolean useContinuous;
	public IDebugDraw debugDraw;
	public boolean enableSatConvex;
	public boolean enableSPU = true;
	public boolean useEpa = true;
	public float allowedCcdPenetration = 0.04f;
	//btStackAlloc*	m_stackAllocator;

	public DispatcherInfo() {
		dispatchFunc = DispatchFunc.DISPATCH_DISCRETE;
		timeOfImpact = 1f;
	}
	
}
