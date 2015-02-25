/*********************************************************************************************
 * 
 * 
 * 'BooleanEditor.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.parameters;

import msi.gama.common.interfaces.EditorListener;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gaml.types.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;

public class BooleanEditor extends AbstractEditor {

	private Button button;

	BooleanEditor(final IParameter param) {
		super(param);
		acceptNull = false;
	}

	BooleanEditor(final Composite parent, final String title, final boolean value,
		final EditorListener<Boolean> whenModified) {
		super(new InputParameter(title, value), whenModified);
		acceptNull = false;
		this.createComposite(parent);
	}

	BooleanEditor(final IAgent agent, final IParameter param) {
		this(agent, param, null);
	}

	BooleanEditor(final IAgent agent, final IParameter param, final EditorListener l) {
		super(agent, param, l);
		acceptNull = false;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		if ( !internalModification ) {
			modifyAndDisplayValue(button.getSelection());
		}
	}

	@Override
	public Control createCustomParameterControl(final Composite comp) {
		button = new Button(comp, SWT.CHECK);
		// button.setLayoutData(this.getParameterGridData());
		button.addSelectionListener(this);
		return button;
	}

	@Override
	protected void displayParameterValue() {
		Boolean b = (Boolean) currentValue;
		if ( b == null ) {
			b = false;
		}
		button.setText(b ? "true" : "false");
		button.setSelection(b);
	}

	@Override
	public Control getEditorControl() {
		return button;
	}

	@Override
	public IType getExpectedType() {
		return Types.BOOL;
	}

	@Override
	protected int[] getToolItems() {
		return new int[] { REVERT };
	}

}
