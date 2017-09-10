package msi.gaml.architecture.simplebdi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Operators {

	@operator(value = "new_predicate", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new predicate with the given properties (name)", examples = @example(value = "predicate(\"people to meet\")", test = false))
	public static Predicate newPredicate(final String name) throws GamaRuntimeException {
		return new Predicate(name);
	}

	@operator(value = "new_predicate", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new predicate with the given properties (name, values)", examples = @example(value = "predicate(\"people to meet\", people1 )", test = false))
	public static Predicate newPredicate(final String name, final Map values) throws GamaRuntimeException {
		return new Predicate(name, values);
	}

	@operator(value = "new_predicate", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new predicate with the given is_true (name, is_true)", examples = @example(value = "predicate(\"hasWater\", true)", test = false))
	public static Predicate newPredicate(final String name, final Boolean ist) throws GamaRuntimeException {
		return new Predicate(name, ist);
	}

	@operator(value = "new_predicate", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new predicate with the given is_true (name, priority)", examples = @example(value = "predicate(\"hasWater\", 2.0 )", test = false))
	public static Predicate newPredicate(final String name, final Double priority) {
		return new Predicate(name, priority);
	}

	@operator(value = "new_predicate", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new predicate with the given is_true (name, lifetime)", examples = @example(value = "predicate(\"hasWater\", 10 ", test = false))
	public static Predicate newPredicate(final String name, final int lifetime) {
		return new Predicate(name, lifetime);
	}

	@operator(value = "new_predicate", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new predicate with the given properties (name, values, lifetime)", examples = @example(value = "predicate(\"people to meet\", [\"time\"::10], true)", test = false))
	public static Predicate newPredicate(final String name, final IAgent agent) throws GamaRuntimeException {
		return new Predicate(name, agent);
	}

	@operator(value = "new_predicate", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new predicate with the given properties (name, values, priority)", examples = @example(value = "predicate(\"people to meet\", people1, [\"time\"::10])", test = false))
	public static Predicate newPredicate(final String name, final Map values, final Double priority)
			throws GamaRuntimeException {
		return new Predicate(name, priority, values);
	}

	@operator(value = "new_predicate", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new predicate with the given properties (name, values, is_true)", examples = @example(value = "predicate(\"people to meet\", [\"time\"::10], true)", test = false))
	public static Predicate newPredicate(final String name, final Map values, final Boolean truth)
			throws GamaRuntimeException {
		return new Predicate(name, values, truth);
	}

	@operator(value = "new_predicate", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new predicate with the given properties (name, values, lifetime)", examples = @example(value = "predicate(\"people to meet\", [\"time\"::10], true)", test = false))
	public static Predicate newPredicate(final String name, final Map values, final int lifetime)
			throws GamaRuntimeException {
		return new Predicate(name, values, lifetime);
	}

	@operator(value = "new_predicate", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new predicate with the given properties (name, values, 	agentCause)", examples = @example(value = "predicate(\"people to meet\", [\"time\"::10], agentA)", test = false))
	public static Predicate newPredicate(final String name, final Map values, final IAgent agent)
			throws GamaRuntimeException {
		return new Predicate(name, values, agent);
	}

	@operator(value = "new_predicate", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new predicate with the given properties (name, values, priority,lifetime)", examples = @example(value = "predicate(\"people to meet\", [\"time\"::10], 2.0,10)", test = false))
	public static Predicate newPredicate(final String name, final Map values, final Double priority, final int lifetime)
			throws GamaRuntimeException {
		return new Predicate(name, priority, values, lifetime);
	}

	@operator(value = "new_predicate", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new predicate with the given properties (name, values, priority, is_true)", examples = @example(value = "predicate(\"people to meet\", [\"time\"::10],2.0, true)", test = false))
	public static Predicate newPredicate(final String name, final Map values, final Double priority,
			final Boolean truth) throws GamaRuntimeException {
		return new Predicate(name, priority, values, truth);
	}

	@operator(value = "new_predicate", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new predicate with the given properties (name, values, priority, agentCause)", examples = @example(value = "predicate(\"people to meet\", [\"time\"::10], 2.0,agentA)", test = false))
	public static Predicate newPredicate(final String name, final Map values, final Double priority, final IAgent agent)
			throws GamaRuntimeException {
		return new Predicate(name, priority, values, agent);
	}

	@operator(value = "new_predicate", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new predicate with the given properties (name, values, is_true, agentCause)", examples = @example(value = "predicate(\"people to meet\", [\"time\"::10], true, agentA)", test = false))
	public static Predicate newPredicate(final String name, final Map values, final Boolean truth, final IAgent agent)
			throws GamaRuntimeException {
		return new Predicate(name, values, truth, agent);
	}

	@operator(value = "new_predicate", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new predicate with the given properties (name, values, lifetime, agentCause)", examples = @example(value = "predicate(\"people to meet\", [\"time\"::10], 10, agentA)", test = false))
	public static Predicate newPredicate(final String name, final Map values, final int lifetime, final IAgent agent)
			throws GamaRuntimeException {
		return new Predicate(name, values, lifetime, agent);
	}

	@operator(value = "new_predicate", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new predicate with the given properties (name, values, lifetime, is_true)", examples = @example(value = "predicate(\"people to meet\", [\"time\"::10], 10,true)", test = false))
	public static Predicate newPredicate(final String name, final Map values, final int lifetime, final Boolean truth)
			throws GamaRuntimeException {
		return new Predicate(name, values, lifetime, truth);
	}

	@operator(value = "new_predicate", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new predicate with the given properties (name, values, priority, lifetime, is_true)", examples = @example(value = "predicate(\"people to meet\", [\"time\"::10],2.0,10, true)", test = false))
	public static Predicate newPredicate(final String name, final Map values, final Double priority, final int lifetime,
			final Boolean truth) throws GamaRuntimeException {
		return new Predicate(name, priority, values, lifetime, truth);
	}

	@operator(value = "new_predicate", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new predicate with the given properties (name, values, priority, lifetime, agentCause)", examples = @example(value = "predicate(\"people to meet\", [\"time\"::10], 2.0,10,agentA)", test = false))
	public static Predicate newPredicate(final String name, final Map values, final Double priority, final int lifetime,
			final IAgent agent) throws GamaRuntimeException {
		return new Predicate(name, priority, values, lifetime, agent);
	}

	@operator(value = "new_predicate", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new predicate with the given properties (name, values, priority, is_true, agentCause)", examples = @example(value = "predicate(\"people to meet\", [\"time\"::10], 2.0, true, agentA)", test = false))
	public static Predicate newPredicate(final String name, final Map values, final Double priority,
			final Boolean truth, final IAgent agent) throws GamaRuntimeException {
		return new Predicate(name, priority, values, truth, agent);
	}

	@operator(value = "new_predicate", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new predicate with the given properties (name, values, lifetime, is_true, agentCause)", examples = @example(value = "predicate(\"people to meet\", [\"time\"::10], 10, true, agentA)", test = false))
	public static Predicate newPredicate(final String name, final Map values, final int lifetime, final Boolean truth,
			final IAgent agent) throws GamaRuntimeException {
		return new Predicate(name, values, lifetime, truth, agent);
	}

	@operator(value = "new_predicate", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new predicate with the given properties (name, values, priority, lifetime, is_true, agentCause)", examples = @example(value = "predicate(\"people to meet\", [\"time\"::10],2.0,10, true, agentA)", test = false))
	public static Predicate newPredicate(final String name, final Map values, final Double priority, final int lifetime,
			final Boolean truth, final IAgent agent) throws GamaRuntimeException {
		return new Predicate(name, priority, values, lifetime, truth, agent);
	}

	@operator(value = "set_agent_cause", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "change the agentCause value of the given predicate", examples = @example(value = "predicate set_agent_cause agentA", test = false))
	public static Predicate withAgentCause(final Predicate predicate, final IAgent agent) throws GamaRuntimeException {
		Predicate temp = predicate.copy();
		temp.setAgentCause(agent);
		return temp;
	}

	@operator(value = "set_truth", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "change the is_true value of the given predicate", examples = @example(value = "predicate set_truth false", test = false))
	public static Predicate withTruth(final Predicate predicate, final Boolean truth) throws GamaRuntimeException {
		Predicate temp = predicate.copy();
		temp.is_true = truth;
		return temp;
	}

	@operator(value = "with_praiseworthiness", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "change the praiseworthiness value of the given predicate", examples = @example(value = "predicate set_truth false", test = false))
	public static Predicate withPraise(final Predicate predicate, final Double praise) throws GamaRuntimeException {
		Predicate temp = predicate.copy();
		temp.setPraiseworthiness(praise);
		return temp;
	}
	
	@operator(value = "with_priority", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "change the priority of the given predicate", examples = @example(value = "predicate with_priority 2", test = false))
	public static Predicate withPriority(final Predicate predicate, final Double priority) throws GamaRuntimeException {
		//Penser à l'enlever car inutile avec la force des états mentaux
		Predicate temp = predicate.copy();
		temp.priority = priority;
		return temp;
	}

	@operator(value = "with_values", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "change the parameters of the given predicate", examples = @example(value = "predicate with_values [\"time\"::10]", test = false))
	public static Predicate withValues(final Predicate predicate, final Map values) throws GamaRuntimeException {
		Predicate temp = predicate.copy();
		temp.setValues(values);
		return temp;
	}

	@operator(value = "with_lifetime", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "change the parameters of the given predicate", examples = @example(value = "predicate with_lifetime 10", test = false))
	public static Predicate withValues(final Predicate predicate, final int lifetime) throws GamaRuntimeException {
		//inutile car dans les états mentaux
		Predicate temp = predicate.copy();
		temp.lifetime = lifetime;
		return temp;
	}

	@operator(value = "and", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "create a new predicate from two others by including them as subintentions", examples = @example(value = "predicate1 and predicate2", test = false))
	public static Predicate and(final Predicate pred1, final Predicate pred2) {
		final Predicate tempPred = new Predicate(pred1.getName() + "_and_" + pred2.getName());
		final List<MentalState> tempList = new ArrayList<MentalState>();
		MentalState tempPred1 = new MentalState("Intention",pred1);
		MentalState tempPred2 = new MentalState("Intention",pred2);
		tempList.add(tempPred1);
		tempList.add(tempPred2);
		tempPred.setSubintentions(tempList);
		final Map<String, Object> tempMap = new HashMap();
		tempMap.put("and", true);
		tempPred.setValues(tempMap);
		return tempPred;
	}

	@operator(value = "or", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "create a new predicate from two others by including them as subintentions. It's an exclusive \"or\" ", examples = @example(value = "predicate1 or predicate2", test = false))
	public static Predicate or(final Predicate pred1, final Predicate pred2) {
		final Predicate tempPred = new Predicate(pred1.getName() + "_or_" + pred2.getName());
		final List<MentalState> tempList = new ArrayList<MentalState>();
		MentalState tempPred1 = new MentalState("Intention",pred1);
		MentalState tempPred2 = new MentalState("Intention",pred2);
		tempList.add(tempPred1);
		tempList.add(tempPred2);
		tempPred.setSubintentions(tempList);
		final Map<String, Object> tempMap = new HashMap();
		tempMap.put("or", true);
		tempPred.setValues(tempMap);
		return tempPred;
	}

	@operator(value = "eval_when", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "evaluate the facet when of a given plan", examples = @example(value = "eval_when(plan1)", test = false))
	public static Boolean evalWhen(final IScope scope, final BDIPlan plan) {
		return plan.getPlanStatement().getContextExpression() == null
				|| msi.gaml.operators.Cast.asBool(scope, plan.getPlanStatement().getContextExpression().value(scope));
	}

	@operator(value = "get_super_intention", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	public static MentalState getSuperIntention(final Predicate pred1) {
		if (pred1.getSuperIntention() != null) {
			return pred1.getSuperIntention();
		} else {
			return null;
		}
	}

	@operator(value = "get_priority", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	public static Double getPriority(final Predicate pred) {
		if (pred != null) {
			return pred.priority;
		} else {
			return null;
		}
	}

	@operator(value = "get_truth", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	public static Boolean getTruth(final Predicate pred) {
		if (pred != null) {
			return pred.is_true;
		} else {
			return null;
		}
	}
	
	@operator(value = "get_lifetime", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	public static int getLifetime(final Predicate pred) {
		if (pred != null) {
			return pred.lifetime;
		} else {
			return 0;
		}
	}

	@operator(value = "get_agent_cause", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	public static IAgent getAgentCause(final Predicate pred) {
		if (pred != null) {
			return pred.getAgentCause();
		} else {
			return null;
		}
	}
	
	@operator(value = "get_praiseworthiness", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	public static double getPraise(final Predicate pred) {
		if (pred != null) {
			return pred.getPraiseworthiness();
		} else {
			return 0.0;
		}
	}
	
	@operator(value = "new_emotion", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new emotion with the given properties (name)", examples = @example(value = "emotion(\"joy\")", test = false))
	public static Emotion newEmotion(final String name) throws GamaRuntimeException {
		return new Emotion(name);
	}

	@operator(value = "new_emotion", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new emotion with the given properties (name, intensity)", examples = @example(value = "emotion(\"joy\",12.3)", test = false))
	public static Emotion newEmotion(final String name, final Double intensity) throws GamaRuntimeException {
		return new Emotion(name, intensity);
	}

	@operator(value = "new_emotion", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new emotion with the given properties (name,about)", examples = @example(value = "emotion(\"joy\",eatFood)", test = false))
	public static Emotion newEmotion(final String name, final Predicate about) throws GamaRuntimeException {
		return new Emotion(name, about);
	}

	@operator(value = "new_emotion", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new emotion with the given properties (name)", examples = @example(value = "emotion(\"joy\",12.3,eatFood,4)", test = false))
	public static Emotion newEmotion(final String name, final IAgent agent) throws GamaRuntimeException {
		return new Emotion(name, agent);
	}

	@operator(value = "new_emotion", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new emotion with the given properties (name,intensity,about)", examples = @example(value = "emotion(\"joy\",12.3,eatFood)", test = false))
	public static Emotion newEmotion(final String name, final Double intensity, final Predicate about)
			throws GamaRuntimeException {
		return new Emotion(name, intensity, about);
	}

	@operator(value = "new_emotion", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new emotion with the given properties (name,intensity,decay)", examples = @example(value = "emotion(\"joy\",12.3,4)", test = false))
	public static Emotion newEmotion(final String name, final Double intensity, final Double decay)
			throws GamaRuntimeException {
		return new Emotion(name, intensity, decay);
	}

	@operator(value = "new_emotion", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new emotion with the given properties (name)", examples = @example(value = "emotion(\"joy\",12.3,eatFood,4)", test = false))
	public static Emotion newEmotion(final String name, final Predicate about, final IAgent agent)
			throws GamaRuntimeException {
		return new Emotion(name, about, agent);
	}

	@operator(value = "new_emotion", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new emotion with the given properties (name)", examples = @example(value = "emotion(\"joy\",12.3,eatFood,4)", test = false))
	public static Emotion newEmotion(final String name, final Double intensity, final IAgent agent)
			throws GamaRuntimeException {
		return new Emotion(name, intensity, agent);
	}

	@operator(value = "new_emotion", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new emotion with the given properties (name)", examples = @example(value = "emotion(\"joy\",12.3,eatFood,4)", test = false))
	public static Emotion newEmotion(final String name, final Double intensity, final Predicate about,
			final Double decay) throws GamaRuntimeException {
		return new Emotion(name, intensity, about, decay);
	}

	@operator(value = "new_emotion", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new emotion with the given properties (name)", examples = @example(value = "emotion(\"joy\",12.3,eatFood,4)", test = false))
	public static Emotion newEmotion(final String name, final Double intensity, final Double decay, final IAgent agent)
			throws GamaRuntimeException {
		return new Emotion(name, intensity, decay, agent);
	}

	@operator(value = "new_emotion", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new emotion with the given properties (name)", examples = @example(value = "emotion(\"joy\",12.3,eatFood,4)", test = false))
	public static Emotion newEmotion(final String name, final Double intensity, final Predicate about,
			final IAgent agent) throws GamaRuntimeException {
		return new Emotion(name, intensity, about, agent);
	}

	@operator(value = "new_emotion", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new emotion with the given properties (name)", examples = @example(value = "emotion(\"joy\",12.3,eatFood,4)", test = false))
	public static Emotion newEmotion(final String name, final Double intensity, final Predicate about,
			final Double decay, final IAgent agent) throws GamaRuntimeException {
		return new Emotion(name, intensity, about, decay, agent);
	}

	@operator(value = "set_agent_cause", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "change the agentCause value of the given emotion", examples = @example(value = "emotion set_agent_cause agentA", test = false))
	public static Emotion withAgentCause(final Emotion emotion, final IAgent agent) throws GamaRuntimeException {
		emotion.agentCause = agent;
		return emotion;
	}

	@operator(value = "set_intensity", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "change the intensity value of the given emotion", examples = @example(value = "emotion set_intensity 12", test = false))
	public static Emotion setIntensity(final Emotion emotion, final Double intensity) throws GamaRuntimeException {
		emotion.intensity = intensity;
		return emotion;
	}

	@operator(value = "set_decay", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "change the decay value of the given emotion", examples = @example(value = "emotion set_decay 12", test = false))
	public static Emotion setDecay(final Emotion emotion, final Double decay) throws GamaRuntimeException {
		emotion.decay = decay;
		return emotion;
	}

	@operator(value = "set_about", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "change the about value of the given emotion", examples = @example(value = "emotion set_about predicate1", test = false))
	public static Emotion setAbout(final Emotion emotion, final Predicate about) throws GamaRuntimeException {
		emotion.about = about;
		return emotion;
	}

	@operator(value = "get_intensity", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "get the intensity value of the given emotion", examples = @example(value = "emotion set_intensity 12", test = false))
	public static Double getIntensity(final Emotion emotion) {
		if(emotion!=null){
			return emotion.intensity;
		}else{
			return null;
		}
	}

	@operator(value = "get_decay", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "get the decay value of the given emotion", examples = @example(value = "get_decay(emotion)", test = false))
	public static Double getDecay(final Emotion emotion) {
		if(emotion!=null){
			return emotion.decay;
		}else{
			return null;
		}
	}

	@operator(value = "get_about", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "get the about value of the given emotion", examples = @example(value = "get_about(emotion)", test = false))
	public static Predicate getAbout(final Emotion emotion) {
		if(emotion!=null){
			return emotion.about;
		}else{
			return null;
		}
	}

	@operator(value = "get_agent_cause", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "get the agent cause value of the given emotion", examples = @example(value = "get_agent_cause(emotion)", test = false))
	public static IAgent getAgent(final Emotion emotion) {
		if(emotion!=null){
			return emotion.getAgentCause();
		}else{
			return null;
		}
	}	
	
	// @operator(value = "new_social_link", can_be_const = true, category = {
	// "BDI" },
	// concept = { IConcept.BDI })
	// @doc(value = "a new social link",
	// examples = @example(value = "new_social_link()", test = false))
	// public static SocialLink newSocialLink() throws GamaRuntimeException {
	// return new SocialLink();
	// }

	@operator(value = "new_social_link", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new social link", examples = @example(value = "new_social_link(agentA)", test = false))
	public static SocialLink newSocialLink(final IAgent agent) throws GamaRuntimeException {
		return new SocialLink(agent);
	}

	@operator(value = "new_social_link", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new social link", examples = @example(value = "new_social_link(agentA,0.0,-0.1,0.2,0.1)", test = false))
	public static SocialLink newSocialLink(final IAgent agent, final Double appreciation, final Double dominance,
			final Double solidarity, final Double familiarity) throws GamaRuntimeException {
		return new SocialLink(agent, appreciation, dominance, solidarity, familiarity);
	}

	@operator(value = "set_agent", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "change the agent value of the given social link", examples = @example(value = "social_link set_agent agentA", test = false))
	public static SocialLink setAgent(final SocialLink social, final IAgent agent) {
		social.setAgent(agent);
		return social;
	}

	@operator(value = "set_liking", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "change the liking value of the given social link", examples = @example(value = "social_link set_liking 0.4", test = false))
	public static SocialLink setLiking(final SocialLink social, final Double appreciation) throws GamaRuntimeException {
		if (appreciation >= -1.0 && appreciation <= 1.0) {
			social.setLiking(appreciation);
		}
		return social;
	}

	@operator(value = "set_dominance", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "change the dominance value of the given social link", examples = @example(value = "social_link set_dominance 0.4", test = false))
	public static SocialLink setDominance(final SocialLink social, final Double dominance) throws GamaRuntimeException {
		if (dominance >= -1.0 && dominance < 1.0) {
			social.setDominance(dominance);
		}
		return social;
	}

	@operator(value = "set_solidarity", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "change the solidarity value of the given social link", examples = @example(value = "social_link set_solidarity 0.4", test = false))
	public static SocialLink setSolidarity(final SocialLink social, final Double solidarity)
			throws GamaRuntimeException {
		if (solidarity >= 0.0 && solidarity <= 1.0) {
			social.setSolidarity(solidarity);
		}
		return social;
	}

	@operator(value = "set_familiarity", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "change the familiarity value of the given social link", examples = @example(value = "social_link set_familiarity 0.4", test = false))
	public static SocialLink setFamiliarity(final SocialLink social, final Double familiarity)
			throws GamaRuntimeException {
		if (familiarity >= 0.0 && familiarity <= 1.0) {
			social.setFamiliarity(familiarity);
		}
		return social;
	}

	@operator(value = "get_agent", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "get the agent value of the given social link", examples = @example(value = "get_agent(social_link1)", test = false))
	public static IAgent getAgent(final SocialLink social) {
		if(social!=null){
			return social.getAgent();
		}else{
			return null;
		}
	}

	@operator(value = "get_liking", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "get the liking value of the given social link", examples = @example(value = "get_liking(social_link1)", test = false))
	public static Double getLikink(final SocialLink social) {
		if(social!=null){
			return social.getLiking();
		}else{
			return null;
		}
	}

	@operator(value = "get_dominance", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "get the dominance value of the given social link", examples = @example(value = "get_dominance(social_link1)", test = false))
	public static Double getDominance(final SocialLink social) {
		if(social!=null){
			return social.getDominance();
		}else{
			return null;
		}
	}

	@operator(value = "get_solidarity", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "get the solidarity value of the given social link", examples = @example(value = "get_solidarity(social_link1)", test = false))
	public static Double getSolidarity(final SocialLink social) {
		if(social!=null){
			return social.getSolidarity();
		}else{
			return null;
		}
	}

	@operator(value = "get_familiarity", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "get the familiarity value of the given social link", examples = @example(value = "get_familiarity(social_link1)", test = false))
	public static Double getFamiliarity(final SocialLink social) {
		if(social!=null){
			return social.getFamiliarity();
		}else{
			return null;
		}
	}

	// Faire en sorte que l'on puisse utiliser les opérateurs seulement avec le
	// nom de l'agent ?
	
	
	
	// Faire des opérateurs pour créer des états mentaux (en précisant ou non l'agent propriétaire)
	@operator(value = "new_mental_state", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new mental state", examples = @example(value = "new_social_link(agentA)", test = false))
	public static MentalState newMentalState(final String modality) throws GamaRuntimeException {
		return new MentalState(modality);
	}
	
	@operator(value = "new_mental_state", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new mental state", examples = @example(value = "new_social_link(agentA)", test = false))
	public static MentalState newMentalState(final String modality, final Predicate pred) throws GamaRuntimeException {
		return new MentalState(modality,pred);
	}
	
	@operator(value = "new_mental_state", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new mental state", examples = @example(value = "new_social_link(agentA)", test = false))
	public static MentalState newMentalState(final String modality, final Predicate pred, final Double strength) throws GamaRuntimeException {
		return new MentalState(modality,pred,strength);
	}
	
	@operator(value = "new_mental_state", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new mental state", examples = @example(value = "new_social_link(agentA)", test = false))
	public static MentalState newMentalState(final String modality, final Predicate pred, final IAgent ag) throws GamaRuntimeException {
		return new MentalState(modality,pred,ag);
	}
	
	@operator(value = "new_mental_state", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new mental state", examples = @example(value = "new_social_link(agentA)", test = false))
	public static MentalState newMentalState(final String modality, final Predicate pred, final Integer life) throws GamaRuntimeException {
		return new MentalState(modality,pred,life);
	}
	
	@operator(value = "new_mental_state", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new mental state", examples = @example(value = "new_social_link(agentA)", test = false))
	public static MentalState newMentalState(final String modality, final Predicate pred, final Double strength, final IAgent ag) throws GamaRuntimeException {
		return new MentalState(modality,pred,strength,ag);
	}
	
	@operator(value = "new_mental_state", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new mental state", examples = @example(value = "new_social_link(agentA)", test = false))
	public static MentalState newMentalState(final String modality, final Predicate pred, final Double strength, final Integer life) throws GamaRuntimeException {
		return new MentalState(modality,pred,strength,life);
	}
	
	@operator(value = "new_mental_state", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new mental state", examples = @example(value = "new_social_link(agentA)", test = false))
	public static MentalState newMentalState(final String modality, final Predicate pred, final Integer life, final IAgent ag) throws GamaRuntimeException {
		return new MentalState(modality,pred,life,ag);
	}
	
	@operator(value = "new_mental_state", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new mental state", examples = @example(value = "new_social_link(agentA)", test = false))
	public static MentalState newMentalState(final String modality, final Predicate pred, final Double strength, final Integer life,final IAgent ag) throws GamaRuntimeException {
		return new MentalState(modality,pred,strength,life,ag);
	}
	
	@operator(value = "new_mental_state", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new mental state", examples = @example(value = "new_social_link(agentA)", test = false))
	public static MentalState newMentalState(final String modality, final MentalState pred) throws GamaRuntimeException {
		return new MentalState(modality,pred);
	}
	
	@operator(value = "new_mental_state", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new mental state", examples = @example(value = "new_social_link(agentA)", test = false))
	public static MentalState newMentalState(final String modality, final MentalState pred, final Double strength) throws GamaRuntimeException {
		return new MentalState(modality,pred,strength);
	}
	
	@operator(value = "new_mental_state", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new mental state", examples = @example(value = "new_social_link(agentA)", test = false))
	public static MentalState newMentalState(final String modality, final MentalState pred, final IAgent ag) throws GamaRuntimeException {
		return new MentalState(modality,pred,ag);
	}
	
	@operator(value = "new_mental_state", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new mental state", examples = @example(value = "new_social_link(agentA)", test = false))
	public static MentalState newMentalState(final String modality, final MentalState pred, final Integer life) throws GamaRuntimeException {
		return new MentalState(modality,pred,life);
	}
	
	@operator(value = "new_mental_state", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new mental state", examples = @example(value = "new_social_link(agentA)", test = false))
	public static MentalState newMentalState(final String modality, final MentalState pred, final Double strength, final IAgent ag) throws GamaRuntimeException {
		return new MentalState(modality,pred,strength,ag);
	}
	
	@operator(value = "new_mental_state", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new mental state", examples = @example(value = "new_social_link(agentA)", test = false))
	public static MentalState newMentalState(final String modality, final MentalState pred, final Double strength, final Integer life) throws GamaRuntimeException {
		return new MentalState(modality,pred,strength,life);
	}
	
	@operator(value = "new_mental_state", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new mental state", examples = @example(value = "new_social_link(agentA)", test = false))
	public static MentalState newMentalState(final String modality, final MentalState pred, final Integer life, final IAgent ag) throws GamaRuntimeException {
		return new MentalState(modality,pred,life,ag);
	}
	
	@operator(value = "new_mental_state", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "a new mental state", examples = @example(value = "new_social_link(agentA)", test = false))
	public static MentalState newMentalState(final String modality, final MentalState pred, final Double strength, final Integer life,final IAgent ag) throws GamaRuntimeException {
		return new MentalState(modality,pred,strength,life,ag);
	}
	
	@operator(value = "set_modality", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "change the modality value of the given mental state", examples = @example(value = "mental state set_modality belief", test = false))
	public static MentalState setModalitity(final MentalState mental, final String modality){
			mental.setModality(modality);
		return mental;
	}
	
	@operator(value = "set_predicate", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "change the predicate value of the given mental state", examples = @example(value = "mental state set_predicate pred1", test = false))
	public static MentalState setPredicate(final MentalState mental, final Predicate predicate){
			mental.setPredicate(predicate);
		return mental;
	}
	
	@operator(value = "set_strength", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "change the strength value of the given mental state", examples = @example(value = "mental state set_strength 1.0", test = false))
	public static MentalState setStrength(final MentalState mental, final Double strength){
			mental.setStrength(strength);
		return mental;
	}
	
	@operator(value = "set_lifetime", can_be_const = true, category = { "BDI" }, concept = { IConcept.BDI })
	@doc(value = "change the lifetime value of the given mental state", examples = @example(value = "mental state set_lifetime 1", test = false))
	public static MentalState setLifetime(final MentalState mental, final int life){
			mental.setLifeTime(life);
		return mental;
	}
	
	@operator(value = "get_modality", can_be_const = true, category = {"BDI"}, concept = { IConcept.BDI })
	@doc(value = "get the modality value of the given mental state", examples = @example(value = "get_modality(mental_state1)", test = false))
	public static String getModality(final MentalState mental) {
		if(mental!=null){
			return mental.getModality();
		}else{
			return null;
		}
	}

	@operator(value = "get_predicate", can_be_const = true, category = {"BDI"}, concept = { IConcept.BDI })
	@doc(value = "get the predicate value of the given mental state", examples = @example(value = "get_predicate(mental_state1)", test = false))
	public static Predicate getPredicate(final MentalState mental) {
		if(mental!=null){
			return mental.getPredicate();
		}else{
			return null;
		}
	}
	
	@operator(value = "get_strength", can_be_const = true, category = {"BDI"}, concept = { IConcept.BDI })
	@doc(value = "get the strength value of the given mental state", examples = @example(value = "get_strength(mental_state1)", test = false))
	public static Double getStrength(final MentalState mental) {
		if(mental!=null){
			return mental.getStrength();
		}else{
			return null;
		}
	}
	
	@operator(value = "get_lifetime", can_be_const = true, category = {"BDI"}, concept = { IConcept.BDI })
	@doc(value = "get the lifetime value of the given mental state", examples = @example(value = "get_lifetime(mental_state1)", test = false))
	public static int getLifetime(final MentalState mental) {
		if(mental!=null){
			return mental.getLifeTime();
		}else{
			return -1;
		}
	}
	
	@operator(value = "get_plan_name", can_be_const = true, category = {"BDI"}, concept = { IConcept.BDI })
	@doc(value = "get the name of a given plan", examples = @example(value = "get_plan_name(agent.current_plan)", test = false))
	public static String getPlanName(final BDIPlan plan){
		if(plan!=null && plan.getPlanStatement()!=null){
			return plan.getPlanStatement().getName();
		}else{
			return null;
		}
	}

	
}
