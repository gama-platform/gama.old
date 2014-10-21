/*********************************************************************************************
 * 
 *
 * 'SimpleBdiArchitecture.java', in plugin 'msi.gaml.architecture.simplebdi', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.architecture.simplebdi;

import java.util.*;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.RandomUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaPair;
import msi.gaml.architecture.reflex.AbstractArchitecture;
import msi.gaml.architecture.reflex.ReflexArchitecture;
import msi.gaml.compilation.ISymbol;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;

@vars({ @var(name = SimpleBdiArchitecture.PERSISTENCE_COEFFICIENT, type = IType.FLOAT, init = "1.0"),
	 @var(name = SimpleBdiArchitecture.PERSISTENCE_COEFFICIENT_GOALS, type = IType.FLOAT, init = "1.0"),
	 @var(name = SimpleBdiArchitecture.PROBABILISTIC_CHOICE, type = IType.BOOL, init = "true"),
	 @var(name = SimpleBdiArchitecture.BELIEF_BASE, type = IType.LIST, init = "[]"),
	@var(name = SimpleBdiArchitecture.LAST_THOUGHTS, type = IType.LIST, init = "[]"),
	@var(name = SimpleBdiArchitecture.INTENSION_BASE, type = IType.LIST, init = "[]"),
	@var(name = SimpleBdiArchitecture.DESIRE_BASE, type = IType.LIST, init = "[]") })
@skill(name = SimpleBdiArchitecture.SIMPLE_BDI)
public class SimpleBdiArchitecture extends ReflexArchitecture {

	public static final String SIMPLE_BDI = "simple_bdi";
	public static final String PLAN = "plan";
	public static final String PRIORITY = "priority";
	public static final String EXECUTEDWHEN = "executed_when";
	public static final String PERCEIVE = "perceive";
	public static final String PERSISTENCE_COEFFICIENT = "persistence_coefficient_plans";
	public static final String PERSISTENCE_COEFFICIENT_GOALS = "persistence_coefficient_goals";
	public static final String PROBABILISTIC_CHOICE = "probabilistic_choice";
//	public static final String PREDICATE = "predicate";
	public static final String LAST_THOUGHTS = "thinking";
	public static final Integer LAST_THOUGHTS_SIZE = 5;
	public static final String PREDICATE = "predicate";
	public static final String PREDICATE_NAME = "name";
	public static final String PREDICATE_VALUE = "value";
	public static final String PREDICATE_PRIORITY = "priority";
	public static final String PREDICATE_PARAMETERS = "parameters";
	public static final String PREDICATE_ONHOLD = "on_hold_until";
	public static final String PREDICATE_TODO = "todo";
	public static final String PREDICATE_SUBGOALS = "subgoals";
	public static final String PREDICATE_DATE = "date";
	public static final String BELIEF_BASE = "belief_base";
	private static final Double EPSILON = 0.0001;
	public static final String REMOVE_DESIRE_AND_INTENSION = "desire_also";
	public static final String DESIRE_BASE = "desire_base";
	public static final String INTENSION_BASE = "intension_base";
	public static final String EVERY_VALUE = "every_possible_value_";

	private IScope _consideringScope;
	private final List<SimpleBdiStatement> _plans = new ArrayList<SimpleBdiStatement>();
	private final List<SimpleBdiStatement> _perceives = new ArrayList<SimpleBdiStatement>();
	private int _plansNumber = 0;
	private int _perceiveNumber = 0;
	private SimpleBdiStatement _persistentTask = null;

	
	/*
	 * msi.gaml.architecture.reflex.ReflexArchitecture cannot be cast to
	 * msi.gaml.architecture.simplebdi.SimpleBdiArchitecture *
	 * 
	 * @getter(PERSISTENCE_COEFFICIENT)
	 * public double getPersistenceCoefficient(final IAgent agent){
	 * return (Double) agent.getAttribute(PERSISTENCE_COEFFICIENT);
	 * }
	 * 
	 * @setter(PERSISTENCE_COEFFICIENT)
	 * public void setPersistenceCoefficient(final IAgent agent, final double persistenceCoefficient){
	 * agent.setAttribute(PERSISTENCE_COEFFICIENT, persistenceCoefficient);
	 * }
	 * 
	 * A predicate (belief, desire, intension) is a map with:
	 * name (string): name of the predicate
	 * parameters (map): the complete the name (same level as the name, must be equal to be considered same predicate)
	 * value (object): the goal
	 * priority (float - should be expression): the priority for desire selectin
	 * todo / subgoals (list<objects(predicates)>) : what did create the disire/desires created (lists) : for desire/intension removal when intension completed
	 * wait_until (expression/list) : the goal is put on hold (no plan selected) until the condition is reached. If a list, it is a list of subgoals that must not be in the desire base anymore
	 * predicate_date: data it was created/updated
	 * /*
	 */

	@Override
	public void setChildren(final List<? extends ISymbol> children) {
		super.setChildren(children);
		for ( ISymbol c : children ) {
			addBehavior((IStatement) c);
		}
	}

	public void addBehavior(final IStatement c) {
		super.addBehavior(c);
		if ( c instanceof SimpleBdiStatement ) {
			String statementKeyword = c.getFacet("keyword").value(_consideringScope).toString();
			if ( statementKeyword.equals(PERCEIVE) ) {
				_perceives.add((SimpleBdiStatement) c);
				_perceiveNumber = _perceives.size();
			} else {
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
		if ( _perceiveNumber > 0 ) {
			for ( int i = 0; i < _perceiveNumber; i++ ) {
				result = _perceives.get(i).executeOn(scope);
			}
		}
		if ( _plansNumber > 0 ) {
			final IAgent agent = getCurrentAgent(scope);
			GamaList<Object> desireBase =
				(GamaList<Object>) (scope.hasArg(DESIRE_BASE) ? scope.getListArg(DESIRE_BASE) : (GamaList<Object>) agent
					.getAttribute(DESIRE_BASE));
			GamaList<Object> intensionBase =
					(GamaList<Object>) (scope.hasArg(INTENSION_BASE) ? scope.getListArg(INTENSION_BASE) : (GamaList<Object>) agent
						.getAttribute(INTENSION_BASE));
			// IGamlAgent a = getCurrentAgent(scope);

			Double persistenceCoefficientPlans =
				scope.hasArg(PERSISTENCE_COEFFICIENT) ? scope.getFloatArg(PERSISTENCE_COEFFICIENT) : (Double) agent.getAttribute(PERSISTENCE_COEFFICIENT);
			// Double persistenceCoefficient = (Double)a.getAttribute(PERSISTENCE_COEFFICIENT);
			Double persistenceCoefficientgoal =
					scope.hasArg(PERSISTENCE_COEFFICIENT_GOALS) ? scope.getFloatArg(PERSISTENCE_COEFFICIENT_GOALS) : (Double) agent
						.getAttribute(PERSISTENCE_COEFFICIENT_GOALS);
					// Double persistenceCoefficient = (Double)a.getAttribute(PERSISTENCE_COEFFICIENT);

// CHECK FOR ACHIEVED INTENSIONS					
					
					if ( intensionBase.size()>0) {
						for( int i=intensionBase.size()-1; i>=0; i--)
						{
							
						}
						
					}
					
					
// RANDOMLY REMOVE (last)INTENSION					
					
					Boolean flipResultgoal = msi.gaml.operators.Random.opFlip(scope, persistenceCoefficientgoal);
						// System.out.println("Flip result of " + persistenceCoefficient + " is " + flipResult);
				if ( !flipResultgoal ) {
					if ( intensionBase.size()>0) {
						 addThoughts(scope,"check what happens if I remove: " +intensionBase.get(intensionBase.size()-1));
						 GamaMap<String,Object> previousint=(GamaMap<String,Object>)intensionBase.get(intensionBase.size()-1);
//						 double priority=Cast.asFloat(scope, previousint.get(PREDICATE_PRIORITY))+EPSILON;
						 double priority=Cast.asFloat(scope, previousint.get(PREDICATE_PRIORITY));
						 previousint.put(PREDICATE_PRIORITY, priority);
						intensionBase.remove(intensionBase.size()-1);
						this.addToBase(scope, previousint, desireBase);
						 _persistentTask=null;						
					}
				}
				
			// If current intension has no plan or is on hold, choose a new Desire				
				if (testOnHold(scope,this.currentGoal(scope)) || selectExecutablePlanWithHighestPriority(scope)==null)
				{
					selectDesireWithHighestPriority(scope);
					_persistentTask=null;
					
				}

				
				
				
//			if ( _persistentTask != null ) 
//				 System.out.println("persistant task: " + _persistentTask.getName());
//			if ( persistenceCoefficient != null && persistenceCoefficient >= 0 ) {
				Boolean flipResult = msi.gaml.operators.Random.opFlip(scope, persistenceCoefficientPlans);
				// System.out.println("Flip result of " + persistenceCoefficient + " is " + flipResult);

// Randomly change persistant plan				
				if ( !flipResult ) 
				{
					if ( _persistentTask != null ) 
						addThoughts(scope,"check what happens if I stop: " +_persistentTask.getName());
//					selectDesireWithHighestPriority(scope);
					_persistentTask = selectExecutablePlanWithHighestPriority(scope);
					
					if ( _persistentTask != null ) {
					addThoughts(scope,"lets do instead " + _persistentTask.getName());
					}
					
				}

// choose a plan for the current goal
				
				if (( _persistentTask == null )&&(currentGoal(scope)==null)) {
						selectDesireWithHighestPriority(scope);
						_persistentTask = selectExecutablePlanWithHighestPriority(scope);
						if (currentGoal(scope)==null)
						{
							addThoughts(scope,"I want nothing...");
							return null;
							
						}
						addThoughts(scope,"ok, new goal: "+this.currentGoal(scope)+" with plan " + _persistentTask.getName());
					}

				if (( _persistentTask == null )&&(currentGoal(scope)!=null)) {
//					selectDesireWithHighestPriority(scope);
					_persistentTask = selectExecutablePlanWithHighestPriority(scope);
					if (_persistentTask!=null)
					addThoughts(scope,"use plan : "+_persistentTask.getName());
				}
				
				if ( _persistentTask != null ) {
						if ( !agent.dead() ) {
							result = _persistentTask.executeOn(scope);
								boolean isExecuted =
										_persistentTask.getExecutedExpression() == null ||
										msi.gaml.operators.Cast.asBool(scope, _persistentTask.getExecutedExpression().value(scope));
								if (isExecuted)
								{
//									addThoughts(scope,"plan " + _persistentTask.getName()+" finished! ");
									 _persistentTask=null;
									
								}
								if (testInBase(scope,currentGoal(scope),getBase(scope,BELIEF_BASE)))
								{
									addThoughts(scope,"goal " + currentGoal(scope)+" reached! abort current plan... ");
									 removeFromBase(scope,currentGoal(scope),getBase(scope,DESIRE_BASE));
									 removeFromBase(scope,currentGoal(scope),getBase(scope,INTENSION_BASE));

									 _persistentTask=null;
									
								}
							
						}
					}
//				if ((result!=null)&&(_persistentTask != null ))
//					addThoughts(scope,"chosen task: " + _persistentTask.getName()+" res "+result);
//			}
			/**/
		}
	
		return result;
	}

	
	
	protected final Boolean selectDesireWithHighestPriority(final IScope scope) {
		final IAgent agent = getCurrentAgent(scope);
		GamaList<Object> desireBase = this.getBase(scope, DESIRE_BASE);
		GamaList<Object> intensionBase = this.getBase(scope, INTENSION_BASE);
		double maxpriority=-1;
		if ( desireBase.size()>0 && intensionBase!=null) {
			Object newGoal=desireBase.anyValue(scope);
			for (Object desire:scope.getExperiment().getRandomGenerator().shuffle(desireBase))
			{
				if (Cast.asFloat(scope,((GamaMap)desire).get(PREDICATE_PRIORITY))>maxpriority)
					if (!(intensionBase.contains(desire)))
				{
//					 System.out.println("better priority: " +Cast.asFloat(scope,((GamaMap)desire).get(PREDICATE_PRIORITY))+" for "+((GamaMap)desire));
					 maxpriority=Cast.asFloat(scope,((GamaMap)desire).get(PREDICATE_PRIORITY));
					 newGoal=((GamaMap)desire);
					
				}
			}
			if (!(intensionBase.contains(newGoal)))
			{
//				 addThoughts(scope,"new goal: " +Cast.asFloat(scope,((GamaMap)newGoal).get(PREDICATE_PRIORITY))+" for "+((GamaMap)newGoal));
			intensionBase.add(newGoal);
			return true;
			}
		}
		return false;
	}

	protected final SimpleBdiStatement selectExecutablePlanWithHighestPriority(final IScope scope) {
		SimpleBdiStatement resultStatement = null;
		double highestPriority = Double.MIN_VALUE;
		for ( Object statement : scope.getExperiment().getRandomGenerator().shuffle(_plans) ) {
			boolean isContextConditionSatisfied =
				((SimpleBdiStatement)statement).getContextExpression() == null ||
					msi.gaml.operators.Cast.asBool(scope, ((SimpleBdiStatement)statement).getContextExpression().value(scope));
			if ( isContextConditionSatisfied ) {
				double currentPriority =
					msi.gaml.operators.Cast.asFloat(scope, ((SimpleBdiStatement)statement).getPriorityExpression().value(scope));
				if ( highestPriority < currentPriority ) {
					highestPriority = currentPriority;
					resultStatement = ((SimpleBdiStatement)statement);
				}
			}
		}
		return resultStatement;
	}
/*
	@action(name = "plan_completed")
	public Object planCompleted(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		_persistentTask = null; 
		return true;
	}
*/
	/*
	@action(name = "add_belief",args = {
			@arg(name = PREDICATE, type = IType.PREDICATE, optional = false, doc = @doc("the speed to use for this move (replaces the current value of speed)"))})
	public Object primAddBelief(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		Predicate pred =
				(Predicate) (scope.hasArg(PREDICATE) ? scope.getArg(PREDICATE,IType.PREDICATE) : (Predicate) agent
			.getAttribute(PREDICATE));
		if ( pred.get("name") == null ) { return null; }
		 java.lang.System.out.println(pred);
		GamaList<Predicate> factBase =
			(GamaList<Predicate>) (scope.hasArg(BELIEF_BASE) ? scope.getListArg(BELIEF_BASE) : (GamaList<Object>) agent
				.getAttribute(BELIEF_BASE));
		int perceiveTime = scope.getClock().getCycle();
		boolean isExistPredicate = false;
		for ( Predicate item : factBase ) {
			try {
				Predicate currentPredicate = item;
				if ( Predicate.isSamePredicate(scope,pred, currentPredicate) ) {
					isExistPredicate = true;
					factBase.remove(currentPredicate);
					pred.add(new GamaPair("time",perceiveTime));
					factBase.add(pred);
					break;
				}
			} catch (Exception e1) {
				//System.out.println(perceiveTime + ": " + e1.getMessage());
			}
		}
		if ( !isExistPredicate ) {
			pred.add(new GamaPair("time",perceiveTime));
			factBase.add(pred);
		}
		 java.lang.System.out.println(factBase);
		agent.setAttribute(BELIEF_BASE, factBase);
		return null;
	}
*/

	public GamaList<String> getThoughts(IScope scope)
	{
		final IAgent agent = getCurrentAgent(scope);
		GamaList<String> thoughts = (GamaList<String>) agent.getAttribute(LAST_THOUGHTS);
		return thoughts;
	}

	public GamaList<String> addThoughts(IScope scope,String think)
	{
		final IAgent agent = getCurrentAgent(scope);
		GamaList<String> thoughts = (GamaList<String>) agent.getAttribute(LAST_THOUGHTS);
		GamaList newthoughts=new GamaList<String>();
		newthoughts.add(think);
		if (thoughts!=null && thoughts.size()>0)
		{
			newthoughts.addAll(thoughts.subList(0, Math.min(LAST_THOUGHTS_SIZE-1,thoughts.size())));
		}
		agent.setAttribute(LAST_THOUGHTS, newthoughts);			
		return newthoughts;
	}


	public boolean testOnHold(final IScope scope, GamaMap goal)
	{
		if (goal==null) return false;
		if (goal.get(this.PREDICATE_ONHOLD)==null) return false;
		Object cond=goal.get(this.PREDICATE_ONHOLD);
		if (cond instanceof GamaList)
		{
			GamaList desbase=this.getBase(scope, this.DESIRE_BASE);
			if (desbase.length(scope)==0) return false;
			boolean end=true;
			for (Object subgoal:((GamaList)cond))
			{
				for (Object subgoald:((GamaList)desbase))
				{
					if ((this.isEqualPredicates((GamaMap)subgoal, (GamaMap)subgoald)) &&((((GamaMap)subgoal).get(PREDICATE_VALUE).equals(SimpleBdiArchitecture.EVERY_VALUE))||(((GamaMap)subgoal).get(PREDICATE_VALUE).equals(((GamaMap)subgoald).get(PREDICATE_VALUE)))) ) 
						return true;
				}
//				if (desbase.contains(subgoal)==true) return true;
				
			}
			this.addThoughts(scope, "no more subgoals for"+goal);
			return false;
		}
		if (cond instanceof String)
		{
			Object res=msi.gaml.operators.System.opEvalGaml(scope,(String)cond);
//			this.addThoughts(scope, "exp eval for "+goal+" is "+Cast.asBool(scope, res));
			if (Cast.asBool(scope, res)==false) return true;
			
		}
		return false;

		
	}
	

	public GamaList<Object> getBase(IScope scope, String basename)
	{
		final IAgent agent = getCurrentAgent(scope);
		return (GamaList<Object>) (scope.hasArg(basename) ? scope.getListArg(basename) : (GamaList<Object>) agent
				.getAttribute(basename));
	}
	
	public boolean testInBase(IScope scope,GamaMap predicateItem,GamaList<Object> base)
	{
		boolean res;
		if (predicateItem==null) return false;
		if (base!=null && base.size()>0)
		for ( Object item : base ) {
				GamaMap<String,Object> currentPredicate = (GamaMap<String,Object>) item;
				if ( isEqualPredicates(predicateItem, currentPredicate)&&((predicateItem.get(this.PREDICATE_VALUE).equals(SimpleBdiArchitecture.EVERY_VALUE))||(predicateItem.get(this.PREDICATE_VALUE).equals(currentPredicate.get(this.PREDICATE_VALUE)))) ) { return true; }
		}
		return false;		
	}

	public boolean removeFromBase(IScope scope,GamaMap predicateItem,String factBaseName)
	{
		final IAgent agent = getCurrentAgent(scope);
		GamaList<Object> factBase=this.getBase(scope, factBaseName);
		boolean res=this.removeFromBase(scope, predicateItem, factBase);
		agent.setAttribute(factBaseName, factBase);
		return true;
	}


	public boolean removeFromBase(IScope scope,GamaMap predicateItem,GamaList<Object> base)
	{
		boolean res;
		for ( Object item : base ) {
				GamaMap<String,Object> currentPredicate = (GamaMap<String,Object>) item;
				if ( isEqualPredicates(predicateItem, currentPredicate)&&((predicateItem.get(this.PREDICATE_VALUE).equals(SimpleBdiArchitecture.EVERY_VALUE))||(predicateItem.get(this.PREDICATE_VALUE).equals(currentPredicate.get(this.PREDICATE_VALUE)))) ) 
				{ base.remove(currentPredicate);
				return true;}
		}
		return false;		
	}

	public GamaMap<String,Object> getFromBase(IScope scope,GamaMap predicateItem,GamaList<Object> base)
	{
		boolean res;
		if (base!=null && base.size()>0)
		for ( Object item : base ) {
				GamaMap<String,Object> currentPredicate = (GamaMap<String,Object>) item;
				if ( isEqualPredicates(predicateItem, currentPredicate)&&((predicateItem.get(this.PREDICATE_VALUE).equals(SimpleBdiArchitecture.EVERY_VALUE))||(predicateItem.get(this.PREDICATE_VALUE).equals(currentPredicate.get(this.PREDICATE_VALUE)))) ) 
				{ return currentPredicate; }
		}
		return null;		
	}

	public GamaMap<String,Object> getFromBase(IScope scope,GamaMap predicateItem,String basename)
	{
		return this.getFromBase(scope, predicateItem, this.getBase(scope, basename));
		
	}

	public boolean addToBase(IScope scope,GamaMap predicateItem,String factBaseName)
	{
		final IAgent agent = getCurrentAgent(scope);
		GamaList<Object> factBase=this.getBase(scope, factBaseName);
		this.addToBase(scope, predicateItem, factBase);
		agent.setAttribute(factBaseName, factBase);
		return true;
	}

	public boolean addToBase(IScope scope,GamaMap predicateItem,GamaList<Object> factBase)
	{
		boolean res;
		boolean isExistPredicate;
		int perceiveTime = scope.getClock().getCycle();
		for ( Object item : factBase ) {
			try {
				GamaMap<String,Object> currentPredicate = (GamaMap<String,Object>) item;
				if ( isEqualPredicates(predicateItem, currentPredicate) ) {
					isExistPredicate = true;
	//				if ( currentPredicate.size() >= 4 ) {
						factBase.remove(currentPredicate);
//						currentPredicate.set(3, perceiveTime);
	//				}
					break;
				}
			} catch (Exception e1) {
				//System.out.println(perceiveTime + ": " + e1.getMessage());
			}
		}
//		if ( !isExistPredicate ) {
			if (!predicateItem.containsKey(this.PREDICATE_PARAMETERS)) predicateItem.put(this.PREDICATE_PARAMETERS,new GamaMap<String,Object>());
			predicateItem.put(this.PREDICATE_DATE,perceiveTime);
			factBase.add(predicateItem);
		return true;		
	}
	
	@action(name = "new_predicate",args = {
			@arg(name = PREDICATE_NAME, type = IType.STRING, optional = false, doc = @doc("predicate name")),
			@arg(name = PREDICATE_VALUE, type = IType.NONE, optional = true, doc = @doc("predicate value (you can't have two predicates with the same name/parameters and different values in your base)")),
			@arg(name = PREDICATE_PARAMETERS, type = IType.MAP, optional = true, doc = @doc("predicate parameters (you can have predicates with same name but different values in your base")),
			@arg(name = PREDICATE_PRIORITY, type = IType.FLOAT, optional = true, doc = @doc("desire priority value (default: 1.0)"))
			},
			doc = @doc(value = "returns the predicate (type map) with the specified parameters.", examples = {@example("") }))
	
//	@args(names = { PREDICATE_NAME, PREDICATE_PARAMETERS })
	public GamaMap<String,Object> primNewPred(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		String predicateName =
			scope.hasArg(PREDICATE_NAME) ? scope.getStringArg(PREDICATE_NAME) : (String) agent
				.getAttribute(PREDICATE_NAME);
			Object predicateValue =
					(scope.hasArg(PREDICATE_VALUE) ? scope.getArg(PREDICATE_VALUE,IType.NONE)
						: null);
		if (predicateValue==null) predicateValue=EVERY_VALUE;
		GamaMap<String,Object> predicateParameters =
			(GamaMap<String,Object>)(scope.hasArg(PREDICATE_PARAMETERS) ? scope.getArg(PREDICATE_PARAMETERS,IType.MAP)
				: (GamaMap<String,Object>) agent.getAttribute(PREDICATE_PARAMETERS));
		Double predicatePriority =
				(Double)(scope.hasArg(PREDICATE_PRIORITY) ? scope.getArg(PREDICATE_PRIORITY,IType.FLOAT)
					: agent.getAttribute(PREDICATE_PRIORITY));
		
		if ( predicateName == null || predicateName.length() == 0 ) { return null; }
		if (predicatePriority==null) predicatePriority=1.0;
		// System.out.println(predicateName);
		
		GamaList<Object> factBase =
			(GamaList<Object>) (scope.hasArg(DESIRE_BASE) ? scope.getListArg(DESIRE_BASE) : (GamaList<Object>) agent
				.getAttribute(DESIRE_BASE));
		GamaMap<String,Object> predicateItem = this.new_predicate(predicateName, predicateValue, predicateParameters);
		predicateItem.put(this.PREDICATE_PRIORITY, predicatePriority);
		return predicateItem;
	}


	@action(name = "add_belief",args = {
			@arg(name = PREDICATE, type = IType.MAP, optional = true, doc = @doc("predicate to check"))},
			doc = @doc(value = "check if the predicates is in the desire base.", returns = "true if it is in the base.", examples = {@example("") }))
	
//	@args(names = { PREDICATE_NAME, PREDICATE_PARAMETERS })
	public Boolean primAddBelief(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);

		GamaMap<String,Object> predicateDirect =
				(GamaMap<String,Object>)(scope.hasArg(PREDICATE) ? scope.getArg(PREDICATE,IType.MAP)
					: null);
			if ( predicateDirect != null )
			{ 
				this.addToBase(scope, predicateDirect, this.BELIEF_BASE);
				return true;
			}
			
		
		
		return false;

	}

	
	@action(name = "has_belief",args = {
			@arg(name = PREDICATE, type = IType.MAP, optional = true, doc = @doc("predicate to check"))},
			doc = @doc(value = "check if the predicates is in the belief base.", returns = "true if it is in the base.", examples = {@example("") }))
	//@args(names = { PREDICATE_NAME, PREDICATE_PARAMETERS })
	public Boolean primTestBelief(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);

		GamaMap<String,Object> predicateDirect =
				(GamaMap<String,Object>)(scope.hasArg(PREDICATE) ? scope.getArg(PREDICATE,IType.MAP)
					: null);
			if ( predicateDirect != null )
			{ 
				if (this.testInBase(scope, predicateDirect,this.getBase(scope, this.BELIEF_BASE)))
				{
			return true;
			
				}
				else
				{
					return false;
				}
			}
			
		
		
		return false;
	}

	@action(name = "get_belief",args = {
			@arg(name = PREDICATE, type = IType.MAP, optional = true, doc = @doc("predicate to check"))},
			doc = @doc(value = "get the predicates is in the belief base.", returns = "the predicate if it is in the base.", examples = {@example("") }))
	//@args(names = { PREDICATE_NAME, PREDICATE_PARAMETERS })
	public GamaMap<String,Object> getBelief(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);

		GamaMap<String,Object> predicateDirect =
				(GamaMap<String,Object>)(scope.hasArg(PREDICATE) ? scope.getArg(PREDICATE,IType.MAP)
					: null);
			if ( predicateDirect != null )
			{ 
				if (this.testInBase(scope, predicateDirect,this.getBase(scope, this.BELIEF_BASE)))
				{
			return this.getFromBase(scope, predicateDirect, BELIEF_BASE);
			
				}
				else
				{
					return null;
				}
			}
			
		
		
		return null;

	}

	@action(name = "is_current_goal",args = {
			@arg(name = PREDICATE, type = IType.MAP, optional = true, doc = @doc("predicate to check"))},
			doc = @doc(value = "check if the predicates is the current goal (last entry of intension base).", returns = "true if it is in the base.", examples = {@example("") }))
	public Boolean iscurrentGoal(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);

		GamaMap<String,Object> predicateDirect =
				(GamaMap<String,Object>)(scope.hasArg(PREDICATE) ? scope.getArg(PREDICATE,IType.MAP)
					: null);
		GamaMap<String,Object> currentGoal=this.currentGoal(scope);
			if (( predicateDirect != null )&&( currentGoal != null ))
			{ 
				if ((this.isEqualPredicates(predicateDirect, currentGoal)) &&((predicateDirect.get(PREDICATE_VALUE).equals(SimpleBdiArchitecture.EVERY_VALUE))||(predicateDirect.get(PREDICATE_VALUE).equals(currentGoal.get(PREDICATE_VALUE)))) ) 
				{
			return true;
			
				}
				else
				{
					return false;
				}
			}
			
		
		
		return false;
	}

	@action(name = "get_current_goal",
			doc = @doc(value = "returns the current goal (last entry of intension base).", returns = "true if it is in the base.", examples = {@example("") }))
	public GamaMap<String,Object> currentGoal(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		GamaList<Object> intensionBase = this.getBase(scope, INTENSION_BASE);
//				(GamaList<Object>) (scope.hasArg(INTENSION_BASE) ? scope.getListArg(INTENSION_BASE) : (GamaList<Object>) agent
//					.getAttribute(INTENSION_BASE));
//			java.lang.System.out.println("intensionbase: "+intensionBase);
//		int perceiveTime = scope.getClock().getCycle();
		if (intensionBase==null) return null;
		if (intensionBase.size()>0)
		{
			return (GamaMap<String,Object>) intensionBase.get(intensionBase.size()-1); 
			
		}
	
		return null;
	}

	
	
	@action(name = "has_desire",args = {
			@arg(name = PREDICATE, type = IType.MAP, optional = true, doc = @doc("predicate to check"))},
			doc = @doc(value = "check if the predicates is in the desire base.", returns = "true if it is in the base.", examples = {@example("") }))
	//@args(names = { PREDICATE_NAME, PREDICATE_PARAMETERS })
	public Boolean primTestDesire(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);

		GamaMap<String,Object> predicateDirect =
				(GamaMap<String,Object>)(scope.hasArg(PREDICATE) ? scope.getArg(PREDICATE,IType.MAP)
					: null);
			if ( predicateDirect != null )
			{ 
				if (this.testInBase(scope, predicateDirect,this.getBase(scope, this.DESIRE_BASE)))
				{
			return true;
			
				}
				else
				{
					return false;
				}
			}
			
		
		
		return false;
	}

	@action(name = "currentgoal_on_hold",args = {
//			@arg(name = PREDICATE, type = IType.MAP, optional = false, doc = @doc("predicate name")),
			@arg(name = PREDICATE_ONHOLD, type = IType.NONE, optional = true, doc = @doc("the specified intension is put on hold (fited plan are not considered) until specific condition is reached. Can be an expression (which will be tested), a list (of subgoals), or nil (by default the condition will be the current list of subgoals of the intension)"))},
			doc = @doc(value = "puts the current goal on hold until the specified condition is reached or all subgoals are reached (not in desire base anymore).", returns = "true if it is in the base.", examples = {@example("") }))
	
