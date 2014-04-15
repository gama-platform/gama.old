/*********************************************************************************************
 * 
 *
 * 'PathFinder.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.util.graph;

import java.util.List;

import msi.gama.util.IList;

/**
 * Optimizer of a set of intersections and roads. Its goal is to return the best
 * route between two intersections. Optimizers can be dynamic (in which case
 * they will probably only really implement bestRouteBetween. They can also be
 * static, which means that they need to be initialized (initialize()) and run
 * (run()) before any agents can use their results. Extends Runnable (which
 * provides the method run()) so that optimization can be easily launched (if
 * neeeded) as a separate thread in background with the following syntax:
 * 
 * Optimizer optimizer = new ClassImplementingOptimizer("...");
 * Executors.defaultThreadFactory().newThread(optimizer).start();
 * 
 * As of Nov. 07, three subclasses have been implemented :
 * FloydWarshallStaticOptimizer (static), DjikstraProgressiveOptimizer
 * (progressive, which means that a static matrix of adjacency is progressively
 * filled by the requests of the agents. Its advantage over a purely static
 * optimizer is that it does not require any startup time), and
 * DjikstraDynamicOptimizer (which does not maintain any matrix, thus allowing
 * the pathfinder to run on platforms with a small amount of memory available).
 * More will probably follow.
 * 
 * @author drogoul 7 nov. 07
 * 
 */
public interface PathFinder<V, E> extends Runnable {

	/**
	 * This number represents the coefficient by which distances in kilometers
	 * between intersections are being multiplied in order to get integers
	 * instead of floats in the matrices
	 */

	public static final int multiplier = 1000;//100000;

	/**
	 * This method returns the best route between the two intersections in
	 * parameters.
	 * 
	 * @param from
	 * @param to
	 * @return a list of objects (edges) composing the path
	 */
	public abstract IList bestRouteBetween(V from, V to);

	/**
	 * This method returns the length of the best paths found between two
	 * intersections.
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public abstract double bestDistanceBetween(V from, V to);

	/**
	 * Optimizers are initialized with a Map containing the intersections
	 * 
	 * @param intersections
	 * @param roads
	 */
	public abstract void setIntersections(List<V> intersections);

}
