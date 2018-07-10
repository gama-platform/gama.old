/*********************************************************************************************
 *
 * 'IntEditor.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.parameters;

import org.eclipse.swt.widgets.Button;
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

public class IntEditor extends NumberEditor<Integer> {

	IntEditor(final IScope scope, final IAgent agent, final IParameter param, final boolean canBeNull) {
		this(scope, agent, param, canBeNull, null);
	}

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
		stepValue = param.getStepValue(getScope());
		if (stepValue == null) {
			stepValue = 1;
		}
	}

	@Override
	protected Integer applyPlus() {
		if (currentValue == null) { return 0; }
		final Integer i = currentValue;
		final Integer newVal = i + stepValue.intValue();
		return newVal;
	}

	@Override
	protected Integer applyMinus() {
		if (currentValue == null) { return 0; }
		final Integer i = currentValue;
		final Integer newVal = i - stepValue.intValue();
		return newVal;
	}

	@Override
	protected void modifyValue(final Integer val) throws GamaRuntimeException {
		final Integer i = Cast.asInt(getScope(), val);
		if (minValue != null && i < minValue.intValue()) { throw GamaRuntimeException
				.error("Value " + i + " should be greater than " + minValue, getScope()); }
		if (maxValue != null && i > maxValue.intValue()) { throw GamaRuntimeException
				.error("Value " + i + " should be smaller than " + maxValue, getScope()); }
		super.modifyValue(i);
	}

	@Override
	protected void checkButtons() {
		super.checkButtons();
		final Button plus = items[PLUS];
		if (plus != null && !plus.isDisposed()) {
			plus.setEnabled(param.isDefined() && (maxValue == null || applyPlus() < maxValue.intValue()));
		}
		final Button minus = items[MINUS];
		if (minus != null && !minus.isDisposed()) {
			minus.setEnabled(param.isDefined() && (minValue == null || applyMinus() > minValue.intValue()));
		}
	}

	@Override
	protected Integer normalizeValues() throws GamaRuntimeException {
		final Integer valueToConsider = getOriginalValue() == null ? 0 : Cast.asInt(getScope(), getOriginalValue());
		currentValue = getOriginalValue() == null ? null : valueToConsider;
		minValue = minValue == null ? null : minValue.intValue();
		maxValue = maxValue == null ? null : maxValue.intValue();
		return valueToConsider;
	}

	@Override
	public IType<Integer> getExpectedType() {
		return Types.INT;
	}

}
