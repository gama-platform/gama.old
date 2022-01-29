/*******************************************************************************************************
 *
 * ContactEdge.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.dynamics.contacts;

import org.jbox2d.dynamics.Body;

/**
 * A contact edge is used to connect bodies and contacts together in a contact graph where each body
 * is a node and each contact is an edge. A contact edge belongs to a doubly linked list maintained
 * in each attached body. Each contact has two contact nodes, one for each attached body.
 * 
 * @author daniel
 */
public class ContactEdge {

  /**
   * provides quick access to the other body attached.
   */
  public Body other = null;

  /**
   * the contact
   */
  public Contact contact = null;

  /**
   * the previous contact edge in the body's contact list
   */
  public ContactEdge prev = null;

  /**
   * the next contact edge in the body's contact list
   */
  public ContactEdge next = null;
}
