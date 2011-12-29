/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
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
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.*;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;

@facets({ @facet(name = IKeyword.NAME, type = IType.LABEL, optional = true),
	@facet(name = IKeyword.TYPE, type = IType.TYPE_ID, optional = true),
	@facet(name = IKeyword.INIT, type = IType.NONE_STR, optional = true),
	@facet(name = IKeyword.MIN, type = IType.NONE_STR, optional = true),
	@facet(name = IKeyword.MAX, type = IType.NONE_STR, optional = true),
	@facet(name = IKeyword.CATEGORY, type = IType.LABEL, optional = true),
	@facet(name = IKeyword.VAR, type = IType.ID, optional = false),
	@facet(name = IKeyword.UNIT, type = IType.LABEL, optional = true),
	@facet(name = IKeyword.STEP, type = IType.FLOAT_STR, optional = true),
	@facet(name = IKeyword.AMONG, type = IType.LIST_STR, optional = true) })
@symbol(name = { IKeyword.PARAMETER }, kind = ISymbolKind.VARIABLE)
@inside(kinds = { ISymbolKind.EXPERIMENT })
public class ExperimentParameter extends Symbol implements IParameter.Batch {

	static Object UNDEFINED = new Object();
	Object value;
	int order;
	static int INDEX = 0;
	Number minValue, maxValue, stepValue;
	List amongValue;
	String varName, title, category, unitLabel;
	IType type;
	boolean isEditable, allowsTooltip, isLabel;
	boolean canBeNull;

	public ExperimentParameter(final IDescription sd) throws GamlException, GamaRuntimeException {
		super(sd);
		VariableDescription desc = (VariableDescription) sd;
		setName(desc.getFacets().getString(IKeyword.VAR));
		type = desc.getType();
		title = getLiteral(IKeyword.NAME);
		unitLabel = getLiteral(IKeyword.UNIT);
		String p = "Parameter " + getTitle() + " ";
		ModelDescription md = desc.getModelDescription();
		SpeciesDescription wd = md.getWorldSpecies();
		VariableDescription vd = wd.getVariable(varName);
		if ( vd == null ) { throw new GamlException(p + "cannot refer to the non-global variable " +
			varName); }
		if ( type.equals(Types.NO_TYPE) ) {
			type = vd.getType();
		} else if ( type.id() != vd.getType().id() ) { throw new GamlException(p +
			"type must be the same as that of " + varName); }
		setCategory(desc.getFacets().getString(IKeyword.CATEGORY));
		IExpression init = assertFacet(desc, IKeyword.INIT, type);
		IExpression min = assertFacet(desc, IKeyword.MIN, type);
		IExpression max = assertFacet(desc, IKeyword.MAX, type);
		IExpression step = assertFacet(desc, IKeyword.STEP, type);
		IExpression among = assertFacet(desc, IKeyword.AMONG, Types.get(IType.LIST));
		boolean isNotModifiable = desc.isNotModifiable();
		if ( isNotModifiable ) { throw new GamlException(p + "cannot be declared as constant"); }
		order = desc.getDefinitionOrder();
		if ( among != null && type.id() != among.getContentType().id() ) { throw new GamlException(
			p + " of type " + type.toString() + " cannot be chosen among " + among.toGaml()); }
		minValue = min == null ? null : (Number) min.value(GAMA.getDefaultScope());
		maxValue = max == null ? null : (Number) max.value(GAMA.getDefaultScope());
		stepValue = step == null ? null : (Number) step.value(GAMA.getDefaultScope());
		amongValue = among == null ? null : (List) among.value(GAMA.getDefaultScope());
		setValue(init == null ? UNDEFINED : init.value(GAMA.getDefaultScope()));
		isEditable = true;
		isLabel = false; // ??
		allowsTooltip = true; // ??
	}

	public ExperimentParameter(final IParameter p) {
		this(p, p.getTitle(), p.getCategory(), p.getAmongValue(), false);
	}

