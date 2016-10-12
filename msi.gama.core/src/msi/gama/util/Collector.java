package msi.gama.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.collect.Sets;

import gnu.trove.set.hash.THashSet;
import gnu.trove.set.hash.TLinkedHashSet;

/**
 * A generic class that forwards additions to a set and prevents creating the
 * set if no additions occur
 * 
 * @author drogoul
 *
 * @param <E>
 */

public abstract class Collector<E, C extends Collection<E>> implements ICollector<E>, Collection<E> {

	@Override
	public boolean removeIf(final Predicate<? super E> filter) {
		if (collect != null)
			return collect.removeIf(filter);
		return ICollector.super.removeIf(filter);
	}

	@Override
	public Spliterator<E> spliterator() {
		if (collect != null)
			return collect.spliterator();
		return ICollector.super.spliterator();
	}

	@Override
	public Stream<E> stream() {
		if (collect != null)
			return collect.stream();
		return ICollector.super.stream();
	}

	@Override
	public Stream<E> parallelStream() {
		if (collect != null)
			return collect.parallelStream();
		return ICollector.super.parallelStream();
	}

	public static class Unique<E> extends Collector<E, Set<E>> {

		public static class Concurrent<E> extends Unique<E> {
			@Override
			protected void initCollect() {
				if (collect == null) {
					collect = Sets.newConcurrentHashSet();
				}
			}

			@Override
			public boolean remove(final Object o) {
				if (o == null)
					return false;
				return super.remove(o);
			}

			@Override
			public boolean removeAll(final Collection<?> o) {
				if (o == null)
					return false;
				return super.removeAll(o);
			}
		}

		@Override
		protected void initCollect() {
			if (collect == null) {
				collect = new THashSet<>();
			}
		}

		@Override
		public Set<E> items() {
			return collect == null ? Collections.EMPTY_SET : collect;
		}
	}

	public static class Ordered<E> extends Collector<E, List<E>> {

		@Override
		protected void initCollect() {
			if (collect == null) {
				collect = new ArrayList<>();
			}
		}

		@Override
		public List<E> items() {
			return collect == null ? Collections.EMPTY_LIST : collect;
		}
	}

	public static class UniqueOrdered<E> extends Unique<E> {

		@Override
		protected void initCollect() {
			if (collect == null) {
				collect = new TLinkedHashSet<>();
			}
		}

	}

	@Override
	public int size() {
		if (collect == null)
			return 0;
		return collect.size();
	}

	@Override
	public boolean contains(final Object o) {
		if (collect == null)
			return false;
		return collect.contains(o);
	}

	@Override
	public Object[] toArray() {
		if (collect == null)
			return new Object[0];
		return collect.toArray();
	}

	@Override
	public <T> T[] toArray(final T[] a) {
		if (collect == null)
			return a;
		return collect.toArray(a);
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		if (collect == null)
			return false;
		return collect.containsAll(c);
	}

	@Override
	public boolean addAll(final Collection<? extends E> c) {
		initCollect();
		return collect.addAll(c);
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		if (collect == null)
			return false;
		return collect.removeAll(c);
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		if (collect == null)
			return false;
		return collect.retainAll(c);
	}

	C collect;

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.ICollector#add(E)
	 */
	@Override
	public boolean add(final E vd) {
		initCollect();
		return collect.add(vd);
	}

	protected abstract void initCollect();

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.ICollector#items()
	 */
	@Override
	public abstract C items();

	@Override
	public Iterator<E> iterator() {
		return items().iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.ICollector#remove(E)
	 */
	@Override
	public boolean remove(final Object e) {
		if (collect == null)
			return false;
		return collect.remove(e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.ICollector#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return collect == null || collect.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.ICollector#clear()
	 */
	@Override
	public void clear() {
		collect = null;
	}
}