/*********************************************************************************************
 *
 * 'IStepable.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.common.interfaces;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * The class IStepable.
 * 
 * @author drogoul
 * @since 13 dec. 2011
 * 
 */
public interface IStepable {

	/**
	 * Called to initialize the attributes of the IStepable with a valid scope.
	 * @param scope
	 * @return true, if the initialization has been performed correctly
	 * @throws GamaRuntimeException
	 */
	public boolean init(IScope scope) throws GamaRuntimeException;

	public boolean step(IScope scope) throws GamaRuntimeException;

	public void dispose();

}
