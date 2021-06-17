package msi.gama.common.util;

import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;

import com.google.common.collect.Queues;

import msi.gama.common.interfaces.IDisposable;
import msi.gama.common.preferences.GamaPreferences;
import ummisco.gama.dev.utils.DEBUG;

public class PoolUtils {

	static Set<ObjectPool> POOLS = new LinkedHashSet<>();
	static boolean POOL = GamaPreferences.External.USE_POOLING.getValue();
	static {
		DEBUG.OFF();
		GamaPreferences.External.USE_POOLING.onChange(v -> {
			POOLS.forEach((p) -> p.dispose());
			POOL = v;
		});
	}

	public static void WriteStats() {
		if (!DEBUG.IS_ON()) return;
		DEBUG.SECTION("Pool statistics");
		POOLS.forEach((p) -> {
			long percentage = p.accessed == 0 ? 100 : 100 - (long) (p.created * 100d / p.accessed);
			DEBUG.OUT(p.name, 30, "instances created " + p.created + " / instances asked " + p.accessed + " = "
					+ percentage + "% of coverage");
		});
	}

	public interface ObjectFactory<T> {
		T createNew();
	}

	public interface ObjectCopy<T> {

		void createNew(T copyFrom, T copyTo);
	}

	public interface ObjectCleaner<T> {
		void clean(T object);
	}

	public static class ObjectPool<T> implements IDisposable {

		private String name;
		private long accessed, released, created;
		private final ObjectFactory<T> factory;
		private final ObjectCopy<T> copy;
		private final ObjectCleaner<T> cleaner;
		private final Queue<T> objects;
		public boolean active;

		private ObjectPool(final ObjectFactory<T> factory, final ObjectCopy<T> copy, final ObjectCleaner<T> cleaner) {
			this.factory = factory;
			this.copy = copy;
			this.cleaner = cleaner;
			objects = Queues.synchronizedDeque(Queues.newArrayDeque());
		}

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

		public T get(final T from) {
			T result = get();
			if (copy != null) { copy.createNew(from, result); }
			return result;
		}

		public void release(final T... tt) {
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
