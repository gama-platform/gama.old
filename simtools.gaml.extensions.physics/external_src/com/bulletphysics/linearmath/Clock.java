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
 * Clock is a portable basic clock that measures accurate time in seconds, use for profiling.
 * 
 * @author jezek2
 */
public class Clock {
	
	private long startTime;

	/**
	 * Creates a new clock and resets it.
	 */
	public Clock() {
		reset();
	}

	/**
	 * Resets clock by setting start time to current.
	 */
	public void reset() {
		startTime = System.nanoTime();
	}
	
	/**
	 * Returns the time in milliseconds since the last call to reset or since the Clock was created.
	 */
	public long getTimeMilliseconds() {
		return (System.nanoTime() - startTime) / 1000000L;
	}
	
	/**
	 * Returns the time in microseconds since the last call to reset or since the Clock was created.
	 */
	public long getTimeMicroseconds() {
		return (System.nanoTime() - startTime) / 1000L;
	}
	
}
