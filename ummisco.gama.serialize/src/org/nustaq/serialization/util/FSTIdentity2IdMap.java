/*******************************************************************************************************
 *
 * FSTIdentity2IdMap.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package org.nustaq.serialization.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: ruedi Date: 20.11.12 Time: 19:57 To change this template use File | Settings | File
 * Templates.
 */
public class FSTIdentity2IdMap {

	/** The Constant RESERVE. */
	private static final int RESERVE = 4;

	/** The Constant MAX_DEPTH. */
	private static final int MAX_DEPTH = 4;

	/** The prim. */
	static int[] prim = { 3, 5, 7, 11, 13, 17, 19, 23, 29, 37, 67, 97, 139, 211, 331, 641, 1097, 1531, 2207, 3121, 5059,
			7607, 10891, 15901, 19993, 30223, 50077, 74231, 99991, 150001, 300017, 1000033, 1500041, 200033, 3000077,
			5000077, 10000019

	};

	/**
	 * Adjust size.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param size
	 *            the size
	 * @return the int
	 * @date 11 févr. 2024
	 */
	static int adjustSize(final int size) {
		for (int i = 0; i < prim.length - 1; i++) { if (size < prim[i]) return prim[i] + RESERVE; }
		return size + RESERVE;
	}

	/** The Constant GROFAC. */
	private static final int GROFAC = 2;

	/** The mask. */
	private int mask;

	/** The m keys. */
	public Object[] mKeys;

	/** The klen. */
	private int klen;

	/** The m values. */
	private int mValues[];

	/** The m number of elements. */
	private int mNumberOfElements;

	/** The next. */
	private FSTIdentity2IdMap next;

	/** The linear scan list. */
	private List<Object> linearScanList; // in case of too deep nesting, this one is filled and linear search is applied

	/** The linear scan vals. */
	private List<Integer> linearScanVals; // in case of too deep nesting, this one is filled and linear search is
											// applied

	/**
	 * Instantiates a new FST identity 2 id map.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param initialSize
	 *            the initial size
	 * @date 11 févr. 2024
	 */
	public FSTIdentity2IdMap(int initialSize) {
		if (initialSize < 2) { initialSize = 2; }

		initialSize = adjustSize(initialSize * GROFAC);

		mKeys = new Object[initialSize];
		mValues = new int[initialSize];
		mNumberOfElements = 0;
		mask = (Integer.highestOneBit(initialSize) << 1) - 1;
		klen = initialSize - 4;
	}

	/**
	 * Size.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the int
	 * @date 11 févr. 2024
	 */
	public int size() {
		if (linearScanList != null) return linearScanList.size();
		return mNumberOfElements + (next != null ? next.size() : 0);
	}

	/**
	 * Put or get.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @return the int
	 * @date 11 févr. 2024
	 */
	final public int putOrGet(final Object key, final int value) {
		int hash = calcHash(key);
		return putOrGetHash(key, value, hash, this, 0);
	}

	/**
	 * Put or get hash.
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
	 * @param depth
	 *            the depth
	 * @return the int
	 * @date 11 févr. 2024
	 */
	final int putOrGetHash(final Object key, final int value, final int hash, final FSTIdentity2IdMap parent,
			final int depth) {
		if (linearScanList != null) {
			for (int i = 0; i < linearScanList.size(); i++) {
				Object o = linearScanList.get(i);
				if (o == key) return linearScanVals.get(i);
			}
			linearScanList.add(key);
			linearScanVals.add(value);
			return Integer.MIN_VALUE;
		}
		if (mNumberOfElements * GROFAC > mKeys.length) {
			if (parent != null) {
				if ((parent.mNumberOfElements + mNumberOfElements) * GROFAC > parent.mKeys.length) {
					parent.resize(parent.mKeys.length * GROFAC);
					return parent.putOrGet(key, value);
				}
			}
			resize(mKeys.length * GROFAC);
		}

		Object[] mKeys = this.mKeys;
		// int idx = calcIndexFromHash(hash, mKeys);
		int idx = calcIndexFromHash(hash, mKeys);

		Object mKeyAtIdx = mKeys[idx];
		if (mKeyAtIdx == null) // new
		{
			mNumberOfElements++;
			mValues[idx] = value;
			mKeys[idx] = key;
			return Integer.MIN_VALUE;
		}
		if (mKeyAtIdx == key)
			return mValues[idx];
		else {
			Object mKeyAtIdxPlus1 = mKeys[idx + 1];
			if (mKeyAtIdxPlus1 == null) // new
			{
				mNumberOfElements++;
				mValues[idx + 1] = value;
				mKeys[idx + 1] = key;
				return Integer.MIN_VALUE;
			} else if (mKeyAtIdxPlus1 == key)
				return mValues[idx + 1];
			else {
				Object mKeysAtIndexPlus2 = mKeys[idx + 2];
				if (mKeysAtIndexPlus2 == null) // new
				{
					mNumberOfElements++;
					mValues[idx + 2] = value;
					mKeys[idx + 2] = key;
					return Integer.MIN_VALUE;
				} else if (mKeysAtIndexPlus2 == key)
					return mValues[idx + 2];
				else
					return putOrGetNext(hash, key, value, depth + 1);
			}
		}
	}

