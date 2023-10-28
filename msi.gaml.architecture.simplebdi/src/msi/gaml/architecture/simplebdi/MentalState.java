/*******************************************************************************************************
 *
 * MentalState.java, in msi.gaml.architecture.simplebdi, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.architecture.simplebdi;

import java.util.List;

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
 * The Class MentalState.
 */
@vars ({ @variable (
		name = "modality",
		type = IType.STRING,
		doc = @doc ("the modality of the mental state")),
		@variable (
				name = "predicate",
				type = PredicateType.id,
				doc = @doc ("the predicate about which is the mental state")),
		@variable (
				name = "mental_state",
				type = MentalStateType.id,
				doc = @doc ("the mental state about which is the mental state")),
		@variable (
				name = "emotion",
				type = EmotionType.EMOTIONTYPE_ID,
				doc = @doc ("the emotion about which is the mental state")),
		@variable (
				name = "owner",
				type = IType.AGENT,
				doc = @doc ("the owner of the mental state")),
		@variable (
				name = "strength",
				type = IType.FLOAT,
				doc = @doc ("the strength value related to the mental state")),
		@variable (
				name = "lifetime",
				type = IType.INT,
				doc = @doc ("the lifetime of the mental state")) })
public class MentalState implements IValue {

	/** The modality. */
	String modality;
	
	/** The predicate. */
	Predicate predicate;
	
	/** The strength. */
	Double strength;
	
	/** The lifetime. */
	int lifetime = -1;
	
	/** The is updated. */
	boolean isUpdated = false;
	
	/** The mental. */
	MentalState mental;
	
	/** The emo. */
	Emotion emo;
	
	/** The owner. */
	IAgent owner;
	
	/** The on hold until. */
	List<MentalState> onHoldUntil;
	
	/** The subintentions. */
	List<MentalState> subintentions;
	
	/** The super intention. */
	MentalState superIntention;

	/**
	 * Gets the modality.
	 *
	 * @return the modality
	 */
	@getter ("modality")
	public String getModality() {
		return modality;
	}

	/**
	 * Gets the predicate.
	 *
	 * @return the predicate
	 */
	@getter ("predicate")
	public Predicate getPredicate() {
		return predicate;
	}

	/**
	 * Gets the mental state.
	 *
	 * @return the mental state
	 */
	@getter ("mental_state")
	public MentalState getMentalState() {
		return mental;
	}

	/**
	 * Gets the emotion.
	 *
	 * @return the emotion
	 */
	@getter ("emotion")
	public Emotion getEmotion() {
		return emo;
	}

	/**
	 * Gets the strength.
	 *
	 * @return the strength
	 */
	@getter ("strength")
	public Double getStrength() {
		return strength;
	}

	/**
	 * Gets the life time.
	 *
	 * @return the life time
	 */
	@getter ("lifetime")
	public int getLifeTime() {
		return lifetime;
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
	 * Gets the subintentions.
	 *
	 * @return the subintentions
	 */
	@getter ("subintentions")
	public List<MentalState> getSubintentions() {
		return subintentions;
	}

	/**
	 * Gets the super intention.
	 *
	 * @return the super intention
	 */
	@getter ("superIntention")
	public MentalState getSuperIntention() {
		return superIntention;
	}

	/**
	 * Gets the on hold until.
	 *
	 * @return the on hold until
	 */
	public List<MentalState> getOnHoldUntil() {
		return onHoldUntil;
	}

	/**
	 * Sets the modality.
	 *
	 * @param mod the new modality
	 */
	public void setModality(final String mod) {
		this.modality = mod;
	}

	/**
	 * Sets the predicate.
	 *
	 * @param pred the new predicate
	 */
	public void setPredicate(final Predicate pred) {
		this.predicate = pred;
	}

	/**
	 * Sets the mental state.
	 *
	 * @param ment the new mental state
	 */
	public void setMentalState(final MentalState ment) {
		this.mental = ment;
	}

	/**
	 * Sets the emotion.
	 *
	 * @param em the new emotion
	 */
	public void setEmotion(final Emotion em) {
		this.emo = em;
	}

	/**
	 * Sets the strength.
	 *
	 * @param stre the new strength
	 */
	public void setStrength(final Double stre) {
		this.strength = stre;
	}

	/**
	 * Sets the life time.
	 *
	 * @param life the new life time
	 */
	public void setLifeTime(final int life) {
		this.lifetime = life;
	}

	/**
	 * Sets the owner.
	 *
	 * @param ag the new owner
	 */
	public void setOwner(final IAgent ag) {
		this.owner = ag;
	}

	/**
	 * Sets the subintentions.
	 *
	 * @param subintentions the new subintentions
	 */
	public void setSubintentions(final List<MentalState> subintentions) {
		this.subintentions = subintentions;
	}

	/**
	 * Sets the super intention.
	 *
	 * @param superPredicate the new super intention
	 */
	public void setSuperIntention(final MentalState superPredicate) {
		this.superIntention = superPredicate;
	}

	/**
	 * Sets the on hold until.
	 *
	 * @param onHoldUntil the new on hold until
	 */
	public void setOnHoldUntil(final List<MentalState> onHoldUntil) {
		this.onHoldUntil = onHoldUntil;
	}

	/**
	 * Update lifetime.
	 */
	public void updateLifetime() {
		if (this.lifetime > 0 && !this.isUpdated) {
			this.lifetime = this.lifetime - 1;
			this.isUpdated = true;
		}
	}

	/**
	 * Instantiates a new mental state.
	 */
	public MentalState() {
		super();
		this.modality = "";
		this.predicate = null;
		this.mental = null;
		this.strength = 1.0;
		this.owner = null;
		this.emo = null;
	}

	/**
	 * Instantiates a new mental state.
	 *
	 * @param mod the mod
	 */
	public MentalState(final String mod) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = null;
		this.strength = 1.0;
		this.owner = null;
		this.emo = null;
	}

