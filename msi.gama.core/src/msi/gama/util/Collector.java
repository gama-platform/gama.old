package msi.gama.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

public abstract class Collector<E, C extends Collection<E>> implements ICollector<E> {

	public static class Unique<E> extends Collector<E, Set<E>> {

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

	C collect;

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.ICollector#add(E)
	 */
	@Override
	public void add(final E vd) {
		initCollect();
		collect.add(vd);
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
	public void remove(final E e) {
		if (collect == null)
			return;
		collect.remove(e);
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