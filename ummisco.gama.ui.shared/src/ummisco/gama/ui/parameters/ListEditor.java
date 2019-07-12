/*********************************************************************************************
 *
 * 'ListEditor.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.parameters;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Button;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.IList;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.ui.interfaces.EditorListener;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class ListEditor extends ExpressionBasedEditor<java.util.List<?>> {

	ListEditor(final IScope scope, final IAgent agent, final IParameter param,
			final EditorListener<java.util.List<?>> l) {
		super(scope, agent, param, l);
	}

	@SuppressWarnings ("rawtypes")
	@Override
	public void applyEdit() {
		if (currentValue instanceof IList) {
			final ListEditorDialog d =
					new ListEditorDialog(WorkbenchHelper.getShell(), (IList) currentValue, param.getName());
			if (d.open() == IDialogConstants.OK_ID) {
				modifyAndDisplayValue(d.getList(ListEditor.this));
			}
		}
	}

	@Override
	protected void checkButtons() {
		final Button edit = items[EDIT];
		if (edit != null && !edit.isDisposed()) {
			edit.setEnabled(currentValue instanceof IList);
		}
	}

	@SuppressWarnings ({ "unchecked", "rawtypes" })
	@Override
	public IType getExpectedType() {
		return Types.LIST;
	}

	@Override
	protected int[] getToolItems() {
		return new int[] { EDIT, REVERT };
	}

}