//	@args(names = { PREDICATE_NAME, PREDICATE_PARAMETERS })
	public Boolean primOnHoldIntension(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
//		GamaMap<String,Object> predicate =
//			(GamaMap<String,Object>)(scope.hasArg(PREDICATE) ? scope.getArg(PREDICATE,IType.MAP)
//				: null);
		GamaMap<String,Object> predicate=this.currentGoal(scope);
		Object until =
				(scope.hasArg(this.PREDICATE_ONHOLD) ? scope.getArg(PREDICATE_ONHOLD,IType.NONE)
					: null);
		if (until==null)
		{
			GamaList subgoal=(GamaList)(predicate.get(this.PREDICATE_SUBGOALS));
			if (subgoal!=null && subgoal.length(scope)>0)
			{
				predicate.put(this.PREDICATE_ONHOLD,subgoal);
//				this.addToBase(scope, (GamaMap)subgoal.get(0), this.INTENSION_BASE);
				
			}
		}
		else
			if ((until instanceof String)||(until instanceof String))
			{
				predicate.put(this.PREDICATE_ONHOLD,until);				
//				this.addToBase(scope, predicate, this.INTENSION_BASE);
			}
			else
				if (until instanceof GamaList)
				{
					predicate.put(this.PREDICATE_ONHOLD,"false");
//					this.addToBase(scope, predicate, this.INTENSION_BASE);
				}
		return true;
	}
	
	
	@action(name = "add_subgoal",args = {
			@arg(name = PREDICATE, type = IType.MAP, optional = false, doc = @doc("predicate name")),
			@arg(name = PREDICATE_SUBGOALS, type = IType.MAP, optional = false, doc = @doc("the subgoal to add to the predicate"))},
			doc = @doc(value = "adds the predicates is in the desire base.", returns = "true if it is in the base.", examples = {@example("") }))
	
	public Boolean addSubGoal(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		GamaMap<String,Object> predicate =
			(GamaMap<String,Object>)(scope.hasArg(PREDICATE) ? scope.getArg(PREDICATE,IType.MAP)
				: null);
		GamaMap<String,Object> subpredicate =
				(GamaMap<String,Object>)(scope.hasArg(PREDICATE_SUBGOALS) ? scope.getArg(PREDICATE_SUBGOALS,IType.MAP)
					: null);
		if ((predicate==null)||(subpredicate==null)) return false;

		Object subg=predicate.get(PREDICATE_SUBGOALS);
		if (subg==null)
		{
			GamaList<Object> subglist=new GamaList();
			subglist.add(subpredicate);
			predicate.put(PREDICATE_SUBGOALS, subglist);
			
		}
		else
		{
			GamaList<Object> subglist=(GamaList)subg;
			for (int i=subglist.size()-1; i>=0; i--)
			{
				if (this.isEqualPredicates(subpredicate, (GamaMap)subglist.get(i)))
				{
					subglist.remove(i);
				}
			}
			subglist.add(subpredicate);
			predicate.put(PREDICATE_SUBGOALS, subglist);				
		}
		
		return true;
	}
	
	
	
	@action(name = "add_desire",args = {
			@arg(name = PREDICATE, type = IType.MAP, optional = false, doc = @doc("predicate to add")),
			@arg(name = PREDICATE_TODO, type = IType.MAP, optional = true, doc = @doc("add the desire as a subgoal of this parameter"))},
			doc = @doc(value = "adds the predicates is in the desire base.", returns = "true if it is in the base.", examples = {@example("") }))
	
