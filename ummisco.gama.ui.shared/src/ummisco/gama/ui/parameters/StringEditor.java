/*******************************************************************************************************
 *
 * StringEditor.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import java.util.List;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.InputParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.ui.interfaces.EditorListener;

/**
 * The Class StringEditor.
 */
public class StringEditor extends ExpressionBasedEditor<String> {

	/**
	 * Instantiates a new string editor.
	 *
	 * @param scope the scope
	 * @param agent the agent
	 * @param param the param
	 * @param l the l
	 */
	StringEditor(final IScope scope, final IAgent agent, final IParameter param, final EditorListener<String> l) {
		super(scope, agent, param, l);
	}

	/**
	 * Instantiates a new string editor.
	 *
	 * @param scope the scope
	 * @param parent the parent
	 * @param title the title
	 * @param value the value
	 * @param whenModified the when modified
	 */
	StringEditor(final IScope scope, final EditorsGroup parent, final String title, final Object value,
			final EditorListener<String> whenModified) {
		super(scope, new InputParameter(title, value), whenModified);
		this.createControls(parent);
	}

	/**
	 * Instantiates a new string editor.
	 *
	 * @param scope the scope
	 * @param parent the parent
	 * @param title the title
	 * @param value the value
	 * @param among the among
	 * @param whenModified the when modified
	 */
	StringEditor(final IScope scope, final EditorsGroup parent, final String title, final String value,
			final List<String> among, final EditorListener<String> whenModified) {
		super(scope, new InputParameter(title, value, Types.STRING, among), whenModified);
		this.createControls(parent);
	}

	@Override
	public IType<String> getExpectedType() {
		return Types.STRING;
	}

}
