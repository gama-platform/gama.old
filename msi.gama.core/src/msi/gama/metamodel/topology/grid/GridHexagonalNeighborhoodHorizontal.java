/*******************************************************************************************************
 *
 * msi.gama.metamodel.topology.grid.GridHexagonalNeighborhoodHorizontal.java, in plugin msi.gama.core, is part of the
 * source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.topology.grid;

import java.util.HashSet;
import java.util.Set;

public class GridHexagonalNeighborhoodHorizontal extends GridHexagonalNeighborhood {

	/**
	 * @param gamaSpatialMatrix
	 */
	GridHexagonalNeighborhoodHorizontal(final GamaSpatialMatrix matrix) {
		super(matrix);
	}

	@Override
	public Set<Integer> getNeighborsAtRadius1(final int placeIndex, final int xSize, final int ySize,
			final boolean isTorus) {
		final int y = placeIndex / xSize;
		final int x = placeIndex - y * xSize;
		// TODO: verify the use of HashSet here, contradictory with the policy of GAMA to not use unordered Sets or Maps

		final Set<Integer> neigh = new HashSet<>();
		int id = getIndexAt(x, y - 1, xSize, ySize, isTorus);
		if (id != -1) {
			neigh.add(id);
		}
		id = getIndexAt(x, y + 1, xSize, ySize, isTorus);
		if (id != -1) {
			neigh.add(id);
		}
		id = getIndexAt(x - 1, y, xSize, ySize, isTorus);
		if (id != -1) {
			neigh.add(id);
		}
		id = getIndexAt(x + 1, y, xSize, ySize, isTorus);
		if (id != -1) {
			neigh.add(id);
		}
		if (x % 2 == 0) {
			id = getIndexAt(x + 1, y - 1, xSize, ySize, isTorus);
			if (id != -1) {
				neigh.add(id);
			}
			id = getIndexAt(x - 1, y - 1, xSize, ySize, isTorus);
			if (id != -1) {
				neigh.add(id);
			}
		} else {
			id = getIndexAt(x + 1, y + 1, xSize, ySize, isTorus);
			if (id != -1) {
				neigh.add(id);
			}
			id = getIndexAt(x - 1, y + 1, xSize, ySize, isTorus);
			if (id != -1) {
				neigh.add(id);
			}
		}
		return neigh;
	}

}