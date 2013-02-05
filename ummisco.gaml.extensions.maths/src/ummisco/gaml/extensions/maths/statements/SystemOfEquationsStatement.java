package ummisco.gaml.extensions.maths.statements;

import java.util.*;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
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
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IVarExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.IStatement;
import msi.gaml.statements.Facets.Facet;
import msi.gaml.types.IType;
import org.apache.commons.math3.exception.*;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

@symbol(name = IKeyword.EQUATION, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true)
@facets(value = {
		@facet(name = IKeyword.NAME, type = IType.ID /* CHANGE */, optional = false),
		@facet(name = IKeyword.SIMULTANEOUSLY, type = IType.LIST_STR, optional = true) }, omissible = IKeyword.NAME)
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
public class SystemOfEquationsStatement extends AbstractStatementSequence
		implements FirstOrderDifferentialEquations {

	final IList<SingleEquationStatement> equations = new GamaList();
	public final IList<IVarExpression> variables = new GamaList();
	public IScope currentScope;
	IExpression simultan = null;

	public SystemOfEquationsStatement(final IDescription desc) {
		super(desc);
		setName(getLiteral(IKeyword.EQUATION));
		simultan = getFacet(IKeyword.SIMULTANEOUSLY);

	}

	/**
	 * This method separates regular statements and equations.
	 * 
	 * @see msi.gaml.statements.AbstractStatementSequence#setChildren(java.util.List)
	 */
	@Override
	public void setChildren(final List<? extends ISymbol> commands) {
		List<ISymbol> others = new ArrayList();
		for (ISymbol s : commands) {
			if (s instanceof SingleEquationStatement) {
				equations.add((SingleEquationStatement) s);
				variables.add(((SingleEquationStatement) s).var);
			} else {
				others.add(s);
			}
		}

		super.setChildren(others);
	}

	// public double[] ydottmp;

	@Override
	public Object privateExecuteIn(final IScope scope)
			throws GamaRuntimeException {
		// We execute whatever is declared in addition to the equations (could
		// be initializations,
		// etc.)
		// GuiUtils.informConsole("it works");
		// for ( int i = 0, n = getDimension(); i < n; i++ ) {
		// IVarExpression v = variables.get(i);
		// ydottmp[i]=(Double)v.value(scope);
		// }
		if (simultan != null) {
			List<IAgent> lst = (List<IAgent>) simultan.value(scope);
			// System.out.println(lst);
			for (IAgent a : lst) {
				SystemOfEquationsStatement ses = (SystemOfEquationsStatement) a
						.getSpecies().getStatement(
								SystemOfEquationsStatement.class, "evol");
				if(!equations.containsAll(ses.equations)){
					equations.addAll(ses.equations);
					variables.addAll(ses.variables);
				}
			}
		}
		return super.privateExecuteIn(scope);
	}

	/**
	 * This method is bound to be called by the integrator of the equations
	 * system (instantiated in SolveStatement).
	 * 
	 * @see org.apache.commons.math3.ode.FirstOrderDifferentialEquations#computeDerivatives(double,
	 *      double[], double[])
	 */
	// double alpha=0.8;
	// double beta=0.2;
	// double gamma=0.2;
	// double delta=0.85;
	@Override
	public void computeDerivatives(final double time, final double[] y,
			final double[] ydot) throws MaxCountExceededException,
			DimensionMismatchException {
		// and the time ?
		// we first initialize the vars with the y vector
		// for ( int i = 0, n = getDimension(); i < n; i++ ) {
		// IVarExpression v = variables.get(i);
		// v.setVal(currentScope, y[i], false);
		// }
		// ydot[0] = y[0] * (alpha - beta * y[1]);
		// ydot[1] =- y[1] * (delta - gamma * y[0]);
		// then we ask the equation(s) to compute and we store their results in
		// the ydot vector
		for (int i = 0, n = getDimension(); i < n; i++) {
			SingleEquationStatement s = equations.get(i);
			ydot[i] = (Double) s.executeOn(currentScope);// ydottmp[i];
		}
		// // finally, we update the value of the variables
		// GuiUtils.informConsole("soe "+ydot[0]+" "+ydot[1]);
		// currentScope.setAgentVarValue("x", y[0]);
		// currentScope.setAgentVarValue("y", y[1]);
		for (int i = 0, n = getDimension(); i < n; i++) {
			IVarExpression v = variables.get(i);
			v.setVal(currentScope, y[i], false);
			
			// currentScope.setAgentVarValue("y", y[1]);
		}
	}

	/**
	 * The dimension of the equations system is simply, here, the number of
	 * equations.
	 * 
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
