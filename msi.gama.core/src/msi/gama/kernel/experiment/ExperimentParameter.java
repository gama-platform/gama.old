/*******************************************************************************************************
 *
 * msi.gama.kernel.experiment.ExperimentParameter.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.locationtech.jts.util.NumberUtil;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.Collector;
import msi.gama.util.GamaColor;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.Symbol;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.expressions.ConstantExpression;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.operators.Cast;
import msi.gaml.statements.ActionStatement;
import msi.gaml.statements.IExecutable;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import msi.gaml.variables.IVariable;
import msi.gaml.variables.Variable;

@facets (
		value = { @facet (
				name = IKeyword.NAME,
				type = IType.LABEL,
				optional = true,
				doc = @doc ("The message displayed in the interface")),
				@facet (
						name = IKeyword.TYPE,
						type = IType.TYPE_ID,
						optional = true,
						doc = @doc ("the variable type")),
				@facet (
						name = IKeyword.INIT,
						type = IType.NONE,
						optional = true,
						doc = @doc ("the init value")),
				@facet (
						name = IKeyword.MIN,
						type = IType.NONE,
						optional = true,
						doc = @doc ("the minimum value")),
				@facet (
						name = IKeyword.MAX,
						type = IType.NONE,
						optional = true,
						doc = @doc ("the maximum value")),
				@facet (
						name = IKeyword.CATEGORY,
						type = IType.LABEL,
						optional = true,
						doc = @doc ("a category label, used to group parameters in the interface")),
				@facet (
						name = IKeyword.VAR,
						type = IType.ID,
						optional = false,
						doc = @doc ("the name of the variable (that should be declared in the global)")),
				@facet (
						name = IKeyword.UNIT,
						type = IType.LABEL,
						optional = true,
						doc = @doc ("the variable unit")),
				@facet (
						name = IKeyword.ON_CHANGE,
						type = IType.NONE,
						optional = true,
						doc = @doc (
								deprecated = "Move the block of statements at the end of the parameter declaration instead",
								value = "Provides a block of statements that will be executed whenever the value of the parameter changes")),
				@facet (
						name = IKeyword.ENABLES,
						type = IType.LIST,
						optional = true,
						doc = @doc ("a list of global variables whose parameter editors will be enabled when this parameter value is set to true (they are otherwise disabled)")),
				@facet (
						name = IKeyword.DISABLES,
						type = IType.LIST,
						optional = true,
						doc = @doc ("a list of global variables whose parameter editors will be disabled when this parameter value is set to true (they are otherwise enabled)")),
				@facet (
						name = "slider",
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Whether or not to display a slider for entering an int or float value. Default is true when max and min values are defined, false otherwise. If no max or min value is defined, setting this facet to true will have no effect")),
				@facet (
						name = "colors",
						type = IType.LIST,
						of = IType.COLOR,
						optional = true,
						doc = @doc ("The colors of the control in the UI. An empty list has no effects. Only used for sliders and switches so far. For sliders, 3 colors will allow to specify the color of the left section, the thumb and the right section (in this order); 2 colors will define the left and right sections only (thumb will be dark green); 1 color will define the left section and the thumb. For switches, 2 colors will define the background for respectively the left 'true' and right 'false' sections. 1 color will define both backgrounds")),
				@facet (
						name = IKeyword.STEP,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("the increment step (mainly used in batch mode to express the variation step between simulation)")),
				@facet (
						name = IKeyword.AMONG,
						type = IType.LIST,
						optional = true,
						doc = @doc ("the list of possible values")) },
		omissible = IKeyword.NAME)
@symbol (
		name = { IKeyword.PARAMETER },
		kind = ISymbolKind.PARAMETER,
		with_sequence = false,
		concept = { IConcept.EXPERIMENT, IConcept.PARAMETER })
@inside (
		kinds = { ISymbolKind.EXPERIMENT })
@validator (Variable.VarValidator.class)
@doc (
		value = "The parameter statement specifies which global attributes (i) will change through the successive simulations (in batch experiments), (ii) can be modified by user via the interface (in gui experiments). In GUI experiments, parameters are displayed depending on their type.",
		usages = { @usage (
				value = "In gui experiment, the general syntax is the following:",
				examples = { @example (
						value = "parameter title var: global_var category: cat;",
						isExecutable = false) }),
				@usage (
						value = "In batch experiment, the two following syntaxes can be used to describe the possible values of a parameter:",
						examples = { @example (
								value = "parameter 'Value of toto:' var: toto among: [1, 3, 7, 15, 100]; ",
								isExecutable = false),
								@example (
										value = "parameter 'Value of titi:' var: titi min: 1 max: 100 step: 2; ",
										isExecutable = false) }), })
@SuppressWarnings ({ "rawtypes" })
public class ExperimentParameter extends Symbol implements IParameter.Batch {

	static Object UNDEFINED = new Object();
	private Object value = UNDEFINED;
	Number minValue, maxValue, stepValue;
	private List amongValue;
	final private String[] disables, enables;
	String varName, title, category, unitLabel;
	IType type = Types.NO_TYPE;
	boolean isEditable;
	boolean canBeNull;
	boolean isDefined = true;
	final IExpression init, among, min, max, step, slider, onChange;
	final List<ParameterChangeListener> listeners = new ArrayList<>();
	ActionStatement action;

	public ExperimentParameter(final IDescription sd) throws GamaRuntimeException {
		super(sd);
		final VariableDescription desc = (VariableDescription) sd;
		setName(desc.getLitteral(IKeyword.VAR));
		type = desc.getGamlType();
		title = sd.getName();
		unitLabel = getLiteral(IKeyword.UNIT);
		final ModelDescription wd = desc.getModelDescription();
		final VariableDescription targetedGlobalVar = wd.getAttribute(varName);
		if (type.equals(Types.NO_TYPE)) { type = targetedGlobalVar.getGamlType(); }
		setCategory(desc.getLitteral(IKeyword.CATEGORY));
		min = getFacet(IKeyword.MIN);
		final IScope runtimeScope = GAMA.getRuntimeScope();
		if (min != null && min.isConst()) { getMinValue(runtimeScope); }
		max = getFacet(IKeyword.MAX);
		if (max != null && max.isConst()) { getMaxValue(runtimeScope); }
		step = getFacet(IKeyword.STEP);
		if (step != null && step.isConst()) { getStepValue(runtimeScope); }
		among = getFacet(IKeyword.AMONG);
		if (among != null && among.isConst()) { getAmongValue(runtimeScope); }
		onChange = getFacet(IKeyword.ON_CHANGE);
		if (onChange != null) {
			listeners.add((scope, v) -> {
				final IExecutable on_changer =
						scope.getAgent().getSpecies().getAction(Cast.asString(scope, onChange.value(scope)));
				scope.getExperiment().executeAction(on_changer);
			});

		}
		slider = getFacet("slider");
		final IExpressionDescription d = type.equals(Types.BOOL) ? getDescription().getFacet(IKeyword.DISABLES) : null;
		final IExpressionDescription e = type.equals(Types.BOOL) ? getDescription().getFacet(IKeyword.ENABLES) : null;
		disables = d != null ? d.getStrings(getDescription(), false).toArray(new String[0]) : EMPTY_STRINGS;
		enables = e != null ? e.getStrings(getDescription(), false).toArray(new String[0]) : EMPTY_STRINGS;
		init = hasFacet(IKeyword.INIT) ? getFacet(IKeyword.INIT) : targetedGlobalVar.getFacetExpr(IKeyword.INIT);

		isEditable = !targetedGlobalVar.isNotModifiable();
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
		disables = EMPTY_STRINGS;
		enables = EMPTY_STRINGS;
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
		if (p instanceof IVariable && getType().getGamlType().id() == IType.FILE) {
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
		if (title == null) { title = name2; }
	}

	@Override
	public List<GamaColor> getColor(final IScope scope) {
		final IExpression exp = getFacet("colors");
		return exp == null ? null
				: (List<GamaColor>) Types.LIST.cast(scope, exp.value(scope), null, Types.INT, Types.COLOR, false);
	}

	@Override
	public void dispose() {
		super.dispose();
		listeners.clear();
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

	@Override
	public void addChangedListener(final ParameterChangeListener listener) {
		if (!listeners.contains(listener)) { listeners.add(listener); }
	}

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

		// See #3006
		// final List among = getAmongValue(scope);
		// if (among != null && !among.isEmpty()) {
		// if (!getAmongValue(scope).contains(newValue)) {
		// newValue = getAmongValue(scope).get(0);
		// }
		// }
		newValue = filterWithAmong(scope, newValue);
		if (value != UNDEFINED) {
			for (final ParameterChangeListener listener : listeners) {
				listener.changed(scope, newValue);
			}
			// Already initialized, we call the on_change behavior
			// final IExecutable on_changer =
			// scope.getAgent().getSpecies().getAction(Cast.asString(scope, onChange.value(scope)));
			// scope.getExperiment().executeAction(on_changer);
			// scope.execute(on_changer, scope.getAgentScope(), null,
			// JunkResults);

		}
		value = newValue;
	}

	private Object filterWithAmong(final IScope scope, final Object newValue) {
		getAmongValue(scope);
		if (amongValue == null || amongValue.isEmpty()) return newValue;
		if (Types.FLOAT.equals(this.getType())) {
			final double newDouble = Cast.asFloat(scope, newValue);
			for (final Object o : amongValue) {
				final Double d = Cast.asFloat(scope, o);
				final Double tolerance = 0.0000001d;
				if (NumberUtil.equalsWithTolerance(d, newDouble, tolerance)) return d;
			}

		} else {
			if (amongValue.contains(newValue)) return newValue;
		}
		return amongValue.get(0);
	}

	@Override
	public void setValue(final IScope scope, final Object val) {
		if (val == UNDEFINED) {
			getAmongValue(scope);
			if (amongValue != null) {
				value = amongValue.get(scope.getRandom().between(0, amongValue.size() - 1));
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
		if (value != UNDEFINED) return;
		if (init == null) return;
		setValue(scope, init.value(scope));

	}

	private Number drawRandomValue(final IScope scope) {
		final double theStep = stepValue == null ? 1.0 : stepValue.doubleValue();
		if (type.id() == IType.INT) {
			final int theMin = minValue == null ? Integer.MIN_VALUE : minValue.intValue();
			final int theMax = maxValue == null ? Integer.MAX_VALUE : maxValue.intValue();
			return scope.getRandom().between(theMin, theMax, (int) theStep);
		}
		final double theMin = minValue == null ? Double.MIN_VALUE : minValue.doubleValue();
		final double theMax = maxValue == null ? Double.MAX_VALUE : maxValue.doubleValue();
		return scope.getRandom().between(theMin, theMax, theStep);
	}

	@Override
	public Set<Object> neighborValues(final IScope scope) throws GamaRuntimeException {
		try (Collector.AsSet<Object> neighborValues = Collector.getSet()) {
			if (getAmongValue(scope) != null && !getAmongValue(scope).isEmpty()) {
				final int index = getAmongValue(scope).indexOf(this.value(scope));
				if (index > 0) { neighborValues.add(getAmongValue(scope).get(index - 1)); }
				if (index < getAmongValue(scope).size() - 1) {
					neighborValues.add(getAmongValue(scope).get(index + 1));
				}
				return neighborValues.items();
			}
			final double theStep = stepValue == null ? 1.0 : stepValue.doubleValue();
			if (type.id() == IType.INT) {
				final int theMin = minValue == null ? Integer.MIN_VALUE : minValue.intValue();
				final int theMax = maxValue == null ? Integer.MAX_VALUE : maxValue.intValue();
				final int val = Cast.asInt(scope, value(scope));
				if (val >= theMin + (int) theStep) { neighborValues.add(val - (int) theStep); }
				if (val <= theMax - (int) theStep) { neighborValues.add(val + (int) theStep); }
			} else if (type.id() == IType.FLOAT) {
				final double theMin = minValue == null ? Double.MIN_VALUE : minValue.doubleValue();
				final double theMax = maxValue == null ? Double.MAX_VALUE : maxValue.doubleValue();
				final double removeZ = Math.max(100000.0, 1.0 / theStep);
				final double val = Cast.asFloat(null, value(scope));
				if (val >= theMin + theStep) {
					final double valLow = Math.round((val - theStep) * removeZ) / removeZ;
					neighborValues.add(valLow);
				}
				if (val <= theMax - theStep) {
					final double valHigh = Math.round((val + theStep) * removeZ) / removeZ;
					neighborValues.add(valHigh);
				}
			}
			return neighborValues.items();
		}
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
		if (minValue == null && min != null) { minValue = (Number) min.value(scope); }
		return minValue;
	}

	@Override
	public Number getMaxValue(final IScope scope) {
		if (maxValue == null && max != null) { maxValue = (Number) max.value(scope); }
		return maxValue;
	}

	@Override
	public List getAmongValue(final IScope scope) {
		if (amongValue == null && among != null) { amongValue = Cast.asList(scope, among.value(scope)); }
		return amongValue;
	}

	@Override
	public Number getStepValue(final IScope scope) {
		if (stepValue == null && step != null) { stepValue = (Number) step.value(scope); }
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
		final List<IStatement> statements = new ArrayList<>();
		for (final ISymbol c : commands) {
			if (c instanceof IStatement) { statements.add((IStatement) c); }
		}
		if (!statements.isEmpty()) {
			final IDescription d =
					DescriptionFactory.create(IKeyword.ACTION, getDescription(), IKeyword.NAME, "inline");
			action = new ActionStatement(d);
			action.setChildren(statements);
			listeners.add((scope, v) -> {
				scope.getExperiment().executeAction(action);
			});
		}
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
		if (unitLabel == null && canBeExplored()) return computeExplorableLabel(scope);
		return unitLabel;
	}

	private String computeExplorableLabel(final IScope scope) {
		final List l = getAmongValue(scope);
		if (l != null) return "among " + l;
		final Number theMax = getMaxValue(scope);
		final Number theMin = getMinValue(scope);
		final Number theStep = getStepValue(scope);
		return "between " + theMin + " and " + theMax + " every " + theStep;
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
		if (slider == null) return true;
		return Cast.asBool(scope, slider.value(scope));
	}

	@Override
	public String[] getEnablement() {
		return this.enables;
	}

	@Override
	public String[] getDisablement() {
		return this.disables;
	}

}
