/*********************************************************************************************
 *
 * 'IValue.java, in plugin msi.gama.core, is part of the source code of the
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
 * Written by drogoul Modified on 19 nov. 2008
 * 
 * @todo Description
 * 
 */
public interface IValue extends IGamlable, ITyped {

	public abstract String stringValue(IScope scope) throws GamaRuntimeException;

	public abstract IValue copy(IScope scope) throws GamaRuntimeException;

}
