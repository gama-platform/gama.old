/*********************************************************************************************
 * 
 * 
 * 'GamaList.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.util;

import java.util.*;
import msi.gama.common.interfaces.IValue;
import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;
import com.google.common.collect.ImmutableList;

/**
 * Written by drogoul Modified on 21 nov. 2008
 * 
 * @todo Description
 */

public class GamaList<E> extends ArrayList<E> implements IList<E> {

	public static final GamaList EMPTY_LIST = new Empty();
	public static final HashSet EMPTY_SET = new HashSet();

	// private class IndexList extends AbstractIndexList<Integer> {
	//
	// /**
	// * Method checkBounds()
	// * @see msi.gama.util.AbstractIndexList#checkBounds(msi.gama.runtime.IScope, java.lang.Object, boolean)
	// */
	// @Override
	// public boolean checkBounds(final IScope scope, final Object index, final boolean forAdding) {
	// // TODO Verify this as it should be an internal method
	// return GamaList.this.checkBounds(scope, index, forAdding);
	// }
	//
	// /**
	// * Method get()
	// * @see msi.gama.util.AbstractIndexList#get(msi.gama.runtime.IScope, java.lang.Integer)
	// */
	// @Override
	// public Integer get(final IScope scope, final Integer index) throws GamaRuntimeException {
	// return index;
	// }
	//
	// /**
	// * Method addContainerIndex()
	// * @see msi.gama.util.AbstractIndexList#addContainerIndex(msi.gama.runtime.IScope, java.lang.Object)
	// */
	// @Override
	// protected void addContainerIndex(final IScope scope, final Integer object) {}
	//
	// /**
	// * Method insertContainerIndex()
	// * @see msi.gama.util.AbstractIndexList#insertContainerIndex(msi.gama.runtime.IScope, java.lang.Integer,
	// * java.lang.Object)
	// */
	// @Override
	// protected void insertContainerIndex(final IScope scope, final Integer index, final Integer object) {}
	//
	// /**
	// * Method changeContainerIndex()
	// * @see msi.gama.util.AbstractIndexList#changeContainerIndex(msi.gama.runtime.IScope, java.lang.Integer,
	// * java.lang.Object)
	// */
	// @Override
	// protected void changeContainerIndex(final IScope scope, final Integer index, final Integer value) {}
	//
	// /**
	// * Method addContainerIndexes()
	// * @see msi.gama.util.AbstractIndexList#addContainerIndexes(msi.gama.runtime.IScope, msi.gama.util.IContainer)
	// */
	// @Override
	// protected void addContainerIndexes(final IScope scope, final IContainer values) {}
	//
	// /**
	// * Method getContainer()
	// * @see msi.gama.util.AbstractIndexList#getContainer()
	// */
	// @Override
	// protected IModifiableContainer<Integer, ?, Integer, ?> getContainer() {
	// return GamaList.this;
	// }
	// }

	private static class Empty extends GamaList {

		@Override
		public Object get(final IScope scope, final Integer index) {
			return null;
		}

		@Override
		public boolean remove(final Object value) {
			return false;
		}

		@Override
		public Object remove(final int index) {
			return null;
		}

		@Override
		public GamaList clone() {
			return this;
		}

		@Override
		public final GamaList copy(final IScope scope) {
			return this;
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public Object set(final int index, final Object element) {
			return null;
		}

		@Override
		public boolean add(final Object e) {
			return false;
		}

		@Override
		public void add(final int index, final Object element) {}

		@Override
		public boolean addAll(final Collection c) {
			return false;
		}

		@Override
		public boolean addAll(final int index, final Collection c) {
			return false;
		}

	}

	public GamaList() {
		super();
	}

	public GamaList(final IScope scope, final IContainer<?, E> container) {
		this(container.iterable(scope));
	}

	public GamaList(final java.lang.Iterable i) {
		super(i instanceof Collection ? (Collection) i : ImmutableList.copyOf(i));
	}

	public GamaList(final Iterator<E> i) {
		super(ImmutableList.copyOf(i));
	}

	public GamaList(final Collection arg0) {
		super(arg0 == null ? Collections.EMPTY_LIST : arg0);
	}

	public GamaList(final Object[] tab) {
		super(tab == null ? 0 : tab.length + 2);
		if ( tab != null ) {
			for ( int i = 0, n = tab.length; i < n; i++ ) {
				add((E) tab[i]);
			}
		}
	}

