/*******************************************************************************************************
 *
 * AssertEditor.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.statements.test.AbstractSummary;
import msi.gaml.statements.test.AssertionSummary;
import msi.gaml.statements.test.TestState;
import ummisco.gama.ui.controls.FlatButton;
import ummisco.gama.ui.interfaces.EditorListener;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.IGamaColors;

/**
 * The Class AssertEditor.
 */
public class AssertEditor extends AbstractStatementEditor<AbstractSummary<?>> {

	/**
	 * Instantiates a new assert editor.
	 *
	 * @param scope the scope
	 * @param command the command
	 */
	public AssertEditor(final IScope scope, final AbstractSummary<?> command) {
		super(scope, command, (EditorListener<Object>) null);
		isSubParameter = command instanceof AssertionSummary;
		name = command.getTitle();
	}

	@Override
	protected Control createCustomParameterControl(final Composite composite) throws GamaRuntimeException {
		textBox = FlatButton.button(composite, getColor(), getText());
		textBox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				GAMA.getGui().editModel(null, getStatement().getURI());
			}
		});
		return textBox;
	}

	/**
	 * Gets the color.
	 *
	 * @return the color
	 */
	private GamaUIColor getColor() {
		GamaUIColor color = GamaColors.get(getStatement().getColor());
		if (color == null) { color = IGamaColors.NEUTRAL; }
		return color;
	}

	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	private String getText() {
		final AbstractSummary<?> summary = getStatement();
		if (summary instanceof AssertionSummary && getStatement().getState() == TestState.ABORTED)
			return getStatement().getState().toString() + ": " + ((AssertionSummary) getStatement()).getError() + "  ";
		return getStatement().getState().toString() + "  ";
	}

}
