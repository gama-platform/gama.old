/*********************************************************************************************
 * 
 *
 * 'AbstractIndexList.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.util;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Cast;
import msi.gaml.types.IContainerType;

/**
 * Class IndexList.
 * 
 * @author drogoul
 * @since 25 févr. 2014
 * 
 */
public abstract class AbstractIndexList<T> implements IContainer.Addressable<Integer, T>,
	IContainer.Modifiable<Integer, T> {

	@Override
	public abstract boolean checkBounds(IScope scope, Object index, boolean forAdding);

	protected abstract IModifiableContainer<T, ?, T, ?> getContainer();

	@Override
	public T buildValue(final IScope scope, final Object object, final IContainerType containerType) {
		return getContainer().buildIndex(scope, object, containerType);
	}

	@Override
	public IContainer<?, T> buildValues(final IScope scope, final IContainer objects, final IContainerType containerType) {
		GamaList list = new GamaList();
		for ( Object o : objects.iterable(scope) ) {
			list.add(buildValue(scope, o, containerType));
		}
		return list;
	}

	@Override
	public Integer buildIndex(final IScope scope, final Object object, final IContainerType containerType) {
		return Cast.asInt(scope, object);
	}

	@Override
	public IContainer<?, Integer> buildIndexes(final IScope scope, final IContainer value,
		final IContainerType containerType) {
		GamaList list = new GamaList();
		for ( Object o : value.iterable(scope) ) {
			list.add(buildIndex(scope, o, containerType));
		}
		return list;
	}

	@Override
	public abstract T get(final IScope scope, final Integer index) throws GamaRuntimeException;

	@Override
	public T getFromIndicesList(final IScope scope, final IList<Integer> indices) throws GamaRuntimeException {
		return get(scope, indices.get(0));
	}

	@Override
	public void addValue(final IScope scope, final T object) {
		addContainerIndex(scope, object);
	}

	protected abstract void addContainerIndex(IScope scope, T object);

	@Override
	public void addValueAtIndex(final IScope scope, final Integer index, final T object) {
		insertContainerIndex(scope, index, object);
	}

	protected abstract void insertContainerIndex(IScope scope, Integer index, T object);

	@Override
	public void setValueAtIndex(final IScope scope, final Integer index, final T value) {
		changeContainerIndex(scope, index, value);
	}

	protected abstract void changeContainerIndex(IScope scope, Integer index, T value);

	@Override
	public void addVallues(final IScope scope, final IContainer values) {
		addContainerIndexes(scope, values);
	}

	protected abstract void addContainerIndexes(IScope scope, IContainer values);

	@Override
	public void setAllValues(final IScope scope, final T value) {
		// Nothing to do ?
	}

	@Override
	public void removeValue(final IScope scope, final Object value) {
		getContainer().removeIndex(scope, value);
	}

	@Override
	public void removeIndex(final IScope scope, final Object index) {
		if ( index instanceof Integer ) {
			getContainer().removeIndex(scope, get(scope, ((Integer) index).intValue()));
		}
	}

	@Override
	public void removeValues(final IScope scope, final IContainer values) {
		getContainer().removeIndexes(scope, values);

	}

	@Override
	public void removeAllOccurencesOfValue(final IScope scope, final Object value) {
		getContainer().removeIndex(scope, value);
	}

	@Override
	public void removeIndexes(final IScope scope, final IContainer<?, Object> indexes) {
		for ( Object i : indexes.iterable(scope) ) {
			if ( i instanceof Integer ) {
				removeValue(scope, get(scope, (Integer) i));
			}
		}
	}

}
