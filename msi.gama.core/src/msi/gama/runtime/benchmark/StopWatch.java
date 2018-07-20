package msi.gama.runtime.benchmark;

import java.util.concurrent.atomic.AtomicInteger;

public class StopWatch implements IStopWatch {
	final static long notRunning = -1;
	private final BenchmarkRecord numbers;
	private long lastStart = notRunning;
	private final AtomicInteger reentrant = new AtomicInteger();

	StopWatch(final BenchmarkRecord numbers) {
		this.numbers = numbers;
	}

	@Override
	public IStopWatch start() {
		if (lastStart == notRunning) {
			lastStart = System.currentTimeMillis();
		}
		reentrant.incrementAndGet();
		return this;
	}

	@Override
	public void close() {
		if (lastStart != notRunning) {
			reentrant.decrementAndGet();
			if (reentrant.intValue() == 0) {
				final long milli = System.currentTimeMillis();
				numbers.addMilliseconds(milli - lastStart);
				numbers.increaseCalls();
				lastStart = notRunning;
			}
		}
	}
}