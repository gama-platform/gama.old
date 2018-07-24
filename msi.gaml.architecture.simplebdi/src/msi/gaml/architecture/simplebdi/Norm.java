package msi.gaml.architecture.simplebdi;

import msi.gama.common.interfaces.IValue;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@vars ({ @variable (
		name = "name",
		type = IType.STRING,
		doc = @doc ("The name of this norm")),
		@variable (
				name = NormStatement.INTENTION,
				type = PredicateType.id,
				doc = @doc ("A string representing the current intention of this Norm")),
		@variable (
				name = NormStatement.OBLIGATION,
				type = PredicateType.id,
				doc = @doc ("A string representing the current obligation of this Norm")),
		@variable (
				name = SimpleBdiArchitecture.FINISHEDWHEN,
				type = IType.STRING),
		@variable (
				name = SimpleBdiArchitecture.INSTANTANEAOUS,
				type = IType.STRING),
		@variable (
				name = NormStatement.LIFETIME,
				type = IType.INT)
//				doc = @doc ("A string representing the current intention of this BDI plan"))
		/*
		 * @var(name = "value", type = IType.NONE),
		 * 
		 * @var(name = "parameters", type = IType.MAP),
		 */
		// @var(name = "values", type = IType.MAP), @var(name = "priority", type
		// = IType.FLOAT),
		// @var(name = "date", type = IType.FLOAT), @var(name = "subintentions",
		// type = IType.LIST),
		// @var(name = "on_hold_until", type = IType.NONE)
})

//Classe qui permet de définir les normes comme type, contenant le norm statement, sur l'exemple des plans
public class Norm implements IValue{

	private NormStatement normStatement;
	private Boolean isViolated;
	private Integer lifetimeViolation;
	private Boolean noLifetime;
	private Boolean isApplied;
	private Boolean isSanctioned;
	
	@getter ("name")
	public String getName() {
		return this.normStatement.getName();
	}
	
	@getter (NormStatement.LIFETIME)
	public Integer getLifetime(final IScope scope) {
		return this.lifetimeViolation;
	}
	
	@getter ("when")
	public String getWhen() {
		return this.normStatement._when.serialize(true);
	}
	
	@getter (NormStatement.INTENTION)
	public Predicate getIntention(final IScope scope) {
		if(this.normStatement!=null && this.normStatement._intention!=null){
			return (Predicate) this.normStatement._intention.value(scope);
		} else {
			return null;
		}
	}
	
	@getter (NormStatement.OBLIGATION)
	public Predicate getObligation(final IScope scope) {
		if(this.normStatement!=null && this.normStatement._obligation!=null){
			return (Predicate) this.normStatement._obligation.value(scope);
		} else {
			return null;
		}
	}
	
	@getter (SimpleBdiArchitecture.FINISHEDWHEN)
	public String getFinishedWhen() {
		if(this.normStatement!=null && this.normStatement._executedwhen!=null){
			return this.normStatement._executedwhen.serialize(true);
		} else {
			return null;
		}
	}
	
	@getter (SimpleBdiArchitecture.INSTANTANEAOUS)
	public String getInstantaneous() {
		if(this.normStatement!=null && this.normStatement._instantaneous!=null){
			return this.normStatement._instantaneous.serialize(true);
		} else {
			return null;
		}
	}
	
	public NormStatement getNormStatement() {
		return this.normStatement;
	}
	
	public Boolean getViolated(){
		return this.isViolated;
	}
	
	public Boolean getApplied(){
		return this.isApplied;
	}
	
	public Boolean getSanctioned(){
		return this.isSanctioned;
	}
	
	public Norm(){
		super();
		this.isViolated = false;
		this.isApplied = false;
		this.lifetimeViolation = -1;
		this.noLifetime = true;
		this.isSanctioned = false;
	}
	
	public Norm(final NormStatement statement) {
		super();
		this.normStatement = statement;
		this.lifetimeViolation = -1;
		this.isViolated = false;
		this.noLifetime = true;
		this.isApplied = false;
		this.isSanctioned = false;
	}
	
	public Norm(final NormStatement statement, final IScope scope) {
		super();
		this.normStatement = statement;
		this.isViolated = false;
		this.isApplied = false;
		this.isSanctioned = false;
		if(statement._lifetime!=null){
			this.lifetimeViolation = (Integer) statement._lifetime.value(scope);
			this.noLifetime = false;
		} else {
			this.lifetimeViolation = -1;
			this.noLifetime = true;
		}
	}
	
	public void setViolation(final Boolean violation){
		this.isViolated = violation;
		this.isApplied = !violation;
	}
	
	public void setSanctioned(final Boolean sanction){
		this.isSanctioned = sanction;
	}
	
	public void sanctioned(){
		this.isSanctioned = true;
	}
	
	public void violated(final IScope scope){
		this.isViolated = true;
		this.isApplied = false;
		if(this.normStatement._lifetime!=null){
			this.lifetimeViolation = (Integer) this.normStatement._lifetime.value(scope);
		} else {
			this.lifetimeViolation = 1;
		}
		this.noLifetime=false;
	}
	
	public void applied(final IScope scope){
		this.isApplied = true;
		this.isViolated = false;
		this.lifetimeViolation=-1;
		this.noLifetime=false;
	}
	
	public void updateLifeime(){
		if(!noLifetime && isViolated){
			this.lifetimeViolation --;
		}
		if(this.lifetimeViolation<0 && !noLifetime){
			isViolated = false;
			isSanctioned = false;
			noLifetime=true;
		}
	}
	
	@Override
	public String serialize(boolean includingBuiltIn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		final Norm other = (Norm) obj;
		if (normStatement == null) {
			if (other.normStatement != null) { return false; }
		} else if (!normStatement.equals(other.normStatement)) { return false; }
		return true;
	}
	
	@Override
	public IType<?> getType() {
		return Types.get(NormType.id);
//		return null;
	}

	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IValue copy(IScope scope) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

}
