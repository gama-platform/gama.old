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

public class NoCacheNeighbourhood implements INeighbourhood {

	/**
	 * 
	 */
	private final GamaSpatialMatrix matrix;

	public NoCacheNeighbourhood(GamaSpatialMatrix gamaSpatialMatrix) {
		matrix = gamaSpatialMatrix;}

	@Override
	public void clear() {}

	/**
	 * Method getNeighboursIn()
	 * @see msi.gama.metamodel.topology.grid.INeighbourhood#getNeighboursIn(int, int)
	 */
	@Override
	public Set<IAgent> getNeighboursIn(final IScope scope, final int placeIndex, final int radius) {
		return computeNeighboursFrom(scope, placeIndex, 1, radius);
	}

	private Set<IAgent> computeNeighboursFrom(final IScope scope, final int placeIndex, final int begin,
		final int end) {
		Set<IAgent> result = new TLinkedHashSet();
		for ( int i = begin; i <= end; i++ ) {
			for ( Integer index : matrix.usesVN ? get4NeighboursAtRadius(placeIndex, i)
				: get8NeighboursAtRadius(placeIndex, i) ) {
				result.add(matrix.matrix[index].getAgent());
			}
		}
		// Addresses Issue 1071 by explicitly shuffling the result
		scope.getRandom().shuffle2(result);
		return result;
	}

	protected List<Integer> get8NeighboursAtRadius(final int placeIndex, final int radius) {
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

	protected List<Integer> get4NeighboursAtRadius(final int placeIndex, final int radius) {
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
	 * @see msi.gama.metamodel.topology.grid.INeighbourhood#isVN()
	 */
	@Override
	public boolean isVN() {
		return false;
	}

	/**
	 * Method getRawNeighboursIncluding()
	 * @see msi.gama.metamodel.topology.grid.INeighbourhood#getRawNeighboursIncluding(int, int)
	 */
	@Override
	public int[] getRawNeighboursIncluding(final IScope scope, final int placeIndex, final int range) {
		throw GamaRuntimeException.warning("The diffusion of signals must rely on a neighbours cache in the grid",
			scope);
	}

	/**
	 * Method neighboursIndexOf()
	 * @see msi.gama.metamodel.topology.grid.INeighbourhood#neighboursIndexOf(int, int)
	 */
	@Override
	public int neighboursIndexOf(final IScope scope, final int placeIndex, final int n) {
		throw GamaRuntimeException.warning("The diffusion of signals must rely on a neighbours cache in the grid",
			scope);
	}

}