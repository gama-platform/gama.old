package ummisco.gama.ui.parameters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
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

public class CommandEditor extends AbstractEditor<Object> {

	FlatButton textBox;
	final UserCommandStatement command;

	public CommandEditor(final IScope scope, final UserCommandStatement command, final EditorListener.Command l) {
		super(scope, null, l);
		this.command = command;
	}

	@Override
	protected EditorListener.Command getListener() {
		return (Command) super.getListener();
	}

	@Override
	protected int[] getToolItems() {
		return new int[0];
	}

	@Override
	protected Control getEditorControl() {
		return textBox;
	}

	@Override
	protected Object getParameterValue() throws GamaRuntimeException {
		return null;
	}

	@Override
	protected String computeUnitLabel() {
		return "";
	}

	@Override
	protected GridData getParameterGridData() {
		final GridData d = new GridData(SWT.FILL, SWT.TOP, false, false);

		d.minimumWidth = 70;
		// d.widthHint = 100; // SWT.DEFAULT
		return d;
	}

	@Override
	protected void addToolbarHiders(final Control... c) {}

	@Override
	protected Control createCustomParameterControl(final Composite composite) throws GamaRuntimeException {
		GamaUIColor color = GamaColors.get(command.getColor(getScope()));
		if (color == null)
			color = IGamaColors.NEUTRAL;
		textBox = FlatButton.button(composite, color, "").light().small();
		textBox.setText(command.getName() + "  ");
		textBox.addSelectionListener(getListener());
		return textBox;

	}

	@Override
	protected void displayParameterValue() {}

}
