/**
 * Created by drogoul, 27 mai 2015
 * 
 */
package msi.gama.common.interfaces;

import java.util.*;

import msi.gama.runtime.IScope;
import msi.gama.util.GamaList;
import msi.gaml.statements.*;

/**
 * Class ICreateDelegate. 
 *
 * @author drogoul
 * @since 27 mai 2015
 *
 */
public interface ICreateDelegate {
	
	/**
	 * Returns whether or not this delegate accepts to create agents from this source.
	 * @param source
	 * @return
	 */
	
	boolean acceptSource(Object source);
	
	
	/**
	 * Fills the list of maps with the initial values read from the source. Returns true if all the inits have been correctly filled
	 * @param scope
	 * @param inits
	 * @param max can be null (in that case, the maximum number of agents to create is ignored)
	 * @param source
	 * @return
	 */
	
	boolean createFrom(IScope scope, List<Map> inits, Integer max, Object source, Arguments init, CreateStatement statement);

}
