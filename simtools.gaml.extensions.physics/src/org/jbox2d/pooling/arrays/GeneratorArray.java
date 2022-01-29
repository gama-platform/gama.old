/*******************************************************************************************************
 *
 * GeneratorArray.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.pooling.arrays;

import java.util.HashMap;

import org.jbox2d.particle.VoronoiDiagram;

/**
 * The Class GeneratorArray.
 */
public class GeneratorArray {

  /** The map. */
  private final HashMap<Integer, VoronoiDiagram.Generator[]> map =
      new HashMap<Integer, VoronoiDiagram.Generator[]>();

  /**
   * Gets the.
   *
   * @param length the length
   * @return the voronoi diagram. generator[]
   */
  public VoronoiDiagram.Generator[] get(int length) {
    assert (length > 0);

    if (!map.containsKey(length)) {
      map.put(length, getInitializedArray(length));
    }

    assert (map.get(length).length == length) : "Array not built of correct length";
    return map.get(length);
  }

  /**
   * Gets the initialized array.
   *
   * @param length the length
   * @return the initialized array
   */
  protected VoronoiDiagram.Generator[] getInitializedArray(int length) {
    final VoronoiDiagram.Generator[] ray = new VoronoiDiagram.Generator[length];
    for (int i = 0; i < ray.length; i++) {
      ray[i] = new VoronoiDiagram.Generator();
    }
    return ray;
  }
}
