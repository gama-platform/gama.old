/*******************************************************************************************************
 *
 * msi.gama.metamodel.topology.grid.GridHexagonalNeighborhood.java, in plugin msi.gama.core, is part of the source code
 * of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.topology.grid;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class GridHexagonalNeighborhood extends GridNeighborhood {

	/**
	 * @param gamaSpatialMatrix
	 */
	GridHexagonalNeighborhood(final GamaSpatialMatrix matrix) {
		super(matrix);
	}

	@Override
	protected Set<Integer> getNeighborsAtRadius(final int placeIndex, final int radius) {
		// TODO: verify the use of HashSet here, contradictory with the policy of GAMA to not use unordered Sets or
		// Maps.
		final Set<Integer> neigh2 = new HashSet<>();
		final Set<Integer> neigh =
				getNeighborsAtRadius(placeIndex, radius, matrix.numCols, matrix.numRows, matrix.isTorus);
		final Iterator<Integer> it = neigh.iterator();
		while (it.hasNext()) {
			final int id = it.next();
			if (matrix.matrix[id] != null) {
				neigh2.add(id);
			}
		}
		return neigh2;
	}

	final int getIndexAt(final int originalX, final int originalY, final int xSize, final int ySize,
			final boolean isTorus) {
		int x = originalX;
		int y = originalY;
		if (x < 0 || y < 0 || x > xSize - 1 || y > ySize - 1) {
			if (!isTorus) { return -1; }
			if (x < 0) {
				x = xSize - 1;
			}
			if (y < 0) {
				y = ySize - 1;
			}
			if (x > xSize - 1) {
				x = 0;
			}
			if (y > ySize - 1) {
				y = 0;
			}
		}
		return y * xSize + x;
	}

	public Set<Integer> getNeighborsAtRadius(final int placeIndex, final int radius, final int xSize, final int ySize,
			final boolean isTorus) {
		// TODO: verify the use of HashSet here, contradictory with the policy of GAMA to not use unordered Sets or
		final Set<Integer> currentNeigh = new HashSet<>();
		currentNeigh.add(placeIndex);
		for (int i = 0; i < radius; i++) {
			final Set<Integer> newNeigh = new HashSet<>();
			final Iterator<Integer> it = currentNeigh.iterator();
			while (it.hasNext()) {
				newNeigh.addAll(getNeighborsAtRadius1(it.next(), xSize, ySize, isTorus));
			}
			currentNeigh.addAll(newNeigh);
		}
		currentNeigh.remove(placeIndex);
		return currentNeigh;

	}

	public abstract Set<Integer> getNeighborsAtRadius1(final int placeIndex, final int xSize, final int ySize,
			final boolean isTorus);

	@Override
	public boolean isVN() {
		return false;
	}

	@Override
	public void clear() {}

}