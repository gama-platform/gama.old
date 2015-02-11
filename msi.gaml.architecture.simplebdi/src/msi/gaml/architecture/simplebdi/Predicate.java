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

import gnu.trove.map.hash.THashMap;

import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IValue;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.IType;

@vars({ @var(name = "name", type = IType.STRING), @var(name = "value", type = IType.NONE), @var(name = "parameters", type = IType.MAP), @var(name = "priority", type = IType.FLOAT),@var(name = "date", type = IType.FLOAT),  @var(name="subgoals", type=IType.LIST), @var(name = "on_hold_until", type = IType.NONE)})
public class Predicate implements IValue{

	String name;
	Object value;
	Map<String,Object> parameters;
	Double priority = 1.0;
	Double date;
	Object onHoldUntil;
	List<Predicate> subgoals;
	boolean everyPossibleValue = false;
	boolean everyPossibleParam = false;
	
	@getter("name")
	public String getName() {
	    return name;
	}
	
	@getter("value")
	public Object getValue() {
	    return value;
	}
	
	@getter("parameters")
	public Map<String,Object> getParameters() {
	    return parameters;
	}
	
	@getter("priority")
	public Double getPriority() {
		return priority;
	}
	
	@getter("date")
	public Double getDate() {
		return date;
	}
	
	@getter("subgoals")
	public List<Predicate> getSubgoals() {
		return subgoals;
	}

	
	public Object getOnHoldUntil() {
		return onHoldUntil;
	}

	public void setOnHoldUntil(Object onHoldUntil) {
		this.onHoldUntil = onHoldUntil;
	}

	public void setValue(Object value) {
		this.value = value;
		everyPossibleValue = (value == "every_possible_value_");
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
		everyPossibleParam = (parameters == null);
	}

	public void setPriority(Double priority) {
		this.priority = priority;
	}

	public void setDate(Double date) {
		this.date = date;
	}

	public void setSubgoals(List<Predicate> subgoals) {
		this.subgoals = subgoals;
	}
	
	public Predicate() {
		super();
		this.name = "";
		this.value = "every_possible_value_";
		everyPossibleValue = true;
		everyPossibleParam = true;
	}

	public Predicate(String name) {
		super();
		this.name = name;
		this.value = "every_possible_value_";
		everyPossibleValue = true;
		everyPossibleParam = true;
	}
	
	public Predicate(String name, Object value) {
		super();
		this.name = name;
		this.value = value;
		everyPossibleValue = (value == "every_possible_value_");
		everyPossibleParam = true;
	}
	
	public Predicate(String name, Object value,double priority) {
		super();
		this.name = name;
		this.value = value;
		this.priority = priority;
		everyPossibleValue = (value == "every_possible_value_");
		everyPossibleParam = true;
	}
	
	public Predicate(String name, Object value, Map<String,Object> parameters) {
		super();
		this.name = name;
		this.value = value;
		this.parameters = parameters;
		everyPossibleValue = (value == "every_possible_value_");
		everyPossibleParam = (parameters == null);
	}
	
	public Predicate(String name, Object value, double priority,Map<String,Object> parameters) {
		super();
		this.name = name;
		this.value = value;
		this.parameters = parameters;
		this.priority = priority;
		everyPossibleValue = (value == "every_possible_value_");
		everyPossibleParam = (parameters == null);
	}
	
	

	
	public void setName(String name) {
		this.name = name;
		
	}
	


	@Override
	public String toString() {
		return toGaml();
	}

	@Override
	public String toGaml() {
		return "predicate(" + name + ((value == null) ? "": ("," + value)) + ((parameters == null) ? "": ("," + parameters)) + ")";
	}

	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		return name + ((value == null) ? "": ("," + value)) + ((parameters == null) ? "": ("," + parameters));
	}

	@Override
	public IValue copy(IScope scope) throws GamaRuntimeException {
		return new Predicate(name,value, priority,new THashMap<String,Object>(parameters));
	}
	
	public boolean isSimilarName(Predicate other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}


	public boolean isSimilarNameValue(Predicate other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Predicate other = (Predicate) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (! everyPossibleValue && ! other.everyPossibleValue && !value.equals(other.value) )
			return false;
		if (everyPossibleParam || other.everyPossibleParam) 
			return true;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		return true;
	}
	
	
	

}
