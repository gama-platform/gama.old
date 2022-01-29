/*******************************************************************************************************
 *
 * ParticleDestructionListener.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.callbacks;

import org.jbox2d.dynamics.World;
import org.jbox2d.particle.ParticleGroup;

/**
 * The listener interface for receiving particleDestruction events.
 * The class that is interested in processing a particleDestruction
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addParticleDestructionListener<code> method. When
 * the particleDestruction event occurs, that object's appropriate
 * method is invoked.
 *
 * @see ParticleDestructionEvent
 */
public interface ParticleDestructionListener {
  /**
   * Called when any particle group is about to be destroyed.
   */
  void sayGoodbye(ParticleGroup group);

  /**
   * Called when a particle is about to be destroyed. The index can be used in conjunction with
   * {@link World#getParticleUserDataBuffer} to determine which particle has been destroyed.
   * 
   * @param index
   */
  void sayGoodbye(int index);
}
