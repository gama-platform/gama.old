/*******************************************************************************************************
 *
 * SolveStatement.java, in ummisco.gaml.extensions.maths, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gaml.extensions.maths.ode.statements;

import java.util.Arrays;
import java.util.List;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.ConstantExpression;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gaml.extensions.maths.ode.MathConstants;
import ummisco.gaml.extensions.maths.ode.statements.SolveStatement.SolveValidator;
import ummisco.gaml.extensions.maths.ode.utils.solver.AdamsBashforthSolver;
import ummisco.gaml.extensions.maths.ode.utils.solver.AdamsMoultonSolver;
import ummisco.gaml.extensions.maths.ode.utils.solver.DormandPrince54Solver;
import ummisco.gaml.extensions.maths.ode.utils.solver.DormandPrince853Solver;
import ummisco.gaml.extensions.maths.ode.utils.solver.EulerSolver;
import ummisco.gaml.extensions.maths.ode.utils.solver.GillSolver;
import ummisco.gaml.extensions.maths.ode.utils.solver.GraggBulirschStoerSolver;
import ummisco.gaml.extensions.maths.ode.utils.solver.HighamHall54Solver;
import ummisco.gaml.extensions.maths.ode.utils.solver.LutherSolver;
import ummisco.gaml.extensions.maths.ode.utils.solver.MidpointSolver;
import ummisco.gaml.extensions.maths.ode.utils.solver.Rk4Solver;
import ummisco.gaml.extensions.maths.ode.utils.solver.Solver;
import ummisco.gaml.extensions.maths.ode.utils.solver.ThreeEighthesSolver;

/**
 * The Class SolveStatement.
 */
@facets (
		value = { @facet (
				name = IKeyword.EQUATION,
				type = IType.ID,
				optional = false,
				doc = @doc ("the equation system identifier to be numerically solved")),

				@facet (
						name = IKeyword.METHOD,
						type = { IType.STRING },
						optional = true,
						doc = @doc (
								value = "integration method (can be one of \"Euler\", \"ThreeEighthes\", \"Midpoint\", \"Gill\", \"Luther\", \"rk4\" or \"dp853\", \"AdamsBashforth\", \"AdamsMoulton\", "
										+ "\"DormandPrince54\", \"GraggBulirschStoer\",  \"HighamHall54\") (default value: \"rk4\") or the corresponding constant")),
				@facet (
						name = "integrated_times",
						type = IType.LIST,
						optional = true,
						doc = @doc (
								deprecated = "do not work anymore, use t[] instead; it is automatically updated.",
								value = "time interval inside integration process")),
				@facet (
						name = "integrated_values",
						type = IType.LIST,
						optional = true,
						doc = @doc (
								deprecated = "do not work anymore, use S[], I[] or any variable of the equations instead; it is automatically updated.",
								value = "list of variables's value inside integration process")),
				@facet (
						name = "t0",
						type = IType.FLOAT,
						optional = true,
						doc = @doc (
								value = "the first bound of the integration interval (defaut value: cycle*step, the time at the begining of the current cycle.)")),
				@facet (
						name = "tf",
						type = IType.FLOAT,
						optional = true,
						doc = @doc (
								value = "the second bound of the integration interval. Can be smaller than t0 for a backward integration (defaut value: cycle*step, the time at the begining of the current cycle.)")),
				@facet (
						name = IKeyword.STEP,
						type = IType.FLOAT,
						optional = true,
						doc = @doc (
								value = "(deprecated) integration step, use with fixed step integrator methods (default value: 0.005*step)")),
				@facet (
						name = "step_size",
						type = IType.FLOAT,
						optional = true,
						doc = @doc (
								value = "integration step, use with fixed step integrator methods (default value: 0.005*step)")),
				@facet (
						name = "min_step",
						type = IType.FLOAT,
						optional = true,
						doc = @doc (
								value = "minimal step, (used with dp853 method only), (sign is irrelevant, regardless of integration direction, forward or backward), the last step can be smaller than this value")),
				@facet (
						name = "max_step",
						type = IType.FLOAT,
						optional = true,
						doc = @doc (
								value = "maximal step, (used with dp853 method only), (sign is irrelevant, regardless of integration direction, forward or backward), the last step can be smaller than this value")),
				@facet (
						name = "scalAbsoluteTolerance",
						type = IType.FLOAT,
						optional = true,
						doc = @doc (
								value = "allowed absolute error (used with dp853 method only)")),
				@facet (
						name = "scalRelativeTolerance",
						type = IType.FLOAT,
						optional = true,
						doc = @doc (
								value = "allowed relative error (used with dp853 method only)")),
				@facet (
						name = "nSteps",
						type = IType.FLOAT,
						optional = true,
						doc = @doc (
								value = "Adams-Bashforth and Adams-Moulton methods only. The number of past steps used for computation excluding the one being computed (default value: 2")) },
		omissible = IKeyword.EQUATION)
