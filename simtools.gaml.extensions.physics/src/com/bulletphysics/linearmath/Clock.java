/*******************************************************************************************************
 *
 * Clock.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.linearmath;

/**
 * Clock is a portable basic clock that measures accurate time in seconds, use for profiling.
 * 
 * @author jezek2
 */
public class Clock {
	
	/** The start time. */
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
