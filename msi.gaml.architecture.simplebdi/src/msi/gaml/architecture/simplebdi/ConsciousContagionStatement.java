package msi.gaml.architecture.simplebdi;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;

@symbol(name = ConsciousContagionStatement.CONSCIOUSCONTAGION, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false, concept = {
		IConcept.BDI })
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@facets(value = {
		@facet(name = IKeyword.NAME, type = IType.ID, optional = true, doc = @doc("the identifier of the unconscious contagion")),
		@facet(name = ConsciousContagionStatement.EMOTIONDETECTED, type = EmotionType.id, optional = false, doc = @doc("the emotion that will start the contagion")),
		@facet(name = ConsciousContagionStatement.EMOTIONCREATED, type = EmotionType.id, optional = false, doc = @doc("the emotion that will be created with the contagion")),
		@facet(name = ConsciousContagionStatement.CHARISMA, type = IType.FLOAT, optional = true, doc = @doc("The charisma value of the perceived agent (between 0 and 1)")),
		@facet(name = IKeyword.WHEN, type = IType.BOOL, optional = true, doc = @doc("A boolean value to get the emotion only with a certain condition")),
		@facet(name = ConsciousContagionStatement.THRESHOLD, type = IType.FLOAT, optional = true, doc = @doc("The threshold value to make the contagion")),
		@facet(name = ConsciousContagionStatement.DECAY, type = IType.FLOAT, optional = true, doc = @doc("The decay value of the emotion added to the agent")),
		@facet(name = ConsciousContagionStatement.RECEPTIVITY, type = IType.FLOAT, optional = true, doc = @doc("The receptivity value of the current agent (between 0 and 1)")) }, omissible = IKeyword.NAME)
@doc(value = "enables to directly add an emotion of a perceived specie if the perceived agent ges a patricular emotion.", examples = {
		@example("conscious_contagion emotion_detected:fear emotion_created:fearConfirmed;"),
		@example("conscious_contagion emotion_detected:fear emotion_created:fearConfirmed charisma: 0.5 receptivity: 0.5;") })

public class ConsciousContagionStatement extends AbstractStatement {

	public static final String CONSCIOUSCONTAGION = "conscious_contagion";
	public static final String EMOTIONDETECTED = "emotion_detected";
	public static final String EMOTIONCREATED = "emotion_created";
	public static final String CHARISMA = "charisma";
	public static final String RECEPTIVITY = "receptivity";
	public static final String THRESHOLD = "threshold";
	public static final String DECAY = "decay";

	final IExpression name;
	final IExpression emotionDetected;
	final IExpression emotionCreated;
	final IExpression charisma;
	final IExpression when;
	final IExpression receptivity;
	final IExpression threshold;
	final IExpression decay;

	public ConsciousContagionStatement(final IDescription desc) {
		super(desc);
		name = getFacet(IKeyword.NAME);
		emotionDetected = getFacet(ConsciousContagionStatement.EMOTIONDETECTED);
		emotionCreated = getFacet(ConsciousContagionStatement.EMOTIONCREATED);
		charisma = getFacet(ConsciousContagionStatement.CHARISMA);
		when = getFacet(IKeyword.WHEN);
		receptivity = getFacet(ConsciousContagionStatement.RECEPTIVITY);
		threshold = getFacet(ConsciousContagionStatement.THRESHOLD);
		decay = getFacet(ConsciousContagionStatement.DECAY);
	}

	@Override
	protected Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		final IAgent[] stack = scope.getAgentsStack();
		final IAgent mySelfAgent = stack[stack.length - 2];
		Double charismaValue = 1.0;
		Double receptivityValue = 1.0;
		Double thresholdValue = 0.25;
		Double decayValue = 0.0;
		IScope scopeMySelf = null;
		if (mySelfAgent != null) {
			scopeMySelf = mySelfAgent.getScope().copy("of ConsciousContagionStatement");
			scopeMySelf.push(mySelfAgent);
		} else
			return null;
		if (when == null || Cast.asBool(scopeMySelf, when.value(scopeMySelf))) {
			if (emotionDetected != null && emotionCreated != null) {
				if (SimpleBdiArchitecture.hasEmotion(scope, (Emotion) emotionDetected.value(scope))) {
					if (charisma != null) {
						charismaValue = (Double) charisma.value(scope);
					} else {
						charismaValue = (Double) scope.getAgent().getAttribute(CHARISMA);
					}
					if (receptivity != null) {
						receptivityValue = (Double) receptivity.value(scopeMySelf);
					} else {
						receptivityValue = (Double) mySelfAgent.getAttribute(RECEPTIVITY);
					}
					if (threshold != null) {
						thresholdValue = (Double) threshold.value(scopeMySelf);
					}
					if (charismaValue * receptivityValue >= thresholdValue) {
						final Emotion tempEmo = (Emotion) emotionCreated.value(scope);
						tempEmo.setAgentCause(scope.getAgent());
						if(decay!=null){
							decayValue = (Double) decay.value(scopeMySelf);
							if(decayValue>1.0){
								decayValue = 1.0;
							}
							if(decayValue<0.0){
								decayValue = 0.0;
							}
						}
						tempEmo.setDecay(decayValue);
						SimpleBdiArchitecture.addEmotion(scopeMySelf, tempEmo);
					}
				}
			}
		}
		GAMA.releaseScope(scopeMySelf);
		return null;
	}
}
