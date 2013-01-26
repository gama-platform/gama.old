package msi.gaml.statements;

import java.util.List;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.types.IType;
import org.apache.commons.math3.exception.*;
import org.apache.commons.math3.ode.*;

@facets(value = { @facet(name = IKeyword.METHOD, type = IType.STRING_STR /* CHANGE */, optional = false) }, omissible = IKeyword.METHOD)
@symbol(name = { IKeyword.SOLVE }, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true)
@inside(symbols = IKeyword.SOLVER)
public class SolveStatement extends AbstractStatementSequence implements
	FirstOrderDifferentialEquations {

	final IList<EquationStatement> equations = new GamaList();
	IScope currentScope;
	Solver solver;

	// Have the same organization as in DrawStatement :
	// The statement contains an abstract subclass called "Solver"; Different solvers (1st order,
	// 2nd order, etc.) are then subclasses of this one. And the statement only calls the one which
	// has been chosen
	// Find a way to declare an initial state (either with the "with:" facet, or using assignments
	// in the body of "solve")

	public SolveStatement(final IDescription desc) {
		super(desc);
		// Based on the facets, choose a solver and init it;
		solver = new FirstOrderSolver();
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {
		for ( ISymbol s : commands ) {
			if ( s instanceof EquationStatement ) {
				equations.add((EquationStatement) s);
			}
		}
		super.setChildren(commands);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		solver.solve(scope);
		return super.privateExecuteIn(scope);
	}

	@Override
	public void computeDerivatives(final double arg0, final double[] arg1, final double[] arg2)
		throws MaxCountExceededException, DimensionMismatchException {
		for ( int i = 0, n = getDimension(); i < n; i++ ) {
			EquationStatement s = equations.get(i);
			arg2[i] = (Double) s.executeOn(currentScope);
		}
	}

	@Override
	public int getDimension() {
		return equations.size();
	}

	@Override
	public void leaveScope(final IScope scope) {
		currentScope = null;
		super.leaveScope(scope);
	}

	@Override
	public void enterScope(final IScope scope) {
		super.enterScope(scope);
		currentScope = scope;
	}

	abstract class Solver {

		// Declare the integrator using facets values (name, parameters)
		// Declare a step handler also using facets values

		// Call the integrator, which should call computeDerivatives
		abstract void solve(IScope scope);

	}

	class FirstOrderSolver extends Solver
	/* implements FirstOrderDifferentialEquations */
	{

		FirstOrderIntegrator integrator;

		FirstOrderSolver() {
			// initialize the integrator, the step handler, etc.
			// This class has access to the facets of the statement
			// ie. getFacet(...)
		}

		@Override
		void solve(final IScope scope) {
			// call the integrator
		}

	}

}
