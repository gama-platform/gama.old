/*********************************************************************************************
 *
 * 'SystemOfEquationsStatement.java, in plugin ummisco.gaml.extensions.maths, is part of the source code of the GAMA
 * modeling and simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gaml.extensions.maths.ode.statements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

import com.google.common.collect.Lists;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.IScope.ExecutionResult;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.GamaPair;
import msi.gama.util.IList;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.AgentVariableExpression;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IVarExpression;
import msi.gaml.expressions.UnaryOperator;
import msi.gaml.operators.Cast;
import msi.gaml.species.GamlSpecies;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gaml.extensions.maths.ode.statements.SystemOfEquationsStatement.SystemOfEquationsValidator;
import ummisco.gaml.extensions.maths.ode.utils.classicalEquations.epidemiology.ClassicalSEIREquations;
import ummisco.gaml.extensions.maths.ode.utils.classicalEquations.epidemiology.ClassicalSIEquations;
import ummisco.gaml.extensions.maths.ode.utils.classicalEquations.epidemiology.ClassicalSIREquations;
import ummisco.gaml.extensions.maths.ode.utils.classicalEquations.epidemiology.ClassicalSIRSEquations;
import ummisco.gaml.extensions.maths.ode.utils.classicalEquations.epidemiology.ClassicalSISEquations;
import ummisco.gaml.extensions.maths.ode.utils.classicalEquations.populationDynamics.ClassicalLVEquations;

/**
 * The class SystemOfEquationsStatement. This class represents a system of equations (SingleEquationStatement) that
 * implements the interface FirstOrderDifferentialEquations and can be integrated by any of the integrators available in
 * the Apache Commons Library.
 *
 * @author drogoul
 * @since 26 janv. 2013
 *
 */
@symbol (
		name = IKeyword.EQUATION,
		kind = ISymbolKind.SEQUENCE_STATEMENT,
		with_sequence = true,
		concept = { IConcept.EQUATION })
@facets (
		value = { @facet (
				name = IKeyword.NAME,
				type = IType.ID /* CHANGE */,
				optional = false,
				doc = @doc ("the equation identifier")),
				@facet (
						name = IKeyword.TYPE,
						type = IType.ID /* CHANGE */,
						optional = true,
						values = { "SI", "SIS", "SIR", "SIRS", "SEIR", "LV" },
						doc = @doc (
								value = "the choice of one among classical models (SI, SIS, SIR, SIRS, SEIR, LV)")),
				@facet (
						name = IKeyword.VARS,
						type = IType.LIST,
						optional = true,
						doc = @doc ("the list of variables used in predefined equation systems")),
				@facet (
						name = IKeyword.PARAMS,
						type = IType.LIST,
						optional = true,
						doc = @doc ("the list of parameters used in predefined equation systems")),
				@facet (
						name = IKeyword.SIMULTANEOUSLY,
						type = IType.LIST,
						of = IType.SPECIES,
						optional = true,
						doc = @doc ("a list of species containing a system of equations (all systems will be solved simultaneously)")) },
		omissible = IKeyword.NAME)
@inside (
		kinds = { ISymbolKind.SPECIES, ISymbolKind.MODEL })
