package ummisco.gaml.extensions.maths.statements;

import java.util.List;
import java.util.Map;
import java.util.Random;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.simulation.SimulationClock;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IVarExpression;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.Facets.Facet;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;
import org.apache.commons.math3.ode.*;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;

import ummisco.gaml.extensions.maths.utils.*;

@facets(value = {
		@facet(name = IKeyword.EQUATION, type = IType.ID, optional = false),
		@facet(name = IKeyword.METHOD, type = IType.STRING_STR /* CHANGE */, optional = false),
		/** Numerous other facets to plan : step, init, etc.) **/
		// @facet(name = IKeyword.WITH, type = { IType.MAP_STR }, optional =
		// true),
		@facet(name = IKeyword.STEP, type = IType.FLOAT_STR, optional = false) }, omissible = IKeyword.EQUATION)
@symbol(name = { IKeyword.SOLVE }, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true)
// , with_args = true)
@inside(kinds =  { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT,ISymbolKind.SPECIES})
public class SolveStatement extends AbstractStatementSequence { // implements
																// IStatement.WithArgs
																// {

	Solver solver;
	StatementDescription equations;
	private Arguments actualArgs = new Arguments();
	double time_initial = 0, time_final = 1, cycle_length = 1;

	// Have the same organization as in DrawStatement :
	// The statement contains an abstract subclass called "Solver"; Different
	// solvers (maybe
	// corresponding to different integrators?) are then subclasses of this one.
	// And the statement
	// only calls the one which has been chosen at the beginning.
	// Find a way to declare an initial state (either with the "with:" facet, or
	// using assignments
	// in the body of "solve")

	public SolveStatement(final IDescription desc) {
		super(desc);

		List<IDescription> statements = desc.getSpeciesContext().getChildren();
		String eqName = getFacet(IKeyword.EQUATION).literalValue();
		for (IDescription s : statements) {
			if (s.getName().equals(eqName)) {
				equations = (StatementDescription) s;
			}
		}
		// Based on the facets, choose a solver and init it;

	}

	@Override
	public Object privateExecuteIn(final IScope scope)
			throws GamaRuntimeException {
		super.privateExecuteIn(scope);
		String method = getFacet("method").literalValue();
		
		if (method.equals("rk4")) {
			solver = new Rk4Solver(Double.parseDouble(""
					+ getFacet(IKeyword.STEP).value(scope)));
		} else if (method.equals("dp853")) {
			solver = new DormandPrince853Solver(Double.parseDouble(""
					+ getFacet(IKeyword.STEP).value(scope)));
		}
		ISpecies context = scope.getAgentScope().getSpecies();
		SystemOfEquationsStatement s = (SystemOfEquationsStatement) context
				.getStatement(SystemOfEquationsStatement.class,
						getFacet(IKeyword.EQUATION).literalValue());
		
		s.currentScope = scope;
		if (scope.hasVar("cycle_length")) {
			cycle_length = Double.parseDouble(""
					+ scope.getVarValue("cycle_length"));
		}
		time_initial = SimulationClock.getCycle() - 1;
		if (scope.hasVar("t0")) {
			time_initial = Double.parseDouble("" + scope.getVarValue("t0"));
		}
		time_final = SimulationClock.getCycle();
		if (scope.hasVar("tf")) {
			time_final = Double.parseDouble("" + scope.getVarValue("tf"));
		}
		solver.solve(scope, s, time_initial, time_final, cycle_length);

		return null;
	}

	
}
