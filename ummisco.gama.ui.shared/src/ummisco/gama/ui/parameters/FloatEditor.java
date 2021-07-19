/*********************************************************************************************
 *
 * 'FloatEditor.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.parameters;

import org.eclipse.swt.widgets.Composite;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.InputParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.ui.interfaces.EditorListener;

public class FloatEditor extends NumberEditor<Double> {

	FloatEditor(final IScope scope, final IAgent agent, final IParameter param, final boolean canBeNull,
			final EditorListener<Double> l) {
		super(scope, agent, param, l, canBeNull);
	}

	FloatEditor(final IScope scope, final Composite parent, final String title, final Double value, final Double min,
			final Double max, final Double step, final boolean canBeNull, final EditorListener<Double> whenModified) {
		// Convenience method
		super(scope, new InputParameter(title, value, min, max, step), whenModified, canBeNull);
		if (step != null) { stepValue = step; }
		this.createComposite(parent);
	}

	@Override
	protected void computeStepValue() {
		super.computeStepValue();
		if (stepValue == null) { stepValue = 0.1; }
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

	// @Override
	// protected void setOriginalValue(final Double val) {
	// // if ( acceptNull && val == null ) {
	// // super.setOriginalValue(val);
	// // }
	// super.setOriginalValue(val);
	// }

	@Override
	protected Double normalizeValues() throws GamaRuntimeException {
		final Double valueToConsider = getOriginalValue() == null ? 0.0 : Cast.asFloat(getScope(), getOriginalValue());
		currentValue = getOriginalValue() == null ? null : valueToConsider;
		minValue = getMinValue() == null ? null : Cast.asFloat(getScope(), getMinValue());
		maxValue = maxValue == null ? null : Cast.asFloat(getScope(), getMaxValue());
		return valueToConsider;
	}

	@Override
	public IType<Double> getExpectedType() {
		return Types.FLOAT;
	}

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
		toolbar.enable(PLUS,
				param.isDefined() && (getMaxValue() == null || applyPlus() < Cast.asFloat(getScope(), getMaxValue())));
		toolbar.enable(MINUS,
				param.isDefined() && (getMinValue() == null || applyMinus() > Cast.asFloat(getScope(), getMinValue())));
	}
}
