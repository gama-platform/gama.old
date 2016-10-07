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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.Symbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.expressions.ConstantExpression;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.fastmaths.FastMath;
import msi.gaml.statements.IExecutable;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import msi.gaml.variables.IVariable;
import msi.gaml.variables.Variable;

@facets(value = {
		@facet(name = IKeyword.NAME, type = IType.LABEL, optional = true, doc = @doc("The message displayed in the interface")),
		@facet(name = IKeyword.TYPE, type = IType.TYPE_ID, optional = true, doc = @doc("the variable type")),
		@facet(name = IKeyword.INIT, type = IType.NONE, optional = true, doc = @doc("the init value")),
		@facet(name = IKeyword.MIN, type = IType.NONE, optional = true, doc = @doc("the minimum value")),
		@facet(name = IKeyword.MAX, type = IType.NONE, optional = true, doc = @doc("the maximum value")),
		@facet(name = IKeyword.CATEGORY, type = IType.LABEL, optional = true, doc = @doc("a category label, used to group parameters in the interface")),
		@facet(name = IKeyword.VAR, type = IType.ID, optional = false, doc = @doc("the name of the variable (that should be declared in the global)")),
		@facet(name = IKeyword.UNIT, type = IType.LABEL, optional = true, doc = @doc("the variable unit")),
		@facet(name = IKeyword.ON_CHANGE, type = IType.NONE, optional = true, doc = @doc("Provides a block of statements that will be executed whenever the value of the parameter changes")),
		@facet(name = "slider", type = IType.BOOL, optional = true, doc = @doc("Whether or not to display a slider for entering an int or float value. Default is true when max and min values are defined, false otherwise. If no max or min value is defined, setting this facet to true will have no effect")),
		@facet(name = IKeyword.STEP, type = IType.FLOAT, optional = true, doc = @doc("the increment step (mainly used in batch mode to express the variation step between simulation)")),
		@facet(name = IKeyword.AMONG, type = IType.LIST, optional = true, doc = @doc("the list of possible values")) }, omissible = IKeyword.NAME)
@symbol(name = { IKeyword.PARAMETER }, kind = ISymbolKind.PARAMETER, with_sequence = false, concept = {
		IConcept.EXPERIMENT, IConcept.PARAMETER })
@inside(kinds = { ISymbolKind.EXPERIMENT })
@validator(Variable.VarValidator.class)
@doc(value = "The parameter statement specifies which global attributes (i) will change through the successive simulations (in batch experiments), (ii) can be modified by user via the interface (in gui experiments). In GUI experiments, parameters are displayed depending on their type.", usages = {
		@usage(value = "In gui experiment, the general syntax is the following:", examples = {
				@example(value = "parameter title var: global_var category: cat;", isExecutable = false) }),
		@usage(value = "In batch experiment, the two following syntaxes can be used to describe the possible values of a parameter:", examples = {
				@example(value = "parameter 'Value of toto:' var: toto among: [1, 3, 7, 15, 100]; ", isExecutable = false),
				@example(value = "parameter 'Value of titi:' var: titi min: 1 max: 100 step: 2; ", isExecutable = false) }), })
@SuppressWarnings({ "rawtypes" })
public class ExperimentParameter extends Symbol implements IParameter.Batch {

	static Object UNDEFINED = new Object();
	private Object value = UNDEFINED;
	// int order;
	static int INDEX = 0;
	Number minValue, maxValue, stepValue;
	private List amongValue;
	String varName, title, category, unitLabel;
	IType type = Types.NO_TYPE;
	boolean isEditable;
	boolean canBeNull;
	boolean isDefined = true;
	final IExpression init, among, min, max, step, slider, onChange;

	public ExperimentParameter(final IDescription sd) throws GamaRuntimeException {
		super(sd);
		final VariableDescription desc = (VariableDescription) sd;
		setName(desc.getLitteral(IKeyword.VAR));
		type = desc.getType();
		title = sd.getName();
		unitLabel = getLiteral(IKeyword.UNIT);
		final ModelDescription wd = desc.getModelDescription();
		final IDescription targetedGlobalVar = wd.getAttribute(varName);
		if (type.equals(Types.NO_TYPE)) {
			type = targetedGlobalVar.getType();
		}
		setCategory(desc.getLitteral(IKeyword.CATEGORY));
		min = getFacet(IKeyword.MIN);
		max = getFacet(IKeyword.MAX);
		step = getFacet(IKeyword.STEP);
		among = getFacet(IKeyword.AMONG);
		onChange = getFacet(IKeyword.ON_CHANGE);
		slider = getFacet("slider");
		init = hasFacet(IKeyword.INIT) ? getFacet(IKeyword.INIT) : targetedGlobalVar.getFacetExpr(IKeyword.INIT);
		// order = desc.getDefinitionOrder();
		isEditable = true;
	}

