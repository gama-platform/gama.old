/*******************************************************************************************************
 *
 * Predicate.java, in msi.gaml.architecture.simplebdi, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.architecture.simplebdi;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import msi.gama.common.interfaces.IValue;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gama.util.IMap;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class Predicate.
 */
@vars ({ @variable (
		name = "name",
		type = IType.STRING,
		doc = @doc ("the name of the predicate")),
		@variable (
				name = "is_true",
				type = IType.BOOL,
				doc = @doc ("the truth value of the predicate")),
		@variable (
				name = "values",
				type = IType.MAP,
				doc = @doc ("the values attached to the predicate")),
		@variable (
				name = "date",
				type = IType.FLOAT,
				doc = @doc ("the date of the predicate")),
		@variable (
				name = "subintentions",
				type = IType.LIST,
				doc = @doc ("the subintentions of the predicate")),
		@variable (
				name = "on_hold_until",
				type = IType.NONE,
				doc = @doc ("the list of intention that must be fullfiled before resuming to an intention related to this predicate")),
		@variable (
				name = "super_intention",
				type = IType.NONE,
				doc = @doc ("the super-intention of the predicate")),
		@variable (
				name = "agentCause",
				type = IType.AGENT,
				doc = @doc ("the agent causing the predicate")) })
public class Predicate implements IValue {

	/** The name. */
	String name;

	/** The values. */
	IMap<String, Object> values;

	/** The date. */
	Double date;

	/** The on hold until. */
	List<MentalState> onHoldUntil;

	/** The subintentions. */
	List<MentalState> subintentions;

	/** The super intention. */
	MentalState superIntention;

	/** The agent cause. */
	IAgent agentCause;

	/** The every possible values. */
	boolean everyPossibleValues = false;

	/** The is true. */
	boolean is_true = true;

	/** The is updated. */
	// int lifetime = -1;
	boolean isUpdated = false;

	/** The no agent cause. */
	private boolean noAgentCause = true;

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@getter ("name")
	public String getName() { return name; }

	/**
	 * Gets the values.
	 *
	 * @return the values
	 */
	@getter ("values")
	public IMap<String, Object> getValues() { return values; }

	/**
	 * Gets the checks if is true.
	 *
	 * @return the checks if is true
	 */
	@getter ("is_true")
	public Boolean getIs_True() { return is_true; }

	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	@getter ("date")
	public Double getDate() { return date; }

	/**
	 * Gets the subintentions.
	 *
	 * @return the subintentions
	 */
	@getter ("subintentions")
	public List<MentalState> getSubintentions() { return subintentions; }

	/**
	 * Gets the super intention.
	 *
	 * @return the super intention
	 */
	@getter ("superIntention")
	public MentalState getSuperIntention() { return superIntention; }

	/**
	 * Gets the agent cause.
	 *
	 * @return the agent cause
	 */
	@getter ("agentCause")
	public IAgent getAgentCause() { return agentCause; }

	/**
	 * Gets the on hold until.
	 *
	 * @return the on hold until
	 */
	public List<MentalState> getOnHoldUntil() { return onHoldUntil; }

	// public int getLifetime() {
	// return lifetime;
	// }

	/**
	 * Sets the super intention.
	 *
	 * @param superPredicate
	 *            the new super intention
	 */
	public void setSuperIntention(final MentalState superPredicate) { this.superIntention = superPredicate; }

	/**
	 * Sets the on hold until.
	 *
	 * @param onHoldUntil
	 *            the new on hold until
	 */
	public void setOnHoldUntil(final List<MentalState> onHoldUntil) { this.onHoldUntil = onHoldUntil; }

	/**
	 * Sets the values.
	 *
	 * @param values
	 *            the values
	 */
	public void setValues(final IMap<String, Object> values) {
		this.values = values;
		everyPossibleValues = values == null;
	}

