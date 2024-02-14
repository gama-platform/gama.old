/*******************************************************************************************************
 *
 * FSTMap.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package org.nustaq.serialization.util;

/**
 * Created by ruedi on 15.06.2015.
 */
public class FSTMap<K, V> {

	/** The prim. */
	static int[] prim = FSTObject2IntMap.prim;

	/** The Constant GROFAC. */
	private static final int GROFAC = 2;

	/**
	 * Adjust size.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param size
	 *            the size
	 * @return the int
	 * @date 11 févr. 2024
	 */
	public static int adjustSize(final int size) {
		for (int i = 0; i < prim.length - 1; i++) { if (size < prim[i]) return prim[i]; }
		return size;
	}

	/** The m keys. */
	public Object mKeys[];

	/** The m values. */
	public Object mValues[];

	/** The m number of elements. */
	public int mNumberOfElements;

	/** The next. */
	FSTMap<K, V> next;

	/** The check clazz on equals. */
	boolean checkClazzOnEquals = false;

	/**
	 * Instantiates a new FST map.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param initialSize
	 *            the initial size
	 * @date 11 févr. 2024
	 */
	public FSTMap(int initialSize) {
		if (initialSize < 2) { initialSize = 2; }

		initialSize = adjustSize(initialSize * 2);

		mKeys = new Object[initialSize];
		mValues = new Object[initialSize];
		mNumberOfElements = 0;
	}

	/**
	 * Size.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the int
	 * @date 11 févr. 2024
	 */
	public int size() {
		return mNumberOfElements + (next != null ? next.size() : 0);
	}

	/**
	 * Put.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @date 11 févr. 2024
	 */
	final public void put(final K key, final V value) {
		int hash = key.hashCode() & 0x7FFFFFFF;
		putHash(key, value, hash, this);
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
	final void putHash(final K key, final V value, final int hash, final FSTMap<K, V> parent) {
		if (mNumberOfElements * GROFAC > mKeys.length) {
			if (parent != null && (parent.mNumberOfElements + mNumberOfElements) * GROFAC > parent.mKeys.length) {
				parent.resize(parent.mKeys.length * GROFAC);
				parent.put(key, value);
				return;
			}
			resize(mKeys.length * GROFAC);
		}

		int idx = hash % mKeys.length;

		if (mKeys[idx] == null) // new
		{
			mNumberOfElements++;
			mValues[idx] = value;
			mKeys[idx] = key;
		} else if (mKeys[idx].equals(key) && (!checkClazzOnEquals || mKeys[idx].getClass() == key.getClass())) // overwrite
		{
			mValues[idx] = value;
		} else {
			putNext(hash, key, value);
		}
	}

	/**
	 * Removes the hash.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param key
	 *            the key
	 * @param hash
	 *            the hash
	 * @return the k
	 * @date 11 févr. 2024
	 */
	final K removeHash(final K key, final int hash) {
		final int idx = hash % mKeys.length;

		final Object mKey = mKeys[idx];
		if (mKey == null)
			// hit++;
			return null;
		if (mKey.equals(key) && (!checkClazzOnEquals || mKeys[idx].getClass() == key.getClass())) // found
		{
			// hit++;
			@SuppressWarnings ("unchecked") K val = (K) mKeys[idx];
			mValues[idx] = 0;
			mKeys[idx] = null;
			mNumberOfElements--;
			return val;
		}
		if (next == null) return null;
		// miss++;
		return next.removeHash(key, hash);
	}

	/**
	 * Put next.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param hash
	 *            the hash
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @date 11 févr. 2024
	 */
	final void putNext(final int hash, final K key, final V value) {
		if (next == null) {
			int newSiz = mNumberOfElements / 3;
			next = new FSTMap<>(newSiz);
		}
		next.putHash(key, value, hash, this);
	}

	/**
	 * Gets the.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param key
	 *            the key
	 * @return the v
	 * @date 11 févr. 2024
	 */
	@SuppressWarnings ("unchecked")
	final public V get(final K key) {
		final int hash = key.hashCode() & 0x7FFFFFFF;
		// return getHash(key,hash); inline =>
		final int idx = hash % mKeys.length;

		final Object mapsKey = mKeys[idx];
		if (mapsKey == null) return null;
		if (mapsKey.equals(key) && (!checkClazzOnEquals || mapsKey.getClass() == key.getClass()))
			return (V) mValues[idx];
		if (next == null) return null;
		V res = next.getHash(key, hash);
		return res;
	}

	/** The miss. */
	static int miss = 0;

	/** The hit. */
	static int hit = 0;

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
	final V getHash(final K key, final int hash) {
		final int idx = hash % mKeys.length;

		final Object mapsKey = mKeys[idx];
		if (mapsKey == null) return null;
		if (mapsKey.equals(key) && (!checkClazzOnEquals || mapsKey.getClass() == key.getClass()))
			return (V) mValues[idx];
		if (next == null) return null;
		V res = next.getHash(key, hash);
		return res;
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
		newSize = adjustSize(newSize);
		Object[] oldTabKey = mKeys;
		Object[] oldTabVal = mValues;

		mKeys = new Object[newSize];
		mValues = new Object[newSize];
		mNumberOfElements = 0;

		for (int n = 0; n < oldTabKey.length; n++) {
			if (oldTabKey[n] != null) { put((K) oldTabKey[n], (V) oldTabVal[n]); }
		}
		if (next != null) {
			FSTMap<K, V> oldNext = next;
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
	private void rePut(final FSTMap<K, V> kfstObject2IntMap) {
		for (int i = 0; i < mKeys.length; i++) {
			Object mKey = mKeys[i];
			if (mKey != null) { kfstObject2IntMap.put((K) mKey, (V) mValues[i]); }
		}
		if (next != null) { next.rePut(kfstObject2IntMap); }
	}

	/**
	 * Clear.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 11 févr. 2024
	 */
	public void clear() {
		if (size() == 0) return;
		FSTUtil.clear(mKeys);
		FSTUtil.clear(mValues);
		mNumberOfElements = 0;
		if (next != null) { next.clear(); }
	}

}
