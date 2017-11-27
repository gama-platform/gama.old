package ummisco.gama.ui.parameters;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.statements.UserCommandStatement;
import ummisco.gama.ui.controls.FlatButton;
import ummisco.gama.ui.interfaces.EditorListener;
import ummisco.gama.ui.interfaces.EditorListener.Command;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.IGamaColors;

public class CommandEditor extends AbstractStatementEditor<UserCommandStatement> {

	public CommandEditor(final IScope scope, final UserCommandStatement command, final EditorListener.Command l) {
		super(scope, command, l);
	}

	@Override
	protected EditorListener.Command getListener() {
		return (Command) super.getListener();
	}

	@Override
	protected Control createCustomParameterControl(final Composite composite) throws GamaRuntimeException {
		GamaUIColor color = GamaColors.get(getStatement().getColor(getScope()));
		if (color == null)
			color = IGamaColors.NEUTRAL;
		textBox = FlatButton.button(composite, color, "").light().small();
		textBox.setText(getStatement().getName() + "  ");
		textBox.addSelectionListener(getListener());
		return textBox;

	}

}
