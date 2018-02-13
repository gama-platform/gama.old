/*********************************************************************************************
 *
 * 'ICreateDelegate.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.common.interfaces;

import msi.gama.outputs.layers.EventLayerStatement;
import msi.gama.runtime.IScope;

/**
 * Class ICreateDelegate.
 *
 * @author drogoul
 * @since 27 mai 2015
 *
 */
public interface IEventLayerDelegate {

	/**
	 * Returns whether or not this delegate accepts the input source.
	 * 
	 * @param scope
	 * @param source
	 * 
	 * @return
	 */

	boolean acceptSource(IScope scope, Object source);

	/**
	 * Fills the list of maps with the initial values read from the source. Returns true if all the inits have been
	 * correctly filled
	 * 
	 * @param scope
	 * @param source
	 * @return
	 */

	boolean createFrom(IScope scope, Object source, EventLayerStatement statement);

}
