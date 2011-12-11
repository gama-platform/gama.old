/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.interfaces;

import msi.gama.util.GamaPoint;

/**
 * WithLocation
 * 
 * @author drogoul 23 oct. 07
 * 
 */
public interface ILocation {

	/**
	 * Sets the location of the object implementing this interface. Can be used to modify the
	 * coordinates of the agents, for instance.
	 * 
	 * @param p the new location
	 */
	public abstract void setLocation(GamaPoint p);

	/**
	 * Gets the absolute location of the object implemeting this interface . If the implementation
	 * classes provide an explicit location to their instances (i.e. coordinates, etc.) it can be
	 * used here. Otherwise, it is ok to return null.
	 * 
	 * @return the absolute (i.e. agent-like) location
	 */
	public abstract GamaPoint getLocation();

}
