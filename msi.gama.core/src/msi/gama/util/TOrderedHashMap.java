/*********************************************************************************************
 * 
 * 
 * 'TOrderedHashMap.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.util;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.map.hash.THashMap;
import gnu.trove.procedure.TObjectObjectProcedure;
import gnu.trove.procedure.TObjectProcedure;
import msi.gama.runtime.IScope;
import one.util.streamex.StreamEx;

public class TOrderedHashMap<K, V> extends THashMap<K, V> implements Cloneable {

	protected static final int EMPTY = -1;
	protected static final int DEFAULT_SIZE = 10;
	protected static final float DEFAULT_LOAD = 0.75f;

	protected transient volatile int[] _indicesByInsertOrder = new int[capacity()];
	{
		Arrays.fill(_indicesByInsertOrder, EMPTY);
	}
	protected transient volatile int _lastInsertOrderIndex = -1;

	public TOrderedHashMap() {
		super(DEFAULT_SIZE, DEFAULT_LOAD);
	}

	/**
	 * Creates a new <code>THashMap</code> instance with a prime capacity equal
	 * to or greater than <tt>initialCapacity</tt> and with the default load
	 * factor.
	 * 
	 * @param initialCapacity
	 *            an <code>int</code> value
	 */
	public TOrderedHashMap(final int initialCapacity) {
		super(initialCapacity, DEFAULT_LOAD);
	}

	/**
	 * Creates a new <code>THashMap</code> instance with a prime capacity equal
	 * to or greater than <tt>initialCapacity</tt> and with the specified load
	 * factor.
	 * 
	 * @param initialCapacity
	 *            an <code>int</code> value
	 * @param loadFactor
	 *            a <code>float</code> value
	 */
	public TOrderedHashMap(final int initialCapacity, final float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * Creates a new <code>THashMap</code> instance which contains the key/value
	 * pairs in <tt>map</tt>.
	 * 
	 * @param map
	 *            a <code>Map</code> value
	 */
	public TOrderedHashMap(final Map<? extends K, ? extends V> map) {
		this(map.size());
		putAll(map);
	}

	/**
	 * @return a shallow clone of this collection
	 */
	@SuppressWarnings("unchecked")
	@Override
	public TOrderedHashMap<K, V> clone() {
		TOrderedHashMap<K, V> m = null;
		try {
			m = (TOrderedHashMap<K, V>) super.clone();
			m._indicesByInsertOrder = this._indicesByInsertOrder.clone();
			m._lastInsertOrderIndex = this._lastInsertOrderIndex;
		} catch (final CloneNotSupportedException e) {
		}

		return m;
	}

	/**
	 * initializes the Object set of this hash table.
	 * 
	 * @param initialCapacity
	 *            an <code>int</code> value
	 * @return an <code>int</code> value
	 */
	@Override
	public int setUp(final int initialCapacity) {
		final int capacity = super.setUp(initialCapacity);
		_indicesByInsertOrder = new int[capacity];
		Arrays.fill(_indicesByInsertOrder, EMPTY);
		_lastInsertOrderIndex = -1;
		return capacity;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void rehash(final int newCapacity) {
		final int oldCapacity = _set.length;
		final Object oldKeys[] = _set;
		final V oldVals[] = _values;
		final int[] oldInsertionOrder = _indicesByInsertOrder;

		_set = new Object[newCapacity];
		Arrays.fill(_set, FREE);
		_values = (V[]) new Object[newCapacity];
		_indicesByInsertOrder = new int[newCapacity];
		Arrays.fill(_indicesByInsertOrder, EMPTY);
		_lastInsertOrderIndex = -1;

		for (int i = 0; i < oldCapacity; i++) {
			final int insertionOrderIndex = oldInsertionOrder[i];
			if (insertionOrderIndex == EMPTY) {
				continue;
			}
			if (oldKeys[insertionOrderIndex] != FREE && oldKeys[insertionOrderIndex] != REMOVED) {
				final Object o = oldKeys[insertionOrderIndex];
				final int index = insertionIndex((K) o);
				if (index < 0) {
					throwObjectContractViolation(_set[-index - 1], o);
				}
				_set[index] = o;
				_values[index] = oldVals[insertionOrderIndex];
				appendToInsertionOrder(index, newCapacity);
			}
		}
	}

	@Override
	public void removeAt(final int index) {
		// find and empty this index in insertionOrder
		removeFromInsertionOrder(index);
		super.removeAt(index); // clear key, state, value; adjust size
	}

	@Override
	public boolean forEachKey(final TObjectProcedure<? super K> procedure) {
		return forEach(procedure);
	}

	@Override
	public boolean forEachValue(final TObjectProcedure<? super V> procedure) {
		final V[] values = _values;
		final Object[] set = _set;
		final int[] inserts = _indicesByInsertOrder;
		for (int i = 0; i <= _lastInsertOrderIndex; i++) {
			final int index = inserts[i];
			if (index == EMPTY) {
				continue;
			}
			if (set[index] != FREE && set[index] != REMOVED && !procedure.execute(values[index])) {
				return false;
			}
		}
		return true;
	}

	public void forEachValue(final Consumer<? super V> procedure) {
		final V[] values = _values;
		final Object[] set = _set;
		final int[] inserts = _indicesByInsertOrder;
		for (int i = 0; i <= _lastInsertOrderIndex; i++) {
			final int index = inserts[i];
			if (index == EMPTY) {
				continue;
			}
			if (set[index] != FREE && set[index] != REMOVED) {
				procedure.accept(values[index]);
			}
		}
	}

	public StreamEx<V> stream(final IScope scope) {
		final StreamEx.Builder<V> b = Stream.builder();
		forEachValue((final V v) -> b.accept(v));
		return StreamEx.of(b.build().parallel());
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean forEachEntry(final TObjectObjectProcedure<? super K, ? super V> procedure) {
		final Object[] keys = _set;
		final V[] values = _values;
		final int[] inserts = _indicesByInsertOrder;
		for (int i = 0; i <= _lastInsertOrderIndex; i++) {
			final int index = inserts[i];
			if (index == EMPTY) {
				continue;
			}
			if (keys[index] != FREE && keys[index] != REMOVED && !procedure.execute((K) keys[index], values[index])) {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean forEach(final TObjectProcedure<? super K> procedure) {
		final Object[] set = _set;
		final int[] inserts = _indicesByInsertOrder;
		for (int i = 0; i <= _lastInsertOrderIndex; i++) {
			final int index = inserts[i];
			if (index == EMPTY) {
				continue;
			}
			if (set[index] != FREE && set[index] != REMOVED && !procedure.execute((K) set[index])) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void clear() {
		if (size() == 0) {
			return; // optimization
		}
		super.clear();
		Arrays.fill(_indicesByInsertOrder, EMPTY);
		// AD: check this
		_lastInsertOrderIndex = -1;
	}

	/**
	 * Inserts a key/value pair into the map.
	 * 
	 * @param key
	 *            an <code>Object</code> value
	 * @param value
	 *            an <code>Object</code> value
	 * @return the previous value associated with <tt>key</tt>, or {@code null}
	 *         if none was found.
	 */
	@Override
	public V put(final K key, final V value) {
		final int index = insertKey(key);
		return doPut(value, index);
	}

	/**
	 * Inserts a key/value pair into the map if the specified key is not already
	 * associated with a value.
	 * 
	 * @param key
	 *            an <code>Object</code> value
	 * @param value
	 *            an <code>Object</code> value
	 * @return the previous value associated with <tt>key</tt>, or {@code null}
	 *         if none was found.
	 */
	@Override
	public V putIfAbsent(final K key, final V value) {
		final int index = insertKey(key);
		if (index < 0) {
			return _values[-index - 1];
		}
		return doPut(value, index);
	}

	/**
	 * Returns a Set view on the entries of the map.
	 * 
	 * @return a <code>Set</code> value
	 */
	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		// TODO
		return new EntryView();
	}

	/**
	 * Returns a view on the values of the map.
	 * 
	 * @return a <code>Collection</code> value
	 */
	@Override
	public Collection<V> values() {
		// TODO
		return new ValueView();
	}

	/**
	 * returns a Set view on the keys of the map.
	 * 
	 * @return a <code>Set</code> value
	 */
	@Override
	public Set<K> keySet() {
		// TODO
		return new KeyView();
	}

	private V doPut(final V value, int index) {
		V previous = null;
		// final Object oldKey;
		boolean isNewMapping = true;
		if (index < 0) {
			index = -index - 1;
			previous = _values[index];
			isNewMapping = false;
		}
		// oldKey = _set[index];
		// _set[index] = key;
		_values[index] = value;

		// must execute before postInsertHook
		if (isNewMapping) {
			appendToInsertionOrder(index, capacity());
			postInsertHook(consumeFreeSlot);
		}

		return previous;
	}

	private void removeFromInsertionOrder(final int index) {
		for (int i = 0; i <= _lastInsertOrderIndex; i++) {
			if (_indicesByInsertOrder[i] == index) {
				_indicesByInsertOrder[i] = EMPTY;
				break;
			}
		}
	}

	/*
	 * To prevent running out of slots in _indicesByInsertOrder, we must compact
	 * the array's elements when we near capacity.
	 */
	private void appendToInsertionOrder(final int index, final int effectiveCapacity) {
		if (_lastInsertOrderIndex == effectiveCapacity - 1) {
			compactInsertionOrderIndices();
		}
		_lastInsertOrderIndex++;
		_indicesByInsertOrder[_lastInsertOrderIndex] = index;
	}

	private void compactInsertionOrderIndices() {
		final int[] oldInserts = _indicesByInsertOrder;
		_indicesByInsertOrder = new int[capacity()];
		Arrays.fill(_indicesByInsertOrder, EMPTY);
		_lastInsertOrderIndex = -1;
		for (final int oldIndex : oldInserts) {
			if (oldIndex == EMPTY) {
				continue;
			}
			_lastInsertOrderIndex++;
			_indicesByInsertOrder[_lastInsertOrderIndex] = oldIndex;
		}
	}

	protected class EntryView extends MapBackedView<Map.Entry<K, V>> {

		private final class EntryIterator extends THashIterator<Map.Entry<K, V>> {

			EntryIterator() {
				super();
			}

			@Override
			@SuppressWarnings("unchecked")
			public Entry objectAtIndex(final int index) {
				return new Entry((K) _set[index], _values[index], index);
			}
		}

		@Override
		public Iterator<Map.Entry<K, V>> iterator() {
			return new EntryIterator();
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public boolean removeElement(final Map.Entry<K, V> entry) {
			Object val;
			int index;

			final K key = keyForEntry(entry);
			index = index(key);
			if (index >= 0) {
				val = valueForEntry(entry);
				if (val == _values[index] || null != val && val.equals(_values[index])) {
					removeAt(index); // clear key,state; adjust size
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean containsElement(final Map.Entry<K, V> entry) {
			final Object val = get(keyForEntry(entry));
			final Object entryValue = entry.getValue();
			return entryValue == val || null != val && val.equals(entryValue);
		}

		protected V valueForEntry(final Map.Entry<K, V> entry) {
			return entry.getValue();
		}

		protected K keyForEntry(final Map.Entry<K, V> entry) {
			return entry.getKey();
		}
	}

	private abstract class MapBackedView<E> extends AbstractSet<E> {

		@Override
		public abstract Iterator<E> iterator();

		public abstract boolean removeElement(E key);

		public abstract boolean containsElement(E key);

		@Override
		@SuppressWarnings("unchecked")
		public boolean contains(final Object key) {
			return containsElement((E) key);
		}

		@Override
		@SuppressWarnings("unchecked")
		public boolean remove(final Object o) {
			return removeElement((E) o);
		}

		@Override
		public boolean containsAll(final Collection<?> collection) {
			for (final Iterator<?> i = collection.iterator(); i.hasNext();) {
				if (!contains(i.next())) {
					return false;
				}
			}
			return true;
		}

		@Override
		public void clear() {
			TOrderedHashMap.this.clear();
		}

		@Override
		public boolean add(final E obj) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int size() {
			return TOrderedHashMap.this.size();
		}

		@Override
		public Object[] toArray() {
			final Object[] result = new Object[size()];
			final Iterator<?> e = iterator();
			for (int i = 0; e.hasNext(); i++) {
				result[i] = e.next();
			}
			return result;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> T[] toArray(T[] a) {
			final int size = size();
			if (a.length < size) {
				a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
			}

			final Iterator<E> it = iterator();
			final Object[] result = a;
			for (int i = 0; i < size; i++) {
				result[i] = it.next();
			}

			if (a.length > size) {
				a[size] = null;
			}

			return a;
		}

		@Override
		public boolean isEmpty() {
			return TOrderedHashMap.this.isEmpty();
		}

		@Override
		public boolean addAll(final Collection<? extends E> collection) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(final Collection<?> collection) {
			boolean changed = false;
			final Iterator<?> i = iterator();
			while (i.hasNext()) {
				if (!collection.contains(i.next())) {
					i.remove();
					changed = true;
				}
			}
			return changed;
		}
	}

	/**
	 * a view onto the keys of the map.
	 */
	protected class KeyView extends MapBackedView<K> {

		@Override
		public Iterator<K> iterator() {
			return new THashIterator<K>() {

				@SuppressWarnings("unchecked")
				@Override
				protected K objectAtIndex(final int idx) {
					return (K) TOrderedHashMap.this._set[idx];
				}
			};
		}

		@Override
		public boolean removeElement(final K key) {
			return null != TOrderedHashMap.this.remove(key);
		}

		@Override
		public boolean containsElement(final K key) {
			return TOrderedHashMap.this.contains(key);
		}
	}

	final class Entry implements Map.Entry<K, V> {

		private K key;
		private V val;
		private final int index;

		Entry(final K key, final V value, final int index) {
			this.key = key;
			this.val = value;
			this.index = index;
		}

		void setKey(final K aKey) {
			this.key = aKey;
		}

		void setValue0(final V aValue) {
			this.val = aValue;
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return val;
		}

		@Override
		public V setValue(final V o) {
			if (_values[index] != val) {
				throw new ConcurrentModificationException();
			}
			_values[index] = o;
			final V o2 = val; // need to return previous value
			val = o; // update this entry's value, in case
			// setValue is called again
			return o2;
		}

		@Override
		public boolean equals(final Object o) {
			if (o instanceof Map.Entry) {
				final Map.Entry<?, ?> e1 = this;
				final Map.Entry<?, ?> e2 = (Map.Entry<?, ?>) o;
				return (e1.getKey() == null ? e2.getKey() == null : e1.getKey().equals(e2.getKey()))
						&& (e1.getValue() == null ? e2.getValue() == null : e1.getValue().equals(e2.getValue()));
			}
			return false;
		}

		@Override
		public int hashCode() {
			return (getKey() == null ? 0 : getKey().hashCode()) ^ (getValue() == null ? 0 : getValue().hashCode());
		}
	}

	protected class ValueView extends MapBackedView<V> {

		@Override
		public Iterator<V> iterator() {
			return new THashIterator<V>() {

				@SuppressWarnings("synthetic-access")
				@Override
				protected V objectAtIndex(final int index) {
					return _values[index];
				}
			};
		}

		@Override
		public boolean containsElement(final V value) {
			return containsValue(value);
		}

		@Override
		public boolean removeElement(final V value) {
			final Object[] values = _values;
			final Object[] set = _set;

			for (int i = values.length; i-- > 0;) {
				if (set[i] != FREE && set[i] != REMOVED && value == values[i]
						|| null != values[i] && values[i].equals(value)) {

					removeAt(i);
					return true;
				}
			}

			return false;
		}
	}

	private abstract class THashIterator<T> implements Iterator<T> {

		/**
		 * Create an instance of THashIterator over the values of the
		 * TObjectHash
		 */
		private THashIterator() {
			_expectedSize = TOrderedHashMap.this.size();
			_index = -1;
		}

		/**
		 * Moves the iterator to the next Object and returns it.
		 * 
		 * @return an <code>Object</code> value
		 * @exception ConcurrentModificationException
		 *                if the structure was changed using a method that isn't
		 *                on this iterator.
		 * @exception NoSuchElementException
		 *                if this is called on an exhausted iterator.
		 */
		@Override
		public T next() {
			moveToNextIndex();
			return objectAtIndex(_indicesByInsertOrder[_index]);
		}

		protected abstract T objectAtIndex(int idx);

		/**
		 * Returns the index of the next value in the data structure or a
		 * negative value if the iterator is exhausted.
		 * 
		 * @return an <code>int</code> value
		 * @exception ConcurrentModificationException
		 *                if the underlying collection's size has been modified
		 *                since the iterator was created.
		 */
		protected final int nextIndex() {
			if (_expectedSize != TOrderedHashMap.this.size()) {
				throw new ConcurrentModificationException();
			}

			final Object[] set = TOrderedHashMap.this._set;
			int i = _index + 1;
			while (i <= _lastInsertOrderIndex
					&& (_indicesByInsertOrder[i] == EMPTY || set[_indicesByInsertOrder[i]] == TObjectHash.FREE
							|| set[_indicesByInsertOrder[i]] == TObjectHash.REMOVED)) {
				i++;
			}
			return i <= _lastInsertOrderIndex ? i : -1;
		}

		/**
		 * the number of elements this iterator believes are in the data
		 * structure it accesses.
		 */
		protected int _expectedSize;
		/** the index used for iteration. */
		protected int _index;

		/**
		 * Returns true if the iterator can be advanced past its current
		 * location.
		 * 
		 * @return a <code>boolean</code> value
		 */
		@Override
		public boolean hasNext() {
			return nextIndex() >= 0;
		}

		/**
		 * Removes the last entry returned by the iterator. Invoking this method
		 * more than once for a single entry will leave the underlying data
		 * structure in a confused state.
		 */
		@Override
		public void remove() {
			if (_expectedSize != TOrderedHashMap.this.size()) {
				throw new ConcurrentModificationException();
			}

			// Disable auto compaction during the remove. This is a workaround
			// for bug 1642768.
			try {
				TOrderedHashMap.this.tempDisableAutoCompaction();
				TOrderedHashMap.this.removeAt(_indicesByInsertOrder[_index]);
			} finally {
				TOrderedHashMap.this.reenableAutoCompaction(false);
			}

			_expectedSize--;
		}

		/**
		 * Sets the internal <tt>index</tt> so that the `next' object can be
		 * returned.
		 */
		protected final void moveToNextIndex() {
			// doing the assignment && < 0 in one line shaves
			// 3 opcodes...
			if ((_index = nextIndex()) < 0) {
				throw new NoSuchElementException();
			}
		}

	}

	int maxSize() {
		return _maxSize;
	}

	public V anyValue(final IScope scope) {
		final int size = _size;
		if (size == 0) {
			return null;
		}
		final int i = scope.getRandom().between(0, _size - 1);
		return valueAt(i);
	}

	// Zero-based access
	public V valueAt(final int i) {
		if (i > _size - 1) {
			return null;
		}
		int index = -1;
		for (int insert = 0; insert <= _lastInsertOrderIndex; insert++) {
			final int keyIndex = _indicesByInsertOrder[insert];
			if (keyIndex == EMPTY) {
				continue;
			} else {
				index++;
				if (index == i) {
					return _values[keyIndex];
				}
			}
		}
		return null;
	}

}
