/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
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
