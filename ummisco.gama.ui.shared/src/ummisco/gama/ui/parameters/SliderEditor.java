/*********************************************************************************************
 *
 * 'SliderEditor.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.parameters;

import static msi.gama.application.workbench.ThemeHelper.isDark;
import static ummisco.gama.ui.resources.GamaColors.get;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gaml.operators.Cast;
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
public abstract class SliderEditor<T extends Comparable> extends AbstractEditor<T> {

	final protected int nbInts;
	final DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);

	private static final int[] ITEMS = { VALUE, REVERT };

	public static class Int extends SliderEditor<Integer> {

		public Int(final IScope scope, final IAgent a, final IParameter variable, final EditorListener<Integer> l) {
			super(scope, a, variable, l);
			formatter.setMaximumFractionDigits(0);
			formatter.setMinimumFractionDigits(0);
			formatter.setMaximumIntegerDigits(nbInts);
			formatter.setMinimumIntegerDigits(nbInts);
			formatter.setGroupingUsed(false);
			stepValue = getStep();
			if (stepValue == null) { stepValue = 1; }
		}

		@Override
		protected Integer computeValue(final double position) {
			return (int) (Cast.asInt(getScope(), getMinValue()) + Math
					.round(position * (Cast.asInt(getScope(), getMaxValue()) - Cast.asInt(getScope(), getMinValue()))));
		}
	}

	public static class Float extends SliderEditor<Double> {

		final int nbFracs;

		public Float(final IScope scope, final IAgent a, final IParameter variable, final EditorListener<Double> l) {
			super(scope, a, variable, l);
			stepValue = getStep();
			if (stepValue == null) {
				stepValue = (Cast.asFloat(scope, getMaxValue()) - Cast.asFloat(scope, getMinValue())) / 100d;
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
			return Cast.asFloat(getScope(), getMinValue())
					+ position * (Cast.asFloat(getScope(), getMaxValue()) - Cast.asFloat(getScope(), getMinValue()));
		}

	}

	SimpleSlider slider;
	Number stepValue;

	public SliderEditor(final IScope scope, final IAgent a, final IParameter variable, final EditorListener<T> l) {
		super(scope, a, variable, l);
		final int minChars = String.valueOf(Cast.asInt(getScope(), getMinValue())).length();
		final int maxChars = String.valueOf(Cast.asInt(getScope(), getMaxValue())).length();
		nbInts = Math.max(minChars, maxChars);
	}

	@Override
	protected int[] getToolItems() {
		return ITEMS;
	}

	@SuppressWarnings ("unchecked")
	protected T getStep() {
		return (T) param.getStepValue(getScope());
	}

	@Override
	protected Control getEditorControl() {
		return slider;
	}

	@Override
	protected Control createCustomParameterControl(final Composite comp) throws GamaRuntimeException {
		final List<GamaColor> colors = getParam().getColor(getScope());
		Color left = IGamaColors.OK.color();
		Color backgroundColor = comp.getBackground();
		Color right = isDark() ? get(backgroundColor).lighter() : get(backgroundColor).darker();
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
			final Double realStep = stepValue.doubleValue()
					/ (Cast.asFloat(getScope(), getMaxValue()) - Cast.asFloat(getScope(), getMinValue()));
			slider.setStep(realStep);
		}

		slider.addPositionChangeListener((s, position) -> modifyAndDisplayValue(computeValue(position)));
		slider.pack(true);
		return slider;
	}

	String truncateCurrentValue() {
		// Avoids invisible exception (see #2044)
		if (currentValue == null) return formatter.format(0);
		return formatter.format(currentValue);
	}

	@Override
	protected GridData getParameterGridData() {
		final GridData result = super.getParameterGridData();
		result.heightHint = 24;
		return result;
	}

	@Override
	public void createComposite(final Composite comp) {
		super.createComposite(comp);
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

	protected abstract T computeValue(final double position);

	@Override
	protected void displayParameterValue() {
		final T p = currentValue;
		final double position = (Cast.asFloat(getScope(), p) - Cast.asFloat(getScope(), getMinValue()))
				/ (Cast.asFloat(getScope(), getMaxValue()) - Cast.asFloat(getScope(), getMinValue()));
		slider.updateSlider(position, false);
		items[VALUE].setText(truncateCurrentValue());
		composite.layout();
	}

}
