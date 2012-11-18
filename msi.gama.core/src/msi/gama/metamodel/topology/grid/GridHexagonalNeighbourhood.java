package msi.gama.metamodel.topology.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaList;

public class GridHexagonalNeighbourhood extends GridNeighbourhood {
	private boolean returnNull = false;
	private static final int[] singleExtent = {1};
	
	public GridHexagonalNeighbourhood(final IShape[] agents, final int xSize, final int ySize ,final boolean isTorus) {
		super(agents, xSize, ySize , isTorus);
	}

	@Override
	protected List<Integer> getNeighboursAtRadius(int placeIndex, int radius) {
		int y = placeIndex / xSize;
		int x = placeIndex - y * xSize;
		Vector vec = getNeighbors(x, y,false);
		return new GamaList<Integer>(vec);
	}

	@Override
	public boolean isVN() {
		return false;
	}
	
	public Vector getNeighbors(int x, int y, boolean returnNull) {
		return getNeighbors(x, y, singleExtent, returnNull);
	}
	public Vector getNeighbors(int x, int y, int[] extents, boolean returnNull) {
		  this.returnNull = returnNull;
		  if (extents.length != 1)
		    throw new IllegalArgumentException("Hexagonal Neighborhoods take one argument");
	int radius = extents[0];
		      if (radius < 1) return new Vector();
		 
		         if (radius == 1) return singleExtent(x, y);
		         if (radius == 2) return doubleExtent(x, y);
		         else return gtTwoExtent(x, y, radius);
		     }
		 
		     private Vector gtTwoExtent(int x, int y, int extent) {
	         Vector v = new Vector(3 * extent * (extent + 1));
		         int destIndex = 0;
		 
		         if (x % 2 == 0) {
		             for (int radius = extent; radius > 2; radius--) {
		                 Vector src = getEvenRing(x, y, radius);
		 
		                 v.addAll(src);
		             }
		         } else {
	             for (int radius = extent; radius > 2; radius--) {
		                 Vector src = getOddRing(x, y, radius);
		 
		                 v.addAll(src);
		             }
		         }
		 
		         Vector src = doubleExtent(x, y);
	 
		         v.addAll(src);
	
		         return v;
		     }
		 
		     private Vector getEvenRing(int x, int y, int radius) {
		         Vector v = new Vector(radius * 6);
		         int yVal = y - radius;
		 
		         addXY(v, x, yVal++);
		 
		         int aIndex = 1;
		         int limit = x + radius;
		         int xVal = x + 1;
		 
		         while (xVal <= limit) {
		            addXY(v, xVal++, yVal);
		             if (xVal > limit) {
		                 yVal++;
		                 break;
		             }
		             addXY(v, xVal++, yVal);
		             yVal++;
		         }
	
	         xVal = x + radius;
	         for (int i = 0; i < radius; i++)
	             addXY(v, xVal, yVal++);
	 
	         if (xVal % 2 != 0) {
	             xVal--;
	            yVal--;
	             addXY(v, xVal, yVal++);
	         }
	 
	         xVal--;
	 
	         while (xVal > x) {
	             addXY(v, xVal--, yVal);
	             if (xVal == x) break;
		             addXY(v, xVal--, yVal);
		             yVal++;
		         }
		 
		         yVal = y + radius;
		         addXY(v, x, yVal);
		         addXY(v, x - 1, yVal);
		 
		         yVal--;
		         xVal = x - 2;
		         limit = x - radius;
		         while (xVal >= limit) {
		             addXY(v, xVal--, yVal);
	             if (xVal < limit) {
		                 yVal--;
		                 break;
		             }
		            addXY(v, xVal--, yVal);
		            yVal--;
		         }
		
		         xVal = x - radius;
		        for (int i = 0; i < radius; i++)
		             addXY(v, xVal, yVal--);
		 
		         xVal++;
		         if (xVal % 2 != 0) {
		            yVal++;
		             addXY(v, xVal++, yVal--);
		         }
		 
		        while (xVal < x) {
		             addXY(v, xVal++, yVal);
		            if (xVal == x) break;
		             addXY(v, xVal++, yVal);
		            yVal--;
		         }
		 
		         //printArray(rarray);
		         return v;
		     }
		 
