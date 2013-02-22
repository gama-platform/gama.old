package ummisco.gaml.extensions.maths.statements;

import java.util.List;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.simulation.SimulationClock;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.types.IType;
import ummisco.gaml.extensions.maths.utils.*;

@facets(value = {
		@facet(name = IKeyword.EQUATION, type = IType.ID, optional = false),
		@facet(name = IKeyword.METHOD, type = IType.ID /* CHANGE */, optional = false, values = {
				"rk4", "dp853" }, doc=@doc(value="integrate method")),
		/** Numerous other facets to plan : step, init, etc.) **/
		// @facet(name = IKeyword.WITH, type = { IType.MAP_STR }, optional =
		// true),
		@facet(name = IKeyword.STEP, type = IType.FLOAT_STR, optional = false) }, omissible = IKeyword.EQUATION)
@symbol(name = { IKeyword.SOLVE }, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true)
// , with_args = true)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT,
		ISymbolKind.SPECIES })
public class SolveStatement extends AbstractStatementSequence { // implements

	// IStatement.WithArgs
	// {

	Solver solver;
	StatementDescription equations;
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
		time_initial = scope.getClock().getCycle() - 1;
		if (scope.hasVar("t0")) {
			time_initial = Double.parseDouble("" + scope.getVarValue("t0"));
		}
		time_final = scope.getClock().getCycle();
		if (scope.hasVar("tf")) {
			time_final = Double.parseDouble("" + scope.getVarValue("tf"));
		}

		s.addExtern(getFacet(IKeyword.EQUATION).literalValue());
		solver.solve(scope, s, time_initial, time_final, cycle_length);
		s.removeExtern(getFacet(IKeyword.EQUATION).literalValue());
		return null;
	}

}
