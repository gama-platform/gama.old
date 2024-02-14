/*******************************************************************************************************
 *
 * FSTObject2IntMap.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package org.nustaq.serialization.util;

/**
 * Created with IntelliJ IDEA. User: ruedi Date: 20.11.12 Time: 19:57 To change this template use File | Settings | File
 * Templates.
 */
public class FSTObject2IntMap<K> {

	/** The prim. */
	static int[] prim = { 3, 5, 7, 11, 13, 17, 19, 23, 29, 37, 67, 97, 139, 211, 331, 641, 1097, 1531, 2207, 3121, 5059,
			7607, 10891, 15901, 19993, 30223, 50077, 74231, 99991, 150001, 300017, 1000033, 1500041, 200033, 3000077,
			5000077, 10000019 };

	/** The Constant GROFAC. */
	private static final int GROFAC = 2;

	/**
	 * Adjust size.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param size
	 *            the size
	 * @return the int
	 * @date 30 sept. 2023
	 */
	public static int adjustSize(final int size) {
		for (int i = 0; i < prim.length - 1; i++) { if (size < prim[i]) return prim[i]; }
		return size;
	}

	/** The m keys. */
	public Object mKeys[];

	/** The m values. */
	public int mValues[];

	/** The m number of elements. */
	public int mNumberOfElements;

	/** The next. */
	FSTObject2IntMap<K> next;

	/** The check clazz on equals. */
	boolean checkClazzOnEquals = false;

	/**
	 * Instantiates a new FST object 2 int map.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param initialSize
	 *            the initial size
	 * @param checkClassOnequals
	 *            the check class onequals
	 * @date 30 sept. 2023
	 */
	public FSTObject2IntMap(int initialSize, final boolean checkClassOnequals) {
		if (initialSize < 2) { initialSize = 2; }

		initialSize = adjustSize(initialSize * 2);

		mKeys = new Object[initialSize];
		mValues = new int[initialSize];
		mNumberOfElements = 0;
		this.checkClazzOnEquals = checkClassOnequals;
	}

	/**
	 * Size.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the int
	 * @date 30 sept. 2023
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
	 * @date 30 sept. 2023
	 */
	final public void put(final K key, final int value) {
		int hash = key.hashCode() & 0x7FFFFFFF;
		putHash(key, value, hash, this);
	}

	/**
	 * Put hash.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param <K>
	 *            the key type
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
	 * @param checkClazzOnEquals
	 *            the check clazz on equals
	 * @date 30 sept. 2023
	 */
	final private static <K> void putHash(final K key, final int value, final int hash, FSTObject2IntMap<K> current,
			FSTObject2IntMap<K> parent, final boolean checkClazzOnEquals) {
		int count = 0;
		while (true) {
			if (current.mNumberOfElements * GROFAC > current.mKeys.length) {
				if (parent != null
						&& (parent.mNumberOfElements + current.mNumberOfElements) * GROFAC > parent.mKeys.length) {
					parent.resize(parent.mKeys.length * GROFAC);
					parent.put(key, value);
					return;
				}
				current.resize(current.mKeys.length * GROFAC);
			}

			int idx = hash % current.mKeys.length;

			if (current.mKeys[idx] == null) // new
			{
				current.mNumberOfElements++;
				current.mValues[idx] = value;
				current.mKeys[idx] = key;
				return;
			}
			if (current.mKeys[idx] == key && (!checkClazzOnEquals || current.mKeys[idx].getClass() == key.getClass())) // overwrite
			{
				current.mValues[idx] = value;
				return;
			}
			if (current.next == null) {
				// try break edge cases leading to long chains of maps
				if (count > 4 && current.mNumberOfElements < 5) {
					int newSiz = current.mNumberOfElements * 2 + 1;
					current.next = new FSTObject2IntMap<>(newSiz, checkClazzOnEquals);
					count = 0;
				} else {
					int newSiz = current.mNumberOfElements / 3;
					current.next = new FSTObject2IntMap<>(newSiz, checkClazzOnEquals);
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
	 * @date 30 sept. 2023
	 */
	final void putHash(final K key, final int value, final int hash, final FSTObject2IntMap<K> parent) {
		putHash(key, value, hash, this, parent, checkClazzOnEquals);
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
	 * @date 30 sept. 2023
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
	 * @date 30 sept. 2023
	 */
	final void putNext(final int hash, final K key, final int value) {
		if (next == null) {
			int newSiz = mNumberOfElements / 3;
			next = new FSTObject2IntMap<>(newSiz, checkClazzOnEquals);
		}
		next.putHash(key, value, hash, this);
	}

	/**
	 * Gets the.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param key
	 *            the key
	 * @return the int
	 * @date 30 sept. 2023
	 */
	final public int get(final K key) {
		final int hash = key.hashCode() & 0x7FFFFFFF;
		// return getHash(key,hash); inline =>
		final int idx = hash % mKeys.length;

		final Object mapsKey = mKeys[idx];
		if (mapsKey == null) return Integer.MIN_VALUE;
		if (mapsKey.equals(key) && (!checkClazzOnEquals || mapsKey.getClass() == key.getClass())) return mValues[idx];
		if (next == null) return Integer.MIN_VALUE;
		int res = next.getHash(key, hash);
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
	 * @date 30 sept. 2023
	 */
	final int getHash(final K key, final int hash) {
		final int idx = hash % mKeys.length;

		final Object mapsKey = mKeys[idx];
		if (mapsKey == null) return Integer.MIN_VALUE;
		if (mapsKey.equals(key) && (!checkClazzOnEquals || mapsKey.getClass() == key.getClass())) return mValues[idx];
		if (next == null) return Integer.MIN_VALUE;
		int res = next.getHash(key, hash);
		return res;
	}

	/**
	 * Resize.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param newSize
	 *            the new size
	 * @date 30 sept. 2023
	 */
	@SuppressWarnings ("unchecked")
	final void resize(int newSize) {
		newSize = adjustSize(newSize);
		Object[] oldTabKey = mKeys;
		int[] oldTabVal = mValues;

		mKeys = new Object[newSize];
		mValues = new int[newSize];
		mNumberOfElements = 0;

		for (int n = 0; n < oldTabKey.length; n++) {
			if (oldTabKey[n] != null) { put((K) oldTabKey[n], oldTabVal[n]); }
		}
		if (next != null) {
			FSTObject2IntMap oldNext = next;
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
	 * @date 30 sept. 2023
	 */
	@SuppressWarnings ("unchecked")
	private void rePut(final FSTObject2IntMap<K> kfstObject2IntMap) {
		for (int i = 0; i < mKeys.length; i++) {
			Object mKey = mKeys[i];
			if (mKey != null) { kfstObject2IntMap.put((K) mKey, mValues[i]); }
		}
		if (next != null) { next.rePut(kfstObject2IntMap); }
	}

	/**
	 * Clear.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 30 sept. 2023
	 */
	public void clear() {
		if (size() == 0) return;
		FSTUtil.clear(mKeys);
		FSTUtil.clear(mValues);
		mNumberOfElements = 0;
		if (next != null) { next.clear(); }
	}

}