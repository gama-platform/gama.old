package msi.gaml.statements.test;

import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.util.GamaColor;

public abstract class AbstractSummary<S extends WithTestSummary<?>> {
	private static int COUNT = 0;
	private final URI uri;
	private final String title;
	public final int index = COUNT++;
	public String error;

	public AbstractSummary(final S statement) {
		if (statement != null) {
			title = statement.getTitleForSummary();
			uri = statement.getURI();
		} else {
			title = null;
			uri = null;
		}
	}

	public void setError(final String error) {
		this.error = error;
	}

	public String getError() {
		return error;
	}

	public int getIndex() {
		return index;
	}

	public URI getURI() {
		return uri;
	}

	public String getTitle() {
		return title;
	}

	public GamaColor getColor() {
		return getState().getColor();
	}

	public abstract TestState getState();

	public abstract void setState(TestState state);

	public void reset() {
		error = null;
	}

	public Map<String, ? extends AbstractSummary<?>> getSummaries() {
		return Collections.EMPTY_MAP;
	}

	public int countTestsWith(final TestState state) {
		return 0;
	}

	public int size() {
		return 0;
	}

	@Override
	public final String toString() {
		final TestState state = getState();
		if (GamaPreferences.Modeling.FAILED_TESTS.getValue() && state != TestState.FAILED && state != TestState.ABORTED)
			return "";
		final StringBuilder sb = new StringBuilder();
		printHeader(sb);
		sb.append(state).append(": ").append(getTitle()).append(" ");
		if (error != null)
			sb.append('[').append(error).append(']');
		printFooter(sb);
		for (final AbstractSummary<?> summary : getSummaries().values()) {
			final String child = summary.toString();
			if (child.isEmpty())
				continue;
			sb.append(child);
		}
		return sb.toString();
	}

	protected void printFooter(final StringBuilder sb) {}

	protected void printHeader(final StringBuilder sb) {}

}
