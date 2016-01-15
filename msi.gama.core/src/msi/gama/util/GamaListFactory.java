/**
 * Created by drogoul, 30 janv. 2015
 *
 */
package msi.gama.util;

import java.util.*;
import com.google.common.collect.Iterables;
import msi.gama.runtime.IScope;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.*;

/**
 * Class GamaListFactory. The factory for creating lists from various other objects. All the methods that accept the scope as a parameter
 * will observe the contract of GAML containers, which is that the objects contained in the list will be casted to the content type of the list.
 * To avoid unecessary castings, some methods (without the scope parameter) will simply copy the objects.
 *
 * @author drogoul
 * @since 30 janv. 2015
 *
 */
public class GamaListFactory {

	private static final int DEFAULT_SIZE = 10;
	public static final GamaList EMPTY_LIST = new GamaList(0, Types.NO_TYPE);

	/**
	 * Create a GamaList from an array of objects, but does not attempt casting its values.
	 *
	 * @param contentType
	 * @param collection
	 * @warning ***WARNING*** This operation can end up putting values of the wrong type into the list
	 * @return
	 */

	public static <T> IList<T> createWithoutCasting(final IType contentType, final T ... objects) {
		IList<T> list = create(contentType, objects.length);
		list.addAll(Arrays.asList(objects));
		return list;
	}

	/**
	 * Create a GamaList from an iterable, but does not attempt casting its values.
	 *
	 * @param contentType
	 * @param collection
	 * @warning ***WARNING*** This operation can end up putting values of the wrong type into the list
	 * @return
	 */

	public static <T> IList<T> createWithoutCasting(final IType contentType, final Iterable<T> objects) {
		IList<T> list = create(contentType);
		Iterables.addAll(list, objects);
		return list;
	}

	private static void castAndAdd(final IScope scope, final IList list, final Object o) {
		list.addValue(scope, o);
	}

	public static IList create(final IScope scope, final IType contentType, final IContainer container) {
		if ( container == null ) { return create(contentType); }
		if ( GamaType.requiresCasting(contentType, container.getType().getContentType()) ) {
			return create(scope, contentType, container.iterable(scope));
		} else {
			return createWithoutCasting(contentType, container.iterable(scope));
		}
	}

	public static <T> IList<T> create(final IScope scope, final IType contentType, final IList<T> container) {
		if ( container == null ) { return create(contentType); }
		if ( GamaType.requiresCasting(contentType, container.getType().getContentType()) ) {
			return create(scope, contentType, (Collection) container);
		} else {
			return createWithoutCasting(contentType, container);
		}
	}

	public static <T> IList<T> create(final IScope scope, final IType contentType, final Iterable<T> iterable) {
		IList<T> list = create(contentType);
		for ( Object o : iterable ) {
			castAndAdd(scope, list, o);
		}
		return list;
	}

	public static <T> IList<T> create(final IScope scope, final IType contentType, final Iterator<T> iterator) {
		IList<T> list = create(contentType);
		if ( iterator != null ) {
			while (iterator.hasNext()) {
				castAndAdd(scope, list, iterator.next());
			}
		}
		return list;
	}

	public static <T> IList<T> create(final IScope scope, final IType contentType, final Enumeration<T> iterator) {
		IList<T> list = create(contentType);
		if ( iterator != null ) {
			while (iterator.hasMoreElements()) {
				castAndAdd(scope, list, iterator.nextElement());
			}
		}
		return list;
	}

	public static <T> IList<T> create(final IScope scope, final IType contentType, final T ... objects) {
		IList<T> list = create(contentType, objects == null ? 0 : objects.length);
		if ( objects != null ) {
			for ( Object o : objects ) {
				castAndAdd(scope, list, o);
			}
		}
		return list;
	}

	public static IList create(final IScope scope, final IType contentType, final int[] ints) {
		IList list = create(contentType, ints == null ? 0 : ints.length);
		if ( ints != null ) {
			for ( int o : ints ) {
				castAndAdd(scope, list, Integer.valueOf(o));
			}
		}
		return list;
	}

	public static IList create(final IScope scope, final IExpression fillExpr, final Integer size) {
		if ( fillExpr == null ) { return create(Types.NO_TYPE, size); }
		final Object[] contents = new Object[size];
		IType contentType = fillExpr.getType();
		// 10/01/14. Cannot use Arrays.fill() everywhere: see Issue 778.
		if ( fillExpr.isConst() ) {
			Arrays.fill(contents, fillExpr.value(scope));
		} else {
			for ( int i = 0; i < contents.length; i++ ) {
				contents[i] = fillExpr.value(scope);
			}
		}
		return create(scope, contentType, contents);
	}

	public static IList create(final IScope scope, final IType contentType, final double[] doubles) {
		IList list = create(contentType, doubles == null ? 0 : doubles.length);
		if ( doubles != null ) {
			for ( double o : doubles ) {
				castAndAdd(scope, list, Double.valueOf(o));
			}
		}
		return list;
	}

	public static <T> IList<T> create(final IType contentType, final int size) {
		return new GamaList<T>(size, contentType);
	}

	public static <T> IList<T> create(final IType contentType) {
		return create(contentType, DEFAULT_SIZE);
	}

	public static IList create() {
		return create(Types.NO_TYPE);
	}

}
