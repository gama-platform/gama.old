/*******************************************************************************************************
 *
 * AssertEditor.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

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
import ummisco.gama.ui.views.toolbar.Selector;

/**
 * The Class AssertEditor.
 */
public class AssertEditor extends AbstractStatementEditor<AbstractSummary<?>> {

	/**
	 * Instantiates a new assert editor.
	 *
	 * @param scope
	 *            the scope
	 * @param command
	 *            the command
	 */
	public AssertEditor(final IScope scope, final AbstractSummary<?> command) {
		super(scope, command, (EditorListener<Object>) null);
		isSubParameter = command instanceof AssertionSummary;
		name = command.getTitle();
	}

	@Override
	protected int[] getToolItems() { return new int[] { VALUE }; }

	@Override
	EditorToolbar createEditorToolbar() {
		editorToolbar = super.createEditorToolbar();
		if (isSubParameter) {
			editorToolbar.setHorizontalAlignment(SWT.LEAD);
			Label l = editorToolbar.getItem(VALUE);
			if (l != null && !l.isDisposed()) {
				l.setFont(JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT));
				l.setText(name);
				l.setAlignment(SWT.LEAD);
			}
		}
		return editorToolbar;
	}

	@Override
	EditorLabel createEditorLabel() {
		editorLabel = new EditorLabel(this, parent, isSubParameter ? " " : name, isSubParameter);
		editorLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		return editorLabel;
	}

	@Override
	protected FlatButton createCustomParameterControl(final Composite composite) throws GamaRuntimeException {
		final AbstractSummary<?> summary = getStatement();
		String text = summary instanceof AssertionSummary && getStatement().getState() == TestState.ABORTED
				? getStatement().getState().toString() + ": " + ((AssertionSummary) getStatement()).getError()
				: getStatement().getState().toString();
		textBox = FlatButton.button(composite, null, text);
		textBox.addSelectionListener((Selector) e -> GAMA.getGui().editModel(null, getStatement().getURI()));
		return textBox;
	}

	/**
	 * Gets the color.
	 *
	 * @return the color
	 */
	@Override
	Color getEditorControlBackground() {
		GamaUIColor color = GamaColors.get(getStatement().getColor(getScope()));
		if (color == null) { color = IGamaColors.NEUTRAL; }
		return color.color();
	}

}
