/*******************************************************************************************************
 *
 * FixtureProxy.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.dynamics;

import org.jbox2d.collision.AABB;

/**
 * This proxy is used internally to connect fixtures to the broad-phase.
 * 
 * @author Daniel
 */
public class FixtureProxy {
  
  /** The aabb. */
  final AABB aabb = new AABB();
  
  /** The fixture. */
  Fixture fixture;
  
  /** The child index. */
  int childIndex;
  
  /** The proxy id. */
  int proxyId;
}
