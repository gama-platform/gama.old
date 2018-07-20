package msi.gama.runtime.benchmark;

import java.util.concurrent.atomic.LongAdder;

import msi.gama.common.interfaces.IBenchmarkable;

public class BenchmarkRecord implements IRecord {

	final LongAdder milliseconds = new LongAdder(), times = new LongAdder();
	final IBenchmarkable object;

	public BenchmarkRecord(final IBenchmarkable object) {
		this.object = object;
	}

	@Override
	public IBenchmarkable getObject() {
		return object;
	}

	@Override
	public IStopWatch getStopWatch() {
		return new StopWatch(this);
	}

	@Override
	public IRecord copy() {
		return new BenchmarkRecord(object);
	}

	public void increaseCalls() {
		times.increment();
	}

	public void addMilliseconds(final long l) {
		milliseconds.add(l);
	}

	@Override
	public long getMilliseconds() {
		return milliseconds.longValue();
	}

	@Override
	public long getInvocations() {
		return times.longValue();
	}

	@Override
	public boolean isUnrecorded() {
		return times.longValue() == 0l;
	}

}
