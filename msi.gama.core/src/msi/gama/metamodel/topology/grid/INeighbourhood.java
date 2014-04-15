/*********************************************************************************************
 * 
 *
 * 'INeighbourhood.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.metamodel.topology.grid;

import java.util.Set;
import msi.gama.metamodel.agent.IAgent;

/**
 * Class INeighbourhood.
 * 
 * @author drogoul
 * @since 19 mai 2013
 * 
 */
public interface INeighbourhood {

	public abstract Set<IAgent> getNeighboursIn(final int placeIndex, final int radius);

	public abstract boolean isVN();

	/**
	 * @param placeIndex
	 * @param range
	 * @return
	 */
	public abstract int[] getRawNeighboursIncluding(int placeIndex, int range);

	/**
	 * @param placeIndex
	 * @param n
	 * @return
	 */
	public abstract int neighboursIndexOf(int placeIndex, int n);

}