@doc (
		value = "The equation statement is used to create an equation system from several single equations.",
		usages = { @usage (
				value = "The basic syntax to define an equation system is:",
				examples = { @example (
						value = "float t;",
						isExecutable = false),
						@example (
								value = "float S;",
								isExecutable = false),
						@example (
								value = "float I;",
								isExecutable = false),
						@example (
								value = "equation SI { ",
								isExecutable = false),
						@example (
								value = "   diff(S,t) = (- 0.3 * S * I / 100);",
								isExecutable = false),
						@example (
								value = "   diff(I,t) = (0.3 * S * I / 100);",
								isExecutable = false),
						@example (
								value = "} ",
								isExecutable = false) }),
				@usage (
						value = "If the type: facet is used, a predefined equation system is defined using variables vars: and parameters params: in the right order. All possible predefined equation systems are the following ones (see [EquationPresentation161 EquationPresentation161] for precise definition of each classical equation system): ",
						examples = { @example (
								value = "equation eqSI type: SI vars: [S,I,t] params: [N,beta];",
								isExecutable = false),
								@example (
										value = "equation eqSIS type: SIS vars: [S,I,t] params: [N,beta,gamma];",
										isExecutable = false),
								@example (
										value = "equation eqSIR type:SIR vars:[S,I,R,t] params:[N,beta,gamma];",
										isExecutable = false),
								@example (
										value = "equation eqSIRS type: SIRS vars: [S,I,R,t] params: [N,beta,gamma,omega,mu];",
										isExecutable = false),
								@example (
										value = "equation eqSEIR type: SEIR vars: [S,E,I,R,t] params: [N,beta,gamma,sigma,mu];",
										isExecutable = false),
								@example (
										value = "equation eqLV type: LV vars: [x,y,t] params: [alpha,beta,delta,gamma] ;",
										isExecutable = false) }),
				@usage (
						value = "If the simultaneously: facet is used, system of all the agents will be solved simultaneously.") },
		see = { "=", IKeyword.SOLVE })
@validator (SystemOfEquationsValidator.class)

@SuppressWarnings ({ "rawtypes", "unchecked" })
public class SystemOfEquationsStatement extends AbstractStatementSequence implements FirstOrderDifferentialEquations {

	public static class SystemOfEquationsValidator implements IDescriptionValidator<IDescription> {
		public final static Set<String> CLASSICAL_NAMES =
				new HashSet<>(Arrays.asList("SIR", "SI", "SIS", "SIRS", "SEIR", "LV"));

		@Override
		public void validate(final IDescription description) {
			final String type = description.getLitteral(TYPE);
			if (type == null) { return; }
			if (!CLASSICAL_NAMES.contains(type)) {
				description.error(type + " is not a recognized classical equation name", IGamlIssue.WRONG_TYPE, TYPE);
			}
		}
	}

	public static String ___equations = "___equations";
	public static String ___variables_diff = "___variables_diff";
	private final GamaMap<Integer, GamaPair<IAgent, SingleEquationStatement>> equations = GamaMapFactory.create();
	private final GamaMap<Integer, GamaPair<IAgent, IExpression>> variables_diff = GamaMapFactory.create();
	public IExpression variable_time = null;
	private IScope currentScope;
	IExpression simultan = null;

	public SystemOfEquationsStatement(final IDescription desc) {
		super(desc);
		setName(desc.getName());
		simultan = getFacet(IKeyword.SIMULTANEOUSLY);

	}

	/**
	 * This method separates regular statements and equations.
	 *
	 * @see msi.gaml.statements.AbstractStatementSequence#setChildren(java.util.List)
	 */
	@Override
	public void setChildren(final Iterable<? extends ISymbol> commands) {
		List<? extends ISymbol> cmd = Lists.newArrayList(commands);
		if (getFacet(IKeyword.TYPE) != null) {
			final String type = getFacet(IKeyword.TYPE).literalValue();
			switch (type) {
				case "SIR":
					cmd.clear();
					cmd = new ClassicalSIREquations(getDescription()).SIR(getFacet(IKeyword.VARS),
							getFacet(IKeyword.PARAMS));
					break;
				case "SI":
					cmd.clear();
					cmd = new ClassicalSIEquations(getDescription()).SI(getFacet(IKeyword.VARS),
							getFacet(IKeyword.PARAMS));
					break;
				case "SIS":
					cmd.clear();
					cmd = new ClassicalSISEquations(getDescription()).SIS(getFacet(IKeyword.VARS),
							getFacet(IKeyword.PARAMS));
					break;
				case "SIRS":
					cmd.clear();
					cmd = new ClassicalSIRSEquations(getDescription()).SIRS(getFacet(IKeyword.VARS),
							getFacet(IKeyword.PARAMS));
					break;
				case "SEIR":
					cmd.clear();
					cmd = new ClassicalSEIREquations(getDescription()).SEIR(getFacet(IKeyword.VARS),
							getFacet(IKeyword.PARAMS));
					break;
				case "LV":
					cmd.clear();
					cmd = new ClassicalLVEquations(getDescription()).LV(getFacet(IKeyword.VARS),
							getFacet(IKeyword.PARAMS));
			}
		}

		final List<ISymbol> others = new ArrayList<>();
		for (final ISymbol s : cmd) {
			if (s instanceof SingleEquationStatement) {
				((SingleEquationStatement) s).establishVar();
				for (int i = 0; i < ((SingleEquationStatement) s).getVars().size(); i++) {
					final IExpression v = ((SingleEquationStatement) s).getVar(i);
					if (((SingleEquationStatement) s).getOrder() > 0) {
						GamaPair p1 = new GamaPair<IAgent, SingleEquationStatement>(null, (SingleEquationStatement) s,
								Types.AGENT, Types.NO_TYPE);
						equations.put(equations.size(), p1);
						GamaPair p2 = new GamaPair<IAgent, IExpression>(null, v, Types.AGENT, Types.NO_TYPE);
						variables_diff.put(variables_diff.size(), p2);
					}
				}
				variable_time = ((SingleEquationStatement) s).getVarTime();
			} else {
				others.add(s);
			}
		}
		super.setChildren(others);
	}

