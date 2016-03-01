/**
 *
 */
package msi.gama.metamodel.topology.grid;

import gnu.trove.set.hash.TIntHashSet;
import msi.gaml.operators.fastmaths.CmnFastMath;

/**
 * Written by drogoul Modified on 8 mars 2011
 *
 * @todo Description
 *
 */
public class GridVonNeumannNeighbourhood extends GridNeighbourhood {


	/**
	 * @param gamaSpatialMatrix
	 */
	GridVonNeumannNeighbourhood(final GamaSpatialMatrix matrix) {
		super(matrix);
	}

	@Override
	protected TIntHashSet getNeighboursAtRadius(final int placeIndex, final int radius) {
		final int y = placeIndex / matrix.numCols;
		final int x = placeIndex - y * matrix.numCols;

		final TIntHashSet v = new TIntHashSet(radius << 2);
		int p;
		for ( int i = -radius; i < radius; i++ ) {
			p = matrix.getPlaceIndexAt(x - i, y - CmnFastMath.abs(i) + radius);
			if ( p != -1 ) {
				v.add(p);
			}
			p = matrix.getPlaceIndexAt(x + i, y + CmnFastMath.abs(i) - radius);
			if ( p != -1 ) {
				v.add(p);
			}
		}
		return v;
	}

	@Override
	public boolean isVN() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.metamodel.topology.grid.INeighbourhood#clear()
	 */
	@Override
	public void clear() {}
}