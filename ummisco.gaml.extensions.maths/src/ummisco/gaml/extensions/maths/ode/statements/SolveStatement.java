/*********************************************************************************************
 *
 *
 * 'SolveStatement.java', in plugin 'ummisco.gaml.extensions.maths', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gaml.extensions.maths.ode.statements;

import java.util.ArrayList;
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
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.IList;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.ConstantExpression;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.ListExpression;
import msi.gaml.expressions.SpeciesConstantExpression;
import msi.gaml.expressions.VariableExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
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

@facets(value = {
		@facet(name = IKeyword.EQUATION, type = IType.ID, optional = false, doc = @doc("the equation system identifier to be numerically solved")),
		@facet(name = IKeyword.METHOD, type = IType.ID /* CHANGE */, optional = true, values = { "Euler",
				"ThreeEighthes", "Midpoint", "Gill", "Luther", "rk4", "dp853", "AdamsBashforth", "AdamsMoulton",
				"DormandPrince54", "GraggBulirschStoer",
				"HighamHall54" }, doc = @doc(value = "integrate method (can be only \"Euler\", \"ThreeEighthes\", \"Midpoint\", \"Gill\", \"Luther\", \"rk4\" or \"dp853\", \"AdamsBashforth\", \"AdamsMoulton\", "
						+ "\"DormandPrince54\", \"GraggBulirschStoer\",  \"HighamHall54\") (default value: \"rk4\")")),
		@facet(name = "integrated_times", type = IType.LIST, optional = true, doc = @doc(value = "time interval inside integration process")),
		@facet(name = "integrated_values", type = IType.LIST, optional = true, doc = @doc(value = "list of variables's value inside integration process")),
		@facet(name = "discretizing_step", type = IType.INT, optional = true, doc = @doc(value = "number of discrete between 2 steps of simulation (default value: 0)")),
		@facet(name = "time_initial", type = IType.FLOAT, optional = true, doc = @doc(value = "initial time")),
		@facet(name = "time_final", type = IType.FLOAT, optional = true, doc = @doc(value = "target time for the integration (can be set to a value smaller than t0 for backward integration)")),
		@facet(name = "cycle_length", type = IType.INT, optional = true, doc = @doc(value = "length of simulation cycle which will be synchronize with step of integrator (default value: 1)")),
		@facet(name = IKeyword.STEP, type = IType.FLOAT, optional = true, doc = @doc(value = "integration step, use with most integrator methods (default value: 1)")),
		@facet(name = "min_step", type = IType.FLOAT, optional = true, doc = @doc(value = "minimal step, (used with dp853 method only), (sign is irrelevant, regardless of integration direction, forward or backward), the last step can be smaller than this value")),
		@facet(name = "max_step", type = IType.FLOAT, optional = true, doc = @doc(value = "maximal step, (used with dp853 method only), (sign is irrelevant, regardless of integration direction, forward or backward), the last step can be smaller than this value")),
		@facet(name = "scalAbsoluteTolerance", type = IType.FLOAT, optional = true, doc = @doc(value = "allowed absolute error (used with dp853 method only)")),
		@facet(name = "scalRelativeTolerance", type = IType.FLOAT, optional = true, doc = @doc(value = "allowed relative error (used with dp853 method only)")) }, omissible = IKeyword.EQUATION)
@symbol(name = { IKeyword.SOLVE }, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false, concept = {
		IConcept.EQUATION, IConcept.MATH })
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@validator(SolveValidator.class)
@doc(value = "Solves all equations which matched the given name, with all systems of agents that should solved simultaneously.", usages = {
		@usage(value = "", examples = {
				@example(value = "solve SIR method: \"rk4\" step:0.001;", isExecutable = false) }) })
public class SolveStatement extends AbstractStatement {

	public static class SolveValidator implements IDescriptionValidator<IDescription> {

