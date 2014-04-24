/*********************************************************************************************
 * 
 * 
 * 'ExperimentParameter.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.GAMA.InScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;
import msi.gaml.variables.Variable;

@facets(value = { @facet(name = IKeyword.NAME, type = IType.LABEL, optional = true),
	@facet(name = IKeyword.TYPE, type = IType.TYPE_ID, optional = true),
	@facet(name = IKeyword.INIT, type = IType.NONE, optional = true),
	@facet(name = IKeyword.MIN, type = IType.NONE, optional = true),
	@facet(name = IKeyword.MAX, type = IType.NONE, optional = true),
	@facet(name = IKeyword.CATEGORY, type = IType.LABEL, optional = true),
	@facet(name = IKeyword.VAR, type = IType.ID, optional = false),
	@facet(name = IKeyword.UNIT, type = IType.LABEL, optional = true),
	@facet(name = IKeyword.STEP, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.AMONG, type = IType.LIST, optional = true) }, omissible = IKeyword.NAME)
@symbol(name = { IKeyword.PARAMETER }, kind = ISymbolKind.PARAMETER, with_sequence = false)
@inside(kinds = { ISymbolKind.EXPERIMENT })
@validator(Variable.VarValidator.class)
public class ExperimentParameter extends Symbol implements IParameter.Batch {

	static Object UNDEFINED = new Object();
	private Object value = UNDEFINED;
	int order;
	static int INDEX = 0;
	Number minValue, maxValue, stepValue;
	private List amongValue;
	String varName, title, category, unitLabel;
	IType type = Types.NO_TYPE/* , contentType = Types.NO_TYPE */;
	boolean isEditable/* , isLabel */;
	boolean canBeNull;
	final IExpression init, among, min, max, step;

	public ExperimentParameter(final IDescription sd) throws GamaRuntimeException {
		super(sd);
		VariableDescription desc = (VariableDescription) sd;
		setName(desc.getFacets().getLabel(IKeyword.VAR));
		type = desc.getType();
		// contentType = desc.getContentType();
		title = getLiteral(IKeyword.NAME);
		unitLabel = getLiteral(IKeyword.UNIT);
		ModelDescription wd = desc.getModelDescription();
		// TypeDescription wd = md.getWorldSpecies();
		IDescription targetedGlobalVar = wd.getVariable(varName);
		if ( type.equals(Types.NO_TYPE) ) {
			type = targetedGlobalVar.getType();
		}
		setCategory(desc.getFacets().getLabel(IKeyword.CATEGORY));
		min = getFacet(IKeyword.MIN);
		max = getFacet(IKeyword.MAX);
		step = getFacet(IKeyword.STEP);
		among = getFacet(IKeyword.AMONG);
		init =
			this.hasFacet(IKeyword.INIT) ? getFacet(IKeyword.INIT) : targetedGlobalVar.getFacets().getExpr(
				IKeyword.INIT);

		order = desc.getDefinitionOrder();

		// setValue(init == null ? UNDEFINED : init.value(GAMA.getDefaultScope()));
		isEditable = true;
		// isLabel = false; // ??
	}

	public ExperimentParameter(final IScope scope, final IParameter p) {
		this(scope, p, p.getTitle(), p.getCategory(), p.getAmongValue(), false);
	}

	public ExperimentParameter(final IScope scope, final IParameter p, final String title, final String category,
		final List among, final boolean canBeNull) {
		this(scope, p, title, category, null, among, canBeNull);
	}

	public ExperimentParameter(final IScope scope, final IParameter p, final String title, final String category,
		final String unit, final List among, final boolean canBeNull) {
		super(null);
		init = null;
		this.title = title;
		this.canBeNull = canBeNull;
		this.order = p.getDefinitionOrder();
		this.amongValue = among;
		if ( among != null ) {
			this.among = new ConstantExpression(among);
		} else {
			this.among = null;
		}
		this.minValue = p.getMinValue();
		if ( minValue != null ) {
			this.min = new ConstantExpression(minValue);
		} else {
			min = null;
		}
		this.maxValue = p.getMaxValue();
		if ( maxValue != null ) {
			this.max = new ConstantExpression(maxValue);
		} else {
			max = null;
		}
		this.stepValue = p.getStepValue();
		if ( stepValue != null ) {
			this.step = new ConstantExpression(stepValue);
		} else {
			step = null;
		}
		setName(p.getName());
		setCategory(category);
		setType(p.getType());
		setValue(scope, p.getInitialValue(scope));
		setEditable(p.isEditable());
		// isLabel = p.isLabel();
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

	// @Override
	// public boolean isLabel() {
	// return isLabel;
	// }

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
		if ( getAmongValue() != null && !getAmongValue().isEmpty() ) {
			if ( !getAmongValue().contains(newValue) ) {
				newValue = getAmongValue().get(0);
			}
		}
		value = newValue;
	}

	@Override
	public void setValue(final IScope scope, final Object val) {
		if ( val == UNDEFINED ) {
			if ( getAmongValue() != null ) {
				value = getAmongValue().get(scope.getRandom().between(0, getAmongValue().size() - 1));
			} else if ( type.id() == IType.INT || type.id() == IType.FLOAT ) {
				value = drawRandomValue(scope);
			} else if ( type.id() == IType.BOOL ) {
				value = scope.getRandom().between(1, 100) > 50;
			} else {
				value = null;
			}
			return;
		}
		setAndVerifyValue(val);
	}

	@Override
	public void reinitRandomly(final IScope scope) {
		setValue(scope, UNDEFINED);
	}

	public void tryToInit(final IScope scope) {
		if ( value != UNDEFINED ) { return; }
		if ( init == null ) { return; }
		setValue(scope, init.value(scope));
		
	}

	private Number drawRandomValue(final IScope scope) {
		double step = stepValue == null ? 1.0 : stepValue.doubleValue();
		if ( type.id() == IType.INT ) {
			int min = minValue == null ? Integer.MIN_VALUE : minValue.intValue();
			int max = maxValue == null ? Integer.MAX_VALUE : maxValue.intValue();
			int nbSteps = (int)((max - min) / step);
			final int val = scope.getRandom().between(0, nbSteps);
			return min + (int)(val * step);
		}
		double min = minValue == null ? Double.MIN_VALUE : minValue.doubleValue();
		double max = maxValue == null ? Double.MAX_VALUE : maxValue.doubleValue();
		final double val = scope.getRandom().between(0., max - min) + 0.5;
		final int nbStep = (int) (val / step);
		final double high = (int) (Math.min(max, min + (nbStep + 1.0) * step) * 1000000 + 0.5) / 1000000.0;
		final double low = (int) ((min + nbStep * step) * 1000000 + 0.5) / 1000000.0;
		return val - low < high - val ? low : high;
	}

	@Override
	public Set<Object> neighbourValues() throws GamaRuntimeException {
		final Set<Object> neighbourValues = new HashSet<Object>();
		if ( getAmongValue() != null && !getAmongValue().isEmpty() ) {
			int index = getAmongValue().indexOf(this.value());
			if ( index > 0 ) {
				neighbourValues.add(getAmongValue().get(index - 1));
			}
			if ( index < getAmongValue().size() - 1 ) {
				neighbourValues.add(getAmongValue().get(index + 1));
			}
			return neighbourValues;
		}
		double step = stepValue == null ? 1.0 : stepValue.doubleValue();
		if ( type.id() == IType.INT ) {
			int min = minValue == null ? Integer.MIN_VALUE : minValue.intValue();
			int max = maxValue == null ? Integer.MAX_VALUE : maxValue.intValue();
			int val = Cast.as(value(), Integer.class);
			if ( val >= min + (int) step ) {
				neighbourValues.add(val - (int) step);
			}
			if ( val <= max - (int) step ) {
				neighbourValues.add(val + (int) step);
			}
		} else if ( type.id() == IType.FLOAT ) {
			double min = minValue == null ? Double.MIN_VALUE : minValue.doubleValue();
			double max = maxValue == null ? Double.MAX_VALUE : maxValue.doubleValue();
			double removeZ = Math.max(100000.0, 1.0 / step);
			double val = Cast.asFloat(null, value());
			if ( val >= min + step ) {
				final double valLow = Math.round((val - step) * removeZ) / removeZ;
				neighbourValues.add(valLow);
			}
			if ( val <= max - step ) {
				final double valHigh = Math.round((val + step) * removeZ) / removeZ;
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
		return getValue(scope);
	}

	@Override
	public Object value() {
		return GAMA.run(new InScope() {

			@Override
			public Object run(final IScope scope) {
				return getValue(scope);
			}

		});

	}

	@Override
	public Object getInitialValue(final IScope scope) {
		return getValue(scope);
	}

	@Override
	public Number getMinValue() {
		if ( minValue == null && min != null ) {
			GAMA.run(new InScope.Void() {

				@Override
				public void process(final IScope scope) {
					minValue = (Number) min.value(scope);
				}
			});
		}
		return minValue;
	}

	@Override
	public Number getMaxValue() {
		if ( maxValue == null && max != null ) {
			GAMA.run(new InScope.Void() {

				@Override
				public void process(final IScope scope) {
					maxValue = (Number) max.value(scope);
				}
			});
		}
		return maxValue;
	}

	@Override
	public List getAmongValue() {
		if ( amongValue == null && among != null ) {
			GAMA.run(new InScope.Void() {

				@Override
				public void process(final IScope scope) {
					amongValue = (List) among.value(scope);
				}
			});
		}
		return amongValue;
	}

	@Override
	public Number getStepValue() {
		if ( stepValue == null && step != null ) {
			GAMA.run(new InScope.Void() {

				@Override
				public void process(final IScope scope) {
					stepValue = (Number) step.value(scope);
				}
			});
		}
		return stepValue;
	}

	@Override
	public IType getType() {
		return type;
	}

	@Override
	public String toGaml() {
		return GAMA.run(new InScope<String>() {

			@Override
			public String run(final IScope scope) {
				return StringUtils.toGaml(getValue(scope));
			}
		});

	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {}

	@Override
	public String toString() {
		return "Parameter '" + title + "' targets var " + varName;
	}

	public boolean canBeNull() {
		return canBeNull;
	}

	@Override
	public boolean canBeExplored() {
		return among != null || min != null && max != null && step != null;
	}

	@Override
	public String getUnitLabel() {
		if ( unitLabel == null && canBeExplored() ) { return computeExplorableLabel(); }
		return unitLabel;
	}

	private String computeExplorableLabel() {
		List l = getAmongValue();
		if ( l != null ) { return "among " + l; }
		Number max = getMaxValue();
		Number min = getMinValue();
		Number step = getStepValue();
		return "between " + min + " and " + max + " every " + step;
	}

	@Override
	public void setUnitLabel(final String label) {
		unitLabel = label;
	}

	Object getValue(final IScope scope) {
		tryToInit(scope);
		return value;
	}

	/**
	 * Method getContentType()
	 * @see msi.gama.kernel.experiment.IParameter#getContentType()
	 */
	// @Override
	// public IType getContentType() {
	// return contentType;
	// }

}
