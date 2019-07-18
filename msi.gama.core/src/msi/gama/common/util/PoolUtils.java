package msi.gama.common.util;

import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;

import com.google.common.collect.Queues;

import msi.gama.common.interfaces.IDisposable;
import ummisco.gama.dev.utils.DEBUG;

public class PoolUtils {

	static Set<ObjectPool> POOLS = new LinkedHashSet<>();
	static boolean POOL = false;
	static {
		DEBUG.OFF();
		// GamaPreferences.External.USE_POOLING.onChange(v -> {
		// POOLS.forEach((p) -> p.dispose());
		// POOL = v;
		// });
	}

	public static void WriteStats() {
		if (!DEBUG.IS_ON()) { return; }
		DEBUG.SECTION("Pool statistics");
		POOLS.forEach((p) -> {
			DEBUG.OUT(p.name, 30, "accessed " + p.accessed + " times | created " + p.created + " times | released "
					+ p.released + " times | objects size: " + p.objects.size());
		});
	}

	public interface ObjectFactory<T> {
		T createNew();
	}

	public interface ObjectCleaner<T> {
		void clean(T object);
	}

	public static class ObjectPool<T> implements IDisposable {

		private String name;
		private long accessed, released, created;
		private final ObjectFactory<T> factory;
		private final ObjectCleaner<T> cleaner;
		private final Queue<T> objects;
		public boolean active;

		private ObjectPool(final ObjectFactory<T> factory, final ObjectCleaner<T> cleaner) {
			this.factory = factory;
			this.cleaner = cleaner;
			objects = Queues.synchronizedDeque(Queues.newArrayDeque());
		}

		public T get() {
			if (!POOL || !active) { return factory.createNew(); }
			accessed++;

			T result = objects.poll();
			if (result == null) {
				created++;
				result = factory.createNew();
			}
			return result;
		}

		public void release(final T t) {
			if (t == null) { return; }
			if (cleaner != null) {
				cleaner.clean(t);
			}
			if (POOL && active) {
				released++;
				objects.offer(t);
			}

		}

		@Override
		public void dispose() {
			objects.clear();
		}
	}

	public static <T> ObjectPool<T> create(final String name, final boolean active, final ObjectFactory<T> factory,
			final ObjectCleaner<T> cleaner) {
		DEBUG.OUT("Adding object pool: " + name);
		final ObjectPool<T> result = new ObjectPool<>(factory, cleaner);
		result.active = active;
		result.name = name;
		if (DEBUG.IS_ON()) {
			POOLS.add(result);
		}
		return result;
	}

}