		@Override
		public void validate(final IDescription desc) {
			final IExpression clen = desc.getFacets().getExpr(IKeyword.CYCLE_LENGTH);
			if(clen != null){
				desc.warning("The cycle_length is deprecated, please use the unit multiplying in equation",
						IGamlIssue.GENERAL);
			}
			final IExpression method = desc.getFacets().getExpr(IKeyword.METHOD);
			if (method != null) {
				final String methodName = method.literalValue();

				if (methodName != null) {
					if (Adaptive_Stepsize_Integrators.contains(methodName)) {
						if (!desc.getFacets().containsKey("min_step") || !desc.getFacets().containsKey("max_step")
								|| !desc.getFacets().containsKey("scalAbsoluteTolerance")
								|| !desc.getFacets().containsKey("scalRelativeTolerance")) {
							desc.error(
									"For Adaptive Stepsize Integrators, the facets min_step, max_step, scalAbsoluteTolerance and scalRelativeTolerance have to be defined. Example: min_step:0.01 max_step:0.1 scalAbsoluteTolerance:0.0001 scalRelativeTolerance:0.0001",
									IGamlIssue.GENERAL);
						}
					} else if (!Fixed_Step_Integrators.contains(methodName)) {
						desc.error(
								"The method facet must have for value either \"Euler\", \"ThreeEighthes\", \"Midpoint\", \"Gill\", \"Luther\", \"rk4\" or \"dp853\",\"AdamsBashforth\", \"AdamsMoulton\", "
										+ "\"DormandPrince54\", \"GraggBulirschStoer\",  \"HighamHall54\"",
								IGamlIssue.GENERAL);
					}
				} else {
					desc.error(
							"The method facet must have for value either \"Euler\", \"ThreeEighthes\", \"Midpoint\", \"Gill\", \"Luther\", \"rk4\" or \"dp853\", \"AdamsBashforth\", \"AdamsMoulton\", "
									+ "\"DormandPrince54\", \"GraggBulirschStoer\",  \"HighamHall54\"",
							IGamlIssue.GENERAL);
				}
			}
		}
	}

	final static List<String> Fixed_Step_Integrators = Arrays
			.asList(new String[] { "Euler", "ThreeEighthes", "Midpoint", "Gill", "Luther", "rk4" });
	final static List<String> Adaptive_Stepsize_Integrators = Arrays.asList(new String[] { "dp853", "AdamsBashforth",
			"AdamsMoulton", "DormandPrince54", "GraggBulirschStoer", "HighamHall54" });

	final String equationName, solverName;
	SystemOfEquationsStatement systemOfEquations;
	final IExpression stepExp, cycleExp, nStepsExp, minStepExp, maxStepExp, absTolerExp, relTolerExp,
			timeInitExp, timeFinalExp;//,discretExp,integrationTimesExp, integratedValuesExp;

	public SolveStatement(final IDescription desc) {
		super(desc);
		equationName = getFacet(IKeyword.EQUATION).literalValue();
		IExpression sn = getFacet(IKeyword.METHOD);
		solverName = sn == null ? "rk4" : sn.literalValue();
		sn = getFacet(IKeyword.STEP);
		stepExp = sn == null ? new ConstantExpression(0.2d) : sn;
		sn = getFacet("cycle_length");
		cycleExp = sn == null ? new ConstantExpression(1d) : sn;
		sn = getFacet("discretizing_step");
		nStepsExp = getFacet("nSteps");
		minStepExp = getFacet("min_step");
		maxStepExp = getFacet("max_step");
		absTolerExp = getFacet("scalAbsoluteTolerance");
		relTolerExp = getFacet("scalRelativeTolerance");
		timeInitExp = getFacet("time_initial");
		timeFinalExp = getFacet("time_final");
//		discretExp = sn == null ? new ConstantExpression(0) : sn;
//		integrationTimesExp = getFacet("integrated_times");
//		integratedValuesExp = getFacet("integrated_values");
	}

	private boolean initSystemOfEquations(final IScope scope) {
		if (systemOfEquations == null) {
			systemOfEquations = scope.getAgent().getSpecies().getStatement(SystemOfEquationsStatement.class,
					equationName);
		}
		return systemOfEquations != null;
	}

	@operator(value = { "internal_integrated_value" }, content_type = IType.FLOAT, category = { IOperatorCategory.CONTAINER }, concept = { IConcept.EQUATION })
	@doc("For internal use only. Corresponds to the implementation, for agents, of the access to containers with [index]")
	public static IList internal_integrated_value(final IScope scope,final IExpression agent, final IExpression var)
		throws GamaRuntimeException {
		// if agent not null
		IAgent a = Cast.asAgent(scope, agent.value(scope));
		// if a not null
		GamaMap<String, IList<Double>> result = (GamaMap<String, IList<Double>>) a.getAttribute("__integrated_values");
		if(result!=null){			
			return result.get(var.getName());
		}
		return GamaListFactory.create();
	}
	
	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		if (!initSystemOfEquations(scope))
			return null;

//		final double cycleLength = Cast.asFloat(scope, cycleExp.value(scope));
		double step = Cast.asFloat(scope, stepExp.value(scope));
//		step = cycleLength > 1.0 ? step / cycleLength : step;

		final Solver solver = createSolver(scope, step);
		if (solver == null)
			return null;

