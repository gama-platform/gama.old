/*********************************************************************************************
 * 
 * 
 * 'SystemOfEquationsStatement.java', in plugin 'ummisco.gaml.extensions.maths', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ummisco.gaml.extensions.maths.ode.statements;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.*;
import msi.gaml.operators.Cast;
import msi.gaml.species.GamlSpecies;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.types.*;
import org.apache.commons.math3.exception.*;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import ummisco.gaml.extensions.maths.ode.utils.classicalEquations.epidemiology.*;
import ummisco.gaml.extensions.maths.ode.utils.classicalEquations.populationDynamics.ClassicalLVEquations;

/**
 * The class SystemOfEquationsStatement.
 * This class represents a system of equations (SingleEquationStatement) that implements the interface
 * FirstOrderDifferentialEquations and can be integrated by any of the integrators available in the Apache Commons Library.
 * 
 * @author drogoul
 * @since 26 janv. 2013
 * 
 */
@symbol(name = IKeyword.EQUATION, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true)
@facets(value = {
	@facet(name = IKeyword.NAME, type = IType.ID /* CHANGE */, optional = false, doc = @doc("the equation identifier")),
	@facet(name = IKeyword.TYPE, type = IType.ID /* CHANGE */, optional = true, values = { "SI", "SIS", "SIR", "SIRS",
		"SEIR", "LV" }, doc = @doc(value = "the choice of one among classical models (SI, SIS, SIR, SIRS, SEIR, LV)")),
	@facet(name = IKeyword.VARS,
		type = IType.LIST,
		optional = true,
		doc = @doc("the list of variables used in predefined equation systems")),
	@facet(name = IKeyword.PARAMS,
		type = IType.LIST,
		optional = true,
		doc = @doc("the list of pramameters used in predefined equation systems")),
	@facet(name = IKeyword.SIMULTANEOUSLY,
		type = IType.LIST,
		optional = true,
		doc = @doc("a list of agents containing a system of equations (all systems will be solved simultaneously)")) },
	omissible = IKeyword.NAME)
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.MODEL })
@doc(value = "The equation statement is used to create an equation system from several single equations.",
	usages = {
		@usage(value = "The basic syntax to define an equation system is:", examples = {
			@example(value = "float t;", isExecutable = false), @example(value = "float S;", isExecutable = false),
			@example(value = "float I;", isExecutable = false),
			@example(value = "equation SI { ", isExecutable = false),
			@example(value = "   diff(S,t) = (- 0.3 * S * I / 100);", isExecutable = false),
			@example(value = "   diff(I,t) = (0.3 * S * I / 100);", isExecutable = false),
			@example(value = "} ", isExecutable = false) }),
		@usage(value = "If the type: facet is used, a predefined equation system is defined using variables vars: and parameters params: in the right order. All possible predefined equation systems are the following ones (see [EquationPresentation161 EquationPresentation161] for precise definition of each classical equation system): ",
			examples = {
				@example(value = "equation eqSI type: SI vars: [S,I,t] params: [N,beta];", isExecutable = false),
				@example(value = "equation eqSIS type: SIS vars: [S,I,t] params: [N,beta,gamma];", isExecutable = false),
				@example(value = "equation eqSIR type:SIR vars:[S,I,R,t] params:[N,beta,gamma];", isExecutable = false),
				@example(value = "equation eqSIRS type: SIRS vars: [S,I,R,t] params: [N,beta,gamma,omega,mu];",
					isExecutable = false),
				@example(value = "equation eqSEIR type: SEIR vars: [S,E,I,R,t] params: [N,beta,gamma,sigma,mu];",
					isExecutable = false),
				@example(value = "equation eqLV type: LV vars: [x,y,t] params: [alpha,beta,delta,gamma] ;",
					isExecutable = false) }),
		@usage(value = "If the simultaneously: facet is used, system of all the agents will be solved simultaneously.") },
	see = { "=", IKeyword.SOLVE })
public class SystemOfEquationsStatement extends AbstractStatementSequence implements FirstOrderDifferentialEquations {

