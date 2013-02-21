package msi.gaml.architecture.simplebdi;

import java.util.ArrayList;
import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.simulation.SimulationClock;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IGamlAgent;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.IPath;
import msi.gaml.architecture.reflex.AbstractArchitecture;
import msi.gaml.compilation.ISymbol;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Maths;
import msi.gaml.skills.ISkill;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;

@vars({ @var(name = SimpleBdiArchitecture.PERSISTENCE_COEFFICIENT, type = IType.FLOAT_STR, init = "1.0"), 
		@var(name = SimpleBdiArchitecture.BELIEF_BASE, type = IType.LIST_STR, init = "[]"),
		@var(name = SimpleBdiArchitecture.DESIRE_BASE, type = IType.LIST_STR, init = "[]")})

@skill(name = SimpleBdiArchitecture.SIMPLE_BDI)
public class SimpleBdiArchitecture extends AbstractArchitecture{
	public static final String SIMPLE_BDI = "simple_bdi";
	public static final String PLAN = "plan";
	public static final String PRIORITY = "priority";
	public static final String PERCEIVE = "perceive";
	public static final String PERSISTENCE_COEFFICIENT = "persistence_coefficient";
	public static final String PREDICATE_NAME = "predicate_name";
	public static final String PREDICATE_PARAMETERS = "predicate_parameters";
	public static final String BELIEF_BASE = "belief_base";
	private static final Double EPSILON = 0.0001;
	public static final String DESIRE_BASE = "desire_base";
	
	private IScope _consideringScope;
	private final List<SimpleBdiStatement> _plans = new ArrayList<SimpleBdiStatement>();
	private final List<SimpleBdiStatement> _perceives = new ArrayList<SimpleBdiStatement>();
	private int _plansNumber = 0;
	private int _perceiveNumber = 0;
	private SimpleBdiStatement _persistentTask = null;
	/* msi.gaml.architecture.reflex.ReflexArchitecture cannot be cast to msi.gaml.architecture.simplebdi.SimpleBdiArchitecture *
	@getter(PERSISTENCE_COEFFICIENT)
	public double getPersistenceCoefficient(final IAgent agent){
		return (Double) agent.getAttribute(PERSISTENCE_COEFFICIENT);
	}
	
	@setter(PERSISTENCE_COEFFICIENT)
	public void setPersistenceCoefficient(final IAgent agent, final double persistenceCoefficient){
		agent.setAttribute(PERSISTENCE_COEFFICIENT, persistenceCoefficient);
	}
	/**/
	
	@Override
	public void setChildren(final List<? extends ISymbol> children) {
		for ( ISymbol c : children ) {
			addBehavior((IStatement) c);
		}
	}

