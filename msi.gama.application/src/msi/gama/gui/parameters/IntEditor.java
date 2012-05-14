/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.parameters;

// TODO Passer le FloatEditor et le IntEditor au même layout.

import msi.gama.common.interfaces.EditorListener;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;
import org.eclipse.swt.widgets.Composite;

public class IntEditor extends NumberEditor {

	IntEditor(final IAgent agent, final IParameter param, final boolean canBeNull) {
		this(agent, param, canBeNull, null);
	}

	IntEditor(final IAgent agent, final IParameter param, final boolean canBeNull,
		final EditorListener l) {
		super(agent, param, l, canBeNull);
	}

	IntEditor(final Composite parent, final String title, final String unit, final Integer value,
		final Integer min, final Integer max, final Integer step,
		final EditorListener<Integer> whenModified, final boolean canBeNull) {
		super(new InputParameter(title, unit, value, min, max, step), whenModified, canBeNull);
		createComposite(parent);
	}

	@Override
	protected void computeStepValue() {
		stepValue = param.getStepValue();
		if ( stepValue == null ) {
			stepValue = 1;
		}
	}

	@Override
	protected Integer applyPlus() {
		if ( currentValue == null ) { return 0; }
		Integer i = (Integer) currentValue;
		Integer newVal = i + stepValue.intValue();
		return newVal;
	}

	@Override
	protected Integer applyMinus() {
		if ( currentValue == null ) { return 0; }
		Integer i = (Integer) currentValue;
		Integer newVal = i - stepValue.intValue();
		return newVal;
	}

	@Override
	protected void checkButtons() {
		plus.setEnabled(maxValue == null || applyPlus() < maxValue.intValue());
		minus.setEnabled(minValue == null || applyMinus() > minValue.intValue());
	}

	@Override
	protected Integer normalizeValues() throws GamaRuntimeException {
		Integer valueToConsider =
			getOriginalValue() == null ? 0 : Cast.asInt(GAMA.getDefaultScope(), getOriginalValue());
		currentValue = getOriginalValue() == null ? null : valueToConsider;
		minValue = minValue == null ? null : minValue.intValue();
		maxValue = maxValue == null ? null : maxValue.intValue();
		return valueToConsider;
	}

	@Override
	public IType getExpectedType() {
		return Types.get(IType.INT);
	}

}
