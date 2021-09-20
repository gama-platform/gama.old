/*******************************************************************************************************
 *
 * FloatEditor.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.InputParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.ui.interfaces.EditorListener;

/**
 * The Class FloatEditor.
 */
public class FloatEditor extends NumberEditor<Double> {

	/**
	 * Instantiates a new float editor.
	 *
	 * @param scope the scope
	 * @param agent the agent
	 * @param param the param
	 * @param canBeNull the can be null
	 * @param l the l
	 */
	FloatEditor(final IScope scope, final IAgent agent, final IParameter param, final boolean canBeNull,
			final EditorListener<Double> l) {
		super(scope, agent, param, l, canBeNull);
	}

	/**
	 * Instantiates a new float editor.
	 *
	 * @param scope the scope
	 * @param parent the parent
	 * @param title the title
	 * @param value the value
	 * @param min the min
	 * @param max the max
	 * @param step the step
	 * @param canBeNull the can be null
	 * @param whenModified the when modified
	 */
	FloatEditor(final IScope scope, final EditorsGroup parent, final String title, final Double value, final Double min,
			final Double max, final Double step, final boolean canBeNull, final EditorListener<Double> whenModified) {
		// Convenience method
		super(scope, new InputParameter(title, value, min, max, step), whenModified, canBeNull);
		if (step != null) { stepValue = step; }
		this.createControls(parent);
	}

	@Override
	protected Double defaultStepValue() {
		return 0.1d;
	}

	@Override
	protected boolean modifyValue(final Object val) throws GamaRuntimeException {
		Double i = Cast.asFloat(getScope(), val);
		if (acceptNull && val == null) {
			i = null;
		} else {
			if (getMinValue() != null && i < Cast.asFloat(getScope(), getMinValue()))
				throw GamaRuntimeException.error("Value " + i + " should be greater than " + getMinValue(), getScope());
			if (getMaxValue() != null && i > Cast.asFloat(getScope(), getMaxValue()))
				throw GamaRuntimeException.error("Value " + i + " should be smaller than " + maxValue, getScope());
		}
		return super.modifyValue(i);

	}

	@Override
	protected Double normalizeValues() throws GamaRuntimeException {
		final Double valueToConsider = getOriginalValue() == null ? 0.0 : Cast.asFloat(getScope(), getOriginalValue());
		currentValue = getOriginalValue() == null ? null : valueToConsider;
		minValue = getMinValue() == null ? null : Cast.asFloat(getScope(), getMinValue());
		maxValue = maxValue == null ? null : Cast.asFloat(getScope(), getMaxValue());
		return valueToConsider;
	}

	@Override
	public IType<Double> getExpectedType() { return Types.FLOAT; }

	@Override
	protected Double applyPlus() {
		if (currentValue == null) return 0.0;
		final Double i = currentValue;
		return i + stepValue.doubleValue();
	}

	@Override
	protected Double applyMinus() {
		if (currentValue == null) return 0.0;
		final Double i = currentValue;
		return i - stepValue.doubleValue();
	}

	@Override
	protected void updateToolbar() {
		super.updateToolbar();
		editorToolbar.enable(PLUS,
				param.isDefined() && (getMaxValue() == null || applyPlus() < Cast.asFloat(getScope(), getMaxValue())));
		editorToolbar.enable(MINUS,
				param.isDefined() && (getMinValue() == null || applyMinus() > Cast.asFloat(getScope(), getMinValue())));
	}
}
