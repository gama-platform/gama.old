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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.parameters;

import java.util.List;
import msi.gama.kernel.experiment.ParameterAdapter;
import msi.gaml.types.*;

class InputParameter extends ParameterAdapter {

	private Object value;
	private List among;
	private Number min, max/* , step */;

	InputParameter(final String name, final Object value) {
		super(name, Types.get(value == null ? Object.class : value.getClass()).id());
		this.value = value;
	}

	InputParameter(final String name, final Object value, final IType type) {
		super(name, type.id());
		this.value = value;
	}

	InputParameter(final String name, final Object value, final List among) {
		this(name, value);
		this.among = among;
	}

	InputParameter(final String name, final Object value, final Number min, final Number max, final Number step) {
		this(name, value);
		this.min = min;
		this.max = max;
		// this.step = step;
	}

	InputParameter(final String name, final String unit, final Object value, final Number min, final Number max,
		final Number step) {
		this(name, value, min, max, step);
		unitLabel = unit;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setValue(final Object value) {
		this.value = value;
	}

	@Override
	public Number getMinValue() {
		return min;
	}

	@Override
	public Number getMaxValue() {
		return max;
	}

	@Override
	public List getAmongValue() {
		return among;
	}

	@Override
	public Object value() {
		return value;
	}

	@Override
	public boolean isEditable() {
		return true;
	}

}