	public ExperimentParameter(final IScope scope, final IParameter p) {
		this(scope, p, p.getTitle(), p.getCategory(), p.getAmongValue(scope), false);
	}

	public ExperimentParameter(final IScope scope, final IParameter p, final String title, final String category,
			final List among, final boolean canBeNull) {
		this(scope, p, title, category, null, among, canBeNull);
	}

	public ExperimentParameter(final IScope scope, final IParameter p, final String title, final String category,
			final String unit, final List among, final boolean canBeNull) {
		super(null);
		this.slider = null;
		this.title = title;
		this.canBeNull = canBeNull;
		// this.order = p.getDefinitionOrder();
		this.amongValue = among;
		if (among != null) {
			this.among = new ConstantExpression(among);
		} else {
			this.among = null;
		}
		this.minValue = p.getMinValue(scope);
		if (minValue != null) {
			this.min = new ConstantExpression(minValue);
		} else {
			min = null;
		}
		this.maxValue = p.getMaxValue(scope);
		if (maxValue != null) {
			this.max = new ConstantExpression(maxValue);
		} else {
			max = null;
		}
		this.stepValue = p.getStepValue(scope);
		if (stepValue != null) {
			this.step = new ConstantExpression(stepValue);
		} else {
			step = null;
		}
		onChange = null;
		setName(p.getName());
		setCategory(category);
		setType(p.getType());
		if (p instanceof IVariable && getType().getType().id() == IType.FILE) {
			init = ((IVariable) p).getFacet(IKeyword.INIT);
		} else {
			init = null;
			setValue(scope, p.getInitialValue(scope));
		}
		setEditable(p.isEditable());
	}

