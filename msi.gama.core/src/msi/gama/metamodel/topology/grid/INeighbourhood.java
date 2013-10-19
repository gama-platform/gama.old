/**
 * Created by drogoul, 19 mai 2013
 * 
 */
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