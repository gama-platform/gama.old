/*********************************************************************************************
 *
 * 'FloatEditor.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.parameters;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolItem;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.ui.interfaces.EditorListener;

public class FloatEditor extends NumberEditor<Double> {

	FloatEditor(final IScope scope, final IParameter param, final boolean canBeNull) {
		super(scope, param, canBeNull);
	}

	FloatEditor(final IScope scope, final IAgent agent, final IParameter param, final boolean canBeNull) {
		this(scope, agent, param, canBeNull, null);
	}

	FloatEditor(final IScope scope, final IAgent agent, final IParameter param, final boolean canBeNull,
			final EditorListener<Double> l) {
		super(scope, agent, param, l, canBeNull);
	}

	FloatEditor(final IScope scope, final Composite parent, final String title, final Double value, final Double min,
			final Double max, final Double step, final boolean canBeNull, final EditorListener<Double> whenModified) {
		// Convenience method
		super(scope, new InputParameter(title, value, min, max), whenModified, canBeNull);
		this.createComposite(parent);
	}

	@Override
	protected void computeStepValue() {
		stepValue = param.getStepValue(getScope());
		if (stepValue == null) {
			stepValue = 0.1;
		}
	}

	@Override
	protected void modifyValue(final Double val) throws GamaRuntimeException {
		Double i = Cast.asFloat(getScope(), val);
		if (acceptNull && val == null) {
			i = null;
		} else {
			if (minValue != null && i < minValue.doubleValue()) { throw GamaRuntimeException
					.error("Value " + i + " should be greater than " + minValue, getScope()); }
			if (maxValue != null && i > maxValue.doubleValue()) { throw GamaRuntimeException
					.error("Value " + i + " should be smaller than " + maxValue, getScope()); }
		}
		super.modifyValue(i);
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
		minValue = minValue == null ? null : minValue.doubleValue();
		maxValue = maxValue == null ? null : maxValue.doubleValue();
		return valueToConsider;
	}

	@Override
	public IType<Double> getExpectedType() {
		return Types.FLOAT;
	}

	@Override
	protected Double applyPlus() {
		if (currentValue == null) { return 0.0; }
		final Double i = currentValue;
		final Double newVal = i + stepValue.doubleValue();
		return newVal;
	}

	@Override
	protected Double applyMinus() {
		if (currentValue == null) { return 0.0; }
		final Double i = currentValue;
		final Double newVal = i - stepValue.doubleValue();
		return newVal;
	}

	@Override
	protected void checkButtons() {
		super.checkButtons();
		final ToolItem plus = items[PLUS];
		if (plus != null && !plus.isDisposed()) {
			plus.setEnabled(param.isDefined() && (maxValue == null || applyPlus() < maxValue.doubleValue()));
		}
		final ToolItem minus = items[MINUS];
		if (minus != null && !minus.isDisposed()) {
			minus.setEnabled(param.isDefined() && (minValue == null || applyMinus() > minValue.doubleValue()));
		}
	}

}
