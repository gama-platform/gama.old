/*********************************************************************************************
 *
 * 'GenericEditor.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.parameters;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.InputParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import ummisco.gama.ui.interfaces.EditorListener;

public class GenericEditor<T> extends ExpressionBasedEditor<T> {

	GenericEditor(final IScope scope, final IAgent agent, final IParameter param, final EditorListener<T> l) {
		super(scope, agent, param, l);
	}

	GenericEditor(final IScope scope, final EditorsGroup parent, final String title, final T value,
			final EditorListener<T> whenModified) {
		// Convenience method
		super(scope, new InputParameter(title, value), whenModified);
		this.createControls(parent);
	}

}
