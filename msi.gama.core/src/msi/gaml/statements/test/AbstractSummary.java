/*******************************************************************************************************
 *
 * msi.gaml.statements.test.AbstractSummary.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.test;

import java.util.Map;

import org.eclipse.emf.common.util.URI;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.util.GamaColor;
import one.util.streamex.StreamEx;

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

	public abstract long getTimeStamp();

	public GamaColor getColor() {
		return getState().getColor();
	}

	public abstract TestState getState();

	public abstract void setState(TestState state);

	public void reset() {
		error = null;
	}

	public abstract Map<String, ? extends AbstractSummary<?>> getSummaries();

	public abstract int countTestsWith(final TestState state);

	public abstract int size();

	@Override
	public final String toString() {
		final TestState state = getState();
		if (GamaPreferences.Runtime.FAILED_TESTS.getValue() && state != TestState.FAILED
				&& state != TestState.ABORTED) {
			return "";
		}
		final StringBuilder sb = new StringBuilder();
		printHeader(sb);
		sb.append(state).append(": ").append(getTitle()).append(" ");
		if (error != null) {
			sb.append('[').append(error).append(']');
		}
		printFooter(sb);
		for (final AbstractSummary<?> summary : getSummaries().values()) {
			final String child = summary.toString();
			if (child.isEmpty()) {
				continue;
			}
			sb.append(child);
		}
		return sb.toString();
	}

	protected void printFooter(final StringBuilder sb) {}

	protected void printHeader(final StringBuilder sb) {}

	public AbstractSummary<?> getSummaryOf(final URI uri) {
		// if (this.uri != null) {
		// DEBUG.OUT("Comparing " + this.uri + " to " + uri);
		// }
		if (uri.equals(this.uri)) { return this; }
		return StreamEx.ofValues(getSummaries()).findFirst(s -> s.getSummaryOf(uri) != null).orElse(null);
	}

}
