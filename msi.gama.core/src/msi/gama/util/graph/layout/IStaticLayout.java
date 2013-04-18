package msi.gama.util.graph.layout;

import java.util.Map;

import msi.gama.runtime.IScope;
import msi.gama.util.graph.GamaGraph;

/**
 * represents a "static" layout, that is a offline one, made to be ran in a run-once way.
 * 
 * @author Samuel Thiriot
 */
public interface IStaticLayout {

	/**
	 * applies a layout for a Gama scope, a gama graph, 
	 * in the given duration (in milliseconds , -1 means no limit), and optional
	 * options that may be accepted by each layout
	 * @param scope
	 * @param graph
	 * @param timeout
	 * @param options
	 */
	public void doLayoutOneShot(IScope scope, GamaGraph<?, ?> graph, long timeout, Map<String,Object> options);
	
}
