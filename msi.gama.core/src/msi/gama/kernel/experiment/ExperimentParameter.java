/*******************************************************************************************************
 *
 * ExperimentParameter.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.experiment;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.locationtech.jts.util.NumberUtil;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.shape.GamaPoint;
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
import msi.gama.util.GamaDate;
import msi.gama.util.GamaDateInterval;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.Symbol;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.ExperimentDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.expressions.ConstantExpression;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Dates;
import msi.gaml.statements.ActionStatement;
import msi.gaml.statements.IExecutable;
import msi.gaml.statements.IStatement;
import msi.gaml.types.GamaDateType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import msi.gaml.variables.IVariable;
import msi.gaml.variables.Variable;

/**
 * The Class ExperimentParameter.
 */

/**
 * The Class ExperimentParameter.
 */
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
						doc = @doc ("the name of the variable (that should be declared in global)")),
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
								// AD deprecation temporarily removed as this facet is used internally now
								// deprecated = "Move the block of statements at the end of the parameter declaration
								// instead",
								value = "Provides a block of statements that will be executed whenever the value of the parameter changes")),
				@facet (
						name = IKeyword.ENABLES,
						type = IType.LIST,
						optional = true,
						doc = @doc ("a list of global variables whose parameter editors will be enabled when this parameter value is set to true or to a value that casts to true (they are otherwise disabled)")),
				@facet (
						name = IKeyword.DISABLES,
						type = IType.LIST,
						optional = true,
						doc = @doc ("a list of global variables whose parameter editors will be disabled when this parameter value is set to true or to a value that casts to true (they are otherwise enabled)")),
				@facet (
						name = IKeyword.UPDATES,
						type = IType.LIST,
						optional = true,
						doc = @doc ("a list of global variables whose parameter editors will be updated when this parameter value is changed "
								+ "(their min, max, step and among values will be updated accordingly if they depend on this parameter. "
								+ "Note that it might lead to some inconsistencies, for instance a parameter value which becomes out of range, "
								+ "or which does not belong anymore to a list of possible values. In these cases, the value of the affected parameter will not change)")),
				@facet (
						name = "in_workspace",
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Makes only sense for file parameters. Whether the file selector will be restricted to the workspace or not")),
				@facet (
						name = "extensions",
						type = IType.LIST,
						of = IType.STRING,
						optional = true,
						doc = @doc ("Makes only sense for file parameters. A list of file extensions (like 'gaml', 'shp', etc.) that restricts the choice offered to the users to certain file types (folders not concerned). Default is empty, effectively accepting all files")),
				@facet (
						name = "slider",
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Whether or not to display a slider for entering an int or float value. Default is true when max and min values are defined, false otherwise. "
								+ "If no max or min value is defined, setting this facet to true will have no effect")),
				@facet (
						name = "colors",
						type = IType.LIST,
						of = IType.COLOR,
						optional = true,
						doc = @doc ("The colors of the control in the UI. An empty list has no effects. Only used for sliders and switches so far. For sliders, "
								+ "3 colors will allow to specify the color of the left section, the thumb and the right section (in this order); 2 colors will "
								+ "define the left and right sections only (thumb will be dark green); 1 color will define the left section and the thumb. "
								+ "For switches, 2 colors will define the background for respectively the left 'true' and right 'false' sections. 1 color will define both backgrounds")),
				@facet (
						name = "labels",
						type = IType.LIST,
						of = IType.STRING,
						optional = true,
						doc = @doc ("The labels that will be displayed for switches (instead of True/False)")),
				@facet (
						name = IKeyword.STEP,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("the increment step (mainly used in batch mode to express the variation step between simulation)")),

				@facet (
						name = "read_only",
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Whether this parameter is read-only or editable")),
				@facet (
						name = IKeyword.AMONG,
						type = IType.LIST,
						optional = true,
						doc = @doc ("the list of possible values that this parameter can take")) },
		omissible = IKeyword.NAME)