	/**
	 * Sets the checks if is true.
	 *
	 * @param ist
	 *            the new checks if is true
	 */
	public void setIs_True(final Boolean ist) { this.is_true = ist; }

	/**
	 * Sets the date.
	 *
	 * @param date
	 *            the new date
	 */
	public void setDate(final Double date) { this.date = date; }

	/**
	 * Sets the subintentions.
	 *
	 * @param subintentions
	 *            the new subintentions
	 */
	public void setSubintentions(final List<MentalState> subintentions) { this.subintentions = subintentions; }

	// public void setLifetime(final int lifetime) {
	// this.lifetime = lifetime;
	// }

	/**
	 * Sets the agent cause.
	 *
	 * @param ag
	 *            the new agent cause
	 */
	public void setAgentCause(final IAgent ag) {
		this.agentCause = ag;
		this.noAgentCause = false;
	}

	/**
	 * Instantiates a new predicate.
	 */
	public Predicate() {
		this.name = "";
		everyPossibleValues = true;
		this.agentCause = null;
	}

	/**
	 * Instantiates a new predicate.
	 *
	 * @param name
	 *            the name
	 */
	public Predicate(final String name) {
		this.name = name;
		everyPossibleValues = true;
		this.agentCause = null;
	}

	/**
	 * Instantiates a new predicate.
	 *
	 * @param name
	 *            the name
	 * @param ist
	 *            the ist
	 */
	public Predicate(final String name, final boolean ist) {
		this.name = name;
		everyPossibleValues = true;
		is_true = ist;
		this.agentCause = null;
	}

	// public Predicate(final String name, final int lifetime) {
	// super();
	// this.name = name;
	// everyPossibleValues = true;
	// this.lifetime = lifetime;
	// this.agentCause = null;
	// }

	/**
	 * Instantiates a new predicate.
	 *
	 * @param name
	 *            the name
	 * @param values
	 *            the values
	 */
	public Predicate(final String name, final IMap<String, Object> values) {
		this.name = name;
		this.values = values;
		everyPossibleValues = values == null;
		this.agentCause = null;
	}

	/**
	 * Instantiates a new predicate.
	 *
	 * @param name
	 *            the name
	 * @param ag
	 *            the ag
	 */
	public Predicate(final String name, final IAgent ag) {
		this.name = name;
		this.agentCause = ag;
		this.noAgentCause = ag == null;
		everyPossibleValues = true;
	}

	/**
	 * Instantiates a new predicate.
	 *
	 * @param name
	 *            the name
	 * @param values
	 *            the values
	 * @param truth
	 *            the truth
	 */
	public Predicate(final String name, final IMap<String, Object> values, final Boolean truth) {
		this.name = name;
		this.values = values;
		this.is_true = truth;
		everyPossibleValues = values == null;
		this.agentCause = null;
	}
	//
	// public Predicate(final String name, final Map<String, Object> values, final int lifetime) {
	// super();
	// this.name = name;
	// this.values = values;
	// this.lifetime = lifetime;
	// everyPossibleValues = values == null;
	// this.agentCause = null;
	// }

	/**
	 * Instantiates a new predicate.
	 *
	 * @param name
	 *            the name
	 * @param values
	 *            the values
	 * @param ag
	 *            the ag
	 */
	public Predicate(final String name, final IMap<String, Object> values, final IAgent ag) {
		this.name = name;
		this.values = values;
		everyPossibleValues = values == null;
		this.agentCause = ag;
		this.noAgentCause = ag == null;
	}
	//
	// public Predicate(final String name, final Map<String, Object> values, final int lifetime, final Boolean truth) {
	// super();
	// this.name = name;
	// this.values = values;
	// this.lifetime = lifetime;
	// this.is_true = truth;
	// everyPossibleValues = values == null;
	// }

