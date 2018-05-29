/*********************************************************************************************
 * 
 * 
 * 'Predicate.java', in plugin 'msi.gaml.architecture.simplebdi', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.architecture.simplebdi;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import msi.gama.common.interfaces.IValue;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@vars({ @var(name = "name", type = IType.STRING), @var(name = "is_true", type = IType.BOOL),
		@var(name = "values", type = IType.MAP), /*@var(name = "priority", type = IType.FLOAT),*/
		@var(name = "date", type = IType.FLOAT), @var(name = "subintentions", type = IType.LIST),
		@var(name = "on_hold_until", type = IType.NONE), @var(name = "super_intention", type = IType.NONE),
		/*@var(name = "praiseworthiness", type = IType.FLOAT),*/@var(name = "agentCause", type = IType.AGENT) })
public class Predicate implements IValue {

	String name;
	Map<String, Object> values;
	//Double priority = 1.0;
	Double date;
	// Object onHoldUntil;
	List<MentalState> onHoldUntil;
	List<MentalState> subintentions;
	MentalState superIntention;
	//Double praiseworthiness = 0.0;
	IAgent agentCause;
	boolean everyPossibleValues = false;
	boolean is_true = true;
	int lifetime = -1;
	boolean isUpdated = false;
	private boolean noAgentCause = true;

	@getter("name")
	public String getName() {
		return name;
	}

	@getter("values")
	public Map<String, Object> getValues() {
		return values;
	}

//	@getter("priority")
//	public Double getPriority() {
//		return priority;
//	}

	@getter("is_true")
	public Boolean getIs_True() {
		return is_true;
	}

	@getter("date")
	public Double getDate() {
		return date;
	}

	@getter("subintentions")
	public List<MentalState> getSubintentions() {
		return subintentions;
	}

	@getter("superIntention")
	public MentalState getSuperIntention() {
		return superIntention;
	}

	@getter("agentCause")
	public IAgent getAgentCause() {
		return agentCause;
	}

	public List<MentalState> getOnHoldUntil() {
		return onHoldUntil;
	}

	public int getLifetime() {
		return lifetime;
	}

	/*public Double getPraiseworthiness(){
		return praiseworthiness;
	}*/
	
	public void setSuperIntention(final MentalState superPredicate) {
		this.superIntention = superPredicate;
	}

	// public void setOnHoldUntil(final Object onHoldUntil) {
	// this.onHoldUntil = onHoldUntil;
	// }

	public void setOnHoldUntil(final List<MentalState> onHoldUntil) {
		this.onHoldUntil = onHoldUntil;
	}

	public void setValues(final Map<String, Object> values) {
		this.values = values;
		everyPossibleValues = values == null;
	}

	public void setIs_True(final Boolean ist) {
		this.is_true = ist;
	}

//	public void setPriority(final Double priority) {
//		this.priority = priority;
//	}

	public void setDate(final Double date) {
		this.date = date;
	}

	public void setSubintentions(final List<MentalState> subintentions) {
		this.subintentions = subintentions;
	}

	public void setLifetime(final int lifetime) {
		this.lifetime = lifetime;
	}

	/*public void setPraiseworthiness(final Double praise){
		this.praiseworthiness = praise;
	}*/
	
	public void setAgentCause(final IAgent ag) {
		this.agentCause = ag;
		this.noAgentCause = false;
	}

	public Predicate() {
		super();
		this.name = "";
		everyPossibleValues = true;
		this.agentCause = null;
	}

	public Predicate(final String name) {
		super();
		this.name = name;
		everyPossibleValues = true;
		this.agentCause = null;
	}

	public Predicate(final String name, final boolean ist) {
		super();
		this.name = name;
		everyPossibleValues = true;
		is_true = ist;
		this.agentCause = null;
	}

//	public Predicate(final String name, final double priority) {
//		super();
//		this.name = name;
//		everyPossibleValues = true;
//		this.priority = priority;
//		this.agentCause = null;
//	}

	public Predicate(final String name, final int lifetime) {
		super();
		this.name = name;
		everyPossibleValues = true;
		this.lifetime = lifetime;
		this.agentCause = null;
	}

	public Predicate(final String name, final Map<String, Object> values) {
		super();
		this.name = name;
		this.values = values;
		everyPossibleValues = values == null;
		this.agentCause = null;
	}

	public Predicate(final String name, final IAgent ag) {
		super();
		this.name = name;
		this.agentCause = ag;
		this.noAgentCause = ag == null;
		everyPossibleValues = true;
	}
	
	public Predicate(final String name, final Map<String, Object> values, final Boolean truth) {
		super();
		this.name = name;
		this.values = values;
		this.is_true = truth;
		everyPossibleValues = values == null;
		this.agentCause = null;
	}