	public ExperimentParameter(final IParameter p, final String title, final String category,
		final List among, final boolean canBeNull) {
		this(p, title, category, null, among, canBeNull);
	}

	public ExperimentParameter(final IParameter p, final String title, final String category,
		final String unit, final List among, final boolean canBeNull) {
		super(null);
		this.title = title;
		this.canBeNull = canBeNull;
		this.order = p.getDefinitionOrder();
		this.amongValue = among;
		this.minValue = p.getMinValue();
		this.maxValue = p.getMaxValue();
		setName(p.getName());
		setCategory(category);
		setType(p.type());
		setValue(p.getInitialValue());
		setEditable(p.isEditable());
		isLabel = p.isLabel();
		allowsTooltip = p.allowsTooltip();
	}

	public IExpression assertFacet(final IDescription d, final String name, final IType type)
		throws GamlException {
		IExpression expr = d.getFacets().getExpr(name);
		if ( expr == null ) { return null; }
		if ( !expr.isConst() ) { throw new GamlException("Parameter " + getTitle() + " " + name +
			" facet must be constant"); }
		if ( type != null && !expr.type().equals(type) ) { throw new GamlException("Parameter " +
			getTitle() + " " + name + " facet must be of type " + type.toString()); }
		return expr;
	}

	@Override
	public void setName(final String name2) {
		varName = name2;
		if ( title == null ) {
			title = name2;
		}
	}

	private void setType(final IType iType) {
		type = iType;
	}

	@Override
	public boolean isEditable() {
		return isEditable;
	}

	@Override
	public boolean allowsTooltip() {
		return allowsTooltip;
	}

	@Override
	public boolean isLabel() {
		return isLabel;
	}

	@Override
	public void setEditable(final boolean editable) {
		isEditable = editable;
	}

	public void setAndVerifyValue(final Object val) {
		Object newValue = val;
		if ( minValue != null ) {
			if ( newValue instanceof Number ) {
				if ( ((Number) newValue).doubleValue() < minValue.doubleValue() ) {
					if ( type.id() == IType.INT ) {
						newValue = minValue.intValue();
					} else {
						newValue = minValue.doubleValue();
					}
				}
			}
		}
		if ( maxValue != null ) {
			if ( newValue instanceof Number ) {
				if ( ((Number) newValue).doubleValue() > maxValue.doubleValue() ) {
					if ( type.id() == IType.INT ) {
						newValue = maxValue.intValue();
					} else {
						newValue = maxValue.doubleValue();
					}
				}
			}
		}
		if ( amongValue != null && !amongValue.isEmpty() ) {
			if ( !amongValue.contains(newValue) ) {
				newValue = amongValue.get(0);
			}
		}
		value = newValue;
	}

	@Override
	public void setValue(final Object val) {
		if ( val == UNDEFINED ) {
			if ( amongValue != null ) {
				value = amongValue.get(GAMA.getRandom().between(0, amongValue.size() - 1));
			} else if ( type.id() == IType.INT || type.id() == IType.FLOAT ) {
				value = drawRandomValue();
			} else if ( type.id() == IType.BOOL ) {
				value = GAMA.getRandom().between(1, 100) > 50;
			} else {
				value = null;
			}
			return;
		}
		setAndVerifyValue(val);
	}

	@Override
	public void reinitRandomly() {
		setValue(UNDEFINED);
	}

	private Number drawRandomValue() {
		double step = stepValue == null ? 1.0 : stepValue.doubleValue();
		if ( type.id() == IType.INT ) {
			int min = minValue == null ? Integer.MIN_VALUE : minValue.intValue();
			int max = maxValue == null ? Integer.MAX_VALUE : maxValue.intValue();
			final int val = (int) (RandomUtils.getDefault().between(0., max - min) + 0.5);
			final int nbStep = (int) (val / step);
			final int high = (int) ((nbStep + 1) * step);
			final int low = (int) (nbStep * step);
			return val - low < high - val ? low : high;
		}
		double min = minValue == null ? Double.MIN_VALUE : minValue.doubleValue();
		double max = maxValue == null ? Double.MAX_VALUE : maxValue.doubleValue();
		final double val = GAMA.getRandom().between(0., max - min) + 0.5;
		final int nbStep = (int) (val / step);
		final double high =
			(int) (Math.min(max, min + (nbStep + 1.0) * step) * 1000000 + 0.5) / 1000000.0;
		final double low = (int) ((min + nbStep * step) * 1000000 + 0.5) / 1000000.0;
		return val - low < high - val ? low : high;
	}

