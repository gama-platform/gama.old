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

import msi.gama.common.interfaces.EditorListener;
import msi.gama.common.util.StringUtils;
import msi.gama.gui.swt.IGamaIcons;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public abstract class NumberEditor extends AbstractEditor {

	private ExpressionControl expression;
	ToolItem plus, minus;
	Number stepValue;
	private Button defineButton;
	private Composite internalComposite;

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
	protected Control createCustomParameterControl(final Composite composite) throws GamaRuntimeException {
		normalizeValues();
		Composite compo = new Composite(composite, SWT.None);
		compo.setLayoutData(getParameterGridData());
		GridLayout layout = new GridLayout(2, true);
		layout.verticalSpacing = 0;
		layout.marginHeight = 1;
		layout.marginWidth = 1;
		compo.setLayout(layout);
		internalComposite = compo;

		if ( acceptNull ) {
			internalComposite = new Composite(composite, SWT.None);
			layout = new GridLayout(3, false);
			layout.verticalSpacing = 0;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			internalComposite.setLayout(layout);
			defineButton = new Button(internalComposite, SWT.CHECK);
			Object originalValue = getOriginalValue();
			boolean selected = param.isDefined() && originalValue != null;
			defineButton.setSelection(selected);
			defineButton.setText(selected ? "Define:" : "Not defined");
			defineButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
			defineButton.pack();
			defineButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					if ( defineButton.getSelection() ) {
						defineButton.setText("Define:");
						expression.getControl().setEnabled(true);
						defineButton.pack();
						internalComposite.layout();
						expression.widgetDefaultSelected(null);
						param.setDefined(true);
						modifyValue(expression.currentValue);
					} else {
						defineButton.setText("Not defined");
						expression.getControl().setEnabled(false);
						defineButton.pack();
						internalComposite.layout();
						param.setDefined(false);
						modifyValue(null);
					}
				}

			});
		}

		expression = new ExpressionControl(internalComposite, this);
		createToolbar(internalComposite);
		return expression.getControl();
	}

	protected abstract Number normalizeValues() throws GamaRuntimeException;

	protected abstract void computeStepValue();

	@Override
	protected void displayParameterValue() {
		expression.getControl().setText(StringUtils.toGaml(currentValue));
		checkButtons();
	}

	protected abstract void checkButtons();

	@Override
	public Control getEditorControl() {
		if ( expression == null ) { return null; }
		return expression.getControl();
	}

	protected void createToolbar(final Composite compo) {
		ToolBar comp = new ToolBar(compo, SWT.HORIZONTAL);
		minus = new ToolItem(comp, SWT.PUSH);
		minus.setText("");
		minus.setImage(IGamaIcons.SMALL_MINUS.image());
		plus = new ToolItem(comp, SWT.PUSH);
		plus.setText("");
		plus.setImage(IGamaIcons.SMALL_PLUS.image());

		comp.addFocusListener(expression);

		plus.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				expression.setFocus();
				modifyAndDisplayValue(applyPlus());
			}
		});

		minus.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				expression.setFocus();
				modifyAndDisplayValue(applyMinus());
			}
		});
	}

	protected abstract Object applyPlus();

	protected abstract Object applyMinus();

	@Override
	public String getTooltipText() {
		return super.getTooltipText() + Text.DELIMITER + "step: " + stepValue;
	}

}
