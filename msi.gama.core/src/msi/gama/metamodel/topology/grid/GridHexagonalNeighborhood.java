/*******************************************************************************************************
 *
 * GridHexagonalNeighborhood.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.topology.grid;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The Class GridHexagonalNeighborhood.
 */
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
		final Set<Integer> neigh2 = new LinkedHashSet<>();
		final Set<Integer> neigh =
				getNeighborsAtRadius(placeIndex, radius, matrix.numCols, matrix.numRows, matrix.isTorus);
		final Iterator<Integer> it = neigh.iterator();
		while (it.hasNext()) {
			final int id = it.next();
			if (matrix.matrix[id] != null) { neigh2.add(id); }
		}
		return neigh2;
	}

	/**
	 * Gets the index at.
	 *
	 * @param originalX
	 *            the original X
	 * @param originalY
	 *            the original Y
	 * @param xSize
	 *            the x size
	 * @param ySize
	 *            the y size
	 * @param isTorus
	 *            the is torus
	 * @return the index at
	 */
	final int getIndexAt(final int originalX, final int originalY, final int xSize, final int ySize,
			final boolean isTorus) {
		int x = originalX;
		int y = originalY;
		if (x < 0 || y < 0 || x > xSize - 1 || y > ySize - 1) {
			if (!isTorus) return -1;
			if (x < 0) { x = xSize - 1; }
			if (y < 0) { y = ySize - 1; }
			if (x > xSize - 1) { x = 0; }
			if (y > ySize - 1) { y = 0; }
		}
		return y * xSize + x;
	}

	/**
	 * Gets the neighbors at radius.
	 *
	 * @param placeIndex
	 *            the place index
	 * @param radius
	 *            the radius
	 * @param xSize
	 *            the x size
	 * @param ySize
	 *            the y size
	 * @param isTorus
	 *            the is torus
	 * @return the neighbors at radius
	 */
	public Set<Integer> getNeighborsAtRadius(final int placeIndex, final int radius, final int xSize, final int ySize,
			final boolean isTorus) {
		// TODO: verify the use of HashSet here, contradictory with the policy of GAMA to not use unordered Sets or
		final Set<Integer> currentNeigh = new LinkedHashSet<>();
		currentNeigh.add(placeIndex);
		for (int i = 0; i < radius; i++) {
			final Set<Integer> newNeigh = new LinkedHashSet<>();
			final Iterator<Integer> it = currentNeigh.iterator();
			while (it.hasNext()) { newNeigh.addAll(getNeighborsAtRadius1(it.next(), xSize, ySize, isTorus)); }
			currentNeigh.addAll(newNeigh);
		}
		currentNeigh.remove(placeIndex);
		return currentNeigh;

	}

	/**
	 * Gets the neighbors at radius 1.
	 *
	 * @param placeIndex
	 *            the place index
	 * @param xSize
	 *            the x size
	 * @param ySize
	 *            the y size
	 * @param isTorus
	 *            the is torus
	 * @return the neighbors at radius 1
	 */
	public abstract Set<Integer> getNeighborsAtRadius1(final int placeIndex, final int xSize, final int ySize,
			final boolean isTorus);

	@Override
	public boolean isVN() { return false; }

	@Override
	public void clear() {}

}