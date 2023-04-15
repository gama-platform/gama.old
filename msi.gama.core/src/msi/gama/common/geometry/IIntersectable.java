/*******************************************************************************************************
 *
 * IIntersectable.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.common.geometry;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

import msi.gama.common.interfaces.IDisposable;

/**
 * The Interface IIntersectable.
 */
public interface IIntersectable extends IDisposable {

	/**
	 * Intersects.
	 *
	 * @param env the env
	 * @return true, if successful
	 */
	boolean intersects(Envelope env);

	/**
	 * Intersects.
	 *
	 * @param env the env
	 * @return true, if successful
	 */
	boolean intersects(Coordinate env);

}
