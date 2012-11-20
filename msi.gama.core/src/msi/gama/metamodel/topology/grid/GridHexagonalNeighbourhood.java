package msi.gama.metamodel.topology.grid;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaList;

public class GridHexagonalNeighbourhood extends GridNeighbourhood {
	
	public GridHexagonalNeighbourhood(final IShape[] agents, final int xSize, final int ySize ,final boolean isTorus) {
		super(agents, xSize, ySize , isTorus);
	}
	
	final static int getIndexAt(final int x, final int y, int xSize) {
		return y * xSize + x;
	}
	
	public static List<Integer> getNeighboursAtRadius(int placeIndex, int radius, int xSize,int  ySize) {
		Set<Integer> currentNeigh = new HashSet<Integer>();
		currentNeigh.add(placeIndex);
		for (int i = 0; i < radius; i++) {
			Set<Integer> newNeigh = new HashSet<Integer>();
			for (Integer id :currentNeigh ) {
				newNeigh.addAll(getNeighboursAtRadius1(id,  xSize,  ySize));
			}
			currentNeigh.addAll(newNeigh);
		}
		currentNeigh.remove(new Integer(placeIndex));
		
		return new GamaList<Integer>(currentNeigh);
		
	}
	
	public static List<Integer> getNeighboursAtRadius1(int placeIndex, int xSize,int  ySize) {
		int y = placeIndex / xSize;
		int x = placeIndex - (y * xSize);
		//yy * numCols + xx;
		List<Integer> neigh = new GamaList<Integer>();
		if (y > 0) 
			neigh.add(getIndexAt(x, y-1, xSize));
		if (y < ySize-1) 
			neigh.add(getIndexAt(x, y+1, xSize));
		if (x > 0) 
			neigh.add(getIndexAt(x-1, y, xSize));
		if (x < xSize-1 ) 
			neigh.add(getIndexAt(x+1, y, xSize));
		if (x % 2 == 0) {
			if (x < xSize-1 &&  y > 0) 
				neigh.add(getIndexAt(x+1, y-1, xSize));
			if (x > 0 && y > 0) 
				neigh.add(getIndexAt(x-1, y-1, xSize));
		} else {
			if (x < xSize -1 &&  y < ySize -1) 
				neigh.add(getIndexAt(x+1, y+1, xSize));
			if (x > 0 && y  < ySize-1) 
				neigh.add(getIndexAt(x-1, y+1, xSize));
		}
		return neigh;
		
	}
	
	@Override
	protected List<Integer> getNeighboursAtRadius(int placeIndex, int radius) {
		List<Integer> neigh2 = new GamaList<Integer>();
		List<Integer> neigh =  getNeighboursAtRadius(placeIndex, radius, xSize, ySize);
		for (Integer id : neigh) {
			if (agents[id] != null)  {
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