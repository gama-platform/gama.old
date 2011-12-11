/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.environment;

import java.util.List;
import msi.gama.interfaces.*;
import msi.gama.util.GamaList;

/**
 * Written by drogoul Modified on 8 mars 2011
 * 
 * @todo Description
 * 
 */
public abstract class GridNeighbourhood {

	IGeometry[] agents;
	int xSize, ySize;
	// boolean isTorus;

	protected int[][] neighbours;
	// i : index of agents; j : index of neighbours
	// protected List<Integer>[] neighboursIndexes;
	protected int[][] neighboursIndexes;

	// i : index of agents; j : index of the neighbours by distance

	public GridNeighbourhood(final IGeometry[] agents, final int xSize, final int ySize/*
																						 * ,
																						 * final
																						 * boolean
																						 * isTorus
																						 */) {
		this.agents = agents;
		this.xSize = xSize;
		this.ySize = ySize;
		neighbours = new int[agents.length][0];
		// neighboursIndexes = new ArrayList[agents.length];
		neighboursIndexes = new int[agents.length][];
		// this.isTorus = isTorus;
	}

	final int getPlaceIndexAt(final int xx, final int yy) {
		// if ( isTorus ) { return (yy < 0 ? yy + xSize : yy) % ySize * xSize +
		// (xx < 0 ? xx + xSize : xx) % xSize; }
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