	/**
	 * Instantiates a new predicate.
	 *
	 * @param name
	 *            the name
	 * @param values
	 *            the values
	 * @param truth
	 *            the truth
	 * @param ag
	 *            the ag
	 */
	public Predicate(final String name, final IMap<String, Object> values, final Boolean truth, final IAgent ag) {
		this.name = name;
		this.values = values;
		this.is_true = truth;
		everyPossibleValues = values == null;
		this.agentCause = ag;
		this.noAgentCause = ag == null;
	}
	//
	// public Predicate(final String name, final Map<String, Object> values, final int lifetime, final IAgent ag) {
	// super();
	// this.name = name;
	// this.values = values;
	// this.lifetime = lifetime;
	// everyPossibleValues = values == null;
	// this.agentCause = ag;
	// this.noAgentCause = ag == null;
	// }

	// public Predicate(final String name, final Map<String, Object> values, final int lifetime, final Boolean truth,
	// final IAgent ag) {
	// super();
	// this.name = name;
	// this.values = values;
	// this.lifetime = lifetime;
	// this.is_true = truth;
	// everyPossibleValues = values == null;
	// this.agentCause = ag;
	// this.noAgentCause = ag == null;
	// }

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            the new name
	 */
	public void setName(final String name) {
		this.name = name;

	}