	public final GamaMap<String, SingleEquationStatement> equations = new GamaMap<String, SingleEquationStatement>();
	public final  GamaMap<String, IExpression> variables_diff = new  GamaMap<String, IExpression>();
	public final IList<IExpression> variables_nondiff = new GamaList<IExpression>();
	// public final GamaMap variables=new GamaMap();
	public final GamaList<SingleEquationStatement> external_equations = new GamaList<SingleEquationStatement>();
	public final GamaList<IAgent> equaAgents = new GamaList<IAgent>();
	public final GamaList<IAgent> equaAgents_ext = new GamaList<IAgent>();
	// private GamaList integrated_times=new GamaList();
	// public GamaList integrate_value=new GamaList();
	public double equa_t = 0;
	public IScope currentScope;
	IExpression simultan = null;

	public SystemOfEquationsStatement(final IDescription desc) {
		super(desc);
		setName(getLiteral(IKeyword.NAME));
		simultan = getFacet(IKeyword.SIMULTANEOUSLY);

	}

	/**
	 * This method separates regular statements and equations.
	 * 
	 * @see msi.gaml.statements.AbstractStatementSequence#setChildren(java.util.List)
	 */
	@Override
	public void setChildren(final List<? extends ISymbol> commands) {
		List<? extends ISymbol> cmd = commands;
		if ( getFacet(IKeyword.TYPE) != null ) {
			if ( getFacet(IKeyword.TYPE).literalValue().equals("SIR") ) {
				cmd.clear();
				cmd =
					new ClassicalSIREquations(getDescription()).SIR(getFacet(IKeyword.VARS), getFacet(IKeyword.PARAMS));
			} else if ( getFacet(IKeyword.TYPE).literalValue().equals("SI") ) {
				cmd.clear();
				cmd = new ClassicalSIEquations(getDescription()).SI(getFacet(IKeyword.VARS), getFacet(IKeyword.PARAMS));
			} else if ( getFacet(IKeyword.TYPE).literalValue().equals("SIS") ) {
				cmd.clear();
				cmd =
					new ClassicalSISEquations(getDescription()).SIS(getFacet(IKeyword.VARS), getFacet(IKeyword.PARAMS));
			} else if ( getFacet(IKeyword.TYPE).literalValue().equals("SIRS") ) {
				cmd.clear();
				cmd =
					new ClassicalSIRSEquations(getDescription()).SIRS(getFacet(IKeyword.VARS),
						getFacet(IKeyword.PARAMS));
			} else if ( getFacet(IKeyword.TYPE).literalValue().equals("SEIR") ) {
				cmd.clear();
				cmd =
					new ClassicalSEIREquations(getDescription()).SEIR(getFacet(IKeyword.VARS),
						getFacet(IKeyword.PARAMS));
			} else if ( getFacet(IKeyword.TYPE).literalValue().equals("LV") ) {
				cmd.clear();
				cmd = new ClassicalLVEquations(getDescription()).LV(getFacet(IKeyword.VARS), getFacet(IKeyword.PARAMS));
			} else {
				GamaRuntimeException.error(getFacet(IKeyword.TYPE).literalValue().equals("SI") +
					" is not a recognized classical equation");
			}
		}

		final List<ISymbol> others = new ArrayList<ISymbol>();
		for ( final ISymbol s : cmd ) {
			if ( s instanceof SingleEquationStatement ) {
				((SingleEquationStatement) s).establishVar();
				equations.addValue(null, new GamaPair<String, SingleEquationStatement>(((SingleEquationStatement) s).toString(),(SingleEquationStatement) s));
				for ( int i = 0; i < ((SingleEquationStatement) s).getVars().size(); i++ ) {
					IExpression v = ((SingleEquationStatement) s).getVar(i);

					if ( ((SingleEquationStatement) s).getOrder() > 0 ) {
//						if ( !variables_diff.contains(v) ) {
							variables_diff.addValue(null, new GamaPair<String, IExpression>(((SingleEquationStatement) s).toString(),v));
//						}
					} else {
						if ( !variables_nondiff.contains(v) ) {
							variables_nondiff.add(v);
						}
					}
				}

			} else {
				others.add(s);
			}
		}
		super.setChildren(others);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		// We execute whatever is declared in addition to the equations (could
		// be initializations,
		// etc.)
		equaAgents.clear();
		for ( int i = 0; i < equations.getValues().size(); i++ ) {
			equaAgents.add(scope.getAgentScope());
		}
		if ( simultan != null ) {
			equaAgents_ext.clear();
			final Object t = simultan.value(scope);
			if ( t != null ) {
				if ( t instanceof GamaList ) {

					final GamaList lst = ((GamaList) t).listValue(scope, Types.NO_TYPE);
					for ( int i = 0; i < lst.size(); i++ ) {
						final Object o = lst.get(i);
						if ( o instanceof IAgent ) {
							final IAgent remoteAgent = (IAgent) o;

							if ( !remoteAgent.dead() && !remoteAgent.equals(scope.getAgentScope()) && !equaAgents_ext.contains(remoteAgent) ) {
								equaAgents_ext.add(remoteAgent);
							}
						} 
//						else if (o instanceof IPopulation){
//							final Iterator<? extends IAgent> ia = ((IPopulation) o).iterator();
//							while (ia.hasNext()) {
//								final IAgent remoteAgent = ia.next();
//								if ( !remoteAgent.dead() && !remoteAgent.equals(scope.getAgentScope()) && !equations_ext.contains(remoteAgent) ) {
//									equations_ext.add(remoteAgent);
//								}
//							}
//						} 
						else if ( o instanceof GamlSpecies ) {

							final Iterator<? extends IAgent> ia = ((GamlSpecies) o).iterable(scope).iterator();
							while (ia.hasNext()) {
								final IAgent remoteAgent = ia.next();
								if ( !remoteAgent.dead() && !remoteAgent.equals(scope.getAgentScope()) && !equaAgents_ext.contains(remoteAgent) ) {
//									GuiUtils.informConsole(scope.getAgentScope()+" simul "+remoteAgent);

									equaAgents_ext.add(remoteAgent);
								}
							}
						}
					}

				} else {
					if ( !equaAgents_ext.contains(t) ) {
						equaAgents_ext.add((IAgent) t);
					}
				}
			}
//			GuiUtils.informConsole("equations_ext "+scope.getAgentScope()+" "+equations_ext);
		}
		

		return super.privateExecuteIn(scope);
	}

