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
