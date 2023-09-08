/*******************************************************************************************************
 *
 * CommandEditor.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gaml.statements.UserCommandStatement;
import ummisco.gama.ui.controls.FlatButton;
import ummisco.gama.ui.interfaces.EditorListener;
import ummisco.gama.ui.interfaces.EditorListener.Command;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.IGamaColors;

/**
 * The Class CommandEditor.
 */
public class CommandEditor extends AbstractStatementEditor<UserCommandStatement> {

	/**
	 * Instantiates a new command editor.
	 *
	 * @param scope
	 *            the scope
	 * @param command
	 *            the command
	 * @param l
	 *            the l
	 */
	public CommandEditor(final IScope scope, final UserCommandStatement command, final EditorListener.Command l) {
		super(scope, command, l);
	}

	@Override
	protected EditorListener.Command getListener() { return (Command) super.getListener(); }

	@Override
	protected FlatButton createCustomParameterControl(final Composite composite) throws GamaRuntimeException {
		textBox = FlatButton.button(composite, null, "");
		textBox.setText(" " + getStatement().getName());
		textBox.addSelectionListener(getListener());
		return textBox;
	}

	@Override
	Color getEditorControlBackground() {
		GamaColor color = getStatement().getColor(getScope());
		return color == null ? IGamaColors.NEUTRAL.color() : GamaColors.get(color).color();
	}

	@Override
	EditorLabel createEditorLabel() {
		return new EditorLabel(this, parent, " ", isSubParameter);
	}

}
