package ummisco.gama.ui.parameters;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import msi.gama.common.util.StringUtils;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import ummisco.gama.ui.controls.IPositionChangeListener;
import ummisco.gama.ui.controls.SimpleSlider;
import ummisco.gama.ui.interfaces.EditorListener;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;

/**
 * A slider for choosing values between a max and a min, with an optional step
 * 
 * @author drogoul
 *
 */
public abstract class SliderEditor<T extends Number> extends AbstractEditor {

	final protected int nbInts;
	final DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);

	private static final int[] ITEMS = new int[] { REVERT };

	public static class Int extends SliderEditor<Integer> {

		public Int(final IScope scope, final IAgent a, final IParameter variable, final EditorListener l) {
			super(scope, a, variable, l);
			formatter.setMaximumFractionDigits(0);
			formatter.setMinimumFractionDigits(0);
			formatter.setMaximumIntegerDigits(nbInts);
			formatter.setMinimumIntegerDigits(nbInts);
			formatter.setGroupingUsed(false);
			stepValue = getStep();
			if (stepValue == null)
				stepValue = 1;
		}

		@Override
		protected Integer computeValue(final double position) {
			return (int) (minValue.intValue() + Math.round(position * (maxValue.intValue() - minValue.intValue())));
		}
	}

	public static class Float extends SliderEditor<Double> {

		final int nbFracs;

		public Float(final IScope scope, final IAgent a, final IParameter variable, final EditorListener l) {
			super(scope, a, variable, l);
			stepValue = getStep();
			if (stepValue == null) {
				stepValue = (maxValue.doubleValue() - minValue.doubleValue()) / 100d;
			}
			formatter.setMaximumIntegerDigits(nbInts);
			formatter.setMinimumIntegerDigits(nbInts);
			nbFracs = String.valueOf(stepValue).split("\\.")[1].length();
			formatter.setMaximumFractionDigits(nbFracs);
			formatter.setMinimumFractionDigits(nbFracs);
			formatter.setGroupingUsed(false);
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
		final int minChars = String.valueOf(minValue.intValue()).length();
		final int maxChars = String.valueOf(maxValue.intValue()).length();
		nbInts = Math.max(minChars, maxChars);

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
				GamaIcons.create("small.slider2").image(), false) {
		};

		if (stepValue != null) {
			final Double realStep = stepValue.doubleValue() / maxValue.doubleValue() - minValue.doubleValue();
			slider.setStep(realStep);
		}

		slider.addPositionChangeListener(new IPositionChangeListener() {

			@Override
			public void positionChanged(final double position) {
				modifyAndDisplayValue(computeValue(position));
			}

		});
		slider.pack(true);
		return slider;
	}

	String truncateCurrentValue() {
		final String s = formatter.format(currentValue);
		// final char[] array = s.toCharArray();
		// int pos = 0;
		// while (array[pos] == '0') {
		// array[pos] = ' ';
		// pos++;
		// }
		// return String.valueOf(array);
		return s;
	}

	@Override
	protected String computeUnitLabel() {
		final StringBuilder sb = new StringBuilder();
		sb.append(truncateCurrentValue()).append(' ');
		sb.append('[').append(StringUtils.toGaml(minValue, false)).append("..")
				.append(StringUtils.toGaml(maxValue, false)).append(']');
		if (stepValue != null) {
			sb.append(" every ").append(stepValue);
		}
		// final String u = param.getUnitLabel(getScope());
		// if (u != null) {
		// sb.append(" ").append(u);
		// }
		return sb.toString();
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
	protected void addToolbarHiders(final Control... c) {
	}

	protected abstract T computeValue(final double position);

	@Override
	protected void displayParameterValue() {
		final T p = (T) currentValue;
		final double position = (p.doubleValue() - minValue.doubleValue())
				/ (maxValue.doubleValue() - minValue.doubleValue());
		slider.updateSlider(position, false);
		unitItem.setText(computeUnitLabel());
		composite.layout();
	}

}
