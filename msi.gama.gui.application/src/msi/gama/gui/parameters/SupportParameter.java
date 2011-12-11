/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.parameters;

import java.util.List;
import msi.gama.internal.types.Types;
import msi.gama.kernel.experiment.ParameterAdapter;

class SupportParameter extends ParameterAdapter {

	Object value;
	List among;
	Number min, max, step;

	SupportParameter(final String name, final Object value) {
		super(name, Types.get(value == null ? Object.class : value.getClass()).id());
		this.value = value;
	}

	SupportParameter(final String name, final Object value, final List among) {
		this(name, value);
		this.among = among;
	}

	SupportParameter(final String name, final Object value, final Number min, final Number max,
		final Number step) {
		this(name, value);
		this.min = min;
		this.max = max;
		this.step = step;
	}

	SupportParameter(final String name, final String unit, final Object value, final Number min,
		final Number max, final Number step) {
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