	public GamaList(final double[] tab) {
		super(tab.length + 2);
		for ( final double d : tab ) {
			add((E) Double.valueOf(d));
		}
	}

	public GamaList(final int[] tab) {
		super(tab.length + 2);
		for ( final int d : tab ) {
			add((E) Integer.valueOf(d));
		}
	}

	public GamaList(final int capacity) {
		super(capacity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gaml.attributes.interfaces.IValueProvider#listValue()
	 */
	@Override
	public GamaList listValue(final IScope scope, final IType contentsType) {
		if ( contentsType == null || contentsType.id() == IType.NONE ) { return new GamaList(this); }
		// AD 24/01/13 - modified by creating a new list to avoid side effects
		// TODO Is the copy necessary in all cases ? It seems a bit overkill !

		GamaList copy = this.clone();
		int n = copy.size();
		for ( int i = 0; i < n; i++ ) {
			copy.set(i, GamaType.toType(scope, copy.get(i), contentsType));
		}
		return copy;
		// return new GamaList(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gaml.attributes.interfaces.IValueProvider#matrixValue()
	 */
	@Override
	public IMatrix matrixValue(final IScope scope, final IType contentType) {
		return GamaMatrixType.from(scope, this, contentType, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gaml.attributes.interfaces.IGamaValue#matrixValue(msi.gaml.types.GamaPoint)
	 */
	@Override
	public IMatrix matrixValue(final IScope scope, final IType contentsType, final ILocation preferredSize) {
		return GamaMatrixType.from(scope, this, contentsType, preferredSize);
	}

	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return toGaml();
	}

	@Override
	public String toGaml() {
		final StringBuilder sb = new StringBuilder(size() * 10);
		sb.append('[');
		for ( int i = 0; i < size(); i++ ) {
			if ( i != 0 ) {
				sb.append(',');
			}
			sb.append(StringUtils.toGaml(get(i)));
		}
		sb.append(']');
		return sb.toString();
	}

	@Override
	public GamaMap mapValue(final IScope scope, final IType keyType, final IType contentsType) {
		// 08/01/14: Change of behavior. A list now returns a map containing its contents casted to pairs.
		// Allows to build sets with the idiom: list <- map(list).values;
		final GamaMap result = new GamaMap();
		final IType<GamaPair> pairType = Types.get(IType.PAIR);
		for ( final E e : this ) {
			result.addValue(scope, pairType.cast(scope, e, null, keyType, contentsType));
		}
		return result;
	}

	@Override
	public void addValue(final IScope scope, final E object) {
		add(object);
	}

	@Override
	public void addValueAtIndex(final IScope scope, final Integer index, final E object) {
		add(index, object);
	}

	@Override
	public void setValueAtIndex(final IScope scope, final Integer index, final E value) {
		set(index, value);
	}

	@Override
	public void addVallues(final IScope scope, final IContainer values) {
		if ( values instanceof List ) {
			addAll((IList) values);
		} else {
			addAll(values.listValue(scope, Types.NO_TYPE));
		}
	}

	@Override
	public void setAllValues(final IScope scope, final E value) {
		if ( value instanceof IValue ) {
			IValue v = (IValue) value;
			for ( int i = 0, n = size(); i < n; i++ ) {
				set(i, (E) v.copy(scope));
			}
		} else {
			for ( int i = 0, n = size(); i < n; i++ ) {
				set(i, value);
			}
		}
	}

	@Override
	public void removeValue(final IScope scope, final Object value) {
		remove(value);
	}

	@Override
	public void removeIndex(final IScope scope, final Object index) {
		if ( index instanceof Integer ) {
			remove(((Integer) index).intValue());
		}
	}

	@Override
	public void removeValues(final IScope scope, final IContainer values) {
		if ( values instanceof Collection ) {
			removeAll((Collection) values);
		} else {
			removeAll(values.listValue(scope, Types.NO_TYPE));
		}
	}

	@Override
	public void removeAllOccurencesOfValue(final IScope scope, final Object value) {
		for ( final Iterator iterator = iterator(); iterator.hasNext(); ) {
			final Object obj = iterator.next();
			if ( obj.equals(value) ) {
				iterator.remove();
			}
		}
	}

	@Override
	public E firstValue(final IScope scope) {
		if ( size() == 0 ) { return null; }
		return get(0);
	}

	@Override
	public E lastValue(final IScope scope) {
		if ( size() == 0 ) { return null; }
		return get(size() - 1);
	}

	@Override
	public E get(final IScope scope, final Integer index) {
		return get(index.intValue());
	}

	@Override
	public int length(final IScope scope) {
		return size();
	}

	@Override
	public IContainer<Integer, E> reverse(final IScope scope) {
		final GamaList list = clone();
		Collections.reverse(list);
		return list;
	}

	public static <T> GamaList with(final T ... a) {
		return new GamaList<T>(a);
	}

	@Override
	public GamaList clone() {
		return (GamaList) super.clone();
	}

	@Override
	public GamaList<E> copy(final IScope scope) {
		return new GamaList(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#checkBounds(java.lang.Object)
	 */
	@Override
	public boolean checkBounds(final IScope scope, final Object object, final boolean forAdding) {
		if ( object instanceof Integer ) {
			Integer index = (Integer) object;
			final int size = size();
			final boolean upper = forAdding ? index <= size : index < size;
			return index >= 0 && upper;
		} else if ( object instanceof IContainer ) {
			for ( Object o : ((IContainer) object).iterable(scope) ) {
				if ( !checkBounds(scope, o, forAdding) ) { return false; }
			}
		}
		return false;
	}

	@Override
	public E anyValue(final IScope scope) {
		if ( isEmpty() ) { return null; }

		final int i = scope.getRandom().between(0, size() - 1);
		return get(i);
	}

	@Override
	public boolean contains(final IScope scope, final Object o) throws GamaRuntimeException {
		return contains(o);
	}

	@Override
	public boolean isEmpty(final IScope scope) {
		return isEmpty();
	}

	@Override
	public java.lang.Iterable<E> iterable(final IScope scope) {
		return this;
	}

	@Override
	public E getFromIndicesList(final IScope scope, final IList indices) throws GamaRuntimeException {
		if ( indices == null || indices.isEmpty() ) { return null; }
		return get(scope, Cast.asInt(scope, indices.get(0)));
		// We do not consider the case where multiple indices are used. Maybe could be used in the
		// future to return a list of values ?
	}

	/**
	 * Method removeIndexes()
	 * @see msi.gama.util.IContainer.Modifiable#removeIndexes(msi.gama.runtime.IScope, msi.gama.util.IContainer)
	 */
	@Override
	public void removeIndexes(final IScope scope, final IContainer<?, Object> index) {
		IList<Integer> l = index.listValue(scope, Types.get(IType.INT));
		Collections.sort(l, Collections.reverseOrder());
		for ( Integer i : l ) {
			remove(i.intValue());
		}
	}

	/**
	 * Method buildValue()
	 * @see msi.gama.util.IContainer.Modifiable#buildValue(msi.gama.runtime.IScope, java.lang.Object, msi.gaml.types.IContainerType)
	 */
	@Override
	public E buildValue(final IScope scope, final Object object, final IContainerType containerType) {
		return (E) containerType.getContentType().cast(scope, object, null);
	}

	/**
	 * Method buildValues()
	 * @see msi.gama.util.IContainer.Modifiable#buildValues(msi.gama.runtime.IScope, msi.gama.util.IContainer, msi.gaml.types.IContainerType)
	 */
	@Override
	public IContainer<?, E>
		buildValues(final IScope scope, final IContainer objects, final IContainerType containerType) {
		return containerType.cast(scope, objects, null);
	}

	/**
	 * Method buildIndex()
	 * @see msi.gama.util.IContainer.Modifiable#buildIndex(msi.gama.runtime.IScope, java.lang.Object, msi.gaml.types.IContainerType)
	 */
	@Override
	public Integer buildIndex(final IScope scope, final Object object, final IContainerType containerType) {
		return GamaIntegerType.staticCast(scope, object, null);
	}

	@Override
	public IContainer<?, Integer> buildIndexes(final IScope scope, final IContainer value,
		final IContainerType containerType) {
		IList<Integer> result = new GamaList();
		for ( Object o : value.iterable(scope) ) {
			result.add(buildIndex(scope, o, containerType));
		}
		return result;
		// We reverse the list of indices in order to avoid the side effect of sequential order, where a previous
		// removal would alter the following indices.
		// Not necessary for add and put return result.reverse(scope);
	}

}
