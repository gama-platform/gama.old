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

import java.util.ArrayList;
import java.util.List;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.architecture.reflex.ReflexArchitecture;
import msi.gaml.compilation.ISymbol;
import msi.gaml.operators.fastmaths.CmnFastMath;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@vars({ @var(name = SimpleBdiArchitecture.PERSISTENCE_COEFFICIENT_PLANS, type = IType.FLOAT, init = "1.0", doc = @doc("plan persistence")),
		@var(name = SimpleBdiArchitecture.PERSISTENCE_COEFFICIENT_INTENTIONS, type = IType.FLOAT, init = "1.0", doc = @doc("intention persistence")),
		@var(name = SimpleBdiArchitecture.PROBABILISTIC_CHOICE, type = IType.BOOL, init = "false"),
		@var(name = SimpleBdiArchitecture.USE_EMOTIONS_ARCHITECTURE, type = IType.BOOL, init = "false"),
		@var(name = SimpleBdiArchitecture.USE_SOCIAL_ARCHITECTURE, type = IType.BOOL, init ="false"),
		@var(name = SimpleBdiArchitecture.CHARISMA, type = IType.FLOAT, init = "1.0"),
		@var(name = SimpleBdiArchitecture.RECEPTIVITY, type = IType.FLOAT, init = "1.0"),
		@var(name = SimpleBdiArchitecture.BELIEF_BASE, type = IType.LIST, of = PredicateType.id, init = "[]"),
		@var(name = SimpleBdiArchitecture.LAST_THOUGHTS, type = IType.LIST, init = "[]"),
		@var(name = SimpleBdiArchitecture.INTENTION_BASE, type = IType.LIST, of = PredicateType.id, init = "[]"),
		@var(name = SimpleBdiArchitecture.EMOTION_BASE, type = IType.LIST, of = EmotionType.id, init = "[]"),
		@var(name = SimpleBdiArchitecture.DESIRE_BASE, type = IType.LIST, of = PredicateType.id, init = "[]"),
		@var(name = SimpleBdiArchitecture.UNCERTAINTY_BASE, type = IType.LIST, of = PredicateType.id, init = "[]"),
		@var(name = SimpleBdiArchitecture.PLAN_BASE, type = IType.LIST, of = BDIPlanType.id, init = "[]"),
		@var(name = SimpleBdiArchitecture.SOCIALLINK_BASE, type = IType.LIST, of = SocialLinkType.id, init = "[]"),
		@var(name = SimpleBdiArchitecture.CURRENT_PLAN, type = IType.NONE) })
@skill(name = SimpleBdiArchitecture.SIMPLE_BDI, concept = { IConcept.BDI, IConcept.ARCHITECTURE })

public class SimpleBdiArchitecture extends ReflexArchitecture {

	public static final String SIMPLE_BDI = "simple_bdi";
	public static final String PLAN = "plan";
	public static final String PRIORITY = "priority";
	public static final String FINISHEDWHEN = "finished_when";
	public static final String PERSISTENCE_COEFFICIENT_PLANS = "plan_persistence";
	public static final String PERSISTENCE_COEFFICIENT_INTENTIONS = "intention_persistence";
	public static final String USE_EMOTIONS_ARCHITECTURE = "use_emotions_architecture";
	public static final String USE_SOCIAL_ARCHITECTURE ="use_social_architecture";
	public static final String CHARISMA = "charisma";
	public static final String RECEPTIVITY = "receptivity";

	// TODO: Not implemented yet

	public static final String PROBABILISTIC_CHOICE = "probabilistic_choice";
	public static final String INSTANTANEAOUS = "instantaneous";

	// INFORMATION THAT CAN BE DISPLAYED
	public static final String LAST_THOUGHTS = "thinking";
	public static final Integer LAST_THOUGHTS_SIZE = 5;

	public static final String EMOTION = "emotion";
	public static final String SOCIALLINK = "social_link";
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
	public static final String SOCIALLINK_BASE = ("social_link_base");
	public static final String EVERY_VALUE = "every_possible_value";
	public static final String PLAN_BASE = "plan_base";
	public static final String CURRENT_PLAN = "current_plan";
	public static final String UNCERTAINTY_BASE = "uncertainty_base";

	// WARNING
	// AD: These values depend on the scope (i.e. the agent)
	// An architecture should be stateless and stock the scope dependent values
	// in the
	// agent(s).
	private final List<BDIPlan> _plans = new ArrayList<BDIPlan>();
	private final List<PerceiveStatement> _perceptions = new ArrayList<PerceiveStatement>();
	private final List<RuleStatement> _rules = new ArrayList<RuleStatement>();
	private int _plansNumber = 0;
	private int _perceptionNumber = 0;
	private boolean iscurrentplaninstantaneous = false;

	private int _rulesNumber = 0;

	@Override
	protected void clearBehaviors() {
		super.clearBehaviors();
		_plans.clear();
		_rules.clear();
		_perceptions.clear();
	}

	@Override
	public void setChildren(final List<? extends ISymbol> children) {
		clearBehaviors();
		for (final ISymbol c : children) {
			addBehavior((IStatement) c);
		}
	}

