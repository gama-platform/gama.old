/*******************************************************************************************************
 *
 * Emotion.java, in msi.gaml.architecture.simplebdi, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.architecture.simplebdi;

import msi.gama.common.interfaces.IValue;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class Emotion.
 */
@vars ({ @variable (
		name = "name",
		type = IType.STRING,
		doc = @doc ("the name of the emotion")),
		@variable (
				name = "intensity",
				type = IType.FLOAT,
				doc = @doc ("the intensity of the emotion")),
		@variable (
				name = "about",
				type = PredicateType.id,
				doc = @doc ("the predicate about which is the emotion")),
		@variable (
				name = "decay",
				type = IType.FLOAT,
				doc = @doc ("the decay value of the emotion")),
		@variable (
				name = "agentCause",
				type = IType.AGENT,
				doc = @doc ("the agent causing the emotion")),
		@variable (
				name = "owner",
				type = IType.AGENT,
				doc = @doc ("the agent owning the emotion")) })
public class Emotion implements IValue {

	/** The name. */
	String name;
	
	/** The intensity. */
	Double intensity = -1.0;
	
	/** The about. */
	Predicate about;
	
	/** The decay. */
	Double decay = 0.0;
	
	/** The agent cause. */
	IAgent agentCause;
	
	/** The owner. */
	IAgent owner;
	
	/** The no agent cause. */
	private boolean noAgentCause = true;
	
	/** The no intensity. */
	private boolean noIntensity = true;
	
	/** The no about. */
	private boolean noAbout = true;

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@getter ("name")
	public String getName() {
		return name;
	}

	/**
	 * Gets the intensity.
	 *
	 * @return the intensity
	 */
	@getter ("intensity")
	public Double getIntensity() {
		return intensity;
	}

	/**
	 * Gets the about.
	 *
	 * @return the about
	 */
	@getter ("about")
	public Predicate getAbout() {
		return about;
	}

	/**
	 * Gets the decay.
	 *
	 * @return the decay
	 */
	@getter ("decay")
	public Double getDecay() {
		return decay;
	}

	/**
	 * Gets the agent cause.
	 *
	 * @return the agent cause
	 */
	@getter ("agentCause")
	public IAgent getAgentCause() {
		return agentCause;
	}

	/**
	 * Gets the owner.
	 *
	 * @return the owner
	 */
	@getter ("owner")
	public IAgent getOwner() {
		return owner;
	}

