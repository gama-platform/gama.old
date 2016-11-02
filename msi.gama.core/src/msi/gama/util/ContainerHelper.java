/*********************************************************************************************
 *
 * 'ContainerHelper.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.util;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Ordering;

import msi.gama.runtime.IScope;
import msi.gama.util.ContainerHelper.GamlGuavaHelper.GamlFunction;
import msi.gama.util.ContainerHelper.GamlGuavaHelper.GamlPredicate;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

/**
 * Class Guava.
 *
 * @author drogoul
 * @since 16 avr. 2014
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ContainerHelper {

	public final static Function<Object, Iterable> transformToIterables = input -> {
		if (input instanceof Iterable) {
			return (Iterable) input;
		}
		return Collections.singleton(input);
	};

	public final static Predicate<Object> NOT_NULL = each -> each != null;

	public static class InContainer implements Predicate, java.util.function.Predicate {

		final IContainer container;
		final IScope scope;

		public InContainer(final IScope s, final IContainer c) {
			scope = s;
			container = GAML.notNull(s, c);
		}

		/**
		 * Method apply()
		 * 
		 * @see com.google.common.base.Predicate#apply(java.lang.Object)
		 */
		@Override
		public boolean apply(final Object input) {
			return container.contains(scope, input);
		}

		@Override
		public boolean test(final Object t) {
			return apply(t);
		}

	}

	public static abstract class GamlGuavaHelper<T> {

		// private static final boolean ENABLE_STATS = false;
		private static final boolean USE_CACHE = false;

		IScope scope;
		IExpression filter;

		GamlGuavaHelper(final IScope scope, final IExpression filter) {
			this.scope = scope;
			this.filter = filter;
		}

		static LoadingCache<Function, Ordering> orderingCache = CacheBuilder.newBuilder().concurrencyLevel(1)
				.initialCapacity(10).maximumSize(10).build(new CacheLoader<Function, Ordering>() {

					@Override
					public Ordering load(final Function key) throws Exception {
						return Ordering.natural().onResultOf(key);
					}
				});

		static LoadingCache<IExpression, GamlFunction> functionCache = CacheBuilder.newBuilder().concurrencyLevel(1)
				.initialCapacity(10).maximumSize(10).build(new CacheLoader<IExpression, GamlFunction>() {

					@Override
					public GamlFunction load(final IExpression key) throws Exception {
						return new GamlFunction(key);
					}
				});

		static LoadingCache<IExpression, GamlPredicate> predicateCache = CacheBuilder.newBuilder().concurrencyLevel(1)
				.initialCapacity(10).maximumSize(10).build(new CacheLoader<IExpression, GamlPredicate>() {

					@Override
					public GamlPredicate load(final IExpression key) throws Exception {
						return new GamlPredicate(key);
					}
				});

		public static <T> GamlFunction getFunction(final IScope scope, final IExpression filter) {
			if (!USE_CACHE) {
				return new GamlFunction(scope, filter);
			}
			try {
				final GamlFunction<T> f = functionCache.get(filter);
				f.scope = scope;
				return f;
			} catch (final ExecutionException e) {
				return new GamlFunction(scope, filter);
			}
		}

		public static GamlPredicate getPredicate(final IScope scope, final IExpression filter) {
			if (!USE_CACHE) {
				return new GamlPredicate(scope, filter);
			}
			try {
				final GamlPredicate f = predicateCache.get(filter);
				f.scope = scope;
				return f;
			} catch (final ExecutionException e) {
				return new GamlPredicate(scope, filter);
			}
		}

		public static Ordering getOrdering(final Function f) {
			if (!USE_CACHE) {
				return Ordering.natural().onResultOf(f);
			}
			try {
				return orderingCache.get(f);
			} catch (final ExecutionException e) {
				return Ordering.natural().onResultOf(f);
			}
		}

		public static class GamlFunction<T> extends GamlGuavaHelper<T>
				implements Function<Object, T>, java.util.function.Function<Object, T> {

			GamlFunction(final IScope scope, final IExpression filter) {
				super(scope, filter);
			}

			GamlFunction(final IExpression filter) {
				super(null, filter);
			}

			@Override
			public T apply(final Object each) {
				scope.setEach(each);
				final T result = (T) filter.value(scope);
				return result;
			}
		}

		public static Function<Object, Iterable> iterableFunction(final IScope scope, final IExpression filter) {
			return input -> {
				final Object o = function(scope, filter).apply(input);
				if (o instanceof Iterable) {
					return (Iterable) o;
				}
				return ImmutableList.of(o);
			};

		}

		static class GamlPredicate extends GamlGuavaHelper implements Predicate, java.util.function.Predicate {

			GamlPredicate(final IScope scope, final IExpression filter) {
				super(scope, filter);
			}

			GamlPredicate(final IExpression filter) {
				super(null, filter);
			}

			@Override
			public boolean apply(final Object each) {
				scope.setEach(each);
				return Cast.asBool(scope, filter.value(scope));
			}

			@Override
			public boolean test(final Object each) {
				return apply(each);
			}
		}
	}

	public static class InterleavingIterator extends AbstractIterator {

		private final Queue<Iterator> queue = new ArrayDeque<Iterator>();

		public InterleavingIterator(final Object... objects) {
			for (final Object object : objects) {
				if (object instanceof Iterator) {
					queue.add((Iterator) object);
				} else if (object instanceof Iterable) {
					queue.add(((Iterable) object).iterator());
				} else {
					queue.add(Iterators.singletonIterator(object));
				}
			}
		}

		@Override
		protected Object computeNext() {
			while (!queue.isEmpty()) {
				final Iterator topIter = queue.poll();
				if (topIter.hasNext()) {
					final Object result = topIter.next();
					queue.offer(topIter);
					return result;
				}
			}
			return endOfData();
		}
	}

	/**
	 *
	 */
	public ContainerHelper() {
	}

	public static <T> GamlFunction function(final IScope scope, final IExpression filter) {
		return GamlGuavaHelper.<T> getFunction(scope, filter);
	}

	public static GamlPredicate withPredicate(final IScope scope, final IExpression filter) {
		return GamlGuavaHelper.getPredicate(scope, filter);
	}

	public static Ordering orderOn(final Function f) {
		return GamlGuavaHelper.getOrdering(f);
	}

	public static InContainer inContainer(final IScope scope, final IContainer l) {
		return new InContainer(scope, l);
	}

	// (Supplier<List<T>>) ArrayList::new, List::add, (left, right) -> {
	// left.addAll(right);
	// return left;
	// }, Collector.Characteristics.IDENTITY_FINISH

	private static Set<Collector.Characteristics> CH = ImmutableSet.<Collector.Characteristics> of(
			Collector.Characteristics.IDENTITY_FINISH);

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

}
