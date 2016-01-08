/*********************************************************************************************
 *
 *
 * 'NumberEditor.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.parameters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import msi.gama.common.interfaces.EditorListener;
import msi.gama.gui.swt.GamaIcons;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public abstract class NumberEditor<T extends Number> extends ExpressionBasedEditor<T> {

	Number stepValue;
	static final String UNDEFINED_LABEL = "-- Undefined --";

	public NumberEditor(final IParameter param, final boolean canBeNull) {
		super(param);
		computeStepValue();
		acceptNull = canBeNull;
	}

	public NumberEditor(final InputParameter supportParameter, final EditorListener whenModified,
		final boolean canBeNull) {
		super(supportParameter, whenModified);
		computeStepValue();
		acceptNull = canBeNull;
	}

	public NumberEditor(final IAgent a, final IParameter p, final EditorListener l, final boolean canBeNull) {
		super(a, p, l);
		computeStepValue();
		acceptNull = canBeNull;
	}

	@Override
	public Control createCustomParameterControl(final Composite composite) throws GamaRuntimeException {
		normalizeValues();
		return super.createCustomParameterControl(composite);
	}

	protected abstract Number normalizeValues() throws GamaRuntimeException;

	protected abstract void computeStepValue();

	@Override
	protected void checkButtons() {
		super.checkButtons();
		ToolItem t = items[DEFINE];
		if ( t == null || t.isDisposed() ) { return; }
		if ( param.isDefined() ) {
			t.setToolTipText("Set the parameter to undefined");
			t.setImage(GamaIcons.create("small.undefine").image());
			getEditorControl().setEnabled(true);
		} else {
			t.setToolTipText("Define the parameter (currently undefined)");
			t.setImage(GamaIcons.create("small.define").image());
			getEditorControl().setEnabled(false);
		}
	}

	@Override
	protected void applyDefine() {
		if ( param.isDefined() ) {
			param.setDefined(false);
			internalModification = true;
			getEditorControl().setText(UNDEFINED_LABEL);
			internalModification = false;
			modifyValue(null);
		} else {
			param.setDefined(true);
			internalModification = true;
			expression.modifyValue();
			internalModification = false;
		}
		checkButtons();
	}

	@Override
	protected ToolItem createPlusItem(final ToolBar t) {
		ToolItem item = super.createPlusItem(t);
		ToolItem unitItem = new ToolItem(t, SWT.READ_ONLY | SWT.FLAT);
		unitItem.setText(String.valueOf(stepValue));
		unitItem.setEnabled(false);
		return item;
	}

	@Override
	protected int[] getToolItems() {
		if ( acceptNull ) { return new int[] { DEFINE, PLUS, MINUS, REVERT }; }
		return new int[] { PLUS, MINUS, REVERT };
	}
}
