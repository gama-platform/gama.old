/*********************************************************************************************
 *
 *
 * 'CopingStatement.java', in plugin 'msi.gaml.architecture.simplebdi', is part of the source code of the GAMA modeling
 * and simulation platform. (c) 2007-2019 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/


package msi.gaml.architecture.simplebdi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.System;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;

@symbol (
		name = CopingStatement.COPING,
		kind = ISymbolKind.SINGLE_STATEMENT,
		with_sequence = false,
		concept = { IConcept.BDI })
@inside (
		symbols = { SimpleBdiArchitecture.SIMPLE_BDI, SimpleBdiArchitectureParallel.PARALLEL_BDI },
		kinds = { ISymbolKind.SPECIES, ISymbolKind.MODEL })
@facets (
		value = { @facet (
				name = CopingStatement.BELIEF,
				type = PredicateType.id,
				optional = true,
				doc = @doc ("The mandatory belief")),
				@facet (
						name = CopingStatement.DESIRE,
						type = PredicateType.id,
						optional = true,
						doc = @doc ("The mandatory desire")),
				@facet (
						name = CopingStatement.EMOTION,
						type = EmotionType.id,
						optional = true,
						doc = @doc ("The mandatory emotion")),
				@facet (
						name = CopingStatement.UNCERTAINTY,
						type = PredicateType.id,
						optional = true,
						doc = @doc ("The mandatory uncertainty")),
				@facet (
						name = CopingStatement.IDEAL,
						type = PredicateType.id,
						optional = true,
						doc = @doc ("The mandatory ideal")),
				@facet (
						name = CopingStatement.OBLIGATION,
						type = PredicateType.id,
						optional = true,
						doc = @doc ("The mandatory obligation")),
				@facet (
						name = CopingStatement.DESIRES,
						type = IType.LIST,
						of = PredicateType.id,
						optional = true,
						doc = @doc ("The mandatory desires")),
				@facet (
						name = CopingStatement.BELIEFS,
						type = IType.LIST,
						of = PredicateType.id,
						optional = true,
						doc = @doc ("The mandatory beliefs")),
				@facet (
						name = CopingStatement.EMOTIONS,
						type = IType.LIST,
						of = EmotionType.id,
						optional = true,
						doc = @doc ("The mandatory emotions")),
				@facet (
						name = CopingStatement.UNCERTAINTIES,
						type = IType.LIST,
						of = PredicateType.id,
						optional = true,
						doc = @doc ("The mandatory uncertainties")),
				@facet (
						name = CopingStatement.IDEALS,
						type = IType.LIST,
						of = PredicateType.id,
						optional = true,
						doc = @doc ("The mandatory ideals")),
				@facet (
						name = CopingStatement.OBLIGATIONS,
						type = IType.LIST,
						of = PredicateType.id,
						optional = true,
						doc = @doc ("The mandatory obligations")),
				@facet (
						name = CopingStatement.NEW_DESIRE,
						type = PredicateType.id,
						optional = true,
						doc = @doc ("The desire that will be added")),
				@facet (
						name = CopingStatement.NEW_BELIEF,
						type = PredicateType.id,
						optional = true,
						doc = @doc ("The belief that will be added")),
				@facet (
						name = CopingStatement.NEW_EMOTION,
						type = EmotionType.id,
						optional = true,
						doc = @doc ("The emotion that will be added")),
				@facet (
						name = CopingStatement.NEW_UNCERTAINTY,
						type = PredicateType.id,
						optional = true,
						doc = @doc ("The uncertainty that will be added")),
				@facet (
						name = CopingStatement.NEW_IDEAL,
						type = PredicateType.id,
						optional = true,
						doc = @doc ("The ideal that will be added")),
				@facet (
						name = CopingStatement.NEW_DESIRES,
						type = IType.LIST,
						of = PredicateType.id,
						optional = true,
						doc = @doc ("The desire that will be added")),
				@facet (
						name = CopingStatement.NEW_BELIEFS,
						type = IType.LIST,
						of = PredicateType.id,
						optional = true,
						doc = @doc ("The belief that will be added")),
				@facet (
						name = CopingStatement.NEW_EMOTIONS,
						type = IType.LIST,
						of = EmotionType.id,
						optional = true,
						doc = @doc ("The emotion that will be added")),
				@facet (
						name = CopingStatement.NEW_UNCERTAINTIES,
						type = IType.LIST,
						of = PredicateType.id,
						optional = true,
						doc = @doc ("The uncertainty that will be added")),
				@facet (
						name = CopingStatement.NEW_IDEALS,
						type = IType.LIST,
						of = PredicateType.id,
						optional = true,
						doc = @doc ("The ideals that will be added")),
				@facet (
						name = CopingStatement.REMOVE_BELIEFS,
						type = IType.LIST,
						of = PredicateType.id,
						optional = true,
						doc = @doc ("The belief that will be removed")),
				@facet (
						name = CopingStatement.REMOVE_DESIRES,
						type = IType.LIST,
						of = PredicateType.id,
						optional = true,
						doc = @doc ("The desire that will be removed")),
				@facet (
						name = CopingStatement.REMOVE_EMOTIONS,
						type = IType.LIST,
						of = EmotionType.id,
						optional = true,
						doc = @doc ("The emotion that will be removed")),
				@facet (
						name = CopingStatement.REMOVE_IDEALS,
						type = IType.LIST,
						of = PredicateType.id,
						optional = true,
						doc = @doc ("The ideals that will be removed")),
				@facet (
						name = CopingStatement.REMOVE_OBLIGATIONS,
						type = IType.LIST,
						of = PredicateType.id,
						optional = true,
						doc = @doc ("The obligation that will be removed")),
				@facet (
						name = CopingStatement.REMOVE_UNCERTAINTIES,
						type = IType.LIST,
						of = PredicateType.id,
						optional = true,
						doc = @doc ("The uncertainty that will be removed")),
				@facet (
						name = CopingStatement.REMOVE_BELIEF,
						type = PredicateType.id,
						optional = true,
						doc = @doc ("The belief that will be removed")),
				@facet (
						name = CopingStatement.REMOVE_IDEAL,
						type = PredicateType.id,
						optional = true,
						doc = @doc ("The ideal that will be removed")),
				@facet (
						name = CopingStatement.REMOVE_DESIRE,
						type = PredicateType.id,
						optional = true,
						doc = @doc ("The desire that will be removed")),
				@facet (
						name = CopingStatement.REMOVE_INTENTION,
						type = PredicateType.id,
						optional = true,
						doc = @doc ("The intention that will be removed")),
				@facet (
						name = CopingStatement.REMOVE_EMOTION,
						type = EmotionType.id,
						optional = true,
						doc = @doc ("The emotion that will be removed")),
				@facet (
						name = CopingStatement.REMOVE_UNCERTAINTY,
						type = PredicateType.id,
						optional = true,
						doc = @doc ("The uncertainty that will be removed")),
				@facet (
						name = CopingStatement.REMOVE_OBLIGATION,
						type = PredicateType.id,
						optional = true,
						doc = @doc ("The obligation that will be removed")),
				@facet (
						name = IKeyword.WHEN,
						type = IType.BOOL,
						optional = true,
						doc = @doc (" ")),
				@facet (
						name = CopingStatement.THRESHOLD,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("Threshold linked to the emotion.")),
				@facet (
						name = IKeyword.PARALLEL,
						type = { IType.BOOL, IType.INT },
						optional = true,
						doc = @doc ("setting this facet to 'true' will allow 'perceive' to use concurrency with a parallel_bdi architecture; setting it to an integer will set the threshold under which they will be run sequentially (the default is initially 20, but can be fixed in the preferences). This facet is true by default.")),
				@facet (
						name = CopingStatement.STRENGTH,
						type = { IType.FLOAT, IType.INT },
						optional = true,
						doc = @doc ("The stregth of the mental state created")),
				@facet (
						name = "lifetime",
						type = IType.INT,
						optional = true,
						doc = @doc ("the lifetime value of the mental state created")),
				@facet (
						name = IKeyword.NAME,
						type = IType.ID,
						optional = true,
						doc = @doc ("The name of the rule")) },
		omissible = IKeyword.NAME)
@doc (
		value = "enables to add or remove mantal states depending on the emotions of the agent, after the emotional engine and before the cognitive or normative engine.",
		examples = {
				@example ("coping emotion: new_emotion(\"fear\") when: flip(0.5) new_desire: new_predicate(\"test\")") })

public class CopingStatement extends AbstractStatement{
	
	public static final String COPING = "coping";
	public static final String BELIEF = "belief";
	public static final String DESIRE = "desire";
	public static final String EMOTION = "emotion";
	public static final String UNCERTAINTY = "uncertainty";
	public static final String IDEAL = "ideal";
	public static final String OBLIGATION = "obligation";
	public static final String RULES = "rules";
	public static final String BELIEFS = "beliefs";
	public static final String DESIRES = "desires";
	public static final String EMOTIONS = "emotions";
	public static final String UNCERTAINTIES = "uncertainties";
	public static final String IDEALS = "ideals";
	public static final String OBLIGATIONS = "obligations";
	public static final String NEW_DESIRE = "new_desire";
	public static final String NEW_BELIEF = "new_belief";
	public static final String NEW_EMOTION = "new_emotion";
	public static final String NEW_UNCERTAINTY = "new_uncertainty";
	public static final String NEW_IDEAL = "new_ideal";
	public static final String REMOVE_BELIEF = "remove_belief";
	public static final String REMOVE_DESIRE = "remove_desire";
	public static final String REMOVE_INTENTION = "remove_intention";
	public static final String REMOVE_EMOTION = "remove_emotion";
	public static final String REMOVE_UNCERTAINTY = "remove_uncertainty";
	public static final String REMOVE_IDEAL = "remove_ideal";
	public static final String REMOVE_OBLIGATION = "remove_obligation";
	public static final String NEW_DESIRES = "new_desires";
	public static final String NEW_BELIEFS = "new_beliefs";
	public static final String NEW_EMOTIONS = "new_emotions";
	public static final String NEW_UNCERTAINTIES = "new_uncertainties";
	public static final String NEW_IDEALS = "new_ideals";
	public static final String REMOVE_BELIEFS = "remove_beliefs";
	public static final String REMOVE_DESIRES = "remove_desires";
	public static final String REMOVE_EMOTIONS = "remove_emotions";
	public static final String REMOVE_UNCERTAINTIES = "remove_uncertainties";
	public static final String REMOVE_IDEALS = "remove_ideals";
	public static final String REMOVE_OBLIGATIONS = "remove_obligations";
	public static final String STRENGTH = "strength";
	public static final String THRESHOLD = "threshold";

	final IExpression when;
	final IExpression parallel;
	final IExpression belief;
	final IExpression desire;
	final IExpression emotion;
	final IExpression uncertainty;
	final IExpression ideal;
	final IExpression obligation;
	final IExpression beliefs;
	final IExpression desires;
	final IExpression emotions;
	final IExpression uncertainties;
	final IExpression ideals;
	final IExpression obligations;
	final IExpression newBelief;
	final IExpression newDesire;
	final IExpression newEmotion;
	final IExpression newUncertainty;
	final IExpression newIdeal;
	final IExpression removeBelief;
	final IExpression removeDesire;
	final IExpression removeIntention;
	final IExpression removeEmotion;
	final IExpression removeUncertainty;
	final IExpression removeIdeal;
	final IExpression removeObligation;
	final IExpression newBeliefs;
	final IExpression newDesires;
	final IExpression newEmotions;
	final IExpression newUncertainties;
	final IExpression newIdeals;
	final IExpression removeBeliefs;
	final IExpression removeDesires;
	final IExpression removeEmotions;
	final IExpression removeUncertainties;
	final IExpression removeIdeals;
	final IExpression removeObligations;
	final IExpression strength;
	final IExpression threshold;
	final IExpression lifetime;
	
	public CopingStatement(IDescription desc) {
		super(desc);
		when = getFacet(IKeyword.WHEN);
		belief = getFacet(CopingStatement.BELIEF);
		desire = getFacet(CopingStatement.DESIRE);
		emotion = getFacet(CopingStatement.EMOTION);
		uncertainty = getFacet(CopingStatement.UNCERTAINTY);
		ideal = getFacet(CopingStatement.IDEAL);
		obligation = getFacet(CopingStatement.OBLIGATION);
		beliefs = getFacet(CopingStatement.BELIEFS);
		desires = getFacet(CopingStatement.DESIRES);
		emotions = getFacet(CopingStatement.EMOTIONS);
		uncertainties = getFacet(CopingStatement.UNCERTAINTIES);
		ideals = getFacet(CopingStatement.IDEALS);
		obligations = getFacet(CopingStatement.OBLIGATIONS);
		newBelief = getFacet(CopingStatement.NEW_BELIEF);
		newDesire = getFacet(CopingStatement.NEW_DESIRE);
		newEmotion = getFacet(CopingStatement.NEW_EMOTION);
		newUncertainty = getFacet(CopingStatement.NEW_UNCERTAINTY);
		newIdeal = getFacet(CopingStatement.NEW_IDEAL);
		removeBelief = getFacet(CopingStatement.REMOVE_BELIEF);
		removeDesire = getFacet(CopingStatement.REMOVE_DESIRE);
		removeIntention = getFacet(CopingStatement.REMOVE_INTENTION);
		removeEmotion = getFacet(CopingStatement.REMOVE_EMOTION);
		removeUncertainty = getFacet(CopingStatement.REMOVE_UNCERTAINTY);
		removeIdeal = getFacet(CopingStatement.REMOVE_IDEAL);
		removeObligation = getFacet(CopingStatement.REMOVE_OBLIGATION);
		newBeliefs = getFacet(CopingStatement.NEW_BELIEFS);
		newDesires = getFacet(CopingStatement.NEW_DESIRES);
		newEmotions = getFacet(CopingStatement.NEW_EMOTIONS);
		newUncertainties = getFacet(CopingStatement.NEW_UNCERTAINTIES);
		newIdeals = getFacet(CopingStatement.NEW_IDEALS);
		removeBeliefs = getFacet(CopingStatement.REMOVE_BELIEFS);
		removeDesires = getFacet(CopingStatement.REMOVE_DESIRES);
		removeEmotions = getFacet(CopingStatement.REMOVE_EMOTIONS);
		removeUncertainties = getFacet(CopingStatement.REMOVE_UNCERTAINTIES);
		removeIdeals = getFacet(CopingStatement.REMOVE_IDEALS);
		removeObligations = getFacet(CopingStatement.REMOVE_OBLIGATIONS);
		strength = getFacet(CopingStatement.STRENGTH);
		threshold = getFacet(CopingStatement.THRESHOLD);
		lifetime = getFacet("lifetime");
		parallel = getFacet(IKeyword.PARALLEL);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object privateExecuteIn(IScope scope) throws GamaRuntimeException {
		if (newBelief == null && newDesire == null && newEmotion == null && newUncertainty == null
				&& removeBelief == null && removeDesire == null && removeIntention == null && removeEmotion == null
				&& removeUncertainty == null && newBeliefs == null && newDesires == null && newEmotions == null
				&& newUncertainties == null && removeBeliefs == null && removeDesires == null && removeEmotions == null
				&& removeUncertainties == null)
			return null;
		if (when == null || Cast.asBool(scope, when.value(scope))) {
			final MentalState tempBelief = new MentalState("Belief");
			if (belief != null) {
			tempBelief.setPredicate((Predicate) belief.value(scope));
			}
			if (belief == null || SimpleBdiArchitecture.hasBelief(scope, tempBelief)) {
				final MentalState tempDesire = new MentalState("Desire");
				if (desire != null) {
					tempDesire.setPredicate((Predicate) desire.value(scope));
				}
				if (desire == null || SimpleBdiArchitecture.hasDesire(scope, tempDesire)) {
					final MentalState tempUncertainty = new MentalState("Uncertainty");
					if (uncertainty != null) {
						tempUncertainty.setPredicate((Predicate) uncertainty.value(scope));
					}
					if (uncertainty == null || SimpleBdiArchitecture.hasUncertainty(scope, tempUncertainty)) {
						final MentalState tempIdeal = new MentalState("Ideal");
						if (ideal != null) {
						tempIdeal.setPredicate((Predicate) ideal.value(scope));
						}
						if (ideal == null || SimpleBdiArchitecture.hasIdeal(scope, tempIdeal)) {
							final MentalState tempObligation = new MentalState("Obligation");
							if (obligation != null) {
								tempObligation.setPredicate((Predicate) obligation.value(scope));
							}
							if (obligation == null || SimpleBdiArchitecture.hasObligation(scope, tempUncertainty)) {
								if (emotion == null
										|| SimpleBdiArchitecture.hasEmotion(scope, (Emotion) emotion.value(scope))) {
									if (beliefs == null || hasBeliefs(scope, (List<Predicate>) beliefs.value(scope))) {
										if (desires == null
												|| hasDesires(scope, (List<Predicate>) desires.value(scope))) {
											if (uncertainties == null || hasUncertainties(scope,
													(List<Predicate>) uncertainties.value(scope))) {
												if (ideals == null
														|| hasIdeals(scope, (List<Predicate>) ideals.value(scope))) {
													if (obligations == null || hasObligations(scope,
															(List<Predicate>) obligations.value(scope))) {
														if (emotions == null || hasEmotions(scope,
																(List<Emotion>) emotions.value(scope))) {
															if (threshold == null || emotion != null
																	&& threshold != null
																	&& SimpleBdiArchitecture.getEmotion(scope,
																			(Emotion) emotion.value(
																					scope)).intensity >= (Double) threshold
																							.value(scope)) {
																if (newDesire != null) {
																	final Predicate newDes =
																			(Predicate) newDesire.value(scope);
																	final MentalState tempNewDesire =
																			new MentalState("Desire", newDes);
																	if (strength != null) {
																		tempNewDesire.setStrength(Cast.asFloat(
																				scope, strength.value(scope)));
																	}
																	if (lifetime != null) {
																		tempNewDesire.setLifeTime(Cast.asInt(scope,
																				lifetime.value(scope)));
																	}
																	SimpleBdiArchitecture.addDesire(scope, null,
																			tempNewDesire);
																}
																if (newBelief != null) {
																	final Predicate newBel =
																			(Predicate) newBelief.value(scope);
																	final MentalState tempNewBelief =
																			new MentalState("Belief", newBel);
																	if (strength != null) {
																		tempNewBelief.setStrength(Cast.asFloat(
																				scope, strength.value(scope)));
																	}
																	if (lifetime != null) {
																		tempNewBelief.setLifeTime(Cast.asInt(scope,
																				lifetime.value(scope)));
																	}
																	SimpleBdiArchitecture.addBelief(scope,
																			tempNewBelief);
																}
																if (newEmotion != null) {
																	final Emotion newEmo =
																			(Emotion) newEmotion.value(scope);
																	SimpleBdiArchitecture.addEmotion(scope, newEmo);
																}
																if (newUncertainty != null) {
																	final Predicate newUncert =
																			(Predicate) newUncertainty.value(scope);
																	final MentalState tempNewUncertainty =
																			new MentalState("Uncertainty",
																					newUncert);
																	if (strength != null) {
																		tempNewUncertainty.setStrength(Cast.asFloat(
																				scope, strength.value(scope)));
																	}
																	if (lifetime != null) {
																		tempNewUncertainty.setLifeTime(Cast.asInt(
																				scope, lifetime.value(scope)));
																	}
																	SimpleBdiArchitecture.addUncertainty(scope,
																			tempNewUncertainty);
																}
																if (newIdeal != null) {
																	final Predicate newIde =
																			(Predicate) newIdeal.value(scope);
																	final MentalState tempNewIdeal =
																			new MentalState("Ideal", newIde);
																	if (strength != null) {
																		tempNewIdeal.setStrength(Cast.asFloat(scope,
																				strength.value(scope)));
																	}
																	if (lifetime != null) {
																		tempNewIdeal.setLifeTime(Cast.asInt(scope,
																				lifetime.value(scope)));
																	}
																	SimpleBdiArchitecture.addIdeal(scope,
																			tempNewIdeal);
																}
																if (removeBelief != null) {
																	final Predicate removBel =
																			(Predicate) removeBelief.value(scope);
																	final MentalState tempRemoveBelief =
																			new MentalState("Belief", removBel);
																	SimpleBdiArchitecture.removeBelief(scope,
																			tempRemoveBelief);
																}
																if (removeDesire != null) {
																	final Predicate removeDes =
																			(Predicate) removeDesire.value(scope);
																	final MentalState tempRemoveDesire =
																			new MentalState("Desire", removeDes);
																	SimpleBdiArchitecture.removeDesire(scope,
																			tempRemoveDesire);
																}
																if (removeIntention != null) {
																	final Predicate removeInt =
																			(Predicate) removeIntention.value(scope);
																	final MentalState tempRemoveIntention =
																			new MentalState("Intention", removeInt);
																	SimpleBdiArchitecture.removeIntention(scope,
																			tempRemoveIntention);
																}
																if (removeEmotion != null) {
																	final Emotion removeEmo =
																			(Emotion) removeEmotion.value(scope);
																	SimpleBdiArchitecture.removeEmotion(scope,
																			removeEmo);
																}
																if (removeUncertainty != null) {
																	final Predicate removUncert =
																			(Predicate) removeUncertainty
																					.value(scope);
																	final MentalState tempRemoveUncertainty =
																			new MentalState("Uncertainty",
																					removUncert);
																	SimpleBdiArchitecture.removeUncertainty(scope,
																			tempRemoveUncertainty);
																}
																if (removeIdeal != null) {
																	final Predicate removeIde =
																			(Predicate) removeIdeal.value(scope);
																	final MentalState tempRemoveIde =
																			new MentalState("Ideal", removeIde);
																	SimpleBdiArchitecture.removeIdeal(scope,
																			tempRemoveIde);
																}
																if (removeObligation != null) {
																	final Predicate removeObl =
																			(Predicate) removeObligation
																					.value(scope);
																	final MentalState tempRemoveObl =
																			new MentalState("Obligation",
																					removeObl);
																	SimpleBdiArchitecture.removeObligation(scope,
																			tempRemoveObl);
																}
																if (newDesires != null) {
																	final List<Predicate> newDess =
																			(List<Predicate>) newDesires.value(scope);
																	for (final Predicate newDes : newDess) {
																		final MentalState tempDesires =
																				new MentalState("Desire", newDes);
																		if (strength != null) {
																			tempDesires.setStrength(Cast.asFloat(scope,
																					strength.value(scope)));
																		}
																		if (lifetime != null) {
																			tempDesires.setLifeTime(Cast.asInt(scope,
																					lifetime.value(scope)));
																		}
																		SimpleBdiArchitecture.addDesire(scope, null,
																				tempDesires);
																	}
																}
																if (newBeliefs != null) {
																	final List<Predicate> newBels =
																			(List<Predicate>) newBeliefs.value(scope);
																	for (final Predicate newBel : newBels) {
																		final MentalState tempBeliefs =
																				new MentalState("Belief", newBel);
																		if (strength != null) {
																			tempBeliefs.setStrength(Cast.asFloat(scope,
																					strength.value(scope)));
																		}
																		if (lifetime != null) {
																			tempBeliefs.setLifeTime(Cast.asInt(scope,
																					lifetime.value(scope)));
																		}
																		SimpleBdiArchitecture.addBelief(scope,
																				tempBeliefs);
																	}
																}
																if (newEmotions != null) {
																	final List<Emotion> newEmos =
																			(List<Emotion>) newEmotions.value(scope);
																	for (final Emotion newEmo : newEmos)
																		SimpleBdiArchitecture.addEmotion(scope, newEmo);
																}
																if (newUncertainties != null) {
																	final List<Predicate> newUncerts =
																			(List<Predicate>) newUncertainties
																					.value(scope);
																	for (final Predicate newUncert : newUncerts) {
																		final MentalState tempUncertainties =
																				new MentalState("Uncertainty",
																						newUncert);
																		if (strength != null) {
																			tempUncertainties.setStrength(Cast.asFloat(
																					scope, strength.value(scope)));
																		}
																		if (lifetime != null) {
																			tempUncertainties.setLifeTime(Cast.asInt(
																					scope, lifetime.value(scope)));
																		}
																		SimpleBdiArchitecture.addUncertainty(scope,
																				tempUncertainties);
																	}
																}
																if (newIdeals != null) {
																	final List<Predicate> newIdes =
																			(List<Predicate>) newIdeals.value(scope);
																	for (final Predicate newIde : newIdes) {
																		final MentalState tempIdeals =
																				new MentalState("Ideal", newIde);
																		if (strength != null) {
																			tempIdeals.setStrength(Cast.asFloat(scope,
																					strength.value(scope)));
																		}
																		if (lifetime != null) {
																			tempIdeals.setLifeTime(Cast.asInt(scope,
																					lifetime.value(scope)));
																		}
																		SimpleBdiArchitecture.addIdeal(scope,
																				tempIdeals);
																	}
																}
																if (removeBeliefs != null) {
																	final List<Predicate> removBels =
																			(List<Predicate>) removeBeliefs
																					.value(scope);
																	for (final Predicate removBel : removBels) {
																		final MentalState tempRemoveBeliefs =
																				new MentalState("Belief", removBel);
																		SimpleBdiArchitecture.removeBelief(scope,
																				tempRemoveBeliefs);
																	}
																}
																if (removeDesires != null) {
																	final List<Predicate> removeDess =
																			(List<Predicate>) removeDesires
																					.value(scope);
																	for (final Predicate removeDes : removeDess) {
																		final MentalState tempRemoveDesires =
																				new MentalState("Desire", removeDes);
																		SimpleBdiArchitecture.removeDesire(scope,
																				tempRemoveDesires);
																	}
																}
																if (removeEmotions != null) {
																	final List<Emotion> removeEmos =
																			(List<Emotion>) removeEmotions.value(scope);
																	for (final Emotion removeEmo : removeEmos)
																		SimpleBdiArchitecture.removeEmotion(scope,
																				removeEmo);
																}
																if (removeUncertainties != null) {
																	final List<Predicate> removUncerts =
																			(List<Predicate>) removeUncertainties
																					.value(scope);
																	for (final Predicate removUncert : removUncerts) {
																		final MentalState tempRemoveUncertainties =
																				new MentalState("Uncertainty",
																						removUncert);
																		SimpleBdiArchitecture.removeUncertainty(scope,
																				tempRemoveUncertainties);
																	}
																}
																if (removeIdeals != null) {
																	final List<Predicate> removeIdes =
																			(List<Predicate>) removeIdeals.value(scope);
																	for (final Predicate removeIde : removeIdes) {
																		final MentalState tempRemoveIdeals =
																				new MentalState("Ideal", removeIde);
																		SimpleBdiArchitecture.removeIdeal(scope,
																				tempRemoveIdeals);
																	}
																}
																if (removeObligations != null) {
																	final List<Predicate> removeObls =
																			(List<Predicate>) removeObligations
																					.value(scope);
																	for (final Predicate removeObl : removeObls) {
																		final MentalState tempRemoveObligations =
																				new MentalState("Obligation",
																						removeObl);
																		SimpleBdiArchitecture.removeObligation(scope,
																				tempRemoveObligations);
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	private boolean hasBeliefs(final IScope scope, final List<Predicate> predicates) {
		for (final Predicate p : predicates) {
			final MentalState temp = new MentalState("Belief", p);
			if (!SimpleBdiArchitecture.hasBelief(scope, temp))
				return false;
		}
		return true;
	}

	private boolean hasDesires(final IScope scope, final List<Predicate> predicates) {
		for (final Predicate p : predicates) {
			final MentalState temp = new MentalState("Desire", p);
			if (!SimpleBdiArchitecture.hasDesire(scope, temp))
				return false;
		}
		return true;
	}

	private boolean hasUncertainties(final IScope scope, final List<Predicate> predicates) {
		for (final Predicate p : predicates) {
			final MentalState temp = new MentalState("Uncertainty", p);
			if (!SimpleBdiArchitecture.hasUncertainty(scope, temp))
				return false;
		}
		return true;
	}

	private boolean hasIdeals(final IScope scope, final List<Predicate> predicates) {
		for (final Predicate p : predicates) {
			final MentalState temp = new MentalState("Ideal", p);
			if (!SimpleBdiArchitecture.hasIdeal(scope, temp))
				return false;
		}
		return true;
	}

	private boolean hasObligations(final IScope scope, final List<Predicate> predicates) {
		for (final Predicate p : predicates) {
			final MentalState temp = new MentalState("Uncertainty", p);
			if (!SimpleBdiArchitecture.hasUncertainty(scope, temp))
				return false;
		}
		return true;
	}

	private boolean hasEmotions(final IScope scope, final List<Emotion> emotions) {
		for (final Emotion p : emotions) {
			if (!SimpleBdiArchitecture.hasEmotion(scope, p))
				return false;
		}
		return true;
	}

	public IExpression getParallel() {
		return parallel;
	}
	
}
