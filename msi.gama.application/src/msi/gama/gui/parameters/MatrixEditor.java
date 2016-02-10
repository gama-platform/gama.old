/*********************************************************************************************
 *
 *
 * 'MatrixEditor.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.parameters;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.*;
import msi.gama.common.interfaces.EditorListener;
import msi.gama.gui.swt.SwtGui;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.types.*;

public class MatrixEditor extends ExpressionBasedEditor<IMatrix> {

	MatrixEditor(final IParameter param) {
		super(param);
	}

	MatrixEditor(final IAgent agent, final IParameter param) {
		this(agent, param, null);
	}

	MatrixEditor(final IAgent agent, final IParameter param, final EditorListener l) {
		super(agent, param, l);
	}

	MatrixEditor(final Composite parent, final String title, final IMatrix value,
		final EditorListener<IMatrix> whenModified) {
		// Convenience method
		super(new InputParameter(title, value), whenModified);
		this.createComposite(parent);
	}

	@Override
	public void applyEdit() {

		MatrixEditorDialog d = new MatrixEditorDialog(SwtGui.getShell(), currentValue);
		if ( d.open() == IDialogConstants.OK_ID ) {
			modifyValue(d.getMatrix());
		}

	}

	@Override
	protected void checkButtons() {
		ToolItem edit = items[EDIT];
		if ( edit != null && !edit.isDisposed() ) {
			edit.setEnabled(true);
		}
	}

	@Override
	public IType getExpectedType() {
		return Types.MATRIX;
	}

	@Override
	protected int[] getToolItems() {
		return new int[] { EDIT, REVERT };
	}

}
