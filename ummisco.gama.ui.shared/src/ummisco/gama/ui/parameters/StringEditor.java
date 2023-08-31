/*******************************************************************************************************
 *
 * StringEditor.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
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
	 * @param scope
	 *            the scope
	 * @param agent
	 *            the agent
	 * @param param
	 *            the param
	 * @param l
	 *            the l
	 */
	StringEditor(final IAgent agent, final IParameter param, final EditorListener<String> l) {
		super(agent, param, l);
	}

	@Override
	public IType<String> getExpectedType() { return Types.STRING; }

}