	public Predicate(final String name, final Map<String, Object> values, final int lifetime) {
		super();
		this.name = name;
		this.values = values;
		this.lifetime = lifetime;
		everyPossibleValues = values == null;
		this.agentCause = null;
	}

//	public Predicate(final String name, final double priority, final Map<String, Object> values) {
//		super();
//		this.name = name;
//		this.values = values;
//		this.priority = priority;
//		everyPossibleValues = values == null;
//		this.agentCause = null;
//	}

	public Predicate(final String name, final Map<String, Object> values, final IAgent ag) {
		super();
		this.name = name;
		this.values = values;
		everyPossibleValues = values == null;
		this.agentCause = ag;
		this.noAgentCause = ag == null;
	}

//	public Predicate(final String name, final double priority, final Map<String, Object> values, final int lifetime) {
//		super();
//		this.name = name;
//		this.values = values;
//		this.priority = priority;
//		this.lifetime = lifetime;
//		everyPossibleValues = values == null;
//		this.agentCause = null;
//	}

//	public Predicate(final String name, final double priority, final Map<String, Object> values, final Boolean truth) {
//		super();
//		this.name = name;
//		this.values = values;
//		this.priority = priority;
//		this.is_true = truth;
//		everyPossibleValues = values == null;
//		this.agentCause = null;
//	}

//	public Predicate(final String name, final double priority, final Map<String, Object> values, final IAgent ag) {
//		super();
//		this.name = name;
//		this.values = values;
//		this.priority = priority;
//		everyPossibleValues = values == null;
//		this.agentCause = ag;
//		this.noAgentCause = ag == null;
//	}

	public Predicate(final String name, final Map<String, Object> values, final int lifetime, final Boolean truth) {
		super();
		this.name = name;
		this.values = values;
		this.lifetime = lifetime;
		this.is_true = truth;
		everyPossibleValues = values == null;
	}

	public Predicate(final String name, final Map<String, Object> values, final Boolean truth, final IAgent ag) {
		super();
		this.name = name;
		this.values = values;
		this.is_true = truth;
		everyPossibleValues = values == null;
		this.agentCause = ag;
		this.noAgentCause = ag == null;
	}

	public Predicate(final String name, final Map<String, Object> values, final int lifetime, final IAgent ag) {
		super();
		this.name = name;
		this.values = values;
		this.lifetime = lifetime;
		everyPossibleValues = values == null;
		this.agentCause = ag;
		this.noAgentCause = ag == null;
	}

//	public Predicate(final String name, final double priority, final Map<String, Object> values, final int lifetime,
//			final Boolean truth) {
//		super();
//		this.name = name;
//		this.values = values;
//		this.priority = priority;
//		this.lifetime = lifetime;
//		this.is_true = truth;
//		everyPossibleValues = values == null;
//		this.agentCause = null;
//	}

//	public Predicate(final String name, final double priority, final Map<String, Object> values, final int lifetime,
//			final IAgent ag) {
//		super();
//		this.name = name;
//		this.values = values;
//		this.priority = priority;
//		this.lifetime = lifetime;
//		everyPossibleValues = values == null;
//		this.agentCause = ag;
//		this.noAgentCause = ag == null;
//	}

//	public Predicate(final String name, final double priority, final Map<String, Object> values, final Boolean truth,
//			final IAgent ag) {
//		super();
//		this.name = name;
//		this.values = values;
//		this.priority = priority;
//		this.is_true = truth;
//		everyPossibleValues = values == null;
//		this.agentCause = ag;
//		this.noAgentCause = ag == null;
//	}

