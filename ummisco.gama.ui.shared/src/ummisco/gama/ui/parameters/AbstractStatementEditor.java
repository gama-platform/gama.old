package ummisco.gama.ui.parameters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import ummisco.gama.ui.controls.FlatButton;
import ummisco.gama.ui.interfaces.EditorListener;
import ummisco.gama.ui.resources.GamaFonts;

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
	protected String computeUnitLabel() {
		return "";
	}

	@Override
	public void createComposite(final Composite parent) {
		this.parent = parent;
		internalModification = true;
		if (!isSubParameter) {
			titleLabel = createLeftLabel(parent, name);
		} else {
			createLeftLabel(parent, " ");
		}
		currentValue = getOriginalValue();
		composite = new Composite(parent, SWT.NONE);
		composite.setBackground(parent.getBackground());
		final GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.minimumWidth = 150;
		composite.setLayoutData(data);
		final GridLayout layout = new GridLayout(isSubParameter ? 3 : 2, false);
		layout.marginWidth = 5;
		composite.setLayout(layout);
		createEditorControl(composite);
		if (isSubParameter) {
			titleLabel = createLeftLabel(composite, name);
			titleLabel.setFont(GamaFonts.getNavigFolderFont());
			final GridData d = new GridData(SWT.LEAD, SWT.CENTER, true, false);
			titleLabel.setLayoutData(d);
		}

		internalModification = false;
		if (isSubParameter) {
			titleLabel.setBackground(HOVERED_BACKGROUND);
			composite.setBackground(HOVERED_BACKGROUND);
		}
		composite.layout();
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
