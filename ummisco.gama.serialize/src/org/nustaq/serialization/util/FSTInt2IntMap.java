/*******************************************************************************************************
 *
 * FSTInt2IntMap.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package org.nustaq.serialization.util;

/**
 * Created with IntelliJ IDEA. User: ruedi Date: 20.11.12 Time: 21:02
 * <p>
 * 
 * unused currently. pretty old.
 */
public class FSTInt2IntMap {

	/** The m keys. */
	public int mKeys[];

	/** The m values. */
	public int mValues[];

	/** The m number of elements. */
	public int mNumberOfElements;

	/** The next. */
	FSTInt2IntMap next;

	/** The Constant GROWFAC. */
	private static final int GROWFAC = 2;

	/**
	 * Instantiates a new FST int 2 int map.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param initialSize
	 *            the initial size
	 * @date 30 sept. 2023
	 */
	public FSTInt2IntMap(int initialSize) {
		if (initialSize < 2) { initialSize = 2; }

		initialSize = FSTObject2IntMap.adjustSize(initialSize * 2);

		mKeys = new int[initialSize];
		mValues = new int[initialSize];
		mNumberOfElements = 0;
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
	final public void put(final int key, final int value) {
		int hash = key & 0x7FFFFFFF;
		if (key == 0 && value == 0 || value == Integer.MIN_VALUE)
			throw new RuntimeException("key value pair not supported " + key + " " + value);
		// putHash(key, value, hash); inline ..
		if (mNumberOfElements * GROWFAC > mKeys.length) { resize(mKeys.length * GROWFAC); }

		int idx = hash % mKeys.length;

		final int mKey = mKeys[idx];
		if (mKey == 0 && mValues[idx] == 0) // new
		{
			mNumberOfElements++;
			mValues[idx] = value;
			mKeys[idx] = key;
		} else if (mKey == key) // overwrite
		{
			mValues[idx] = value;
		} else {
			putNext(hash, key, value);
		}
		// end inline
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
	final void putHash(final int key, final int value, final int hash, final FSTInt2IntMap parent) {
		if (mNumberOfElements * GROWFAC > mKeys.length) {
			if (parent != null) {
				if ((parent.mNumberOfElements + mNumberOfElements) * GROWFAC > parent.mKeys.length) {
					parent.resize(parent.mKeys.length * GROWFAC);
					parent.put(key, value);
					return;
				}
			}
			resize(mKeys.length * GROWFAC);
		}
		int idx = hash % mKeys.length;

		final int mKey = mKeys[idx];
		if (mKey == 0 && mValues[idx] == 0) // new
		{
			mNumberOfElements++;
			mValues[idx] = value;
			mKeys[idx] = key;
		} else if (mKey == key) // overwrite
		{
			mValues[idx] = value;
		} else {
			putNext(hash, key, value);
		}
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
	final void putNext(final int hash, final int key, final int value) {
		if (next == null) {
			int newSiz = mNumberOfElements / 3;
			next = new FSTInt2IntMap(newSiz);
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
	final public int get(final int key) {
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
	 * @date 30 sept. 2023
	 */
	final int getHash(final int key, final int hash) {
		final int idx = hash % mKeys.length;

		final int mKey = mKeys[idx];
		if (mKey == 0 && mValues[idx] == 0) return Integer.MIN_VALUE;
		if (mKey == key)
			return mValues[idx];
		else {
			if (next == null) return Integer.MIN_VALUE;
			return next.getHash(key, hash);
		}
	}

	/**
	 * Resize.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param newSize
	 *            the new size
	 * @date 30 sept. 2023
	 */
	final void resize(int newSize) {
		newSize = FSTObject2IntMap.adjustSize(newSize);
		int[] oldTabKey = mKeys;
		int[] oldTabVal = mValues;

		mKeys = new int[newSize];
		mValues = new int[newSize];
		mNumberOfElements = 0;

		for (int n = 0; n < oldTabKey.length; n++) {
			if (oldTabKey[n] != 0 || oldTabVal[n] != 0) { put(oldTabKey[n], oldTabVal[n]); }
		}
		if (next != null) {
			FSTInt2IntMap oldNext = next;
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
	private void rePut(final FSTInt2IntMap kfstObject2IntMap) {
		for (int i = 0; i < mKeys.length; i++) {
			int mKey = mKeys[i];
			if (mKey != 0 || mValues[i] != 0) { kfstObject2IntMap.put(mKey, mValues[i]); }
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
		FSTUtil.clear(mKeys);
		FSTUtil.clear(mValues);
		mNumberOfElements = 0;
		if (next != null) { next.clear(); }
	}

}