//	@args(names = { PREDICATE_NAME, PREDICATE_PARAMETERS })
	public Boolean primAddDesire(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);

		GamaMap<String,Object> predicateDirect =
				(GamaMap<String,Object>)(scope.hasArg(PREDICATE) ? scope.getArg(PREDICATE,IType.MAP)
					: null);
			if ( predicateDirect != null )
			{ 
				
				GamaMap<String,Object> superpredicate =
						(GamaMap<String,Object>)(scope.hasArg(PREDICATE_TODO) ? scope.getArg(PREDICATE_TODO,IType.MAP)
							: null);
				if (superpredicate!=null)
				{
					Object subg=superpredicate.get(PREDICATE_SUBGOALS);
					if (subg==null)
					{
						GamaList<Object> subglist=new GamaList();
						subglist.add(predicateDirect);
						superpredicate.put(PREDICATE_SUBGOALS, subglist);
						
					}
					else
					{
						GamaList<Object> subglist=(GamaList)subg;
						subglist.add(predicateDirect);
						superpredicate.put(PREDICATE_SUBGOALS, subglist);				
					}
					
					
				}


				this.addToBase(scope, predicateDirect, this.DESIRE_BASE);
				return true;
			}
			
		
		
		return false;
	}
	

	@action(name = "remove_belief",args = {
			@arg(name = PREDICATE, type = IType.MAP, optional = true, doc = @doc("predicate to add"))},
			doc = @doc(value = "removes the predicates from the belief base.", returns = "true if it is in the base.", examples = {@example("") }))

