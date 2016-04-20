/**
 * 
 */
package msi.gama.metamodel.topology.grid;

import java.util.*;
import gnu.trove.set.hash.TLinkedHashSet;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.fastmaths.CmnFastMath;

public class NoCacheNeighborhood implements INeighborhood {

	/**
	 * 
	 */
	private final GamaSpatialMatrix matrix;

	public NoCacheNeighborhood(GamaSpatialMatrix gamaSpatialMatrix) {
		matrix = gamaSpatialMatrix;}

	@Override
	public void clear() {}

	/**
	 * Method getNeighborsIn()
	 * @see msi.gama.metamodel.topology.grid.INeighborhood#getNeighborsIn(int, int)
	 */
	@Override
	public Set<IAgent> getNeighborsIn(final IScope scope, final int placeIndex, final int radius) {
		return computeNeighborsFrom(scope, placeIndex, 1, radius);
	}

	private Set<IAgent> computeNeighborsFrom(final IScope scope, final int placeIndex, final int begin,
		final int end) {
		Set<IAgent> result = new TLinkedHashSet();
		for ( int i = begin; i <= end; i++ ) {
			for ( Integer index : matrix.usesVN ? get4NeighborsAtRadius(placeIndex, i)
				: get8NeighborsAtRadius(placeIndex, i) ) {
				result.add(matrix.matrix[index].getAgent());
			}
		}
		// Addresses Issue 1071 by explicitly shuffling the result
		scope.getRandom().shuffle2(result);
		return result;
	}

	protected List<Integer> get8NeighborsAtRadius(final int placeIndex, final int radius) {
		final int y = placeIndex / matrix.numCols;
		final int x = placeIndex - y * matrix.numCols;
		final List<Integer> v = new ArrayList<Integer>(radius + 1 * radius + 1);
		int p;
		for ( int i = 1 - radius; i < radius; i++ ) {
			p = matrix.getPlaceIndexAt(x + i, y - radius);
			if ( p != -1 ) {
				v.add(p);
			}
			p = matrix.getPlaceIndexAt(x - i, y + radius);
			if ( p != -1 ) {
				v.add(p);
			}
		}
		for ( int i = -radius; i < radius + 1; i++ ) {
			p = matrix.getPlaceIndexAt(x - radius, y - i);
			if ( p != -1 ) {
				v.add(p);
			}
			p = matrix.getPlaceIndexAt(x + radius, y + i);
			if ( p != -1 ) {
				v.add(p);
			}
		}
		return v;
	}

	protected List<Integer> get4NeighborsAtRadius(final int placeIndex, final int radius) {
		final int y = placeIndex / matrix.numCols;
		final int x = placeIndex - y * matrix.numCols;

		final List<Integer> v = new ArrayList<Integer>(radius << 2);
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

	/**
	 * Method isVN()
	 * @see msi.gama.metamodel.topology.grid.INeighborhood#isVN()
	 */
	@Override
	public boolean isVN() {
		return false;
	}

	/**
	 * Method getRawNeighborsIncluding()
	 * @see msi.gama.metamodel.topology.grid.INeighborhood#getRawNeighborsIncluding(int, int)
	 */
	@Override
	public int[] getRawNeighborsIncluding(final IScope scope, final int placeIndex, final int range) {
		throw GamaRuntimeException.warning("The diffusion of signals must rely on a neighbors cache in the grid",
			scope);
	}

	/**
	 * Method neighborsIndexOf()
	 * @see msi.gama.metamodel.topology.grid.INeighborhood#neighborsIndexOf(int, int)
	 */
	@Override
	public int neighborsIndexOf(final IScope scope, final int placeIndex, final int n) {
		throw GamaRuntimeException.warning("The diffusion of signals must rely on a neighbors cache in the grid",
			scope);
	}

}