package msi.gama.runtime.benchmark;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.runtime.IScope;
import msi.gaml.compilation.ISymbol;
import msi.gaml.expressions.IExpression;

public class Benchmark {

	Map<String, ScopeRecord> records = new ConcurrentHashMap<>();
	private final BenchmarkTree executionTree;

	public Benchmark(final IExperimentPlan experiment) {
		executionTree = new BenchmarkTree(experiment.getModel().getDescription(), experiment.getDescription());
	}

	public IStopWatch record(final IScope scope, final ISymbol symbol) {
		if (scope == null) { return IStopWatch.NULL; }
		return get(scope).getStopWatchFor(symbol).start();
	}

	public IStopWatch record(final IScope scope, final IExpression expr) {
		if (scope == null) { return IStopWatch.NULL; }
		return get(scope).getStopWatchFor(expr).start();
	}

	public ScopeRecord get(final IScope scope) {
		return records.computeIfAbsent(scope.getName(), (s) -> new ScopeRecord(s, executionTree));
	}

	public void printOn(final StringBuilder sb) {
		records.forEach((s, r) -> r.printOn(sb, executionTree));
	}

	public void saveAndDispose(final IExperimentPlan experiment) {
		final StringBuilder sb = new StringBuilder();
		printOn(sb);
		new BenchmarkCSVExporter().saveAsCSV(experiment, executionTree, records);
		System.out.println(sb.toString());
		executionTree.dispose();
		records.clear();

	}

}
