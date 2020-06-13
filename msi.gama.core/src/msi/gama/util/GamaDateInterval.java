/*******************************************************************************************************
 *
 * msi.gama.util.GamaDateInterval.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

import com.google.common.collect.Iterators;

import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.operators.Dates;
import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * An immutable interval of time between two instants.
 * <p>
 * An interval represents the time on the time-line between two {@link GamaDate} s. The class stores the start and end
 * dates, with the start inclusive and the end exclusive. The end date is always greater than or equal to the start
 * instant.
 * <p>
 * The {@link Duration} of an interval can be obtained, but is a separate concept. An interval is connected to the
 * time-line, whereas a duration is not.
 * <p>
 * Intervals are not comparable. To compare the length of two intervals, it is generally recommended to compare their
 * durations.
 */
public final class GamaDateInterval implements IList<GamaDate> {

	/**
	 * The start instant (inclusive).
	 */
	final GamaDate start;
	/**
	 * The end instant (exclusive).
	 */
	final GamaDate end;

	final Duration step;

	final Integer size;

	/**
	 * @param startInclusive
	 *            the start instant, inclusive, MIN_DATE treated as unbounded, not null
	 * @param endExclusive
	 *            the end instant, exclusive, MAX_DATE treated as unbounded, not null
	 * @return the half-open interval, not null
	 * @throws DateTimeException
	 *             if the end is before the start
	 */
	public static GamaDateInterval of(final GamaDate startInclusive, final GamaDate endExclusive) {
		return new GamaDateInterval(startInclusive, endExclusive);
	}

	private GamaDateInterval(final GamaDate startInclusive, final GamaDate endExclusive) {
		this(startInclusive, endExclusive,
				Duration.of(Dates.DATES_TIME_STEP.getValue().longValue(), ChronoUnit.SECONDS));
	}

	public GamaDateInterval(final GamaDate startInclusive, final GamaDate endExclusive, final Duration step) {
		this.start = startInclusive;
		this.end = endExclusive;
		if (start.isAfter(end)) {
			this.step = step.abs().negated();
		} else {
			this.step = step;
		}
		size = size();
	}

	public GamaDate getStart() {
		return start;
	}

	public GamaDate getEnd() {
		return end;
	}

	@Override
	public boolean isEmpty() {
		return start.equals(end);
	}

	public boolean contains(final GamaDate instant) {
		return start.compareTo(instant) <= 0 && instant.compareTo(end) < 0;
	}

	// -----------------------------------------------------------------------
	/**
	 * Obtains the duration of this interval.
	 * <p>
	 * An {@code Interval} is associated with two specific instants on the time-line. A {@code Duration} is simply an
	 * amount of time, separate from the time-line.
	 *
	 * @return the duration of the time interval
	 * @throws ArithmeticException
	 *             if the calculation exceeds the capacity of {@code Duration}
	 */
	public Duration toDuration() {
		return Duration.between(start, end);
	}

	// -----------------------------------------------------------------------
	/**
	 * Checks if this interval is equal to another interval.
	 * <p>
	 * Compares this {@code Interval} with another ensuring that the two instants are the same. Only objects of type
	 * {@code Interval} are compared, other types return false.
	 *
	 * @param obj
	 *            the object to check, null returns false
	 * @return true if this is equal to the other interval
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj instanceof GamaDateInterval) {
			final GamaDateInterval other = (GamaDateInterval) obj;
			return start.equals(other.start) && end.equals(other.end);
		}
		return false;
	}

	/**
	 * A hash code for this interval.
	 *
	 * @return a suitable hash code
	 */
	@Override
	public int hashCode() {
		return start.hashCode() ^ end.hashCode();
	}

	// -----------------------------------------------------------------------
	/**
	 * Outputs this interval as a {@code String}, such as {@code 2007-12-03T10:15:30/2007-12-04T10:15:30}.
	 * <p>
	 * The output will be the ISO-8601 format formed by combining the {@code toString()} methods of the two instants,
	 * separated by a forward slash.
	 *
	 * @return a string representation of this instant, not null
	 */
	@Override
	public String toString() {
		return start.toString() + '/' + end.toString();
	}

	@Override
	public IContainerType<?> getGamlType() {
		return Types.LIST.of(Types.DATE);
	}

	@Override
	public IList<GamaDate> listValue(final IScope scope, final IType contentType, final boolean copy) {
		if (copy) { return GamaListFactory.createWithoutCasting(Types.DATE, this); }
		return this;
	}

	@Override
	public Iterable<? extends GamaDate> iterable(final IScope scope) {
		return this;
	}

	@Override
	public boolean contains(final IScope scope, final Object o) throws GamaRuntimeException {
		return contains(o);
	}

	@Override
	public GamaDate firstValue(final IScope scope) throws GamaRuntimeException {
		return start;
	}

	@Override
	public GamaDate lastValue(final IScope scope) throws GamaRuntimeException {
		return end;
	}

	@Override
	public int length(final IScope scope) {
		return size();
	}

	@Override
	public boolean isEmpty(final IScope scope) {
		return this.isEmpty();
	}

