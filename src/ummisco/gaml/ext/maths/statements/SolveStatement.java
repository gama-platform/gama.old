package ummisco.gaml.ext.maths.statements;

import java.util.List;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.types.IType;
import org.apache.commons.math3.ode.*;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;

@facets(value = { @facet(name = IKeyword.EQUATION, type = IType.ID, optional = false),
	@facet(name = IKeyword.METHOD, type = IType.STRING_STR /* CHANGE */, optional = false)
/** Numerous other facets to plan : step, init, etc.) **/
}, omissible = IKeyword.EQUATION)
@symbol(name = { IKeyword.SOLVE }, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true)
@inside(kinds = ISymbolKind.SPECIES)
public class SolveStatement extends AbstractStatementSequence {

	Solver solver;
	FirstOrderDifferentialEquations equations;

	// Have the same organization as in DrawStatement :
	// The statement contains an abstract subclass called "Solver"; Different solvers (maybe
	// corresponding to different integrators?) are then subclasses of this one. And the statement
	// only calls the one which has been chosen at the beginning.
	// Find a way to declare an initial state (either with the "with:" facet, or using assignments
	// in the body of "solve")

	public SolveStatement(final IDescription desc) {
		super(desc);
		List<IDescription> statements = desc.getSpeciesContext().getChildren();
		String eqName = getFacet(IKeyword.EQUATION).literalValue();
		for ( IDescription s : statements ) {
			if ( s instanceof SystemOfEquationsStatement && s.getName().equals(eqName) ) {
				equations = (SystemOfEquationsStatement) s;
			}
		}
		// Based on the facets, choose a solver and init it;
		solver = new FirstOrderSolver();
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		solver.solve(scope);
		return super.privateExecuteIn(scope);
	}

	abstract class Solver {

		// Declare the integrator using facets values (name, parameters)
		// Declare a step handler also using facets values
		// Should the solver implement StepHandler or use a given StepHandler ?

		// Call the integrator, which should call computeDerivatives on the system of equations;
		abstract void solve(IScope scope);

	}

	class FirstOrderSolver extends Solver {

		FirstOrderIntegrator integrator;

		FirstOrderSolver() {
			// initialize the integrator, the step handler, etc.
			// This class has access to the facets of the statement
			// ie. getFacet(...)
			//

			// Just a trial
			integrator = new ClassicalRungeKuttaIntegrator(1.0);
		}

		@Override
		void solve(final IScope scope) {
			// call the integrator.
			// We need to save the state (previous time the integrator has been solved, etc.)
		}

	}

}
