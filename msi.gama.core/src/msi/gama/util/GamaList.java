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
import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 21 nov. 2008
 *
 * @todo Description
 */

public class GamaList<E> extends ArrayList<E> implements IList<E> {

	private IContainerType type;

	@Override
	public IContainerType getType() {
		return type;
	}

	protected GamaList(final int capacity, final IType contentType) {
		super(capacity);
		this.type = Types.LIST.of(contentType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gaml.attributes.interfaces.IValueProvider#listValue()
	 */
	@Override
	public IList listValue(final IScope scope, final IType contentsType, final boolean copy) {
		if ( !GamaType.requiresCasting(contentsType, getType().getContentType()) ) {
			if ( copy ) { return this.cloneWithContentType(contentsType); }
			return this;
		}
		GamaList clone = this.cloneWithContentType(contentsType);
		int n = size();
		for ( int i = 0; i < n; i++ ) {
			clone.setValueAtIndex(scope, i, get(i));
		}
		return clone;
		// return new GamaList(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gaml.attributes.interfaces.IValueProvider#matrixValue()
	 */
	@Override
	public IMatrix matrixValue(final IScope scope, final IType contentType, final boolean copy) {
		return GamaMatrixType.from(scope, this, contentType, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gaml.attributes.interfaces.IGamaValue#matrixValue(msi.gaml.types.GamaPoint)
	 */
	@Override
	public IMatrix matrixValue(final IScope scope, final IType contentsType, final ILocation preferredSize,
		final boolean copy) {
		return GamaMatrixType.from(scope, this, contentsType, preferredSize);
	}

	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return serialize(false);
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		final StringBuilder sb = new StringBuilder(size() * 10);
		sb.append('[');
		for ( int i = 0; i < size(); i++ ) {
			if ( i != 0 ) {
				sb.append(',');
			}
			sb.append(StringUtils.toGaml(get(i), includingBuiltIn));
		}
		sb.append(']');
		return sb.toString();
	}

	@Override
	public GamaMap mapValue(final IScope scope, final IType keyType, final IType contentsType, final boolean copy) {
		// 08/01/14: Change of behavior. A list now returns a map containing its contents casted to pairs.
		// Allows to build sets with the idiom: list <- map(list).values;
		IType kt = GamaType.findSpecificType(keyType, type.getContentType()); // not keyType() !
		IType ct = GamaType.findSpecificType(contentsType, type.getContentType());

		final GamaMap result = GamaMapFactory.create(kt, ct);
		for ( final E e : this ) {
			result.addValue(scope, GamaPairType.staticCast(scope, e, kt, ct, copy));
		}
		return result;
	}

	@Override
	public void addValue(final IScope scope, final E object) {
		super.add(buildValue(scope, object));
	}

	@Override
	public void addValueAtIndex(final IScope scope, final Object index, final E object) {
		super.add(buildIndex(scope, index), buildValue(scope, object));
	}

	@Override
	public void setValueAtIndex(final IScope scope, final Object index, final E value) {
		super.set(buildIndex(scope, index), buildValue(scope, value));
	}

	@Override
	public void addValues(final IScope scope, final IContainer values) {
		super.addAll(buildValues(scope, values));
	}

	@Override
	public void setAllValues(final IScope scope, final E value) {
		E element = buildValue(scope, value);
		for ( int i = 0, n = size(); i < n; i++ ) {
			super.set(i, element);
		}
	}

	/**
	 *
	 * Method add()
	 * @see java.util.ArrayList#add(java.lang.Object)
	 * @deprecated This method DOES NOT ensure type safety. Use addValue(IScope, Object) instead
	 */
	@Override
	public boolean add(final E value) {
		return super.add(value);
	}

	/**
	 *
	 * Method add()
	 * @see java.util.ArrayList#add(int, java.lang.Object)
	 * @deprecated This method DOES NOT ensure type safety. Use addValue(IScope, Integer, Object) instead
	 */
	@Override
	public void add(final int index, final E value) {
		super.add(index, value);
	}

	/**
	 *
	 * Method set()
	 * @see java.util.ArrayList#set(int, java.lang.Object)
	 * @deprecated This method DOES NOT ensure type safety. Use setValueAtIndex(IScope, Integer, Object) instead
	 */
	@Override
	public E set(final int index, final E value) {
		return super.set(index, value);
	}

	/**
	 *
	 * Method addAll()
	 * @see java.util.ArrayList#addAll(java.util.Collection)
	 * @deprecated This method DOES NOT ensure type safety. Use addValues(IScope, IContainer) instead
	 */
	@Override
	public boolean addAll(final Collection<? extends E> values) {
		return super.addAll(values);
	}

	/**
	 *
	 * Method addAll()
	 * @see java.util.ArrayList#addAll(int, java.util.Collection)
	 * @deprecated This method DOES NOT ensure type safety.
	 */
	@Override
	public boolean addAll(final int index, final Collection<? extends E> values) {
		return super.addAll(index, values);
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
			removeAll(values.listValue(scope, Types.NO_TYPE, false));
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
	public IContainer reverse(final IScope scope) {
		final IList list = copy(scope);
		Collections.reverse(list);
		return list;
	}

	public GamaList cloneWithContentType(final IType contentType) {
		GamaList clone = (GamaList) super.clone();
		clone.type = Types.LIST.of(contentType);
		return clone;
	}

	@Override
	public IList<E> copy(final IScope scope) {
		return cloneWithContentType(type.getContentType());
		// return GamaListFactory.create(scope, type.getContentType(), this);
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
		IList<Integer> l = index.listValue(scope, Types.INT, false);
		Collections.sort(l, Collections.reverseOrder());
		for ( Integer i : l ) {
			remove(i.intValue());
		}
	}

	/**
	 * Method buildValue()
	 * @see msi.gama.util.IContainer.Modifiable#buildValue(msi.gama.runtime.IScope, java.lang.Object, msi.gaml.types.IContainerType)
	 */
	protected E buildValue(final IScope scope, final Object object) {
		IType ct = type.getContentType();
		return (E) ct.cast(scope, object, null, false);
	}

	/**
	 * Method buildValues()
	 * @see msi.gama.util.IContainer.Modifiable#buildValues(msi.gama.runtime.IScope, msi.gama.util.IContainer, msi.gaml.types.IContainerType)
	 */
	protected IList<E> buildValues(final IScope scope, final IContainer objects) {
		return (IList<E>) type.cast(scope, objects, null, false);
	}

	/**
	 * Method buildIndex()
	 * @see msi.gama.util.IContainer.Modifiable#buildIndex(msi.gama.runtime.IScope, java.lang.Object, msi.gaml.types.IContainerType)
	 */
	protected Integer buildIndex(final IScope scope, final Object object) {
		return GamaIntegerType.staticCast(scope, object, null, false);
	}

	protected IContainer<?, Integer> buildIndexes(final IScope scope, final IContainer value) {
		IList<Integer> result = GamaListFactory.create(Types.INT);
		for ( Object o : value.iterable(scope) ) {
			result.add(buildIndex(scope, o));
		}
		return result;
	}

}