		double timeInit = timeInitExp == null ? scope.getSimulation().getClock().getCycle()
				: Cast.asFloat(scope, timeInitExp.value(scope));
		double timeFinal = timeFinalExp == null ? scope.getSimulation().getClock().getCycle() + 1//scope.getSimulationScope().getClock().getStep()
				: Cast.asFloat(scope, timeFinalExp.value(scope));
//		if (cycleLength > 1.0) {
//			timeInit /= cycleLength;
//			timeFinal /= cycleLength;
//		}

		solver.solve(scope, systemOfEquations, timeInit, timeFinal, getIntegratedValues(scope));

//		if (integrationTimesExp != null) {
//			final List<Double> integrationTimes = getIntegrationTimes(scope);
//			if (integrationTimes != null)
//				((VariableExpression) getFacet("integrated_times")).setVal(scope, integrationTimes, false);
//		}

//		if (integratedValuesExp != null) {
//			final GamaMap<String, IList<Double>> integratedValues = getIntegratedValues(scope);
//			if (integratedValues != null) {
//				final ListExpression fv = (ListExpression) getFacet("integrated_values");
//				IExpression[] L = fv.getElements();
//				for (int i = 0; i < L.length; i++) {
//					((VariableExpression) L[i]).setVal(scope, integratedValues.get(i), false);
//				}
//			}
//		}

		return null;
	}

	private Solver createSolver(final IScope scope, final double step) {
//		final int discret = Math.max(0, Cast.asInt(scope, discretExp.value(scope)));
		final GamaMap<String, IList<Double>> integratedValues = getIntegratedValues(scope);
//		final List<Double> integrationTimes = getIntegrationTimes(scope);
		int nSteps = 2;
		double minStep = 0.1, maxStep = 0.1, scalAbsoluteTolerance = 0.1, scalRelativeTolerance = 0.1;

		if (Adaptive_Stepsize_Integrators.contains(solverName)) {
			minStep = Cast.asFloat(scope, minStepExp.value(scope));
			maxStep = Cast.asFloat(scope, maxStepExp.value(scope));
//			if (cycleLength > 1.0) {
//				minStep /= cycleLength;
//				maxStep /= cycleLength;
//			}
			scalAbsoluteTolerance = Cast.asFloat(scope, absTolerExp.value(scope));
			scalRelativeTolerance = Cast.asFloat(scope, relTolerExp.value(scope));
			if (nStepsExp != null) {
				nSteps = Cast.asInt(scope, nStepsExp.value(scope));
			}
		}

		switch (solverName) {
			case "Euler":
				return new EulerSolver(step, integratedValues);

			case "ThreeEighthes":
				return new ThreeEighthesSolver(step, integratedValues);

			case "Midpoint":
				return new MidpointSolver(step, integratedValues);

			case "Gill":
				return new GillSolver(step, integratedValues);

			case "Luther":
				return new LutherSolver(step, integratedValues);

			case "rk4":
				return new Rk4Solver(step, integratedValues);

			case "dp853":
				return new DormandPrince853Solver(minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance, integratedValues);

			case "DormandPrince54":
				return new DormandPrince54Solver(minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance, integratedValues);

			case "GraggBulirschStoer":
				return new GraggBulirschStoerSolver(minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance, integratedValues);

			case "HighamHall54":
				return new HighamHall54Solver(minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance, integratedValues);

			case "AdamsBashforth":
				return new AdamsBashforthSolver(nSteps, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance, integratedValues);

			case "AdamsMoulton":
				return new AdamsMoultonSolver(nSteps, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance,
						 integratedValues);

			default:
				return new Rk4Solver(step, integratedValues);
		}
	}

	private GamaMap<String, IList<Double>> getIntegratedValues(final IScope scope) {
//		if (integratedValuesExp == null)
//			return null;
		GamaMap<String, IList<Double>> result = (GamaMap<String, IList<Double>>) scope.getAgent().getAttribute("__integrated_values");
		if (result == null) {
			result = new GamaMap<String, IList<Double>>(0, Types.STRING, Types.LIST);
			scope.getAgent().setAttribute("__integrated_values", result);
		}
		return result;
	}

//	private List<Double> getIntegrationTimes(final IScope scope) {
////		if (integrationTimesExp == null)
////			return null;
//		List<Double> result = (List<Double>) scope.getAgentScope().getAttribute("__integrated_times");
//		if (result == null) {
//			result = new ArrayList<>();
//			scope.getAgentScope().setAttribute("__integrated_times", result);
//		}
//		return result;
//	}

}
