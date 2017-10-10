package ummisco.gama.ui.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jgrapht.alg.util.Pair;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.statements.test.TestStatement;
import msi.gaml.statements.test.TestStatement.State;
import ummisco.gama.ui.controls.FlatButton;
import ummisco.gama.ui.interfaces.EditorListener;
import ummisco.gama.ui.parameters.AbstractStatementEditor;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.IGamaColors;

public class AssertEditor extends AbstractStatementEditor<Pair<String, TestStatement.State>> {

	public AssertEditor(final IScope scope, final Pair<String, TestStatement.State> command) {
		super(scope, command, (EditorListener<Object>) null);
		name = command.getFirst();
	}

	@Override
	protected Control createCustomParameterControl(final Composite composite) throws GamaRuntimeException {
		textBox = FlatButton.button(composite, getColor(), getText()).small().disabled();
		return textBox;
	}

	private GamaUIColor getColor() {
		GamaUIColor color = GamaColors.get(getStatement().getSecond().getColor());
		if (color == null)
			color = IGamaColors.NEUTRAL;
		return color;
	}

	private String getText() {
		return getStatement().getSecond().toString() + "  ";
	}

	public void updateValueWith(final Pair<String, State> assertion) {
		setStatement(assertion);
		textBox.setText(getText());
		textBox.setColor(getColor());
		textBox.redraw();
		textBox.update();
	}

}
