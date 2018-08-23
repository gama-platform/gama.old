package msi.gama.common.interfaces;

/**
 * Represents objects that can be used in benchmarking operations (see {@link msi.gama.runtime.benchmark.Benchmark})
 * 
 * @author drogoul
 * @since July 2018
 *
 */

public interface IBenchmarkable {

	/**
	 * Returns a human-readable name for benchmark results
	 * 
	 * @return a string representing this object in benchmark results
	 */
	public String getNameForBenchmarks();

}
