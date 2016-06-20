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
package ummisco.gama.ui.parameters;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolItem;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaList;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.ui.interfaces.EditorListener;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class ListEditor extends ExpressionBasedEditor<java.util.List> {

	ListEditor(final IScope scope, final IParameter param) {
		super(scope, param);
	}

	ListEditor(final IScope scope, final IAgent agent, final IParameter param) {
		this(scope, agent, param, null);
	}

	ListEditor(final IScope scope, final IAgent agent, final IParameter param, final EditorListener l) {
		super(scope, agent, param, l);
	}

	ListEditor(final IScope scope, final Composite parent, final String title, final Object value,
		final EditorListener<java.util.List> whenModified) {
		// Convenience method
		super(scope, new InputParameter(title, value), whenModified);
		this.createComposite(parent);
	}

	@Override
	public void applyEdit() {
		if ( currentValue instanceof GamaList ) {
			final ListEditorDialog d =
				new ListEditorDialog(WorkbenchHelper.getShell(), (GamaList) currentValue, param.getName());
			if ( d.open() == IDialogConstants.OK_ID ) {
				modifyAndDisplayValue(d.getList(ListEditor.this));
			}
		}
	}

	@Override
	protected void checkButtons() {
		final ToolItem edit = items[EDIT];
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
