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

import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.hash.TIntHashSet;

public abstract class GridHexagonalNeighborhood extends GridNeighborhood {



	/**
	 * @param gamaSpatialMatrix
	 */
	GridHexagonalNeighborhood(final GamaSpatialMatrix matrix) {
		super(matrix);
	}

	@Override
	protected TIntHashSet getNeighborsAtRadius(final int placeIndex, final int radius) {
		final TIntHashSet neigh2 = new TIntHashSet();
		final TIntHashSet neigh = getNeighborsAtRadius(placeIndex, radius, matrix.numCols, matrix.numRows, matrix.isTorus);
		TIntIterator it = neigh.iterator();
		while (it.hasNext()) {
			int id = it.next();
			if ( matrix.matrix[id] != null ) {
				neigh2.add(id);
			}
		}
		return neigh2;
	}

	final int getIndexAt(int x, int y, final int xSize, final int ySize, final boolean isTorus) {
		if ( x < 0 || y < 0 || x > xSize - 1 || y > ySize - 1 ) {
			if ( !isTorus ) { return -1; }
			if ( x < 0 ) {
				x = xSize - 1;
			}
			if ( y < 0 ) {
				y = ySize - 1;
			}
			if ( x > xSize - 1 ) {
				x = 0;
			}
			if ( y > ySize - 1 ) {
				y = 0;
			}
		}
		return y * xSize + x;
	}

	public TIntHashSet getNeighborsAtRadius(final int placeIndex, final int radius, final int xSize,
		final int ySize, final boolean isTorus) {
		final TIntHashSet currentNeigh = new TIntHashSet();
		currentNeigh.add(placeIndex);
		for ( int i = 0; i < radius; i++ ) {
			final TIntHashSet newNeigh = new TIntHashSet();
			TIntIterator it = currentNeigh.iterator();
			while (it.hasNext()) {
				newNeigh.addAll(getNeighborsAtRadius1(it.next(), xSize, ySize, isTorus));
			}
			currentNeigh.addAll(newNeigh);
		}
		currentNeigh.remove(placeIndex);
		return currentNeigh;

	}
	
	public abstract TIntHashSet getNeighborsAtRadius1(final int placeIndex, final int xSize, final int ySize,
		final boolean isTorus);
	
	@Override
	public boolean isVN() {
		return false;
	}

	@Override
	public void clear() {}

}