/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.util;

import java.util.*;
import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.matrix.*;
import msi.gaml.operators.Cast;
import com.google.common.collect.ImmutableList;

/**
 * Written by drogoul Modified on 21 nov. 2008
 * 
 * @todo Description
 */

public class GamaList<E> extends ArrayList<E> implements IList<E> {

	public static final GamaList EMPTY_LIST = new Immutable();
	public static final HashSet EMPTY_SET = new HashSet();

	private static class Immutable extends GamaList {

		@Override
		public Object get(final IScope scope, final Integer index) {
			return null;
		}

		@Override
		public boolean remove(final Object value) {
			return false;
		}

		@Override
		public void remove(final IScope scope, final Object index, final Object value, final boolean all) {}

		@Override
		public void add(final IScope scope, final Integer index, final Object value, final Object param,
			final boolean all, final boolean add) {}

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

	public GamaList(final Iterable i) {
		super(i instanceof Collection ? (Collection) i : ImmutableList.copyOf(i));
	}

	public GamaList(final Iterator<E> i) {
		super(ImmutableList.copyOf(i));
	}

	public GamaList(final Collection arg0) {
		super(arg0 == null ? Collections.EMPTY_LIST : arg0);
	}

	public GamaList(final Object[] tab) {
		super(tab.length + 2);
		for ( int i = 0, n = tab.length; i < n; i++ ) {
			add((E) tab[i]);
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
	public GamaList listValue(final IScope scope) {
		// AD 24/01/13 - modified by creating a new list to avoid side effects
		// TODO Is the copy necessary in all cases ? It seems a bit overkill !

		return this;
		// return new GamaList(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gaml.attributes.interfaces.IValueProvider#matrixValue()
	 */
	@Override
	public IMatrix matrixValue(final IScope scope) {
		return new GamaObjectMatrix(scope, this, false, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gaml.attributes.interfaces.IGamaValue#matrixValue(msi.gaml.types.GamaPoint)
	 */
	@Override
	public IMatrix matrixValue(final IScope scope, final ILocation preferredSize) {
		return new GamaObjectMatrix(scope, this, false, preferredSize);
	}

	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		final StringBuilder sb = new StringBuilder(size() * 5);
		sb.append('[');
		for ( int i = 0, n = size(); i < n; i++ ) {
			if ( i != 0 ) {
				sb.append(',');
			}
			sb.append(Cast.asString(scope, get(i)));
		}
		sb.append(']');
		return sb.toString();
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
	public GamaMap mapValue(final IScope scope) {

		// TODO REVOIR CA POUR RENVOYER PLUTOT UNE MAP<INTEGER, E>
		final GamaMap result = new GamaMap();
		if ( isPairs(this) ) {
			for ( final E e : this ) {
				final GamaPair pair = (GamaPair) e;
				result.put(pair.first(), pair.last());
			}
		} else {
			// expects a list containing alternatively keys and values
			// TODO verify if size is odd or even
			for ( int i = 0, end = size(); i < end; i += 2 ) {
				result.put(get(i), get(i + 1));
			}
		}
		return result;
	}

	public static boolean isPairs(final IList list) {
		for ( final Object obj : list ) {
			if ( !(obj instanceof GamaPair) ) { return false; }
		}
		return true;
	}

	@Override
	public void add(final IScope scope, final Integer i, final Object value, final Object param, final boolean all,
		final boolean add) throws GamaRuntimeException {
		if ( i == null ) {
			if ( all && !add ) {
				for ( int index = 0, n = size(); index < n; index++ ) {
					set(index, (E) value);
				}
			} else if ( !all && add ) {
				add((E) value);
			} else if ( all && add && value instanceof IContainer ) {
				addAll(((IContainer) value).listValue(scope));
			}
		} else {
			if ( add ) {
				if ( all && value instanceof IContainer ) {
					addAll(i, ((IContainer) value).listValue(scope));
				} else {
					add(i, (E) value);
				}
			} else {
				set(i, (E) value);
			}
		}
	}

	@Override
	public void remove(final IScope scope, final Object index, final Object value, final boolean all) {
		if ( index == null ) {
			if ( all ) {
				if ( value instanceof IContainer ) {
					for ( final Object o : ((IContainer) value).iterable(scope) ) {
						remove(scope, null, o, true);
					}
				} else if ( value != null ) {
					for ( final Iterator iterator = iterator(); iterator.hasNext(); ) {
						final Object obj = iterator.next();
						if ( obj.equals(value) ) {
							iterator.remove();
						}
					}
				} else {
					clear();
				}
			} else {
				remove(value);
			}
		} else {
			final int i = Cast.asInt(scope, index);
			remove(i);
		}
	}

	@Override
	public E first(final IScope scope) {
		if ( size() == 0 ) { return null; }
		return get(0);
	}

	@Override
	public E last(final IScope scope) {
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
	public boolean checkBounds(final Integer index, final boolean forAdding) {
		final int size = size();
		final boolean upper = forAdding ? index <= size : index < size;
		return index >= 0 && upper;
	}

	@Override
	public E any(final IScope scope) {
		if ( isEmpty() ) { return null; }

		final int i = GAMA.getRandom().between(0, size() - 1);
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
	public Iterable<E> iterable(final IScope scope) {
		return this;
	}

	@Override
	public E getFromIndicesList(final IScope scope, final IList indices) throws GamaRuntimeException {
		if ( indices == null || indices.isEmpty() ) { return null; }
		return get(scope, Cast.asInt(scope, indices.get(0)));
		// We do not consider the case where multiple indices are used. Maybe could be used in the
		// future to return a list of values ?
	}
}
