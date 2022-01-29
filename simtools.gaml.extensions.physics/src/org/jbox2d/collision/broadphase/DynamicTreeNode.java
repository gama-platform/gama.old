/*******************************************************************************************************
 *
 * DynamicTreeNode.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.collision.broadphase;

import org.jbox2d.collision.AABB;

/**
 * The Class DynamicTreeNode.
 */
public class DynamicTreeNode {
  /**
   * Enlarged AABB
   */
  public final AABB aabb = new AABB();

  /** The user data. */
  public Object userData;

  /** The parent. */
  protected DynamicTreeNode parent;

  /** The child 1. */
  protected DynamicTreeNode child1;
  
  /** The child 2. */
  protected DynamicTreeNode child2;
  
  /** The id. */
  protected final int id;
  
  /** The height. */
  protected int height;

  /**
   * Gets the user data.
   *
   * @return the user data
   */
  public Object getUserData() {
    return userData;
  }

  /**
   * Sets the user data.
   *
   * @param argData the new user data
   */
  public void setUserData(Object argData) {
    userData = argData;
  }

  /**
   * Instantiates a new dynamic tree node.
   *
   * @param id the id
   */
  protected DynamicTreeNode(int id) {
    this.id = id;
  }
}