//	@args(names = { PREDICATE_NAME, PREDICATE_PARAMETERS })
	public Boolean primRemoveBelief(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);

		GamaMap<String,Object> predicateDirect =
				(GamaMap<String,Object>)(scope.hasArg(PREDICATE) ? scope.getArg(PREDICATE,IType.MAP)
					: null);
			if ( predicateDirect != null )
			{ 
				this.removeFromBase(scope, predicateDirect, this.BELIEF_BASE);
				return true;
			}

		return false;
	}

	@action(name = "remove_desire",args = {
			@arg(name = PREDICATE, type = IType.MAP, optional = true, doc = @doc("predicate to add"))},
			doc = @doc(value = "removes the predicates from the desire base.", returns = "true if it is in the base.", examples = {@example("") }))

//	@args(names = { PREDICATE_NAME, PREDICATE_PARAMETERS })
	public Boolean primRemoveDesire(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);

		GamaMap<String,Object> predicateDirect =
				(GamaMap<String,Object>)(scope.hasArg(PREDICATE) ? scope.getArg(PREDICATE,IType.MAP)
					: null);
			if ( predicateDirect != null )
			{ 
				this.removeFromBase(scope, predicateDirect, this.DESIRE_BASE);
				return true;
			}

		return false;
	}

	@action(name = "remove_intention",args = {
			@arg(name = PREDICATE, type = IType.MAP, optional = true, doc = @doc("predicate to add")),
			@arg(name = REMOVE_DESIRE_AND_INTENSION, type = IType.BOOL, optional = false, doc = @doc("removes also desire"))},
			doc = @doc(value = "removes the predicates from the desire base.", returns = "true if it is in the base.", examples = {@example("") }))

