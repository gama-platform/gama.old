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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.kernel.experiment;

import java.util.*;
import msi.gama.common.util.StringUtils;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.*;

public abstract class ParameterAdapter implements IParameter.Batch {

	protected boolean editable;
	protected boolean tooltip;
	protected boolean label;
	protected String title;
	protected IType type;
	protected String category;
	protected String unitLabel;

	public ParameterAdapter(final String title, final short type) {
		this.title = title;
		this.type = Types.get(type);
	}

	public ParameterAdapter(final String title, final String category, final short type) {
		this(title, type);
		this.category = category;
	}

	public ParameterAdapter(final String title, final String category, final String unit,
		final short type) {
		this(title, category, type);
		this.unitLabel = unit;
	}

	// @Override
	public String serializeToGaml() {
		// GamlSerializer gs = new GamlSerializer();
		return "";
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getName() {
		return getTitle();
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public boolean allowsTooltip() {
		return true;
	}

	@Override
	public boolean isLabel() {
		return false;
	}

	@Override
	public boolean canBeExplored() {
		return false;
	}

	@Override
	public void setEditable(final boolean editable) {
		this.editable = editable;
	}

	@Override
	public String getCategory() {
		return category;
	}

	@Override
	public String getUnitLabel() {
		return unitLabel;
	}

	@Override
	public Integer getDefinitionOrder() {
		return 0;
	}

	@Override
	public void setValue(final Object value) {}

	@Override
	public Object value(final IScope iScope) throws GamaRuntimeException {
		return value();
	}

	@Override
	public IType type() {
		return type;
	}

	@Override
	public String toGaml() {
		return StringUtils.toGaml(value());
	}

	@Override
	public Object getInitialValue() {
		return value();
	}

	@Override
	public Number getMinValue() {
		return null;
	}

	@Override
	public Number getMaxValue() {
		return null;
	}

	@Override
	public List getAmongValue() {
		return null;
	}

	@Override
	public abstract Object value();

	@Override
	public void reinitRandomly() {}

	public void reinitIfNull() {}

	@Override
	public Set<Object> neighbourValues() throws GamaRuntimeException {
		return null;
	}

	@Override
	public Number getStepValue() {
		return null;
	}

	@Override
	public void setCategory(final String name) {
		category = name;
	}

}
