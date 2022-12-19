/*******************************************************************************************************
 *
 * PoolUtils.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.common.util;

import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;

import com.google.common.collect.Queues;

import msi.gama.common.interfaces.IDisposable;
import msi.gama.common.preferences.GamaPreferences;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class PoolUtils.
 */
public class PoolUtils {

	/** The pools. */
	static Set<ObjectPool> POOLS = new LinkedHashSet<>();
	
	/** The pool. */
	static public boolean POOL = GamaPreferences.External.USE_POOLING.getValue();
	static {
		DEBUG.OFF();
		GamaPreferences.External.USE_POOLING.onChange(v -> {
			POOLS.forEach(ObjectPool::dispose);
			POOL = v;
		});
	}

	/**
	 * Write stats.
	 */
	public static void WriteStats() {
		if (!DEBUG.IS_ON()) return;
		DEBUG.SECTION("Pool statistics");
		POOLS.forEach(p -> {
			long percentage = p.accessed == 0 ? 100 : 100 - (long) (p.created * 100d / p.accessed);
			DEBUG.OUT(p.name, 30, "instances created " + p.created + " / instances asked " + p.accessed + " = "
					+ percentage + "% of coverage");
		});
	}

	/**
	 * A factory for creating Object objects.
	 *
	 * @param <T> the generic type
	 */
	public interface ObjectFactory<T> { /**
  * Creates a new Object object.
  *
  * @return the t
  */
 T createNew(); }

	/**
	 * The Interface ObjectCopy.
	 *
	 * @param <T> the generic type
	 */
	public interface ObjectCopy<T> {

		/**
		 * Creates the new.
		 *
		 * @param copyFrom the copy from
		 * @param copyTo the copy to
		 */
		void createNew(T copyFrom, T copyTo);
	}

	/**
	 * The Interface ObjectCleaner.
	 *
	 * @param <T> the generic type
	 */
	public interface ObjectCleaner<T> { /**
  * Clean.
  *
  * @param object the object
  */
 void clean(T object); }

	/**
	 * The Class ObjectPool.
	 *
	 * @param <T> the generic type
	 */
	public static class ObjectPool<T> implements IDisposable {

		/** The name. */
		private String name;
		
		/** The created. */
		private long accessed, released, created;
		
		/** The factory. */
		private final ObjectFactory<T> factory;
		
		/** The copy. */
		private final ObjectCopy<T> copy;
		
		/** The cleaner. */
		private final ObjectCleaner<T> cleaner;
		
		/** The objects. */
		private final Queue<T> objects;
		
		/** The active. */
		public boolean active;

		/**
		 * Instantiates a new object pool.
		 *
		 * @param factory the factory
		 * @param copy the copy
		 * @param cleaner the cleaner
		 */
		private ObjectPool(final ObjectFactory<T> factory, final ObjectCopy<T> copy, final ObjectCleaner<T> cleaner) {
			this.factory = factory;
			this.copy = copy;
			this.cleaner = cleaner;
			objects = Queues.synchronizedDeque(Queues.newArrayDeque());
		}

		/**
		 * Gets the.
		 *
		 * @return the t
		 */
		public T get() {
			if (!POOL || !active) return factory.createNew();
			accessed++;

			T result = objects.poll();
			if (result == null) {
				created++;
				result = factory.createNew();
			}
			return result;
		}

		/**
		 * Gets the.
		 *
		 * @param from the from
		 * @return the t
		 */
		public T get(final T from) {
			T result = get();
			if (copy != null) { copy.createNew(from, result); }
			return result;
		}

		/**
		 * Release.
		 *
		 * @param tt the tt
		 */
		public void release(@SuppressWarnings ("unchecked") final T... tt) {
			if (tt == null) return;
			for (T t : tt) {
				if (cleaner != null) { cleaner.clean(t); }
				if (POOL && active) {
					released++;
					objects.offer(t);
				}
			}

		}

		@Override
		public void dispose() {
			objects.clear();
		}
	}

	/**
	 * Creates a new object pool
	 *
	 * @param <T>
	 *            the type of objects created and maintained in the poool
	 * @param name
	 *            the name of the pool
	 * @param active
	 *            whether or not it is active
	 * @param factory
	 *            the factory to create new objects
	 * @param copy
	 *            the factory to create new objects from existing ones
	 * @param cleaner
	 *            the code to execute to return the object to its pristine state
	 * @return
	 */

	public static <T> ObjectPool<T> create(final String name, final boolean active, final ObjectFactory<T> factory,
			final ObjectCopy<T> copy, final ObjectCleaner<T> cleaner) {
		DEBUG.OUT("Adding object pool: " + name);
		final ObjectPool<T> result = new ObjectPool<>(factory, copy, cleaner);
		result.active = active;
		result.name = name;
		// if (DEBUG.IS_ON()) {
		POOLS.add(result);
		// }
		return result;
	}

}