//	@args(names = { PREDICATE_NAME, PREDICATE_PARAMETERS })
	public Boolean primRemoveIntention(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);

		GamaMap<String,Object> predicateDirect =
				(GamaMap<String,Object>)(scope.hasArg(PREDICATE) ? scope.getArg(PREDICATE,IType.MAP)
					: null);
			if ( predicateDirect != null )
			{ 
				Boolean dodesire=scope.hasArg(REMOVE_DESIRE_AND_INTENSION) ? scope.getBoolArg(REMOVE_DESIRE_AND_INTENSION) : false;
				this.removeFromBase(scope, predicateDirect, this.INTENSION_BASE);
				if(dodesire)
					this.removeFromBase(scope, predicateDirect, this.DESIRE_BASE);
				return true;
			}

		return false;
	}

	@Override
	public boolean init(final IScope scope) throws GamaRuntimeException {
		super.init(scope);
		this._consideringScope = scope;
		return true;
	}

	@Override
	public void verifyBehaviors(final ISpecies context) {}

	@Override
	public void dispose() {}

	private boolean isEqualPredicates(final GamaMap<String,Object> predicate1, final GamaMap<String,Object> predicate2) {
			if (predicate1.isEmpty() || predicate2.isEmpty()) {
				return false;
			}
		if (!predicate1.get(this.PREDICATE_NAME).equals(predicate2.get(this.PREDICATE_NAME))) {return false;}
		if (predicate1.size() > 1 ) {
			if (predicate2.size() <= 1 ) {
				return false;
			}
			else
			{
				if ((predicate1.get(this.PREDICATE_PARAMETERS) instanceof GamaMap)&&(predicate1.get(this.PREDICATE_PARAMETERS) instanceof GamaMap))
				{
					for (Object key:((GamaMap)predicate1.get(this.PREDICATE_PARAMETERS)).getKeys())
					{
						if (!(((GamaMap)predicate1.get(this.PREDICATE_PARAMETERS)).get(key).equals(((GamaMap)predicate2.get(this.PREDICATE_PARAMETERS)).get(key))))
								{
//							java.lang.System.out.println("pred diff: "+((GamaMap)predicate1.get(PREDICATE_PARAMETERS)).get(key)+ "/"+((GamaMap)predicate1.get(PREDICATE_VALUE)).get(key));
							return false;
								}
					}
				}
			}
//			if (!predicate1.get(PREDICATE_VALUE).equals(predicate2.get(PREDICATE_VALUE))) {return false;}
			
		} else if (predicate2.size() > 1 ) return false;
		return true;
	}