	// static {
	// DEBUG.ON();
	// }

	static boolean OWN_DEBUG = false;

	public void assignValue(final IScope scope, final double time, final double[] y) {
		// final List<SingleEquationStatement> equationValues = new
		// ArrayList<SingleEquationStatement>(equations.values());
		// final List<IExpression> variableValues = new
		// ArrayList<IExpression>(variables_diff.values());
		GamaMap<Integer, GamaPair<IAgent, SingleEquationStatement>> myEQ = getEquations(currentScope.getAgent());
		GamaMap<Integer, GamaPair<IAgent, IExpression>> myVar = getVariableDiff(scope.getAgent());
		for (int i = 0, n = myEQ.size(); i < n; i++) {

			final GamaPair<IAgent, SingleEquationStatement> s = myEQ.get(i);
			if (s.getValue().getOrder() == 0) {
				continue;
			}

			final IAgent remoteAgent = s.getKey();// getEquationAgents(scope).get(i);
			boolean pushed = false;
			if (null != remoteAgent && !remoteAgent.dead()) {
				pushed = scope.push(remoteAgent);
				try {
					if (s.getValue().getVarTime() instanceof IVarExpression) {
						((IVarExpression) s.getValue().getVarTime()).setVal(scope, time, false);
					}
					if (myVar.get(i).getValue() instanceof IVarExpression) {
						if (OWN_DEBUG)
							DEBUG.LOG(remoteAgent + " assign " + myVar.get(i).getValue() + " " + y[i]);
						((IVarExpression) myVar.get(i).getValue()).setVal(scope, y[i], false);
					}
				} catch (final Throwable ex1) {
					GAMA.reportAndThrowIfNeeded(scope, GamaRuntimeException.create(ex1, scope), true);
				} finally {
					if (pushed) {
						scope.pop(remoteAgent);
					}
				}
			}

		}

		for (int i = 0, n = myEQ.size(); i < n; i++) {
			final GamaPair<IAgent, SingleEquationStatement> s = myEQ.get(i);
			if (s.getValue().getOrder() == 0) {
				final IExpression tmp = ((UnaryOperator) s.getValue().getFunction()).arg(0);
				final Object v = s.getValue().getExpression().value(currentScope);
				if (tmp instanceof AgentVariableExpression) {
					((AgentVariableExpression) tmp).setVal(currentScope, v, false);
				}
			}

		}
		setEquations(scope.getAgent(), myEQ);
		setVariableDiff(scope.getAgent(), myVar);
	}

	/**
	 * This method is bound to be called by the integrator of the equations system (instantiated in SolveStatement).
	 *
	 * @see org.apache.commons.math3.ode.FirstOrderDifferentialEquations#computeDerivatives(double, double[], double[])
	 */

