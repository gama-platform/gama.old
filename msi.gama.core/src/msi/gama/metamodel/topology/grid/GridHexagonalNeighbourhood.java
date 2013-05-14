package msi.gama.metamodel.topology.grid;

import java.util.*;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaList;

public class GridHexagonalNeighbourhood extends GridNeighbourhood {

	public GridHexagonalNeighbourhood(final IShape[] agents, final int xSize, final int ySize, final boolean isTorus) {
		super(agents, xSize, ySize, isTorus);
	}

	final static int getIndexAt(int x, int y, final int xSize, final int ySize, final boolean isTorus) {

		if ( x < 0 || y < 0 || x > xSize - 1 || y > ySize - 1 ) {
			if ( !isTorus ) { return -1; }
			if ( x < 0 ) {
				x = xSize - 1;
			}
			if ( y < 0 ) {
				y = ySize - 1;
			}
			if ( x > xSize - 1 ) {
				x = 0;
			}
			if ( y > ySize - 1 ) {
				y = 0;
			}

		}

		return y * xSize + x;
	}

	public static List<Integer> getNeighboursAtRadius(final int placeIndex, final int radius, final int xSize,
		final int ySize, final boolean isTorus) {
		final Set<Integer> currentNeigh = new HashSet<Integer>();
		currentNeigh.add(placeIndex);
		for ( int i = 0; i < radius; i++ ) {
			final Set<Integer> newNeigh = new HashSet<Integer>();
			for ( final Integer id : currentNeigh ) {
				newNeigh.addAll(getNeighboursAtRadius1(id, xSize, ySize, isTorus));
			}
			currentNeigh.addAll(newNeigh);
		}
		currentNeigh.remove(new Integer(placeIndex));

		return new GamaList<Integer>(currentNeigh);

	}

	public static List<Integer> getNeighboursAtRadius1(final int placeIndex, final int xSize, final int ySize,
		final boolean isTorus) {
		final int y = placeIndex / xSize;
		final int x = placeIndex - y * xSize;
		final List<Integer> neigh = new GamaList<Integer>();
		int id = getIndexAt(x, y - 1, xSize, ySize, isTorus);
		if ( id != -1 ) {
			neigh.add(id);
		}
		id = getIndexAt(x, y + 1, xSize, ySize, isTorus);
		if ( id != -1 ) {
			neigh.add(id);
		}
		id = getIndexAt(x - 1, y, xSize, ySize, isTorus);
		if ( id != -1 ) {
			neigh.add(id);
		}
		id = getIndexAt(x + 1, y, xSize, ySize, isTorus);
		if ( id != -1 ) {
			neigh.add(id);
		}

		if ( x % 2 == 0 ) {
			id = getIndexAt(x + 1, y - 1, xSize, ySize, isTorus);
			if ( id != -1 ) {
				neigh.add(id);
			}
			id = getIndexAt(x - 1, y - 1, xSize, ySize, isTorus);
			if ( id != -1 ) {
				neigh.add(id);
			}
		} else {
			id = getIndexAt(x + 1, y + 1, xSize, ySize, isTorus);
			if ( id != -1 ) {
				neigh.add(id);
			}
			id = getIndexAt(x - 1, y + 1, xSize, ySize, isTorus);
			if ( id != -1 ) {
				neigh.add(id);
			}
		}

		return neigh;

	}

	@Override
	protected List<Integer> getNeighboursAtRadius(final int placeIndex, final int radius) {
		final List<Integer> neigh2 = new GamaList<Integer>();
		final List<Integer> neigh = getNeighboursAtRadius(placeIndex, radius, xSize, ySize, isTorus);
		for ( final Integer id : neigh ) {
			if ( agents[id] != null ) {
				neigh2.add(id);
			}
		}
		return neigh2;
	}

	@Override
	public boolean isVN() {
		return false;
	}

}