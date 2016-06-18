/*********************************************************************************************
 *
 *
 * 'IOutputManager.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.outputs;

import java.util.Map;

import msi.gama.common.interfaces.IStepable;

/**
 * The class IOutputManager.
 *
 * @author drogoul
 * @since 14 d�c. 2011
 *
 */
public interface IOutputManager extends IStepable {

	void addOutput(IOutput output);

	void removeAllOutput();

	void removeOutput(IOutput output);

	IOutput getOutput(String id);

	public IOutput getOutputWithName(final String name);

	public IOutput getOutputWithOriginalName(final String name);

	// hqnghi
	Map<String, ? extends IOutput> getOutputs();

	void addOutput(String oName, IOutput o);

	// end-hqnghi
	void forceUpdateOutputs();

}
