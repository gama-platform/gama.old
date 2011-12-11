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
package msi.gama.kernel.experiment;

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.internal.types.Types;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.Cast;

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
	public Object value(final IAgent agent) throws GamaRuntimeException {
		return value();
	}

	@Override
	public void setVal(final IScope scope, final IAgent agent, final Object v)
		throws GamaRuntimeException {
		setValue(v);
	}

	@Override
	public IType type() {
		return type;
	}

	@Override
	public IType getContentType() {
		return type.defaultContentType();
	}

	@Override
	public String toGaml() {
		return Cast.toGaml(value());
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
