/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.metamodel.topology.grid;

import java.util.List;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaList;

/**
 * Written by drogoul Modified on 8 mars 2011
 * 
 * @todo Description
 * 
 */
public abstract class GridNeighbourhood {

	IShape[] agents;
	int xSize, ySize;
	boolean isTorus;

	protected int[][] neighbours;
	// i : index of agents; j : index of neighbours
	// protected List<Integer>[] neighboursIndexes;
	protected int[][] neighboursIndexes;

	// i : index of agents; j : index of the neighbours by distance

	public GridNeighbourhood(final IShape[] agents, final int xSize, final int ySize, final boolean isTorus) {
		this.agents = agents;
		this.xSize = xSize;
		this.ySize = ySize;
		neighbours = new int[agents.length][0];
		// neighboursIndexes = new ArrayList[agents.length];
		neighboursIndexes = new int[agents.length][];
		this.isTorus = isTorus;
	}

	int getPlaceIndexAt(final int xx, final int yy) {
		if ( isTorus ) { return (yy < 0 ? yy + xSize : yy) % ySize * xSize + (xx < 0 ? xx + xSize : xx) % xSize; }
		if ( xx < 0 || xx >= xSize || yy < 0 || yy >= ySize ) { return -1; }
		return yy * xSize + xx;
	}

	int[] getRawNeighboursIncluding(final int placeIndex, final int radius) {
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

	protected abstract List<Integer> getNeighboursAtRadius(final int placeIndex, final int radius);

	private void computeNeighboursFrom(final int placeIndex, final int begin, final int end) {
		for ( int i = begin; i <= end; i++ ) {
			// final int previousIndex = i == 1 ? 0 : neighboursIndexes[placeIndex].get(i - 2);
			final int previousIndex = i == 1 ? 0 : neighboursIndexes[placeIndex][i - 2];
			final List<Integer> list = getNeighboursAtRadius(placeIndex, i);
			final int size = list.size();
			int[] listArray = new int[size];
			for ( int j = 0; j < size; j++ ) {
				listArray[j] = list.get(j);
			}
			final int[] newArray = new int[neighbours[placeIndex].length + size];
			if ( neighbours[placeIndex].length != 0 ) {
				System.arraycopy(neighbours[placeIndex], 0, newArray, 0,
					neighbours[placeIndex].length);
			}
			System.arraycopy(listArray, 0, newArray, neighbours[placeIndex].length, size);
			neighbours[placeIndex] = newArray;
			// neighboursIndexes[placeIndex].add(previousIndex + size);
			addToNeighboursIndex(placeIndex, previousIndex + size);
		}
	}

	private final void addToNeighboursIndex(final int placeIndex, final int newIndex) {
		int[] previous = neighboursIndexes[placeIndex];
		int[] newOne = new int[previous.length + 1];
		System.arraycopy(previous, 0, newOne, 0, previous.length);
		newOne[previous.length] = newIndex;
		neighboursIndexes[placeIndex] = newOne;
	}

	int neighboursIndexOf(final int placeIndex, final int n) {
		if ( n == 1 ) { return 0; }
		// final int size = neighboursIndexes[placeIndex].size();
		final int size = neighboursIndexes[placeIndex].length;
		if ( n > size ) { return neighbours[placeIndex].length - 1; }
		// return neighboursIndexes[placeIndex].get(n - 2);
		return neighboursIndexes[placeIndex][n - 2];
	}

	public GamaList<IAgent> getNeighboursIn(final int placeIndex, final int radius) {
		// List<Integer> n = neighboursIndexes[placeIndex];
		int[] n = neighboursIndexes[placeIndex];
		if ( n == null ) {
			// n = new ArrayList<Integer>();
			n = new int[0];
			neighboursIndexes[placeIndex] = n;
		}
		final int size = n.length;
		if ( radius > size ) {
			computeNeighboursFrom(placeIndex, size + 1, radius);
		}
		int[] nn = neighbours[placeIndex];
		int nnSize = neighboursIndexes[placeIndex][radius - 1];
		final GamaList<IAgent> result = new GamaList(nnSize);
		for ( int i = 0; i < nnSize; i++ ) {
			result.add(agents[nn[i]].getAgent());
		}
		return result;
	}

	/**
	 * @return
	 */
	public abstract boolean isVN();

}
