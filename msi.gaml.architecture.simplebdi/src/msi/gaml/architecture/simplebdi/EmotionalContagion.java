package msi.gaml.architecture.simplebdi;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
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
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;

@symbol(name = EmotionalContagion.EMOTIONALCONTAGION, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false, concept = {
		IConcept.BDI })
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@facets(value = {
		@facet(name = IKeyword.NAME, type = IType.ID, optional = true, doc = @doc("the identifier of the emotional contagion")),
		@facet(name = EmotionalContagion.EMOTIONDETECTED, type = EmotionType.id, optional = false, doc = @doc("the emotion that will start the contagion")),
		@facet(name = EmotionalContagion.EMOTIONCREATED, type = EmotionType.id, optional = true, doc = @doc("the emotion that will be created with the contagion")),
		@facet(name = EmotionalContagion.CHARISMA, type = IType.FLOAT, optional = true, doc = @doc("The charisma value of the perceived agent (between 0 and 1)")),
		@facet(name = IKeyword.WHEN, type = IType.BOOL, optional = true, doc = @doc("A boolean value to get the emotion only with a certain condition")),
		@facet(name = EmotionalContagion.THRESHOLD, type = IType.FLOAT, optional = true, doc = @doc("The threshold value to make the contagion")),
		@facet(name = EmotionalContagion.RECEPTIVITY, type = IType.FLOAT, optional = true, doc = @doc("The receptivity value of the current agent (between 0 and 1)")) },
	omissible = IKeyword.NAME)
@doc(value = "enables to make conscious or unconscious emotional contagion", examples = {
		@example("emotional_contagion emotion_detected:fearConfirmed;"),
		@example("emotional_contagion emotion_detected:fear emotion_created:fearConfirmed;"),
		@example("emotional_contagion emotion_detected:fear emotion_created:fearConfirmed charisma: 0.5 receptivity: 0.5;")})


public class EmotionalContagion extends AbstractStatement {

	public static final String EMOTIONALCONTAGION = "emotional_contagion";
	public static final String EMOTIONDETECTED = "emotion_detected";
	public static final String EMOTIONCREATED = "emotion_created";
	public static final String CHARISMA = "charisma";
	public static final String RECEPTIVITY = "receptivity";
	public static final String THRESHOLD = "threshold";

	final IExpression name;
	final IExpression emotionDetected;
	final IExpression emotionCreated;
	final IExpression charisma;
	final IExpression when;
	final IExpression receptivity;
	final IExpression threshold;
	
	public EmotionalContagion(IDescription desc) {
		super(desc);
		name = getFacet(IKeyword.NAME);
		emotionDetected = getFacet(EmotionalContagion.EMOTIONDETECTED);
		emotionCreated = getFacet(EmotionalContagion.EMOTIONCREATED);
		charisma = getFacet(EmotionalContagion.CHARISMA);
		when = getFacet(IKeyword.WHEN);
		receptivity = getFacet(EmotionalContagion.RECEPTIVITY);
		threshold = getFacet(EmotionalContagion.THRESHOLD);
	}

	@Override
	protected Object privateExecuteIn(IScope scope) throws GamaRuntimeException {
		final IAgent[] stack = scope.getAgentsStack();
		final IAgent mySelfAgent = stack[stack.length - 2];
		Double charismaValue = 1.0;
		Double receptivityValue = 1.0;
		Double thresholdValue = 0.25;
		IScope scopeMySelf = null;
		if (mySelfAgent != null) {
			scopeMySelf = mySelfAgent.getScope().copy("of EmotionalContagion");
			scopeMySelf.push(mySelfAgent);
		}
		if (when == null || Cast.asBool(scopeMySelf, when.value(scopeMySelf))) {
			if (emotionDetected != null) {
				if (SimpleBdiArchitecture.hasEmotion(scope, (Emotion) emotionDetected.value(scope))) {
					if (charisma != null) {
						charismaValue = (Double) charisma.value(scope);
					}else{charismaValue = (Double) scope.getAgentScope().getAttribute(CHARISMA);}
					if (receptivity != null) {
						receptivityValue = (Double) receptivity.value(scopeMySelf);
					}else{receptivityValue = (Double) mySelfAgent.getAttribute(RECEPTIVITY);}
					if (threshold != null) {
						thresholdValue = (Double) threshold.value(scopeMySelf);
					}
					if(emotionCreated != null){
						if (charismaValue * receptivityValue >= thresholdValue) {
							final Emotion tempEmo = (Emotion) emotionCreated.value(scope);
							SimpleBdiArchitecture.addEmotion(scopeMySelf, tempEmo);
						}
					}else{
						if (charismaValue * receptivityValue >= thresholdValue) {
							final Emotion tempEmo = SimpleBdiArchitecture.getEmotion(scope, (Emotion) emotionDetected.value(scope));
							Emotion temp;
							if (!tempEmo.getNoIntensity()) {
								temp = new Emotion(tempEmo.getName(),
										tempEmo.getIntensity() * charismaValue * receptivityValue, tempEmo.getAbout(),
										tempEmo.getDecay());
							} else {
								temp = tempEmo;
							}
							SimpleBdiArchitecture.addEmotion(scopeMySelf, temp);
						}
					}
				}
			}
		}
		return null;
	}

}
