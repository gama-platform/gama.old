package msi.gama.runtime.benchmark;

import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IBenchmarkable;
import msi.gaml.operators.Strings;

public interface IRecord {

	public static IRecord NULL = () -> () -> "unknown";

	default IStopWatch getStopWatch() {
		return IStopWatch.NULL;
	}

	default void printOn(final StringBuilder sb, final int level) {
		if (isUnrecorded()) { return; }
		sb.append(Strings.LN);
		final String result = String.format("%30s", "[" + getMilliseconds() + " ms, " + getInvocations() + " calls] ");
		sb.append(result);
		appendIndentedName(sb, "-" + Strings.TAB, level);
	}

	default long getInvocations() {
		return 0l;
	}

	default long getMilliseconds() {
		return 0l;
	}

	default boolean isUnrecorded() {
		return true;
	}

	public IBenchmarkable getObject();

	default void appendIndentedName(final StringBuilder sb, final String indent, final int level) {
		for (int i = 0; i < level; i++) {
			sb.append(indent);
		}
		appendPrintableName(sb);
	}

	default void appendPrintableName(final StringBuilder sb) {
		sb.append(' ');
		sb.append(getObject().getNameForBenchmarks());
	}

	default void fill(final List<String> columns, final Map<String, ScopeRecord> scopes) {
		columns.add(getObject().getNameForBenchmarks());
		scopes.forEach((scope, scopeRecord) -> {
			final IRecord record = scopeRecord.find(getObject());
			columns.add(record.isUnrecorded() ? "" : String.valueOf(record.getMilliseconds()));
			columns.add(record.isUnrecorded() ? "" : String.valueOf(record.getInvocations()));
		});
	}

	default IRecord copy() {
		return this;
	}
}
