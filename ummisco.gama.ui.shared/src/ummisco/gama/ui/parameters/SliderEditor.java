/*********************************************************************************************
 *
 * 'SliderEditor.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.parameters;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import msi.gama.common.util.StringUtils;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import ummisco.gama.ui.controls.SimpleSlider;
import ummisco.gama.ui.interfaces.EditorListener;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.IGamaColors;

/**
 * A slider for choosing values between a max and a min, with an optional step
 *
 * @author drogoul
 *
 */
public abstract class SliderEditor<T extends Number> extends AbstractEditor<T> {

	final protected int nbInts;
	final DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);

	private static final int[] ITEMS = new int[] { REVERT };

	public static class Int extends SliderEditor<Integer> {

		public Int(final IScope scope, final IAgent a, final IParameter variable, final EditorListener<Integer> l) {
			super(scope, a, variable, l);
			formatter.setMaximumFractionDigits(0);
			formatter.setMinimumFractionDigits(0);
			formatter.setMaximumIntegerDigits(nbInts);
			formatter.setMinimumIntegerDigits(nbInts);
			formatter.setGroupingUsed(false);
			stepValue = getStep();
			if (stepValue == null) {
				stepValue = 1;
			}
		}

		@Override
		protected Integer computeValue(final double position) {
			return (int) (minValue.intValue() + Math.round(position * (maxValue.intValue() - minValue.intValue())));
		}
	}

	public static class Float extends SliderEditor<Double> {

		final int nbFracs;

		public Float(final IScope scope, final IAgent a, final IParameter variable, final EditorListener<Double> l) {
			super(scope, a, variable, l);
			stepValue = getStep();
			if (stepValue == null) {
				stepValue = (maxValue.doubleValue() - minValue.doubleValue()) / 100d;
			}
			formatter.setMaximumIntegerDigits(nbInts);
			formatter.setMinimumIntegerDigits(nbInts);
			final String[] segments = String.valueOf(stepValue).split("\\.");
			if (segments.length > 1) {
				nbFracs = segments[1].length();
			} else {
				nbFracs = 1;
			}
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
	Number stepValue;

	public SliderEditor(final IScope scope, final IAgent a, final IParameter variable, final EditorListener<T> l) {
		super(scope, a, variable, l);
		final int minChars = String.valueOf(minValue.intValue()).length();
		final int maxChars = String.valueOf(maxValue.intValue()).length();
		nbInts = Math.max(minChars, maxChars);
	}

	@Override
	protected int[] getToolItems() {
		return ITEMS;
	}

	@SuppressWarnings ("unchecked")
	protected Number getStep() {
		return param.getStepValue(getScope());
	}

	@Override
	protected Control getEditorControl() {
		return slider;
	}

	@Override
	protected Control createCustomParameterControl(final Composite comp) throws GamaRuntimeException {
		final List<GamaColor> colors = getParam().getColor(getScope());
		Color left = IGamaColors.OK.color();
		Color right = IGamaColors.GRAY_LABEL.lighter();
		Color thumb = left;
		if (colors != null) {
			if (colors.size() == 1) {
				left = thumb = GamaColors.get(colors.get(0)).color();
			} else if (colors.size() == 2) {
				left = GamaColors.get(colors.get(0)).color();
				right = GamaColors.get(colors.get(1)).color();
			} else if (colors.size() >= 3) {
				left = GamaColors.get(colors.get(0)).color();
				thumb = GamaColors.get(colors.get(1)).color();
				right = GamaColors.get(colors.get(2)).color();
			}
		}
		slider = new SimpleSlider(comp, left, right, thumb, false) {};

		if (stepValue != null) {
			final Double realStep = stepValue.doubleValue() / (maxValue.doubleValue() - minValue.doubleValue());
			slider.setStep(realStep);
		}
		slider.setInteger(this instanceof Int);

		slider.addPositionChangeListener((s, position) -> modifyAndDisplayValue(computeValue(position)));
		slider.pack(true);
		return slider;
	}

	String truncateCurrentValue() {
		// Avoids invisible exception (see #2044)
		if (currentValue == null) { return formatter.format(0); }
		final String s = formatter.format(currentValue);
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
	protected Composite createToolbar2() {
		final Composite t = super.createToolbar2();
		t.setBackground(getNormalBackground());
		for (final Control c : t.getChildren()) {
			c.setBackground(getNormalBackground());
		}
		return t;
	}

	@Override
	protected void addToolbarHiders(final Control... c) {}

	protected abstract T computeValue(final double position);

	@Override
	protected void displayParameterValue() {
		final T p = currentValue;
		final double position =
				(p.doubleValue() - minValue.doubleValue()) / (maxValue.doubleValue() - minValue.doubleValue());
		slider.updateSlider(position, false);
		unitItem.setText(computeUnitLabel());
		composite.layout();
	}

}
