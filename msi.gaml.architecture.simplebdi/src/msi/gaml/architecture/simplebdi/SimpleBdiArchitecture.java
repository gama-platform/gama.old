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
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.architecture.reflex.ReflexArchitecture;
import msi.gaml.compilation.ISymbol;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.IStatement;
import msi.gaml.types.*;

@vars({ @var(name = SimpleBdiArchitecture.PERSISTENCE_COEFFICIENT_PLANS, type = IType.FLOAT, init = "1.0", doc= @doc ("plan persistence")),
	@var(name = SimpleBdiArchitecture.PERSISTENCE_COEFFICIENT_INTENTIONS, type = IType.FLOAT, init = "1.0", doc= @doc ("intention persistence")),
	@var(name = SimpleBdiArchitecture.PROBABILISTIC_CHOICE, type = IType.BOOL, init = "true"),
	@var(name = SimpleBdiArchitecture.BELIEF_BASE, type = IType.LIST, of = PredicateType.id, init = "[]"),
	@var(name = SimpleBdiArchitecture.LAST_THOUGHTS, type = IType.LIST, init = "[]"),
	@var(name = SimpleBdiArchitecture.INTENTION_BASE, type = IType.LIST, of = PredicateType.id, init = "[]"),
	@var(name = SimpleBdiArchitecture.EMOTION_BASE, type = IType.LIST, of = PredicateType.id, init = "[]"),
	@var(name = SimpleBdiArchitecture.DESIRE_BASE, type = IType.LIST, of = PredicateType.id, init = "[]"),
	@var(name = SimpleBdiArchitecture.PLAN_BASE, type = IType.LIST, of = BDIPlanType.id, init = "[]"),
	@var(name = SimpleBdiArchitecture.CURRENT_PLAN, type = IType.NONE)})
@skill(name = SimpleBdiArchitecture.SIMPLE_BDI)

public class SimpleBdiArchitecture extends ReflexArchitecture {

	public static final String SIMPLE_BDI = "simple_bdi";
	public static final String PLAN = "plan";
	public static final String PRIORITY = "priority";
	public static final String FINISHEDWHEN = "finished_when";
	public static final String PERSISTENCE_COEFFICIENT_PLANS = "plan_persistence";
	public static final String PERSISTENCE_COEFFICIENT_INTENTIONS = "intention_persistence";	
	
	//TODO: Not implemented yet
	public static final String PROBABILISTIC_CHOICE = "probabilistic_choice";
	public static final String INSTANTANEAOUS = "instantaneous";

	//INFORMATION THAT CAN BE DISPLAYED
	public static final String LAST_THOUGHTS = "thinking";
	public static final Integer LAST_THOUGHTS_SIZE = 5;
	
	
	public static final String PREDICATE = "predicate";
	public static final String PREDICATE_NAME = "name";
	public static final String PREDICATE_VALUE = "value";
	public static final String PREDICATE_PRIORITY = "priority";
	public static final String PREDICATE_PARAMETERS = "parameters";
	public static final String PREDICATE_ONHOLD = "on_hold_until";
	public static final String PREDICATE_TODO = "todo";
	public static final String PREDICATE_SUBINTENTIONS = "subintentions";
	public static final String PREDICATE_DATE = "date";
	public static final String BELIEF_BASE = "belief_base";
	public static final String REMOVE_DESIRE_AND_INTENTION = "desire_also";
	public static final String DESIRE_BASE = "desire_base";
	public static final String INTENTION_BASE = "intention_base";
	public static final String EMOTION_BASE = "emotion_base";
	public static final String EVERY_VALUE = "every_possible_value";
	public static final String PLAN_BASE = "plan_base";
	public static final String CURRENT_PLAN = "current_plan";

	private IScope _consideringScope;
//	private final List<SimpleBdiPlanStatement> _plans = new ArrayList<SimpleBdiPlanStatement>();
	private final List<PerceiveStatement> _perceptions = new ArrayList<PerceiveStatement>();
	private final List<BDIPlan> _plans = new ArrayList<BDIPlan>();
	private int _plansNumber = 0;
	private int _perceptionNumber = 0;
	private boolean iscurrentplaninstantaneous=false;

	@Override
	public void setChildren(final List<? extends ISymbol> children) {
		clearBehaviors();
		for ( ISymbol c : children ) {
			addBehavior((IStatement) c);
		}
	}