	@Override
	public Set<Object> neighbourValues() throws GamaRuntimeException {
		final Set<Object> neighbourValues = new HashSet<Object>();
		if ( amongValue != null && !amongValue.isEmpty() ) {
			int index = amongValue.indexOf(this.value());
			if ( index > 0 ) {
				neighbourValues.add(amongValue.get(index - 1));
			}
			if ( index < amongValue.size() - 1 ) {
				neighbourValues.add(amongValue.get(index + 1));
			}
			return neighbourValues;
		}
		double step = stepValue == null ? 1.0 : stepValue.doubleValue();
		if ( type.id() == IType.INT ) {
			int min = minValue == null ? Integer.MIN_VALUE : minValue.intValue();
			int max = maxValue == null ? Integer.MAX_VALUE : maxValue.intValue();
			int val = Cast.asInt(GAMA.getDefaultScope(), value());
			if ( val >= min + (int) step ) {
				neighbourValues.add(val - (int) step);
			}
			if ( val <= max - (int) step ) {
				neighbourValues.add(val + (int) step);
			}
		} else if ( type.id() == IType.FLOAT ) {
			double min = minValue == null ? Double.MIN_VALUE : minValue.doubleValue();
			double max = maxValue == null ? Double.MAX_VALUE : maxValue.doubleValue();
			double val = Cast.asFloat(GAMA.getDefaultScope(), value());
			if ( val >= min + step ) {
				final double valLow = (int) ((val - step) * 100000 + 0.5) / 100000.0;
				neighbourValues.add(valLow);
			}
			if ( ((Double) this.value()).doubleValue() <= max - step ) {
				final double valHigh = (int) ((val + step) * 100000 + 0.5) / 100000.0;
				neighbourValues.add(valHigh);
			}
		}
		return neighbourValues;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getName() {
		return varName;
	}

	@Override
	public String getCategory() {
		return category;
	}

	@Override
	public void setCategory(final String cat) {
		category = cat;
	}

	@Override
	public Integer getDefinitionOrder() {
		return order;
	}

	@Override
	public Object value(final IScope scope) {
		return value;
	}

	@Override
	public Object value(final IAgent agent) throws GamaRuntimeException {
		return value;
	}

	@Override
	public void setVal(final IScope scope, final IAgent agent, final Object v)
		throws GamaRuntimeException {
		setAndVerifyValue(v);
	}

	@Override
	public Object value() {
		return value;
	}

	@Override
	public Object getInitialValue() {
		return value;
	}

	@Override
	public Number getMinValue() {
		return minValue;
	}

	@Override
	public Number getMaxValue() {
		return maxValue;
	}

	@Override
	public List getAmongValue() {
		return amongValue;
	}

	@Override
	public Number getStepValue() {
		return stepValue;
	}

	@Override
	public IType type() {
		return type;
	}

	@Override
	public IType getContentType() {
		return type().defaultContentType();
	}

	@Override
	public String toGaml() {
		return StringUtils.toGaml(value);
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) throws GamlException {}

	@Override
	public String toString() {
		return "Parameter '" + title + "' targets var " + varName;
	}

	public boolean canBeNull() {
		return canBeNull;
	}

	@Override
	public boolean canBeExplored() {
		return amongValue != null || minValue != null && maxValue != null && stepValue != null;
	}

	@Override
	public String getUnitLabel() {
		return unitLabel;
	}

}
