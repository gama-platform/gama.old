package ummisco.gaml.extensions.maths.statements;

import java.util.List;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.combination;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
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
				"rk4", "dp853" }, doc = @doc(value = "integrate method")),
		/** Numerous other facets to plan : step, init, etc.) **/
		@facet(name = "time_initial", type = IType.FLOAT_STR, optional = true, doc = @doc(value = "initial time")),
		@facet(name = "time_final", type = IType.FLOAT_STR, optional = true, doc = @doc(value = "target time for the integration (can be set to a value smaller than t0 for backward integration)")),
		@facet(name = "cycle_length", type = IType.INT_STR, optional = true, doc = @doc(value = "length of simulation cycle which will be synchronize with step of integrator")),
		@facet(name = IKeyword.STEP, type = IType.FLOAT_STR, optional = true, doc = @doc(value = "integration step, use with most integrator method")),
		@facet(name = "min_step", type = IType.FLOAT_STR, optional = true, doc = @doc(value = "minimal step, use with dp853 method, (sign is irrelevant, regardless of integration direction, forward or backward), the last step can be smaller than this")),
		@facet(name = "max_step", type = IType.FLOAT_STR, optional = true, doc = @doc(value = "maximal step, use with dp853 method, (sign is irrelevant, regardless of integration direction, forward or backward), the last step can be smaller than this")),
		@facet(name = "scalAbsoluteTolerance", type = IType.FLOAT_STR, optional = true, doc = @doc(value = "allowed absolute error, use with dp853 method,")),
		@facet(name = "scalRelativeTolerance", type = IType.FLOAT_STR, optional = true, doc = @doc(value = "allowed relative error, use with dp853 method,")) },

combinations = {
		@combination({ IKeyword.STEP }),
		@combination({ "min_step", "max_step",
				"scalAbsoluteTolerance", "scalRelativeTolerance" }) }, omissible = IKeyword.EQUATION)
@symbol(name = { IKeyword.SOLVE }, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true)
// , with_args = true)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT,
		ISymbolKind.SPECIES })
public class SolveStatement extends AbstractStatementSequence { // implements

	// IStatement.WithArgs
	// {

	Solver solver;
	StatementDescription equations;
	double time_initial = 0, time_final = 1;
	int cycle_length = 1; 

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
		} else if (method.equals("dp853") && getFacet("min_step") != null
				&& getFacet("max_step") != null
				&& getFacet("scalAbsoluteTolerance") != null
				&& getFacet("scalRelativeTolerance") != null) {
			double minStep = Double.parseDouble(""
					+ getFacet("min_step").value(scope));
			double maxStep = Double.parseDouble(""
					+ getFacet("max_step").value(scope));
			double scalAbsoluteTolerance = Double.parseDouble(""
					+ getFacet("scalAbsoluteTolerance").value(scope));
			double scalRelativeTolerance = Double.parseDouble(""
					+ getFacet("scalRelativeTolerance").value(scope));
			solver = new DormandPrince853Solver(minStep, maxStep,
					scalAbsoluteTolerance, scalRelativeTolerance);
		}
		ISpecies context = scope.getAgentScope().getSpecies();
		SystemOfEquationsStatement s = (SystemOfEquationsStatement) context
				.getStatement(SystemOfEquationsStatement.class,
						getFacet(IKeyword.EQUATION).literalValue());

		s.currentScope = scope;
		if (getFacet("cycle_length") != null) {
			cycle_length = Integer.parseInt(""
					+ getFacet("cycle_length").value(scope));
		}
		time_initial = scope.getClock().getCycle() - 1;
		if (getFacet("time_initial") != null) {
			time_initial = Double.parseDouble("" + getFacet("time_initial"));
		}
		time_final = scope.getClock().getCycle();
		if (getFacet("time_final") != null) {
			time_final = Double.parseDouble("" + getFacet("time_final"));
		}

		s.addExtern(getFacet(IKeyword.EQUATION).literalValue());
		solver.solve(scope, s, time_initial, time_final, cycle_length);
		s.removeExtern(getFacet(IKeyword.EQUATION).literalValue());
		return null;
	}

}