	@Override
	public GamaDate anyValue(final IScope scope) {
		final int i = scope.getRandom().between(0, size());
		return get(i);
	}

	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return toString();
	}

	@Override
	public GamaDateInterval copy(final IScope scope) throws GamaRuntimeException {
		return new GamaDateInterval(start, end, step);
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return "(" + start.serialize(includingBuiltIn) + " to " + end.serialize(includingBuiltIn) + ") every ("
				+ (double) step.get(ChronoUnit.SECONDS) + ")";
	}

	@Override
	public boolean checkBounds(final IScope scope, final Object index, final boolean forAdding) {
		return false;
	}

	@Override
	public void addValue(final IScope scope, final GamaDate value) {}

	@Override
	public void addValueAtIndex(final IScope scope, final Object index, final GamaDate value) {}

	@Override
	public void setValueAtIndex(final IScope scope, final Object index, final GamaDate value) {}

	@Override
	public void addValues(final IScope scope, final IContainer values) {}

	@Override
	public void setAllValues(final IScope scope, final GamaDate value) {}

	@Override
	public void removeValue(final IScope scope, final Object value) {}

	@Override
	public void removeIndex(final IScope scope, final Object index) {}

	@Override
	public void removeIndexes(final IScope scope, final IContainer<?, ?> index) {}

	@Override
	public void removeValues(final IScope scope, final IContainer values) {}

	@Override
	public void removeAllOccurrencesOfValue(final IScope scope, final Object value) {}

	@Override
	public GamaDate get(final IScope scope, final Integer index) throws GamaRuntimeException {
		return get(index);
	}

	@Override
	public GamaDate getFromIndicesList(final IScope scope, final IList indices) throws GamaRuntimeException {
		return get(scope, (Integer) indices.get(0));
	}

	@Override
	public int size() {
		if (size != null) { return size; }
		final int size = Iterators.size(iterator());
		return size;
	}

	@Override
	public boolean contains(final Object o) {
		if (!(o instanceof GamaDate)) { return false; }
		return this.contains((GamaDate) o);
	}

	@Override
	public Iterator<GamaDate> iterator() {
		return new Iterator<GamaDate>() {

			GamaDate current = null;

			@Override
			public boolean hasNext() {
				if (current == null) { return !isEmpty(); }
				return current.plus(step).isBefore(end);
			}

			@Override
			public GamaDate next() {
				if (current == null) {
					current = start;
				} else {
					current = current.plus(step);
				}
				return current;
			}
		};
	}

	@Override
	public GamaDate[] toArray() {
		return Iterators.toArray(iterator(), GamaDate.class);
	}

	@SuppressWarnings ("unchecked")
	@Override
	public <T> T[] toArray(final T[] a) {
		return (T[]) Iterators.toArray(iterator(), Object.class);
	}

	@Override
	public boolean add(final GamaDate e) {
		return false;
	}

	@Override
	public boolean remove(final Object o) {
		return false;
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		for (final Object o : c) {
			if (!contains(o)) { return false; }
		}
		return true;
	}

	@Override
	public boolean addAll(final Collection<? extends GamaDate> c) {
		return false;
	}

	@Override
	public boolean addAll(final int index, final Collection<? extends GamaDate> c) {
		return false;
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		return false;
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		return false;
	}

	@Override
	public void clear() {}

	@Override
	public GamaDate get(final int index) {
		return start.plus(step.get(ChronoUnit.SECONDS), index, ChronoUnit.SECONDS);
	}

	@Override
	public GamaDate set(final int index, final GamaDate element) {
		return null;
	}

	@Override
	public void add(final int index, final GamaDate element) {}

	@Override
	public GamaDate remove(final int index) {
		return null;
	}

	@Override
	public int indexOf(final Object o) {
		int i = 0;
		for (final GamaDate d : this) {

			if (d.equals(o)) { return i; }
			i++;
		}
		return -1;
	}

	@Override
	public int lastIndexOf(final Object o) {
		return indexOf(o);
	}

	@Override
	public ListIterator<GamaDate> listIterator() {
		return new ArrayList<>(this).listIterator();
	}

	@Override
	public ListIterator<GamaDate> listIterator(final int index) {
		return new ArrayList<>(this).listIterator(index);
	}

	@Override
	public GamaDateInterval subList(final int fromIndex, final int toIndex) {
		return new GamaDateInterval(get(fromIndex), get(toIndex), step);
	}

	@Override
	public GamaDateInterval reverse(final IScope scope) {
		return new GamaDateInterval(end, start, step);
	}

	@Override
	public IMatrix<GamaDate> matrixValue(final IScope scope, final IType contentType, final ILocation size,
			final boolean copy) {
		return GamaListFactory.wrap(Types.DATE, this).matrixValue(scope, contentType, copy);
	}

	@Override
	public IMatrix<GamaDate> matrixValue(final IScope scope, final IType contentType, final boolean copy) {
		return GamaListFactory.wrap(Types.DATE, this).matrixValue(scope, contentType, copy);
	}

	public IList<GamaDate> step(final Double step) {
		return new GamaDateInterval(start, end, Duration.of((long) step.doubleValue(), ChronoUnit.SECONDS));
	}

	@SuppressWarnings ("unchecked")
	@Override
	public IMap mapValue(final IScope scope, final IType keyType, final IType contentType, final boolean copy) {
		final IMap<GamaDate, GamaDate> map = GamaMapFactory.create(Types.DATE, Types.DATE, this.size());
		for (final GamaDate date : this) {
			map.put(date, date);
		}
		return map;
	}

}