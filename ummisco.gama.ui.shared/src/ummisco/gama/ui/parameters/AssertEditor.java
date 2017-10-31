package ummisco.gama.ui.parameters;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;

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

public class AssertEditor extends AbstractStatementEditor<AbstractSummary<?>> {

	public AssertEditor(final IScope scope, final AbstractSummary<?> command) {
		super(scope, command, (EditorListener<Object>) null);
		isSubParameter = command instanceof AssertionSummary;
		name = command.getTitle();
	}

	@Override
	protected Control createCustomParameterControl(final Composite composite) throws GamaRuntimeException {
		textBox = FlatButton.button(composite, getColor(), getText()).small();
		textBox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				GAMA.getGui().editModel(null, getStatement().getURI());
			}
		});
		return textBox;
	}

	@Override
	protected ToolBar createToolbar() {
		return null;
	}

	private GamaUIColor getColor() {
		GamaUIColor color = GamaColors.get(getStatement().getColor());
		if (color == null)
			color = IGamaColors.NEUTRAL;
		return color;
	}

	private String getText() {
		final AbstractSummary<?> summary = getStatement();

		if (summary instanceof AssertionSummary && getStatement().getState() == TestState.ABORTED)
			return getStatement().getState().toString() + ": " + ((AssertionSummary) getStatement()).getError() + "  ";
		return getStatement().getState().toString() + "  ";
	}

	public void updateValueWith(final AbstractSummary<?> assertion) {
		setStatement(assertion);
		textBox.setText(getText());
		textBox.setColor(getColor());
		textBox.redraw();
		textBox.update();
	}

}
