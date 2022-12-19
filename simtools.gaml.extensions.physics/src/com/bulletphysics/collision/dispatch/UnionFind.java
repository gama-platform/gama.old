/*******************************************************************************************************
 *
 * UnionFind.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.dispatch;

import java.util.ArrayList;
import java.util.Collections;

import com.bulletphysics.linearmath.MiscUtil;

/**
 * UnionFind calculates connected subsets. Implements weighted Quick Union with path compression.
 *
 * @author jezek2
 */
public class UnionFind {

	/** The elements. */
	private final ArrayList<Element> elements = new ArrayList<>();

	/**
	 * This is a special operation, destroying the content of UnionFind. It sorts the elements, based on island id, in
	 * order to make it easy to iterate over islands.
	 */
	public void sortIslands() {
		// first store the original body index, and islandId
		int numElements = elements.size();

		for (int i = 0; i < numElements; i++) {
			elements.get(i).id = find(i);
			elements.get(i).sz = i;
		}
		// elements.sort(elementComparator);
		Collections.sort(elements);
	}

	/**
	 * Reset.
	 *
	 * @param N the n
	 */
	public void reset(final int N) {
		allocate(N);

		for (int i = 0; i < N; i++) {
			elements.get(i).id = i;
			elements.get(i).sz = 1;
		}
	}

	/**
	 * Gets the num elements.
	 *
	 * @return the num elements
	 */
	public int getNumElements() {
		return elements.size();
	}

	/**
	 * Checks if is root.
	 *
	 * @param x the x
	 * @return true, if is root
	 */
	public boolean isRoot(final int x) {
		return x == elements.get(x).id;
	}

	/**
	 * Gets the element.
	 *
	 * @param index the index
	 * @return the element
	 */
	public Element getElement(final int index) {
		return elements.get(index);
	}

	/**
	 * Allocate.
	 *
	 * @param N the n
	 */
	public void allocate(final int N) {
		MiscUtil.resize(elements, N, Element.class);
	}

	/**
	 * Free.
	 */
	public void free() {
		elements.clear();
	}

	/**
	 * Find.
	 *
	 * @param p the p
	 * @param q the q
	 * @return the int
	 */
	public int find(final int p, final int q) {
		return find(p) == find(q) ? 1 : 0;
	}

	/**
	 * Unite.
	 *
	 * @param p the p
	 * @param q the q
	 */
	public void unite(final int p, final int q) {
		int i = find(p), j = find(q);
		if (i == j) return;
		elements.get(i).id = j;
		elements.get(j).sz += elements.get(i).sz;
	}

	/**
	 * Find.
	 *
	 * @param x the x
	 * @return the int
	 */
	public int find(int x) {
		while (x != elements.get(x).id) {
			elements.get(x).id = elements.get(elements.get(x).id).id;
			x = elements.get(x).id;
		}
		return x;
	}

	////////////////////////////////////////////////////////////////////////////

	/**
	 * The Class Element.
	 */
	public static class Element implements Comparable<Element> {
		
		/** The id. */
		public int id;
		
		/** The sz. */
		public int sz;

		@Override
		public int compareTo(final Element o) {
			return Integer.compare(id, o.id);
		}
	}

	// private static final Comparator<Element> elementComparator = (o1, o2) -> o1 == o2 ? 0 : o1.id < o2.id ? -1 : +1;

}