	@Override
	public String toString() {
		return serialize(true);
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return "predicate(" + name + (values == null ? "" : "," + values) + (agentCause == null ? "" : "," + agentCause)
				+ "," + is_true + ")";
	}

	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return "predicate(" + name + (values == null ? "" : "," + values) + (agentCause == null ? "" : "," + agentCause)
				+ "," + is_true + ")";
	}

	@Override
	public Predicate copy(final IScope scope) throws GamaRuntimeException {
		return new Predicate(name, values == null ? null : ((GamaMap<String, Object>) values).copy(scope));
	}

	/**
	 * Copy.
	 *
	 * @return the predicate
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public Predicate copy() throws GamaRuntimeException {
		if (values != null && agentCause != null) return new Predicate(name,
				((GamaMap<String, Object>) values).copy(GAMA.getRuntimeScope()), is_true, agentCause);
		if (values != null) return new Predicate(name, ((GamaMap<String, Object>) values).copy(GAMA.getRuntimeScope()));
		return new Predicate(name);
	}

	// public void updateLifetime() {
	// if (this.lifetime > 0 && !this.isUpdated) {
	// this.lifetime = this.lifetime - 1;
	// this.isUpdated = true;
	// }
	// }

	/**
	 * Checks if is similar name.
	 *
	 * @param other
	 *            the other
	 * @return true, if is similar name
	 */
	public boolean isSimilarName(final Predicate other) {
		if (this == other) return true;
		if ((other == null) || !Objects.equals(name, other.name)) return false;
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, values);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		final Predicate other = (Predicate) obj;
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
		// } else if (superIntention.getPredicate() == null) {
		// if (other.superIntention!=null && other.superIntention.getPredicate() != null) {
		// return false;
		// }
		// } else if (other.superIntention!=null &&
		// !superIntention.getPredicate().partialEquality(other.superIntention.getPredicate())) {
		// return false;
		// }
		if (!Objects.equals(name, other.name) || (is_true != other.is_true)) return false;
		// if(lifetime!=-1 || other.lifetime!=1){
		// if(lifetime!=other.lifetime){return false;}
		// }
		if (everyPossibleValues && noAgentCause || other.everyPossibleValues && other.noAgentCause) return true;
		/*
		 * if ( values == null ) { if ( other.values != null ) { return false; } } else //
		 */ if (values != null && other.values != null && !values.isEmpty() && !other.values.isEmpty()) {
			final Set<String> keys = values.keySet();
			keys.retainAll(other.values.keySet());
			for (final String k : keys) {
				if (this.values.get(k) == null && other.values.get(k) != null) return false;
				if (!values.get(k).equals(other.values.get(k))) return false;
			}
			return true;
		}
		// if (values != null && other.values != null && !values.equals(other.values)) {
		// return false;
		// }

		/*
		 * if(agentCause==null){ if(other.agentCause!=null){return false;} }else
		 */if (agentCause != null && other.agentCause != null && !agentCause.equals(other.agentCause)) return false;

		return true;
	}

	// private boolean partialEquality(final Object obj) {
	// // You don't test the sub-intentions. Used when testing the equality of
	// // the super-intention
	// if (this == obj) {
	// return true;
	// }
	// if (obj == null) {
	// return false;
	// }
	// if (getClass() != obj.getClass()) {
	// return false;
	// }
	// final Predicate other = (Predicate) obj;
	// if (name == null) {
	// if (other.name != null) {
	// return false;
	// }
	// } else if (!name.equals(other.name)) {
	// return false;
	// }
	//// if (superIntention == null) {
	//// if (other.superIntention != null) {
	//// return false;
	//// }
	//// } else if (superIntention.getPredicate() == null) {
	//// if (other.superIntention!=null && other.superIntention.getPredicate() != null) {
	//// return false;
	//// }
	//// } else if (other.superIntention!=null &&
	// !superIntention.getPredicate().partialEquality(other.superIntention.getPredicate())) {
	//// return false;
	//// }
	// if (is_true != other.is_true) {
	// return false;
	// }
	// // if(lifetime!=-1 || other.lifetime!=1){
	// // if(lifetime!=other.lifetime){return false;}
	// // }
	// if (everyPossibleValues && noAgentCause || other.everyPossibleValues && other.noAgentCause) {
	// return true;
	// }
	// /*
	// * if ( values == null ) { if ( other.values != null ) { return false; }
	// * } else
	// */
	// if (values != null && other.values != null && !values.isEmpty() && !other.values.isEmpty())
	// {
	// Set<String> keys = values.keySet();
	// keys.retainAll(other.values.keySet());
	// for (String k : keys) {
	// if (!values.get(k).equals(other.values.get(k)))
	// return false;
	// }
	// return true;
	// }
	//// if (values != null && other.values != null && !values.equals(other.values)) {
	//// return false;
	//// }
	// /*
	// * if(agentCause==null){ if(other.agentCause!=null){return false;} }else
	// */if (agentCause != null && other.agentCause != null && !agentCause.equals(other.agentCause)) {
	// return false;
	// }
	//
	// return true;
	// }

	/**
	 * Equals intention plan.
	 *
	 * @param obj
	 *            the obj
	 * @return true, if successful
	 */
	public boolean equalsIntentionPlan(final Object obj) {
		// Only test case where the parameter is not null
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		final Predicate other = (Predicate) obj;
		// if (subintentions != null) {
		// if (!subintentions.equals(other.subintentions)) {
		// return false;
		// }
		// }
		// if (superIntention == null) {
		// if (other.superIntention!=null && other.superIntention != null) {
		// return false;
		// }
		// } else if (superIntention.getPredicate() != null) {
		// if (!superIntention.getPredicate().partialEquality(other.superIntention.getPredicate())) {
		// return false;
		// }
		// }
		if (!Objects.equals(name, other.name) || (is_true != other.is_true)) return false;
		// if(lifetime!=-1 || other.lifetime!=1){
		// if(lifetime!=other.lifetime){return false;}
		// }
		if (everyPossibleValues && noAgentCause || other.everyPossibleValues && other.noAgentCause) return true;
		/*
		 * if ( values == null ) { if ( other.values != null ) { return false; } } else
		 */
		if (values != null && other.values != null && !values.isEmpty() && !other.values.isEmpty()) {
			final Set<String> keys = values.keySet();
			keys.retainAll(other.values.keySet());
			for (final String k : keys) {
				if (this.values.get(k) == null && other.values.get(k) != null) return false;
				if (!values.get(k).equals(other.values.get(k))) return false;
			}
			return true;
		}
		// if (values != null && other.values != null && !values.equals(other.values)) {
		// return false;
		// }
		/*
		 * if(agentCause==null){ if(other.agentCause!=null){return false;} }else
		 */if (agentCause != null && other.agentCause != null && !agentCause.equals(other.agentCause)) return false;

		return true;
	}

	/**
	 * Equals but not truth.
	 *
	 * @param obj
	 *            the obj
	 * @return true, if successful
	 */
	public boolean equalsButNotTruth(final Object obj) {
		// return true if the predicates are equals but one is true and not the
		// other
		// Doesn't check the lifetime value
		// Used in emotions
		if (obj == null || getClass() != obj.getClass()) return false;
		final Predicate other = (Predicate) obj;
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
		// } else if (superIntention.getPredicate() == null) {
		// if (other.superIntention != null) {
		// return false;
		// }
		// } else if (other.superIntention!=null &&
		// !superIntention.getPredicate().partialEquality(other.superIntention.getPredicate())) {
		// return false;
		// }
		if (!Objects.equals(name, other.name) || (is_true == other.is_true)) return false;
		if (everyPossibleValues && noAgentCause || other.everyPossibleValues && other.noAgentCause) return true;
		/*
		 * if ( values == null ) { if ( other.values != null ) { return false; } } else
		 */
		if (values != null && other.values != null && !values.isEmpty() && !other.values.isEmpty()) {
			final Set<String> keys = values.keySet();
			keys.retainAll(other.values.keySet());
			for (final String k : keys) {
				if (this.values.get(k) == null && other.values.get(k) != null) return false;
				if (!values.get(k).equals(other.values.get(k))) return false;
			}
		}
		// if (values != null && other.values != null && !values.equals(other.values)) {
		// return false;
		// }
		/*
		 * if(agentCause==null){ if(other.agentCause!=null){return false;} }else
		 */
		// if (agentCause != null && other.agentCause != null && !agentCause.equals(other.agentCause)) {
		// return false;
		// }

		return true;
	}

	/**
	 * Equals emotions.
	 *
	 * @param obj
	 *            the obj
	 * @return true, if successful
	 */
	public boolean equalsEmotions(final Object obj) {
		// Ne teste pas l'agent cause.
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		final Predicate other = (Predicate) obj;
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
		// } else if (superIntention.getPredicate() == null) {
		// if (other.superIntention!=null && other.superIntention.getPredicate() != null) {
		// return false;
		// }
		// } else if (other.superIntention!=null &&
		// !superIntention.getPredicate().partialEquality(other.superIntention.getPredicate())) {
		// return false;
		// }
		if (!Objects.equals(name, other.name) || (is_true != other.is_true)) return false;
		// if(lifetime!=-1 || other.lifetime!=1){
		// if(lifetime!=other.lifetime){return false;}
		// }
		if (everyPossibleValues && noAgentCause || other.everyPossibleValues && other.noAgentCause) return true;
		/*
		 * if ( values == null ) { if ( other.values != null ) { return false; } } else
		 */
		if (values != null && other.values != null && !values.isEmpty() && !other.values.isEmpty()) {
			final Set<String> keys = values.keySet();
			keys.retainAll(other.values.keySet());
			for (final String k : keys) {
				if (this.values.get(k) == null && other.values.get(k) != null) return false;
				if (!values.get(k).equals(other.values.get(k))) return false;
			}
		}
		// if (values != null && other.values != null && !values.equals(other.values)) {
		// return false;
		// }
		/*
		 * if(agentCause==null){ if(other.agentCause!=null){return false;} }else
		 */
		// if (agentCause != null && other.agentCause != null && !agentCause.equals(other.agentCause)) {
		// return false;
		// }

		return true;
	}

	/**
	 * Method getType()
	 *
	 * @see msi.gama.common.interfaces.ITyped#getGamlType()
	 */
	@Override
	public IType<?> getGamlType() { return Types.get(PredicateType.id); }

}
