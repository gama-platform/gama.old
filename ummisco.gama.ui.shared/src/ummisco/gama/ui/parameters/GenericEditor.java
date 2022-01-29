/*******************************************************************************************************
 *
 * GenericEditor.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.InputParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import ummisco.gama.ui.interfaces.EditorListener;

/**
 * The Class GenericEditor.
 *
 * @param <T> the generic type
 */
public class GenericEditor<T> extends ExpressionBasedEditor<T> {

	/**
	 * Instantiates a new generic editor.
	 *
	 * @param scope the scope
	 * @param agent the agent
	 * @param param the param
	 * @param l the l
	 */
	GenericEditor(final IScope scope, final IAgent agent, final IParameter param, final EditorListener<T> l) {
		super(scope, agent, param, l);
	}

	/**
	 * Instantiates a new generic editor.
	 *
	 * @param scope the scope
	 * @param parent the parent
	 * @param title the title
	 * @param value the value
	 * @param whenModified the when modified
	 */
	GenericEditor(final IScope scope, final EditorsGroup parent, final String title, final T value,
			final EditorListener<T> whenModified) {
		// Convenience method
		super(scope, new InputParameter(title, value), whenModified);
		this.createControls(parent);
	}

}