@symbol (
		name = { IKeyword.SOLVE },
		kind = ISymbolKind.SINGLE_STATEMENT,
		with_sequence = false,
		concept = { IConcept.EQUATION, IConcept.MATH })
@inside (
		kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@validator (SolveValidator.class)
@doc (
		value = "Solves all equations which matched the given name, with all systems of agents that should solved simultaneously.",
		usages = { @usage (
				value = "",
				examples = { @example (
						value = "solve SIR method: #rk4 step:0.001;",
						isExecutable = false) }) })
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class SolveStatement extends AbstractStatement implements MathConstants {

	/**
	 * The Class SolveValidator.
	 */
	public static class SolveValidator implements IDescriptionValidator<IDescription> {

		@Override
		public void validate(final IDescription desc) {
			final IExpression clen = desc.getFacetExpr(IKeyword.CYCLE_LENGTH);
			if (clen != null) {
				desc.warning("The cycle_length is deprecated, please use the unit multiplying in equation",
						IGamlIssue.GENERAL);
			}
			final IExpression stepE = desc.getFacetExpr("step");
			if (stepE != null) {
				desc.warning("This facet is deprecated and be removed soon, please use step_size instead",
						IGamlIssue.GENERAL);
			}
			final IExpression method = desc.getFacetExpr(IKeyword.METHOD);
			if (method != null && method.isConst() && method.isContextIndependant()) {
				final String methodName = method.literalValue();

				if (methodName != null) {
					if (Adaptive_Stepsize_Integrators.contains(methodName)) {
						if (!desc.hasFacet("min_step") || !desc.hasFacet("max_step")
								|| !desc.hasFacet("scalAbsoluteTolerance") || !desc.hasFacet("scalRelativeTolerance")) {
							desc.error(
									"For Adaptive Stepsize Integrators, the facets min_step, max_step, scalAbsoluteTolerance and scalRelativeTolerance have to be defined. Example: min_step:0.01 max_step:0.1 scalAbsoluteTolerance:0.0001 scalRelativeTolerance:0.0001",
									IGamlIssue.GENERAL);
						}
					} else if (!Fixed_Step_Integrators.contains(methodName)) {
						desc.error(
								"The method facet must have for value either \"Euler\", \"ThreeEighthes\", \"Midpoint\", \"Gill\", \"Luther\", \"rk4\" or \"dp853\",\"AdamsBashforth\", \"AdamsMoulton\", "
										+ "\"DormandPrince54\", \"GraggBulirschStoer\", \"HighamHall54\"",
								IGamlIssue.GENERAL);
					}
				} else {
					desc.error(
							"The method facet must have for value either \"Euler\", \"ThreeEighthes\", \"Midpoint\", \"Gill\", \"Luther\", \"rk4\" or \"dp853\", \"AdamsBashforth\", \"AdamsMoulton\", "
									+ "\"DormandPrince54\", \"GraggBulirschStoer\", \"HighamHall54\"",
							IGamlIssue.GENERAL);
				}
			}
		}
	}

	/** The Constant Fixed_Step_Integrators. */
	final static List<String> Fixed_Step_Integrators = Arrays.asList(Euler, ThreeEighthes, Midpoint, Gill, Luther, rk4);

	/** The Constant Adaptive_Stepsize_Integrators. */
	final static List<String> Adaptive_Stepsize_Integrators =
			Arrays.asList(dp853, AdamsBashforth, AdamsMoulton, DormandPrince54, GraggBulirschStoer, HighamHall54);

	/** The equation name. */
	final String equationName;

	/** The solver name. */
	String solverName;

	/** The system of equations. */
	SystemOfEquationsStatement systemOfEquations;

	/** The time final exp. */
	final IExpression solverExp, stepExp, nStepsExp, minStepExp, maxStepExp, absTolerExp, relTolerExp, timeInitExp,
			timeFinalExp;// ,discretExp,integrationTimesExp,cycleExp,
	// integratedValuesExp;

	/**
	 * Instantiates a new solve statement.
	 *
	 * @param desc
	 *            the desc
	 */
	public SolveStatement(final IDescription desc) {
		super(desc);
		equationName = getFacet(IKeyword.EQUATION).literalValue();
		// IExpression sn = getFacet(IKeyword.METHOD);
		// solverName = sn == null ? "rk4" : sn.literalValue();
		solverExp = getFacet(IKeyword.METHOD);
		stepExp = getFacet("step_size") == null
				? getFacet("step") == null ? new ConstantExpression(0.005d) : getFacet("step") : getFacet("step_size");
		nStepsExp = getFacet("nSteps");
		minStepExp = getFacet("min_step");
		maxStepExp = getFacet("max_step");
		absTolerExp = getFacet("scalAbsoluteTolerance");
		relTolerExp = getFacet("scalRelativeTolerance");
		timeInitExp = getFacet("t0");
		timeFinalExp = getFacet("tf");
	}

	/**
	 * Inits the system of equations.
	 *
	 * @param scope
	 *            the scope
	 * @return true, if successful
	 */
	private boolean initSystemOfEquations(final IScope scope) {
		if (solverName == null) {
			if (solverExp == null) {
				solverName = "rk4";
			} else {
				solverName = Cast.asString(scope, solverExp.value(scope));
				if (Adaptive_Stepsize_Integrators.contains(solverName)) {
					if (minStepExp == null || maxStepExp == null || absTolerExp == null || relTolerExp == null)
						throw GamaRuntimeException.error(
								"For Adaptive Stepsize Integrators, the facets min_step, max_step, scalAbsoluteTolerance and scalRelativeTolerance have to be defined. Example: min_step:0.01 max_step:0.1 scalAbsoluteTolerance:0.0001 scalRelativeTolerance:0.0001",
								scope);
				} else if (!Fixed_Step_Integrators.contains(solverName)) throw GamaRuntimeException.error(
						"The method must be either 'Euler', 'ThreeEighthes', 'Midpoint', 'Gill', 'Luther', 'rk4', 'dp853','AdamsBashforth', 'AdamsMoulton', 'DormandPrince54', 'GraggBulirschStoer' or 'HighamHall54'",
						scope);

			}
		}

		if (systemOfEquations == null)

		{
			systemOfEquations =
					scope.getAgent().getSpecies().getStatement(SystemOfEquationsStatement.class, equationName);
		}
		return systemOfEquations != null;
	}

	/**
	 * Internal integrated value.
	 *
	 * @param scope
	 *            the scope
	 * @param agent
	 *            the agent
	 * @param var
	 *            the var
	 * @return the i list
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = { "internal_integrated_value" },
			content_type = IType.FLOAT,
			category = { IOperatorCategory.CONTAINER },
			concept = { IConcept.EQUATION })
	@doc ("For internal use only. Corresponds to the implementation, for agents, of the access to containers with [index]")
	@no_test
	public static IList internal_integrated_value(final IScope scope, final IExpression agent, final IExpression var)
			throws GamaRuntimeException {
		// if agent not null
		final IAgent a = Cast.asAgent(scope, agent.value(scope));
		// if a not null
		final IMap<String, IList<Double>> result = (IMap<String, IList<Double>>) a.getAttribute("__integrated_values");
		if (result != null) return result.get(a + var.getName());
		return GamaListFactory.EMPTY_LIST;
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		if (!initSystemOfEquations(scope)) return null;

		final double simStepDurationFromUnit = scope.getSimulation().getTimeStep(scope);
		double stepSize = Cast.asFloat(scope, stepExp.value(scope));
		// FIXME Must deprecate and remove facet Step, which is replaced by step_size
		if (getFacet(IKeyword.STEP) == null && getFacet("step_size") == null) {
			stepSize = stepSize * simStepDurationFromUnit;
		}

		final Solver solver = createSolver(scope, stepSize);
		// if (solver == null) { return null; }
		final double timeInit =
				timeInitExp == null ? scope.getSimulation().getClock().getCycle() * simStepDurationFromUnit
						: Cast.asFloat(scope, timeInitExp.value(scope));
		final double timeFinal =
				timeFinalExp == null ? (scope.getSimulation().getClock().getCycle() + 1) * simStepDurationFromUnit// scope.getSimulationScope().getClock().getStep()
						: Cast.asFloat(scope, timeFinalExp.value(scope));
		solver.solve(scope, systemOfEquations, timeInit, timeFinal, getIntegratedValues(scope));

		return null;
	}

	/**
	 * Creates the solver.
	 *
	 * @param scope
	 *            the scope
	 * @param step
	 *            the step
	 * @return the solver
	 */
	private Solver createSolver(final IScope scope, final double step) {
		final IMap<String, IList<Double>> integratedValues = getIntegratedValues(scope);
		int nSteps = 2;
		double minStep = 0.1, maxStep = 0.1, scalAbsoluteTolerance = 0.1, scalRelativeTolerance = 0.1;

		if (Adaptive_Stepsize_Integrators.contains(solverName)) {
			minStep = Cast.asFloat(scope, minStepExp.value(scope));
			maxStep = Cast.asFloat(scope, maxStepExp.value(scope));
			scalAbsoluteTolerance = Cast.asFloat(scope, absTolerExp.value(scope));
			scalRelativeTolerance = Cast.asFloat(scope, relTolerExp.value(scope));
			if (nStepsExp != null) { nSteps = Cast.asInt(scope, nStepsExp.value(scope)); }
		}

		return switch (solverName) {
			case Euler -> new EulerSolver(step, integratedValues);
			case ThreeEighthes -> new ThreeEighthesSolver(step, integratedValues);
			case Midpoint -> new MidpointSolver(step, integratedValues);
			case Gill -> new GillSolver(step, integratedValues);
			case Luther -> new LutherSolver(step, integratedValues);
			case rk4 -> new Rk4Solver(step, integratedValues);
			case dp853 -> new DormandPrince853Solver(minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance,
					integratedValues);
			case DormandPrince54 -> new DormandPrince54Solver(minStep, maxStep, scalAbsoluteTolerance,
					scalRelativeTolerance, integratedValues);
			case GraggBulirschStoer -> new GraggBulirschStoerSolver(minStep, maxStep, scalAbsoluteTolerance,
					scalRelativeTolerance, integratedValues);
			case HighamHall54 -> new HighamHall54Solver(minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance,
					integratedValues);
			case AdamsBashforth -> new AdamsBashforthSolver(nSteps, minStep, maxStep, scalAbsoluteTolerance,
					scalRelativeTolerance, integratedValues);
			case AdamsMoulton -> new AdamsMoultonSolver(nSteps, minStep, maxStep, scalAbsoluteTolerance,
					scalRelativeTolerance, integratedValues);
			default -> new Rk4Solver(step, integratedValues);
		};
	}

	/**
	 * Gets the integrated values.
	 *
	 * @param scope
	 *            the scope
	 * @return the integrated values
	 */
	private IMap<String, IList<Double>> getIntegratedValues(final IScope scope) {
		IMap<String, IList<Double>> result =
				(IMap<String, IList<Double>>) scope.getAgent().getAttribute("__integrated_values");
		if (result == null) {
			result = GamaMapFactory.create(Types.STRING, Types.LIST, 0);
			scope.getAgent().setAttribute("__integrated_values", result);
		}
		return result;
	}

}
