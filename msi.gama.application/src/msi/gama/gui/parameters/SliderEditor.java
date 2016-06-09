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

/**
 * A slider for choosing values between a max and a min, with an optional step
 * @author drogoul
 *
 */
public abstract class SliderEditor<T extends Number> extends AbstractEditor {

	private static final int[] ITEMS = new int[] { REVERT };

	public static class Int extends SliderEditor<Integer> {

		public Int(final IScope scope, final IAgent a, final IParameter variable, final EditorListener l) {
			super(scope, a, variable, l);
		}

		@Override
		protected Integer computeValue(final double position) {
			return (int) (minValue.intValue() + Math.round(position * (maxValue.intValue() - minValue.intValue())));
		}
	}

	public static class Float extends SliderEditor<Double> {

		public Float(final IScope scope, final IAgent a, final IParameter variable, final EditorListener l) {
			super(scope, a, variable, l);
		}

		@Override
		protected Double computeValue(final double position) {
			return minValue.doubleValue() + position * (maxValue.doubleValue() - minValue.doubleValue());
		}

	}

	SimpleSlider slider;
	T stepValue;

	public SliderEditor(final IScope scope, final IAgent a, final IParameter variable, final EditorListener l) {
		super(scope, a, variable);
	}

	@Override
	protected int[] getToolItems() {
		return ITEMS;
	}

	protected T getStep() {
		return (T) param.getStepValue(getScope());
	}

	@Override
	protected Control getEditorControl() {
		return slider;
	}

	@Override
	protected Control createCustomParameterControl(final Composite composite) throws GamaRuntimeException {

		slider = new SimpleSlider(composite, IGamaColors.OK.color(), IGamaColors.GRAY_LABEL.lighter(),
			GamaIcons.create("small.slider2").image(), false) {};
		stepValue = getStep();
		if ( stepValue != null ) {
			final Double realStep = stepValue.doubleValue() / maxValue.doubleValue() - minValue.doubleValue();
			slider.setStep(realStep);
		}

		slider.addPositionChangeListener(new IPositionChangeListener() {

			@Override
			public void positionChanged(final double position) {
				modifyAndDisplayValue(computeValue(position));
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

	protected abstract T computeValue(final double position);

	@Override
	protected void displayParameterValue() {
		final T p = (T) currentValue;
		final double position =
			(p.doubleValue() - minValue.doubleValue()) / (maxValue.doubleValue() - minValue.doubleValue());
		slider.updateSlider(position, false);

	}

}