	public void addBehavior(final IStatement c) {
		if ( c instanceof SimpleBdiStatement ) {
			String statementKeyword = c.getFacet("keyword").value(_consideringScope).toString();
			if (statementKeyword.equals(PERCEIVE)){
				_perceives.add((SimpleBdiStatement) c);
				_perceiveNumber = _perceives.size();
			}
			else{
				_plans.add((SimpleBdiStatement) c);
				_plansNumber++;
			}
		} 
	}
	
	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		return executePlans(scope);
	}

	protected final Object executePlans(final IScope scope) {
		Object result = null;
		if (_perceiveNumber > 0){
			for (int i = 0; i < _perceiveNumber; i++){
				result = _perceives.get(i).executeOn(scope);
			}
		}
		if (_plansNumber > 0){
			IGamlAgent a = getCurrentAgent(scope);
			/**/
			Double persistenceCoefficient = scope.hasArg(PERSISTENCE_COEFFICIENT) ? scope.getFloatArg(PERSISTENCE_COEFFICIENT) : (Double)a.getAttribute(PERSISTENCE_COEFFICIENT);
			//Double persistenceCoefficient = (Double)a.getAttribute(PERSISTENCE_COEFFICIENT);
			if ((persistenceCoefficient != null) && (persistenceCoefficient > 0)){
				Boolean flipResult = msi.gaml.operators.Random.opFlip(scope, persistenceCoefficient);
				//System.out.println("Flip result of " + persistenceCoefficient + " is " + flipResult);
				if (flipResult){
					if (_persistentTask == null){
						_persistentTask = selectExecutablePlanWithHighestPriority(scope);
					}
					if (_persistentTask != null){
						if (!a.dead()){
							result = _persistentTask.executeOn(scope);
						}
					}
				}
				else{
					_persistentTask = selectExecutablePlanWithHighestPriority(scope);
					if (_persistentTask != null){
						if (!a.dead()){
							result = _persistentTask.executeOn(scope);
						}
					}
				}
				//System.out.println("chosen task: " + _persistentTask.getName());
			}
			/**/	
		}
		return result;
	}
	protected final SimpleBdiStatement selectExecutablePlanWithHighestPriority(final IScope scope){
		SimpleBdiStatement resultStatement = null;
		double highestPriority = Double.MIN_VALUE;
		for (SimpleBdiStatement statement: _plans){
			boolean isContextConditionSatisfied = ( (statement.getContextExpression() == null) || Cast.asBool(scope, statement.getContextExpression().value(scope)) );
			if (isContextConditionSatisfied){
				double currentPriority = Cast.asFloat(scope, statement.getPriorityExpression().value(scope));
				if (highestPriority < currentPriority){
					highestPriority = currentPriority;
					resultStatement = statement;
				}
			}
		}
		return resultStatement;
	}
	
	@action(name = "add_belief")
	@args(names = { PREDICATE_NAME, PREDICATE_PARAMETERS})
	public Object primAddBelief(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		String predicateName = scope.hasArg(PREDICATE_NAME) ? scope.getStringArg(PREDICATE_NAME) : (String)agent.getAttribute(PREDICATE_NAME);
		GamaList<Object> predicateParameters = (GamaList<Object>) (scope.hasArg(PREDICATE_PARAMETERS) ? scope.getListArg(PREDICATE_PARAMETERS) : (GamaList<Object>)agent.getAttribute(PREDICATE_PARAMETERS));
		if ( (predicateName == null) || (predicateName.length() == 0)){
			return null;
		}
		//System.out.println(predicateName);
		GamaList<Object> factBase = (GamaList<Object>) (scope.hasArg(BELIEF_BASE) ? scope.getListArg(BELIEF_BASE) : (GamaList<Object>)agent.getAttribute(BELIEF_BASE));
		GamaList<Object> predicateItem = new GamaList<Object>();
		predicateItem.add(predicateName);
		if ( (predicateParameters != null) && (predicateParameters.size() > 0)){
			predicateItem.add(predicateParameters);
		}
		else predicateItem.add(null);
		int perceiveTime = scope.getClock().getCycle();
		boolean isExistPredicate = false;
		for (Object item: factBase){
			try{
				GamaList<Object> currentPredicate = (GamaList<Object>) item;
				if (isEqualPredicates(predicateItem, currentPredicate)){
					isExistPredicate = true;
					if (currentPredicate.size() >= 4){
						currentPredicate.set(3, perceiveTime);
					}
					break;
				}
			}
			catch (Exception e1){
				System.out.println(perceiveTime + ": " + e1.getMessage());
			}
		}
		if (!isExistPredicate){
			predicateItem.add(perceiveTime);
			predicateItem.add(perceiveTime);
			factBase.add(predicateItem);
		}
		agent.setAttribute(BELIEF_BASE, factBase);
		return null;
	}
	
	@action(name = "has_belief")
	@args(names = { PREDICATE_NAME, PREDICATE_PARAMETERS})
	public Boolean primTestBelief(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		String predicateName = scope.hasArg(PREDICATE_NAME) ? scope.getStringArg(PREDICATE_NAME) : (String)agent.getAttribute(PREDICATE_NAME);
		GamaList<Object> predicateParameters = (GamaList<Object>) (scope.hasArg(PREDICATE_PARAMETERS) ? scope.getListArg(PREDICATE_PARAMETERS) : (GamaList<Object>)agent.getAttribute(PREDICATE_PARAMETERS));
		if ( (predicateName == null) || (predicateName.length() == 0)){
			return false;
		}
		//System.out.println(predicateName);
		GamaList<Object> factBase = (GamaList<Object>) (scope.hasArg(BELIEF_BASE) ? scope.getListArg(BELIEF_BASE) : (GamaList<Object>)agent.getAttribute(BELIEF_BASE));
		GamaList<Object> predicateItem = new GamaList<Object>();
		predicateItem.add(predicateName);
		if ( (predicateParameters != null) && (predicateParameters.size() > 0)){
			predicateItem.add(predicateParameters);
		}
		else predicateItem.add(null);
		int perceiveTime = scope.getClock().getCycle();
		for (Object item: factBase){
			try{
				GamaList<Object> currentPredicate = (GamaList<Object>) item;
				if (isEqualPredicates(predicateItem, currentPredicate)){
					return true;
				}
			}
			catch (Exception e1){
				System.out.println(perceiveTime + ": " + e1.getMessage());
			}
		}
		return false;
	}
	
	@action(name = "add_desire")
	@args(names = { PREDICATE_NAME, PREDICATE_PARAMETERS})
	public Object primAddDesire(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		String predicateName = scope.hasArg(PREDICATE_NAME) ? scope.getStringArg(PREDICATE_NAME) : (String)agent.getAttribute(PREDICATE_NAME);
		GamaList<Object> predicateParameters = (GamaList<Object>) (scope.hasArg(PREDICATE_PARAMETERS) ? scope.getListArg(PREDICATE_PARAMETERS) : (GamaList<Object>)agent.getAttribute(PREDICATE_PARAMETERS));
		if ( (predicateName == null) || (predicateName.length() == 0)){
			return null;
		}
		//System.out.println(predicateName);
		GamaList<Object> desireBase = (GamaList<Object>) (scope.hasArg(DESIRE_BASE) ? scope.getListArg(DESIRE_BASE) : (GamaList<Object>)agent.getAttribute(DESIRE_BASE));
		GamaList<Object> predicateItem = new GamaList<Object>();
		predicateItem.add(predicateName);
		if ( (predicateParameters != null) && (predicateParameters.size() > 0)){
			predicateItem.add(predicateParameters);
		}
		else predicateItem.add(null);
		int perceiveTime = scope.getClock().getCycle();
		boolean isExistPredicate = false;
		for (Object item: desireBase){
			try{
				GamaList<Object> currentPredicate = (GamaList<Object>) item;
				if (isEqualPredicates(predicateItem, currentPredicate)){
					isExistPredicate = true;
					if (currentPredicate.size() >= 4){
						currentPredicate.set(3, perceiveTime);
					}
					break;
				}
			}
			catch (Exception e1){
				System.out.println(perceiveTime + ": " + e1.getMessage());
			}
		}
		if (!isExistPredicate){
			predicateItem.add(perceiveTime);
			predicateItem.add(perceiveTime);
			desireBase.add(predicateItem);
		}
		agent.setAttribute(DESIRE_BASE, desireBase);
		return null;
	}
	
	@action(name = "has_desire")
	@args(names = { PREDICATE_NAME, PREDICATE_PARAMETERS})
	public Boolean primTestDesire(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		String predicateName = scope.hasArg(PREDICATE_NAME) ? scope.getStringArg(PREDICATE_NAME) : (String)agent.getAttribute(PREDICATE_NAME);
		GamaList<Object> predicateParameters = (GamaList<Object>) (scope.hasArg(PREDICATE_PARAMETERS) ? scope.getListArg(PREDICATE_PARAMETERS) : (GamaList<Object>)agent.getAttribute(PREDICATE_PARAMETERS));
		if ( (predicateName == null) || (predicateName.length() == 0)){
			return false;
		}
		//System.out.println(predicateName);
		GamaList<Object> desireBase = (GamaList<Object>) (scope.hasArg(DESIRE_BASE) ? scope.getListArg(DESIRE_BASE) : (GamaList<Object>)agent.getAttribute(DESIRE_BASE));
		GamaList<Object> predicateItem = new GamaList<Object>();
		predicateItem.add(predicateName);
		if ( (predicateParameters != null) && (predicateParameters.size() > 0)){
			predicateItem.add(predicateParameters);
		}
		else predicateItem.add(null);
		int perceiveTime = scope.getClock().getCycle();
		for (Object item: desireBase){
			try{
				GamaList<Object> currentPredicate = (GamaList<Object>) item;
				if (isEqualPredicates(predicateItem, currentPredicate)){
					return true;
				}
			}
			catch (Exception e1){
				System.out.println(perceiveTime + ": " + e1.getMessage());
			}
		}
		return false;
	}
	
	@action(name = "remove_belief")
	@args(names = { PREDICATE_NAME, PREDICATE_PARAMETERS})
	public Object primRemoveBelief(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		String predicateName = scope.hasArg(PREDICATE_NAME) ? scope.getStringArg(PREDICATE_NAME) : (String)agent.getAttribute(PREDICATE_NAME);
		GamaList<Object> predicateParameters = (GamaList<Object>) (scope.hasArg(PREDICATE_PARAMETERS) ? scope.getListArg(PREDICATE_PARAMETERS) : (GamaList<Object>)agent.getAttribute(PREDICATE_PARAMETERS));
		if ( (predicateName == null) || (predicateName.length() == 0)){
			return null;
		}
		//System.out.println(predicateName);
		GamaList<Object> factBase = (GamaList<Object>) (scope.hasArg(BELIEF_BASE) ? scope.getListArg(BELIEF_BASE) : (GamaList<Object>)agent.getAttribute(BELIEF_BASE));
		GamaList<Object> predicateItem = new GamaList<Object>();
		predicateItem.add(predicateName);
		if ( (predicateParameters != null) && (predicateParameters.size() > 0)){
			predicateItem.add(predicateParameters);
		}
		else predicateItem.add(null);
		int perceiveTime = scope.getClock().getCycle();
		GamaList<Object> newFactBase = new GamaList<Object>();
		for (Object item: factBase){
			try{
				GamaList<Object> currentPredicate = (GamaList<Object>) item;
				if (!isEqualPredicates(predicateItem, currentPredicate)){
					newFactBase.add(currentPredicate);
				}
			}
			catch (Exception e1){
				System.out.println(perceiveTime + ": " + e1.getMessage());
			}
		}
		agent.setAttribute(BELIEF_BASE, newFactBase);
		return null;
	}
	
	@action(name = "remove_desire")
	@args(names = { PREDICATE_NAME, PREDICATE_PARAMETERS})
	public Object primRemoveDesire(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		String predicateName = scope.hasArg(PREDICATE_NAME) ? scope.getStringArg(PREDICATE_NAME) : (String)agent.getAttribute(PREDICATE_NAME);
		GamaList<Object> predicateParameters = (GamaList<Object>) (scope.hasArg(PREDICATE_PARAMETERS) ? scope.getListArg(PREDICATE_PARAMETERS) : (GamaList<Object>)agent.getAttribute(PREDICATE_PARAMETERS));
		if ( (predicateName == null) || (predicateName.length() == 0)){
			return null;
		}
		//System.out.println(predicateName);
		GamaList<Object> desireBase = (GamaList<Object>) (scope.hasArg(DESIRE_BASE) ? scope.getListArg(DESIRE_BASE) : (GamaList<Object>)agent.getAttribute(DESIRE_BASE));
		GamaList<Object> predicateItem = new GamaList<Object>();
		predicateItem.add(predicateName);
		if ( (predicateParameters != null) && (predicateParameters.size() > 0)){
			predicateItem.add(predicateParameters);
		}
		else predicateItem.add(null);
		int perceiveTime = scope.getClock().getCycle();
		GamaList<Object> newDesireBase = new GamaList<Object>();
		for (Object item: desireBase){
			try{
				GamaList<Object> currentPredicate = (GamaList<Object>) item;
				if (!isEqualPredicates(predicateItem, currentPredicate)){
					newDesireBase.add(currentPredicate);
				}
			}
			catch (Exception e1){
				System.out.println(perceiveTime + ": " + e1.getMessage());
			}
		}
		agent.setAttribute(DESIRE_BASE, newDesireBase);
		return null;
	}
	
	@Override
	public void init(final IScope scope) throws GamaRuntimeException {
		this._consideringScope = scope;
	}

	@Override
	public void verifyBehaviors(final ISpecies context) {
	}

	@Override
	public void dispose() {}
	
	private boolean isEqualPredicates(GamaList<Object> predicate1, GamaList<Object> predicate2){
		if ((predicate1.size() < 2) || (predicate2.size() < 0)) return false;
		Object currentElement; 
		currentElement = predicate1.get(0);
		if (!(currentElement instanceof String)) return false;
		String name1 = (String) currentElement;
		currentElement = predicate2.get(0);
		if (!(currentElement instanceof String)) return false;
		String name2 = (String) currentElement;
		if (!(name1.equals(name2))) return false;
		
		currentElement = predicate1.get(1);
		if (!(currentElement instanceof GamaList<?>)) return false;
		GamaList<?> parameter1 = (GamaList<?>) currentElement;
		currentElement = predicate2.get(1);
		if (!(currentElement instanceof GamaList<?>)) return false;
		GamaList<?> parameter2 = (GamaList<?>) currentElement;
		if (parameter1.size() != parameter2.size()) return false;
		for (int index = 0; index < parameter1.size(); index++){
			Object childElement1 = parameter1.get(index);
			Object childElement2 = parameter2.get(index);
			if (childElement1 instanceof String){
				if (!(childElement2 instanceof String)) return false;
				String value1 = (String) childElement1;
				String value2 = (String) childElement2;
				if (! value1.equals(value2)) return false;
			}
			else if (childElement1 instanceof Double){
				if (!(childElement2 instanceof Double)) return false;
				Double value1 = (Double) childElement1;
				Double value2 = (Double) childElement2;
				if (Maths.abs(value1 - value2) > EPSILON) return false;
			}
			else if (childElement1 instanceof Integer){
				if (!(childElement2 instanceof Integer)) return false;
				Integer value1 = (Integer) childElement1;
				Integer value2 = (Integer) childElement2;
				if ((value1 - value2) > 0) return false;
			}
			else{
				return false;
			}
		}
		return true;
	}
}