	@Override
	public void addBehavior(final IStatement c) {
		if ( c instanceof SimpleBdiPlanStatement ) {
			String statementKeyword = c.getFacet("keyword").value(_consideringScope).toString();
			_plans.add(new BDIPlan((SimpleBdiPlanStatement) c));
			_plansNumber++;
		} else if(c instanceof PerceiveStatement){
			String statementKeyword = c.getFacet("keyword").value(_consideringScope).toString();
			_perceptions.add((PerceiveStatement)c);
			_perceptionNumber++;
		} else{
			super.addBehavior(c);
		}
	}

	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		super.executeOn(scope);
		if ( _perceptionNumber > 0 ) {
			for ( int i = 0; i < _perceptionNumber; i++ ) {
				_perceptions.get(i).executeOn(scope);
			}
		}
		return executePlans(scope);
	}

	protected final Object executePlans(final IScope scope) {
		Object result = null;
//		if ( _perceiveNumber > 0 ) {
//			for ( int i = 0; i < _perceiveNumber; i++ ) {
//				result = _perceives.get(i).executeOn(scope);
//			}
//		}
		if ( _plansNumber > 0 ) {
			boolean loop_instantaneous_plans=true;
			while (loop_instantaneous_plans)
			{
				loop_instantaneous_plans=false;
			final IAgent agent = getCurrentAgent(scope);
			GamaList<Predicate> desireBase =
				(GamaList<Predicate>) (scope.hasArg(DESIRE_BASE) ? scope.getListArg(DESIRE_BASE)
					: (GamaList<Predicate>) agent.getAttribute(DESIRE_BASE));
			GamaList<Predicate> intentionBase =
				(GamaList<Predicate>) (scope.hasArg(INTENTION_BASE) ? scope.getListArg(INTENTION_BASE)
					: (GamaList<Predicate>) agent.getAttribute(INTENTION_BASE));
			Double persistenceCoefficientPlans =
				scope.hasArg(PERSISTENCE_COEFFICIENT_PLANS) ? scope.getFloatArg(PERSISTENCE_COEFFICIENT_PLANS) : (Double) agent
					.getAttribute(PERSISTENCE_COEFFICIENT_PLANS);
			Double persistenceCoefficientintention =
				scope.hasArg(PERSISTENCE_COEFFICIENT_INTENTIONS) ? scope.getFloatArg(PERSISTENCE_COEFFICIENT_INTENTIONS)
					: (Double) agent.getAttribute(PERSISTENCE_COEFFICIENT_INTENTIONS);

			SimpleBdiPlanStatement _persistentTask = (SimpleBdiPlanStatement)agent.getAttribute(CURRENT_PLAN);
				
			// RANDOMLY REMOVE (last)INTENTION
			Boolean flipResultintention = msi.gaml.operators.Random.opFlip(scope, persistenceCoefficientintention);
			while (!flipResultintention && (intentionBase.size() > 0)) {
				flipResultintention = msi.gaml.operators.Random.opFlip(scope,
						persistenceCoefficientintention);
				if (intentionBase.size() > 0) {
					int toremove=intentionBase.size()-1;
					Predicate previousint = intentionBase.get(toremove);
					intentionBase.remove(toremove);
					String think="check what happens if I remove: "
							+ previousint;					
					addThoughts(scope, think);
					_persistentTask = null;
					agent.setAttribute(CURRENT_PLAN, _persistentTask);
				}
			}

			// If current intention has no plan or is on hold, choose a new
			// Desire
//			System.out.println("_persistentTask 0 " +  _persistentTask);
			if ( testOnHold(scope, currentIntention(scope)) || selectExecutablePlanWithHighestPriority(scope) == null ) {
				selectDesireWithHighestPriority(scope);
				_persistentTask = null;
				agent.setAttribute(CURRENT_PLAN, _persistentTask);

			}
			Boolean flipResult = msi.gaml.operators.Random.opFlip(scope, persistenceCoefficientPlans);

			if ( !flipResult ) {
				if ( _persistentTask != null ) {
					addThoughts(scope, "check what happens if I stop: " + _persistentTask.getName());
				}
				_persistentTask = selectExecutablePlanWithHighestPriority(scope);
				agent.setAttribute(CURRENT_PLAN, _persistentTask);

				if ( _persistentTask != null ) {
					addThoughts(scope, "lets do instead " + _persistentTask.getName());
				}

			}

			// choose a plan for the current intention
//			System.out.println("_persistentTask 1 " +  _persistentTask);
			if ( _persistentTask == null && currentIntention(scope) == null ) {
				selectDesireWithHighestPriority(scope);
				if ( currentIntention(scope) == null ) {
					addThoughts(scope, "I want nothing...");
					return null;

				}
//				System.out.println("_persistentTask 2 " +  _persistentTask);
				_persistentTask = selectExecutablePlanWithHighestPriority(scope);
				agent.setAttribute(CURRENT_PLAN, _persistentTask);
				addThoughts(scope, "ok, new intention: " + currentIntention(scope) + " with plan " + _persistentTask.getName());
			}
			if ( (_persistentTask) == null && currentIntention(scope) != null ) {
//				System.out.println("_persistentTask 3 " +  _persistentTask);
				_persistentTask = selectExecutablePlanWithHighestPriority(scope);
				agent.setAttribute(CURRENT_PLAN, _persistentTask);
				if ( _persistentTask != null ) {
//					System.out.println("_persistentTask 4 " +  _persistentTask);
					addThoughts(scope, "use plan : " + _persistentTask.getName());
				}
			}
			if ( _persistentTask != null ) {
				if ( !agent.dead() ) {
//					System.out.println("_persistentTask 5 " +  _persistentTask);
					result = _persistentTask.executeOn(scope);
					boolean isExecuted = false;
					if(_persistentTask.getExecutedExpression() != null){
							isExecuted = msi.gaml.operators.Cast.asBool(scope, _persistentTask.getExecutedExpression().value(scope));
					}
					if (this.iscurrentplaninstantaneous)
					{
						loop_instantaneous_plans=true;
					}
//					System.out.println("isExecuted: " +  isExecuted);
					if ( isExecuted ) {
						_persistentTask = null;
						agent.setAttribute(CURRENT_PLAN, _persistentTask);

					}				
				}
			}
			
		}
		}

		return result;
	}

	protected final Boolean selectDesireWithHighestPriority(final IScope scope) {
		final IAgent agent = getCurrentAgent(scope);
		Boolean is_probabilistic_choice = scope.hasArg(PROBABILISTIC_CHOICE) ? 
				scope.getBoolArg(PROBABILISTIC_CHOICE) : (Boolean) agent.getAttribute(PROBABILISTIC_CHOICE) ;
				
		if(is_probabilistic_choice){
			GamaList<Predicate> desireBase = getBase(scope, DESIRE_BASE);
			GamaList<Predicate> intentionBase = getBase(scope, INTENTION_BASE);
			if(desireBase.size()>0){
				Predicate newIntention = desireBase.anyValue(scope);
				double priority_list[] = new double[desireBase.length(scope)];
				for (int i=0; i<desireBase.length(scope);i++){
					priority_list[i]= desireBase.get(i).priority;
				}
				IList priorities = GamaListFactory.create(scope, Types.FLOAT, priority_list);
				int index_choice = msi.gaml.operators.Random.opRndChoice(scope, priorities);
				newIntention=desireBase.get(index_choice);
				while(intentionBase.contains(newIntention)){
					int index_choice2 = msi.gaml.operators.Random.opRndChoice(scope, priorities);
					newIntention=desireBase.get(index_choice2);
				}
				if(newIntention.getSubintentions() == null){
					if ( !intentionBase.contains(newIntention) ) {
						intentionBase.addValue(scope, newIntention);
						return true;
					}
				}
				else{
					for(int i=0; i<newIntention.getSubintentions().size();i++ ){
						if(!desireBase.contains(newIntention.getSubintentions().get(i))){
							desireBase.addValue(scope, newIntention.getSubintentions().get(i));
						}
					}
					newIntention.setOnHoldUntil(newIntention.getSubintentions());
					if (!intentionBase.contains(newIntention)){
						intentionBase.addValue(scope, newIntention);
						return true;
					}
				}
			}
		}
		else{
			GamaList<Predicate> desireBase =
				(GamaList<Predicate>) scope.getExperiment().getRandomGenerator().shuffle(getBase(scope, DESIRE_BASE));
			GamaList<Predicate> intentionBase = getBase(scope, INTENTION_BASE);
			double maxpriority = Double.NEGATIVE_INFINITY;
			if ( desireBase.size() > 0 && intentionBase != null ) {
				Predicate newIntention = desireBase.anyValue(scope);
				for ( Predicate desire : desireBase ) {
					if ( desire.priority > maxpriority ) {
						if ( !intentionBase.contains(desire) ) {
							maxpriority = desire.priority;
							newIntention = desire;
						}
					}
				}
				if(newIntention.getSubintentions()==null){
					if ( !intentionBase.contains(newIntention) ) {
						intentionBase.addValue(scope, newIntention);
						return true;
					}
				}else{
					for(int i=0; i<newIntention.getSubintentions().size();i++ ){
						if(!desireBase.contains(newIntention.getSubintentions().get(i))){
							desireBase.addValue(scope, newIntention.getSubintentions().get(i));
						}
					}
					newIntention.setOnHoldUntil(newIntention.getSubintentions());
					if (!intentionBase.contains(newIntention)){
						intentionBase.addValue(scope, newIntention);
						return true;
					}
				}
			}
		}
		return false;
	}

	protected final SimpleBdiPlanStatement selectExecutablePlanWithHighestPriority(final IScope scope) {
		final IAgent agent = getCurrentAgent(scope);
		Boolean is_probabilistic_choice = scope.hasArg(PROBABILISTIC_CHOICE) ? 
				scope.getBoolArg(PROBABILISTIC_CHOICE) : (Boolean) agent.getAttribute(PROBABILISTIC_CHOICE) ;
				
		SimpleBdiPlanStatement resultStatement = null;
		
			double highestPriority = Double.MIN_VALUE;
			List<SimpleBdiPlanStatement> temp_plan = new ArrayList<SimpleBdiPlanStatement>();
			IList priorities = GamaListFactory.create(Types.FLOAT);
//			System.out.println("intention: " + getBase(scope, SimpleBdiArchitecture.INTENTION_BASE));
			for ( Object BDIPlanstatement : scope.getExperiment().getRandomGenerator().shuffle(_plans) ) {
				SimpleBdiPlanStatement statement = ((BDIPlan)BDIPlanstatement).getPlanStatement();
//				System.out.println("statement: " + statement);
//				System.out.println("((SimpleBdiPlan) statement).getContextExpression(): " + ((SimpleBdiPlan) statement).getContextExpression());
				boolean isContextConditionSatisfied =
					((SimpleBdiPlanStatement) statement).getContextExpression() == null ||
						msi.gaml.operators.Cast.asBool(scope, ((SimpleBdiPlanStatement) statement).getContextExpression()
							.value(scope));
//				System.out.println("isContextConditionSatisfied: " + isContextConditionSatisfied);
				boolean isIntentionConditionSatisfied = 
						((SimpleBdiPlanStatement) statement).getIntentionExpression() == null ||
						((Predicate)((SimpleBdiPlanStatement) statement).getIntentionExpression().value(scope)).equals(currentIntention(scope));
				if ( isContextConditionSatisfied && isIntentionConditionSatisfied) {
//					System.out.println("is_probabilistic_choice: " + is_probabilistic_choice);
					if(is_probabilistic_choice){
//						System.out.println("(SimpleBdiPlan) statement: " +  statement);
						
						temp_plan.add((SimpleBdiPlanStatement) statement);
//					System.out.println("temp_plan: " +  temp_plan);
						
					}
					else{
						double currentPriority =1.0;
						if(((SimpleBdiPlanStatement) statement).getFacet(SimpleBdiArchitecture.PRIORITY)!=null){
							 currentPriority = msi.gaml.operators.Cast.asFloat(scope, ((SimpleBdiPlanStatement) statement).getPriorityExpression()
									.value(scope));
						}
		
						if ( highestPriority < currentPriority ) {
							highestPriority = currentPriority;
							resultStatement = (SimpleBdiPlanStatement) statement;
						}
					}
				}
			}
			if(is_probabilistic_choice){
				if (! temp_plan.isEmpty()) {
					for(Object statement : temp_plan){
						if(((SimpleBdiPlanStatement) statement).hasFacet(PRIORITY)){
							priorities.add(msi.gaml.operators.Cast.asFloat(scope,((SimpleBdiPlanStatement) statement).getPriorityExpression().value(scope)));
						}
						else{
							priorities.add(1.0);
						}
					}
					int index_plan = msi.gaml.operators.Random.opRndChoice(scope, priorities);
					resultStatement = temp_plan.get(index_plan);
//					System.out.println("resultStatement: " +  resultStatement);
				}
			}

		iscurrentplaninstantaneous=false;
		if (resultStatement!=null)
		if(((SimpleBdiPlanStatement) resultStatement).getFacet(SimpleBdiArchitecture.INSTANTANEAOUS)!=null){
			iscurrentplaninstantaneous = msi.gaml.operators.Cast.asBool(scope, ((SimpleBdiPlanStatement) resultStatement).getInstantaneousExpression()
					.value(scope));
		}

		return resultStatement;
	}

	public GamaList<String> getThoughts(final IScope scope) {
		final IAgent agent = getCurrentAgent(scope);
		GamaList<String> thoughts = (GamaList<String>) agent.getAttribute(LAST_THOUGHTS);
		return thoughts;
	}

	public IList<String> addThoughts(final IScope scope, final String think) {
		final IAgent agent = getCurrentAgent(scope);
		GamaList<String> thoughts = (GamaList<String>) agent.getAttribute(LAST_THOUGHTS);
		IList newthoughts = GamaListFactory.create(Types.STRING);
		newthoughts.add(think);
		if ( thoughts != null && thoughts.size() > 0 ) {
			newthoughts.addAll(thoughts.subList(0, Math.min(LAST_THOUGHTS_SIZE - 1, thoughts.size())));
		}
		agent.setAttribute(LAST_THOUGHTS, newthoughts);
		return newthoughts;
	}

	public boolean testOnHold(final IScope scope, final Predicate intention) {
		if ( intention == null ) { return false; }
		if ( intention.onHoldUntil == null ) { return false; }
		if (intention.getValues()!=null){
			if (intention.getValues().containsKey("and")){
	//			System.out.println("intention : "+ intention);
				Object cond = intention.onHoldUntil;
	//			System.out.println("onHoldUntil : "+ intention.onHoldUntil);
	//			System.out.println("size : "+ ((ArrayList)cond).size());
				if(cond instanceof ArrayList){
	//				System.out.println("size : "+ ((ArrayList)cond).size());
					if(((ArrayList)cond).size()==0){
						GamaList desbase = getBase(scope, DESIRE_BASE);
						GamaList intentionbase = getBase(scope, INTENTION_BASE);
						desbase.remove(intention);
						intentionbase.remove(intention);
						for(Object statement : getBase(scope, SimpleBdiArchitecture.INTENTION_BASE)){
							if(((Predicate)statement).getSubintentions()!=null){
								if(((Predicate)statement).getSubintentions().contains(intention)){
									((Predicate)statement).getSubintentions().remove(intention);
								}
							}
							if(((ArrayList)((Predicate)statement).getOnHoldUntil())!=null){
								if(((ArrayList)((Predicate)statement).getOnHoldUntil()).contains(intention)){
									((ArrayList)((Predicate)statement).getOnHoldUntil()).remove(intention);
								}
							}
						}
						return false;
					}
					else{
						return true;
					}
				}
			}
			if (intention.getValues().containsKey("or")){
	//			System.out.println("intention : "+ intention);
				Object cond = intention.onHoldUntil;
	//			System.out.println("onHoldUntil : "+ intention.onHoldUntil);
	//			System.out.println("size : "+ ((ArrayList)cond).size());
				if(cond instanceof ArrayList){
					if(((ArrayList)cond).size()<=1){
						System.out.println("size : "+ ((ArrayList)cond).size());
						GamaList desbase = getBase(scope, DESIRE_BASE);
						GamaList intentionbase = getBase(scope, INTENTION_BASE);
						desbase.remove(intention);
						intentionbase.remove(intention);
						if(((ArrayList)cond).size()==1){
							if(desbase.contains(((ArrayList)cond).get(0))){
								desbase.remove(((ArrayList)cond).get(0));
							}
							for(Object statement : getBase(scope, SimpleBdiArchitecture.INTENTION_BASE)){
								if(((Predicate)statement).getSubintentions()!=null){
									if(((Predicate)statement).getSubintentions().contains(intention)){
										((Predicate)statement).getSubintentions().remove(intention);
									}
								}
								if(((ArrayList)((Predicate)statement).getOnHoldUntil())!=null){
									if(((ArrayList)((Predicate)statement).getOnHoldUntil()).contains(intention)){
										((ArrayList)((Predicate)statement).getOnHoldUntil()).remove(intention);
									}
								}
							}
						}
						return false;
					}
					else{
						return true;
					}
				}
			}
		}
		Object cond = intention.onHoldUntil;
		if ( cond instanceof ArrayList ) {
			GamaList desbase = getBase(scope, DESIRE_BASE);
			if ( desbase.isEmpty() ) { return false; }
			for ( Object subintention : (ArrayList) cond ) {
				if ( desbase.contains(subintention) ) {
					return true; 
					}
			}
			addThoughts(scope, "no more subintention for" + intention);
//			return false;
			/*Must return true the step it lost it's last subintention to reinitilizate the current plan*/
			return true;
		}
//		if( cond instanceof Predicate){
//			GamaList desbase = getBase(scope, DESIRE_BASE);
//			if ( desbase.isEmpty() ) { return false; }
//			if ( desbase.contains(cond) ) {
//				return true; 
//				}
//		}
		if ( cond instanceof String ) {
			Object res = msi.gaml.operators.System.opEvalGaml(scope, (String) cond);
			if ( Cast.asBool(scope, res) == false ) { return true; }

		}
		return false;

	}
	
	@action(name = "get_plans", 
			doc = @doc(value = "get the list of plans.",
			returns = "the list of BDI plans.",
			examples = { @example("get_plans()") }))
	public List<BDIPlan> getPlans(final IScope scope) {
		if (_plans.size()>0)
		{
			return _plans;
		}
		return null;
	}


	public GamaList<Predicate> getBase(final IScope scope, final String basename) {
		final IAgent agent = getCurrentAgent(scope);
		return (GamaList<Predicate>) (scope.hasArg(basename) ? scope.getListArg(basename) : (GamaList<Predicate>) agent
			.getAttribute(basename));
	}

	public boolean removeFromBase(final IScope scope, final Predicate predicateItem, final String factBaseName) {
		GamaList<Predicate> factBase = getBase(scope, factBaseName);
		return factBase.remove(predicateItem);
	}

	public boolean addToBase(final IScope scope, final Predicate predicateItem, final String factBaseName) {
		return addToBase(scope, predicateItem, getBase(scope, factBaseName));
	}

	public boolean addToBase(final IScope scope, final Predicate predicateItem, final GamaList<Predicate> factBase) {
		factBase.remove(predicateItem);

		predicateItem.setDate(scope.getClock().getTime());
		return factBase.add(predicateItem);
	}

