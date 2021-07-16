/*********************************************************************************************
 *
 * 'PointEditor.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.parameters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import msi.gama.common.util.StringUtils;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.InputParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.ui.interfaces.EditorListener;

public class PointEditor extends AbstractEditor<ILocation> implements VerifyListener {

	private final Text[] ordinates = new Text[3];
	private static final String[] labels = { "x", "y", "z" };
	private Composite pointEditor;
	private boolean allowVerification;
	private boolean isReverting;

	PointEditor(final IScope scope, final IAgent agent, final IParameter param, final EditorListener<ILocation> l) {
		super(scope, agent, param, l);
	}

	PointEditor(final IScope scope, final Composite parent, final String title, final ILocation value,
			final EditorListener<ILocation> whenModified) {
		// Convenience method
		super(scope, new InputParameter(title, value), whenModified);
		this.createComposite(parent);
	}

	@Override
	public Control createCustomParameterControl(final Composite comp) {
		pointEditor = new Composite(comp, SWT.NONE);
		final var pointEditorLayout = new GridLayout(3, true);
		pointEditorLayout.horizontalSpacing = 5;
		pointEditorLayout.verticalSpacing = 0;
		pointEditorLayout.marginHeight = 0;
		pointEditorLayout.marginWidth = 0;
		pointEditor.setLayout(pointEditorLayout);
		// pointEditor.setBackground(IGamaColors.PARAMETERS_BACKGROUND.color());

		for (var i = 0; i < 3; i++) {
			final var xComposite = new Composite(pointEditor, SWT.NO_BACKGROUND);
			// xComposite.setBackground(IGamaColors.PARAMETERS_BACKGROUND.color());
			final var subCompositeLayout = new GridLayout(2, false);
			subCompositeLayout.marginHeight = 0;
			subCompositeLayout.marginWidth = 0;
			xComposite.setLayout(subCompositeLayout);
			final var subCompositeGridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
			xComposite.setLayoutData(subCompositeGridData);
			final var xLabel = new Label(xComposite, SWT.NONE);
			xLabel.setText(labels[i]);
			ordinates[i] = new Text(xComposite, SWT.BORDER);
			final var textGridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
			ordinates[i].setLayoutData(textGridData);
			ordinates[i].addModifyListener(this);
			ordinates[i].addVerifyListener(this);
		}

		displayParameterValue();
		return pointEditor;
	}

	@Override
	public void verifyText(final VerifyEvent event) {
		if (internalModification || !allowVerification) return;
		final var myChar = event.character;
		// Last one is for texts
		final var text = (Text) event.widget;
		final var old = text.getText();
		final var alreadyPoint = old.contains(".");
		final var atBeginning = event.start == 0;
		event.doit = Character.isDigit(myChar) || myChar == '\b' || myChar == '.' && !alreadyPoint
				|| myChar == '-' && atBeginning;
	}

	@Override
	protected void displayParameterValue() {
		allowVerification = false;
		final var p = (GamaPoint) currentValue;
		for (var i = 0; i < 3; i++) {
			if (isReverting || !ordinates[i].isFocusControl()) {
				ordinates[i].setText(currentValue == null ? "0.0" : StringUtils.toGaml(p.getOrdinate(i), false));
			}
		}
		isReverting = false;
		allowVerification = true;
	}

	@Override
	public void modifyText(final ModifyEvent me) {
		if (internalModification || !allowVerification) return;
		modifyAndDisplayValue(new GamaPoint(Cast.asFloat(getScope(), ordinates[0].getText()),
				Cast.asFloat(getScope(), ordinates[1].getText()), Cast.asFloat(getScope(), ordinates[2].getText())));

	}

	@Override
	protected boolean modifyValue(final Object val) throws GamaRuntimeException {
		GamaPoint i = Cast.asPoint(getScope(), val).toGamaPoint();
		// throw GamaRuntimeException.error("Value " + i + " should be greater than " + minValue, getScope());
		if (minValue != null && i.smallerThan(Cast.asPoint(getScope(), minValue))
				|| maxValue != null && i.biggerThan(Cast.asPoint(getScope(), maxValue)))
			return false;
		// throw GamaRuntimeException.error("Value " + i + " should be smaller than " + maxValue, getScope());
		return super.modifyValue(i);
	}

	@Override
	protected void computeStepValue() {
		super.computeStepValue();
		if (stepValue == null) { stepValue = new GamaPoint(0.1, 0.1, 0.1); }
	}

	@Override
	public Control getEditorControl() {
		return pointEditor;
	}

	@SuppressWarnings ({ "unchecked", "rawtypes" })
	@Override
	public IType getExpectedType() {
		return Types.POINT;
	}

	@Override
	protected int[] getToolItems() {
		return new int[] { PLUS, MINUS, REVERT };
	}

	@Override
	protected ILocation applyRevert() {
		isReverting = true;
		return super.applyRevert();
	}

	@Override
	protected ILocation applyPlus() {
		GamaPoint p = currentValue.toGamaPoint().clone();
		p.add(stepValue.toGamaPoint());
		return p;
	}

	@Override
	protected ILocation applyMinus() {
		GamaPoint p = currentValue.toGamaPoint().clone();
		p.subtract(stepValue.toGamaPoint());
		return p;
	}

}
