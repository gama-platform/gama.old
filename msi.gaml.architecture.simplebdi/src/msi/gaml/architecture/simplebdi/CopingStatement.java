/*******************************************************************************************************
 *
 * CopingStatement.java, in msi.gaml.architecture.simplebdi, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/


package msi.gaml.architecture.simplebdi;

import java.util.List;

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
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.types.IType;

/**
 * The Class CopingStatement.
 */
@symbol (
		name = CopingStatement.COPING,
		kind = ISymbolKind.BEHAVIOR,
		with_sequence = true,
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
						type = EmotionType.EMOTIONTYPE_ID,
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
						of = EmotionType.EMOTIONTYPE_ID,
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
						type = EmotionType.EMOTIONTYPE_ID,
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
						of = EmotionType.EMOTIONTYPE_ID,
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
						of = EmotionType.EMOTIONTYPE_ID,
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
						type = EmotionType.EMOTIONTYPE_ID,
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
				@example ("coping emotion: new_emotion(\"fear\") when: flip(0.5) new_desire: new_predicate(\"test\");") })

public class CopingStatement extends AbstractStatementSequence{
	
	/** The Constant COPING. */
	public static final String COPING = "coping";
	
	/** The Constant BELIEF. */
	public static final String BELIEF = "belief";
	
	/** The Constant DESIRE. */
	public static final String DESIRE = "desire";
	
	/** The Constant EMOTION. */
	public static final String EMOTION = "emotion";
	
	/** The Constant UNCERTAINTY. */
	public static final String UNCERTAINTY = "uncertainty";
	
	/** The Constant IDEAL. */
	public static final String IDEAL = "ideal";
	
	/** The Constant OBLIGATION. */
	public static final String OBLIGATION = "obligation";
	
	/** The Constant RULES. */
	public static final String RULES = "rules";
	
	/** The Constant BELIEFS. */
	public static final String BELIEFS = "beliefs";
	
	/** The Constant DESIRES. */
	public static final String DESIRES = "desires";
	
	/** The Constant EMOTIONS. */
	public static final String EMOTIONS = "emotions";
	
	/** The Constant UNCERTAINTIES. */
	public static final String UNCERTAINTIES = "uncertainties";
	
	/** The Constant IDEALS. */
	public static final String IDEALS = "ideals";
	
	/** The Constant OBLIGATIONS. */
	public static final String OBLIGATIONS = "obligations";
	
	/** The Constant NEW_DESIRE. */
	public static final String NEW_DESIRE = "new_desire";
	
	/** The Constant NEW_BELIEF. */
	public static final String NEW_BELIEF = "new_belief";
	
	/** The Constant NEW_EMOTION. */
	public static final String NEW_EMOTION = "new_emotion";
	
	/** The Constant NEW_UNCERTAINTY. */
	public static final String NEW_UNCERTAINTY = "new_uncertainty";
	
	/** The Constant NEW_IDEAL. */
	public static final String NEW_IDEAL = "new_ideal";
	
	/** The Constant REMOVE_BELIEF. */
	public static final String REMOVE_BELIEF = "remove_belief";
	
	/** The Constant REMOVE_DESIRE. */
	public static final String REMOVE_DESIRE = "remove_desire";
	
	/** The Constant REMOVE_INTENTION. */
	public static final String REMOVE_INTENTION = "remove_intention";
	
	/** The Constant REMOVE_EMOTION. */
	public static final String REMOVE_EMOTION = "remove_emotion";
	
	/** The Constant REMOVE_UNCERTAINTY. */
	public static final String REMOVE_UNCERTAINTY = "remove_uncertainty";
	
	/** The Constant REMOVE_IDEAL. */
	public static final String REMOVE_IDEAL = "remove_ideal";
	
	/** The Constant REMOVE_OBLIGATION. */
	public static final String REMOVE_OBLIGATION = "remove_obligation";
	
	/** The Constant NEW_DESIRES. */
	public static final String NEW_DESIRES = "new_desires";
	
	/** The Constant NEW_BELIEFS. */
	public static final String NEW_BELIEFS = "new_beliefs";
	
	/** The Constant NEW_EMOTIONS. */
	public static final String NEW_EMOTIONS = "new_emotions";
	
	/** The Constant NEW_UNCERTAINTIES. */
	public static final String NEW_UNCERTAINTIES = "new_uncertainties";
	
	/** The Constant NEW_IDEALS. */
	public static final String NEW_IDEALS = "new_ideals";
	
	/** The Constant REMOVE_BELIEFS. */
	public static final String REMOVE_BELIEFS = "remove_beliefs";
	
	/** The Constant REMOVE_DESIRES. */
	public static final String REMOVE_DESIRES = "remove_desires";
	
	/** The Constant REMOVE_EMOTIONS. */
	public static final String REMOVE_EMOTIONS = "remove_emotions";
	
	/** The Constant REMOVE_UNCERTAINTIES. */
	public static final String REMOVE_UNCERTAINTIES = "remove_uncertainties";
	
	/** The Constant REMOVE_IDEALS. */
	public static final String REMOVE_IDEALS = "remove_ideals";
	
	/** The Constant REMOVE_OBLIGATIONS. */
	public static final String REMOVE_OBLIGATIONS = "remove_obligations";
	
	/** The Constant STRENGTH. */
	public static final String STRENGTH = "strength";
	
	/** The Constant THRESHOLD. */
	public static final String THRESHOLD = "threshold";

	/** The when. */
	final IExpression when;
	
	/** The parallel. */
	final IExpression parallel;
	