/*	private boolean isEqualPredicates(final GamaList<Object> predicate1, final GamaList<Object> predicate2) {
		if ( predicate1.size() < 2 || predicate2.size() < 0 ) { return false; }
		Object currentElement;
		currentElement = predicate1.get(PREDICATE_NAME);
		if ( !(currentElement instanceof String) ) { return false; }
		String name1 = (String) currentElement;
		currentElement = predicate2.get(PREDICATE_NAME);
		if ( !(currentElement instanceof String) ) { return false; }
		String name2 = (String) currentElement;
		if ( !name1.equals(name2) ) { return false; }

		currentElement = predicate1.get(PREDICATE_VALUE);
		if ( !(currentElement instanceof GamaList<?>) ) { return false; }
		GamaList<?> parameter1 = (GamaList<?>) currentElement;
		currentElement = predicate2.get(PREDICATE_VALUE);
		if ( !(currentElement instanceof GamaList<?>) ) { return false; }
		GamaList<?> parameter2 = (GamaList<?>) currentElement;
		if ( parameter1.size() != parameter2.size() ) { return false; }
		for ( int index = 0; index < parameter1.size(); index++ ) {
			Object childElement1 = parameter1.get(index);
			Object childElement2 = parameter2.get(index);
			if ( childElement1 instanceof String ) {
				if ( !(childElement2 instanceof String) ) { return false; }
				String value1 = (String) childElement1;
				String value2 = (String) childElement2;
				if ( !value1.equals(value2) ) { return false; }
			} else if ( childElement1 instanceof Double ) {
				if ( !(childElement2 instanceof Double) ) { return false; }
				Double value1 = (Double) childElement1;
				Double value2 = (Double) childElement2;
				if ( msi.gaml.operators.Maths.abs(value1 - value2) > EPSILON ) { return false; }
			} else if ( childElement1 instanceof Integer ) {
				if ( !(childElement2 instanceof Integer) ) { return false; }
				Integer value1 = (Integer) childElement1;
				Integer value2 = (Integer) childElement2;
				if ( value1 - value2 > 0 ) { return false; }
			} else {
				return false;
			}
		}
		return true;
	}*/

	@operator(value = "new_predicate", category={IOperatorCategory.MAP})
	@doc(value = "returns the predicate with the given name, value and parameters", 
		examples = { @example(value="GamaList mypredicate<-new_predicate(PredName,PredValue,[par1::val1])") , 
		 @example(value="GamaList OnAB<-new_predicate(\"On\",true,[\"sub\"::blocka,\"on\"::blockb])") }, 
		see = {"simple_bdi" })
	public static GamaMap<String,Object> new_predicate(String name,Object value, GamaMap<String,Object> params)
	{
		GamaMap res=new GamaMap<String,Object>();
		res.put(PREDICATE_NAME,name);
		res.put(PREDICATE_VALUE, value);
//		if (value==null)
//			res.put(PREDICATE_VALUE, EVERY_VALUE);
		if (params==null || params.size()==0)
			res.put(PREDICATE_PARAMETERS,new GamaMap<String,Object>());
		else
		if (params.size()>0)
			res.put(PREDICATE_PARAMETERS,params);
		if (!res.containsKey(PREDICATE_ONHOLD))
		res.put(PREDICATE_ONHOLD, "true");
		if (!res.containsKey(PREDICATE_SUBGOALS))
		res.put(PREDICATE_SUBGOALS, new GamaList());
		if (!res.containsKey(PREDICATE_TODO))
		res.put(PREDICATE_TODO, new GamaList());
		return res;
	}

}
