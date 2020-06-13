/*******************************************************************************************************
 *
 * msi.gama.runtime.benchmark.StopWatch.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.runtime.benchmark;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicInteger;

public class StopWatch implements Closeable {
	public final static StopWatch NULL = new StopWatch(BenchmarkRecord.NULL, BenchmarkRecord.NULL);
	final static long notRunning = -1;
	private final BenchmarkRecord numbers, scope;
	private long lastStart = notRunning;
	private final AtomicInteger reentrant = new AtomicInteger();

	StopWatch(final BenchmarkRecord scope, final BenchmarkRecord numbers) {
		this.numbers = numbers;
		this.scope = scope;
	}

	public StopWatch start() {
		if (lastStart == notRunning) {
			lastStart = System.currentTimeMillis();
		}
		reentrant.incrementAndGet();
		return this;
	}

	@Override
	public void close() {
		if (lastStart != notRunning) {
			final int value = reentrant.decrementAndGet();
			if (value == 0) {
				final long milli = System.currentTimeMillis() - lastStart;
				numbers.milliseconds.add(milli);
				scope.milliseconds.add(milli);
				numbers.times.increment();
				lastStart = notRunning;
			}
		}
	}
}