	private void addEquationsExtern(final IAgent remoteAgent, final String eqName) {
		final SystemOfEquationsStatement ses =
			remoteAgent.getSpecies().getStatement(SystemOfEquationsStatement.class, eqName);
		if ( ses != null ) {
			// final int n = equaAgents.size();

			for ( int i = 0; i < ses.equations.getValues().size(); i++ ) {
				equaAgents.add(remoteAgent);
			}
			if( equations.contains(ses.equations)){
				return;
			}
			for(Object s : ses.equations.getPairs()){
				String  name=remoteAgent.getName()+((GamaPair<String, SingleEquationStatement>) s).getKey();
				SingleEquationStatement eq=((GamaPair<String, SingleEquationStatement>) s).getValue();
				equations.addValue(currentScope, new GamaPair<String, SingleEquationStatement>(name,eq));
			}
//			equations.addAll(ses.equations);
			
			for(Object s : ses.variables_diff.getPairs()){
				String  name=remoteAgent.getName()+((GamaPair<String, IExpression>) s).getKey();
				IExpression v=((GamaPair<String, IExpression>) s).getValue();
				variables_diff.addValue(currentScope, new GamaPair<String, IExpression>(name,v));
			}
//			variables_diff.addAll(ses.variables_diff);
//			GuiUtils.informConsole("Add variables_diff "+remoteAgent+" "+variables_diff);
		}

	}

	private void removeEquationsExtern(final IAgent remoteAgent, final String eqName) {
		final SystemOfEquationsStatement ses =
			remoteAgent.getSpecies().getStatement(SystemOfEquationsStatement.class, eqName);
//		if( remoteAgent.getSpecies().getDescription().equals(this.getDescription().getSpeciesContext())){
//			return;
//		}
		if ( ses != null ) {
//			GuiUtils.informConsole("Remove variables_diff "+remoteAgent+" "+ses.variables_diff);
//			GuiUtils.informConsole("Remove equations "+remoteAgent+" "+ses.equations);
			for(String s : ses.equations.getKeys()){

				equations.remove(remoteAgent.getName()+s);
				variables_diff.remove(remoteAgent.getName()+s);
			}
//			equations.removeAll(ses.equations);
//			variables_diff.removeAll(ses.variables_diff);
		}

	}

