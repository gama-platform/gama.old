/*********************************************************************************************
 *
 * 'IntEditor.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
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
import org.eclipse.swt.widgets.ToolItem;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.InputParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.ui.interfaces.EditorListener;

public class IntEditor extends NumberEditor<Integer> {

	IntEditor(final IScope scope, final IAgent agent, final IParameter param, final boolean canBeNull,
			final EditorListener<Integer> l) {
		super(scope, agent, param, l, canBeNull);
	}

	IntEditor(final IScope scope, final Composite parent, final String title, final String unit, final Integer value,
			final Integer min, final Integer max, final Integer step, final EditorListener<Integer> whenModified,
			final boolean canBeNull) {
		super(scope, new InputParameter(title, unit, value, min, max, step), whenModified, canBeNull);
		createComposite(parent);
	}

	@Override
	protected void computeStepValue() {
		super.computeStepValue();
		if (stepValue == null) { stepValue = 1; }
	}

	@Override
	protected Integer applyPlus() {
		if (currentValue == null) return 0;
		final Integer i = currentValue;
		return i + stepValue.intValue();
	}

	@Override
	protected Integer applyMinus() {
		if (currentValue == null) return 0;
		final Integer i = currentValue;
		return i - stepValue.intValue();
	}

	@Override
	protected boolean modifyValue(final Object val) throws GamaRuntimeException {
		final int i = Cast.asInt(getScope(), val);
		if (getMinValue() != null && i < Cast.asInt(getScope(), getMinValue()))
			throw GamaRuntimeException.error("Value " + i + " should be greater than " + getMinValue(), getScope());
		if (maxValue != null && i > Cast.asInt(getScope(), getMaxValue()))
			throw GamaRuntimeException.error("Value " + i + " should be smaller than " + maxValue, getScope());
		return super.modifyValue(i);
	}

	@Override
	protected void checkButtons() {
		super.checkButtons();
		final ToolItem plus = items[PLUS];
		if (plus != null && !plus.isDisposed()) {
			plus.setEnabled(
					param.isDefined() && (maxValue == null || applyPlus() < Cast.asInt(getScope(), getMaxValue())));
		}
		final ToolItem minus = items[MINUS];
		if (minus != null && !minus.isDisposed()) {
			minus.setEnabled(param.isDefined()
					&& (getMinValue() == null || applyMinus() > Cast.asInt(getScope(), getMinValue())));
		}
	}

	@Override
	protected Integer normalizeValues() throws GamaRuntimeException {
		final Integer valueToConsider = getOriginalValue() == null ? 0 : Cast.asInt(getScope(), getOriginalValue());
		currentValue = getOriginalValue() == null ? null : valueToConsider;
		minValue = getMinValue() == null ? null : Cast.asInt(getScope(), getMinValue());
		maxValue = getMaxValue() == null ? null : Cast.asInt(getScope(), getMaxValue());
		return valueToConsider;
	}

	@Override
	public IType<Integer> getExpectedType() {
		return Types.INT;
	}

}
