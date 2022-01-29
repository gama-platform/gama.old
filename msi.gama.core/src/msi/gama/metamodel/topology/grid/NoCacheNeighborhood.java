/*******************************************************************************************************
 *
 * NoCacheNeighborhood.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.metamodel.topology.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.Collector;

/**
 * The Class NoCacheNeighborhood.
 */
public class NoCacheNeighborhood implements INeighborhood {

	/**
	 *
	 */
	private final GamaSpatialMatrix matrix;

	/**
	 * Instantiates a new no cache neighborhood.
	 *
	 * @param gamaSpatialMatrix the gama spatial matrix
	 */
	public NoCacheNeighborhood(final GamaSpatialMatrix gamaSpatialMatrix) {
		matrix = gamaSpatialMatrix;
	}

	@Override
	public void clear() {}

	/**
	 * Method getNeighborsIn()
	 *
	 * @see msi.gama.metamodel.topology.grid.INeighborhood#getNeighborsIn(int, int)
	 */
	@Override
	public Set<IAgent> getNeighborsIn(final IScope scope, final int placeIndex, final int radius) {
		return computeNeighborsFrom(scope, placeIndex, 1, radius);
	}

	/**
	 * Compute neighbors from.
	 *
	 * @param scope the scope
	 * @param placeIndex the place index
	 * @param begin the begin
	 * @param end the end
	 * @return the sets the
	 */
	private Set<IAgent> computeNeighborsFrom(final IScope scope, final int placeIndex, final int begin, final int end) {
		try (final Collector.AsOrderedSet<IAgent> result = Collector.getOrderedSet()) {
			for (int i = begin; i <= end; i++) {
				for (final Integer index : matrix.usesVN ? get4NeighborsAtRadius(placeIndex, i)
						: get8NeighborsAtRadius(placeIndex, i)) {
					result.add(matrix.matrix[index].getAgent());
				}
			}
			// Addresses Issue 1071 by explicitly shuffling the result
			result.shuffleInPlaceWith(scope.getRandom());
			return result.items();
		}
	}

	/**
	 * Gets the 8 neighbors at radius.
	 *
	 * @param placeIndex the place index
	 * @param radius the radius
	 * @return the 8 neighbors at radius
	 */
	protected List<Integer> get8NeighborsAtRadius(final int placeIndex, final int radius) {
		final int y = placeIndex / matrix.numCols;
		final int x = placeIndex - y * matrix.numCols;
		final List<Integer> v = new ArrayList<>(radius + 1 * radius + 1);
		int p;
		for (int i = 1 - radius; i < radius; i++) {
			p = matrix.getPlaceIndexAt(x + i, y - radius);
			if (p != -1) {
				v.add(p);
			}
			p = matrix.getPlaceIndexAt(x - i, y + radius);
			if (p != -1) {
				v.add(p);
			}
		}
		for (int i = -radius; i < radius + 1; i++) {
			p = matrix.getPlaceIndexAt(x - radius, y - i);
			if (p != -1) {
				v.add(p);
			}
			p = matrix.getPlaceIndexAt(x + radius, y + i);
			if (p != -1) {
				v.add(p);
			}
		}
		return v;
	}

	/**
	 * Gets the 4 neighbors at radius.
	 *
	 * @param placeIndex the place index
	 * @param radius the radius
	 * @return the 4 neighbors at radius
	 */
	protected List<Integer> get4NeighborsAtRadius(final int placeIndex, final int radius) {
		final int y = placeIndex / matrix.numCols;
		final int x = placeIndex - y * matrix.numCols;

		final List<Integer> v = new ArrayList<>(radius << 2);
		int p;
		for (int i = -radius; i < radius; i++) {
			p = matrix.getPlaceIndexAt(x - i, y - Math.abs(i) + radius);
			if (p != -1) {
				v.add(p);
			}
			p = matrix.getPlaceIndexAt(x + i, y + Math.abs(i) - radius);
			if (p != -1) {
				v.add(p);
			}
		}
		return v;
	}

	/**
	 * Method isVN()
	 *
	 * @see msi.gama.metamodel.topology.grid.INeighborhood#isVN()
	 */
	@Override
	public boolean isVN() {
		return false;
	}

	/**
	 * Method getRawNeighborsIncluding()
	 *
	 * @see msi.gama.metamodel.topology.grid.INeighborhood#getRawNeighborsIncluding(int, int)
	 */
	@Override
	public int[] getRawNeighborsIncluding(final IScope scope, final int placeIndex, final int range) {
		throw GamaRuntimeException.warning("The diffusion of signals must rely on a neighbors cache in the grid",
				scope);
	}

	/**
	 * Method neighborsIndexOf()
	 *
	 * @see msi.gama.metamodel.topology.grid.INeighborhood#neighborsIndexOf(int, int)
	 */
	@Override
	public int neighborsIndexOf(final IScope scope, final int placeIndex, final int n) {
		throw GamaRuntimeException.warning("The diffusion of signals must rely on a neighbors cache in the grid",
				scope);
	}

}