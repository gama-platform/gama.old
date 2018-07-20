package msi.gama.runtime.benchmark;

import java.io.Closeable;

public interface IStopWatch extends Closeable {

	public final static IStopWatch NULL = new IStopWatch() {};

	default IStopWatch start() {
		return this;
	}

	@Override
	default void close() {}

}
