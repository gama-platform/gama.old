/*********************************************************************************************
 *
 * 'IExecutable.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.statements;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Class IExecutable.
 * 
 * @author drogoul
 * @since 20 août 2013
 * 
 */
public interface IExecutable {

	public abstract Object executeOn(final IScope scope) throws GamaRuntimeException;
}
