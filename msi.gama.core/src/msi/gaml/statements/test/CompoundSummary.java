package msi.gaml.statements.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.emf.common.util.URI;

import com.google.common.base.Objects;

import msi.gama.util.TOrderedHashMap;
import one.util.streamex.StreamEx;

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
public class CompoundSummary<T extends AbstractSummary<?>, S extends WithTestSummary<?>> extends AbstractSummary<S> {

	public final Map<String, T> summaries = new TOrderedHashMap<>();
	public boolean aborted;
	public String stringSummary;

	@SuppressWarnings ("unchecked")
	public CompoundSummary(final S symbol) {
		super(symbol);
		if (symbol != null)
			symbol.getSubElements().forEach(a -> addSummary((T) a.getSummary()));

	}

	public CompoundSummary(final Collection<T> summaries) {
		super(null);
		summaries.forEach(a -> addSummary(a));
	}

	// public CompoundSummary(final Collection<T> summaries) {
	// super(null);
	// summaries.forEach(a -> addSummary(a));
	// }

	public CompoundSummary() {
		this(Collections.EMPTY_LIST);
	}

	public boolean isEmpty() {
		return summaries.isEmpty();
	}

	@Override
	public long getTimeStamp() {
		return StreamEx.ofValues(summaries).mapToLong(s -> s.getTimeStamp()).max().getAsLong();
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

	public void addSummaries(final Collection<T> summaries) {
		for (final T s : summaries)
			addSummary(s);
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

	@Override
	public int countTestsWith(final TestState state) {
		return StreamEx.ofValues(summaries).mapToInt(s -> s.countTestsWith(state)).sum();
	}

	public CompoundSummary<T, S> getSubSummariesBelongingTo(final URI fileURI) {
		final List<AbstractSummary<?>> list = new ArrayList<>();
		final String uri = fileURI.toString();
		getSummaries().values().forEach(s -> {
			if (s.getURI() != null && s.getURI().toString().startsWith(uri))
				list.add(s);
		});
		return new CompoundSummary(list);
	}

	public String getStringSummary() {
		if (stringSummary == null) {
			stringSummary = createTestsSummary();
		}
		return stringSummary;
	}

	protected String createTestsSummary() {
		final Map<TestState, Integer> map = new TreeMap<>();
		map.put(TestState.ABORTED, 0);
		map.put(TestState.FAILED, 0);
		map.put(TestState.NOT_RUN, 0);
		map.put(TestState.PASSED, 0);
		map.put(TestState.WARNING, 0);
		final int[] size = { 0 };
		summaries.values().forEach(t -> {
			map.keySet().forEach(state -> map.put(state, map.get(state) + t.countTestsWith(state)));
			size[0] += t.size();
		});
		String message = "" + size[0] + " tests";
		for (final TestState s : map.keySet()) {
			if (map.get(s) == 0)
				continue;
			message += ", " + map.get(s) + " " + s;
		}
		return message;
	}

}