/*********************************************************************************************
 *
 * 'BooleanEditor.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.parameters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.ui.controls.SwitchButton;
import ummisco.gama.ui.interfaces.EditorListener;

public class BooleanEditor extends AbstractEditor<Boolean> {

	private SwitchButton button;

	BooleanEditor(final IScope scope, final IParameter param) {
		super(scope, param);
		acceptNull = false;
	}

	BooleanEditor(final IScope scope, final Composite parent, final String title, final boolean value,
			final EditorListener<Boolean> whenModified) {
		super(scope, new InputParameter(title, value), whenModified);
		acceptNull = false;
		this.createComposite(parent);
	}

	BooleanEditor(final IScope scope, final IAgent agent, final IParameter param) {
		this(scope, agent, param, null);
	}

	BooleanEditor(final IScope scope, final IAgent agent, final IParameter param, final EditorListener<Boolean> l) {
		super(scope, agent, param, l);
		acceptNull = false;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		if (!internalModification) {
			modifyAndDisplayValue(button.getSelection());
		}
	}

	@Override
	protected void hideToolbar() {
		super.hideToolbar();
		if (getEditor() != null) {
			this.getEditor().setBackground(getNormalBackground());
		}
	}

	@Override
	protected void showToolbar() {
		super.showToolbar();
		if (getEditor() != null) {
			this.getEditor().setBackground(HOVERED_BACKGROUND);
			// AD 26/12/15 Commented for the moment to not force the focus (see
			// Issues #1339 and #1248)
			// if ( combo != null ) {
			// combo.forceFocus();
			// } else {
			// Control c = getEditorControl();
			// if ( c != null ) {
			// c.forceFocus();
			// }
			// }
		}
	}

	@Override
	public Control createCustomParameterControl(final Composite comp) {
		button = new SwitchButton(comp, SWT.CHECK);
		button.addSelectionListener(this);
		return button;
	}

	@Override
	protected void displayParameterValue() {
		internalModification = true;
		Boolean b = currentValue;
		if (b == null) {
			b = false;
		}
		button.setSelection(b);
		internalModification = false;

	}

	@Override
	public Control getEditorControl() {
		return button;
	}

	@Override
	public IType<Boolean> getExpectedType() {
		return Types.BOOL;
	}

	@Override
	protected int[] getToolItems() {
		return new int[] { REVERT };
	}

}