	@Override
	public void computeDerivatives(final double time, final double[] y, final double[] ydot)
			throws MaxCountExceededException, DimensionMismatchException {
		/*
		 * the y value is calculated automatically inside integrator's algorithm just get y, and assign value to
		 * Variables in GAMA, which is use by GAMA modeler
		 */

		GamaMap<Integer, GamaPair<IAgent, SingleEquationStatement>> myEQ = getEquations(currentScope.getAgent());
		assignValue(currentScope, time, y);

		/*
		 * with variables assigned, calculate new value of expression in function loop through equations (internal and
		 * external) to get SingleEquation values
		 */

		// final List<SingleEquationStatement> equationValues = new
		// ArrayList<SingleEquationStatement>(equations.values());
		final Map<Integer, IAgent> equaAgents = getEquationAgents(currentScope);
		for (int i = 0, n = getDimension(); i < n; i++) {
			if (myEQ.get(i) == null)
				continue;
			try {
				final ExecutionResult result = currentScope.execute(myEQ.get(i).getValue(), equaAgents.get(i), null);
				ydot[i] = Cast.asFloat(currentScope, result.getValue());
			} catch (final Throwable e2) {
				GAMA.reportAndThrowIfNeeded(currentScope, GamaRuntimeException.create(e2, currentScope), true);
			}
		}
		setEquations(currentScope.getAgent(), myEQ);

	}

	/**
	 * The dimension of the equations system is simply, here, the number of equations.
	 *
	 * @see org.apache.commons.math3.ode.FirstOrderDifferentialEquations#getDimension()
	 */
	@Override
	public int getDimension() {
		int count = 0;
		GamaMap<Integer, GamaPair<IAgent, SingleEquationStatement>> myEQ = getEquations(currentScope.getAgent());
		for (final GamaPair<IAgent, SingleEquationStatement> s : myEQ.values()) {
			if (s.getValue().getOrder() > 0) {
				count++;
			}
		}

		return count;
	}

	private void setCurrentScope(final IScope currentScope) {
		this.currentScope = currentScope;
	}

	private Set<IAgent> getExternalAgents(final IScope scope) {
		if (scope.getAgent() == null)
			return new HashSet();
		Set<IAgent> result = (Set<IAgent>) scope.getAgent().getAttribute("__externalAgents");
		if (result == null) {
			result = new HashSet();
			scope.getAgent().setAttribute("__externalAgents", result);
		}
		return result;
	}

	public GamaMap<Integer, GamaPair<IAgent, SingleEquationStatement>> getEquations(IAgent agt) {
		if (agt == null)
			return GamaMapFactory.create();
		GamaMap<Integer, GamaPair<IAgent, SingleEquationStatement>> result =
				(GamaMap<Integer, GamaPair<IAgent, SingleEquationStatement>>) agt.getAttribute(___equations);
		if (result == null) {
			result = GamaMapFactory.create();
			agt.setAttribute(___equations, result);
		}
		return result;
	}

	public void setEquations(IAgent agt, GamaMap<Integer, GamaPair<IAgent, SingleEquationStatement>> eqs) {
		if (agt == null)
			return;
		agt.setAttribute(___equations, eqs);
	}

	public GamaMap<Integer, GamaPair<IAgent, IExpression>> getVariableDiff(IAgent agt) {
		if (agt == null)
			return GamaMapFactory.create();
		GamaMap<Integer, GamaPair<IAgent, IExpression>> result =
				(GamaMap<Integer, GamaPair<IAgent, IExpression>>) agt.getAttribute(___variables_diff);
		if (result == null) {
			result = GamaMapFactory.create();
			currentScope.getAgent().setAttribute(___variables_diff, result);
		}
		return result;
	}

	public void setVariableDiff(IAgent agt, GamaMap<Integer, GamaPair<IAgent, IExpression>> var) {
		if (agt == null)
			return;
		agt.setAttribute(___variables_diff, var);
	}

	public Map<Integer, IAgent> getEquationAgents(final IScope scope) {
		if (scope.getAgent() == null)
			return new HashMap();
		Map<Integer, IAgent> result = (Map<Integer, IAgent>) scope.getAgent().getAttribute("__equationAgents");
		if (result == null) {
			result = new HashMap();
			scope.getAgent().setAttribute("__equationAgents", result);
		}
		return result;

	}

	private void beginWithScope(final IScope scope) {
		setCurrentScope(scope);
		addInternalAgents(scope);
		addInternalEquations(scope);
		addExternalAgents(scope);
		addExternalEquations(scope);
	}