//	@getter (value = CURRENT_PLAN)
//	public String getPlan(IScope scope){
//		return ((SimpleBdiPlan)(getCurrentAgent(scope).getAttribute(CURRENT_PLAN))).getName();
//	}
	
	private Boolean addBelief(final IScope scope, final Predicate predicateDirect){
		if ( predicateDirect != null ) { 
			if(getBase(scope, SimpleBdiArchitecture.INTENTION_BASE).contains(predicateDirect)){
				removeFromBase(scope, predicateDirect, DESIRE_BASE);
				removeFromBase(scope, predicateDirect, INTENTION_BASE);
			}
			for(Object statement : getBase(scope, SimpleBdiArchitecture.INTENTION_BASE)){
				if(((Predicate)statement).getSubintentions()!=null){
					if(((Predicate)statement).getSubintentions().contains(predicateDirect)){
						((Predicate)statement).getSubintentions().remove(predicateDirect);
					}
				}
				if(((ArrayList)(((Predicate)statement).getOnHoldUntil()))!=null){
					if(((ArrayList)(((Predicate)statement).getOnHoldUntil())).contains(predicateDirect)){
						((ArrayList)(((Predicate)statement).getOnHoldUntil())).remove(predicateDirect);
					}
				}
			}
			return addToBase(scope, predicateDirect, BELIEF_BASE); 
		}

		return false;
	}
	
	@action(name = "add_belief", args = { @arg(name = PREDICATE,
		type = IType.MAP,
		optional = true,
		doc = @doc("predicate to check")) }, doc = @doc(value = "check if the predicates is in the desire base.",
		returns = "true if it is in the base.",
		examples = { @example("") }))
	// @args(names = { PREDICATE_NAME, PREDICATE_PARAMETERS })
		public
		Boolean primAddBelief(final IScope scope) throws GamaRuntimeException {
		Predicate predicateDirect =
			(Predicate) (scope.hasArg(PREDICATE) ? scope.getArg(PREDICATE, PredicateType.id) : null);
		return addBelief(scope,predicateDirect);

	}

	@action(name = "has_belief", args = { @arg(name = PREDICATE,
		type = PredicateType.id,
		optional = true,
		doc = @doc("predicate to check")) }, doc = @doc(value = "check if the predicates is in the belief base.",
		returns = "true if it is in the base.",
		examples = { @example("") }))
	// @args(names = { PREDICATE_NAME, PREDICATE_PARAMETERS })
		public
		Boolean primTestBelief(final IScope scope) throws GamaRuntimeException {
		Predicate predicateDirect =
			(Predicate) (scope.hasArg(PREDICATE) ? scope.getArg(PREDICATE, PredicateType.id) : null);
		if ( predicateDirect != null ) { return getBase(scope, BELIEF_BASE).contains(predicateDirect);

		}
		return false;
	}

	@action(name = "get_belief", args = { @arg(name = PREDICATE,
		type = PredicateType.id,
		optional = false,
		doc = @doc("predicate to check")) }, doc = @doc(value = "get the predicates is in the belief base (if several, returns the first one).",
		returns = "the predicate if it is in the base.",
		examples = { @example("get_belief(new_predicate(\"has_water\", true))") }))
	// @args(names = { PREDICATE_NAME, PREDICATE_PARAMETERS })
	public Predicate getBelief(final IScope scope) throws GamaRuntimeException {
		Predicate predicateDirect =
			(Predicate) (scope.hasArg(PREDICATE) ? scope.getArg(PREDICATE, PredicateType.id) : null);
		if ( predicateDirect != null ) {
			for ( Predicate pred : getBase(scope, BELIEF_BASE) ) {
				if ( predicateDirect.equals(pred) ) { return pred; }
			}

		}
		return null;

	}
	
	@action(name = "get_belief_with_name", args = { @arg(name = "name",
			type = IType.STRING,
			optional = false,
			doc = @doc("name of the predicate to check")) }, doc = @doc(value = "get the predicates is in the belief base (if several, returns the first one).",
			returns = "the predicate if it is in the base.",
			examples = { @example("get_belief(\"has_water\")") }))
		public Predicate getBeliefName(final IScope scope) throws GamaRuntimeException {
			String predicateName =
				(String) (scope.hasArg("name") ? scope.getArg("name", IType.STRING) : null);
			if ( predicateName != null ) {
				for ( Predicate pred : getBase(scope, BELIEF_BASE) ) {
					if ( predicateName.equals(pred.getName())) { return pred; }
				}
			}
			return null;
		}
	
	@action(name = "get_beliefs_with_name", args = { @arg(name = "name",
			type = IType.STRING,
			optional = false,
			doc = @doc("name of the predicates to check")) }, doc = @doc(value = "get the list of predicates is in the belief base with the given name.",
			returns = "the list of predicates.",
			examples = { @example("get_belief(\"has_water\")") }))
		public List<Predicate> getBeliefsName(final IScope scope) throws GamaRuntimeException {
			String predicateName =
				(String) (scope.hasArg("name") ? scope.getArg("name", IType.STRING) : null);
			List<Predicate> predicates = GamaListFactory.create();
			if ( predicateName != null ) {
				for ( Predicate pred : getBase(scope, BELIEF_BASE) ) {
					if ( predicateName.equals(pred.getName())) { predicates.add(pred); }
				}
			}
			return predicates;
		}

	@action(name = "get_beliefs", args = { @arg(name = PREDICATE,
			type = PredicateType.id,
			optional = false,
			doc = @doc("name of the predicates to check")) }, doc = @doc(value = "get the list of predicates is in the belief base",
			returns = "the list of predicates.",
			examples = { @example("get_belief(\"has_water\")") }))
		public List<Predicate> getBeliefs(final IScope scope) throws GamaRuntimeException {
		Predicate predicateDirect =
				(Predicate) (scope.hasArg(PREDICATE) ? scope.getArg(PREDICATE, PredicateType.id) : null);
			List<Predicate> predicates = GamaListFactory.create();
			if ( predicateDirect != null ) {
				for ( Predicate pred : getBase(scope, BELIEF_BASE) ) {
					if ( predicateDirect.equals(pred)) { predicates.add(pred); }
				}
			}
			return predicates;
		}

	
	@action(name = "is_current_intention",
		args = { @arg(name = PREDICATE, type = PredicateType.id, optional = false, doc = @doc("predicate to check")) },
		doc = @doc(value = "check if the predicates is the current intention (last entry of intention base).",
			returns = "true if it is in the base.",
			examples = { @example("") }))
	public Boolean iscurrentIntention(final IScope scope) throws GamaRuntimeException {
		Predicate predicateDirect =
			(Predicate) (scope.hasArg(PREDICATE) ? scope.getArg(PREDICATE, PredicateType.id) : null);
		Predicate currentIntention = currentIntention(scope);

		if ( predicateDirect != null && currentIntention != null ) { return predicateDirect.equals(currentIntention); }

		return false;
	}

	@action(name = "get_current_intention", doc = @doc(value = "returns the current intention (last entry of intention base).",
		returns = "true if it is in the base.",
		examples = { @example("") }))
	public Predicate currentIntention(final IScope scope) throws GamaRuntimeException {
		GamaList<Predicate> intentionBase = getBase(scope, INTENTION_BASE);
		if ( intentionBase == null ) { return null; }
		if ( !intentionBase.isEmpty() ) { return intentionBase.lastValue(scope); }
		return null;
	}

	@action(name = "has_desire", args = { @arg(name = PREDICATE,
		type = PredicateType.id,
		optional = true,
		doc = @doc("predicate to check")) }, doc = @doc(value = "check if the predicates is in the desire base.",
		returns = "true if it is in the base.",
		examples = { @example("") }))
	// @args(names = { PREDICATE_NAME, PREDICATE_PARAMETERS })
		public
		Boolean primTestDesire(final IScope scope) throws GamaRuntimeException {
		Predicate predicateDirect =
			(Predicate) (scope.hasArg(PREDICATE) ? scope.getArg(PREDICATE, PredicateType.id) : null);
		if ( predicateDirect != null ) { return getBase(scope, DESIRE_BASE).contains(predicateDirect);

		}
		return false;
	}

	@action(name = "current_intention_on_hold",
		args = { @arg(name = PREDICATE_ONHOLD,
			type = IType.NONE,
			optional = true,
			doc = @doc("the specified intention is put on hold (fited plan are not considered) until specific condition is reached. Can be an expression (which will be tested), a list (of subintentions), or nil (by default the condition will be the current list of subintentions of the intention)")) },
		doc = @doc(value = "puts the current intention on hold until the specified condition is reached or all subintentions are reached (not in desire base anymore).",
			returns = "true if it is in the base.",
			examples = { @example("") }))
	public
		Boolean primOnHoldIntention(final IScope scope) throws GamaRuntimeException {
		Predicate predicate = currentIntention(scope);
		Object until = scope.hasArg(PREDICATE_ONHOLD) ? scope.getArg(PREDICATE_ONHOLD, IType.NONE) : null;
		if(predicate!=null){
			if ( until == null ) {
				List<Predicate> subintention = predicate.subintentions;
				if ( subintention != null && !subintention.isEmpty() ) {
					predicate.onHoldUntil = subintention;
				}
			} else {
				if (predicate.onHoldUntil == null){
					predicate.onHoldUntil = GamaListFactory.create(Types.get(PredicateType.id));
				}
				if ( predicate.getSubintentions() == null ) {
					predicate.subintentions = GamaListFactory.create(Types.get(PredicateType.id));
				} /*else {
					predicate.getSubintentions().remove(until);
				}*/
				predicate.onHoldUntil.add((Predicate) until);
				predicate.getSubintentions().add((Predicate) until);
				addToBase(scope, (Predicate)until, DESIRE_BASE);
			}
		}
		return true;
	}

	@action(name = "add_subintention",
		args = {
			@arg(name = PREDICATE, type = PredicateType.id, optional = false, doc = @doc("predicate name")),
			@arg(name = PREDICATE_SUBINTENTIONS,
				type = PredicateType.id,
				optional = false,
				doc = @doc("the subintention to add to the predicate")),
				@arg(name = "add_as_desire",
				type = IType.BOOL,
				optional = true,
				doc = @doc("add the subintention as a desire as well (by default, false) ")) },
		doc = @doc(value = "adds the predicates is in the desire base.",
			returns = "true if it is in the base.",
			examples = { @example("") }))
	public Boolean addSubIntention(final IScope scope) throws GamaRuntimeException {
		Predicate predicate = (Predicate) (scope.hasArg(PREDICATE) ? scope.getArg(PREDICATE, PredicateType.id) : null);
		Predicate subpredicate =
			(Predicate) (scope.hasArg(PREDICATE_SUBINTENTIONS) ? scope.getArg(PREDICATE_SUBINTENTIONS, PredicateType.id) : null);

		if ( predicate == null || subpredicate == null ) { return false; }
		Boolean addAsDesire = (Boolean) (scope.hasArg("add_as_desire") ? scope.getArg("add_as_desire", PredicateType.BOOL) : false);
		
		if ( predicate.getSubintentions() == null ) {
			predicate.subintentions = GamaListFactory.create(Types.get(PredicateType.id));
		} /*else {
			predicate.getSubintentions().remove(subpredicate);
		}*/
		predicate.getSubintentions().add(subpredicate);
		if (addAsDesire) {
			addToBase(scope, subpredicate, DESIRE_BASE);
		}
		return true;
	}

	@action(name = "add_desire",
		args = {
			@arg(name = PREDICATE, type = PredicateType.id, optional = false, doc = @doc("predicate to add")),
			@arg(name = PREDICATE_TODO,
				type = PredicateType.id,
				optional = true,
				doc = @doc("add the desire as a subintention of this parameter")) },
		doc = @doc(value = "adds the predicates is in the desire base.",
			returns = "true if it is in the base.",
			examples = { @example("") }))
	public Boolean primAddDesire(final IScope scope) throws GamaRuntimeException {
		Predicate predicateDirect =
			(Predicate) (scope.hasArg(PREDICATE) ? scope.getArg(PREDICATE, PredicateType.id) : null);
		if ( predicateDirect != null ) {
			Predicate superpredicate =
				(Predicate) (scope.hasArg(PREDICATE_TODO) ? scope.getArg(PREDICATE_TODO, PredicateType.id) : null);
			if ( superpredicate != null ) {
				if ( superpredicate.getSubintentions() == null ) {
					superpredicate.subintentions = GamaListFactory.create(Types.get(PredicateType.id));
				}
				superpredicate.getSubintentions().add(predicateDirect);
			}
			addToBase(scope, predicateDirect, DESIRE_BASE);
			return true;
		}

		return false;
	}

	@action(name = "remove_belief", args = { @arg(name = PREDICATE,
		type = PredicateType.id,
		optional = true,
		doc = @doc("predicate to add")) }, doc = @doc(value = "removes the predicates from the belief base.",
		returns = "true if it is in the base.",
		examples = { @example("") }))
	public Boolean primRemoveBelief(final IScope scope) throws GamaRuntimeException {
		Predicate predicateDirect =
			(Predicate) (scope.hasArg(PREDICATE) ? scope.getArg(PREDICATE, PredicateType.id) : null);
		if ( predicateDirect != null ) { return getBase(scope, BELIEF_BASE).remove(predicateDirect);

		}
		return false;
	}
	
	@action(name = "replace_belief", args = { @arg(name = "old_predicate",
			type = PredicateType.id,
			optional = false,
			doc = @doc("predicate to remove")),
			@arg(name = PREDICATE,
			type = PredicateType.id,
			optional = false,
			doc = @doc("predicate to add"))}, doc = @doc(value = "replace the old predicate by the new one.",
			returns = "true if the old predicate is in the base.",
			examples = { @example("") }))
		public Boolean primPlaceBelief(final IScope scope) throws GamaRuntimeException {
			Predicate oldPredicate =
				(Predicate) (scope.hasArg("old_predicate") ? scope.getArg("old_predicate", PredicateType.id) : null);
			boolean ok = true;
			if ( oldPredicate != null ) { 
				ok = getBase(scope, BELIEF_BASE).remove(oldPredicate);
			} else {ok = false;}
			Predicate newPredicate =
					(Predicate) (scope.hasArg(PREDICATE) ? scope.getArg(PREDICATE, PredicateType.id) : null);
			if ( newPredicate != null ) { 
//				Predicate current_intention = currentIntention(scope);
				if(getBase(scope, SimpleBdiArchitecture.INTENTION_BASE).contains(newPredicate)){
					removeFromBase(scope, newPredicate, DESIRE_BASE);
					removeFromBase(scope, newPredicate, INTENTION_BASE);
				}
				for(Object statement : getBase(scope, SimpleBdiArchitecture.INTENTION_BASE)){
					if(((Predicate)statement).getSubintentions()!=null){
						if(((Predicate)statement).getSubintentions().contains(newPredicate)){
							((Predicate)statement).getSubintentions().remove(newPredicate);
						}
					}
					if(((GamaList)((Predicate)statement).getOnHoldUntil())!=null){
						if(((GamaList)((Predicate)statement).getOnHoldUntil()).contains(newPredicate)){
							((GamaList)((Predicate)statement).getOnHoldUntil()).remove(newPredicate);
						}
					}
				}
				return addToBase(scope, newPredicate, BELIEF_BASE); 
			}
			return ok;
		}

	@action(name = "remove_desire", args = { @arg(name = PREDICATE,
		type = PredicateType.id,
		optional = true,
		doc = @doc("predicate to add")) }, doc = @doc(value = "removes the predicates from the desire base.",
		returns = "true if it is in the base.",
		examples = { @example("") }))
	public Boolean primRemoveDesire(final IScope scope) throws GamaRuntimeException {
		Predicate predicateDirect =
			(Predicate) (scope.hasArg(PREDICATE) ? scope.getArg(PREDICATE, PredicateType.id) : null);
		if ( predicateDirect != null ) { 
			getBase(scope, DESIRE_BASE).remove(predicateDirect);
			//remove the intention too (and the subintentions)
			getBase(scope, INTENTION_BASE).remove(predicateDirect);
			for(Object statement : getBase(scope, SimpleBdiArchitecture.INTENTION_BASE)){
				if(((Predicate)statement).getSubintentions()!=null){
					if(((Predicate)statement).getSubintentions().contains(predicateDirect)){
						((Predicate)statement).getSubintentions().remove(predicateDirect);
					}
				}
				if(((ArrayList)((Predicate)statement).getOnHoldUntil())!=null){
					if(((ArrayList)((Predicate)statement).getOnHoldUntil()).contains(predicateDirect)){
						((ArrayList)((Predicate)statement).getOnHoldUntil()).remove(predicateDirect);
					}
				}
			}
			return true;
		}
		return false;
	}

	@action(name = "remove_intention",
		args = {
			@arg(name = PREDICATE, type = PredicateType.id, optional = true, doc = @doc("predicate to add")),
			@arg(name = REMOVE_DESIRE_AND_INTENTION,
				type = IType.BOOL,
				optional = false,
				doc = @doc("removes also desire")) },
		doc = @doc(value = "removes the predicates from the desire base.",
			returns = "true if it is in the base.",
			examples = { @example("") }))
	public Boolean primRemoveIntention(final IScope scope) throws GamaRuntimeException {

		Predicate predicateDirect =
			(Predicate) (scope.hasArg(PREDICATE) ? scope.getArg(PREDICATE, PredicateType.id) : null);
		if ( predicateDirect != null ) {
			Boolean dodesire =
				scope.hasArg(REMOVE_DESIRE_AND_INTENTION) ? scope.getBoolArg(REMOVE_DESIRE_AND_INTENTION) : false;
			getBase(scope, INTENTION_BASE).remove(predicateDirect);
			if ( dodesire ) {
				getBase(scope, DESIRE_BASE).remove(predicateDirect);
			}
			for(Object statement : getBase(scope, SimpleBdiArchitecture.INTENTION_BASE)){
				if(((Predicate)statement).getSubintentions()!=null){
					if(((Predicate)statement).getSubintentions().contains(predicateDirect)){
						((Predicate)statement).getSubintentions().remove(predicateDirect);
					}
				}
				if(((ArrayList)((Predicate)statement).getOnHoldUntil())!=null){
					if(((ArrayList)((Predicate)statement).getOnHoldUntil()).contains(predicateDirect)){
						((ArrayList)((Predicate)statement).getOnHoldUntil()).remove(predicateDirect);
					}
				}
			}
			return true;
		}

		return false;
	}

	@Override
	public boolean init(final IScope scope) throws GamaRuntimeException {
		super.init(scope);
		_consideringScope = scope;
		return true;
	}

	@Override
	public void verifyBehaviors(final ISpecies context) {}

	
}
