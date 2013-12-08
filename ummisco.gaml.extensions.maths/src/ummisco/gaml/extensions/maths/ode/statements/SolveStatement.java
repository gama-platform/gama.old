package ummisco.gaml.extensions.maths.ode.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.combination;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.*;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.types.IType;
import ummisco.gaml.extensions.maths.ode.utils.solver.*;

@facets(value = {
	@facet(name = IKeyword.EQUATION, type = IType.STRING, optional = false),
	@facet(name = IKeyword.METHOD, type = IType.ID /* CHANGE */, optional = true, values = { "rk4", "dp853" }, doc = @doc(value = "integrate method")),
	@facet(name = "integrated_times", type = IType.LIST, optional = true, doc = @doc(value = "time interval inside integration process")),
	@facet(name = "integrated_values", type = IType.LIST, optional = true, doc = @doc(value = "list of Variables's value inside integration process")),
	@facet(name = "discretizing_step", type = IType.INT, optional = true, doc = @doc(value = "number of discret beside 2 step of simulation")),
	@facet(name = "time_initial", type = IType.FLOAT, optional = true, doc = @doc(value = "initial time")),
	@facet(name = "time_final", type = IType.FLOAT, optional = true, doc = @doc(value = "target time for the integration (can be set to a value smaller than t0 for backward integration)")),
	@facet(name = "cycle_length", type = IType.INT, optional = true, doc = @doc(value = "length of simulation cycle which will be synchronize with step of integrator")),
	@facet(name = IKeyword.STEP, type = IType.FLOAT, optional = true, doc = @doc(value = "integration step, use with most integrator method")),
	@facet(name = "min_step", type = IType.FLOAT, optional = true, doc = @doc(value = "minimal step, use with dp853 method, (sign is irrelevant, regardless of integration direction, forward or backward), the last step can be smaller than this")),
	@facet(name = "max_step", type = IType.FLOAT, optional = true, doc = @doc(value = "maximal step, use with dp853 method, (sign is irrelevant, regardless of integration direction, forward or backward), the last step can be smaller than this")),
	@facet(name = "scalAbsoluteTolerance", type = IType.FLOAT, optional = true, doc = @doc(value = "allowed absolute error, use with dp853 method,")),
	@facet(name = "scalRelativeTolerance", type = IType.FLOAT, optional = true, doc = @doc(value = "allowed relative error, use with dp853 method,")) },

combinations = { @combination({ IKeyword.STEP }),
	@combination({ "min_step", "max_step", "scalAbsoluteTolerance", "scalRelativeTolerance" }) }, omissible = IKeyword.EQUATION)
@symbol(name = { IKeyword.SOLVE }, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.SPECIES, ISymbolKind.MODEL })
public class SolveStatement extends AbstractStatementSequence {

	Solver solver;
	final String equationName, solverName;
	SystemOfEquationsStatement equations;
	double timeInit = 0, timeFinal = 1;
	int discret = 0;
	double cycle_length = 1;
	final IExpression stepExp, cycleExp, discretExp, minStepExp, maxStepExp, absTolerExp, relTolerExp, timeInitExp,
		timeFinalExp;

	public SolveStatement(final IDescription desc) {
		super(desc);
		equationName = getFacet(IKeyword.EQUATION).literalValue();
		IExpression sn = getFacet(IKeyword.METHOD);
		solverName = sn == null ? "rk4" : sn.literalValue();
		sn = getFacet(IKeyword.STEP);
		stepExp = sn == null ? new ConstantExpression(1d) : sn;
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
	}

	private SystemOfEquationsStatement getEquations(final IScope scope) {
		if ( equations == null ) {
			equations = scope.getAgentScope().getSpecies().getStatement(SystemOfEquationsStatement.class, equationName);
		}
		return equations;
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		super.privateExecuteIn(scope);

		GamaList integrate_time = new GamaList();
		GamaList integrate_val = new GamaList();

		discret = Cast.asInt(scope, discretExp.value(scope));
		cycle_length = Cast.asFloat(scope, cycleExp.value(scope));
		double step = Cast.asFloat(scope, stepExp.value(scope));

		if ( getEquations(scope) == null ) { return null; }
		equations.currentScope = scope;

		if ( solverName.equals("rk4") ) {
			solver = new Rk4Solver(step, integrate_time, integrate_val);
		} else if ( solverName.equals("dp853") && minStepExp != null && maxStepExp != null && absTolerExp != null &&
			relTolerExp != null ) {
			double minStep = Cast.asFloat(scope, minStepExp.value(scope));
			double maxStep = Cast.asFloat(scope, maxStepExp.value(scope));
			double scalAbsoluteTolerance = Cast.asFloat(scope, absTolerExp.value(scope));
			double scalRelativeTolerance = Cast.asFloat(scope, relTolerExp.value(scope));

			solver =
				new DormandPrince853Solver(minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance,
					integrate_time, integrate_val);
		}

		timeInit = timeInitExp == null ? scope.getClock().getCycle() : Cast.asFloat(scope, timeInitExp.value(scope));
		timeFinal =
			timeFinalExp == null ? scope.getClock().getCycle() + 1 : Cast.asFloat(scope, timeFinalExp.value(scope));

		equations.addExtern(equationName);
		solver.solve(scope, equations, timeInit, timeFinal, cycle_length);
		equations.removeExtern(scope, equationName);

		decreaseDiscretTime(integrate_time, integrate_val, discret);

		if ( getFacet("integrated_times") != null ) {
			((VariableExpression) getFacet("integrated_times")).setVal(scope, integrate_time, false);
		}

		if ( getFacet("integrated_values") != null ) {
			IExpression fv = getFacet("integrated_values").resolveAgainst(scope);
			IExpression[] exp = ((ListExpression) fv).getElements();
			for ( int i = 0; i < exp.length; i++ ) {
				((VariableExpression) exp[i]).setVal(scope, integrate_val.get(i), false);
			}
		}

		return null;
	}

	public void decreaseDiscretTime(final GamaList integratedTimes, final GamaList integratedValues, double d) {
		int size = integratedTimes.size();
		if ( size == 0 ) { return; }
		if ( d == 0 ) {
			d = size;
		}
		int tmp = size / (int) d;

		int i = size - 2;
		while (i > 1) {
			if ( i % tmp != 0 ) {
				integratedTimes.remove(i);
				for ( int j = 0; j < integratedValues.size(); j++ ) {
					((GamaList) integratedValues.get(j)).remove(i);
				}
			}
			i--;
		}
	}

}
