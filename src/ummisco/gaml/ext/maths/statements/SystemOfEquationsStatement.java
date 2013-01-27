package ummisco.gaml.ext.maths.statements;

import java.util.*;
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
import msi.gaml.expressions.IVarExpression;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.types.IType;
import org.apache.commons.math3.exception.*;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

//@symbol(name = IKeyword.EQUATION , kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true)
//@facets(value = { @facet(name = IKeyword.NAME, type = IType.ID /* CHANGE */, optional = false) }, omissible = IKeyword.NAME)
//@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.SPECIES })

@symbol(name = IKeyword.EQUATION , kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true)
@facets(value = { @facet(name = IKeyword.NAME, type = IType.ID /* CHANGE */, optional = false) }, omissible = IKeyword.NAME)
@inside(kinds = ISymbolKind.SPECIES)
/**
 * The class SystemOfEquationsStatement. 
 * This class represents a system of equations (SingleEquationStatement) that implements the interface 
 * FirstOrderDifferentialEquations and can be integrated by any of the integrators available in the Apache Commons Library.
 * 
 * @author drogoul
 * @since 26 janv. 2013
 *
 */
public class SystemOfEquationsStatement extends AbstractStatementSequence implements
	FirstOrderDifferentialEquations {

	final IList<SingleEquationStatement> equations = new GamaList();
	final IList<IVarExpression> variables = new GamaList();
	IScope currentScope;

	public SystemOfEquationsStatement(final IDescription desc) {
		super(desc);
	}

	/**
	 * This method separates regular statements and equations.
	 * @see msi.gaml.statements.AbstractStatementSequence#setChildren(java.util.List)
	 */
	@Override
	public void setChildren(final List<? extends ISymbol> commands) {
		List<ISymbol> others = new ArrayList();
		for ( ISymbol s : commands ) {
			if ( s instanceof SingleEquationStatement ) {
				equations.add((SingleEquationStatement) s);
				variables.add(((SingleEquationStatement) s).var);
			} else {
				others.add(s);
			}
		}
		super.setChildren(others);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		// We execute whatever is declared in addition to the equations (could be initializations,
		// etc.)
		return super.privateExecuteIn(scope);
	}

	/**
	 * This method is bound to be called by the integrator of the equations system (instantiated in
	 * SolveStatement).
	 * @see org.apache.commons.math3.ode.FirstOrderDifferentialEquations#computeDerivatives(double,
	 *      double[], double[])
	 */
	@Override
	public void computeDerivatives(final double time, final double[] y, final double[] ydot)
		throws MaxCountExceededException, DimensionMismatchException {
		// and the time ?
		// we first initialize the vars with the y vector
		for ( int i = 0, n = getDimension(); i < n; i++ ) {
			IVarExpression v = variables.get(i);
			v.setVal(currentScope, y[i], false);
		}
		// then we ask the equation(s) to compute and we store their results in the ydot vector
		for ( int i = 0, n = getDimension(); i < n; i++ ) {
			SingleEquationStatement s = equations.get(i);
			ydot[i] = (Double) s.executeOn(currentScope);
		}
		// finally, we update the value of the variables
		for ( int i = 0, n = getDimension(); i < n; i++ ) {
			IVarExpression v = variables.get(i);
			v.setVal(currentScope, ydot[i], false);
		}
	}

	/**
	 * The dimension of the equations system is simply, here, the number of equations.
	 * @see org.apache.commons.math3.ode.FirstOrderDifferentialEquations#getDimension()
	 */
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

}
