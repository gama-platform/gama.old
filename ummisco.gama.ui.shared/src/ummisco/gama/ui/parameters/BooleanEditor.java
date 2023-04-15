/*******************************************************************************************************
 *
 * BooleanEditor.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
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
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.IGamaColors;

/**
 * The Class BooleanEditor.
 */
public class BooleanEditor extends AbstractEditor<Boolean> {

	/** The button. */
	private SwitchButton button;

	// /**
	// * Instantiates a new boolean editor.
	// *
	// * @param scope the scope
	// * @param parent the parent
	// * @param title the title
	// * @param value the value
	// * @param whenModified the when modified
	// */
	// BooleanEditor(final IScope scope, final EditorsGroup parent, final String title, final boolean value,
	// final EditorListener<Boolean> whenModified) {
	// super(scope, new InputParameter(title, value), whenModified);
	// acceptNull = false;
	// this.createControls(parent);
	// }

	/**
	 * Instantiates a new boolean editor.
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
		final var colors = getParam().getColors(getScope());
		final String[] labels = getParam().getLabels(getScope());
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
		button = new SwitchButton(comp, SWT.CHECK, left, right, labels);
		button.addSelectionListener(this);
		return button;
	}

	@Override
	protected void displayParameterValue() {
		internalModification = true;
		button.setSelection(currentValue == null ? false : currentValue);
		internalModification = false;

	}

	@Override
	public IType<Boolean> getExpectedType() { return Types.BOOL; }

	@Override
	protected int[] getToolItems() { return new int[] { REVERT }; }

}
