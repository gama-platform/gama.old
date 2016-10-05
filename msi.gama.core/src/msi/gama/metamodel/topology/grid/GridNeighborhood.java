/**
 *
 */
package msi.gama.metamodel.topology.grid;

import java.util.Set;

import gnu.trove.set.hash.TIntHashSet;
import gnu.trove.set.hash.TLinkedHashSet;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;

/**
 * Written by drogoul Modified on 8 mars 2011
 *
 * @todo Description
 *
 */
public abstract class GridNeighborhood implements INeighborhood {

	protected final GamaSpatialMatrix matrix;
	// i : index of agents; j : index of neighbors
	protected int[][] neighbors;
	// i : index of agents; j : index of the neighbors by distance
	protected int[][] neighborsIndexes;

	public GridNeighborhood(final GamaSpatialMatrix matrix) {
		this.matrix = matrix;
		neighbors = new int[matrix.matrix.length][0];
		// neighborsIndexes = new ArrayList[agents.length];
		neighborsIndexes = new int[matrix.matrix.length][];
	}

	@Override
	public int[] getRawNeighborsIncluding(final IScope scope, final int placeIndex, final int radius) {
		// List<Integer> n = neighborsIndexes[placeIndex];
		int[] n = neighborsIndexes[placeIndex];
		if (n == null) {
			// n = new ArrayList<Integer>();
			n = new int[0];
			neighborsIndexes[placeIndex] = n;
		}
		// final int size = n.size();
		final int size = n.length;
		if (radius > size) {
			computeNeighborsFrom(placeIndex, size + 1, radius);
		}
		return neighbors[placeIndex];
	}

	protected abstract TIntHashSet getNeighborsAtRadius(final int placeIndex, final int radius);

	private void computeNeighborsFrom(final int placeIndex, final int begin, final int end) {
		for (int i = begin; i <= end; i++) {
			// final int previousIndex = i == 1 ? 0 :
			// neighborsIndexes[placeIndex].get(i - 2);
			final int previousIndex = i == 1 ? 0 : neighborsIndexes[placeIndex][i - 2];
			final TIntHashSet list = getNeighborsAtRadius(placeIndex, i);
			final int[] listArray = list.toArray();
			final int size = listArray.length;
			// final int size = list.size();
			// final int[] listArray = new int[size];
			// for ( int j = 0; j < size; j++ ) {
			// listArray[j] = list.get(j);
			// }
			final int[] newArray = new int[neighbors[placeIndex].length + size];
			if (neighbors[placeIndex].length != 0) {
				java.lang.System.arraycopy(neighbors[placeIndex], 0, newArray, 0, neighbors[placeIndex].length);
			}
			java.lang.System.arraycopy(listArray, 0, newArray, neighbors[placeIndex].length, size);
			neighbors[placeIndex] = newArray;
			// neighborsIndexes[placeIndex].add(previousIndex + size);
			addToNeighborsIndex(placeIndex, previousIndex + size);
		}
	}

	private final void addToNeighborsIndex(final int placeIndex, final int newIndex) {
		final int[] previous = neighborsIndexes[placeIndex];
		final int[] newOne = new int[previous.length + 1];
		java.lang.System.arraycopy(previous, 0, newOne, 0, previous.length);
		newOne[previous.length] = newIndex;
		neighborsIndexes[placeIndex] = newOne;
	}

	@Override
	public int neighborsIndexOf(final IScope scope, final int placeIndex, final int n) {
		if (n == 1) {
			return 0;
		}
		final int size = neighborsIndexes[placeIndex].length;
		if (n > size) {
			return neighbors[placeIndex].length - 1;
		}
		return neighborsIndexes[placeIndex][n - 2];
	}

	@Override
	public Set<IAgent> getNeighborsIn(final IScope scope, final int placeIndex, final int radius) {
		int[] n = neighborsIndexes[placeIndex];
		if (n == null) {
			n = new int[0];
			neighborsIndexes[placeIndex] = n;
		}
		final int size = n.length;
		if (radius > size) {
			computeNeighborsFrom(placeIndex, size + 1, radius);
		}
		final int[] nn = neighbors[placeIndex];
		final int nnSize = neighborsIndexes[placeIndex][radius - 1];
		final Set<IAgent> result = new TLinkedHashSet<>();
		for (int i = 0; i < nnSize; i++) {
			result.add(matrix.matrix[nn[i]].getAgent());
		}
		scope.getRandom().shuffle2(result);
		return result;
	}

	@Override
	public abstract boolean isVN();

	@Override
	public void clear() {
		neighbors = null;
		neighborsIndexes = null;
	}

}