	/**
	 * Instantiates a new mental state.
	 *
	 * @param mod the mod
	 * @param pred the pred
	 */
	public MentalState(final String mod, final Predicate pred) {
		super();
		this.modality = mod;
		this.predicate = pred;
		this.mental = null;
		this.strength = 1.0;
		this.owner = null;
		this.emo = null;
	}

	/**
	 * Instantiates a new mental state.
	 *
	 * @param mod the mod
	 * @param ment the ment
	 */
	public MentalState(final String mod, final MentalState ment) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = ment;
		this.strength = 1.0;
		this.owner = null;
		this.emo = null;
	}

	/**
	 * Instantiates a new mental state.
	 *
	 * @param mod the mod
	 * @param em the em
	 */
	public MentalState(final String mod, final Emotion em) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = null;
		this.strength = 1.0;
		this.owner = null;
		this.emo = em;
	}

	/**
	 * Instantiates a new mental state.
	 *
	 * @param mod the mod
	 * @param pred the pred
	 * @param ag the ag
	 */
	public MentalState(final String mod, final Predicate pred, final IAgent ag) {
		super();
		this.modality = mod;
		this.predicate = pred;
		this.mental = null;
		this.strength = 1.0;
		this.owner = ag;
		this.emo = null;
	}

	/**
	 * Instantiates a new mental state.
	 *
	 * @param mod the mod
	 * @param ment the ment
	 * @param ag the ag
	 */
	public MentalState(final String mod, final MentalState ment, final IAgent ag) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = ment;
		this.strength = 1.0;
		this.owner = ag;
		this.emo = null;
	}

	/**
	 * Instantiates a new mental state.
	 *
	 * @param mod the mod
	 * @param em the em
	 * @param ag the ag
	 */
	public MentalState(final String mod, final Emotion em, final IAgent ag) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = null;
		this.strength = 1.0;
		this.owner = ag;
		this.emo = em;
	}

	/**
	 * Instantiates a new mental state.
	 *
	 * @param mod the mod
	 * @param pred the pred
	 * @param stre the stre
	 */
	public MentalState(final String mod, final Predicate pred, final Double stre) {
		super();
		this.modality = mod;
		this.predicate = pred;
		this.mental = null;
		this.strength = stre;
		this.emo = null;
	}

	/**
	 * Instantiates a new mental state.
	 *
	 * @param mod the mod
	 * @param ment the ment
	 * @param stre the stre
	 */
	public MentalState(final String mod, final MentalState ment, final Double stre) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = ment;
		this.strength = stre;
		this.emo = null;
	}

	/**
	 * Instantiates a new mental state.
	 *
	 * @param mod the mod
	 * @param em the em
	 * @param stre the stre
	 */
	public MentalState(final String mod, final Emotion em, final Double stre) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = null;
		this.strength = stre;
		this.emo = em;
	}

	/**
	 * Instantiates a new mental state.
	 *
	 * @param mod the mod
	 * @param pred the pred
	 * @param life the life
	 */
	public MentalState(final String mod, final Predicate pred, final int life) {
		super();
		this.modality = mod;
		this.predicate = pred;
		this.mental = null;
		this.lifetime = life;
		this.strength = 1.0;
		this.emo = null;
	}

	/**
	 * Instantiates a new mental state.
	 *
	 * @param mod the mod
	 * @param ment the ment
	 * @param life the life
	 */
	public MentalState(final String mod, final MentalState ment, final int life) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = ment;
		this.lifetime = life;
		this.strength = 1.0;
		this.emo = null;
	}

	/**
	 * Instantiates a new mental state.
	 *
	 * @param mod the mod
	 * @param em the em
	 * @param life the life
	 */
	public MentalState(final String mod, final Emotion em, final int life) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = null;
		this.lifetime = life;
		this.strength = 1.0;
		this.emo = em;
	}

	/**
	 * Instantiates a new mental state.
	 *
	 * @param mod the mod
	 * @param pred the pred
	 * @param stre the stre
	 * @param life the life
	 */
	public MentalState(final String mod, final Predicate pred, final Double stre, final int life) {
		super();
		this.modality = mod;
		this.predicate = pred;
		this.mental = null;
		this.strength = stre;
		this.lifetime = life;
		this.emo = null;
	}

	/**
	 * Instantiates a new mental state.
	 *
	 * @param mod the mod
	 * @param ment the ment
	 * @param stre the stre
	 * @param life the life
	 */
	public MentalState(final String mod, final MentalState ment, final Double stre, final int life) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = ment;
		this.strength = stre;
		this.lifetime = life;
		this.emo = null;
	}

	/**
	 * Instantiates a new mental state.
	 *
	 * @param mod the mod
	 * @param em the em
	 * @param stre the stre
	 * @param life the life
	 */
	public MentalState(final String mod, final Emotion em, final Double stre, final int life) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = null;
		this.strength = stre;
		this.lifetime = life;
		this.emo = em;
	}

	/**
	 * Instantiates a new mental state.
	 *
	 * @param mod the mod
	 * @param pred the pred
	 * @param stre the stre
	 * @param ag the ag
	 */
	public MentalState(final String mod, final Predicate pred, final Double stre, final IAgent ag) {
		super();
		this.modality = mod;
		this.predicate = pred;
		this.mental = null;
		this.strength = stre;
		this.owner = ag;
		this.emo = null;
	}

	/**
	 * Instantiates a new mental state.
	 *
	 * @param mod the mod
	 * @param ment the ment
	 * @param stre the stre
	 * @param ag the ag
	 */
	public MentalState(final String mod, final MentalState ment, final Double stre, final IAgent ag) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = ment;
		this.strength = stre;
		this.owner = ag;
		this.emo = null;
	}

	/**
	 * Instantiates a new mental state.
	 *
	 * @param mod the mod
	 * @param em the em
	 * @param stre the stre
	 * @param ag the ag
	 */
	public MentalState(final String mod, final Emotion em, final Double stre, final IAgent ag) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = null;
		this.strength = stre;
		this.owner = ag;
		this.emo = em;
	}

	/**
	 * Instantiates a new mental state.
	 *
	 * @param mod the mod
	 * @param pred the pred
	 * @param life the life
	 * @param ag the ag
	 */
	public MentalState(final String mod, final Predicate pred, final int life, final IAgent ag) {
		super();
		this.modality = mod;
		this.predicate = pred;
		this.mental = null;
		this.strength = 1.0;
		this.lifetime = life;
		this.owner = ag;
		this.emo = null;
	}

	/**
	 * Instantiates a new mental state.
	 *
	 * @param mod the mod
	 * @param ment the ment
	 * @param life the life
	 * @param ag the ag
	 */
	public MentalState(final String mod, final MentalState ment, final int life, final IAgent ag) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = ment;
		this.strength = 1.0;
		this.lifetime = life;
		this.owner = ag;
		this.emo = null;
	}

	/**
	 * Instantiates a new mental state.
	 *
	 * @param mod the mod
	 * @param em the em
	 * @param life the life
	 * @param ag the ag
	 */
	public MentalState(final String mod, final Emotion em, final int life, final IAgent ag) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = null;
		this.strength = 1.0;
		this.lifetime = life;
		this.owner = ag;
		this.emo = em;
	}

	/**
	 * Instantiates a new mental state.
	 *
	 * @param mod the mod
	 * @param pred the pred
	 * @param stre the stre
	 * @param life the life
	 * @param ag the ag
	 */
	public MentalState(final String mod, final Predicate pred, final Double stre, final int life, final IAgent ag) {
		super();
		this.modality = mod;
		this.predicate = pred;
		this.mental = null;
		this.strength = stre;
		this.lifetime = life;
		this.owner = ag;
		this.emo = null;
	}

	/**
	 * Instantiates a new mental state.
	 *
	 * @param mod the mod
	 * @param ment the ment
	 * @param stre the stre
	 * @param life the life
	 * @param ag the ag
	 */
	public MentalState(final String mod, final MentalState ment, final Double stre, final int life, final IAgent ag) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = ment;
		this.strength = stre;
		this.lifetime = life;
		this.owner = ag;
		this.emo = null;
	}

	/**
	 * Instantiates a new mental state.
	 *
	 * @param mod the mod
	 * @param em the em
	 * @param stre the stre
	 * @param life the life
	 * @param ag the ag
	 */
	public MentalState(final String mod, final Emotion em, final Double stre, final int life, final IAgent ag) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = null;
		this.strength = stre;
		this.lifetime = life;
		this.owner = ag;
		this.emo = em;
	}

	@Override
	public String toString() {
		return serializeToGaml(true);
	}

	@Override
	public String serializeToGaml(final boolean includingBuiltIn) {
		return modality + "(" + (predicate == null ? "" : predicate) + (mental == null ? "" : mental)
				+ (emo == null ? "" : emo) + "," + (owner == null ? "" : owner) + "," + strength + "," + lifetime + ")";
	}

	@Override
	public IType<?> getGamlType() {
		return Types.get(MentalStateType.id);
	}

	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return modality + "(" + (predicate == null ? "" : predicate) + (mental == null ? "" : mental)
				+ (emo == null ? "" : emo) + "," + (owner == null ? "" : owner) + "," + strength + "," + lifetime + ")";
	}

	@Override
	public IValue copy(final IScope scope) throws GamaRuntimeException {
		final MentalState tempMental = new MentalState(modality);
		tempMental.setLifeTime(lifetime);
		tempMental.setStrength(strength);
		tempMental.setOwner(owner);
		if (predicate != null) {
			tempMental.setPredicate(predicate);
			return tempMental;
		} else if (mental != null) {
			tempMental.setMentalState(mental);
			return tempMental;
		} else if (emo != null) {
			tempMental.setEmotion(emo);
			return tempMental;
		}
		return tempMental;
	}

	@Override
	public int hashCode() {
		// final int prime = 31;
		final int result = 1;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		final MentalState other = (MentalState) obj;
		// if(other.getModality()!=this.modality){return false;}
		if (this.predicate == null && other.getPredicate() != null) { return false; }
		if (this.predicate != null && other.getPredicate() == null) { return false; }
		if (this.predicate != null && other.getPredicate() != null) {
			if (!other.getPredicate().equals(this.predicate)) { return false; }
		}
		if (this.mental == null && other.getMentalState() != null) { return false; }
		if (this.mental != null && other.getMentalState() == null) { return false; }
		if (this.mental != null && other.getMentalState() != null) {
			if (!other.getMentalState().equals(this.mental)) { return false; }
		}
		if (this.emo == null && other.getEmotion() != null) { return false; }
		if (this.emo != null && other.getEmotion() == null) { return false; }
		if (this.emo != null && other.getEmotion() != null) {
			if (!other.getEmotion().equals(this.emo)) { return false; }
		}
		// if (subintentions == null) {
		// if (other.subintentions != null && !other.subintentions.isEmpty()) {
		// return false;
		// }
		// } else if (!subintentions.equals(other.subintentions)) {
		// return false;
		// }
		// if (superIntention == null) {
		// if (other.superIntention != null) {
		// return false;
		// }
		// } else if (superIntention.partialEquality(other.superIntention)) {
		// return false;
		// }
		if (this.owner != null && other.getOwner() != null) {
			if (!other.getOwner().equals(this.owner)) { return false; }
		}
		// if(other.getStrength()!=this.strength){return false;}
		return true;
	}

	// private boolean partialEquality(final Object obj) {
	// // You don't test the sub-intentions. Used when testing the equality of
	// // the super-intention
	// if (this == obj) { return true; }
	// if (obj == null) { return false; }
	// if (getClass() != obj.getClass()) { return false; }
	// final MentalState other = (MentalState) obj;
	// // if(other.getModality()!=this.modality){return false;}
	// if (this.predicate == null && other.getPredicate() != null) { return false; }
	// if (this.predicate != null && other.getPredicate() == null) { return false; }
	// if (this.predicate != null && other.getPredicate() != null) {
	// if (!other.getPredicate().equals(this.predicate)) { return false; }
	// }
	// if (this.mental == null && other.getMentalState() != null) { return false; }
	// if (this.mental != null && other.getMentalState() == null) { return false; }
	// if (this.mental != null && other.getMentalState() != null) {
	// if (!other.getMentalState().equals(this.mental)) { return false; }
	// }
	// if (this.emo == null && other.getEmotion() != null) { return false; }
	// if (this.emo != null && other.getEmotion() == null) { return false; }
	// if (this.emo != null && other.getEmotion() != null) {
	// if (!other.getEmotion().equals(this.emo)) { return false; }
	// }
	// if (superIntention == null) {
	// if (other.superIntention != null) { return false; }
	// } else if (superIntention.partialEquality(other.superIntention)) { return false; }
	// if (this.owner != null && other.getOwner() != null) {
	// if (!other.getOwner().equals(this.owner)) { return false; }
	// }
	// // if(other.getStrength()!=this.strength){return false;}
	// return true;
	// }

}
