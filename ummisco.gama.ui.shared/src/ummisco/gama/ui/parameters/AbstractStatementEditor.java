package ummisco.gama.ui.parameters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import ummisco.gama.ui.controls.FlatButton;
import ummisco.gama.ui.interfaces.EditorListener;

public abstract class AbstractStatementEditor<T> extends AbstractEditor<Object> {

	protected FlatButton textBox;
	T statement;

	public AbstractStatementEditor(final IScope scope, final T command, final EditorListener<Object> l) {
		super(scope, null, l);
		this.statement = command;
	}

	public T getStatement() {
		return statement;
	}

	public void setStatement(final T s) {
		statement = s;
	}

	@Override
	protected final int[] getToolItems() {
		return new int[0];
	}

	@Override
	protected final Control getEditorControl() {
		return textBox;
	}

	@Override
	protected final Object getParameterValue() throws GamaRuntimeException {
		return null;
	}

	@Override
	protected final String computeUnitLabel() {
		return "";
	}

	@Override
	protected final GridData getParameterGridData() {
		final GridData d = new GridData(SWT.FILL, SWT.TOP, false, false);
		d.minimumWidth = 70;
		return d;
	}

	@Override
	protected final void addToolbarHiders(final Control... c) {}

	@Override
	protected final void displayParameterValue() {}

}
