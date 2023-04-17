/*******************************************************************************************************
 *
 * ILocated.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gaml.types.IType;

/**
 * ILocated. Represents objects that are provided with a location in GAMA (and thus with an attribute named 'location'
 * in GAML)
 *
 * @author drogoul
 * @since 23 oct. 07
 *
 */
@vars ({ @variable (
		name = IKeyword.LOCATION,
		type = IType.POINT,
		doc = { @doc ("Returns the location of the receiver object") }) })
public interface ILocated {

	/**
	 * Sets the location of the object implementing this interface. Can be used to modify the coordinates of the agents,
	 * for instance.
	 *
	 * @param p
	 *            the new location
	 * @return the new location or the previous one if a problem has occured
	 */
	GamaPoint setLocation(GamaPoint p);

	/**
	 * Gets the absolute location of the object implemeting this interface . If the implementation classes provide an
	 * explicit location to their instances (i.e. coordinates, etc.) it can be used here. Otherwise, it is ok to return
	 * null.
	 *
	 * @return the absolute (i.e. agent-like) location
	 */
	@getter (IKeyword.LOCATION)
	GamaPoint getLocation();

}
