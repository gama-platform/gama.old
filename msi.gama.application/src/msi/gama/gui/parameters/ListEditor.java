/*********************************************************************************************
 * 
 * 
 * 'ListEditor.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.parameters;

import msi.gama.common.interfaces.EditorListener;
import msi.gama.gui.swt.SwtGui;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.util.GamaList;
import msi.gaml.types.*;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.*;

public class ListEditor extends ExpressionBasedEditor<java.util.List> {

	ListEditor(final IParameter param) {
		super(param);
	}

	ListEditor(final IAgent agent, final IParameter param) {
		this(agent, param, null);
	}

	ListEditor(final IAgent agent, final IParameter param, final EditorListener l) {
		super(agent, param, l);
	}

	ListEditor(final Composite parent, final String title, final Object value,
		final EditorListener<java.util.List> whenModified) {
		// Convenience method
		super(new InputParameter(title, value), whenModified);
		this.createComposite(parent);
	}

	@Override
	public void applyEdit() {
		if ( currentValue instanceof GamaList ) {
			ListEditorDialog d = new ListEditorDialog(SwtGui.getShell(), (GamaList) currentValue, param.getName());
			if ( d.open() == IDialogConstants.OK_ID ) {
				modifyAndDisplayValue(d.getList(ListEditor.this));
			}
		}
	}

	@Override
	protected void checkButtons() {
		ToolItem edit = items[EDIT];
		if ( edit != null && !edit.isDisposed() ) {
			edit.setEnabled(currentValue instanceof GamaList);
		}
	}

	@Override
	public IType getExpectedType() {
		return Types.LIST;
	}

	@Override
	protected int[] getToolItems() {
		return new int[] { EDIT, REVERT };
	}

}
