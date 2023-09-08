/*******************************************************************************************************
 *
 * GridVonNeumannNeighborhood.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.topology.grid;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Written by drogoul Modified on 8 mars 2011
 *
 * @todo Description
 *
 */
public class GridVonNeumannNeighborhood extends GridNeighborhood {

	/**
	 * @param gamaSpatialMatrix
	 */
	GridVonNeumannNeighborhood(final GamaSpatialMatrix matrix) {
		super(matrix);
	}

	@Override
	protected Set<Integer> getNeighborsAtRadius(final int placeIndex, final int radius) {
		final int y = placeIndex / matrix.numCols;
		final int x = placeIndex - y * matrix.numCols;
		final Set<Integer> v = new LinkedHashSet<>(radius << 2);
		int p;
		for (int i = -radius; i < radius; i++) {
			p = matrix.getPlaceIndexAt(x - i, y - Math.abs(i) + radius);
			if (p != -1) { v.add(p); }
			p = matrix.getPlaceIndexAt(x + i, y + Math.abs(i) - radius);
			if (p != -1) { v.add(p); }
		}
		return v;
	}

	@Override
	public boolean isVN() { return true; }

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.metamodel.topology.grid.INeighborhood#clear()
	 */
	@Override
	public void clear() {}
}