		     private Vector getOddRing(int x, int y, int radius) {
		 
		         Vector v = new Vector(radius * 6);
		
		         addXY(v, x, y - radius);
		         addXY(v, x + 1, y - radius);
		 
		         int aIndex = 2;
		         int xVal = x + 2;
		         int yVal = (y - radius) + 1;
		         int limit = x + radius;
		 
		         while (xVal <= limit) {
		             addXY(v, xVal++, yVal);
		             if (xVal > limit) {
		                 yVal++;
		                 break;
		             }
		             addXY(v, xVal++, yVal);
		             yVal++;
		         }
		 
		         xVal = x + radius;
		         for (int i = 0; i < radius; i++)
		             addXY(v, xVal, yVal++);
		 
		         if (xVal % 2 != 0) {
		             yVal--;
		             xVal--;
		             addXY(v, xVal, yVal++);
		         }
		 
		         xVal--;
		         while (xVal > x) {
		             addXY(v, xVal--, yVal);
		             addXY(v, xVal--, yVal);
		             yVal++;
		         }
		 
		         addXY(v, x, y + radius);
		         yVal = y + radius - 1;
		         xVal = x - 1;
		 
		         limit = x - radius;
	         while (xVal >= limit) {
		             addXY(v, xVal--, yVal);
		             if (xVal < limit) {
		                 yVal--;
		                 break;
		             }
		             addXY(v, xVal--, yVal);
		             yVal--;
		         }
	 
	         xVal = x - radius;
		         for (int i = 0; i < radius; i++)
		             addXY(v, xVal, yVal--);
		 
		         xVal++;
		         if (xVal % 2 != 0) {
		             yVal++;
		             addXY(v, xVal++, yVal);
		             yVal--;
		         }
		 
		         while (xVal < x) {
		             addXY(v, xVal++, yVal);
		             if (xVal == x) break;
		             addXY(v, xVal++, yVal);
		             yVal--;
		         }
		 
		         return v;
		     }
		 
		     private Vector doubleExtent(int x, int y) {
	         Vector v = new Vector();
		 
		         if (x % 2 == 0) {
		             addXY(v, x, y - 2);
		             addXY(v, x + 1, y - 1);
		             addXY(v, x + 2, y - 1);
		             addXY(v, x + 2, y);
		             addXY(v, x + 2, y + 1);
		             addXY(v, x + 1, y + 2);
		             addXY(v, x, y + 2);
		             addXY(v, x - 1, y + 2);
		             addXY(v, x - 2, y + 1);
		             addXY(v, x - 2, y);
		            addXY(v, x - 2, y - 1);
		             addXY(v, x - 1, y - 1);
		 
		         } else {
		             addXY(v, x, y - 2);
		             addXY(v, x + 1, y - 2);
		             addXY(v, x + 2, y - 1);
	            addXY(v, x + 2, y);
	             addXY(v, x + 2, y + 1);
		             addXY(v, x + 1, y + 1);
		             addXY(v, x, y + 2);
		             addXY(v, x - 1, y + 1);
		             addXY(v, x - 2, y + 1);
		             addXY(v, x - 2, y);
		             addXY(v, x - 2, y - 1);
		             addXY(v, x - 1, y - 2);
		         }
		         v.addAll(singleExtent(x, y));
		         return v;
		     }
		 
		     private Vector singleExtent(int x, int y) {
		         Vector v = new Vector(6);
		 
		         if (x % 2 == 0) {
		             addXY(v, x, y - 1);
		             addXY(v, x + 1, y);
		             addXY(v, x + 1, y + 1);
		             addXY(v, x, y + 1);
		             addXY(v, x - 1, y + 1);
		             addXY(v, x - 1, y);
		         } else {
		             int top = y - 1;
		 
		             addXY(v, x, top);
		           addXY(v, x + 1, top);
		             addXY(v, x + 1, y);
		             addXY(v, x, y + 1);
		             addXY(v, x - 1, y);
		             addXY(v, x - 1, top);
		         }
		 
		         return v;
		     }
		 
		     protected void addXY(Vector v, int x, int y) {
		         Object o = null;
		 
		         if (!isTorus) {
		             if (x >= 0 & x < xSize & y >= 0 & y < ySize)
		                 o = getPlaceIndexAt(x, y);
		       } else
		             o = getPlaceIndexAt(x, y);
		         if (returnNull)
		            v.add(o);
		         else if (o != null)
		           v.add(o);
		     }
		 
		


}