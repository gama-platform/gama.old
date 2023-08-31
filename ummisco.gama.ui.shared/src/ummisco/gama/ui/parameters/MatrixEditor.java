/*******************************************************************************************************
 *
 * MatrixEditor.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import org.eclipse.jface.dialogs.IDialogConstants;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.ui.interfaces.EditorListener;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The Class MatrixEditor.
 */
public class MatrixEditor extends ExpressionBasedEditor<IMatrix<?>> {

	/**
	 * Instantiates a new matrix editor.
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
	MatrixEditor(final IAgent agent, final IParameter param, final EditorListener<IMatrix<?>> l) {
		super(agent, param, l);
	}

	@Override
	public void applyEdit() {

		final MatrixEditorDialog d = new MatrixEditorDialog(getScope(), WorkbenchHelper.getShell(), currentValue);
		if (d.open() == IDialogConstants.OK_ID) { modifyValue(d.getMatrix()); }

	}

	@Override
	protected void updateToolbar() {
		super.updateToolbar();
		editorToolbar.enable(EDIT, currentValue != null);
	}

	@SuppressWarnings ({ "unchecked", "rawtypes" })
	@Override
	public IType getExpectedType() { return Types.MATRIX; }

	@Override
	protected int[] getToolItems() { return new int[] { EDIT, REVERT }; }

}