	private void finishWithScope(final IScope scope) {
		removeExternalEquations(scope);
		getEquationAgents(scope).clear();
		getExternalAgents(scope).clear();
		getEquations(scope.getAgent()).clear();
		getVariableDiff(scope.getAgent()).clear();
		setEquations(scope.getAgent(), null);
		setVariableDiff(scope.getAgent(), null);
		setCurrentScope(null);
	}

	public synchronized void executeInScope(final IScope scope, final Runnable criticalSection) {
		beginWithScope(scope);
		criticalSection.run();
		finishWithScope(scope);
	}

	private void addInternalAgents(final IScope scope) {
		for (int i = 0; i < equations.size(); i++) {
			getEquationAgents(scope).put(i, scope.getAgent());
		}
	}

	private void addInternalEquations(final IScope scope) {
		for (int i = 0; i < equations.size(); i++) {
			addEquationsOf(scope.getAgent());
		}
	}

	private void addExternalAgents(final IScope scope) {
		addExternalAgents(scope, simultan, getExternalAgents(scope));
	}

	private void addExternalEquations(final IScope scope) {
		for (final IAgent remoteAgent : getExternalAgents(scope)) {
			if (!remoteAgent.dead()) {
				addEquationsOf(remoteAgent);
			}
		}
	}

	private void removeExternalEquations(final IScope scope) {
		if (scope.getAgent() == null)
			return;
		final GamaMap<String, IList<Double>> result =
				(GamaMap<String, IList<Double>>) scope.getAgent().getAttribute("__integrated_values");
		for (final IAgent remoteAgent : getExternalAgents(scope)) {
			if (!remoteAgent.dead()) {
				remoteAgent.setAttribute("__integrated_values", result);
				removeEquationsOf(remoteAgent);
			}
		}
	}

	private void addEquationsOf(final IAgent remoteAgent) {
		GamaMap<Integer, GamaPair<IAgent, SingleEquationStatement>> myEQ = getEquations(currentScope.getAgent());
		GamaMap<Integer, GamaPair<IAgent, IExpression>> myVar = getVariableDiff(currentScope.getAgent());
		// if (remoteAgent.equals(currentScope.getAgent())) {
		for (final GamaPair<IAgent, SingleEquationStatement> e : myEQ.values()) {
			if (e.getKey().equals(remoteAgent)) { return; }
		}
		if (OWN_DEBUG)
			DEBUG.LOG(currentScope.getAgent() + " addEquationsOf " + remoteAgent);
		final SystemOfEquationsStatement ses =
				remoteAgent.getSpecies().getStatement(SystemOfEquationsStatement.class, getName());
		if (ses != null) {
			if (!remoteAgent.equals(currentScope.getAgent())) {
				for (int i = 0, n = ses.equations.size(); i < n; i++) {
					getEquationAgents(currentScope).put(getEquationAgents(currentScope).size(), remoteAgent);
				}
			}
			for (final GamaPair<IAgent, SingleEquationStatement> s : ses.equations.values()) {
				// final String name = remoteAgent.getName() + s.getKey();
				final SingleEquationStatement eq = s.getValue();
				if (OWN_DEBUG)
					DEBUG.LOG("eq " + eq);
				GamaPair p1 =
						new GamaPair<IAgent, SingleEquationStatement>(remoteAgent, eq, Types.AGENT, Types.NO_TYPE);
				myEQ.put(myEQ.size(), p1);
			}
			for (final GamaPair<IAgent, IExpression> s : ses.variables_diff.values()) {
				// final String name = remoteAgent.getName() + s.getKey();
				final IExpression v = s.getValue();
				if (OWN_DEBUG)
					DEBUG.LOG("v " + v);
				GamaPair p1 = new GamaPair<IAgent, IExpression>(remoteAgent, v, Types.AGENT, Types.NO_TYPE);
				myVar.put(myVar.size(), p1);
			}
		}

		// }else {
		// if (OWN_DEBUG) DEBUG.LOG("addEquationsOf "+remoteAgent);
		// GamaMap<Integer, GamaPair<IAgent, SingleEquationStatement>> eqs = getEquations(remoteAgent);
		// GamaMap<Integer, GamaPair<IAgent, IExpression>> sesVar = getVariableDiff(remoteAgent);
		// if (eqs != null) {
		// for (int i = 0, n = eqs.size(); i < n; i++) {
		// getEquationAgents(currentScope).put(getEquationAgents(currentScope).size(), remoteAgent);
		// }
		// for (final GamaPair<IAgent, SingleEquationStatement> s : eqs.values()) {
		// // final String name = remoteAgent.getName() + s.getKey();
		//// if(s.getKey()!=null) {
		// final SingleEquationStatement eq = s.getValue();
		// if (OWN_DEBUG) DEBUG.LOG("eq "+eq);
		// GamaPair p1 = new GamaPair<IAgent, SingleEquationStatement>(remoteAgent, eq, Types.AGENT,
		// Types.NO_TYPE);
		// myEQ.put(myEQ.size(), p1);
		//// }
		// }
		// for (final GamaPair<IAgent, IExpression> s : sesVar.values()) {
		// // final String name = remoteAgent.getName() + s.getKey();
		// final IExpression v = s.getValue();
		// DEBUG.LOG("v "+v);
		// GamaPair p1 = new GamaPair<IAgent, IExpression>(remoteAgent, v, Types.AGENT, Types.NO_TYPE);
		// myVar.put(myVar.size(), p1);
		// }
		// }
		// }

		setEquations(currentScope.getAgent(), myEQ);
		setVariableDiff(currentScope.getAgent(), myVar);

	}

