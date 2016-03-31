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
import java.util.List;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
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
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.ConstantExpression;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.VariableExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;
import ummisco.gaml.extensions.maths.ode.statements.SolveStatement.SolveValidator;
import ummisco.gaml.extensions.maths.ode.utils.solver.DormandPrince853Solver;
import ummisco.gaml.extensions.maths.ode.utils.solver.Rk4Solver;
import ummisco.gaml.extensions.maths.ode.utils.solver.Solver;

@facets(value = {
		@facet(name = IKeyword.EQUATION, type = IType.ID, optional = false, doc = @doc("the equation system identifier to be numerically solved")),
		@facet(name = IKeyword.METHOD, type = IType.ID /* CHANGE */, optional = true, values = { "rk4",
				"dp853" }, doc = @doc(value = "integrate method (can be only \"rk4\" or \"dp853\") (default value: \"rk4\")")),
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
			final IExpression method = desc.getFacets().getExpr(IKeyword.METHOD);
			if (method != null) {
				final String methodName = method.literalValue();
				if (methodName != null) {
					if ("dp853".equals(methodName)) {
						if (!desc.getFacets().containsKey("min_step") || !desc.getFacets().containsKey("max_step")
								|| !desc.getFacets().containsKey("scalAbsoluteTolerance")
								|| !desc.getFacets().containsKey("scalRelativeTolerance")) {
							desc.error(
									"For method dp853, the facets min_step, max_step, scalAbsoluteTolerance and scalRelativeTolerance have to be defined",
									IGamlIssue.GENERAL);
						}
					} else if (!"rk4".equals(methodName)) {
						desc.error("The method facet must have for value either \"rk4\" or \"dp853\"",
								IGamlIssue.GENERAL);
					}
				} else {
					desc.error("The method facet must have for value either \"rk4\" or \"dp853\"", IGamlIssue.GENERAL);
				}
			}
		}
	}

	final String equationName, solverName;
	SystemOfEquationsStatement systemOfEquations;
	final IExpression stepExp, cycleExp, discretExp, minStepExp, maxStepExp, absTolerExp, relTolerExp, timeInitExp,
			timeFinalExp, integrationTimesExp, integratedValuesExp;

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
		discretExp = sn == null ? new ConstantExpression(0) : sn;
		minStepExp = getFacet("min_step");
		maxStepExp = getFacet("max_step");
		absTolerExp = getFacet("scalAbsoluteTolerance");
		relTolerExp = getFacet("scalRelativeTolerance");
		timeInitExp = getFacet("time_initial");
		timeFinalExp = getFacet("time_final");
		integrationTimesExp = getFacet("integrated_times");
		integratedValuesExp = getFacet("integrated_values");
	}

	private boolean initSystemOfEquations(final IScope scope) {
		if (systemOfEquations == null) {
			systemOfEquations = scope.getAgentScope().getSpecies().getStatement(SystemOfEquationsStatement.class,
					equationName);
		}
		return systemOfEquations != null;
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		if (!initSystemOfEquations(scope))
			return null;

		final double cycleLength = Cast.asFloat(scope, cycleExp.value(scope));
		double step = Cast.asFloat(scope, stepExp.value(scope));
		step = cycleLength > 1.0 ? step / cycleLength : step;

		final Solver solver = createSolver(scope, step, cycleLength);
		if (solver == null)
			return null;

		double timeInit = timeInitExp == null ? scope.getClock().getCycle()
				: Cast.asFloat(scope, timeInitExp.value(scope));
		double timeFinal = timeFinalExp == null ? scope.getClock().getCycle() + scope.getClock().getStep()
				: Cast.asFloat(scope, timeFinalExp.value(scope));
		if (cycleLength > 1.0) {
			timeInit /= cycleLength;
			timeFinal /= cycleLength;
		}

		solver.solve(scope, systemOfEquations, timeInit, timeFinal, cycleLength, getIntegrationTimes(scope),
				getIntegratedValues(scope));

		if (integrationTimesExp != null) {
			final List<Double> integrationTimes = getIntegrationTimes(scope);
			if (integrationTimes != null)
				((VariableExpression) getFacet("integrated_times")).setVal(scope, integrationTimes, false);
		}

		if (integratedValuesExp != null) {
			final List<List<Double>> integratedValues = getIntegratedValues(scope);
			if (integratedValues != null) {
				final IExpression fv = getFacet("integrated_values");
				final IList fvListe = Cast.asList(scope, fv.value(scope));
				fvListe.clear();
				fvListe.addAll(integratedValues);
			}
		}

		return null;
	}

	private Solver createSolver(final IScope scope, final double step, final double cycleLength) {
		final int discret = Math.max(0, Cast.asInt(scope, discretExp.value(scope)));
		final List<List<Double>> integratedValues = getIntegratedValues(scope);
		final List<Double> integrationTimes = getIntegrationTimes(scope);
		if (solverName.equals("rk4")) {
			return new Rk4Solver(step, discret, integrationTimes, integratedValues);
		} else if (solverName.equals("dp853")) {
			double minStep = Cast.asFloat(scope, minStepExp.value(scope));
			double maxStep = Cast.asFloat(scope, maxStepExp.value(scope));
			if (cycleLength > 1.0) {
				minStep /= cycleLength;
				maxStep /= cycleLength;
			}
			final double scalAbsoluteTolerance = Cast.asFloat(scope, absTolerExp.value(scope));
			final double scalRelativeTolerance = Cast.asFloat(scope, relTolerExp.value(scope));

			return new DormandPrince853Solver(minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance, discret,
					integrationTimes, integratedValues);
		}
		return null;

	}

	private List<List<Double>> getIntegratedValues(final IScope scope) {
		if (integratedValuesExp == null)
			return null;
		List<List<Double>> result = (List<List<Double>>) scope.getAgentScope().getAttribute("__integrated_values");
		if (result == null) {
			result = new ArrayList<>();
			scope.getAgentScope().setAttribute("__integrated_values", result);
		}
		return result;
	}

	private List<Double> getIntegrationTimes(final IScope scope) {
		if (integrationTimesExp == null)
			return null;
		List<Double> result = (List<Double>) scope.getAgentScope().getAttribute("__integrated_times");
		if (result == null) {
			result = new ArrayList<>();
			scope.getAgentScope().setAttribute("__integrated_times", result);
		}
		return result;
	}

}
