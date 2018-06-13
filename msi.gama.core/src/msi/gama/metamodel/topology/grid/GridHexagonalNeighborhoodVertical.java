/*********************************************************************************************
 *
 * 'GridHexagonalNeighborhood.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.metamodel.topology.grid;

import gnu.trove.set.hash.TIntHashSet;

public class GridHexagonalNeighborhoodVertical extends GridHexagonalNeighborhood {



	/**
	 * @param gamaSpatialMatrix
	 */
	GridHexagonalNeighborhoodVertical(final GamaSpatialMatrix matrix) {
		super(matrix);
	}

	public TIntHashSet getNeighborsAtRadius1(final int placeIndex, final int xSize, final int ySize,
		final boolean isTorus) {
		final int y = placeIndex / xSize;
		final int x = placeIndex - y * xSize;
		final TIntHashSet neigh = new TIntHashSet();
		int id = getIndexAt(x, y - 1, xSize, ySize, isTorus);
		if ( id != -1 ) {
			neigh.add(id);
		}
		id = getIndexAt(x, y + 1, xSize, ySize, isTorus);
		if ( id != -1 ) {
			neigh.add(id);
		}
		id = getIndexAt(x - 1, y, xSize, ySize, isTorus);
		if ( id != -1 ) {
			neigh.add(id);
		}
		id = getIndexAt(x + 1, y, xSize, ySize, isTorus);
		if ( id != -1 ) {
			neigh.add(id);
		}
		if ( y % 2 == 1 ) {
			id = getIndexAt(x + 1, y - 1, xSize, ySize, isTorus);
			if ( id != -1 ) {
				neigh.add(id);
			}
			id = getIndexAt(x + 1, y + 1, xSize, ySize, isTorus);
			if ( id != -1 ) {
				neigh.add(id);
			}
		} else {
			id = getIndexAt(x - 1, y - 1, xSize, ySize, isTorus);
			if ( id != -1 ) {
				neigh.add(id);
			}
			id = getIndexAt(x - 1, y + 1, xSize, ySize, isTorus);
			if ( id != -1 ) {
				neigh.add(id);
			}
		}
		return neigh;
	}

	

}