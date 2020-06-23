/*******************************************************************************************************
 *
 * msi.gama.runtime.benchmark.BenchmarkConsolePrinter.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.runtime.benchmark;

import msi.gama.util.tree.GamaTree.Order;
import msi.gaml.operators.Strings;
import ummisco.gama.dev.utils.DEBUG;

public class BenchmarkConsolePrinter {
	static final String S = "\n------------------------------------------------\n";

	public void print(final Benchmark scopes) {
		final StringBuilder sb = new StringBuilder();
		scopes.forEach((s, r) -> {
			final BenchmarkRecord sr = r.ownRecord;
			sb.append(S).append(sr.object.getNameForBenchmarks()).append(" (").append(sr.milliseconds).append(" ms)")
					.append(S);
			scopes.tree.visit(Order.PRE_ORDER, (n) -> {
				final BenchmarkRecord br = r.find(n.getData());
				if (br != BenchmarkRecord.NULL && !br.isUnrecorded()) {
					sb.append(Strings.LN)
							.append(String.format("%30s", "[" + br.milliseconds + " ms, " + br.times + " calls] "));
					for (int i = 0; i < n.getWeight(); i++) {
						sb.append("-" + Strings.TAB);
					}
					sb.append(' ').append(br.object.getNameForBenchmarks());

				}
			});
			sb.append(Strings.LN);
		});
		DEBUG.LOG(sb.toString());
	}

}
