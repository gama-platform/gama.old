/*******************************************************************************************************
 *
 * msi.gama.common.interfaces.IValue.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Represents a 'value' in GAML (a Java object that can provide a GAML type, be serializable into a GAML expression, and
 * be copied
 *
 * @author drogoul
 * @since 19 nov. 2008
 *
 */
public interface IValue extends IGamlable, ITyped {

	/**
	 * Returns the string 'value' of this value.
	 *
	 * @param scope
	 *            the current GAMA scope
	 * @return a string representing this value (not necessarily its serialization in GAML)
	 * @throws GamaRuntimeException
	 */
	String stringValue(IScope scope) throws GamaRuntimeException;

	/**
	 * Returns a copy of this value
	 *
	 * @param scope
	 *            the current GAMA scope
	 * @return a copy of this value. The definition of copy (whether shallow or deep, etc.) depends on the subclasses
	 * @throws GamaRuntimeException
	 */
	IValue copy(IScope scope) throws GamaRuntimeException;

}