	/**
	 * Put or get next.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param hash
	 *            the hash
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @param depth
	 *            the depth
	 * @return the int
	 * @date 11 févr. 2024
	 */
	final int putOrGetNext(final int hash, final Object key, final int value, final int depth) {
		if (next == null) { // new
			int newSiz = mKeys.length / 10;
			next = new FSTIdentity2IdMap(newSiz);
			if (depth > MAX_DEPTH) {
				next.linearScanVals = new ArrayList<>(3);
				next.linearScanList = new ArrayList<>(3);
			}
			next.putHash(key, value, hash, this, depth);
			return Integer.MIN_VALUE;
		}
		return next.putOrGetHash(key, value, hash, this, depth + 1);
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
	final public void put(final Object key, final int value) {
		int hash = calcHash(key);
		putHash(key, value, hash, this, 0);
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
	 * @param depth
	 *            the depth
	 * @date 11 févr. 2024
	 */
	final void putHash(final Object key, final int value, final int hash, final FSTIdentity2IdMap parent,
			final int depth) {
		if (linearScanList != null) {
			for (int i = 0; i < linearScanList.size(); i++) {
				Object o = linearScanList.get(i);
				if (o == key) {
					linearScanVals.set(i, value);
					return;
				}
			}
			linearScanList.add(key);
			linearScanVals.add(value);
			return;
		}
		if (mNumberOfElements * GROFAC > mKeys.length) {
			if (parent != null) {
				if ((parent.mNumberOfElements + mNumberOfElements) * GROFAC > parent.mKeys.length) {
					parent.resize(parent.mKeys.length * GROFAC);
					parent.put(key, value);
					return;
				}
			}
			resize(mKeys.length * GROFAC);
		}

		Object[] mKeys = this.mKeys;
		int idx = calcIndexFromHash(hash, mKeys);

		if (mKeys[idx] == null) // new
		{
			mNumberOfElements++;
			mValues[idx] = value;
			mKeys[idx] = key;
		} else if (mKeys[idx] == key) // overwrite
		{
			// bloom|=hash;
			mValues[idx] = value;
		} else if (mKeys[idx + 1] == null) // new
		{
			mNumberOfElements++;
			mValues[idx + 1] = value;
			mKeys[idx + 1] = key;
		} else if (mKeys[idx + 1] == key) // overwrite
		{
			// bloom|=hash;
			mValues[idx + 1] = value;
		} else {
			if (mKeys[idx + 2] == null) // new
			{
				mNumberOfElements++;
				mValues[idx + 2] = value;
				mKeys[idx + 2] = key;
			} else if (mKeys[idx + 2] == key) // overwrite
			{
				// bloom|=hash;
				mValues[idx + 2] = value;
			} else {
				putNext(hash, key, value, depth + 1);
			}
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
	 * @param depth
	 *            the depth
	 * @date 11 févr. 2024
	 */
	final void putNext(final int hash, final Object key, final int value, final int depth) {
		if (next == null) {
			int newSiz = mKeys.length / 10;
			next = new FSTIdentity2IdMap(newSiz);
			if (depth > MAX_DEPTH) {
				next.linearScanVals = new ArrayList<>(3);
				next.linearScanList = new ArrayList<>(3);
			}
		}
		next.putHash(key, value, hash, this, depth + 1);
	}

	/**
	 * Gets the.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param key
	 *            the key
	 * @return the int
	 * @date 11 févr. 2024
	 */
	final public int get(final Object key) {
		int hash = calcHash(key);
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
	final int getHash(final Object key, final int hash) {
		if (linearScanList != null) {
			for (int i = 0; i < linearScanList.size(); i++) {
				Object o = linearScanList.get(i);
				if (o == key) return linearScanVals.get(i);
			}
			return Integer.MIN_VALUE;
		}

		final int idx = calcIndexFromHash(hash, mKeys);

		Object mapsKey = mKeys[idx];
		if (mapsKey == null) return Integer.MIN_VALUE;
		if (mapsKey == key)
			return mValues[idx];
		else {
			mapsKey = mKeys[idx + 1];
			if (mapsKey == null)
				return Integer.MIN_VALUE;
			else if (mapsKey == key)
				return mValues[idx + 1];
			else {
				mapsKey = mKeys[idx + 2];
				if (mapsKey == null)
					return Integer.MIN_VALUE;
				else if (mapsKey == key)
					return mValues[idx + 2];
				else {
					if (next == null) return Integer.MIN_VALUE;
					return next.getHash(key, hash);
				}
			}
		}
	}

	/**
	 * Resize.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param newSize
	 *            the new size
	 * @date 11 févr. 2024
	 */
	final void resize(int newSize) {
		newSize = adjustSize(newSize);
		Object[] oldTabKey = mKeys;
		int[] oldTabVal = mValues;

		mKeys = new Object[newSize];
		mValues = new int[newSize];
		mNumberOfElements = 0;
		mask = (Integer.highestOneBit(newSize) << 1) - 1;
		klen = newSize - RESERVE;

		for (int n = 0; n < oldTabKey.length; n++) { if (oldTabKey[n] != null) { put(oldTabKey[n], oldTabVal[n]); } }
		if (next != null) {
			FSTIdentity2IdMap oldNext = next;
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
	private void rePut(final FSTIdentity2IdMap kfstObject2IntMap) {
		if (linearScanList != null) {
			int size = linearScanList.size();
			for (int i = 0; i < size; i++) {
				Object key = linearScanList.get(i);
				int value = linearScanVals.get(i);
				kfstObject2IntMap.put(key, value);
			}
			return;
		}

		for (int i = 0; i < mKeys.length; i++) {
			Object mKey = mKeys[i];
			if (mKey != null) { kfstObject2IntMap.put(mKey, mValues[i]); }
		}
		if (next != null) { next.rePut(kfstObject2IntMap); }
	}

	/**
	 * Calc index from hash.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param hash
	 *            the hash
	 * @param mKeys
	 *            the m keys
	 * @return the int
	 * @date 11 févr. 2024
	 */
	final int calcIndexFromHash(final int hash, final Object[] mKeys) {
		int res = hash & mask;
		while (res >= klen) { res = res >>> 1; }
		return res;
	}

	/**
	 * Calc hash.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param x
	 *            the x
	 * @return the int
	 * @date 11 févr. 2024
	 */
	private static int calcHash(final Object x) {
		int h = System.identityHashCode(x);
		// return h>>2;
		return (h << 1) - (h << 8) & 0x7fffffff;
	}

	/**
	 * Clear.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 11 févr. 2024
	 */
	public void clear() {
		if (size() == 0) return;
		if (linearScanList != null) {
			linearScanList.clear();
			linearScanVals.clear();
		}
		FSTUtil.clear(mKeys);
		FSTUtil.clear(mValues);
		// Arrays.fill(mKeys,null);
		// Arrays.fill(mValues,0);
		mNumberOfElements = 0;
		if (next != null) { next.clear(); }
	}

	/**
	 * Dump.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 11 févr. 2024
	 */
	public void dump() {
		for (int i = 0; i < mKeys.length; i++) {
			Object mKey = mKeys[i];
			if (mKey != null) { System.out.println("" + mKey + " => " + mValues[i]); }
		}
		if (next != null) { next.dump(); }
	}

	/**
	 * Keys length.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the int
	 * @date 11 févr. 2024
	 */
	public int keysLength() {
		return mKeys.length;
	}
}