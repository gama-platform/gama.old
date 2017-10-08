package ummisco.gama.ui.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.statements.test.AssertStatement;
import ummisco.gama.ui.controls.FlatButton;
import ummisco.gama.ui.interfaces.EditorListener;
import ummisco.gama.ui.parameters.AbstractStatementEditor;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.IGamaColors;

public class AssertEditor extends AbstractStatementEditor<AssertStatement> {

	public AssertEditor(final IScope scope, final AssertStatement command) {
		super(scope, command, (EditorListener<Object>) null);
		name = command.getFacet(IKeyword.VALUE).serialize(true);
	}

	@Override
	protected Control createCustomParameterControl(final Composite composite) throws GamaRuntimeException {
		return FlatButton.button(composite, getColor(), getText()).small().disabled();
	}

	private GamaUIColor getColor() {
		GamaUIColor color = GamaColors.get(TestView.getItemDisplayColor(getStatement().getState()));
		if (color == null)
			color = IGamaColors.NEUTRAL;
		return color;
	}

	private String getText() {
		return getStatement().getState().toString() + "  ";
	}

}