	/**
	 * Gets the no intensity.
	 *
	 * @return the no intensity
	 */
	public boolean getNoIntensity() {
		return this.noIntensity;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Sets the intensity.
	 *
	 * @param intens the new intensity
	 */
	public void setIntensity(final Double intens) {
		this.intensity = intens;
		this.noIntensity = false;
	}

	/**
	 * Sets the about.
	 *
	 * @param ab the new about
	 */
	public void setAbout(final Predicate ab) {
		this.about = ab;
		this.noAbout = false;
	}

	/**
	 * Sets the decay.
	 *
	 * @param de the new decay
	 */
	public void setDecay(final Double de) {
		this.decay = de;
	}

	/**
	 * Sets the agent cause.
	 *
	 * @param ag the new agent cause
	 */
	public void setAgentCause(final IAgent ag) {
		this.agentCause = ag;
		this.noAgentCause = false;
	}

	/**
	 * Sets the owner.
	 *
	 * @param own the new owner
	 */
	public void setOwner(final IAgent own) {
		this.owner = own;
	}

	/**
	 * Instantiates a new emotion.
	 */
	public Emotion() {
		this.name = "";
		this.about = null;
		this.agentCause = null;
		this.owner = null;
	}

	/**
	 * Instantiates a new emotion.
	 *
	 * @param name the name
	 */
	public Emotion(final String name) {
		this.name = name;
		this.about = null;
		this.agentCause = null;
		this.owner = null;
	}

	/**
	 * Instantiates a new emotion.
	 *
	 * @param name the name
	 * @param intensity2 the intensity 2
	 */
	public Emotion(final String name, final Double intensity2) {
		this.name = name;
		this.intensity = intensity2;
		this.about = null;
		this.agentCause = null;
		this.owner = null;
		this.noIntensity = false;
	}

	/**
	 * Instantiates a new emotion.
	 *
	 * @param name the name
	 * @param ab the ab
	 */
	public Emotion(final String name, final Predicate ab) {
		this.name = name;
		this.about = ab;
		this.agentCause = null;
		this.owner = null;
		this.noAbout = false;
	}

	/**
	 * Instantiates a new emotion.
	 *
	 * @param name the name
	 * @param ag the ag
	 */
	public Emotion(final String name, final IAgent ag) {
		this.name = name;
		this.about = null;
		this.agentCause = ag;
		this.owner = null;
		this.noAgentCause = ag == null;
	}

	/**
	 * Instantiates a new emotion.
	 *
	 * @param name the name
	 * @param intens the intens
	 * @param de the de
	 */
	public Emotion(final String name, final Double intens, final Double de) {
		this.name = name;
		this.intensity = intens;
		this.about = null;
		this.agentCause = null;
		this.owner = null;
		this.decay = de;
		this.noIntensity = false;
		this.noAbout = false;
	}

	/**
	 * Instantiates a new emotion.
	 *
	 * @param name the name
	 * @param intens the intens
	 * @param ab the ab
	 */
	public Emotion(final String name, final Double intens, final Predicate ab) {
		this.name = name;
		this.intensity = intens;
		this.about = ab;
		this.agentCause = null;
		this.owner = null;
		this.noIntensity = false;
		this.noAbout = false;
	}

	/**
	 * Instantiates a new emotion.
	 *
	 * @param name the name
	 * @param ab the ab
	 * @param ag the ag
	 */
	public Emotion(final String name, final Predicate ab, final IAgent ag) {
		this.name = name;
		this.about = ab;
		this.agentCause = ag;
		this.owner = null;
		this.noAbout = false;
		this.noAgentCause = ag == null;
	}

	/**
	 * Instantiates a new emotion.
	 *
	 * @param name the name
	 * @param intens the intens
	 * @param ag the ag
	 */
	public Emotion(final String name, final Double intens, final IAgent ag) {
		this.name = name;
		this.intensity = intens;
		this.about = null;
		this.agentCause = ag;
		this.owner = null;
		this.noIntensity = false;
		this.noAgentCause = ag == null;
	}

	/**
	 * Instantiates a new emotion.
	 *
	 * @param name the name
	 * @param intens the intens
	 * @param ab the ab
	 * @param de the de
	 */
	public Emotion(final String name, final Double intens, final Predicate ab, final Double de) {
		this.name = name;
		this.intensity = intens;
		this.about = ab;
		this.agentCause = null;
		this.owner = null;
		this.decay = de;
		this.noIntensity = false;
		this.noAbout = false;
	}

	/**
	 * Instantiates a new emotion.
	 *
	 * @param name the name
	 * @param intens the intens
	 * @param de the de
	 * @param ag the ag
	 */
	public Emotion(final String name, final Double intens, final Double de, final IAgent ag) {
		this.name = name;
		this.intensity = intens;
		this.about = null;
		this.agentCause = ag;
		this.owner = null;
		this.decay = de;
		this.noIntensity = false;
		this.noAgentCause = ag == null;
	}

	/**
	 * Instantiates a new emotion.
	 *
	 * @param name the name
	 * @param intens the intens
	 * @param ab the ab
	 * @param ag the ag
	 */
	public Emotion(final String name, final Double intens, final Predicate ab, final IAgent ag) {
		this.name = name;
		this.intensity = intens;
		this.about = ab;
		this.agentCause = ag;
		this.owner = null;
		this.noIntensity = false;
		this.noAgentCause = ag == null;
		this.noAbout = false;
	}

	/**
	 * Instantiates a new emotion.
	 *
	 * @param name the name
	 * @param intens the intens
	 * @param ab the ab
	 * @param de the de
	 * @param ag the ag
	 */
	public Emotion(final String name, final Double intens, final Predicate ab, final Double de, final IAgent ag) {
		this.name = name;
		this.intensity = intens;
		this.about = ab;
		this.agentCause = ag;
		this.owner = null;
		this.decay = de;
		this.noIntensity = false;
		this.noAbout = false;
		this.noAgentCause = ag == null;
	}

	/**
	 * Decay intensity.
	 */
	public void decayIntensity() {
		this.intensity = this.intensity - this.decay;
	}

	@Override
	public String toString() {
		return serialize(true);
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		if (intensity > 0) {
			return "emotion(" + name + "," + intensity + "," + about + "," + decay + "," + agentCause + "," + owner
					+ ")";
		}
		return "emotion(" + name + "," + about + "," + decay + "," + agentCause + "," + owner + ")";
	}

	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return name;
	}

	@Override
	public IValue copy(final IScope scope) throws GamaRuntimeException {
		return new Emotion(name, intensity, about, decay, agentCause);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		final Emotion other = (Emotion) obj;
		if (name == null) {
			if (other.name != null) { return false; }
		} else if (!name.equals(other.name)) { return false; }
		if (noAbout && noAgentCause || other.noAbout && other.noAgentCause) { return true; }
		/*
		 * if(about==null){ if(other.about!=null){return false;} }else
		 */if (about != null && other.about != null && !about.equalsEmotions(other.about)) { return false; }
		/*
		 * if(agentCause==null){ if(other.agentCause!=null){return false;} }else
		 */
		/*
		 * if (agentCause != null && other.agentCause != null && !agentCause.equals(other.agentCause)) { return false; }
		 */
		if (owner != null && other.owner != null && !owner.equals(other.owner)) { return false; }
		return true;
	}

	@Override
	public IType<?> getGamlType() {
		return Types.get(EmotionType.EMOTIONTYPE_ID);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
