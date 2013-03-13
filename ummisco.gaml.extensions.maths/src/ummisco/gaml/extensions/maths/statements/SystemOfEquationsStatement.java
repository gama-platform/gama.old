package ummisco.gaml.extensions.maths.statements;

import java.util.*;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IVarExpression;
import msi.gaml.species.GamlSpecies;
import msi.gaml.statements.AbstractStatementSequence;
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

	public final IList<SingleEquationStatement> equations = new GamaList<SingleEquationStatement>();
	public final IList<IVarExpression> variables = new GamaList<IVarExpression>();
	public final GamaList<IAgent> equations_ext = new GamaList<IAgent>();
	public final GamaList<IAgent> equaAgents = new GamaList<IAgent>();
	public double equa_t=0;
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
		List<ISymbol> others = new ArrayList<ISymbol>();
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
			equaAgents.add(0, scope.getAgentScope());
			equations_ext.clear();
			Object t = simultan.value(scope);
			if (t != null) {
				if (t instanceof GamaList) {
					GamaList lst = ((GamaList) t).listValue(scope);
					for (int i = 0; i < lst.size(); i++) {
						Object o = lst.get(i);

						if (o instanceof IAgent) {
							final IAgent remoteAgent = (IAgent) o;
							if (!remoteAgent.dead()
									&& !equations_ext.contains(remoteAgent)) {
								equations_ext.add(remoteAgent);
							}
						} else if (o instanceof GamlSpecies) {
							Iterator<IAgent> ia = ((GamlSpecies) o).iterator();
							while (ia.hasNext()) {
								final IAgent remoteAgent = (IAgent) ia.next();
								if (!remoteAgent.dead()
										&& !equations_ext.contains(remoteAgent)) {
									equations_ext.add(remoteAgent);
								}
							}
						}
					}

					// if (!equations_ext.containsAll(lst))
					// equations_ext.addAll(lst);
				} else {
					if (!equations_ext.contains(t))
						equations_ext.add((IAgent) t);
				}
			}
		}

		return super.privateExecuteIn(scope);
	}

	private void addEquationsExtern(IAgent remoteAgent, String eqName) {
		SystemOfEquationsStatement ses = (SystemOfEquationsStatement) remoteAgent
				.getSpecies().getStatement(SystemOfEquationsStatement.class,
						eqName);
		if (ses != null) {
			int n = equaAgents.size();
			// System.out.println(equaAgents);
			for (int i = 0; i < ses.equations.size(); i++) {
				equaAgents.add(n + i, remoteAgent);
			}

			if (!equations.containsAll(ses.equations))
				equations.addAll(ses.equations);
			if (!variables.containsAll(ses.variables))
				variables.addAll(ses.variables);
			// System.out.println(equaAgents+"\n eq "+equations+"\n var "+variables+"\n");
		}

	}

	private void removeEquationsExtern(IAgent remoteAgent, String eqName) {
		SystemOfEquationsStatement ses = (SystemOfEquationsStatement) remoteAgent
				.getSpecies().getStatement(SystemOfEquationsStatement.class,
						eqName);
		if (ses != null) {

			// equaAgents.remove(ses);
			// if (!equations.containsAll(ses.equations))
			equations.remove(ses.equations);
			// if (!variables.containsAll(ses.variables))
			variables.remove(ses.variables);
		}

	}

	public void addExtern(String eqName) {
		if (equations_ext.size() > 0) {
			// System.out.println("ex "+equations_ext);
			for (int i = 0, n = equations_ext.size(); i < n; i++) {
				Object o = equations_ext.get(i);
				final IAgent remoteAgent = (IAgent) o;
				if (!remoteAgent.dead()) {
					addEquationsExtern(remoteAgent, eqName);
				}
				/*
				 * if (o instanceof IAgent) { final IAgent remoteAgent =
				 * (IAgent) o; if (!remoteAgent.dead()) {
				 * addEquationsExtern(remoteAgent); } } else if (o instanceof
				 * GamlSpecies) { Iterator<IAgent> ia = ((GamlSpecies)
				 * o).iterator(); while (ia.hasNext()) { final IAgent
				 * remoteAgent = (IAgent) ia.next(); if (!remoteAgent.dead()) {
				 * addEquationsExtern(remoteAgent); } } }
				 */
			}

		}

	}

	public void removeExtern(String eqName) {
		if (equations_ext.size() > 0) {
			for (int i = 0, n = equations_ext.size(); i < n; i++) {
				Object o = equations_ext.get(i);
				if (o instanceof IAgent) {
					final IAgent remoteAgent = (IAgent) o;
					if (!remoteAgent.dead()) {
						removeEquationsExtern(remoteAgent, eqName);
					}
				} else if (o instanceof GamlSpecies) {
					Iterator<IAgent> ia = ((GamlSpecies) o).iterator();
					while (ia.hasNext()) {
						final IAgent remoteAgent = (IAgent) ia.next();
						if (!remoteAgent.dead()) {
							removeEquationsExtern(remoteAgent, eqName);
						}
					}
				}
			}
			equaAgents.clear();
		}
	}

	/**
	 * This method is bound to be called by the integrator of the equations
	 * system (instantiated in SolveStatement).
	 * 
	 * @see org.apache.commons.math3.ode.FirstOrderDifferentialEquations#computeDerivatives(double,
	 *      double[], double[])
	 */
	 double alpha=0.8;
	 double beta=0.2;
	 double gamma=0.2;
	 double delta=0.85;
	@Override
	public void computeDerivatives(final double time, final double[] y,
			final double[] ydot) throws MaxCountExceededException,
			DimensionMismatchException {
		// and the time ?
		// we first initialize the vars with the y vector
		//		 ydot[0] = y[0] * (alpha - beta * y[1]);
		//		 ydot[1] =- y[1] * (delta - gamma * y[0]);
		// for ( int i = 0, n = getDimension(); i < n; i++ ) {
		// IVarExpression v = variables.get(i);
		// v.setVal(currentScope, y[i], false);
		// }
		/*
		ydot[0]=-3*y[0];
		ydot[1]=0.0;
		ydot[2]=0.0;
		GuiUtils.informConsole("t"+time+"= "+y[0]+"    "+ydot[0]+"\n");
		
		*/
		
		
		
		
		// then we ask the equation(s) to compute and we store their results in
		// the ydot vector
		/*
		 * the y value is calculed automatically inside integrator's algorithm
		 * just get y, and assign value to Variables in GAMA, which is use by GAMA modeler
		 */
		for (int i = 0, n = getDimension(); i < n; i++) {
			IVarExpression v = variables.get(i);
			

			try {
				v.setVal(currentScope, y[i], false);
			} catch (Exception ex) {
				for (int j = 0; j < equations_ext.size(); j++) {
					Object o = equations_ext.get(j);
					if (o instanceof IAgent) {
						final IAgent remoteAgent = (IAgent) o;
						if (!remoteAgent.dead()) {
							currentScope.push(remoteAgent);
							try {
								v.setVal(currentScope, y[i], false);
							} catch (Exception ex1) {
							} finally {
								currentScope.pop(remoteAgent);
							}
						}
					} else if (o instanceof GamlSpecies) {
						Iterator<IAgent> ia = ((GamlSpecies) o).iterator();
						while (ia.hasNext()) {
							final IAgent remoteAgent = (IAgent) ia.next();
							if (!remoteAgent.dead()) {
								currentScope.push(remoteAgent);
								try {
									v.setVal(currentScope, y[i], false);
								} catch (Exception ex1) {
								} finally {
									currentScope.pop(remoteAgent);
								}
							}
						}
					}
				}
			}
		}
		
		
		
		
		
		
		

		/*
		 * with variables assigned, calcul new value of expression in function
		 * loop through equations (internal and external) to get singleequation
		 * value
		 */
		for (int i = 0, n = getDimension(); i < n; i++) {
			SingleEquationStatement s = equations.get(i);
			// ydot[i] = (Double) s.executeOn(currentScope);// ydottmp[i];
			if (equaAgents.size() > 0)
				currentScope.push(equaAgents.get(i));
			try {
				s.var_t.setVal(currentScope, time, false);
				ydot[i] = Double.parseDouble(""+s.executeOn(currentScope));
			} catch (Exception ex1) {
			} finally {

				if (equaAgents.size() > 0)
					currentScope.pop(equaAgents.get(i));
			}
		}

//		GuiUtils.informConsole("t"+time+"= "+y[0]+"    "+ydot[0]+"\n");
		// // finally, we update the value of the variables
		// GuiUtils.informConsole("soe "+ydot[0]+" "+ydot[1]);
		
	
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
