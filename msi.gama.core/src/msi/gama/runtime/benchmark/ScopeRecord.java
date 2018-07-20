package msi.gama.runtime.benchmark;

import java.util.Map;

import msi.gama.common.interfaces.IBenchmarkable;
import msi.gama.util.TOrderedHashMap;
import msi.gama.util.tree.GamaTree.Order;
import msi.gaml.compilation.ISymbol;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Strings;

public class ScopeRecord {

	final String scopeName;
	Map<IBenchmarkable, IRecord> objects = new TOrderedHashMap<>();

	public ScopeRecord(final String scope, final BenchmarkTree original) {
		this.scopeName = scope;
		original.visit(Order.PRE_ORDER, (n) -> objects.put(n.getData().getObject(), n.getData().copy()));
	}

	public IRecord find(final IBenchmarkable object) {
		return objects.getOrDefault(object, IRecord.NULL);
	}

	public IStopWatch getStopWatchFor(final ISymbol desc) {
		return find(desc.getDescription()).getStopWatch();
	}

	public IStopWatch getStopWatchFor(final IExpression expr) {
		if (!(expr instanceof IBenchmarkable)) { return IStopWatch.NULL; }
		return find((IBenchmarkable) expr).getStopWatch();
	}

	public void printOn(final StringBuilder sb, final BenchmarkTree original) {
		sb.append(Strings.LN);
		sb.append("---------------------------------------------------------------------").append(Strings.LN);
		sb.append(scopeName).append(Strings.LN);
		sb.append("---------------------------------------------------------------------").append(Strings.LN);
		original.visit(Order.PRE_ORDER, (n) -> {
			find(n.getData().getObject()).printOn(sb, n.getWeight());
		});
		sb.append(Strings.LN);
	}

}
