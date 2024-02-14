/*******************************************************************************************************
 *
 * DefaultFSTInt2ObjectMap.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package org.nustaq.serialization.util;

/**
 * The Class DefaultFSTInt2ObjectMap.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @param <V>
 *            the value type
 * @date 11 févr. 2024
 */
public class DefaultFSTInt2ObjectMap<V> implements FSTInt2ObjectMap<V> {

	/** The m keys. */
	private int mKeys[];

	/** The m values. */
	private Object mValues[];

	/** The m number of elements. */
	private int mNumberOfElements;

	/** The next. */
	private DefaultFSTInt2ObjectMap<V> next;

	/** The Constant GROWFAC. */
	private static final int GROWFAC = 2;

	/**
	 * Instantiates a new default FST int 2 object map.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param initialSize
	 *            the initial size
	 * @date 11 févr. 2024
	 */
	public DefaultFSTInt2ObjectMap(int initialSize) {
		if (initialSize < 2) { initialSize = 2; }

		initialSize = FSTObject2IntMap.adjustSize(initialSize * 2);

		mKeys = new int[initialSize];
		mValues = new Object[initialSize];
		mNumberOfElements = 0;
	}

	@Override
	public int size() {
		return mNumberOfElements + (next != null ? next.size() : 0);
	}

	@Override
	final public void put(final int key, final V value) {
		int hash = key & 0x7FFFFFFF;
		if (key == 0 && value == null) throw new RuntimeException("key value pair not supported " + key + " " + value);
		putHash(key, value, hash, this);
	}

	/**
	 * Put hash.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param <V>
	 *            the value type
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @param hash
	 *            the hash
	 * @param current
	 *            the current
	 * @param parent
	 *            the parent
	 * @date 11 févr. 2024
	 */
	final private static <V> void putHash(final int key, final V value, final int hash,
			DefaultFSTInt2ObjectMap<V> current, DefaultFSTInt2ObjectMap<V> parent) {
		int count = 0;
		while (true) {
			if (current.mNumberOfElements * GROWFAC > current.mKeys.length) {
				if (parent != null
						&& (parent.mNumberOfElements + current.mNumberOfElements) * GROWFAC > parent.mKeys.length) {
					parent.resize(parent.mKeys.length * GROWFAC);
					parent.put(key, value);
					return;
				}
				current.resize(current.mKeys.length * GROWFAC);
			}

			int idx = hash % current.mKeys.length;

			if (current.mKeys[idx] == 0 && current.mValues[idx] == null) // new
			{
				current.mNumberOfElements++;
				current.mValues[idx] = value;
				current.mKeys[idx] = key;
				return;
			}
			if (current.mKeys[idx] == key) // overwrite
			{
				current.mValues[idx] = value;
				return;
			}
			if (current.next == null) {
				// try break edge cases leading to long chains of maps
				if (count > 4 && current.mNumberOfElements < 5) {
					int newSiz = current.mNumberOfElements * 2 + 1;
					current.next = new DefaultFSTInt2ObjectMap<>(newSiz);
					count = 0;
				} else {
					int newSiz = current.mNumberOfElements / 3;
					current.next = new DefaultFSTInt2ObjectMap<>(newSiz);
				}
			}
			parent = current;
			current = current.next;
			count++;
		}
	}

	/**
	 * Put hash.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @param hash
	 *            the hash
	 * @param parent
	 *            the parent
	 * @date 11 févr. 2024
	 */
	final void putHash(final int key, final V value, final int hash, final DefaultFSTInt2ObjectMap<V> parent) {
		putHash(key, value, hash, this, parent);
	}

	@Override
	final public V get(final int key) {
		int hash = key & 0x7FFFFFFF;
		return getHash(key, hash);
	}

	/**
	 * Gets the hash.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param key
	 *            the key
	 * @param hash
	 *            the hash
	 * @return the hash
	 * @date 11 févr. 2024
	 */
	@SuppressWarnings ("unchecked")
	final V getHash(final int key, final int hash) {
		final int idx = hash % mKeys.length;

		final int mKey = mKeys[idx];
		final Object mValue = mValues[idx];
		if (mKey == 0 && mValue == null)
			// hit++;
			return null;
		if (mKey == key)
			// hit++;
			return (V) mValue;
		if (next == null) return null;
		// miss++;
		return next.getHash(key, hash);
	}

	/**
	 * Resize.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param newSize
	 *            the new size
	 * @date 11 févr. 2024
	 */
	@SuppressWarnings ("unchecked")
	final void resize(int newSize) {
		newSize = FSTObject2IntMap.adjustSize(newSize);
		int[] oldTabKey = mKeys;
		Object[] oldTabVal = mValues;

		mKeys = new int[newSize];
		mValues = new Object[newSize];
		mNumberOfElements = 0;

		for (int n = 0; n < oldTabKey.length; n++) {
			if (oldTabKey[n] != 0 || oldTabVal[n] != null) { put(oldTabKey[n], (V) oldTabVal[n]); }
		}
		if (next != null) {
			DefaultFSTInt2ObjectMap oldNext = next;
			next = null;
			oldNext.rePut(this);
		}
	}

	/**
	 * Re put.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param kfstObject2IntMap
	 *            the kfst object 2 int map
	 * @date 11 févr. 2024
	 */
	@SuppressWarnings ("unchecked")
	private void rePut(final DefaultFSTInt2ObjectMap<V> kfstObject2IntMap) {
		for (int i = 0; i < mKeys.length; i++) {
			int mKey = mKeys[i];
			if (mKey != 0 || mValues[i] != null) { kfstObject2IntMap.put(mKey, (V) mValues[i]); }
		}
		if (next != null) { next.rePut(kfstObject2IntMap); }
	}

	@Override
	public void clear() {
		int size = size();
		if (size == 0) return;
		if (mKeys.length > 6 * size() && size > 0) {
			// avoid cleaning huge mem areas after having written a large object
			if (size < 2) { size = 2; }
			size = FSTObject2IntMap.adjustSize(size * 2);
			mKeys = new int[size];
			mValues = new Object[size];
			mNumberOfElements = 0;
		} else {
			FSTUtil.clear(mKeys);
			FSTUtil.clear(mValues);
			mNumberOfElements = 0;
			if (next != null) { next.clear(); }
		}
	}
}