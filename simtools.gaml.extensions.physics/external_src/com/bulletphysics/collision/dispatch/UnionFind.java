/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * Bullet Continuous Collision Detection and Physics Library Copyright (c) 2003-2008 Erwin Coumans
 * http://www.bulletphysics.com/
 *
 * This software is provided 'as-is', without any express or implied warranty. In no event will the authors be held
 * liable for any damages arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose, including commercial applications, and to alter
 * it and redistribute it freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not claim that you wrote the original software.
 * If you use this software in a product, an acknowledgment in the product documentation would be appreciated but is not
 * required. 2. Altered source versions must be plainly marked as such, and must not be misrepresented as being the
 * original software. 3. This notice may not be removed or altered from any source distribution.
 */

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

	public void reset(final int N) {
		allocate(N);

		for (int i = 0; i < N; i++) {
			elements.get(i).id = i;
			elements.get(i).sz = 1;
		}
	}

	public int getNumElements() {
		return elements.size();
	}

	public boolean isRoot(final int x) {
		return x == elements.get(x).id;
	}

	public Element getElement(final int index) {
		return elements.get(index);
	}

	public void allocate(final int N) {
		MiscUtil.resize(elements, N, Element.class);
	}

	public void free() {
		elements.clear();
	}

	public int find(final int p, final int q) {
		return find(p) == find(q) ? 1 : 0;
	}

	public void unite(final int p, final int q) {
		int i = find(p), j = find(q);
		if (i == j) return;
		elements.get(i).id = j;
		elements.get(j).sz += elements.get(i).sz;
	}

	public int find(int x) {
		while (x != elements.get(x).id) {
			elements.get(x).id = elements.get(elements.get(x).id).id;
			x = elements.get(x).id;
		}
		return x;
	}

	////////////////////////////////////////////////////////////////////////////

	public static class Element implements Comparable<Element> {
		public int id;
		public int sz;

		@Override
		public int compareTo(final Element o) {
			return Integer.compare(id, o.id);
		}
	}

	// private static final Comparator<Element> elementComparator = (o1, o2) -> o1 == o2 ? 0 : o1.id < o2.id ? -1 : +1;

}
