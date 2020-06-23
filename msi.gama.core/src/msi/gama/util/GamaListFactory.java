/*******************************************************************************************************
 *
 * msi.gama.util.GamaListFactory.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang.ArrayUtils;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import msi.gama.runtime.IScope;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Class GamaListFactory. The factory for creating lists from various other objects. All the methods that accept the
 * scope as a parameter will observe the contract of GAML containers, which is that the objects contained in the list
 * will be casted to the content type of the list. To avoid unecessary castings, some methods (without the scope
 * parameter) will simply copy the objects.
 *
 * @author drogoul
 * @since 30 janv. 2015
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaListFactory {

	private static final int DEFAULT_SIZE = 4;
	public static final IList EMPTY_LIST = wrap(Types.NO_TYPE, Collections.EMPTY_LIST);
	static Set<Collector.Characteristics> CH =
			ImmutableSet.<Collector.Characteristics> of(Collector.Characteristics.IDENTITY_FINISH);

	public static <T> Collector<T, IList<T>, IList<T>> toGamaList() {
		return new Collector<T, IList<T>, IList<T>>() {

			@Override
			public Supplier<IList<T>> supplier() {
				return GamaListFactory::create;
			}

			@Override
			public BiConsumer<IList<T>, T> accumulator() {
				return (left, right) -> left.add(right);
			}

			@Override
			public BinaryOperator<IList<T>> combiner() {
				return (left, right) -> {
					left.addAll(right);
					return left;
				};
			}

			@Override
			public java.util.function.Function<IList<T>, IList<T>> finisher() {
				return (left) -> left;
			}

			@Override
			public Set<java.util.stream.Collector.Characteristics> characteristics() {
				return CH;
			}
		};
	}

	public static Collector<Object, IList<Object>, IList<Object>> TO_GAMA_LIST = toGamaList();

	public static class GamaListSupplier implements Supplier<IList> {

		final IType t;

		public GamaListSupplier(final IType t) {
			this.t = t;
		}

		@Override
		public IList get() {
			return create(t);
		}

	}

	public static <T> IList<T> create(final IType t, final Stream<T> stream) {
		return (IList<T>) stream.collect(TO_GAMA_LIST);
	}

	/**
	 * Create a GamaList from an array of objects, but does not attempt casting its values.
	 *
	 * @param contentType
	 * @param collection
	 * @warning ***WARNING*** This operation can end up putting values of the wrong type into the list
	 * @return
	 */
	public static <T> IList<T> createWithoutCasting(final IType contentType, final T... objects) {
		final IList<T> list = create(contentType, objects.length);
		list.addAll(Arrays.asList(objects));
		return list;
	}

	/**
	 * Create a GamaList from an array of ints, but does not attempt casting its values.
	 *
	 * @param contentType
	 * @param collection
	 * @warning ***WARNING*** This operation can end up putting values of the wrong type into the list
	 * @return
	 */
	public static IList<Integer> createWithoutCasting(final IType contentType, final int[] objects) {
		final IList<Integer> list = create(contentType, objects.length);
		list.addAll(Arrays.asList(ArrayUtils.toObject(objects)));
		return list;
	}

	/**
	 * Create a GamaList from an array of floats, but does not attempt casting its values.
	 *
	 * @param contentType
	 * @param collection
	 * @warning ***WARNING*** This operation can end up putting values of the wrong type into the list
	 * @return
	 */
	public static IList<Double> createWithoutCasting(final IType contentType, final double[] objects) {
		final IList<Double> list = create(contentType, objects.length);
		list.addAll(Arrays.asList(ArrayUtils.toObject(objects)));
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
		final IList<T> list = create(contentType);
		Iterables.addAll(list, objects);
		return list;
	}

	private static void castAndAdd(final IScope scope, final IList list, final Object o) {
		list.addValue(scope, o);
	}

	public static IList create(final IScope scope, final IType contentType, final IContainer container) {
		if (container == null) { return create(contentType); }
		if (GamaType.requiresCasting(contentType, container.getGamlType().getContentType())) {
			return create(scope, contentType, container.iterable(scope));
		} else {
			return createWithoutCasting(contentType, container.iterable(scope));
		}
	}

	public static <T> IList<T> create(final IScope scope, final IType contentType, final IList<T> container) {
		if (container == null) { return create(contentType); }
		if (GamaType.requiresCasting(contentType, container.getGamlType().getContentType())) {
			return create(scope, contentType, (Collection) container);
		} else {
			return createWithoutCasting(contentType, container);
		}
	}

	public static <T> IList<T> create(final IScope scope, final IType contentType, final Iterable<T> iterable) {
		final IList<T> list = create(contentType);
		for (final Object o : iterable) {
			castAndAdd(scope, list, o);
		}
		return list;
	}

	public static <T> IList<T> create(final IScope scope, final IType contentType, final Iterator<T> iterator) {
		final IList<T> list = create(contentType);
		if (iterator != null) {
			while (iterator.hasNext()) {
				castAndAdd(scope, list, iterator.next());
			}
		}
		return list;
	}

	public static <T> IList<T> create(final IScope scope, final IType contentType, final Enumeration<T> iterator) {
		final IList<T> list = create(contentType);
		if (iterator != null) {
			while (iterator.hasMoreElements()) {
				castAndAdd(scope, list, iterator.nextElement());
			}
		}
		return list;
	}

	@SafeVarargs
	public static <T> IList<T> create(final IScope scope, final IType contentType, final T... objects) {
		final IList<T> list = create(contentType, objects == null ? 0 : objects.length);
		if (objects != null) {
			for (final Object o : objects) {
				castAndAdd(scope, list, o);
			}
		}
		return list;
	}

	public static IList create(final IScope scope, final IType contentType, final byte[] ints) {
		final IList list = create(contentType, ints == null ? 0 : ints.length);
		if (ints != null) {
			for (final int o : ints) {
				castAndAdd(scope, list, Integer.valueOf(o));
			}
		}
		return list;
	}

	public static IList create(final IScope scope, final IType contentType, final int[] ints) {
		final IList list = create(contentType, ints == null ? 0 : ints.length);
		if (ints != null) {
			for (final int o : ints) {
				castAndAdd(scope, list, Integer.valueOf(o));
			}
		}
		return list;
	}

	public static IList create(final IScope scope, final IType contentType, final long[] ints) {
		final IList list = create(contentType, ints == null ? 0 : ints.length);
		if (ints != null) {
			for (final long o : ints) {
				castAndAdd(scope, list, Long.valueOf(o).intValue());
			}
		}
		return list;
	}

	public static IList create(final IScope scope, final IType contentType, final float[] doubles) {
		final IList list = create(contentType, doubles == null ? 0 : doubles.length);
		if (doubles != null) {
			for (final float o : doubles) {
				castAndAdd(scope, list, Double.valueOf(o));
			}
		}
		return list;
	}

	public static IList create(final IScope scope, final IExpression fillExpr, final Integer size) {
		if (fillExpr == null) { return create(Types.NO_TYPE, size); }
		final Object[] contents = new Object[size];
		final IType contentType = fillExpr.getGamlType();
		// 10/01/14. Cannot use Arrays.fill() everywhere: see Issue 778.
		if (fillExpr.isConst()) {
			final Object o = fillExpr.value(scope);
			GamaExecutorService.executeThreaded(() -> IntStream.range(0, contents.length).parallel().forEach(i -> {
				contents[i] = o;
			}));
		} else {
			GamaExecutorService.executeThreaded(
					() -> IntStream.range(0, contents.length)./* see #2974. parallel(). */forEach(i -> {
						contents[i] = fillExpr.value(scope);
					}));
		}
		return create(scope, contentType, contents);
	}

	public static IList create(final IScope scope, final IType contentType, final double[] doubles) {
		final IList list = create(contentType, doubles == null ? 0 : doubles.length);
		if (doubles != null) {
			for (final double o : doubles) {
				castAndAdd(scope, list, Double.valueOf(o));
			}
		}
		return list;
	}

	public static <T> IList<T> create(final IType contentType, final int size) {
		return new GamaList<>(size, contentType);
	}

	public static <T> IList<T> create(final IType contentType) {
		return create(contentType, DEFAULT_SIZE);
	}

	/**
	 * Create a IList with no type and no elements
	 *
	 * @param clazz,
	 *            the class from which the contents type
	 * @return a new IList
	 */
	public static <T> IList<T> create(final Class<T> clazz) {
		return create(Types.get(clazz));
	}

	/**
	 * Create a IList with no type and no elements
	 *
	 * @return a new IList
	 */
	public static <T> IList<T> create() {
		return create(Types.NO_TYPE);
	}

	/**
	 * Wraps the parameter into an IList. Every change in the wrapped list is reflected immediately and every change to
	 * the IList is reflected in the wrapped list. No copy is made, , only a thin layer is created to wrap the parameter
	 *
	 * @param contentType
	 * @param wrapped
	 * @return
	 */
	public static <E> IList<E> wrap(final IType contentType, final List<E> wrapped) {
		// return createWithoutCasting(contentType, wrapped);
		return new GamaListWrapper(wrapped, contentType);
	}

	/**
	 * Wraps the array into an IList. Every change in the wrapped array is reflected immediately and every change to the
	 * IList is reflected in the array, exluding add and remove (as well as addAll and removeAll) operations, which
	 * yield a runtime exception. No copy of the array is made, only a thin layer is created to wrap the array
	 *
	 * @param contentType
	 * @param wrapped
	 * @return
	 */
	public static <E> IList<E> wrap(final IType contentType, final E... wrapped) {
		// return createWithoutCasting(contentType, wrapped);
		return new GamaListArrayWrapper(wrapped, contentType);
	}

	/**
	 * Wraps the parameter Collection into an IList. Every change in the wrapped Collection is reflected immediately and
	 * every change to the IList is reflected in the wrapped Collection. No copy is made, only a thin layer is created
	 * to wrap the parameter. Some operations (esp. those using indices) are not really meaningful for collections; they
	 * are emulated in the best possible way by this wrapper
	 *
	 * @param contentType
	 *            s
	 * @param wrapped
	 * @return
	 */
	public static <E> IList<E> wrap(final IType contentType, final Collection<E> wrapped) {
		// return createWithoutCasting(contentType, wrapped);
		return new GamaListCollectionWrapper(wrapped, contentType);
	}

	public static boolean equals(final IList one, final IList two) {
		final Iterator<Object> it1 = one.iterator();
		final Iterator<Object> it2 = two.iterator();
		while (it1.hasNext() && it2.hasNext()) {
			if (!Objects.equals(it1.next(), it2.next())) { return false; }
		}
		return !(it1.hasNext() || it2.hasNext());
	}

}
