/*********************************************************************************************
 *
 * 'IOutputManager.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.outputs;

import java.util.Map;

import msi.gama.common.interfaces.IStepable;
import msi.gama.runtime.IScope;

/**
 * The class IOutputManager.
 *
 * @author drogoul
 * @since 14 d�c. 2011
 *
 */
public interface IOutputManager extends IStepable, Iterable<IOutput> {

	void add(IOutput output);

	void put(String name, IOutput output);

	void clear();

	void putAll(Map<String, IOutput> outputs);

	void remove(IOutput output);

	IOutput get(String id);

	IOutput getOutputWithName(final String name);

	IOutput getOutputWithOriginalName(final String name);

	// hqnghi
	Map<String, ? extends IOutput> getOutputs();

	// end-hqnghi
	void forceUpdateOutputs();

	void dispose(IScope scope);

	public Iterable<IDisplayOutput> getDisplayOutputs();

	void pause();

	void resume();

	void synchronize();

	void unSynchronize();

	void close();

}
