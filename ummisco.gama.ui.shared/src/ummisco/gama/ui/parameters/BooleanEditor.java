/*********************************************************************************************
 *
 * 'BooleanEditor.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
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
import msi.gama.kernel.experiment.InputParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.ui.controls.SwitchButton;
import ummisco.gama.ui.interfaces.EditorListener;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.IGamaColors;

public class BooleanEditor extends AbstractEditor<Boolean> {

	private SwitchButton button;

	BooleanEditor(final IScope scope, final Composite parent, final String title, final boolean value,
			final EditorListener<Boolean> whenModified) {
		super(scope, new InputParameter(title, value), whenModified);
		acceptNull = false;
		this.createComposite(parent);
	}

	BooleanEditor(final IScope scope, final IAgent agent, final IParameter param, final EditorListener<Boolean> l) {
		super(scope, agent, param, l);
		acceptNull = false;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		if (!internalModification) { modifyAndDisplayValue(button.getSelection()); }
	}

	@Override
	public Control createCustomParameterControl(final Composite comp) {
		final var colors = getParam().getColor(getScope());
		var left = IGamaColors.OK.color();
		var right = IGamaColors.ERROR.color();
		if (colors != null) {
			if (colors.size() == 1) {
				left = right = GamaColors.get(colors.get(0)).color();
			} else if (colors.size() >= 2) {
				left = GamaColors.get(colors.get(0)).color();
				right = GamaColors.get(colors.get(1)).color();
			}
		}
		button = new SwitchButton(comp, SWT.CHECK, left, right);
		button.addSelectionListener(this);
		return button;
	}

	@Override
	protected void displayParameterValue() {
		internalModification = true;
		var b = currentValue;
		if (b == null) { b = false; }
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