	public Predicate(final String name, final Map<String, Object> values, final int lifetime, final Boolean truth,
			final IAgent ag) {
		super();
		this.name = name;
		this.values = values;
		this.lifetime = lifetime;
		this.is_true = truth;
		everyPossibleValues = values == null;
		this.agentCause = ag;
		this.noAgentCause = ag == null;
	}

//	public Predicate(final String name, final double priority, final Map<String, Object> values, final int lifetime,
//			final Boolean truth, final IAgent ag) {
//		super();
//		this.name = name;
//		this.values = values;
//		this.priority = priority;
//		this.lifetime = lifetime;
//		this.is_true = truth;
//		everyPossibleValues = values == null;
//		this.agentCause = ag;
//		this.noAgentCause = ag == null;
//	}

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
				+ ")";
	}

	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return name + (values == null ? "" : "," + values);
	}

	@Override
	public Predicate copy(final IScope scope) throws GamaRuntimeException {
		return new Predicate(name, new LinkedHashMap<String, Object>(values));
	}

	public Predicate copy() throws GamaRuntimeException {
		if(values!=null && agentCause!=null){
			return new Predicate(name, new LinkedHashMap<String, Object>(values),is_true,agentCause);
		}
		if(values!=null){
			return new Predicate(name, new LinkedHashMap<String, Object>(values),is_true);
		}
		return new Predicate(name);
	}
	
	public void updateLifetime() {
		if (this.lifetime > 0 && !this.isUpdated) {
			this.lifetime = this.lifetime - 1;
			this.isUpdated = true;
		}
	}

	public boolean isSimilarName(final Predicate other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result + (values == null ? 0 : values.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Predicate other = (Predicate) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}

		if (subintentions == null) {
			if (other.subintentions != null && !other.subintentions.isEmpty()) {
				return false;
			}
		} else if (!subintentions.equals(other.subintentions)) {
			return false;
		}
		if (superIntention == null) {
			if (other.superIntention != null) {
				return false;
			}
		} else if (superIntention.getPredicate() == null) {
			if (other.superIntention!=null && other.superIntention.getPredicate() != null) {
				return false;
			}
		} else if (other.superIntention!=null && !superIntention.getPredicate().partialEquality(other.superIntention.getPredicate())) {
			return false;
		}
		if (is_true != other.is_true) {
			return false;
		}
		// if(lifetime!=-1 || other.lifetime!=1){
		// if(lifetime!=other.lifetime){return false;}
		// }
		if (everyPossibleValues && noAgentCause || other.everyPossibleValues && other.noAgentCause) {
			return true;
		}
		/*
		 * if ( values == null ) { if ( other.values != null ) { return false; }
		 * } else
//		 */ if (values != null && other.values != null && !values.isEmpty() && !other.values.isEmpty())
			{
			 Set<String> keys = values.keySet();
			 keys.retainAll(other.values.keySet());
			 for (String k : keys) {
				 if (!values.get(k).equals(other.values.get(k))) 
					 return false;
			 }
			 return true;
			}
//			if (values != null && other.values != null && !values.equals(other.values)) {
//			return false;
//		}
		
		/*
		 * if(agentCause==null){ if(other.agentCause!=null){return false;} }else
		 */if (agentCause != null && other.agentCause != null && !agentCause.equals(other.agentCause)) {
			return false;
		}

		return true;
	}

	private boolean partialEquality(final Object obj) {
		// You don't test the sub-intentions. Used when testing the equality of
		// the super-intention
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Predicate other = (Predicate) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (superIntention == null) {
			if (other.superIntention != null) {
				return false;
			}
		} else if (superIntention.getPredicate() == null) {
			if (other.superIntention!=null && other.superIntention.getPredicate() != null) {
				return false;
			}
		} else if (other.superIntention!=null && !superIntention.getPredicate().partialEquality(other.superIntention.getPredicate())) {
			return false;
		}
		if (is_true != other.is_true) {
			return false;
		}
		// if(lifetime!=-1 || other.lifetime!=1){
		// if(lifetime!=other.lifetime){return false;}
		// }
		if (everyPossibleValues && noAgentCause || other.everyPossibleValues && other.noAgentCause) {
			return true;
		}
		/*
		 * if ( values == null ) { if ( other.values != null ) { return false; }
		 * } else
		 */ 
		if (values != null && other.values != null && !values.isEmpty() && !other.values.isEmpty())
		{
		 Set<String> keys = values.keySet();
		 keys.retainAll(other.values.keySet());
		 for (String k : keys) {
			 if (!values.get(k).equals(other.values.get(k))) 
				 return false;
		 }
		 return true;
		}
//		if (values != null && other.values != null && !values.equals(other.values)) {
//			return false;
//		}
		/*
		 * if(agentCause==null){ if(other.agentCause!=null){return false;} }else
		 */if (agentCause != null && other.agentCause != null && !agentCause.equals(other.agentCause)) {
			return false;
		}

		return true;
	}

	public boolean equalsIntentionPlan(final Object obj) {
		// Only test case where the parameter is not null
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Predicate other = (Predicate) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (subintentions != null) {
			if (!subintentions.equals(other.subintentions)) {
				return false;
			}
		}
		if (superIntention == null) {
			if (other.superIntention!=null && other.superIntention != null) {
				return false;
			}
		} else if (superIntention.getPredicate() != null) {
			if (!superIntention.getPredicate().partialEquality(other.superIntention.getPredicate())) {
				return false;
			}
		}
		if (is_true != other.is_true) {
			return false;
		}
		// if(lifetime!=-1 || other.lifetime!=1){
		// if(lifetime!=other.lifetime){return false;}
		// }
		if (everyPossibleValues && noAgentCause || other.everyPossibleValues && other.noAgentCause) {
			return true;
		}
		/*
		 * if ( values == null ) { if ( other.values != null ) { return false; }
		 * } else
		 */ 
		if (values != null && other.values != null && !values.isEmpty() && !other.values.isEmpty())
		{
		 Set<String> keys = values.keySet();
		 keys.retainAll(other.values.keySet());
		 for (String k : keys) {
			 if (!values.get(k).equals(other.values.get(k))) 
				 return false;
		 }
		 return true;
		}
//		if (values != null && other.values != null && !values.equals(other.values)) {
//			return false;
//		}
		/*
		 * if(agentCause==null){ if(other.agentCause!=null){return false;} }else
		 */if (agentCause != null && other.agentCause != null && !agentCause.equals(other.agentCause)) {
			return false;
		}

		return true;
	}

	public boolean equalsButNotTruth(final Object obj) {
		// return true if the predicates are equals but one is true and not the
		// other
		// Doesn't check the lifetime value
		// Used in emotions
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Predicate other = (Predicate) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (subintentions == null) {
			if (other.subintentions != null && !other.subintentions.isEmpty()) {
				return false;
			}
		} else if (!subintentions.equals(other.subintentions)) {
			return false;
		}
		if (superIntention == null) {
			if (other.superIntention != null) {
				return false;
			}
		} else if (superIntention.getPredicate() == null) {
			if (other.superIntention != null) {
				return false;
			}
		} else if (other.superIntention!=null && !superIntention.getPredicate().partialEquality(other.superIntention.getPredicate())) {
			return false;
		}
		if (is_true != other.is_true) {
			if (everyPossibleValues && noAgentCause || other.everyPossibleValues && other.noAgentCause) {
				return true;
			}
			/*
			 * if ( values == null ) { if ( other.values != null ) { return
			 * false; } } else
			 */ 
			if (values != null && other.values != null && !values.isEmpty() && !other.values.isEmpty())
			{
			 Set<String> keys = values.keySet();
			 keys.retainAll(other.values.keySet());
			 for (String k : keys) {
				 if (!values.get(k).equals(other.values.get(k))) 
					 return false;
			 }
			 return true;
			}
//			if (values != null && other.values != null && !values.equals(other.values)) {
//				return false;
//			}
			/*
			 * if(agentCause==null){ if(other.agentCause!=null){return false;}
			 * }else*/
//			 if (agentCause != null && other.agentCause != null && !agentCause.equals(other.agentCause)) {
//				return false;
//			}

			return true;
		} else {
			return false;
		}
	}

	public boolean equalsEmotions(final Object obj) {
		//Ne teste pas l'agent cause.
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Predicate other = (Predicate) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}

		if (subintentions == null) {
			if (other.subintentions != null && !other.subintentions.isEmpty()) {
				return false;
			}
		} else if (!subintentions.equals(other.subintentions)) {
			return false;
		}
		if (superIntention == null) {
			if (other.superIntention != null) {
				return false;
			}
		} else if (superIntention.getPredicate() == null) {
			if (other.superIntention!=null && other.superIntention.getPredicate() != null) {
				return false;
			}
		} else if (other.superIntention!=null && !superIntention.getPredicate().partialEquality(other.superIntention.getPredicate())) {
			return false;
		}
		if (is_true != other.is_true) {
			return false;
		}
		// if(lifetime!=-1 || other.lifetime!=1){
		// if(lifetime!=other.lifetime){return false;}
		// }
		if (everyPossibleValues && noAgentCause || other.everyPossibleValues && other.noAgentCause) {
			return true;
		}
		/*
		 * if ( values == null ) { if ( other.values != null ) { return false; }
		 * } else
		 */ 
		if (values != null && other.values != null && !values.isEmpty() && !other.values.isEmpty())
		{
		 Set<String> keys = values.keySet();
		 keys.retainAll(other.values.keySet());
		 for (String k : keys) {
			 if (!values.get(k).equals(other.values.get(k))) 
				 return false;
		 }
		 return true;
		}
//		if (values != null && other.values != null && !values.equals(other.values)) {
//			return false;
//		}
		/*
		 * if(agentCause==null){ if(other.agentCause!=null){return false;} }else*/
//		 if (agentCause != null && other.agentCause != null && !agentCause.equals(other.agentCause)) {
//			return false;
//		}

		return true;
	}
	
	/**
	 * Method getType()
	 * 
	 * @see msi.gama.common.interfaces.ITyped#getType()
	 */
	@Override
	public IType<?> getType() {
		return Types.get(PredicateType.id);
	}

}
