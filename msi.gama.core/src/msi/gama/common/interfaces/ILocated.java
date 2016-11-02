/*********************************************************************************************
 *
 * 'ILocated.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.common.interfaces;

import msi.gama.metamodel.shape.ILocation;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gaml.types.IType;

/**
 * WithLocation
 *
 * @author drogoul 23 oct. 07
 *
 */
@vars({
	@var(name = IKeyword.LOCATION, type = IType.POINT, doc = { @doc("Returns the location of the receiver object") }) })
public interface ILocated {

	/**
	 * Sets the location of the object implementing this interface. Can be used to modify the
	 * coordinates of the agents, for instance.
	 *
	 * @param p the new location
	 */
	public abstract void setLocation(ILocation p);

	/**
	 * Gets the absolute location of the object implemeting this interface . If the implementation
	 * classes provide an explicit location to their instances (i.e. coordinates, etc.) it can be
	 * used here. Otherwise, it is ok to return null.
	 *
	 * @return the absolute (i.e. agent-like) location
	 */
	@getter(IKeyword.LOCATION)
	public abstract ILocation getLocation();

}
