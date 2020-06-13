/*******************************************************************************************************
 *
 * msi.gama.runtime.benchmark.BenchmarkRecord.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.runtime.benchmark;

import java.util.concurrent.atomic.LongAdder;

import msi.gama.common.interfaces.IBenchmarkable;

public class BenchmarkRecord {

	public static BenchmarkRecord NULL = new BenchmarkRecord(() -> "unknown");
	public final LongAdder milliseconds = new LongAdder(), times = new LongAdder();
	public final IBenchmarkable object;

	public BenchmarkRecord(final IBenchmarkable object) {
		this.object = object;
	}

	public boolean isUnrecorded() {
		return times.longValue() == 0l;
	}

}
