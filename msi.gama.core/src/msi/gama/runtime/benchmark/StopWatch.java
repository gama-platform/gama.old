/*******************************************************************************************************
 *
 * StopWatch.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.runtime.benchmark;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Class StopWatch.
 */
public class StopWatch implements Closeable {
	
	/** The Constant NULL. */
	public final static StopWatch NULL = new StopWatch(BenchmarkRecord.NULL, BenchmarkRecord.NULL);
	
	/** The Constant notRunning. */
	final static long notRunning = -1;
	
	/** The scope. */
	private final BenchmarkRecord numbers, scope;
	
	/** The last start. */
	private long lastStart = notRunning;
	
	/** The reentrant. */
	private final AtomicInteger reentrant = new AtomicInteger();

	/**
	 * Instantiates a new stop watch.
	 *
	 * @param scope the scope
	 * @param numbers the numbers
	 */
	StopWatch(final BenchmarkRecord scope, final BenchmarkRecord numbers) {
		this.numbers = numbers;
		this.scope = scope;
	}

	/**
	 * Start.
	 *
	 * @return the stop watch
	 */
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