	public void addExtern(final String eqName) {
		if ( equaAgents_ext.size() > 0 ) {

			for ( int i = 0, n = equaAgents_ext.size(); i < n; i++ ) {
				final IAgent remoteAgent = equaAgents_ext.get(i);

				if ( !remoteAgent.dead() ) {
					addEquationsExtern(remoteAgent, eqName);
				}

			}

		}

	}

	public void removeExtern(final IScope scope, final String eqName) {
		if ( equaAgents_ext.size() > 0 ) {
			for ( int i = 0, n = equaAgents_ext.size(); i < n; i++ ) {
				final Object o = equaAgents_ext.get(i);
				if ( o instanceof IAgent ) {
					final IAgent remoteAgent = (IAgent) o;
					if ( !remoteAgent.dead() ) {
						removeEquationsExtern(remoteAgent, eqName);
					}
				} else if ( o instanceof GamlSpecies ) {
					final Iterator<? extends IAgent> ia = ((GamlSpecies) o).iterable(scope).iterator();
					while (ia.hasNext()) {
						final IAgent remoteAgent = ia.next();
						if ( !remoteAgent.dead() ) {
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
	 * @see org.apache.commons.math3.ode.FirstOrderDifferentialEquations#computeDerivatives(double, double[], double[])
	 */

	public void assignValue(final double time, final double[] y) {
		// TODO Should be rewritten in a more correct way (by calling
		// scope.setAgentVarValue(...)
		for ( int i = 0, n = equations.getValues().size(); i < n; i++ ) {
			final SingleEquationStatement s = equations.getValues().get(i);
			if ( s.getOrder() == 0 ) {
				continue;
			}

			final IAgent remoteAgent = equaAgents.get(i);
			boolean pushed = false;
			if ( !remoteAgent.dead() ) {
				pushed = currentScope.push(remoteAgent);
				try {
					if ( s.getVar_t() instanceof IVarExpression ) {
						((IVarExpression) s.getVar_t()).setVal(currentScope, time, false);
					}
					if ( variables_diff.getValues().get(i) instanceof IVarExpression ) {
						((IVarExpression) variables_diff.getValues().get(i)).setVal(currentScope, y[i], false);
					} else if ( variables_diff.getValues().get(i) instanceof MapExpression ) {
						System.out.println(((MapExpression) variables_diff.getValues().get(i)).valuesArray());

					}
				} catch (final Exception ex1) {
					GuiUtils.debug(ex1);
				} finally {
					if ( pushed ) {
						currentScope.pop(remoteAgent);
					}
				}
			}

		}

		for ( int i = 0, n = equations.getValues().size(); i < n; i++ ) {
			final SingleEquationStatement s = equations.getValues().get(i);
			if ( s.getOrder() == 0 ) {
				IExpression tmp = ((UnaryOperator) s.getFunction()).arg(0);
				Object v = s.getExpression().value(currentScope);
				if ( tmp instanceof AgentVariableExpression ) {
					((AgentVariableExpression) tmp).setVal(currentScope, v, false);
				}
			}

		}

	}

	@Override
	public void computeDerivatives(final double time, final double[] y, final double[] ydot)
		throws MaxCountExceededException, DimensionMismatchException {
		/*
		 * the y value is calculed automatically inside integrator's algorithm
		 * just get y, and assign value to Variables in GAMA, which is use by
		 * GAMA modeler
		 */

		assignValue(time, y);

		/*
		 * with variables assigned, calcul new value of expression in function
		 * loop through equations (internal and external) to get singleequation
		 * value
		 */

		// TODO Should be rewritten in a more correct way : scope.execute(s,
		// agent)...
		for ( int i = 0, n = getDimension(); i < n; i++ ) {

			boolean pushed = false;
			if ( equaAgents.size() > 0 ) {
				pushed = currentScope.push(equaAgents.get(i));
			}
			try {
				ydot[i] = Cast.asFloat(currentScope, equations.getValues().get(i).executeOn(currentScope));
			} catch (final Exception ex1) {
				GuiUtils.debug(ex1);
			} finally {
				if ( equaAgents.size() > 0 ) {
					if ( pushed ) {
						currentScope.pop(equaAgents.get(i));
					}
				}
			}
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
		int count = 0;
		for ( int i = 0; i < equations.getValues().size(); i++ ) {
			if ( equations.getValues().get(i).getOrder() > 0 ) {
				count++;
			}
		}
		
		return count;
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