	@Override
	public void setName(final String name2) {
		varName = name2;
		if (title == null) {
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
	public boolean isDefined() {
		return isDefined;
	}

	@Override
	public void setDefined(final boolean defined) {
		isDefined = defined;
	}

	@Override
	public void setEditable(final boolean editable) {
		isEditable = editable;
	}

	// private static Object[] JunkResults = new Object[1];

	public void setAndVerifyValue(final IScope scope, final Object val) {
		Object newValue = val;
		if (minValue != null) {
			if (newValue instanceof Number) {
				if (((Number) newValue).doubleValue() < minValue.doubleValue()) {
					if (type.id() == IType.INT) {
						newValue = minValue.intValue();
					} else {
						newValue = minValue.doubleValue();
					}
				}
			}
		}
		if (maxValue != null) {
			if (newValue instanceof Number) {
				if (((Number) newValue).doubleValue() > maxValue.doubleValue()) {
					if (type.id() == IType.INT) {
						newValue = maxValue.intValue();
					} else {
						newValue = maxValue.doubleValue();
					}
				}
			}
		}
		if (getAmongValue(scope) != null && !getAmongValue(scope).isEmpty()) {
			if (!getAmongValue(scope).contains(newValue)) {
				newValue = getAmongValue(scope).get(0);
			}
		}
		if (value != UNDEFINED && onChange != null) {
			// Already initialized, we call the on_change behavior
			final IExecutable on_changer = scope.getAgent().getSpecies()
					.getAction(Cast.asString(scope, onChange.value(scope)));
			scope.getExperiment().executeAction(on_changer);
			// scope.execute(on_changer, scope.getAgentScope(), null,
			// JunkResults);

		}
		value = newValue;
	}

	@Override
	public void setValue(final IScope scope, final Object val) {
		if (val == UNDEFINED) {
			if (getAmongValue(scope) != null) {
				value = getAmongValue(scope).get(scope.getRandom().between(0, getAmongValue(scope).size() - 1));
			} else if (type.id() == IType.INT || type.id() == IType.FLOAT) {
				value = drawRandomValue(scope);
			} else if (type.id() == IType.BOOL) {
				value = scope.getRandom().between(1, 100) > 50;
			} else {
				value = null;
			}
			return;
		}
		setAndVerifyValue(scope, val);
	}

	@Override
	public void reinitRandomly(final IScope scope) {
		setValue(scope, UNDEFINED);
	}

	public void tryToInit(final IScope scope) {
		if (value != UNDEFINED) {
			return;
		}
		if (init == null) {
			return;
		}
		setValue(scope, init.value(scope));

	}

	private Number drawRandomValue(final IScope scope) {
		final double step = stepValue == null ? 1.0 : stepValue.doubleValue();
		if (type.id() == IType.INT) {
			final int min = minValue == null ? Integer.MIN_VALUE : minValue.intValue();
			final int max = maxValue == null ? Integer.MAX_VALUE : maxValue.intValue();
			return scope.getRandom().between(min, max, (int) step);
		}
		final double min = minValue == null ? Double.MIN_VALUE : minValue.doubleValue();
		final double max = maxValue == null ? Double.MAX_VALUE : maxValue.doubleValue();
		return scope.getRandom().between(min, max, step);
	}

	@Override
	public Set<Object> neighborValues(final IScope scope) throws GamaRuntimeException {
		final Set<Object> neighborValues = new HashSet<Object>();
		if (getAmongValue(scope) != null && !getAmongValue(scope).isEmpty()) {
			final int index = getAmongValue(scope).indexOf(this.value(scope));
			if (index > 0) {
				neighborValues.add(getAmongValue(scope).get(index - 1));
			}
			if (index < getAmongValue(scope).size() - 1) {
				neighborValues.add(getAmongValue(scope).get(index + 1));
			}
			return neighborValues;
		}
		final double step = stepValue == null ? 1.0 : stepValue.doubleValue();
		if (type.id() == IType.INT) {
			final int min = minValue == null ? Integer.MIN_VALUE : minValue.intValue();
			final int max = maxValue == null ? Integer.MAX_VALUE : maxValue.intValue();
			final int val = Cast.as(value(scope), Integer.class, false);
			if (val >= min + (int) step) {
				neighborValues.add(val - (int) step);
			}
			if (val <= max - (int) step) {
				neighborValues.add(val + (int) step);
			}
		} else if (type.id() == IType.FLOAT) {
			final double min = minValue == null ? Double.MIN_VALUE : minValue.doubleValue();
			final double max = maxValue == null ? Double.MAX_VALUE : maxValue.doubleValue();
			final double removeZ = FastMath.max(100000.0, 1.0 / step);
			final double val = Cast.asFloat(null, value(scope));
			if (val >= min + step) {
				final double valLow = FastMath.round((val - step) * removeZ) / removeZ;
				neighborValues.add(valLow);
			}
			if (val <= max - step) {
				final double valHigh = FastMath.round((val + step) * removeZ) / removeZ;
				neighborValues.add(valHigh);
			}
		}
		return neighborValues;
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

	// @Override
	// public Integer getDefinitionOrder() {
	// return order;
	// }

	@Override
	public Object value(final IScope scope) {
		return getValue(scope);
	}

	@Override
	public Object value() {
		return GAMA.run(scope -> getValue(scope));

	}

	@Override
	public Object getInitialValue(final IScope scope) {
		return getValue(scope);
	}

	@Override
	public Number getMinValue(final IScope scope) {
		if (minValue == null && min != null) {
			minValue = (Number) min.value(scope);
		}
		return minValue;
	}

	@Override
	public Number getMaxValue(final IScope scope) {
		if (maxValue == null && max != null) {
			maxValue = (Number) max.value(scope);
		}
		return maxValue;
	}

	@Override
	public List getAmongValue(final IScope scope) {
		if (amongValue == null && among != null) {
			amongValue = (List) among.value(scope);
		}
		return amongValue;
	}

	@Override
	public Number getStepValue(final IScope scope) {
		if (stepValue == null && step != null) {
			stepValue = (Number) step.value(scope);
		}
		return stepValue;
	}

	@Override
	public IType getType() {
		return type;
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return GAMA.run(scope -> StringUtils.toGaml(getValue(scope), includingBuiltIn));

	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> commands) {
	}

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
	public String getUnitLabel(final IScope scope) {
		if (unitLabel == null && canBeExplored()) {
			return computeExplorableLabel(scope);
		}
		return unitLabel;
	}

	private String computeExplorableLabel(final IScope scope) {
		final List l = getAmongValue(scope);
		if (l != null) {
			return "among " + l;
		}
		final Number max = getMaxValue(scope);
		final Number min = getMinValue(scope);
		final Number step = getStepValue(scope);
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

	@Override
	public boolean acceptsSlider(final IScope scope) {
		if (slider == null)
			return true;
		return Cast.asBool(scope, slider.value(scope));
	}

}
