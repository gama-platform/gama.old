package msi.gama.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;

import com.google.common.base.Objects;
import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.Iterables;

import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * A wrapper that tries to wrap a Set into an IList. Not all operations are meaningful (those with indices in
 * particular) and some are really costly (listIterators).
 *
 * @author drogoul
 *
 * @param <E>
 */
public class GamaListCollectionWrapper<E> extends ForwardingCollection<E> implements IList<E> {

	final Collection<E> wrapped;
	final IContainerType type;

	GamaListCollectionWrapper(final Collection<E> wrapped, final IType contents) {
		this.type = Types.LIST.of(contents);
		this.wrapped = wrapped;
	}

	@Override
	public boolean equals(final Object other) {
		if (other == this) { return true; }
		if (!(other instanceof IList)) { return false; }
		return GamaListFactory.equals(this, (IList) other);
	}

	@Override
	public IContainerType<?> getGamlType() {
		return type;
	}

	@Override
	protected Collection<E> delegate() {
		return wrapped;
	}

	@Override
	public boolean addAll(final int index, final Collection<? extends E> c) {
		if (wrapped instanceof List) { return ((List<E>) wrapped).addAll(index, c); }
		return addAll(c);
	}

	@Override
	public E get(final int index) {
		if (index > size() - 1) { return null; }
		if (wrapped instanceof List) { return ((List<E>) wrapped).get(index); }
		return Iterables.get(wrapped, index);
	}

	@Override
	public E set(final int index, final E element) {
		if (wrapped instanceof List) { return ((List<E>) wrapped).set(index, element); }
		final E old = get(index);
		// No real meaning for collections
		if (add(element)) {
			return old;
		} else {
			return null;
		}
	}

	@Override
	public void add(final int index, final E element) {
		if (wrapped instanceof List) {
			((List<E>) wrapped).add(index, element);
		} else {
			// No real meaning for collections
			add(element);
		}
	}

	@Override
	public E remove(final int index) {
		if (wrapped instanceof List) { return ((List<E>) wrapped).remove(index); }
		final E element = get(index);
		if (remove(element)) {
			return element;
		} else {
			return null;
		}
	}

	@Override
	public int indexOf(final Object o) {
		return Iterables.indexOf(wrapped, (o1) -> Objects.equal(o, o1));
	}

	@Override
	public int lastIndexOf(final Object o) {
		// Same as indexOf for collections
		if (wrapped instanceof List) { return ((List) wrapped).lastIndexOf(o); }
		return new ArrayList<>(wrapped).lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		if (wrapped instanceof List) { return ((List<E>) wrapped).listIterator(); }
		return new ArrayList<>(wrapped).listIterator();
	}

	@Override
	public ListIterator<E> listIterator(final int index) {
		if (wrapped instanceof List) { return ((List<E>) wrapped).listIterator(index); }
		return new ArrayList<>(wrapped).listIterator(index);
	}

	@Override
	public List<E> subList(final int fromIndex, final int toIndex) {
		if (wrapped instanceof List) { return ((List<E>) wrapped).subList(fromIndex, toIndex); }
		return this;
	}

	@Override
	public Spliterator<E> spliterator() {
		return wrapped.spliterator();
	}

	@Override
	public int hashCode() {
		return wrapped.hashCode();
	}

}
