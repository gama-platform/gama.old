/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.parameters;

import msi.gama.common.util.StringUtils;
import msi.gama.gui.swt.SwtGui;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public abstract class NumberEditor extends AbstractEditor {

	ExpressionControl expression;
	ToolItem plus, minus;
	Number stepValue;
	boolean acceptNull = false;
	Button defineButton;
	Composite internalComposite;

	public NumberEditor(final IParameter param, final boolean canBeNull) {
		super(param);
		computeStepValue();
		acceptNull = canBeNull;
	}

	public NumberEditor(final SupportParameter supportParameter, final EditorListener whenModified,
		final boolean canBeNull) {
		super(supportParameter, whenModified);
		computeStepValue();
		acceptNull = canBeNull;
	}

	public NumberEditor(final IAgent a, final IParameter p, final EditorListener l,
		final boolean canBeNull) {
		super(a, p, l);
		computeStepValue();
		acceptNull = canBeNull;
	}

	@Override
	protected Control createCustomParameterControl(final Composite composite)
		throws GamaRuntimeException {
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
			defineButton.setText(originalValue != null ? "Define:" : "Not defined");
			defineButton.setSelection(originalValue != null);
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

					} else {
						defineButton.setText("Not defined");
						expression.getControl().setEnabled(false);
						defineButton.pack();
						internalComposite.layout();
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
	public boolean acceptTooltip() {
		return false;
	}

	@Override
	public Control getEditorControl() {
		return expression.getControl();
	}

	protected void createToolbar(final Composite compo) {
		ToolBar comp = new ToolBar(compo, SWT.HORIZONTAL);
		minus = new ToolItem(comp, SWT.PUSH);
		minus.setText("");
		minus.setImage(SwtGui.collapse);
		plus = new ToolItem(comp, SWT.PUSH);
		plus.setText("");
		plus.setImage(SwtGui.expand);

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