@symbol (
		name = { IKeyword.PARAMETER },
		kind = ISymbolKind.PARAMETER,
		with_sequence = true,

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

	/** The undefined. */
	static final Object UNDEFINED = new Object();

	/** The value. */
	private Object value = UNDEFINED;

	/** The max value. */
	// Object minValue, maxValue;

	/** The step value. */
	// Object stepValue;

	/** The among value. */
	// private List amongValue;

	/** The enables. */
	final private String[] disables, enables, extensions, updates, labels;

	/** The unit label. */
	String varName, title, category, unitLabel;

	/** The type. */
	IType type = Types.NO_TYPE;

	/** The is editable. */
	boolean isEditable;

	/** The can be null. */
	boolean canBeNull;

	/** The is defined. */
	boolean isDefined = true;

	/** The is experiment. */
	// if true, means the target of the parameter is a variable defined in experiment
	boolean isExperiment = false;

	/** The on change. */
	final IExpression init, among, min, max, step, slider, onChange, inWorkspace;

	/** The listeners. */
	final List<ParameterChangeListener> listeners = new ArrayList<>();

	/** The action. */
	ActionStatement action;

	/**
	 * Instantiates a new experiment parameter.
	 *
	 * @param sd
	 *            the sd
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public ExperimentParameter(final IDescription sd) throws GamaRuntimeException {
		super(sd);
		final VariableDescription desc = (VariableDescription) sd;
		setName(desc.getLitteral(IKeyword.VAR));
		type = desc.getGamlType();
		title = sd.getName();
		unitLabel = getLiteral(IKeyword.UNIT);

		min = getFacet(IKeyword.MIN);
		final IScope runtimeScope = GAMA.getRuntimeScope();
		if (min != null && min.isConst()) { getMinValue(runtimeScope); }
		max = getFacet(IKeyword.MAX);
		if (max != null && max.isConst()) { getMaxValue(runtimeScope); }
		step = getFacet(IKeyword.STEP);
		if (step != null && step.isConst()) { getStepValue(runtimeScope); }
		among = getFacet(IKeyword.AMONG);
		// if (among != null && among.isConst()) { getAmongValue(runtimeScope); }
		onChange = getFacet(IKeyword.ON_CHANGE);
		if (onChange != null) {
			listeners.add((scope, v) -> {
				final IExecutable on_changer =
						scope.getExperiment().getSpecies().getAction(Cast.asString(scope, onChange.value(scope)));
				scope.getExperiment().executeAction(on_changer);
			});

		}
		inWorkspace = getFacet("in_workspace");

		slider = getFacet("slider");
		final IExpressionDescription d = getDescription().getFacet(IKeyword.DISABLES);
		final IExpressionDescription e = getDescription().getFacet(IKeyword.ENABLES);
		final IExpressionDescription f = getDescription().getFacet(IKeyword.EXTENSIONS);
		final IExpressionDescription g = getDescription().getFacet(IKeyword.UPDATES);
		final IExpressionDescription h = getDescription().getFacet("labels");

		disables = d != null ? d.getStrings(getDescription(), false).toArray(new String[0]) : EMPTY_STRINGS;
		enables = e != null ? e.getStrings(getDescription(), false).toArray(new String[0]) : EMPTY_STRINGS;
		extensions = f != null ? f.getStrings(getDescription(), false).toArray(new String[0]) : EMPTY_STRINGS;
		updates = g != null ? g.getStrings(getDescription(), false).toArray(new String[0]) : EMPTY_STRINGS;
		String[] tab = h != null ? h.getStrings(getDescription(), false).toArray(new String[0]) : SWITCH_STRINGS;
		labels = tab.length != 2 ? SWITCH_STRINGS : tab;
		final VariableDescription targetedGlobalVar = findTargetedVar(sd);
		init = hasFacet(IKeyword.INIT) ? getFacet(IKeyword.INIT) : targetedGlobalVar.getFacetExpr(IKeyword.INIT);
		isEditable = !targetedGlobalVar.isNotModifiable();
		if (isEditable) {
			final IExpression ed = getFacet("read_only");
			if (ed != null) { isEditable = !Cast.asBool(runtimeScope, ed.value(runtimeScope)); }
		}
		isExperiment = targetedGlobalVar.isDefinedInExperiment();

		setCategory(desc.getLitteral(IKeyword.CATEGORY));
	}

	/**
	 * Find targeted var.
	 *
	 * @param parameterDescription
	 *            the parameter description
	 * @return the variable description
	 */
	private VariableDescription findTargetedVar(final IDescription parameterDescription) {
		// We look first in the model to make sure that built-in parameters (like seed) are correctly retrieved
		final ModelDescription wd = parameterDescription.getModelDescription();
		VariableDescription targetedGlobalVar = wd.getAttribute(varName);
		if (targetedGlobalVar == null) {
			final ExperimentDescription ed = (ExperimentDescription) parameterDescription.getEnclosingDescription();
			targetedGlobalVar = ed.getAttribute(varName);
			isExperiment = true;
		}
		return targetedGlobalVar;
	}

	/**
	 * Instantiates a new experiment parameter.
	 *
	 * @param scope
	 *            the scope
	 * @param p
	 *            the p
	 */
	public ExperimentParameter(final IScope scope, final IParameter p) {
		this(scope, p, p.getTitle(), p.getCategory(), p.getAmongValue(scope), false);
	}

	/**
	 * Instantiates a new experiment parameter.
	 *
	 * @param scope
	 *            the scope
	 * @param p
	 *            the p
	 * @param title
	 *            the title
	 * @param category
	 *            the category
	 * @param among
	 *            the among
	 * @param canBeNull
	 *            the can be null
	 */
	public ExperimentParameter(final IScope scope, final IParameter p, final String title, final String category,
			final List among, final boolean canBeNull) {
		this(scope, p, title, category, null, among, canBeNull);
	}

	/**
	 * Instantiates a new experiment parameter.
	 *
	 * @param scope
	 *            the scope
	 * @param p
	 *            the p
	 * @param title
	 *            the title
	 * @param category
	 *            the category
	 * @param unit
	 *            the unit
	 * @param among
	 *            the among
	 * @param canBeNull
	 *            the can be null
	 */
	public ExperimentParameter(final IScope scope, final IParameter p, final String title, final String category,
			final String unit, final List among, final boolean canBeNull) {
		super(null);
		this.slider = null;
		this.title = title;
		disables = EMPTY_STRINGS;
		enables = EMPTY_STRINGS;
		labels = EMPTY_STRINGS;
		this.canBeNull = canBeNull;
		// this.order = p.getDefinitionOrder();
		// this.amongValue = among;
		if (among != null) {
			this.among = new ConstantExpression(among);
		} else {
			this.among = null;
		}
		Object minValue = p.getMinValue(scope);
		if (minValue != null) {
			this.min = new ConstantExpression(minValue);
		} else {
			min = null;
		}
		Object maxValue = p.getMaxValue(scope);
		if (maxValue != null) {
			this.max = new ConstantExpression(maxValue);
		} else {
			max = null;
		}
		Object stepValue = p.getStepValue(scope);
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
		this.isExperiment = p.isDefinedInExperiment();
		inWorkspace = null;
		extensions = EMPTY_STRINGS;
		updates = EMPTY_STRINGS;
	}

	@Override
	public void setName(final String name2) {
		varName = name2;
		if (title == null) { title = name2; }
	}

	@Override
	public List<GamaColor> getColors(final IScope scope) {
		final IExpression exp = getFacet("colors");
		return exp == null ? null
				: (List<GamaColor>) Types.LIST.cast(scope, exp.value(scope), null, Types.INT, Types.COLOR, false);
	}

	@Override
	public GamaColor getColor(final IScope scope) {
		List<GamaColor> colors = getColors(scope);
		if (colors == null || colors.isEmpty()) return null;
		return colors.get(0);
	}

	@Override
	public void dispose() {
		super.dispose();
		listeners.clear();
	}

	/**
	 * Sets the type.
	 *
	 * @param iType
	 *            the new type
	 */
	private void setType(final IType iType) { type = iType; }

	@Override
	public boolean isEditable() { return isEditable; }

	@Override
	public boolean isDefined() { return isDefined; }

	@Override
	public void setDefined(final boolean defined) { isDefined = defined; }

	@Override
	public void setEditable(final boolean editable) { isEditable = editable; }

	@Override
	public void addChangedListener(final ParameterChangeListener listener) {
		if (!listeners.contains(listener)) { listeners.add(listener); }
	}

	/**
	 * Verify min.
	 *
	 * @param newValue
	 *            the new value
	 * @return the object
	 */
	@SuppressWarnings ("unchecked")
	private Object verifyMin(final IScope scope, final Object newValue) {
		if (!(newValue instanceof Comparable nc)) return newValue;
		Comparable mc = getMinValue(scope);
		if (mc != null && mc.compareTo(nc) > 0) return mc;
		return newValue;
	}

	/**
	 * Verify max.
	 *
	 * @param newValue
	 *            the new value
	 * @return the object
	 */
	@SuppressWarnings ("unchecked")
	private Object verifyMax(final IScope scope, final Object newValue) {
		if (!(newValue instanceof Comparable nc)) return newValue;
		Comparable mc = getMaxValue(scope);
		if (mc != null && mc.compareTo(nc) < 0) return mc;
		return newValue;
	}

	/**
	 * Sets the and verify value.
	 *
	 * @param scope
	 *            the scope
	 * @param val
	 *            the val
	 */
	public void setAndVerifyValue(final IScope scope, final Object val) {
		Object newValue = verifyMin(scope, verifyMax(scope, type.cast(scope, val, null, false)));
		newValue = filterWithAmong(scope, newValue);
		if (value != UNDEFINED) {
			for (final ParameterChangeListener listener : listeners) { listener.changed(scope, newValue); }
		}
		value = newValue;
	}

	@Override
	public void setValueNoCheckNoNotification(final Object value) { this.value = value; }

	/**
	 * Filter with among.
	 *
	 * @param scope
	 *            the scope
	 * @param newValue
	 *            the new value
	 * @return the object
	 */
	private Object filterWithAmong(final IScope scope, final Object newValue) {
		List amongValue = getAmongValue(scope);
		if (amongValue == null || amongValue.isEmpty()) return newValue;
		if (Types.FLOAT.equals(this.getType())) {
			final double newDouble = Cast.asFloat(scope, newValue);
			for (final Object o : amongValue) {
				final Double d = Cast.asFloat(scope, o);
				final Double tolerance = 0.0000001d;
				if (NumberUtil.equalsWithTolerance(d, newDouble, tolerance)) return d;
			}

		} else if (amongValue.contains(newValue)) return newValue;
		return amongValue.get(0);
	}

	@Override
	public void setValue(final IScope scope, final Object val) {
		if (val == UNDEFINED) {
			List amongValue = getAmongValue(scope);
			if (amongValue != null) {
				value = amongValue.get(scope.getRandom().between(0, amongValue.size() - 1));
			} else if (type.id() == IType.INT || type.id() == IType.FLOAT || type.id() == IType.POINT
					|| type.id() == IType.DATE) {
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

	/**
	 * Try to init.
	 *
	 * @param scope
	 *            the scope
	 */
	public void tryToInit(final IScope scope) {
		if (value != UNDEFINED || init == null) return;
		setValue(scope, init.value(scope));

	}

	/**
	 * Draw random value.
	 *
	 * @param scope
	 *            the scope
	 * @return the comparable
	 */
	private Comparable drawRandomValue(final IScope scope) {
		Object minValue = getMinValue(scope);
		Object maxValue = getMaxValue(scope);
		Object stepValue = getStepValue(scope);
		return switch (type.id()) {
			case IType.INT -> {
				final int iMin = minValue == null ? Integer.MIN_VALUE : Cast.asInt(scope, minValue);
				final int iMax = maxValue == null ? Integer.MAX_VALUE : Cast.asInt(scope, maxValue);
				final int iStep = stepValue == null ? 1 : Cast.asInt(scope, stepValue);
				yield scope.getRandom().between(iMin, iMax, iStep);
			}
			case IType.POINT -> {
				final GamaPoint pStep = stepValue == null ? new GamaPoint(1, 1, 1) : Cast.asPoint(scope, stepValue);
				final GamaPoint pMin =
						minValue == null ? new GamaPoint(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE)
								: Cast.asPoint(scope, minValue);
				final GamaPoint pMax =
						maxValue == null ? new GamaPoint(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE)
								: Cast.asPoint(scope, maxValue);
				yield scope.getRandom().between(pMin, pMax, pStep);
			}
			case IType.DATE -> {
				final double dStep =
						stepValue == null ? Dates.DATES_TIME_STEP.getValue() : Cast.asFloat(scope, stepValue);
				final GamaDate dMin =
						minValue == null ? GamaDate.of(LocalDateTime.now()).minus(Integer.MAX_VALUE, ChronoUnit.SECONDS)
								: GamaDateType.staticCast(scope, minValue, null, false);
				final GamaDate dMax =
						maxValue == null ? GamaDate.of(LocalDateTime.now()).plus(Integer.MAX_VALUE, ChronoUnit.SECONDS)
								: GamaDateType.staticCast(scope, maxValue, null, false);
				yield new GamaDateInterval(dMin, dMax, Duration.of((long) dStep, ChronoUnit.SECONDS)).anyValue(scope);
			}
			default -> {
				final double fStep = stepValue == null ? 1.0 : Cast.asFloat(scope, stepValue);
				final double fMin = minValue == null ? Double.MIN_VALUE : Cast.asFloat(scope, minValue);
				final double fMax = maxValue == null ? Double.MAX_VALUE : Cast.asFloat(scope, maxValue);
				yield scope.getRandom().between(fMin, fMax, fStep);
			}
		};
	}

	@Override
	// AD TODO Will not work with points and dates for the moment

	public Set<Object> neighborValues(final IScope scope) throws GamaRuntimeException {
		try (Collector.AsOrderedSet<Object> neighborValues = Collector.getOrderedSet()) {
			if (getAmongValue(scope) != null && !getAmongValue(scope).isEmpty()) {
				final int index = getAmongValue(scope).indexOf(this.value(scope));
				if (index > 0) { neighborValues.add(getAmongValue(scope).get(index - 1)); }
				if (index < getAmongValue(scope).size() - 1) {
					neighborValues.add(getAmongValue(scope).get(index + 1));
				}
				return neighborValues.items();
			}
			Object minValue = getMinValue(scope);
			Object maxValue = getMaxValue(scope);
			Object stepValue = getStepValue(scope);
			switch (type.id()) {
				case IType.INT:
					final int iMin = minValue == null ? Integer.MIN_VALUE : Cast.asInt(scope, minValue);
					final int iMax = maxValue == null ? Integer.MAX_VALUE : Cast.asInt(scope, maxValue);
					final int iStep = stepValue == null ? 1 : Cast.asInt(scope, stepValue);
					final int iVal = Cast.asInt(scope, value(scope));
					if (iVal >= iMin + iStep) { neighborValues.add(iVal - iStep); }
					if (iVal <= iMax - iStep) { neighborValues.add(iVal + iStep); }
					break;
				case IType.POINT:
					final GamaPoint pStep = stepValue == null ? new GamaPoint(1, 1, 1) : Cast.asPoint(scope, stepValue);
					final GamaPoint pMin =
							minValue == null ? new GamaPoint(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE)
									: Cast.asPoint(scope, minValue);
					final GamaPoint pMax =
							maxValue == null ? new GamaPoint(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE)
									: Cast.asPoint(scope, maxValue);
					final GamaPoint pVal = Cast.asPoint(scope, value(scope));
					for (int i = -1; i <= 1; i++) {
						for (int j = -1; j <= 1; j++) {
							for (int k = -1; k <= 1; k++) {
								if (i == 0 && j == 0 && k == 0) { continue; }
								double x = pVal.x + i * pStep.x;
								double y = pVal.y + j * pStep.y;
								double z = pVal.z + k * pStep.z;
								int reset = 0;
								if (x < pMin.x || x > pMax.x) {
									x = pVal.x;
									reset++;
								}
								if (y < pMin.y || y > pMax.y) {
									y = pVal.y;
									reset++;
								}
								if (z < pMin.z || z > pMax.z) {
									z = pVal.z;
									reset++;
								}
								if (reset < 3) { neighborValues.add(new GamaPoint(x, y, z)); }
							}
						}
					}
					break;
				case IType.DATE:
					final double dStep =
							stepValue == null ? Dates.DATES_TIME_STEP.getValue() : Cast.asFloat(scope, stepValue);
					final GamaDate dMin = minValue == null
							? GamaDate.of(LocalDateTime.now()).minus(Integer.MAX_VALUE, ChronoUnit.SECONDS)
							: GamaDateType.staticCast(scope, minValue, null, false);
					final GamaDate dMax = maxValue == null
							? GamaDate.of(LocalDateTime.now()).plus(Integer.MAX_VALUE, ChronoUnit.SECONDS)
							: GamaDateType.staticCast(scope, maxValue, null, false);
					Duration dd = Duration.of((long) dStep, ChronoUnit.SECONDS);
					final GamaDate dVal = GamaDateType.staticCast(scope, value(scope), null, false);
					if (dVal.isGreaterThan(dMin.plus(dd), false)) { neighborValues.add(dVal.minus(dd)); }
					if (dVal.isSmallerThan(dMax.minus(dd), false)) { neighborValues.add(dVal.plus(dd)); }
					break;
				default:
					final double fStep = stepValue == null ? 1.0 : Cast.asFloat(scope, stepValue);
					final double fMin = minValue == null ? Double.MIN_VALUE : Cast.asFloat(scope, minValue);
					final double fMax = maxValue == null ? Double.MAX_VALUE : Cast.asFloat(scope, maxValue);
					final double fVal = Cast.asFloat(scope, value(scope));
					final double removeZ = Math.max(100000.0, 1.0 / fStep);
					if (fVal >= fMin + fStep) {
						final double valLow = Math.round((fVal - fStep) * removeZ) / removeZ;
						neighborValues.add(valLow);
					}
					if (fVal <= fMax - fStep) {
						final double valHigh = Math.round((fVal + fStep) * removeZ) / removeZ;
						neighborValues.add(valHigh);
					}
			}

			return neighborValues.items();
		}

	}

	@Override
	public String getTitle() { return title; }

	@Override
	public String getName() { return varName; }

	@Override
	public String getCategory() { return category == null ? IParameter.Batch.super.getCategory() : category; }

	@Override
	public void setCategory(final String cat) { category = cat; }

	@Override
	public Object value(final IScope scope) {
		return getValue(scope);
	}

	@Override
	public Object value() {
		return GAMA.run(this::getValue);

	}

	@Override
	public Object getInitialValue(final IScope scope) {
		return getValue(scope);
	}

	@Override
	public Comparable getMinValue(final IScope scope) {
		if (min == null) return null;
		Object minValue = type.cast(scope, min.value(scope), null, false);
		if (!(minValue instanceof Comparable mc)) return null;
		return mc;
	}

	@Override
	public Comparable getMaxValue(final IScope scope) {
		if (max == null) return null;
		Object maxValue = type.cast(scope, max.value(scope), null, false);
		if (!(maxValue instanceof Comparable mc)) return null;
		return mc;
	}

	@Override
	public List getAmongValue(final IScope scope) {
		if (among != null) return Cast.asList(scope, among.value(scope));
		return null;
	}

	@Override
	public Comparable getStepValue(final IScope scope) {
		return step == null ? null : (Comparable) step.value(scope);
		// if (stepValue == null && step != null) { stepValue = step.value(scope); }
		// return (Comparable) stepValue;
	}

	@Override
	public IType getType() { return type; }

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return GAMA.run(scope -> StringUtils.toGaml(getValue(scope), includingBuiltIn));

	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> commands) {
		final List<IStatement> statements = new ArrayList<>();
		for (final ISymbol c : commands) { if (c instanceof IStatement) { statements.add((IStatement) c); } }
		if (!statements.isEmpty()) {
			final IDescription d =
					DescriptionFactory.create(IKeyword.ACTION, getDescription(), IKeyword.NAME, "inline");
			action = new ActionStatement(d);
			action.setChildren(statements);
			listeners.add((scope, v) -> { scope.getExperiment().executeAction(action); });
		}
	}

	@Override
	public String toString() {
		return "Parameter '" + title + "' targets var " + varName;
	}

	/**
	 * Can be null.
	 *
	 * @return true, if successful
	 */
	public boolean canBeNull() {
		return canBeNull;
	}

	@Override
	public boolean canBeExplored() {
		return among != null || min != null && max != null;
	}

	@Override
	public String getUnitLabel(final IScope scope) {
		if (unitLabel == null && canBeExplored()) return computeExplorableLabel(scope);
		return unitLabel;
	}

	/**
	 * Compute explorable label.
	 *
	 * @param scope
	 *            the scope
	 * @return the string
	 */
	private String computeExplorableLabel(final IScope scope) {
		final List l = getAmongValue(scope);
		if (l != null) return "among " + l;
		return "between " + getMinValue(scope) + " and " + getMaxValue(scope) + " every " + getStepValue(scope);
	}

	@Override
	public void setUnitLabel(final String label) { unitLabel = label; }

	/**
	 * Gets the value.
	 *
	 * @param scope
	 *            the scope
	 * @return the value
	 */
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
	public String[] getEnablement() { return this.enables; }

	@Override
	public String[] getDisablement() { return this.disables; }

	@Override
	public boolean isDefinedInExperiment() { return isExperiment; }

	@Override
	public String[] getFileExtensions() { return extensions; }

	@Override
	public String[] getRefreshment() { return updates; }

	@Override
	public boolean isWorkspace() {
		if (inWorkspace == null) return false;
		return GAMA.run(s -> Cast.asBool(s, inWorkspace.value(s)));
	}

	@Override
	public String[] getLabels(final IScope scope) {
		return labels;
	}

}
