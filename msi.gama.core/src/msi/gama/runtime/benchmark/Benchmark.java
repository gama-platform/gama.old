/*******************************************************************************************************
 *
 * msi.gama.runtime.benchmark.Benchmark.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.runtime.benchmark;

import java.util.concurrent.ConcurrentHashMap;

import msi.gama.common.interfaces.IBenchmarkable;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.runtime.IScope;
import msi.gama.runtime.benchmark.Benchmark.ScopeRecord;

public class Benchmark extends ConcurrentHashMap<IScope, ScopeRecord> {

	class ScopeRecord extends ConcurrentHashMap<IBenchmarkable, BenchmarkRecord> {

		final BenchmarkRecord ownRecord;

		public ScopeRecord(final IScope scope) {
			ownRecord = new BenchmarkRecord(scope);
		}

		public BenchmarkRecord find(final IBenchmarkable object) {
			return computeIfAbsent(object, (o) -> new BenchmarkRecord(o));
		}

		public StopWatch getStopWatchFor(final IBenchmarkable desc) {
			return new StopWatch(ownRecord, find(desc));
		}

	}

	public final BenchmarkTree tree;

	public Benchmark(final IExperimentPlan experiment) {
		tree = new BenchmarkTree(experiment.getModel().getDescription(), experiment.getDescription());
	}

	public StopWatch record(final IScope scope, final IBenchmarkable symbol) {
		return computeIfAbsent(scope, (s) -> new ScopeRecord(s)).getStopWatchFor(symbol).start();
	}

	public void saveAndDispose(final IExperimentPlan experiment) {
		new BenchmarkConsolePrinter().print(this);
		new BenchmarkCSVExporter().save(experiment, this);
		tree.dispose();
		clear();
	}

}
