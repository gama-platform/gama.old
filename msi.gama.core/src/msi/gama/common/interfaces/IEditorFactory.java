/*********************************************************************************************
 * 
 *
 * 'IEditorFactory.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.common.interfaces;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;

/**
 * The class IEditorFactory.
 * 
 * @author drogoul
 * @since 18 d�c. 2011
 * 
 */
public interface IEditorFactory {

	IParameterEditor create(IScope scope, IAgent agent, IParameter var, EditorListener l);

}
