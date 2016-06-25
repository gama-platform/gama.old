/*********************************************************************************************
 * 
 * 
 * 'BDIPlan.java', in plugin 'msi.gaml.architecture.simplebdi', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.architecture.simplebdi;

import msi.gama.common.interfaces.IValue;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@vars({ @var(name = "name", type = IType.STRING),
	 @var(name = "todo", type = IType.STRING),
		@var(name = SimpleBdiPlanStatement.INTENTION, type = IType.NONE),
		@var(name = SimpleBdiArchitecture.FINISHEDWHEN, type = IType.STRING),
		@var(name = SimpleBdiArchitecture.INSTANTANEAOUS, type = IType.STRING)
	/* @var(name = "value", type = IType.NONE),
	@var(name = "parameters", type = IType.MAP),*/
//	@var(name = "values", type = IType.MAP), @var(name = "priority", type = IType.FLOAT),
//	@var(name = "date", type = IType.FLOAT), @var(name = "subintentions", type = IType.LIST),
//	@var(name = "on_hold_until", type = IType.NONE) 
	})
public class BDIPlan implements IValue {

	private SimpleBdiPlanStatement planstatement;

	@getter("name")
	public String getName() {
		return this.planstatement.getName();
	}
	
	@getter("todo")
	public String getWhen() {
		return this.planstatement._when.serialize(true);
	}
	
	@getter(SimpleBdiArchitecture.FINISHEDWHEN)
	public String getFinishedWhen() {
		return this.planstatement._executedwhen.serialize(true);
	}
	
	@getter(SimpleBdiPlanStatement.INTENTION)
	public Predicate getIntention(IScope scope) {
		return (Predicate)this.planstatement._intention.value(scope);
	}
	
	@getter(SimpleBdiArchitecture.INSTANTANEAOUS)
	public String getInstantaneous() {
		return this.planstatement._instantaneous.serialize(true);
	}
	
	public SimpleBdiPlanStatement getPlanStatement(){
		return this.planstatement;
	}
	
	public BDIPlan() {
		super();
	}

	public BDIPlan(final SimpleBdiPlanStatement statement) {
		super();
		this.planstatement = statement;
	}

	public void setSimpleBdiPlanStatement(final SimpleBdiPlanStatement statement) {
		this.planstatement = statement;

	}

	@Override
	public String toString() {
		return serialize(true);
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return "BDIPlan(" + planstatement
//				+(values == null ? "" : "," + values) +
			+")";
	}

	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return "BDIPlan(" + planstatement  
//				+(values == null ? "" : "," + values)
				;
	}

	@Override
	public IValue copy(final IScope scope) throws GamaRuntimeException {
		return new BDIPlan(planstatement);
	}

	public boolean isSimilarName(final BDIPlan other) {
		if ( this == other ) { return true; }
		if ( other == null ) { return false; }
		if ( planstatement == null ) {
			if ( other.planstatement != null ) { return false; }
		} else if ( !planstatement.equals(other.planstatement) ) { return false; }
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (planstatement == null ? 0 : planstatement.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if ( this == obj ) { return true; }
		if ( obj == null ) { return false; }
		if ( getClass() != obj.getClass() ) { return false; }
		BDIPlan other = (BDIPlan) obj;
		if ( planstatement == null ) {
			if ( other.planstatement != null ) { return false; }
		} else if ( !planstatement.equals(other.planstatement) ) { return false; }
		return true;
	}

	/**
	 * Method getType()
	 * @see msi.gama.common.interfaces.ITyped#getType()
	 */
	@Override
	public IType getType() {
		return Types.get(BDIPlanType.id);
	}

}
