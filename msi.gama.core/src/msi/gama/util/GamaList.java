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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
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
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.matrix.*;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 21 nov. 2008
 * 
 * @todo Description
 */

public class GamaList<E> extends ArrayList<E> implements IList<E> {

	public static final GamaList EMPTY_LIST = new Immutable();

	private static class Immutable extends GamaList {

		@Override
		public Object get(final Integer index) {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see msi.gama.interfaces.IGamaContainer#isFixedLength()
		 */
		@Override
		public boolean isFixedLength() {
			return true;
		}

		@Override
		public boolean remove(final Object value) {
			return false;
		}

		@Override
		public Object removeAt(final Integer index) {
			return null;
		}

		@Override
		public void add(final Object value, final Object param) {};

		@Override
		public void add(final Integer index, final Object value, final Object param) {}

		@Override
		public void put(final Integer index, final Object value, final Object param) {}

		@Override
		public void putAll(final Object value, final Object param) {}

		@Override
		public GamaList clone() {
			return this;
		}

		@Override
		public GamaList copy() {
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
		for ( double d : tab ) {
			add((E) Double.valueOf(d));
		}
	}

	public GamaList(final int[] tab) {
		super(tab.length + 2);
		for ( int d : tab ) {
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
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gaml.attributes.interfaces.IValueProvider#matrixValue()
	 */
	@Override
	public IMatrix matrixValue(final IScope scope) {
		return new GamaObjectMatrix(this, false, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gaml.attributes.interfaces.IGamaValue#matrixValue(msi.gaml.types.GamaPoint)
	 */
	@Override
	public IMatrix matrixValue(final IScope scope, final ILocation preferredSize) {
		return new GamaObjectMatrix(this, false, preferredSize);
	}

	@Override
	// @operator(value = "string", can_be_const = true)
	public String stringValue() throws GamaRuntimeException {
		final StringBuilder sb = new StringBuilder();
		sb.append('[');
		for ( int i = 0; i < size(); i++ ) {
			if ( i != 0 ) {
				sb.append(',');
			}
			sb.append(Cast.asString(GAMA.getDefaultScope(), get(i)));
		}
		sb.append(']');
		return sb.toString();
	}

	@Override
	public IType type() {
		return Types.get(IType.LIST);
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

	// @Override
	// public String toJava() {
	// final StringBuilder sb = new StringBuilder(size() * 10);
	// sb.append("new ").append(getClass().getCanonicalName()).append('(');
	// for ( int i = 0; i < size(); i++ ) {
	// if ( i != 0 ) {
	// sb.append(',');
	// }
	// sb.append(Cast.toJava(get(i)));
	// }
	// sb.append(')');
	// return sb.toString();
	// }

	@Override
	public GamaMap mapValue(final IScope scope) {

		// TODO REVOIR CA POUR RENVOYER PLUTOT UNE MAP<INTEGER, E>
		final GamaMap result = new GamaMap();
		if ( isPairs() ) {
			for ( final E e : this ) {
				GamaPair pair = (GamaPair) e;
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

	private boolean isPairs() {
		for ( final Object obj : this ) {
			if ( !(obj instanceof GamaPair) ) { return false; }
		}
		return true;
	}

	@Override
	public void add(final E value, final Object param) throws GamaRuntimeException {
		add(value);
	}

	@Override
	public void add(final Integer i, final E value, final Object param) throws GamaRuntimeException {
		// all verifications have normally been already done in the add command
		add(i, value);
	}

	@Override
	public boolean removeFirst(final E value) {
		return remove(value);
	}

	@Override
	public boolean removeAll(final IContainer<?, E> list) {
		boolean removed = false;
		boolean result = true;
		for ( Object value : list ) {
			removed = false;
			for ( Iterator iterator = iterator(); iterator.hasNext(); ) {
				Object obj = iterator.next();
				if ( obj.equals(value) ) {
					iterator.remove();
					removed = true;
				}
			}
			if ( removed == false ) {
				result = false;
			}
		}
		return result;
	}

	@Override
	public Object removeAt(final Integer i) {
		// All verifications have been done in the remove command
		return remove(i);
	}

	@Override
	public void put(final Integer i, final E value, final Object param) {
		// All verifications have been done in the put command
		set(i, value); // Attention au casting
	}

	@Override
	public E first() {
		if ( size() == 0 ) { return null; }
		return get(0);
	}

	@Override
	public E last() {
		if ( size() == 0 ) { return null; }
		return get(size() - 1);
	}

	// @Override
	// public Boolean contains(final Object o) {
	// return contains(o);
	// }

	@Override
	public E get(final Integer index) {
		return get(index.intValue());
	}

	@Override
	public Object sum(final IScope scope) throws GamaRuntimeException {
		// OPTIMISER EN FONCTION DU TYPE -- DONC PASSER LE TYPE EN PARAMETRE D'UNE FACON OU D'UNE
		// AUTRE.
		boolean allInt = true;
		boolean allFloat = true;
		boolean allPoint = true;
		for ( int i = 0, n = size(); i < n; i++ ) {
			Object o = get(i);
			if ( o instanceof Integer ) {
				allPoint = false;
			} else if ( o instanceof Double ) {
				allInt = false;
				allPoint = false;
			} else if ( o instanceof ILocation ) {
				allInt = false;
				allFloat = false;
			}
		}
		if ( allInt ) {
			Integer sum = 0;
			for ( int i = 0, n = size(); i < n; i++ ) {
				Integer ii = Cast.asInt(scope, get(i));
				if ( ii != null ) {
					sum += ii;
				}
			}
			return sum;
		}
		if ( allFloat ) {
			Double sum = 0d;
			for ( int i = 0, n = size(); i < n; i++ ) {
				// Double dd = (Double) get(i);
				Double dd = Cast.asFloat(scope, get(i));
				if ( dd != null ) {
					sum += dd;
				}
			}
			return sum;
		}
		if ( allPoint ) {
			ILocation sum = new GamaPoint(0, 0);
			for ( int i = 0, n = size(); i < n; i++ ) {
				ILocation o = Cast.asPoint(scope, get(i));
				if ( o != null ) {
					sum.add(o);
				}
			}
			return sum;
		}

		// In case there is something else in the list than ints, floats or points

		// Throw an exception ?

		Double sum = 0d;
		for ( int i = 0, n = size(); i < n; i++ ) {
			sum += Cast.asFloat(null, get(i));
		}
		return sum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gaml.types.IGamaContainer#getProduct()
	 */
	@Override
	public Object product(final IScope scope) throws GamaRuntimeException {
		boolean allInt = true;
		boolean allFloat = true;
		boolean allPoint = true;
		for ( int i = 0, n = size(); i < n; i++ ) {
			Object o = get(i);
			if ( o instanceof Integer ) {
				allPoint = false;
			} else if ( o instanceof Double ) {
				allInt = false;
				allPoint = false;
			} else if ( o instanceof ILocation ) {
				allInt = false;
				allFloat = false;
			}
		}
		if ( allInt ) {
			Integer mul = 1;
			for ( int i = 0, n = size(); i < n; i++ ) {
				Integer ii = Cast.asInt(scope, get(i));
				if ( ii != null ) {
					mul *= ii;
				}
			}
			return mul;
		}
		if ( allFloat ) {
			Double mul = 1d;
			for ( int i = 0, n = size(); i < n; i++ ) {
				// Double dd = (Double) get(i);
				Double dd = Cast.asFloat(scope, get(i));
				if ( dd != null ) {
					mul *= dd;
				}
			}
			return mul;
		}
		if ( allPoint ) {
			GamaPoint mul = new GamaPoint(1, 1);
			for ( int i = 0, n = size(); i < n; i++ ) {
				ILocation o = Cast.asPoint(scope, get(i));
				if ( o != null ) {
					mul.setLocation(o.getX() * mul.x, o.getY() * mul.y);
				}
			}
			return mul;
		}

		// In case there is something else in the list than ints, floats or points

		// Throw an exception ?

		Double mul = 0d;
		for ( int i = 0, n = size(); i < n; i++ ) {
			mul *= Cast.asFloat(null, get(i));
		}
		return mul;
	}

	@Override
	public int length() {
		return size();
	}

	@Override
	public E max(final IScope scope) throws GamaRuntimeException {
		boolean allInt = true;
		boolean allFloat = true;
		boolean allPoint = true;
		for ( int i = 0, n = size(); i < n; i++ ) {
			Object o = get(i);
			if ( o instanceof Integer ) {
				allPoint = false;
			} else if ( o instanceof Double ) {
				allInt = false;
				allPoint = false;
			} else if ( o instanceof ILocation ) {
				allInt = false;
				allFloat = false;
			}
		}
		if ( allInt ) {
			Integer max = Integer.MIN_VALUE;
			for ( int i = 0, n = size(); i < n; i++ ) {
				Integer o = Cast.asInt(scope, get(i));
				if ( o > max ) {
					max = o;
				}
			}
			return (E) max;
		}
		if ( allFloat ) {
			Double max = Double.MIN_VALUE;
			for ( int i = 0, n = size(); i < n; i++ ) {
				// Double o = (Double) get(i);
				Double o = Cast.asFloat(scope, get(i));
				if ( o > max ) {
					max = o;
				}
			}
			return (E) max;
		}
		if ( allPoint ) {
			ILocation max = new GamaPoint(Double.MIN_VALUE, Double.MIN_VALUE);
			for ( int i = 0, n = size(); i < n; i++ ) {
				ILocation o = Cast.asPoint(scope, get(i));
				if ( o.compareTo(max) > 0 ) {
					max = o;
				}
			}
			return (E) max;
		}

		// In case there is something else in the list other than ints, floats or points

		Double max = Double.MIN_VALUE;
		for ( int i = 0, n = size(); i < n; i++ ) {
			Double o = Cast.asFloat(scope, get(i));
			if ( o > max ) {
				max = o;
			}
		}
		return (E) max;

	}

	@Override
	public E min(final IScope scope) throws GamaRuntimeException {
		boolean allInt = true;
		boolean allFloat = true;
		boolean allPoint = true;
		for ( int i = 0, n = size(); i < n; i++ ) {
			Object o = get(i);
			if ( o instanceof Integer ) {
				allPoint = false;
			} else if ( o instanceof Double ) {
				allInt = false;
				allPoint = false;
			} else if ( o instanceof ILocation ) {
				allInt = false;
				allFloat = false;
			}
		}
		if ( allInt ) {
			Integer min = Integer.MAX_VALUE;
			for ( int i = 0, n = size(); i < n; i++ ) {
				Integer o = Cast.asInt(scope, get(i));
				if ( o < min ) {
					min = o;
				}
			}
			return (E) min;
		}
		if ( allFloat ) {
			Double min = Double.MAX_VALUE;
			for ( int i = 0, n = size(); i < n; i++ ) {
				// Double o = (Double) get(i);
				Double o = Cast.asFloat(scope, get(i));
				if ( o < min ) {
					min = o;
				}
			}
			return (E) min;
		}
		if ( allPoint ) {
			ILocation min = new GamaPoint(Double.MIN_VALUE, Double.MIN_VALUE);
			for ( int i = 0, n = size(); i < n; i++ ) {
				ILocation o = Cast.asPoint(scope, get(i));
				if ( o.compareTo(min) < 0 ) {
					min = o;
				}
			}
			return (E) min;
		}

		// In case there is something else in the list other than ints, floats or points

		Double min = Double.MIN_VALUE;
		for ( int i = 0, n = size(); i < n; i++ ) {
			Double o = Cast.asFloat(scope, get(i));
			if ( o < min ) {
				min = o;
			}
		}
		return (E) min;

	}

	@Override
	public IContainer<Integer, E> reverse() {
		GamaList list = clone();
		Collections.reverse(list);
		return list;
	}

	@Override
	public void putAll(final E value, final Object param) {
		for ( int i = 0, n = size(); i < n; i++ ) {
			set(i, value);
		}

	}

	public static GamaList with(final Object ... a) {
		return new GamaList(a);
	}

	@Override
	public GamaList clone() {
		return (GamaList) super.clone();
	}

	@Override
	public GamaList copy() {
		return new GamaList(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#checkIndex(java.lang.Object)
	 */
	@Override
	public boolean checkIndex(final Object index) {
		return index instanceof Integer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#checkValue(java.lang.Object)
	 */
	@Override
	public boolean checkValue(final Object value) {
		return true;// Maybe a check on the type would be possible ?
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#checkBounds(java.lang.Object)
	 */
	@Override
	public boolean checkBounds(final Integer index, final boolean forAdding) {
		int size = size();
		boolean upper = forAdding ? index <= size : index < size;
		return index >= 0 && upper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#isFixedLength()
	 */
	@Override
	public boolean isFixedLength() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#addAll(msi.gama.interfaces.IGamaContainer,
	 * java.lang.Object)
	 */
	@Override
	public void addAll(final IContainer list, final Object param) throws GamaRuntimeException {
		for ( Object o : list ) {
			add((E) o);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#addAll(java.lang.Object,
	 * msi.gama.interfaces.IGamaContainer, java.lang.Object)
	 */
	@Override
	public void addAll(final Integer index, final IContainer list, final Object param)
		throws GamaRuntimeException {
		int i = index;
		for ( Object o : list ) {
			add(i, (E) o);
			i++;
		}
	}

	@Override
	public E any() {
		if ( isEmpty() ) { return null; }
		int i = GAMA.getRandom().between(0, size() - 1);
		return get(i);
	}
}
