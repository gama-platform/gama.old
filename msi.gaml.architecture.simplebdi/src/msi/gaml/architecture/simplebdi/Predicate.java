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

import msi.gama.common.interfaces.IValue;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMapFactory;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@vars({ @var(name = "name", type = IType.STRING), @var(name = "is_true", type = IType.BOOL),
	@var(name = "values", type = IType.MAP), @var(name = "priority", type = IType.FLOAT),
	@var(name = "date", type = IType.FLOAT), @var(name = "subintentions", type = IType.LIST),
	@var(name = "on_hold_until", type = IType.NONE), @var(name = "super_intention", type = IType.NONE) })
public class Predicate implements IValue {

	String name;
	Map<String, Object> values;
	Double priority = 1.0;
	Double date;
//	Object onHoldUntil;
	List<Predicate> onHoldUntil;
	List<Predicate> subintentions;
	Predicate superIntention;
	boolean everyPossibleValues = false;
	boolean is_true=true;
	int lifetime = -1;
	boolean isUpdated = false;
	
	@getter("name")
	public String getName() {
		return name;
	}

	@getter("values")
	public Map<String, Object> getValues() {
		return values;
	}
	
	@getter("priority")
	public Double getPriority() {
		return priority;
	}

	@getter("is_true")
	public Boolean getIs_True() {
		return is_true;
	}

	@getter("date")
	public Double getDate() {
		return date;
	}

	@getter("subintentions")
	public List<Predicate> getSubintentions() {
		return subintentions;
	}

	@getter("superIntention")
	public Predicate getSuperIntention(){
		return superIntention;
	}
	
	public List<Predicate> getOnHoldUntil() {
		return onHoldUntil;
	}

	public int getLifetime(){
		return lifetime;
	}
	
	public void setSuperIntention(Predicate superPredicate){
		this.superIntention = superPredicate;
	}
	
//	public void setOnHoldUntil(final Object onHoldUntil) {
//		this.onHoldUntil = onHoldUntil;
//	}
	
	public void setOnHoldUntil(final List<Predicate> onHoldUntil){
		this.onHoldUntil = onHoldUntil;
	}
	
	public void setValues(final Map<String, Object> values){
		this.values = (Map<String, Object>) GamaMapFactory.createWithoutCasting(getType().getKeyType(), getType().getContentType(), values);
		everyPossibleValues = values == null;
	}

	public void setIs_True(final Boolean ist) {
		this.is_true=ist;
	}

	public void setPriority(final Double priority) {
		this.priority = priority;
	}

	public void setDate(final Double date) {
		this.date = date;
	}

	public void setSubintentions(final List<Predicate> subintentions) {
		this.subintentions = subintentions;
	}

	public void setLifetime(final int lifetime){
		this.lifetime = lifetime;
	}
	
	public Predicate() {
		super();
		this.name = "";
		everyPossibleValues = true;
	}

	public Predicate(final String name) {
		super();
		this.name = name;
		everyPossibleValues = true;
	}

	public Predicate(final String name, boolean ist) {
		super();
		this.name = name;
		everyPossibleValues = true;
		is_true=ist;
	}
	
	public Predicate(final String name, double priority){
		super();
		this.name = name;
		everyPossibleValues = true;
		this.priority = priority;
	}
	
	public Predicate(final String name, int lifetime){
		super();
		this.name = name;
		everyPossibleValues = true;
		this.lifetime = lifetime;
	}
	
	public Predicate(final String name, final Map<String, Object> values) {
		super();
		this.name = name;
		this.values = (Map<String, Object>) GamaMapFactory.createWithoutCasting(getType().getKeyType(), getType().getContentType(), values);
		everyPossibleValues = values == null;;
	}

	public Predicate(final String name, final Map<String, Object> values, final Boolean truth) {
		super();
		this.name = name;
		this.values = (Map<String, Object>) GamaMapFactory.createWithoutCasting(getType().getKeyType(), getType().getContentType(), values);
		this.is_true=truth;
		everyPossibleValues = values == null;;
	}

	public Predicate(final String name, final Map<String, Object> values, final int lifetime) {
		super();
		this.name = name;
		this.values = (Map<String, Object>) GamaMapFactory.createWithoutCasting(getType().getKeyType(), getType().getContentType(), values);
		this.lifetime=lifetime;
		everyPossibleValues = values == null;;
	}
	
	public Predicate(final String name, final double priority, final Map<String, Object> values) {
		super();
		this.name = name;
		this.values = (Map<String, Object>) GamaMapFactory.createWithoutCasting(getType().getKeyType(), getType().getContentType(), values);
		this.priority = priority;
		everyPossibleValues = values == null;
	}

	public void setName(final String name) {
		this.name = name;

	}

	@Override
	public String toString() {
		return serialize(true);
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return "predicate(" + name + (values == null ? "" : "," + values) +
			")";
	}

	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return name + (values == null ? "" : "," + values);
	}

	@Override
	public IValue copy(final IScope scope) throws GamaRuntimeException {
		return new Predicate(name, priority, new LinkedHashMap<String, Object>(values));
	}
	
	public void updateLifetime(){
		if((this.lifetime>0) && (!this.isUpdated)){
			this.lifetime = this.lifetime-1;
			this.isUpdated = true;
		}
	}
	
	public boolean isSimilarName(final Predicate other) {
		if ( this == other ) { return true; }
		if ( other == null ) { return false; }
		if ( name == null ) {
			if ( other.name != null ) { return false; }
		} else if ( !name.equals(other.name) ) { return false; }
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
		if ( this == obj ) { return true; }
		if ( obj == null ) { return false; }
		if ( getClass() != obj.getClass() ) { return false; }
		Predicate other = (Predicate) obj;
		if ( name == null ) {
			if ( other.name != null ) { return false; }
		} else if ( !name.equals(other.name) ) { return false; }
		if(subintentions == null){
			if (other.subintentions!=null){return false;}
		} else if(!subintentions.equals(other.subintentions)) {return false;}
		if(superIntention == null){
			if(other.superIntention != null){return false;}
		}else if(!superIntention.equals(other.superIntention)){return false;}
		if(is_true!=other.is_true){return false;}
		if(lifetime!=other.lifetime){return false;}
		if ( everyPossibleValues || other.everyPossibleValues ) { return true; }
		if ( values == null ) {
			if ( other.values != null ) { return false; }
		} else if ( !values.equals(other.values) ) { return false; }
		
		return true;
	}

	/**
	 * Method getType()
	 * @see msi.gama.common.interfaces.ITyped#getType()
	 */
	@Override
	public IType getType() {
		return Types.get(PredicateType.id);
	}

}
