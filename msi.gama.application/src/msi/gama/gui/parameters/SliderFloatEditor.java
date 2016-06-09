package msi.gama.gui.parameters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import msi.gama.common.interfaces.EditorListener;
import msi.gama.common.util.StringUtils;
import msi.gama.gui.swt.GamaIcons;
import msi.gama.gui.swt.IGamaColors;
import msi.gama.gui.swt.controls.IPositionChangeListener;
import msi.gama.gui.swt.controls.SimpleSlider;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Cast;

public class SliderFloatEditor extends AbstractEditor {

	private static final int[] NULL = new int[0];
	SimpleSlider slider;
	Double stepValue;

	public SliderFloatEditor(final IScope scope, final IAgent a, final IParameter variable, final EditorListener l) {
		super(scope, a, variable);
	}

	@Override
	protected int[] getToolItems() {
		// TODO Auto-generated method stub
		return NULL;
	}

	@Override
	protected Control getEditorControl() {
		return slider;
	}

	@Override
	protected Control createCustomParameterControl(final Composite composite) throws GamaRuntimeException {

		slider = new SimpleSlider(composite, IGamaColors.OK.color(), IGamaColors.GRAY_LABEL.lighter(),
			GamaIcons.create("small.slider2").image(), false) {

		};
		final Number step = param.getStepValue(getScope());
		if ( step != null ) {
			stepValue = Cast.asFloat(getScope(), step);
			final Double min = Cast.asFloat(getScope(), minValue);
			final Double max = Cast.asFloat(getScope(), maxValue);
			final Double realStep = stepValue.doubleValue() / max.doubleValue() - min.doubleValue();
			slider.setStep(realStep);
		}

		slider.addPositionChangeListener(new IPositionChangeListener() {

			@Override
			public void positionChanged(final double position) {
				modifyValue(computeValue(position));
				unitItem.setText(computeUnitLabel());
				toolbar.layout();
				toolbar.pack();
			}

		});
		slider.pack(true);
		return slider;
	}

	@Override
	protected String computeUnitLabel() {
		String s = StringUtils.toGaml(this.currentValue, false);
		s += " [" + StringUtils.toGaml(minValue, false) + ".." + StringUtils.toGaml(maxValue, false) + "]";
		if ( stepValue != null ) {
			s += " every " + stepValue;
		}
		final String u = param.getUnitLabel(getScope());
		if ( u != null ) {
			s += " " + u;
		}
		return s;
	}

	@Override
	protected GridData getParameterGridData() {
		final GridData result = super.getParameterGridData();
		result.heightHint = 24;
		return result;
	}

	@Override
	protected void hideToolbar() {
		final GridData data = new GridData(SWT.FILL, SWT.BOTTOM, false, false);
		toolbar.setLayoutData(data);
		toolbar.layout();
	}

	@Override
	protected void addToolbarHiders(final Control ... c) {}

	private double computeValue(final double position) {
		return minValue.doubleValue() + position * (maxValue.doubleValue() - minValue.doubleValue());
	}

	@Override
	protected void displayParameterValue() {
		final double p = (double) this.getParameterValue();
		final double position = (p - minValue.doubleValue()) / (maxValue.doubleValue() - minValue.doubleValue());
		slider.updateSlider(position, false);

	}

}
