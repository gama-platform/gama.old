package msi.gaml.statements.test;

import java.util.Map;

import com.google.common.base.Objects;

import msi.gama.util.TOrderedHashMap;

/**
 * A summary composed of other summaries (for instance, a TestStatement summary is composed of AsserStatement summaries)
 * 
 * @author drogoul
 *
 * @param <S>
 *            the type of the statement represented by this summary
 * 
 * @param <T>
 *            the type of the sub-summaries
 * 
 */
public abstract class CompoundSummary<T extends AbstractSummary<?>, S extends WithTestSummary<?>>
		extends AbstractSummary<S> {

	public final Map<String, T> summaries = new TOrderedHashMap<>();
	public boolean aborted;

	@SuppressWarnings ("unchecked")
	public CompoundSummary(final S symbol) {
		super(symbol);
		if (symbol != null)
			symbol.getSubElements().forEach(a -> addSummary((T) a.getSummary()));

	}

	public boolean isEmpty() {
		return summaries.isEmpty();
	}

	@Override
	public int size() {
		return summaries.size();
	}

	@Override
	public void setState(final TestState s) {
		aborted = s == TestState.ABORTED;
	}

	@Override
	public Map<String, ? extends AbstractSummary<?>> getSummaries() {
		return summaries;
	}

	public void addSummary(final T summary) {
		summaries.put(summary.getTitle(), summary);
	}

	@Override
	public void reset() {
		summaries.values().forEach(u -> u.reset());
		aborted = false;
	}

	@SuppressWarnings ("unchecked")
	@Override
	public boolean equals(final Object o) {
		if (!getClass().isInstance(o))
			return false;
		return Objects.equal(((AbstractSummary<?>) o).getTitle(), getTitle());
	}

	@Override
	public TestState getState() {
		if (aborted)
			return TestState.ABORTED;
		TestState state = TestState.NOT_RUN;
		for (final AbstractSummary<?> a : summaries.values()) {
			final TestState s = a.getState();
			switch (s) {
				case NOT_RUN:
					break;
				case FAILED:
					state = TestState.FAILED;
					break;
				case PASSED:
					if (state.equals(TestState.NOT_RUN))
						state = TestState.PASSED;
					break;
				case WARNING:
					if (state.equals(TestState.PASSED) || state.equals(TestState.NOT_RUN))
						state = TestState.WARNING;
					break;
				case ABORTED:
					return TestState.ABORTED;
			}
		}
		return state;

	}

}