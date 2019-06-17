/*
 * Copyright 2006 - 2016
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
package msi.gama.util.graph.graphstream_copy;

/**
 * Graph generator.
 * 
 * <p>
 * A graph generator is an object that can send graph events to create a
 * new graph from an internal description. Some generators will create a
 * static predefined graph, others will be able to continuously evolve
 * Indeed some generators define an end to the generation process, others
 * may continue endlessly.
 * </p>
 * 
 * <p>
 * Each generator, in addition of being a source of events, provide only
 * three methods:
 * <ul>
 * 		<li>One to start the generation process {@link #begin()}.
 * 		For static generators this often generate a whole graph, for dynamic
 * 		generators this only initialise a base graph.</li>
 * 		<li>One to generate more dynamic events {@link #nextEvents()}.
 * 		This method will, as its name suggests, generate more dynamic
 * 		events making the graph evolve. You can call it (repeatedly) only
 * 		between a call to {@link #begin()} and to {@link #end()}. This
 * 		method returns a boolean that may indicate that no more events
 * 		can be generated.</li>
 * 		<li>One to end the generation process {@link #end()}. This method
 * 		must ALWAYS be called when finished with the generator.</li>
 * </ul>
 * </p>
 */
public interface Generator extends Source {
	/**
	 * Begin the graph generation. This usually is the place for initialization
	 * of the generator. After calling this method, call the
	 * {@link #nextEvents()} method to add elements to the graph.
	 */
	void begin();

	/**
	 * Perform the next step in generating the graph. While this method returns
	 * true, there are still more elements to add to the graph to generate it.
	 * Be careful that some generators never return false here, since they can
	 * generate graphs of arbitrary size. For such generators, simply stop
	 * calling this method when enough elements have been generated.
	 * 
	 * A call to this method can produce an undetermined number of nodes and
	 * edges. Checking nodes count is advisable when generating the graph to
	 * avoid an unwanted big graph.
	 * 
	 * @return true while there are elements to add to the graph.
	 */
	boolean nextEvents();

	/**
	 * End the graph generation by finalizing it. Once the {@link #nextEvents()}
	 * method returned false (or even if you stop before), this method must be
	 * called to finish the graph.
	 */
	void end();
}