	@Override
	public void addBehavior(final IStatement c) {
		if (c instanceof SimpleBdiPlanStatement) {
			// final String statementKeyword = c.getDescription().getKeyword();
			_plans.add(new BDIPlan((SimpleBdiPlanStatement) c));
			_plansNumber++;
		} else if (c instanceof PerceiveStatement) {
			// final String statementKeyword = c.getDescription().getKeyword();
			_perceptions.add((PerceiveStatement) c);
			_perceptionNumber++;
		} else if (c instanceof RuleStatement) {
			// final String statementKeyword = c.getDescription().getKeyword();
			_rules.add((RuleStatement) c);
			_rulesNumber++;
		} else {
			super.addBehavior(c);
		}
	}

	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		super.executeOn(scope);
		final IAgent agent = scope.getAgent();
		if (agent.dead()) {
			return null;
		}
		if (_perceptionNumber > 0) {
			for (int i = 0; i < _perceptionNumber; i++) {
				_perceptions.get(i).executeOn(scope);
				if (agent.dead()) {
					return null;
				}
			}
		}
		if (_rulesNumber > 0) {
			for (int i = 0; i < _rulesNumber; i++) {
				_rules.get(i).executeOn(scope);
				if (agent.dead()) {
					return null;
				}
			}
		}
		computeEmotions(scope);
		updateSocialLinks(scope);
		return executePlans(scope);
	}

	protected final Object executePlans(final IScope scope) {
		Object result = null;
		if (_plansNumber > 0) {
			boolean loop_instantaneous_plans = true;
			while (loop_instantaneous_plans) {
				loop_instantaneous_plans = false;
				final IAgent agent = getCurrentAgent(scope);
				final GamaList<Predicate> desireBase = (GamaList<Predicate>) (scope.hasArg(DESIRE_BASE)
						? scope.getListArg(DESIRE_BASE) : (GamaList<Predicate>) agent.getAttribute(DESIRE_BASE));
				final GamaList<Predicate> intentionBase = (GamaList<Predicate>) (scope.hasArg(INTENTION_BASE)
						? scope.getListArg(INTENTION_BASE) : (GamaList<Predicate>) agent.getAttribute(INTENTION_BASE));
				final Double persistenceCoefficientPlans = scope.hasArg(PERSISTENCE_COEFFICIENT_PLANS)
						? scope.getFloatArg(PERSISTENCE_COEFFICIENT_PLANS)
						: (Double) agent.getAttribute(PERSISTENCE_COEFFICIENT_PLANS);
				final Double persistenceCoefficientintention = scope.hasArg(PERSISTENCE_COEFFICIENT_INTENTIONS)
						? scope.getFloatArg(PERSISTENCE_COEFFICIENT_INTENTIONS)
						: (Double) agent.getAttribute(PERSISTENCE_COEFFICIENT_INTENTIONS);

				SimpleBdiPlanStatement _persistentTask = (SimpleBdiPlanStatement) agent.getAttribute(CURRENT_PLAN);
				// RANDOMLY REMOVE (last)INTENTION
				Boolean flipResultintention = msi.gaml.operators.Random.opFlip(scope, persistenceCoefficientintention);
				while (!flipResultintention && intentionBase.size() > 0) {
					flipResultintention = msi.gaml.operators.Random.opFlip(scope, persistenceCoefficientintention);
					if (intentionBase.size() > 0) {
						final int toremove = intentionBase.size() - 1;
						final Predicate previousint = intentionBase.get(toremove);
						intentionBase.remove(toremove);
						final String think = "check what happens if I remove: " + previousint;
						addThoughts(scope, think);
						_persistentTask = null;
						agent.setAttribute(CURRENT_PLAN, _persistentTask);
					}
				}
				// If current intention has no plan or is on hold, choose a new
				// Desire

				if (testOnHold(scope, currentIntention(scope)) || listExecutablePlans(scope).isEmpty()) {
					selectDesireWithHighestPriority(scope);
					_persistentTask = null;
					agent.setAttribute(CURRENT_PLAN, _persistentTask);

				}

				_persistentTask = (SimpleBdiPlanStatement) agent.getAttribute(CURRENT_PLAN);
				// if((currentIntention(scope)!=null) && (_persistentTask!=null)
				// &&
				// !(_persistentTask._intention.value(scope).equals(currentIntention(scope)))){
				// _persistentTask = null;
				// agent.setAttribute(CURRENT_PLAN, _persistentTask);
				// }
				final Boolean flipResult = msi.gaml.operators.Random.opFlip(scope, persistenceCoefficientPlans);

				if (!flipResult) {
					if (_persistentTask != null) {
						addThoughts(scope, "check what happens if I stop: " + _persistentTask.getName());
					}
					_persistentTask = selectExecutablePlanWithHighestPriority(scope);
					agent.setAttribute(CURRENT_PLAN, _persistentTask);

					if (_persistentTask != null) {
						addThoughts(scope, "lets do instead " + _persistentTask.getName());
					}

				}

				// choose a plan for the current intention
				if (_persistentTask == null && currentIntention(scope) == null) {
					selectDesireWithHighestPriority(scope);
					if (currentIntention(scope) == null) {
						addThoughts(scope, "I want nothing...");
						// update the lifetime of beliefs
						updateLifeTimePredicates(scope);
						updateEmotionsIntensity(scope);
						return null;

					}
					_persistentTask = selectExecutablePlanWithHighestPriority(scope);
					agent.setAttribute(CURRENT_PLAN, _persistentTask);
					if (_persistentTask != null)
						addThoughts(scope, "ok, new intention: " + currentIntention(scope) + " with plan "
								+ _persistentTask.getName());
				}
				if (_persistentTask == null && currentIntention(scope) != null) {
					_persistentTask = selectExecutablePlanWithHighestPriority(scope);
					agent.setAttribute(CURRENT_PLAN, _persistentTask);
					if (_persistentTask != null) {
						addThoughts(scope, "use plan : " + _persistentTask.getName());
					}
				}
				if (_persistentTask != null) {
					if (!agent.dead()) {
						result = _persistentTask.executeOn(scope);
						boolean isExecuted = false;
						if (_persistentTask.getExecutedExpression() != null) {
							isExecuted = msi.gaml.operators.Cast.asBool(scope,
									_persistentTask.getExecutedExpression().value(scope));
						}
						if (this.iscurrentplaninstantaneous) {
							loop_instantaneous_plans = true;
						}
						if (isExecuted) {
							_persistentTask = null;
							agent.setAttribute(CURRENT_PLAN, _persistentTask);

						}
					}
				}
				if (!agent.dead()) {
					// Part that manage the lifetime of predicates
					updateLifeTimePredicates(scope);
					updateEmotionsIntensity(scope);
				}
			}
		}

		return result;
	}

	protected final Boolean selectDesireWithHighestPriority(final IScope scope) {
		final IAgent agent = getCurrentAgent(scope);
		final Boolean is_probabilistic_choice = scope.hasArg(PROBABILISTIC_CHOICE)
				? scope.getBoolArg(PROBABILISTIC_CHOICE) : (Boolean) agent.getAttribute(PROBABILISTIC_CHOICE);
		if (is_probabilistic_choice) {
			final GamaList<Predicate> desireBase = getBase(scope, DESIRE_BASE);
			final GamaList<Predicate> intentionBase = getBase(scope, INTENTION_BASE);
			if (desireBase.size() > 0) {
				Predicate newIntention = desireBase.anyValue(scope);
				final double priority_list[] = new double[desireBase.length(scope)];
				for (int i = 0; i < desireBase.length(scope); i++) {
					priority_list[i] = desireBase.get(i).priority;
				}
				final IList priorities = GamaListFactory.create(scope, Types.FLOAT, priority_list);
				final int index_choice = msi.gaml.operators.Random.opRndChoice(scope, priorities);
				newIntention = desireBase.get(index_choice);
				if (desireBase.size() > intentionBase.size()) {
					while (intentionBase.contains(newIntention)) {
						final int index_choice2 = msi.gaml.operators.Random.opRndChoice(scope, priorities);
						newIntention = desireBase.get(index_choice2);
					}
				}
				if (newIntention.getSubintentions() == null) {
					if (!intentionBase.contains(newIntention)) {
						intentionBase.addValue(scope, newIntention);
						return true;
					}
				} else {
					for (int i = 0; i < newIntention.getSubintentions().size(); i++) {
						if (!desireBase.contains(newIntention.getSubintentions().get(i))) {
							desireBase.addValue(scope, newIntention.getSubintentions().get(i));
						}
					}
					newIntention.setOnHoldUntil(newIntention.getSubintentions());
					if (!intentionBase.contains(newIntention)) {
						intentionBase.addValue(scope, newIntention);
						return true;
					}
				}
			}
		} else {
			final GamaList<Predicate> desireBase = (GamaList<Predicate>) scope.getSimulation().getRandomGenerator()
					.shuffle(getBase(scope, DESIRE_BASE));
			final GamaList<Predicate> intentionBase = getBase(scope, INTENTION_BASE);
			double maxpriority = Double.NEGATIVE_INFINITY;
			if (desireBase.size() > 0 && intentionBase != null) {
				Predicate newIntention = desireBase.anyValue(scope);
				for (final Predicate desire : desireBase) {

					if (desire.priority > maxpriority) {
						if (!intentionBase.contains(desire)) {
							maxpriority = desire.priority;
							newIntention = desire;
						}
					}
				}
				if (newIntention.getSubintentions() == null) {
					if (!intentionBase.contains(newIntention)) {
						intentionBase.addValue(scope, newIntention);
						return true;
					}
				} else {
					for (int i = 0; i < newIntention.getSubintentions().size(); i++) {
						if (!desireBase.contains(newIntention.getSubintentions().get(i))) {
							desireBase.addValue(scope, newIntention.getSubintentions().get(i));
						}
					}
					newIntention.setOnHoldUntil(newIntention.getSubintentions());
					if (!intentionBase.contains(newIntention)) {
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
		final Boolean is_probabilistic_choice = scope.hasArg(PROBABILISTIC_CHOICE)
				? scope.getBoolArg(PROBABILISTIC_CHOICE) : (Boolean) agent.getAttribute(PROBABILISTIC_CHOICE);

		SimpleBdiPlanStatement resultStatement = null;

		double highestPriority = Double.MIN_VALUE;
		final List<SimpleBdiPlanStatement> temp_plan = new ArrayList<SimpleBdiPlanStatement>();
		final IList priorities = GamaListFactory.create(Types.FLOAT);
		for (final Object BDIPlanstatement : scope.getSimulation().getRandomGenerator()
				.shuffle(new ArrayList(_plans))) {
			final SimpleBdiPlanStatement statement = ((BDIPlan) BDIPlanstatement).getPlanStatement();
			final boolean isContextConditionSatisfied = statement.getContextExpression() == null
					|| msi.gaml.operators.Cast.asBool(scope, statement.getContextExpression().value(scope));
			final boolean isIntentionConditionSatisfied = statement.getIntentionExpression() == null
					|| ((Predicate) statement.getIntentionExpression().value(scope))
							.equalsIntentionPlan(currentIntention(scope));
			final boolean isEmotionConditionSatisfied = statement.getEmotionExpression() == null
					|| getEmotionBase(scope, EMOTION_BASE).contains(statement.getEmotionExpression().value(scope));
			final boolean thresholdSatisfied = statement.getThreshold() == null
					|| statement.getEmotionExpression() != null && SimpleBdiArchitecture.getEmotion(scope,
							(Emotion) statement.getEmotionExpression().value(scope)).intensity >= (Double) statement
									.getThreshold().value(scope);
			if (isContextConditionSatisfied && isIntentionConditionSatisfied && isEmotionConditionSatisfied
					&& thresholdSatisfied) {
				if (is_probabilistic_choice) {
					temp_plan.add(statement);
				} else {
					double currentPriority = 1.0;
					if (statement.getFacet(SimpleBdiArchitecture.PRIORITY) != null) {
						currentPriority = msi.gaml.operators.Cast.asFloat(scope,
								statement.getPriorityExpression().value(scope));
					}

					if (highestPriority < currentPriority) {
						highestPriority = currentPriority;
						resultStatement = statement;
					}
				}
			}
		}
		if (is_probabilistic_choice) {
			if (!temp_plan.isEmpty()) {
				for (final Object statement : temp_plan) {
					if (((SimpleBdiPlanStatement) statement).hasFacet(PRIORITY)) {
						priorities.add(msi.gaml.operators.Cast.asFloat(scope,
								((SimpleBdiPlanStatement) statement).getPriorityExpression().value(scope)));
					} else {
						priorities.add(1.0);
					}
				}
				final int index_plan = msi.gaml.operators.Random.opRndChoice(scope, priorities);
				resultStatement = temp_plan.get(index_plan);
			}
		}

		iscurrentplaninstantaneous = false;
		if (resultStatement != null) {
			if (resultStatement.getFacet(SimpleBdiArchitecture.INSTANTANEAOUS) != null) {
				iscurrentplaninstantaneous = msi.gaml.operators.Cast.asBool(scope,
						resultStatement.getInstantaneousExpression().value(scope));
			}
		}

		return resultStatement;
	}

	private void updateLifeTimePredicates(final IScope scope) {
		for (final Predicate pred : getBase(scope, BELIEF_BASE)) {
			pred.isUpdated = false;
		}
		for (final Predicate pred : getBase(scope, DESIRE_BASE)) {
			pred.isUpdated = false;
		}
		for (final Predicate pred : getBase(scope, INTENTION_BASE)) {
			pred.isUpdated = false;
		}
		for (final Predicate pred : getBase(scope, UNCERTAINTY_BASE)) {
			pred.isUpdated = false;
		}
		for (final Predicate pred : getBase(scope, BELIEF_BASE)) {
			pred.updateLifetime();
		}
		for (final Predicate pred : listBeliefsLifeTimeNull(scope)) {
			removeBelief(scope, pred);
		}
		for (final Predicate pred : getBase(scope, DESIRE_BASE)) {
			pred.updateLifetime();
		}
		for (final Predicate pred : listDesiresLifeTimeNull(scope)) {
			removeDesire(scope, pred);
		}
		for (final Predicate pred : getBase(scope, INTENTION_BASE)) {
			pred.updateLifetime();
		}
		for (final Predicate pred : listIntentionsLifeTimeNull(scope)) {
			removeIntention(scope, pred);
		}
		for (final Predicate pred : getBase(scope, UNCERTAINTY_BASE)) {
			pred.updateLifetime();
		}
		for (final Predicate pred : listUncertaintyLifeTimeNull(scope)) {
			removeUncertainty(scope, pred);
		}
	}

	private List<Predicate> listBeliefsLifeTimeNull(final IScope scope) {
		final List<Predicate> tempPred = new ArrayList<Predicate>();
		for (final Predicate pred : getBase(scope, BELIEF_BASE)) {
			if (pred.getLifetime() == 0) {
				tempPred.add(pred);
			}
		}
		return tempPred;
	}

	private List<Predicate> listDesiresLifeTimeNull(final IScope scope) {
		final List<Predicate> tempPred = new ArrayList<Predicate>();
		for (final Predicate pred : getBase(scope, DESIRE_BASE)) {
			if (pred.getLifetime() == 0) {
				tempPred.add(pred);
			}
		}
		return tempPred;
	}

	private List<Predicate> listIntentionsLifeTimeNull(final IScope scope) {
		final List<Predicate> tempPred = new ArrayList<Predicate>();
		for (final Predicate pred : getBase(scope, INTENTION_BASE)) {
			if (pred.getLifetime() == 0) {
				tempPred.add(pred);
			}
		}
		return tempPred;
	}

	private List<Predicate> listUncertaintyLifeTimeNull(final IScope scope) {
		final List<Predicate> tempPred = new ArrayList<Predicate>();
		for (final Predicate pred : getBase(scope, UNCERTAINTY_BASE)) {
			if (pred.getLifetime() == 0) {
				tempPred.add(pred);
			}
		}
		return tempPred;
	}

	protected final List<SimpleBdiPlanStatement> listExecutablePlans(final IScope scope) {
		final IAgent agent = getCurrentAgent(scope);
		final List<SimpleBdiPlanStatement> plans = new ArrayList<SimpleBdiPlanStatement>();
		for (final Object BDIPlanstatement : scope.getRandom().shuffle(new ArrayList(_plans))) {
			final SimpleBdiPlanStatement statement = ((BDIPlan) BDIPlanstatement).getPlanStatement();

			if (statement.getContextExpression() != null
					&& !msi.gaml.operators.Cast.asBool(scope, statement.getContextExpression().value(scope))) {
				continue;
			}
			if (statement.getIntentionExpression() == null
					|| ((Predicate) statement.getIntentionExpression().value(scope))
							.equalsIntentionPlan(currentIntention(scope))) {
				plans.add(statement);
			}
		}
		return plans;
	}

	public GamaList<String> getThoughts(final IScope scope) {
		final IAgent agent = getCurrentAgent(scope);
		final GamaList<String> thoughts = (GamaList<String>) agent.getAttribute(LAST_THOUGHTS);
		return thoughts;
	}

	public IList<String> addThoughts(final IScope scope, final String think) {
		final IAgent agent = getCurrentAgent(scope);
		final GamaList<String> thoughts = (GamaList<String>) agent.getAttribute(LAST_THOUGHTS);
		final IList newthoughts = GamaListFactory.create(Types.STRING);
		newthoughts.add(think);
		if (thoughts != null && thoughts.size() > 0) {
			newthoughts.addAll(thoughts.subList(0, CmnFastMath.min(LAST_THOUGHTS_SIZE - 1, thoughts.size())));
		}
		agent.setAttribute(LAST_THOUGHTS, newthoughts);
		return newthoughts;
	}

	public boolean testOnHold(final IScope scope, final Predicate intention) {
		if (intention == null) {
			return false;
		}
		if (intention.onHoldUntil == null) {
			return false;
		}
		if (intention.getValues() != null) {
			if (intention.getValues().containsKey("and")) {
				final Object cond = intention.onHoldUntil;
				if (cond instanceof ArrayList) {
					if (((ArrayList) cond).size() == 0) {
						final GamaList desbase = getBase(scope, DESIRE_BASE);
						final GamaList intentionbase = getBase(scope, INTENTION_BASE);
						desbase.remove(intention);
						intentionbase.remove(intention);
						for (final Object statement : getBase(scope, SimpleBdiArchitecture.INTENTION_BASE)) {
							final List<Predicate> statementSubintention = ((Predicate) statement).getSubintentions();
							if (statementSubintention != null) {
								if (statementSubintention.contains(intention)) {
									statementSubintention.remove(intention);
								}
							}
							final List<Predicate> statementOnHoldUntil = ((Predicate) statement).getOnHoldUntil();
							if (statementOnHoldUntil != null) {
								if (statementOnHoldUntil.contains(intention)) {
									statementOnHoldUntil.remove(intention);
								}
							}
						}
						return false;
					} else {
						return true;
					}
				}
			}
			if (intention.getValues().containsKey("or")) {
				final Object cond = intention.onHoldUntil;
				if (cond instanceof ArrayList) {
					if (((ArrayList) cond).size() <= 1) {
						final GamaList desbase = getBase(scope, DESIRE_BASE);
						final GamaList intentionbase = getBase(scope, INTENTION_BASE);
						desbase.remove(intention);
						intentionbase.remove(intention);
						if (((ArrayList) cond).size() == 1) {
							if (desbase.contains(((ArrayList) cond).get(0))) {
								desbase.remove(((ArrayList) cond).get(0));
							}
							for (final Object statement : getBase(scope, SimpleBdiArchitecture.INTENTION_BASE)) {
								final List<Predicate> statementSubintention = ((Predicate) statement)
										.getSubintentions();
								if (statementSubintention != null) {
									if (statementSubintention.contains(intention)) {
										statementSubintention.remove(intention);
									}
								}
								final List<Predicate> statementOnHoldUntil = ((Predicate) statement).getOnHoldUntil();
								if (statementOnHoldUntil != null) {
									if (statementOnHoldUntil.contains(intention)) {
										statementOnHoldUntil.remove(intention);
									}
								}
							}
						}
						return false;
					} else {
						return true;
					}
				}
			}
		}
		final Object cond = intention.onHoldUntil;
		if (cond instanceof ArrayList) {
			final GamaList desbase = getBase(scope, DESIRE_BASE);
			if (desbase.isEmpty()) {
				return false;
			}
			for (final Object subintention : (ArrayList) cond) {
				if (desbase.contains(subintention)) {
					return true;
				}
			}
			addThoughts(scope, "no more subintention for" + intention);
			/* Must remove the current plan to change for a new one */
			final IAgent agent = getCurrentAgent(scope);
			SimpleBdiPlanStatement _persistentTask = (SimpleBdiPlanStatement) agent.getAttribute(CURRENT_PLAN);
			_persistentTask = null;
			agent.setAttribute(CURRENT_PLAN, _persistentTask);
			return false;

		}
		// if (cond instanceof String) {
		// final Object res = msi.gaml.operators.System.opEvalGaml(scope,
		// (String) cond);
		// if (Cast.asBool(scope, res) == false) {
		// return true;
		// }
		//
		// }
		return false;

	}

	@action(name = "get_plans", doc = @doc(value = "get the list of plans.", returns = "the list of BDI plans.", examples = {
			@example("get_plans()") }))
	public List<BDIPlan> getPlans(final IScope scope) {
		if (_plans.size() > 0) {
			return _plans;
		}
		return null;
	}

	public static GamaList<Predicate> getBase(final IScope scope, final String basename) {
		final IAgent agent = scope.getAgent();
		return (GamaList<Predicate>) (scope.hasArg(basename) ? scope.getListArg(basename)
				: (GamaList<Predicate>) agent.getAttribute(basename));
	}

	public static GamaList<Emotion> getEmotionBase(final IScope scope, final String basename) {
		final IAgent agent = scope.getAgent();
		return (GamaList<Emotion>) (scope.hasArg(basename) ? scope.getListArg(basename)
				: (GamaList<Emotion>) agent.getAttribute(basename));
	}

	public static GamaList<SocialLink> getSocialBase(final IScope scope, final String basename) {
		final IAgent agent = scope.getAgent();
		return (GamaList<SocialLink>) (scope.hasArg(basename) ? scope.getListArg(basename)
				: (GamaList<SocialLink>) agent.getAttribute(basename));
	}
	
	public static boolean removeFromBase(final IScope scope, final Predicate predicateItem, final String factBaseName) {
		final GamaList<Predicate> factBase = getBase(scope, factBaseName);
		return factBase.remove(predicateItem);
	}

	public static boolean removeFromBase(final IScope scope, final Emotion emotionItem, final String factBaseName) {
		final GamaList<Emotion> factBase = getEmotionBase(scope, factBaseName);
		return factBase.remove(emotionItem);
	}

	public static boolean addToBase(final IScope scope, final Predicate predicateItem, final String factBaseName) {
		return addToBase(scope, predicateItem, getBase(scope, factBaseName));
	}

	public static boolean addToBase(final IScope scope, final Emotion emotionItem, final String factBaseName) {
		return addToBase(scope, emotionItem, getEmotionBase(scope, factBaseName));
	}
	
	public static boolean addToBase(final IScope scope, final SocialLink socialItem, final String factBaseName) {
		return addToBase(scope, socialItem, getSocialBase(scope, factBaseName));
	}

	public static boolean addToBase(final IScope scope, final Predicate predicateItem,
			final GamaList<Predicate> factBase) {

		factBase.remove(predicateItem);

		predicateItem.setDate(scope.getClock().getTime());
		return factBase.add(predicateItem);
	}

	public static boolean addToBase(final IScope scope, final Emotion predicateItem, final GamaList<Emotion> factBase) {
		factBase.remove(predicateItem);
		return factBase.add(predicateItem);
	}
	
	public static boolean addToBase(final IScope scope, final SocialLink socialItem, final GamaList<SocialLink> factBase) {
		factBase.remove(socialItem);
		return factBase.add(socialItem);
	}

	public static Boolean addBelief(final IScope scope, final Predicate predicateDirect) {
		final GamaList<Predicate> factBase = getBase(scope, BELIEF_BASE);
		Predicate predTemp = null;
		if (predicateDirect != null) {
			createJoyFromPredicate(scope, predicateDirect);
			for (final Predicate predTest : getBase(scope, SimpleBdiArchitecture.BELIEF_BASE)) {
				if (predTest.equalsButNotTruth(predicateDirect)) {
					predTemp = predTest;
				}
			}
			if (predTemp != null) {
				removeFromBase(scope, predTemp, BELIEF_BASE);
			}
			if (getBase(scope, SimpleBdiArchitecture.INTENTION_BASE).contains(predicateDirect)) {
				removeFromBase(scope, predicateDirect, DESIRE_BASE);
				removeFromBase(scope, predicateDirect, INTENTION_BASE);
			}
			if (getBase(scope, SimpleBdiArchitecture.DESIRE_BASE).contains(predicateDirect)) {
				removeFromBase(scope, predicateDirect, DESIRE_BASE);
			}
			if (getBase(scope, SimpleBdiArchitecture.UNCERTAINTY_BASE).contains(predicateDirect)) {
				removeFromBase(scope, predicateDirect, UNCERTAINTY_BASE);
			}
			for (final Predicate predTest : getBase(scope, SimpleBdiArchitecture.UNCERTAINTY_BASE)) {
				if(predTest.equalsButNotTruth(predicateDirect)){
					predTemp = predTest;
				}
			}
			if (predTemp != null) {
				removeFromBase(scope, predTemp, UNCERTAINTY_BASE);
			}
			for (final Object statement : getBase(scope, SimpleBdiArchitecture.INTENTION_BASE)) {
				final List<Predicate> statementSubintention = ((Predicate) statement).getSubintentions();
				if (statementSubintention != null) {
					if (statementSubintention.contains(predicateDirect)) {
						statementSubintention.remove(predicateDirect);
					}
				}
				final List<Predicate> statementOnHoldUntil = ((Predicate) statement).getOnHoldUntil();
				if (statementOnHoldUntil != null) {
					if (statementOnHoldUntil.contains(predicateDirect)) {
						statementOnHoldUntil.remove(predicateDirect);
					}
				}
			}
			return addToBase(scope, predicateDirect, BELIEF_BASE);
		}

		return false;
	}

	@action(name = "add_belief", args = {
			@arg(name = PREDICATE, type = IType.MAP, optional = true, doc = @doc("predicate to add as a belief")) }, doc = @doc(value = "add the predicate in the belief base.", returns = "true if it is added in the base.", examples = {
					@example("") }))
	public Boolean primAddBelief(final IScope scope) throws GamaRuntimeException {
		final Predicate predicateDirect = (Predicate) (scope.hasArg(PREDICATE)
				? scope.getArg(PREDICATE, PredicateType.id) : null);
		return addBelief(scope, predicateDirect);

	}

	public static Boolean hasBelief(final IScope scope, final Predicate predicateDirect) {
		return getBase(scope, BELIEF_BASE).contains(predicateDirect);
	}

	public static Boolean hasDesire(final IScope scope, final Predicate predicateDirect) {
		return getBase(scope, DESIRE_BASE).contains(predicateDirect);
	}

	@action(name = "has_belief", args = {
			@arg(name = PREDICATE, type = PredicateType.id, optional = true, doc = @doc("predicate to check")) }, doc = @doc(value = "check if the predicates is in the belief base.", returns = "true if it is in the base.", examples = {
					@example("") }))
	public Boolean primTestBelief(final IScope scope) throws GamaRuntimeException {
		final Predicate predicateDirect = (Predicate) (scope.hasArg(PREDICATE)
				? scope.getArg(PREDICATE, PredicateType.id) : null);
		if (predicateDirect != null) {
			return hasBelief(scope, predicateDirect);
		}
		return false;
	}

	@action(name = "get_belief", args = {
			@arg(name = PREDICATE, type = PredicateType.id, optional = false, doc = @doc("predicate to get")) }, doc = @doc(value = "get the predicate in the belief base (if several, returns the first one).", returns = "the predicate if it is in the base.", examples = {
					@example("get_belief(new_predicate(\"has_water\", true))") }))
	public Predicate getBelief(final IScope scope) throws GamaRuntimeException {
		final Predicate predicateDirect = (Predicate) (scope.hasArg(PREDICATE)
				? scope.getArg(PREDICATE, PredicateType.id) : null);
		if (predicateDirect != null) {
			for (final Predicate pred : getBase(scope, BELIEF_BASE)) {
				if (predicateDirect.equals(pred)) {
					return pred;
				}
			}

		}
		return null;

	}

	@action(name = "get_belief_with_name", args = {
			@arg(name = "name", type = IType.STRING, optional = false, doc = @doc("name of the predicate to check")) }, doc = @doc(value = "get the predicates is in the belief base (if several, returns the first one).", returns = "the predicate if it is in the base.", examples = {
					@example("get_belief_with_name(\"has_water\")") }))
	public Predicate getBeliefName(final IScope scope) throws GamaRuntimeException {
		final String predicateName = (String) (scope.hasArg("name") ? scope.getArg("name", IType.STRING) : null);
		if (predicateName != null) {
			for (final Predicate pred : getBase(scope, BELIEF_BASE)) {
				if (predicateName.equals(pred.getName())) {
					return pred;
				}
			}
		}
		return null;
	}

	@action(name = "get_beliefs_with_name", args = {
			@arg(name = "name", type = IType.STRING, optional = false, doc = @doc("name of the predicates to check")) }, doc = @doc(value = "get the list of predicates is in the belief base with the given name.", returns = "the list of predicates.", examples = {
					@example("get_belief(\"has_water\")") }))
	public List<Predicate> getBeliefsName(final IScope scope) throws GamaRuntimeException {
		final String predicateName = (String) (scope.hasArg("name") ? scope.getArg("name", IType.STRING) : null);
		final List<Predicate> predicates = GamaListFactory.create();
		if (predicateName != null) {
			for (final Predicate pred : getBase(scope, BELIEF_BASE)) {
				if (predicateName.equals(pred.getName())) {
					predicates.add(pred);
				}
			}
		}
		return predicates;
	}

	@action(name = "get_beliefs", args = {
			@arg(name = PREDICATE, type = PredicateType.id, optional = false, doc = @doc("name of the predicates to check")) }, doc = @doc(value = "get the list of predicates is in the belief base", returns = "the list of predicates.", examples = {
					@example("get_belief(\"has_water\")") }))
	public IList<Predicate> getBeliefs(final IScope scope) throws GamaRuntimeException {
		final Predicate predicateDirect = (Predicate) (scope.hasArg(PREDICATE)
				? scope.getArg(PREDICATE, PredicateType.id) : null);
		final IList<Predicate> predicates = GamaListFactory.create();
		if (predicateDirect != null) {
			for (final Predicate pred : getBase(scope, BELIEF_BASE)) {
				if (predicateDirect.equals(pred)) {
					predicates.add(pred);
				}
			}
		}
		return predicates;
	}

	@action(name = "is_current_intention", args = {
			@arg(name = PREDICATE, type = PredicateType.id, optional = false, doc = @doc("predicate to check")) }, doc = @doc(value = "check if the predicates is the current intention (last entry of intention base).", returns = "true if it is in the base.", examples = {
					@example("") }))
	public Boolean iscurrentIntention(final IScope scope) throws GamaRuntimeException {
		final Predicate predicateDirect = (Predicate) (scope.hasArg(PREDICATE)
				? scope.getArg(PREDICATE, PredicateType.id) : null);
		final Predicate currentIntention = currentIntention(scope);

		if (predicateDirect != null && currentIntention != null) {
			return predicateDirect.equals(currentIntention);
		}

		return false;
	}

	@action(name = "get_current_intention", doc = @doc(value = "returns the current intention (last entry of intention base).", returns = "the current intention", examples = {
			@example("") }))
	public Predicate currentIntention(final IScope scope) throws GamaRuntimeException {
		final GamaList<Predicate> intentionBase = getBase(scope, INTENTION_BASE);
		if (intentionBase == null) {
			return null;
		}
		if (!intentionBase.isEmpty()) {
			return intentionBase.lastValue(scope);
		}
		return null;
	}

	@action(name = "has_desire", args = {
			@arg(name = PREDICATE, type = PredicateType.id, optional = true, doc = @doc("predicate to check")) }, doc = @doc(value = "check if the predicates is in the desire base.", returns = "true if it is in the base.", examples = {
					@example("") }))
	// @args(names = { PREDICATE_NAME, PREDICATE_PARAMETERS })
	public Boolean primTestDesire(final IScope scope) throws GamaRuntimeException {
		final Predicate predicateDirect = (Predicate) (scope.hasArg(PREDICATE)
				? scope.getArg(PREDICATE, PredicateType.id) : null);
		if (predicateDirect != null) {
			return getBase(scope, DESIRE_BASE).contains(predicateDirect);

		}
		return false;
	}

	@action(name = "current_intention_on_hold", args = {
			@arg(name = "until", type = IType.NONE, optional = true, doc = @doc("the current intention is put on hold (fited plan are not considered) until specific condition is reached. Can be an expression (which will be tested), a list (of subintentions), or nil (by default the condition will be the current list of subintentions of the intention)")) },

			doc = @doc(value = "puts the current intention on hold until the specified condition is reached or all subintentions are reached (not in desire base anymore).", returns = "true if it is in the base.", examples = {
					@example("") }))
	public Boolean primOnHoldIntention(final IScope scope) throws GamaRuntimeException {
		final Predicate predicate = currentIntention(scope);
		final Object until = scope.hasArg("until") ? scope.getArg("until", IType.NONE) : null;
		if (predicate != null) {
			if (until == null) {
				final List<Predicate> subintention = predicate.subintentions;
				if (subintention != null && !subintention.isEmpty()) {
					predicate.onHoldUntil = subintention;
				}
			} else {
				if (predicate.onHoldUntil == null) {
					predicate.onHoldUntil = GamaListFactory.create(Types.get(PredicateType.id));
				}
				if (predicate.getSubintentions() == null) {
					predicate.subintentions = GamaListFactory.create(Types.get(PredicateType.id));
				}
				((Predicate) until).setSuperIntention(predicate);
				predicate.onHoldUntil.add((Predicate) until);
				predicate.getSubintentions().add((Predicate) until);
				addToBase(scope, (Predicate) until, DESIRE_BASE);
			}
		}
		return true;
	}

	@action(name = "add_subintention", args = {
			@arg(name = PREDICATE, type = PredicateType.id, optional = false, doc = @doc("predicate name")),
			@arg(name = PREDICATE_SUBINTENTIONS, type = PredicateType.id, optional = false, doc = @doc("the subintention to add to the predicate")),
			@arg(name = "add_as_desire", type = IType.BOOL, optional = true, doc = @doc("add the subintention as a desire as well (by default, false) ")) }, doc = @doc(value = "adds the predicates is in the desire base.", returns = "true if it is in the base.", examples = {
					@example("") }))
	public Boolean addSubIntention(final IScope scope) throws GamaRuntimeException {
		final Predicate predicate = (Predicate) (scope.hasArg(PREDICATE) ? scope.getArg(PREDICATE, PredicateType.id)
				: null);
		final Predicate subpredicate = (Predicate) (scope.hasArg(PREDICATE_SUBINTENTIONS)
				? scope.getArg(PREDICATE_SUBINTENTIONS, PredicateType.id) : null);

		if (predicate == null || subpredicate == null) {
			return false;
		}
		final Boolean addAsDesire = (Boolean) (scope.hasArg("add_as_desire") ? scope.getArg("add_as_desire", IType.BOOL)
				: false);

		if (predicate.getSubintentions() == null) {
			predicate.subintentions = GamaListFactory.create(Types.get(PredicateType.id));
		}
		subpredicate.setSuperIntention(predicate);
		predicate.getSubintentions().add(subpredicate);
		if (addAsDesire) {
			addToBase(scope, subpredicate, DESIRE_BASE);
		}
		return true;
	}

	public static Boolean addDesire(final IScope scope, final Predicate superPredicate, final Predicate predicate) {
		// Faire un test pour ne pas rajouter deux fois le m�me d�sire
		if (superPredicate != null) {
			if (superPredicate.getSubintentions() == null) {
				superPredicate.subintentions = GamaListFactory.create(Types.get(PredicateType.id));
			}
			superPredicate.getSubintentions().add(predicate);
		}
		addToBase(scope, predicate, DESIRE_BASE);
		predicate.setSuperIntention(superPredicate);
		return false;
	}

	@action(name = "add_desire", args = {
			@arg(name = PREDICATE, type = PredicateType.id, optional = false, doc = @doc("predicate to add")),
			@arg(name = PREDICATE_TODO, type = PredicateType.id, optional = true, doc = @doc("add the desire as a subintention of this parameter")) }, doc = @doc(value = "adds the predicates is in the desire base.", returns = "true if it is in the base.", examples = {
					@example("") }))
	public Boolean primAddDesire(final IScope scope) throws GamaRuntimeException {
		final Predicate predicateDirect = (Predicate) (scope.hasArg(PREDICATE)
				? scope.getArg(PREDICATE, PredicateType.id) : null);
		if (predicateDirect != null) {
			final Predicate superpredicate = (Predicate) (scope.hasArg(PREDICATE_TODO)
					? scope.getArg(PREDICATE_TODO, PredicateType.id) : null);
			return addDesire(scope, superpredicate, predicateDirect);
		}

		return false;
	}

	@action(name = "get_desire", args = {
			@arg(name = PREDICATE, type = PredicateType.id, optional = false, doc = @doc("predicate to check")) }, doc = @doc(value = "get the predicates is in the desire base (if several, returns the first one).", returns = "the predicate if it is in the base.", examples = {
					@example("get_desire(new_predicate(\"has_water\", true))") }))
	// @args(names = { PREDICATE_NAME, PREDICATE_PARAMETERS })
	public Predicate getDesire(final IScope scope) throws GamaRuntimeException {
		final Predicate predicateDirect = (Predicate) (scope.hasArg(PREDICATE)
				? scope.getArg(PREDICATE, PredicateType.id) : null);
		if (predicateDirect != null) {
			for (final Predicate pred : getBase(scope, DESIRE_BASE)) {
				if (predicateDirect.equals(pred)) {
					return pred;
				}
			}
		}
		return null;
	}

	@action(name = "get_desires", args = {
			@arg(name = PREDICATE, type = PredicateType.id, optional = false, doc = @doc("name of the predicates to check")) }, doc = @doc(value = "get the list of predicates is in the belief base", returns = "the list of predicates.", examples = {
					@example("get_desires(\"has_water\")") }))
	public IList<Predicate> getDesires(final IScope scope) throws GamaRuntimeException {
		final Predicate predicateDirect = (Predicate) (scope.hasArg(PREDICATE)
				? scope.getArg(PREDICATE, PredicateType.id) : null);
		final IList<Predicate> predicates = GamaListFactory.create();
		if (predicateDirect != null) {
			for (final Predicate pred : getBase(scope, DESIRE_BASE)) {
				if (predicateDirect.equals(pred)) {
					predicates.add(pred);
				}
			}
		}
		return predicates;
	}

	@action(name = "get_desire_with_name", args = {
			@arg(name = "name", type = IType.STRING, optional = false, doc = @doc("name of the predicate to check")) }, doc = @doc(value = "get the predicates is in the belief base (if several, returns the first one).", returns = "the predicate if it is in the base.", examples = {
					@example("get_desire_with_name(\"has_water\")") }))
	public Predicate getDesireName(final IScope scope) throws GamaRuntimeException {
		final String predicateName = (String) (scope.hasArg("name") ? scope.getArg("name", IType.STRING) : null);
		if (predicateName != null) {
			for (final Predicate pred : getBase(scope, DESIRE_BASE)) {
				if (predicateName.equals(pred.getName())) {
					return pred;
				}
			}
		}
		return null;
	}

	@action(name = "get_desires_with_name", args = {
			@arg(name = "name", type = IType.STRING, optional = false, doc = @doc("name of the predicates to check")) }, doc = @doc(value = "get the list of predicates is in the belief base with the given name.", returns = "the list of predicates.", examples = {
					@example("get_belief(\"has_water\")") }))
	public List<Predicate> getDesiresName(final IScope scope) throws GamaRuntimeException {
		final String predicateName = (String) (scope.hasArg("name") ? scope.getArg("name", IType.STRING) : null);
		final List<Predicate> predicates = GamaListFactory.create();
		if (predicateName != null) {
			for (final Predicate pred : getBase(scope, DESIRE_BASE)) {
				if (predicateName.equals(pred.getName())) {
					predicates.add(pred);
				}
			}
		}
		return predicates;
	}

	public static Boolean removeBelief(final IScope scope, final Predicate pred) {
		return getBase(scope, BELIEF_BASE).remove(pred);
	}

	@action(name = "remove_belief", args = {
			@arg(name = PREDICATE, type = PredicateType.id, optional = true, doc = @doc("predicate to remove")) }, doc = @doc(value = "removes the predicate from the belief base.", returns = "true if it is in the base.", examples = {
					@example("") }))
	public Boolean primRemoveBelief(final IScope scope) throws GamaRuntimeException {
		final Predicate predicateDirect = (Predicate) (scope.hasArg(PREDICATE)
				? scope.getArg(PREDICATE, PredicateType.id) : null);
		if (predicateDirect != null) {
			return removeBelief(scope, predicateDirect);
		}
		return false;
	}

	@action(name = "replace_belief", args = {
			@arg(name = "old_predicate", type = PredicateType.id, optional = false, doc = @doc("predicate to remove")),
			@arg(name = PREDICATE, type = PredicateType.id, optional = false, doc = @doc("predicate to add")) }, doc = @doc(value = "replace the old predicate by the new one.", returns = "true if the old predicate is in the base.", examples = {
					@example("") }))
	public Boolean primPlaceBelief(final IScope scope) throws GamaRuntimeException {
		final Predicate oldPredicate = (Predicate) (scope.hasArg("old_predicate")
				? scope.getArg("old_predicate", PredicateType.id) : null);
		boolean ok = true;
		if (oldPredicate != null) {
			ok = getBase(scope, BELIEF_BASE).remove(oldPredicate);
		} else {
			ok = false;
		}
		final Predicate newPredicate = (Predicate) (scope.hasArg(PREDICATE) ? scope.getArg(PREDICATE, PredicateType.id)
				: null);
		if (newPredicate != null) {
			// Predicate current_intention = currentIntention(scope);
			if (getBase(scope, SimpleBdiArchitecture.INTENTION_BASE).contains(newPredicate)) {
				removeFromBase(scope, newPredicate, DESIRE_BASE);
				removeFromBase(scope, newPredicate, INTENTION_BASE);
			}
			if (getBase(scope, SimpleBdiArchitecture.DESIRE_BASE).contains(newPredicate)) {
				removeFromBase(scope, newPredicate, DESIRE_BASE);
			}
			for (final Object statement : getBase(scope, SimpleBdiArchitecture.INTENTION_BASE)) {
				final List<Predicate> statementSubintention = ((Predicate) statement).getSubintentions();
				if (statementSubintention != null) {
					if (statementSubintention.contains(newPredicate)) {
						statementSubintention.remove(newPredicate);
					}
				}
				final List<Predicate> statementOnHoldUntil = ((Predicate) statement).getOnHoldUntil();
				if (statementOnHoldUntil != null) {
					if (statementOnHoldUntil.contains(newPredicate)) {
						statementOnHoldUntil.remove(newPredicate);
					}
				}
			}
			return addToBase(scope, newPredicate, BELIEF_BASE);
		}
		return ok;
	}

	public static Boolean removeDesire(final IScope scope, final Predicate pred) {
		getBase(scope, DESIRE_BASE).remove(pred);
		getBase(scope, INTENTION_BASE).remove(pred);
		for (final Object statement : getBase(scope, SimpleBdiArchitecture.INTENTION_BASE)) {
			final List<Predicate> statementSubintention = ((Predicate) statement).getSubintentions();
			if (statementSubintention != null) {
				if (statementSubintention.contains(pred)) {
					statementSubintention.remove(pred);
				}
			}
			final List<Predicate> statementOnHoldUntil = ((Predicate) statement).getOnHoldUntil();
			if (statementOnHoldUntil != null) {
				if (statementOnHoldUntil.contains(pred)) {
					statementOnHoldUntil.remove(pred);
				}
			}
		}
		return true;
	}

	@action(name = "remove_desire", args = {
			@arg(name = PREDICATE, type = PredicateType.id, optional = true, doc = @doc("predicate to add")) }, doc = @doc(value = "removes the predicates from the desire base.", returns = "true if it is in the base.", examples = {
					@example("") }))
	public Boolean primRemoveDesire(final IScope scope) throws GamaRuntimeException {
		final Predicate predicateDirect = (Predicate) (scope.hasArg(PREDICATE)
				? scope.getArg(PREDICATE, PredicateType.id) : null);
		if (predicateDirect != null) {
			return removeDesire(scope, predicateDirect);
		}
		return false;
	}

	@action(name = "add_intention", args = {
			@arg(name = PREDICATE, type = IType.MAP, optional = true, doc = @doc("predicate to check")) }, doc = @doc(value = "check if the predicates is in the desire base.", returns = "true if it is in the base.", examples = {
					@example("") }))
	// @args(names = { PREDICATE_NAME, PREDICATE_PARAMETERS })
	public Boolean primAddIntention(final IScope scope) throws GamaRuntimeException {
		final Predicate predicateDirect = (Predicate) (scope.hasArg(PREDICATE)
				? scope.getArg(PREDICATE, PredicateType.id) : null);
		return addToBase(scope, predicateDirect, INTENTION_BASE);

	}

	@action(name = "get_intention", args = {
			@arg(name = PREDICATE, type = PredicateType.id, optional = false, doc = @doc("predicate to check")) }, doc = @doc(value = "get the predicates is in the belief base (if several, returns the first one).", returns = "the predicate if it is in the base.", examples = {
					@example("get_belief(new_predicate(\"has_water\", true))") }))
	// @args(names = { PREDICATE_NAME, PREDICATE_PARAMETERS })
	public Predicate getIntention(final IScope scope) throws GamaRuntimeException {
		final Predicate predicateDirect = (Predicate) (scope.hasArg(PREDICATE)
				? scope.getArg(PREDICATE, PredicateType.id) : null);
		if (predicateDirect != null) {
			for (final Predicate pred : getBase(scope, INTENTION_BASE)) {
				if (predicateDirect.equals(pred)) {
					return pred;
				}
			}
		}
		return null;
	}

	@action(name = "get_intentions", args = {
			@arg(name = PREDICATE, type = PredicateType.id, optional = false, doc = @doc("name of the predicates to check")) }, doc = @doc(value = "get the list of predicates is in the belief base", returns = "the list of predicates.", examples = {
					@example("get_belief(\"has_water\")") }))
	public IList<Predicate> getIntentions(final IScope scope) throws GamaRuntimeException {
		final Predicate predicateDirect = (Predicate) (scope.hasArg(PREDICATE)
				? scope.getArg(PREDICATE, PredicateType.id) : null);
		final IList<Predicate> predicates = GamaListFactory.create();
		if (predicateDirect != null) {
			for (final Predicate pred : getBase(scope, INTENTION_BASE)) {
				if (predicateDirect.equals(pred)) {
					predicates.add(pred);
				}
			}
		}
		return predicates;
	}

	@action(name = "get_intention_with_name", args = {
			@arg(name = "name", type = IType.STRING, optional = false, doc = @doc("name of the predicate to check")) }, doc = @doc(value = "get the predicates is in the belief base (if several, returns the first one).", returns = "the predicate if it is in the base.", examples = {
					@example("get_intention_with_name(\"has_water\")") }))
	public Predicate getIntentionName(final IScope scope) throws GamaRuntimeException {
		final String predicateName = (String) (scope.hasArg("name") ? scope.getArg("name", IType.STRING) : null);
		if (predicateName != null) {
			for (final Predicate pred : getBase(scope, INTENTION_BASE)) {
				if (predicateName.equals(pred.getName())) {
					return pred;
				}
			}
		}
		return null;
	}

	@action(name = "get_intentions_with_name", args = {
			@arg(name = "name", type = IType.STRING, optional = false, doc = @doc("name of the predicates to check")) }, doc = @doc(value = "get the list of predicates is in the belief base with the given name.", returns = "the list of predicates.", examples = {
					@example("get_belief(\"has_water\")") }))
	public List<Predicate> getIntentionsName(final IScope scope) throws GamaRuntimeException {
		final String predicateName = (String) (scope.hasArg("name") ? scope.getArg("name", IType.STRING) : null);
		final List<Predicate> predicates = GamaListFactory.create();
		if (predicateName != null) {
			for (final Predicate pred : getBase(scope, INTENTION_BASE)) {
				if (predicateName.equals(pred.getName())) {
					predicates.add(pred);
				}
			}
		}
		return predicates;
	}

	public static Boolean removeIntention(final IScope scope, final Predicate pred) {
		getBase(scope, INTENTION_BASE).remove(pred);
		for (final Object statement : getBase(scope, SimpleBdiArchitecture.INTENTION_BASE)) {
			final List<Predicate> statementSubintention = ((Predicate) statement).getSubintentions();
			if (statementSubintention != null) {
				if (statementSubintention.contains(pred)) {
					statementSubintention.remove(pred);
				}
			}
			final List<Predicate> statementOnHoldUntil = ((Predicate) statement).getOnHoldUntil();
			if (statementOnHoldUntil != null) {
				if (statementOnHoldUntil.contains(pred)) {
					statementOnHoldUntil.remove(pred);
				}
			}
		}
		return true;
	}

	@action(name = "remove_intention", args = {
			@arg(name = PREDICATE, type = PredicateType.id, optional = true, doc = @doc("predicate to add")),
			@arg(name = REMOVE_DESIRE_AND_INTENTION, type = IType.BOOL, optional = false, doc = @doc("removes also desire")) }, doc = @doc(value = "removes the predicates from the desire base.", returns = "true if it is in the base.", examples = {
					@example("") }))
	public Boolean primRemoveIntention(final IScope scope) throws GamaRuntimeException {

		final Predicate predicateDirect = (Predicate) (scope.hasArg(PREDICATE)
				? scope.getArg(PREDICATE, PredicateType.id) : null);
		if (predicateDirect != null) {
			final Boolean dodesire = scope.hasArg(REMOVE_DESIRE_AND_INTENTION)
					? scope.getBoolArg(REMOVE_DESIRE_AND_INTENTION) : false;
			getBase(scope, INTENTION_BASE).remove(predicateDirect);
			if (dodesire) {
				getBase(scope, DESIRE_BASE).remove(predicateDirect);
			}
			if (predicateDirect.equals(currentIntention(scope)))
				scope.getAgent().setAttribute(CURRENT_PLAN, null);
			for (final Object statement : getBase(scope, SimpleBdiArchitecture.INTENTION_BASE)) {
				final List<Predicate> statementSubintention = ((Predicate) statement).getSubintentions();
				if (statementSubintention != null) {
					if (statementSubintention.contains(predicateDirect)) {
						statementSubintention.remove(predicateDirect);
					}
				}
				final List<Predicate> statementOnHoldUntil = ((Predicate) statement).getOnHoldUntil();
				if (statementOnHoldUntil != null) {
					if (statementOnHoldUntil.contains(predicateDirect)) {
						statementOnHoldUntil.remove(predicateDirect);
					}
				}
			}

			return true;
		}

		return false;
	}

	@action(name = "clear_beliefs", doc = @doc(value = "clear the belief base", returns = "true if the base is cleared correctly", examples = {
			@example("") }))
	public Boolean primClearBelief(final IScope scope) {
		getBase(scope, BELIEF_BASE).clear();
		return true;
	}

	@action(name = "clear_desires", doc = @doc(value = "clear the desire base", returns = "true if the base is cleared correctly", examples = {
			@example("") }))
	public Boolean primClearDesire(final IScope scope) {
		getBase(scope, DESIRE_BASE).clear();
		return true;
	}

	@action(name = "clear_intentions", doc = @doc(value = "clear the intention base", returns = "true if the base is cleared correctly", examples = {
			@example("") }))
	public Boolean primClearIntention(final IScope scope) {
		getBase(scope, INTENTION_BASE).clear();
		scope.getAgent().setAttribute(CURRENT_PLAN, null);
		return true;
	}

	@action(name = "remove_all_beliefs", args = {
			@arg(name = PREDICATE, type = PredicateType.id, optional = true, doc = @doc("predicate to remove")) }, doc = @doc(value = "removes the predicates from the belief base.", returns = "true if it is in the base.", examples = {
					@example("") }))
	public Boolean primRemoveAllBelief(final IScope scope) throws GamaRuntimeException {
		final Predicate predicateDirect = (Predicate) (scope.hasArg(PREDICATE)
				? scope.getArg(PREDICATE, PredicateType.id) : null);
		if (predicateDirect != null) {
			getBase(scope, BELIEF_BASE).removeAllOccurrencesOfValue(scope, predicateDirect);
			return true;
		}
		return false;
	}

	protected void updateEmotionsIntensity(final IScope scope) {
		for (final Emotion emo : getEmotionBase(scope, SimpleBdiArchitecture.EMOTION_BASE)) {
			emo.decayIntensity();
		}
		for (final Emotion emo : listEmotionsNull(scope)) {
			removeFromBase(scope, emo, SimpleBdiArchitecture.EMOTION_BASE);
		}
	}

	protected void computeEmotions(final IScope scope) {
		// Etape 0, demander à l'utilisateur s'il veut ou non utiliser cette
		// architecture
		// Etape 1, créer les émotions par rapport à la cognition (modèle thèse
		// de Carole). Cette étape va être dissociée d'ici.
		final IAgent agent = getCurrentAgent(scope);
		final Boolean use_emotion_architecture = scope.hasArg(USE_EMOTIONS_ARCHITECTURE)
				? scope.getBoolArg(USE_EMOTIONS_ARCHITECTURE) : (Boolean) agent.getAttribute(USE_EMOTIONS_ARCHITECTURE);
		if (use_emotion_architecture) {
			createJoy(scope);
			createSadness(scope);
			createHope(scope);
			createFear(scope);
			createSatisfaction(scope);
			createFearConfirmed(scope);
			createRelief(scope);
			createDisappointment(scope);
		}
	}

	private void createJoy(final IScope scope) {
		// Simplement vérifier si l'agent possède à la fois la même croyance et
		// le même désir.
		for (final Predicate predTest : getBase(scope, SimpleBdiArchitecture.BELIEF_BASE)) {
			if (getBase(scope, SimpleBdiArchitecture.DESIRE_BASE).contains(predTest)) {
				final Emotion joy = new Emotion("joy", predTest);
				IAgent agentTest = predTest.getAgentCause();
				if(agentTest!=null){joy.setAgentCause(agentTest);}
				addEmotion(scope, joy);
			}
		}
	}

	private static void createJoyFromPredicate(final IScope scope, final Predicate predTest) {
		if (getBase(scope, SimpleBdiArchitecture.DESIRE_BASE).contains(predTest)) {
			final Emotion joy = new Emotion("joy", predTest);
			IAgent agentTest = predTest.getAgentCause();
			if(agentTest!=null){joy.setAgentCause(agentTest);}
			addEmotion(scope, joy);
		}
	}

	private void createSadness(final IScope scope) {
		// Lagent possède la croyance A et le désir nonA (et vice versa).
		// L'émotion est créée sur la croyance.
		// A améliorer en termes de rapidité de calcul
		for (final Predicate predTest : getBase(scope, SimpleBdiArchitecture.BELIEF_BASE)) {
			for (final Predicate desireTest : getBase(scope, SimpleBdiArchitecture.DESIRE_BASE)) {
				if (predTest.equalsButNotTruth(desireTest)) {
					final Emotion sadness = new Emotion("sadness", predTest);
					IAgent agentTest = predTest.getAgentCause();
					if(agentTest!=null){sadness.setAgentCause(agentTest);}
					addEmotion(scope, sadness);
				}
			}
		}
	}

	private void createFear(final IScope scope) {
		for (final Predicate predTest : getBase(scope, SimpleBdiArchitecture.UNCERTAINTY_BASE)) {
			for (final Predicate desireTest : getBase(scope, SimpleBdiArchitecture.DESIRE_BASE)) {
				if (predTest.equalsButNotTruth(desireTest)) {
					final Emotion fear = new Emotion("fear", predTest);
					IAgent agentTest = predTest.getAgentCause();
					if(agentTest!=null){fear.setAgentCause(agentTest);}
					addEmotion(scope, fear);
				}
			}
		}
	}

	private void createHope(final IScope scope) {
		for (final Predicate predTest : getBase(scope, SimpleBdiArchitecture.DESIRE_BASE)) {
			if (getBase(scope, SimpleBdiArchitecture.UNCERTAINTY_BASE).contains(predTest)) {
				final Emotion hope = new Emotion("hope", predTest);
				IAgent agentTest = predTest.getAgentCause();
				if(agentTest!=null){hope.setAgentCause(agentTest);}
				addEmotion(scope, hope);
			}
		}
	}

	private void createSatisfaction(final IScope scope) {
		final GamaList<Emotion> emoTemps = getEmotionBase(scope, EMOTION_BASE)
				.cloneWithContentType(getEmotionBase(scope, EMOTION_BASE).getType());
		for (final Emotion emo : emoTemps) {
			if (emo.getName().equals("hope")) {
				if (emo.getAbout() != null
						&& getBase(scope, SimpleBdiArchitecture.BELIEF_BASE).contains(emo.getAbout())) {
					Emotion satisfaction = null;
					Emotion joy = null;
					IAgent agentTest = emo.getAgentCause();
					if (emo.getNoIntensity()) {
						satisfaction = new Emotion("satisfaction", emo.getAbout());
						if(agentTest!=null){satisfaction.setAgentCause(agentTest);}
						joy = new Emotion("joy", emo.getAbout());
						if(agentTest!=null){joy.setAgentCause(agentTest);}
					} else {
						// On décide de transmettre l'intensité de l'émotion
						// précédente.
						satisfaction = new Emotion("satisfaction", emo.getIntensity(), emo.getAbout());
						if(agentTest!=null){satisfaction.setAgentCause(agentTest);}
						joy = new Emotion("joy", emo.getIntensity(), emo.getAbout());
						if(agentTest!=null){joy.setAgentCause(agentTest);}
					}
					addEmotion(scope, satisfaction);
					addEmotion(scope, joy);
					removeEmotion(scope, emo);
				}
			}
		}
	}

	private void createFearConfirmed(final IScope scope) {
		final GamaList<Emotion> emoTemps = getEmotionBase(scope, EMOTION_BASE)
				.cloneWithContentType(getEmotionBase(scope, EMOTION_BASE).getType());
		for (final Emotion emo : emoTemps) {
			if (emo.getName().equals("fear")) {
				if (emo.getAbout() != null
						&& getBase(scope, SimpleBdiArchitecture.BELIEF_BASE).contains(emo.getAbout())) {
					Emotion fearConfirmed = null;
					Emotion sadness = null;
					IAgent agentTest = emo.getAgentCause();
					if (emo.getNoIntensity()) {
						fearConfirmed = new Emotion("fear_confirmed", emo.getAbout());
						if(agentTest!=null){fearConfirmed.setAgentCause(agentTest);}
						sadness = new Emotion("sadness", emo.getAbout());
						if(agentTest!=null){sadness.setAgentCause(agentTest);}
					} else {
						// On décide de transmettre l'intensité de l'émotion
						// précédente.
						fearConfirmed = new Emotion("fearConfirmed", emo.getIntensity(), emo.getAbout());
						if(agentTest!=null){fearConfirmed.setAgentCause(agentTest);}
						sadness = new Emotion("sadness", emo.getIntensity(), emo.getAbout());
						if(agentTest!=null){sadness.setAgentCause(agentTest);}
					}
					addEmotion(scope, fearConfirmed);
					addEmotion(scope, sadness);
					removeEmotion(scope, emo);
				}
			}
		}
	}

	private void createRelief(final IScope scope) {
		final GamaList<Emotion> emoTemps = getEmotionBase(scope, EMOTION_BASE)
				.cloneWithContentType(getEmotionBase(scope, EMOTION_BASE).getType());
		for (final Emotion emo : emoTemps) {
			if (emo.getName().equals("fear")) {
				if (emo.getAbout() != null) {
					for (final Predicate beliefTest : getBase(scope, SimpleBdiArchitecture.BELIEF_BASE)) {
						if (emo.getAbout().equalsButNotTruth(beliefTest)) {
							Emotion relief = null;
							Emotion joy = null;
							IAgent agentTest = emo.getAgentCause();
							if (emo.getNoIntensity()) {
								relief = new Emotion("relief", beliefTest);
								if(agentTest!=null){relief.setAgentCause(agentTest);}
								joy = new Emotion("joy", beliefTest);
								if(agentTest!=null){joy.setAgentCause(agentTest);}
							} else {
								// On décide de transmettre l'intensité de
								// l'émotion précédente.
								relief = new Emotion("relief", emo.getIntensity(), emo.getAbout());
								if(agentTest!=null){relief.setAgentCause(agentTest);}
								joy = new Emotion("joy", emo.getIntensity(), emo.getAbout());
								if(agentTest!=null){joy.setAgentCause(agentTest);}
							}
							addEmotion(scope, relief);
							addEmotion(scope, joy);
							removeEmotion(scope, emo);
						}
					}
				}
			}
		}
	}

	private void createDisappointment(final IScope scope) {
		final GamaList<Emotion> emoTemps = getEmotionBase(scope, EMOTION_BASE)
				.cloneWithContentType(getEmotionBase(scope, EMOTION_BASE).getType());
		for (final Emotion emo : emoTemps) {
			if (emo.getName().equals("hope")) {
				if (emo.getAbout() != null) {
					for (final Predicate beliefTest : getBase(scope, SimpleBdiArchitecture.BELIEF_BASE)) {
						if (emo.getAbout().equalsButNotTruth(beliefTest)) {
							Emotion disappointment = null;
							Emotion sadness = null;
							IAgent agentTest = emo.getAgentCause();
							if (emo.getNoIntensity()) {
								disappointment = new Emotion("disappointment", beliefTest);
								if(agentTest!=null){disappointment.setAgentCause(agentTest);}
								sadness = new Emotion("sadness", beliefTest);
								if(agentTest!=null){sadness.setAgentCause(agentTest);}
							} else {
								// On décide de transmettre l'intensité de
								// l'émotion précédente.
								disappointment = new Emotion("disappointment", emo.getIntensity(), emo.getAbout());
								if(agentTest!=null){disappointment.setAgentCause(agentTest);}
								sadness = new Emotion("sadness", emo.getIntensity(), emo.getAbout());
								if(agentTest!=null){sadness.setAgentCause(agentTest);}
							}
							addEmotion(scope, disappointment);
							addEmotion(scope, sadness);
							removeEmotion(scope, emo);
						}
					}
				}
			}
		}
	}

	private List<Emotion> listEmotionsNull(final IScope scope) {
		final List<Emotion> tempPred = new ArrayList<Emotion>();
		for (final Emotion pred : getEmotionBase(scope, SimpleBdiArchitecture.EMOTION_BASE)) {
			if (pred.getIntensity() == 0) {
				tempPred.add(pred);
			}
		}
		return tempPred;
	}

	@action(name = "add_emotion", args = {
			@arg(name = EMOTION, type = EmotionType.id, optional = true, doc = @doc("emotion to add to the base")) }, doc = @doc(value = "add the emotion to the emotion base.", returns = "true if it is added in the base.", examples = {
					@example("") }))
	public Boolean primAddEmotion(final IScope scope) throws GamaRuntimeException {
		final Emotion emotionDirect = (Emotion) (scope.hasArg(EMOTION) ? scope.getArg(EMOTION, EmotionType.id) : null);
		return addEmotion(scope, emotionDirect);
	}

	public static boolean addEmotion(final IScope scope, final Emotion emo) {
		Emotion newEmo = emo;
		if (!emo.getNoIntensity() && hasEmotion(scope, emo)) {
			final Emotion oldEmo = getEmotion(scope, emo);
			if (!oldEmo.getNoIntensity()) {
				newEmo = new Emotion(emo.getName(), emo.getIntensity() + oldEmo.getIntensity(), emo.getAbout(),
						Math.min(emo.getDecay(), oldEmo.getDecay()),emo.getAgentCause());
			}
		}
		return addToBase(scope, newEmo, EMOTION_BASE);
	}

	@action(name = "has_emotion", args = {
			@arg(name = EMOTION, type = EmotionType.id, optional = true, doc = @doc("emotion to check")) }, doc = @doc(value = "check if the emotion is in the belief base.", returns = "true if it is in the base.", examples = {
					@example("") }))
	public Boolean primTestEmotion(final IScope scope) throws GamaRuntimeException {
		final Emotion emotionDirect = (Emotion) (scope.hasArg(EMOTION) ? scope.getArg(EMOTION, EmotionType.id) : null);
		if (emotionDirect != null) {
			return hasEmotion(scope, emotionDirect);
		}
		return false;
	}

	public static Boolean hasEmotion(final IScope scope, final Emotion emo) {
		return getEmotionBase(scope, EMOTION_BASE).contains(emo);
	}

	@action(name = "get_emotion", args = {
			@arg(name = EMOTION, type = EmotionType.id, optional = false, doc = @doc("emotion to get")) }, doc = @doc(value = "get the emotion in the emotion base (if several, returns the first one).", returns = "the emotion if it is in the base.", examples = {
					@example("get_belief(new_predicate(\"has_water\", true))") }))
	public Emotion getEmotion(final IScope scope) throws GamaRuntimeException {
		final Emotion emotionDirect = (Emotion) (scope.hasArg(EMOTION) ? scope.getArg(EMOTION, EmotionType.id) : null);
		if (emotionDirect != null) {
			for (final Emotion emo : getEmotionBase(scope, EMOTION_BASE)) {
				if (emotionDirect.equals(emo)) {
					return emo;
				}
			}
		}
		return null;
	}

	public static Emotion getEmotion(final IScope scope, final Emotion emotionDirect) {
		for (final Emotion emo : getEmotionBase(scope, EMOTION_BASE)) {
			if (emotionDirect.equals(emo)) {
				return emo;
			}
		}
		return null;
	}

	public static Boolean removeEmotion(final IScope scope, final Emotion emo) {
		return getEmotionBase(scope, EMOTION_BASE).remove(emo);
	}

	@action(name = "remove_emotion", args = {
			@arg(name = EMOTION, type = EmotionType.id, optional = true, doc = @doc("emotion to remove")) }, doc = @doc(value = "removes the emotion from the emotion base.", returns = "true if it is in the base.", examples = {
					@example("") }))
	public Boolean primRemoveEmotion(final IScope scope) throws GamaRuntimeException {
		final Emotion emotionDirect = (Emotion) (scope.hasArg(EMOTION) ? scope.getArg(EMOTION, EmotionType.id) : null);
		if (emotionDirect != null) {
			return removeEmotion(scope, emotionDirect);
		}
		return false;
	}

	// Peut-être mettre un replace emotion.

	// Réalisation des outils de manipulation des incertitudes
	public static Boolean addUncertainty(final IScope scope, final Predicate predicate) {
		if (getBase(scope, SimpleBdiArchitecture.BELIEF_BASE).contains(predicate)) {
			removeFromBase(scope, predicate, BELIEF_BASE);
		}
		return addToBase(scope, predicate, UNCERTAINTY_BASE);
	}

	@action(name = "add_uncertainty", args = {
			@arg(name = PREDICATE, type = IType.MAP, optional = true, doc = @doc("predicate to check")) }, doc = @doc(value = "add a predicate in the uncertainty base.", returns = "true it works.", examples = {
					@example("") }))
	public Boolean primAddUncertainty(final IScope scope) throws GamaRuntimeException {
		final Predicate predicateDirect = (Predicate) (scope.hasArg(PREDICATE)
				? scope.getArg(PREDICATE, PredicateType.id) : null);

		return addUncertainty(scope, predicateDirect);

	}

	@action(name = "get_uncertainty", args = {
			@arg(name = PREDICATE, type = PredicateType.id, optional = false, doc = @doc("predicate to check")) }, doc = @doc(value = "get the predicates is in the uncertainty base (if several, returns the first one).", returns = "the predicate if it is in the base.", examples = {
					@example("get_uncertainty(new_predicate(\"has_water\", true))") }))
	public Predicate getUncertainty(final IScope scope) throws GamaRuntimeException {
		final Predicate predicateDirect = (Predicate) (scope.hasArg(PREDICATE)
				? scope.getArg(PREDICATE, PredicateType.id) : null);
		if (predicateDirect != null) {
			for (final Predicate pred : getBase(scope, UNCERTAINTY_BASE)) {
				if (predicateDirect.equals(pred)) {
					return pred;
				}
			}
		}
		return null;
	}

	public static Boolean hasUncertainty(final IScope scope, final Predicate predicateDirect) {
		return getBase(scope, UNCERTAINTY_BASE).contains(predicateDirect);
	}

	@action(name = "has_uncertainty", args = {
			@arg(name = PREDICATE, type = PredicateType.id, optional = true, doc = @doc("predicate to check")) }, doc = @doc(value = "check if the predicates is in the uncertainty base.", returns = "true if it is in the base.", examples = {
					@example("") }))
	public Boolean primTestUncertainty(final IScope scope) throws GamaRuntimeException {
		final Predicate predicateDirect = (Predicate) (scope.hasArg(PREDICATE)
				? scope.getArg(PREDICATE, PredicateType.id) : null);
		if (predicateDirect != null) {
			return hasUncertainty(scope, predicateDirect);
		}
		return false;
	}

	public static Boolean removeUncertainty(final IScope scope, final Predicate pred) {
		return getBase(scope, UNCERTAINTY_BASE).remove(pred);
	}

	@action(name = "remove_uncertainty", args = {
			@arg(name = PREDICATE, type = PredicateType.id, optional = true, doc = @doc("predicate to add")) }, doc = @doc(value = "removes the predicates from the desire base.", returns = "true if it is in the base.", examples = {
					@example("") }))
	public Boolean primRemoveUncertainty(final IScope scope) throws GamaRuntimeException {
		final Predicate predicateDirect = (Predicate) (scope.hasArg(PREDICATE)
				? scope.getArg(PREDICATE, PredicateType.id) : null);
		if (predicateDirect != null) {
			return removeUncertainty(scope, predicateDirect);
		}
		return false;
	}

	// Peut-être mettre après un replace Uncertainty
	
	@action(name = "add_social_link", args = {
			@arg(name = SOCIALLINK, type = SocialLinkType.id, optional = true, doc = @doc("social link to add to the base")) }, doc = @doc(value = "add the social link to the social link base.", returns = "true if it is added in the base.", examples = {
					@example("") }))
	public Boolean primAddSocialLink(final IScope scope) throws GamaRuntimeException {
		final SocialLink social = (SocialLink) (scope.hasArg(SOCIALLINK) ? scope.getArg(SOCIALLINK, SocialLinkType.id) : null);
		return addSocialLink(scope, social);
	}

	public static boolean addSocialLink(final IScope scope, final SocialLink social) {
		if(social.getLiking()>=-1.0 && social.getLiking()<=1.0){
			if(social.getDominance()>=-1.0 && social.getDominance()<=1.0){
				if(social.getSolidarity()>=0.0 && social.getSolidarity()<=1.0){
					if(social.getFamiliarity()>=0.0 && social.getFamiliarity()<=1.0){
						if(getSocialLink(scope,social)==null){
							return addToBase(scope, social, SOCIALLINK_BASE);
						}
					}
				}
			}
		}
		return false;
	}

	@action(name = "get_social_link", args = {
			@arg(name = SOCIALLINK, type = SocialLinkType.id, optional = false, doc = @doc("social link to check")) }, doc = @doc(value = "get the social linke (if several, returns the first one).", returns = "the social link if it is in the base.", examples = {
					@example("get_social_link(new_social_link(agentA))") }))
	public SocialLink getSocialLink(final IScope scope) throws GamaRuntimeException {
		final SocialLink socialDirect = (SocialLink) (scope.hasArg(SOCIALLINK) ? scope.getArg(SOCIALLINK, SocialLinkType.id) : null);
		if (socialDirect != null) {
			return getSocialLink(scope,socialDirect);
		}
		return null;
	}
	
	public static SocialLink getSocialLink(final IScope scope, final SocialLink social) {
		for (final SocialLink socialLink : getSocialBase(scope, SOCIALLINK_BASE)) {
			if (socialLink.equals(social)) {
				return socialLink;
			}
			if (socialLink.equalsInAgent(social)) {
				return socialLink;
			}
		}
		return null;
	}
	
	public static Boolean hasSocialLink(final IScope scope, final SocialLink socialDirect) {
		return getSocialBase(scope, SOCIALLINK_BASE).contains(socialDirect);
	}
	
	@action(name = "has_social_link", args = {
			@arg(name = SOCIALLINK, type = SocialLinkType.id, optional = true, doc = @doc("social link to check")) }, doc = @doc(value = "check if the social link base.", returns = "true if it is in the base.", examples = {
					@example("") }))
	public Boolean primTestSocial(final IScope scope) throws GamaRuntimeException {
		final SocialLink socialDirect = (SocialLink) (scope.hasArg(SOCIALLINK)
				? scope.getArg(SOCIALLINK, SocialLinkType.id) : null);
		if (socialDirect != null) {
			return hasSocialLink(scope, socialDirect);
		}
		return false;
	}
	
	protected void updateSocialLinks(final IScope scope) {
		// Etape 0, demander à l'utilisateur s'il veut ou non utiliser cette
		// architecture
		// Etape 1, mettre à jour les relations sociales par rapport aux états mentaux de l'agent
		final IAgent agent = getCurrentAgent(scope);
		final Boolean use_social_architecture = scope.hasArg(USE_SOCIAL_ARCHITECTURE)
				? scope.getBoolArg(USE_SOCIAL_ARCHITECTURE) : (Boolean) agent.getAttribute(USE_SOCIAL_ARCHITECTURE);
		if (use_social_architecture) {
			for(SocialLink tempLink : getSocialBase(scope,SOCIALLINK_BASE)){
				updateSocialLink(scope,tempLink);
			}
		}
	}
	
	private void updateSocialLink(IScope scope, SocialLink social){
		updateAppreciation(scope,social);
		updateDominance(scope,social);
		updateSolidarity(scope,social);
		updateFamiliarity(scope,social);
	}
	
	private void updateAppreciation(IScope scope, SocialLink social){
		IAgent agentCause = social.getAgent();
		Double tempPositif = 0.0;
		Double tempNegatif = 0.0;
		Double appreciationModif = social.getLiking();
		for(Emotion emo : getEmotionBase(scope,EMOTION_BASE)){
			if(emo.getAgentCause()!=null && emo.getAgentCause().equals(agentCause)){
				if (emo.getName().equals("joy") || emo.getName().equals("hope")){
					tempPositif = tempPositif + 1.0;
				}
				if (emo.getName().equals("sadness") || emo.getName().equals("fear")){
					tempNegatif = tempNegatif + 1.0;
				}
			}
		}
		appreciationModif = appreciationModif +0.1*tempPositif-0.1*tempNegatif;
		if(appreciationModif>1.0){
			appreciationModif = 1.0;
		}
		if(appreciationModif<-1.0){
			appreciationModif = -1.0;
		}
		social.setLiking(appreciationModif);
	}
	
	private void updateDominance(IScope scope, SocialLink social){
		IAgent agentCause = social.getAgent();
		IScope scopeAgentCause = null;
		if (agentCause != null) {
			scopeAgentCause = agentCause.getScope().copy("in SimpleBdiArchitecture");
			scopeAgentCause.push(agentCause);
		}
		IAgent currentAgent = scope.getAgent();
		Double tempPositif = 0.0;
		Double tempNegatif = 0.0;
		Double dominanceModif = social.getDominance();
		for(Emotion emo : getEmotionBase(scope,EMOTION_BASE)){
			if(emo.getAgentCause()!=null && emo.getAgentCause().equals(agentCause)){
				if (emo.getName().equals("sadness") || emo.getName().equals("fear")){
					tempNegatif = tempNegatif + 1.0;
				}
			}
		}
		for(Emotion emo : getEmotionBase(scopeAgentCause,EMOTION_BASE)){
			if(emo.getAgentCause() != null && emo.getAgentCause().equals(currentAgent)){
				if (emo.getName().equals("sadness") || emo.getName().equals("fear")){
					tempPositif = tempPositif + 1.0;
				}
			}
		}
		dominanceModif = dominanceModif +0.1*tempPositif-0.1*tempNegatif;
		if(dominanceModif>1.0){
			dominanceModif = 1.0;
		}
		if(dominanceModif<-1.0){
			dominanceModif = -1.0;
		}
		social.setDominance(dominanceModif);
		GAMA.releaseScope(scopeAgentCause);
	}
	
	private void updateSolidarity(IScope scope, SocialLink social){
		IAgent agentCause = social.getAgent();
		IScope scopeAgentCause = null;
		if (agentCause != null) {
			scopeAgentCause = agentCause.getScope().copy("in SimpleBdiArchitecture");
			scopeAgentCause.push(agentCause);
		}
		Double tempPositif = 0.0;
		Double tempNegatif = 0.0;
		Double solidarityModif = social.getSolidarity();
		for(Emotion emo : getEmotionBase(scope,EMOTION_BASE)){
			if(emo.getAgentCause()!=null && emo.getAgentCause().equals(agentCause)){
				if (emo.getName().equals("sadness") || emo.getName().equals("fear")){
					tempNegatif = tempNegatif + 1.0;
				}
			}
		}
		for (final Predicate predTest1 : getBase(scope, SimpleBdiArchitecture.BELIEF_BASE)) {
			for (final Predicate predTest2 : getBase(scopeAgentCause, SimpleBdiArchitecture.BELIEF_BASE)) {
				if(predTest1.equals(predTest2)){
					tempPositif = tempPositif + 1.0;
				}			
				if (predTest1.equalsButNotTruth(predTest2)) {
					tempNegatif = tempNegatif + 1.0;
				}
			}
		}
		for (final Predicate predTest1 : getBase(scope, SimpleBdiArchitecture.DESIRE_BASE)) {
			for (final Predicate predTest2 : getBase(scopeAgentCause, SimpleBdiArchitecture.DESIRE_BASE)) {
				if(predTest1.equals(predTest2)){
					tempPositif = tempPositif + 1.0;
				}			
				if (predTest1.equalsButNotTruth(predTest2)) {
					tempNegatif = tempNegatif + 1.0;
				}
			}
		}
		for (final Predicate predTest1 : getBase(scope, SimpleBdiArchitecture.UNCERTAINTY_BASE)) {
			for (final Predicate predTest2 : getBase(scopeAgentCause, SimpleBdiArchitecture.UNCERTAINTY_BASE)) {
				if(predTest1.equals(predTest2)){
					tempPositif = tempPositif + 1.0;
				}			
				if (predTest1.equalsButNotTruth(predTest2)) {
					tempNegatif = tempNegatif + 1.0;
				}
			}
		}
		
		solidarityModif = solidarityModif +0.1*tempPositif-0.1*tempNegatif;
		if(solidarityModif>1.0){
			solidarityModif = 1.0;
		}
		if(solidarityModif<0.0){
			solidarityModif = 0.0;
		}
		social.setSolidarity(solidarityModif);
		GAMA.releaseScope(scopeAgentCause);
	}
	
	private void updateFamiliarity(IScope scope, SocialLink social){
		Double familiarityModif = social.getFamiliarity();
		familiarityModif = familiarityModif*(1+social.getLiking());
		if(familiarityModif>1.0){
			familiarityModif = 1.0;
		}
		if(familiarityModif<0.0){
			familiarityModif = 0.0;
		}
		if(social.getFamiliarity()==0.0){
			familiarityModif = 0.1;
		}
		social.setFamiliarity(familiarityModif);
	}
	
	@Override
	public boolean init(final IScope scope) throws GamaRuntimeException {
		super.init(scope);
		// _consideringScope = scope;
		return true;
	}

	@Override
	public void verifyBehaviors(final ISpecies context) {
	}

}
