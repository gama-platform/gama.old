/**
 * Created by drogoul, 16 avr. 2014
 *
 */
package msi.gama.util;

import java.util.*;
import java.util.concurrent.ExecutionException;
import com.google.common.base.*;
import com.google.common.cache.*;
import com.google.common.collect.*;
import com.google.common.collect.AbstractIterator;
import msi.gama.runtime.IScope;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

/**
 * Class Guava.
 *
 * @author drogoul
 * @since 16 avr. 2014
 *
 */
public class Guava {

	public final static Function<Object, Iterable> transformToIterables = new Function<Object, Iterable>() {

		@Override
		public Iterable apply(final Object input) {
			if ( input instanceof Iterable ) { return (Iterable) input; }
			return ImmutableList.of(input);
		}
	};

	public static class InContainer implements Predicate {

		final IContainer container;
		final IScope scope;

		public InContainer(final IScope s, final IContainer c) {
			scope = s;
			container = GAML.nullCheck(s, c);
		}

		/**
		 * Method apply()
		 * @see com.google.common.base.Predicate#apply(java.lang.Object)
		 */
		@Override
		public boolean apply(final Object input) {
			return container.contains(scope, input);
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
			if ( !USE_CACHE ) { return new GamlFunction(scope, filter); }
			try {
				final GamlFunction<T> f = functionCache.get(filter);
				f.scope = scope;
				return f;
			} catch (final ExecutionException e) {
				return new GamlFunction(scope, filter);
			}
		}

		public static GamlPredicate getPredicate(final IScope scope, final IExpression filter) {
			if ( !USE_CACHE ) { return new GamlPredicate(scope, filter); }
			try {
				final GamlPredicate f = predicateCache.get(filter);
				f.scope = scope;
				return f;
			} catch (final ExecutionException e) {
				return new GamlPredicate(scope, filter);
			}
		}

		public static Ordering getOrdering(final Function f) {
			if ( !USE_CACHE ) { return Ordering.natural().onResultOf(f); }
			try {
				return orderingCache.get(f);
			} catch (final ExecutionException e) {
				return Ordering.natural().onResultOf(f);
			}
		}

		public static class GamlFunction<T> extends GamlGuavaHelper<T> implements Function<Object, T> {

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
			return new Function<Object, Iterable>() {

				@Override
				public Iterable apply(final Object input) {
					final Object o = function(scope, filter).apply(input);
					if ( o instanceof Iterable ) { return (Iterable) o; }
					return ImmutableList.of(o);
				}

			};

		}

		static class GamlPredicate extends GamlGuavaHelper implements Predicate {

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
		}
	}

	public static class InterleavingIterator extends AbstractIterator {

		private final Queue<Iterator> queue = new ArrayDeque<Iterator>();

		public InterleavingIterator(final Object ... objects) {
			for ( final Object object : objects ) {
				if ( object instanceof Iterator ) {
					queue.add((Iterator) object);
				} else if ( object instanceof Iterable ) {
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
				if ( topIter.hasNext() ) {
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
	public Guava() {}

	public static <T> Function function(final IScope scope, final IExpression filter) {
		return GamlGuavaHelper.<T> getFunction(scope, filter);
	}

	public static Predicate withPredicate(final IScope scope, final IExpression filter) {
		return GamlGuavaHelper.getPredicate(scope, filter);
	}

	public static Ordering orderOn(final Function f) {
		return GamlGuavaHelper.getOrdering(f);
	}

	public static Predicate inContainer(final IScope scope, final IContainer l) {
		return new InContainer(scope, l);
	}

}
