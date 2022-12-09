/*******************************************************************************************************
 *
 * BenchmarkRecord.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.runtime.benchmark;

import java.util.concurrent.atomic.LongAdder;

import msi.gama.common.interfaces.IBenchmarkable;

/**
 * The Class BenchmarkRecord.
 */
public class BenchmarkRecord {

	/** The null. */
	public static BenchmarkRecord NULL = new BenchmarkRecord(() -> "unknown");
	
	/** The times. */
	public final LongAdder milliseconds = new LongAdder(), times = new LongAdder();
	
	/** The object. */
	public final IBenchmarkable object;

	/**
	 * Instantiates a new benchmark record.
	 *
	 * @param object the object
	 */
	public BenchmarkRecord(final IBenchmarkable object) {
		this.object = object;
	}

	/**
	 * Checks if is unrecorded.
	 *
	 * @return true, if is unrecorded
	 */
	public boolean isUnrecorded() {
		return times.longValue() == 0l;
	}

}