	private void removeEquationsOf(final IAgent remoteAgent) {
		GamaMap<Integer, GamaPair<IAgent, SingleEquationStatement>> myEQ = getEquations(currentScope.getAgent());
		GamaMap<Integer, GamaPair<IAgent, IExpression>> myVar = getVariableDiff(currentScope.getAgent());

		// final SystemOfEquationsStatement ses =
		// remoteAgent.getSpecies().getStatement(SystemOfEquationsStatement.class,
		// getName());

		DEBUG.LOG("removeEquationsOf " + remoteAgent);
		// GamaMap<Integer, GamaPair<IAgent, SingleEquationStatement>> ses = getEquations(remoteAgent);
		// GamaMap<Integer, GamaPair<IAgent, IExpression>> sesVar = getVariableDiff(remoteAgent);
		if (myEQ != null) {
			// final int n = equations.size();
			for (final GamaPair<IAgent, SingleEquationStatement> e : myEQ.values()) {
				if (e.getKey().equals(remoteAgent)) {
					myVar.values().remove(remoteAgent);
					// for (final IExpression eV : e.getValue().getVars()) {
					// DEBUG.OUT(eV);
					// GamaPair p1 = new GamaPair<IAgent, IExpression>(remoteAgent, eV, Types.AGENT, Types.NO_TYPE);
					// myVar.values().remove(p1);
					// }
				}
			}
			myEQ.values().removeIf(e -> e.getKey().equals(remoteAgent));
			// for (final Integer s : ses.equations.keySet()) {
			// equations.remove(n - s - 1);
			// variables_diff.remove(n - s - 1);
			// }
		}
		setEquations(currentScope.getAgent(), myEQ);
		setVariableDiff(currentScope.getAgent(), myVar);
	}

	private void addExternalAgents(final IScope scope, final Object toAdd, final Set<IAgent> externalAgents) {
		if (toAdd instanceof IExpression) {
			addExternalAgents(scope, ((IExpression) toAdd).value(scope), externalAgents);
		} else if (toAdd instanceof IAgent && !toAdd.equals(scope.getAgent()) && !((IAgent) toAdd).dead()) {
			externalAgents.add((IAgent) toAdd);
		} else if (toAdd instanceof GamlSpecies) {
			addExternalAgents(scope, ((GamlSpecies) toAdd).getPopulation(scope), externalAgents);
		} else if (toAdd instanceof IList) {
			for (final Object o : ((IList) toAdd).iterable(scope)) {
				addExternalAgents(scope, o, externalAgents);
			}
		}
	}

}
