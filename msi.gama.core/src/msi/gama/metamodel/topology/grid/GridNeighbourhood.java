/**
 *
 */
package msi.gama.metamodel.topology.grid;

import java.util.Set;
import gnu.trove.set.hash.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;

/**
 * Written by drogoul Modified on 8 mars 2011
 *
 * @todo Description
 *
 */
public abstract class GridNeighbourhood implements INeighbourhood {

	protected final GamaSpatialMatrix matrix;
	// i : index of agents; j : index of neighbours
	protected int[][] neighbours;
	// i : index of agents; j : index of the neighbours by distance
	protected int[][] neighboursIndexes;

	public GridNeighbourhood(final GamaSpatialMatrix matrix) {
		this.matrix = matrix;
		neighbours = new int[matrix.matrix.length][0];
		// neighboursIndexes = new ArrayList[agents.length];
		neighboursIndexes = new int[matrix.matrix.length][];
	}

	@Override
	public int[] getRawNeighboursIncluding(final IScope scope, final int placeIndex, final int radius) {
		// List<Integer> n = neighboursIndexes[placeIndex];
		int[] n = neighboursIndexes[placeIndex];
		if ( n == null ) {
			// n = new ArrayList<Integer>();
			n = new int[0];
			neighboursIndexes[placeIndex] = n;
		}
		// final int size = n.size();
		final int size = n.length;
		if ( radius > size ) {
			computeNeighboursFrom(placeIndex, size + 1, radius);
		}
		return neighbours[placeIndex];
	}

	protected abstract TIntHashSet getNeighboursAtRadius(final int placeIndex, final int radius);

	private void computeNeighboursFrom(final int placeIndex, final int begin, final int end) {
		for ( int i = begin; i <= end; i++ ) {
			// final int previousIndex = i == 1 ? 0 : neighboursIndexes[placeIndex].get(i - 2);
			final int previousIndex = i == 1 ? 0 : neighboursIndexes[placeIndex][i - 2];
			final TIntHashSet list = getNeighboursAtRadius(placeIndex, i);
			int[] listArray = list.toArray();
			int size = listArray.length;
			// final int size = list.size();
			// final int[] listArray = new int[size];
			// for ( int j = 0; j < size; j++ ) {
			// listArray[j] = list.get(j);
			// }
			final int[] newArray = new int[neighbours[placeIndex].length + size];
			if ( neighbours[placeIndex].length != 0 ) {
				java.lang.System.arraycopy(neighbours[placeIndex], 0, newArray, 0, neighbours[placeIndex].length);
			}
			java.lang.System.arraycopy(listArray, 0, newArray, neighbours[placeIndex].length, size);
			neighbours[placeIndex] = newArray;
			// neighboursIndexes[placeIndex].add(previousIndex + size);
			addToNeighboursIndex(placeIndex, previousIndex + size);
		}
	}

	private final void addToNeighboursIndex(final int placeIndex, final int newIndex) {
		final int[] previous = neighboursIndexes[placeIndex];
		final int[] newOne = new int[previous.length + 1];
		java.lang.System.arraycopy(previous, 0, newOne, 0, previous.length);
		newOne[previous.length] = newIndex;
		neighboursIndexes[placeIndex] = newOne;
	}

	@Override
	public int neighboursIndexOf(final IScope scope, final int placeIndex, final int n) {
		if ( n == 1 ) { return 0; }
		final int size = neighboursIndexes[placeIndex].length;
		if ( n > size ) { return neighbours[placeIndex].length - 1; }
		return neighboursIndexes[placeIndex][n - 2];
	}

	@Override
	public Set<IAgent> getNeighboursIn(final IScope scope, final int placeIndex, final int radius) {
		int[] n = neighboursIndexes[placeIndex];
		if ( n == null ) {
			n = new int[0];
			neighboursIndexes[placeIndex] = n;
		}
		final int size = n.length;
		if ( radius > size ) {
			computeNeighboursFrom(placeIndex, size + 1, radius);
		}
		final int[] nn = neighbours[placeIndex];
		final int nnSize = neighboursIndexes[placeIndex][radius - 1];
		final Set<IAgent> result = new TLinkedHashSet();
		for ( int i = 0; i < nnSize; i++ ) {
			result.add(matrix.matrix[nn[i]].getAgent());
		}
		scope.getRandom().shuffle2(result);
		return result;
	}

	@Override
	public abstract boolean isVN();

	@Override
	public void clear() {
		neighbours = null;
		neighboursIndexes = null;
	}

}