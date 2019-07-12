package msi.gama.util;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import com.google.common.collect.Iterators;

import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

public class GamaListArrayWrapper<E> extends AbstractList<E> implements IList<E>, RandomAccess {

	private final E[] a;
	private final IContainerType type;

	GamaListArrayWrapper(final E[] array, final IType contents) {
		a = Objects.requireNonNull(array);
		type = Types.LIST.of(contents);
	}

	@Override
	public int size() {
		return a.length;
	}

	@Override
	public Object[] toArray() {
		return a.clone();
	}

	@Override
	public boolean equals(final Object other) {
		if (other == this) { return true; }
		if (!(other instanceof IList)) { return false; }
		return GamaListFactory.equals(this, (IList) other);
	}

	@Override
	@SuppressWarnings ("unchecked")
	public <T> T[] toArray(final T[] a) {
		final int size = size();
		if (a.length < size) { return Arrays.copyOf(this.a, size, (Class<? extends T[]>) a.getClass()); }
		System.arraycopy(this.a, 0, a, 0, size);
		if (a.length > size) {
			a[size] = null;
		}
		return a;
	}

	@Override
	public E get(final int index) {
		return a[index];
	}

	@Override
	public E set(final int index, final E element) {
		final E oldValue = a[index];
		a[index] = element;
		return oldValue;
	}

	@Override
	public int indexOf(final Object o) {
		final E[] a = this.a;
		if (o == null) {
			for (int i = 0; i < a.length; i++) {
				if (a[i] == null) { return i; }
			}
		} else {
			for (int i = 0; i < a.length; i++) {
				if (o.equals(a[i])) { return i; }
			}
		}
		return -1;
	}

	@Override
	public boolean contains(final Object o) {
		return indexOf(o) != -1;
	}

	@Override
	public Spliterator<E> spliterator() {
		return Spliterators.spliterator(a, Spliterator.ORDERED);
	}

	@Override
	public void forEach(final Consumer<? super E> action) {
		for (final E e : a) {
			action.accept(e);
		}
	}

	@Override
	public void replaceAll(final UnaryOperator<E> operator) {
		final E[] a = this.a;
		for (int i = 0; i < a.length; i++) {
			a[i] = operator.apply(a[i]);
		}
	}

	@Override
	public void sort(final Comparator<? super E> c) {
		Arrays.sort(a, c);
	}

	@Override
	public IContainerType<?> getGamlType() {
		return type;
	}

	@Override
	public Iterator<E> iterator() {
		return Iterators.forArray(a);
	}

}
