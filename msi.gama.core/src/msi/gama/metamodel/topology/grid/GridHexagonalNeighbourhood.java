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
	
	final static int getIndexAt( int x,  int y, int xSize,int ySize, boolean isTorus) {
		
		if ((x < 0) || (y < 0) || (x > xSize-1) || (y > ySize -1)) {
			if (!isTorus)
				return -1;
			if (x < 0)
				x = xSize -1;
			if (y < 0)
				y = ySize -1;
			if (x > xSize-1)
				x = 0;
			if (y > ySize -1)
				y = 0;
			
		}
	
		return y * xSize + x;
	}
	
	public static List<Integer> getNeighboursAtRadius(int placeIndex, int radius, int xSize,int  ySize, boolean isTorus) {
		Set<Integer> currentNeigh = new HashSet<Integer>();
		currentNeigh.add(placeIndex);
		for (int i = 0; i < radius; i++) {
			Set<Integer> newNeigh = new HashSet<Integer>();
			for (Integer id :currentNeigh ) {
				newNeigh.addAll(getNeighboursAtRadius1(id,  xSize,  ySize,isTorus));
			}
			currentNeigh.addAll(newNeigh);
		}
		currentNeigh.remove(new Integer(placeIndex));
		
		return new GamaList<Integer>(currentNeigh);
		
	}
	
	public static List<Integer> getNeighboursAtRadius1(int placeIndex, int xSize,int  ySize, boolean isTorus) {
		int y = placeIndex / xSize;
		int x = placeIndex - (y * xSize);
		List<Integer> neigh = new GamaList<Integer>(); 
		int id = getIndexAt(x, y-1, xSize, ySize,isTorus);
		if (id != -1)
			neigh.add(id);
		id = getIndexAt(x, y+1, xSize, ySize, isTorus);
		if (id != -1)
			neigh.add(id);
		id = getIndexAt(x-1, y, xSize, ySize, isTorus);
		if (id != -1)
			neigh.add(id);
		id = getIndexAt(x+1, y, xSize, ySize, isTorus);
		if (id != -1)
			neigh.add(id);
		
		if (x % 2 == 0) {
			id = getIndexAt(x+1, y-1, xSize, ySize, isTorus);
			if (id != -1)
				neigh.add(id);
			id = getIndexAt(x-1, y-1, xSize, ySize, isTorus);
			if (id != -1)
				neigh.add(id);
		} else {
			id = getIndexAt(x+1, y+1, xSize, ySize, isTorus);
			if (id != -1)
				neigh.add(id);
			id = getIndexAt(x-1, y+1, xSize, ySize, isTorus);
			if (id != -1)
				neigh.add(id);
		}
		
		return neigh;
		
	}
	
	public static List<Integer> getNeighboursAtRadius1old(int placeIndex, int xSize,int  ySize, boolean isTorus) {
		int y = placeIndex / xSize;
		int x = placeIndex - (y * xSize);
		List<Integer> neigh = new GamaList<Integer>(); 
		if (x%2 == 1)
			ySize--;
	/*	if (y > 0) 
			neigh.add(getIndexAt(x, y-1, xSize));
		//else if (isTorus) neigh.add(getIndexAt(x, ySize-1, xSize));
		if (y < ySize-1) 
			neigh.add(getIndexAt(x, y+1, xSize));
		//else if (isTorus) neigh.add(getIndexAt(x, 0, xSize));
		if (x > 0) 
			neigh.add(getIndexAt(x-1, y, xSize));
		//else if (isTorus) neigh.add(getIndexAt(xSize-1, y, xSize));
		if (x < xSize-1 ) 
			neigh.add(getIndexAt(x+1, y, xSize));
		//else if (isTorus) neigh.add(getIndexAt(0, y, xSize));
		if (x % 2 == 0) {
			if (x < xSize-1 &&  y > 0) {
				int id = getIndexAt(x+1, y-1, xSize);
				if (id != -1)
					neigh.add(id);
			}
		*/		
			/*else if (isTorus){
				if (x < xSize-1 &&  y == 0) 
					neigh.add(getIndexAt(x+1, ySize -1, xSize));
				else if (x == xSize-1 &&  y > 0) 
					neigh.add(getIndexAt(0, y-1, xSize));
				else 
					neigh.add(getIndexAt(0, ySize -1, xSize));
			}*/
	//		if (x > 0 && y > 0) 
	//			neigh.add(getIndexAt(x-1, y-1, xSize));
			/*else if (isTorus){
				if (x > 0 &&  y == 0) 
					neigh.add(getIndexAt(x-1, ySize -1, xSize));
				else if (x == 0 &&  y > 0) 
					neigh.add(getIndexAt(xSize-1, y-1, xSize));
				else 
					neigh.add(getIndexAt(xSize-1, ySize -1, xSize));
			}*/
//		} else {
	//		if (x < xSize -1 &&  y < ySize -1) 
	//			neigh.add(getIndexAt(x+1, y+1, xSize));
			/*else if (isTorus){
				if (x < xSize-1 &&  y == ySize -1) 
					neigh.add(getIndexAt(x+1, 0, xSize));
				else if (x == xSize-1 &&  y < ySize -1) 
					neigh.add(getIndexAt(0, y+1, xSize));
				else 
					neigh.add(getIndexAt(0, 0, xSize));
			}*/
		//	if (x > 0 && y  < ySize-1) 
		//		neigh.add(getIndexAt(x-1, y+1, xSize));
			/*else if (isTorus){
				if (x == 0 && y  < ySize-1) 
					neigh.add(getIndexAt(xSize-1, y+1, xSize));
				else if (x > 0 && y == ySize-1) 
					neigh.add(getIndexAt(x-1, 0, xSize));
				else 
					neigh.add(getIndexAt(xSize-1, 0, xSize));
			}*/
	//	}
		
		return neigh;
		
	}
	
	@Override
	protected List<Integer> getNeighboursAtRadius(int placeIndex, int radius) {
		List<Integer> neigh2 = new GamaList<Integer>();
		List<Integer> neigh =  getNeighboursAtRadius(placeIndex, radius, xSize, ySize,isTorus);
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