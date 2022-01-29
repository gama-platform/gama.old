/*******************************************************************************************************
 *
 * BroadPhase.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.collision.broadphase;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.callbacks.PairCallback;
import org.jbox2d.callbacks.TreeCallback;
import org.jbox2d.callbacks.TreeRayCastCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.RayCastInput;
import org.jbox2d.common.Vec2;


/**
 * The Interface BroadPhase.
 */
public interface BroadPhase {

  /** The Constant NULL_PROXY. */
  public static final int NULL_PROXY = -1;

  /**
   * Create a proxy with an initial AABB. Pairs are not reported until updatePairs is called.
   * 
   * @param aabb
   * @param userData
   * @return
   */
  int createProxy(AABB aabb, Object userData);

  /**
   * Destroy a proxy. It is up to the client to remove any pairs.
   * 
   * @param proxyId
   */
  void destroyProxy(int proxyId);

  /**
   * Call MoveProxy as many times as you like, then when you are done call UpdatePairs to finalized
   * the proxy pairs (for your time step).
   */
  void moveProxy(int proxyId, AABB aabb, Vec2 displacement);

  /**
   * Touch proxy.
   *
   * @param proxyId the proxy id
   */
  void touchProxy(int proxyId);

  /**
   * Gets the user data.
   *
   * @param proxyId the proxy id
   * @return the user data
   */
  Object getUserData(int proxyId);

  /**
   * Gets the fat AABB.
   *
   * @param proxyId the proxy id
   * @return the fat AABB
   */
  AABB getFatAABB(int proxyId);

  /**
   * Test overlap.
   *
   * @param proxyIdA the proxy id A
   * @param proxyIdB the proxy id B
   * @return true, if successful
   */
  boolean testOverlap(int proxyIdA, int proxyIdB);

  /**
   * Get the number of proxies.
   * 
   * @return
   */
  int getProxyCount();

  /**
   * Draw tree.
   *
   * @param argDraw the arg draw
   */
  void drawTree(DebugDraw argDraw);

  /**
   * Update the pairs. This results in pair callbacks. This can only add pairs.
   * 
   * @param callback
   */
  void updatePairs(PairCallback callback);

  /**
   * Query an AABB for overlapping proxies. The callback class is called for each proxy that
   * overlaps the supplied AABB.
   * 
   * @param callback
   * @param aabb
   */
  void query(TreeCallback callback, AABB aabb);

  /**
   * Ray-cast against the proxies in the tree. This relies on the callback to perform a exact
   * ray-cast in the case were the proxy contains a shape. The callback also performs the any
   * collision filtering. This has performance roughly equal to k * log(n), where k is the number of
   * collisions and n is the number of proxies in the tree.
   * 
   * @param input the ray-cast input data. The ray extends from p1 to p1 + maxFraction * (p2 - p1).
   * @param callback a callback class that is called for each proxy that is hit by the ray.
   */
  void raycast(TreeRayCastCallback callback, RayCastInput input);

  /**
   * Get the height of the embedded tree.
   * 
   * @return
   */
  int getTreeHeight();

  /**
   * Gets the tree balance.
   *
   * @return the tree balance
   */
  int getTreeBalance();

  /**
   * Gets the tree quality.
   *
   * @return the tree quality
   */
  float getTreeQuality();
}