	/** The belief. */
	final IExpression belief;
	
	/** The desire. */
	final IExpression desire;
	
	/** The emotion. */
	final IExpression emotion;
	
	/** The uncertainty. */
	final IExpression uncertainty;
	
	/** The ideal. */
	final IExpression ideal;
	
	/** The obligation. */
	final IExpression obligation;
	
	/** The beliefs. */
	final IExpression beliefs;
	
	/** The desires. */
	final IExpression desires;
	
	/** The emotions. */
	final IExpression emotions;
	
	/** The uncertainties. */
	final IExpression uncertainties;
	
	/** The ideals. */
	final IExpression ideals;
	
	/** The obligations. */
	final IExpression obligations;
	
	/** The new belief. */
	final IExpression newBelief;
	
	/** The new desire. */
	final IExpression newDesire;
	
	/** The new emotion. */
	final IExpression newEmotion;
	
	/** The new uncertainty. */
	final IExpression newUncertainty;
	
	/** The new ideal. */
	final IExpression newIdeal;
	
	/** The remove belief. */
	final IExpression removeBelief;
	
	/** The remove desire. */
	final IExpression removeDesire;
	
	/** The remove intention. */
	final IExpression removeIntention;
	
	/** The remove emotion. */
	final IExpression removeEmotion;
	
	/** The remove uncertainty. */
	final IExpression removeUncertainty;
	
	/** The remove ideal. */
	final IExpression removeIdeal;
	
	/** The remove obligation. */
	final IExpression removeObligation;
	
	/** The new beliefs. */
	final IExpression newBeliefs;
	
	/** The new desires. */
	final IExpression newDesires;
	
	/** The new emotions. */
	final IExpression newEmotions;
	
	/** The new uncertainties. */
	final IExpression newUncertainties;
	
	/** The new ideals. */
	final IExpression newIdeals;
	
	/** The remove beliefs. */
	final IExpression removeBeliefs;
	
	/** The remove desires. */
	final IExpression removeDesires;
	
	/** The remove emotions. */
	final IExpression removeEmotions;
	
	/** The remove uncertainties. */
	final IExpression removeUncertainties;
	
	/** The remove ideals. */
	final IExpression removeIdeals;
	
	/** The remove obligations. */
	final IExpression removeObligations;
	
	/** The strength. */
	final IExpression strength;
	
	/** The threshold. */
	final IExpression threshold;
	
	/** The lifetime. */
	final IExpression lifetime;
	
	/**
	 * Instantiates a new coping statement.
	 *
	 * @param desc the desc
	 */
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
	public Object privateExecuteIn(IScope scope) throws GamaRuntimeException {
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
		return super.privateExecuteIn(scope);
//		return null;
	}
	
	/**
	 * Checks for beliefs.
	 *
	 * @param scope the scope
	 * @param predicates the predicates
	 * @return true, if successful
	 */
	private boolean hasBeliefs(final IScope scope, final List<Predicate> predicates) {
		for (final Predicate p : predicates) {
			final MentalState temp = new MentalState("Belief", p);
			if (!SimpleBdiArchitecture.hasBelief(scope, temp))
				return false;
		}
		return true;
	}

	/**
	 * Checks for desires.
	 *
	 * @param scope the scope
	 * @param predicates the predicates
	 * @return true, if successful
	 */
	private boolean hasDesires(final IScope scope, final List<Predicate> predicates) {
		for (final Predicate p : predicates) {
			final MentalState temp = new MentalState("Desire", p);
			if (!SimpleBdiArchitecture.hasDesire(scope, temp))
				return false;
		}
		return true;
	}

	/**
	 * Checks for uncertainties.
	 *
	 * @param scope the scope
	 * @param predicates the predicates
	 * @return true, if successful
	 */
	private boolean hasUncertainties(final IScope scope, final List<Predicate> predicates) {
		for (final Predicate p : predicates) {
			final MentalState temp = new MentalState("Uncertainty", p);
			if (!SimpleBdiArchitecture.hasUncertainty(scope, temp))
				return false;
		}
		return true;
	}

	/**
	 * Checks for ideals.
	 *
	 * @param scope the scope
	 * @param predicates the predicates
	 * @return true, if successful
	 */
	private boolean hasIdeals(final IScope scope, final List<Predicate> predicates) {
		for (final Predicate p : predicates) {
			final MentalState temp = new MentalState("Ideal", p);
			if (!SimpleBdiArchitecture.hasIdeal(scope, temp))
				return false;
		}
		return true;
	}

	/**
	 * Checks for obligations.
	 *
	 * @param scope the scope
	 * @param predicates the predicates
	 * @return true, if successful
	 */
	private boolean hasObligations(final IScope scope, final List<Predicate> predicates) {
		for (final Predicate p : predicates) {
			final MentalState temp = new MentalState("Uncertainty", p);
			if (!SimpleBdiArchitecture.hasUncertainty(scope, temp))
				return false;
		}
		return true;
	}

	/**
	 * Checks for emotions.
	 *
	 * @param scope the scope
	 * @param emotions the emotions
	 * @return true, if successful
	 */
	private boolean hasEmotions(final IScope scope, final List<Emotion> emotions) {
		for (final Emotion p : emotions) {
			if (!SimpleBdiArchitecture.hasEmotion(scope, p))
				return false;
		}
		return true;
	}

	/**
	 * Gets the parallel.
	 *
	 * @return the parallel
	 */
	public IExpression getParallel() {
		return parallel;
	}
	
}
