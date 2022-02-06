/*******************************************************************************************************
 *
 * IntEditor.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.ui.interfaces.EditorListener;

/**
 * The Class IntEditor.
 */
public class IntEditor extends NumberEditor<Integer> {

	/**
	 * Instantiates a new int editor.
	 *
	 * @param scope
	 *            the scope
	 * @param agent
	 *            the agent
	 * @param param
	 *            the param
	 * @param canBeNull
	 *            the can be null
	 * @param l
	 *            the l
	 */
	IntEditor(final IScope scope, final IAgent agent, final IParameter param, final boolean canBeNull,
			final EditorListener<Integer> l) {
		super(scope, agent, param, l, canBeNull);
	}

	@Override
	protected Integer defaultStepValue() {
		return 1;
	}

	@Override
	protected Integer applyPlus() {
		if (currentValue == null) return 0;
		final Integer i = currentValue;
		return i + getStepValue().intValue();
	}

	@Override
	protected Integer applyMinus() {
		if (currentValue == null) return 0;
		final Integer i = currentValue;
		return i - getStepValue().intValue();
	}

	@Override
	protected boolean modifyValue(final Object val) throws GamaRuntimeException {
		final int i = Cast.asInt(getScope(), val);
		if (getMinValue() != null && i < Cast.asInt(getScope(), getMinValue()))
			throw GamaRuntimeException.error("Value " + i + " should be greater than " + getMinValue(), getScope());
		if (getMaxValue() != null && i > Cast.asInt(getScope(), getMaxValue()))
			throw GamaRuntimeException.error("Value " + i + " should be smaller than " + getMaxValue(), getScope());
		return super.modifyValue(i);
	}

	@Override
	protected void updateToolbar() {
		super.updateToolbar();
		editorToolbar.enable(PLUS,
				param.isDefined() && (getMaxValue() == null || applyPlus() < Cast.asInt(getScope(), getMaxValue())));
		editorToolbar.enable(MINUS,
				param.isDefined() && (getMinValue() == null || applyMinus() > Cast.asInt(getScope(), getMinValue())));
	}

	@Override
	protected Integer normalizeValues() throws GamaRuntimeException {
		final Integer valueToConsider = getOriginalValue() == null ? 0 : Cast.asInt(getScope(), getOriginalValue());
		currentValue = getOriginalValue() == null ? null : valueToConsider;
		return valueToConsider;
	}

	@Override
	public IType<Integer> getExpectedType() { return Types.INT; }

}
