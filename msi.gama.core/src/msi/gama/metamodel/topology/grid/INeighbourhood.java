/**
 * Created by drogoul, 19 mai 2013
 * 
 */
package msi.gama.metamodel.topology.grid;

import java.util.Iterator;
import msi.gama.metamodel.agent.IAgent;

/**
 * Class INeighbourhood. 
 *
 * @author drogoul
 * @since 19 mai 2013
 *
 */
public interface INeighbourhood {

	public abstract Iterator<IAgent> getNeighboursIn(final int placeIndex, final int radius);

	public abstract boolean isVN();

}