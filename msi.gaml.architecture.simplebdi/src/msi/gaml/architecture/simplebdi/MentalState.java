package msi.gaml.architecture.simplebdi;

import msi.gama.common.interfaces.IValue;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@vars({@var(name = "modality", type = IType.STRING),
	@var(name = "predicate", type = IType.NONE),
	@var(name = "strength", type = IType.FLOAT)})
public class MentalState implements IValue {

	String modality;
	Predicate predicate;
	Double strength;
	
	@getter("modality")
	public String getModality(){
		return modality;
	}
	
	@getter("predicate")
	public Predicate getPredicate(){
		return predicate;
	}
	
	@getter("strength")
	public Double getStrength(){
		return strength;
	}
	
	public void setModality(String mod){
		this.modality=mod;
	}
	
	public void setPredicate(Predicate pred){
		this.predicate=pred;
	}
	
	public void setStrength(Double stre){
		this.strength=stre;
	}
	
	public MentalState(){
		super();
		this.modality="";
		this.predicate=null;
		this.strength=1.0;
	}
	
	public MentalState(String mod){
		super();
		this.modality=mod;
		this.predicate=null;
		this.strength=1.0;
	}
	
	public MentalState(String mod, Predicate pred){
		super();
		this.modality=mod;
		this.predicate=pred;
		this.strength=1.0;
	}
	
	public MentalState(String mod, Predicate pred, Double stre){
		super();
		this.modality=mod;
		this.predicate=pred;
		this.strength=stre;
	}
	
	@Override
	public String toString() {
		return serialize(true);
	}
	
	@Override
	public String serialize(boolean includingBuiltIn) {
		return modality + "(" + predicate +","+strength+")";
	}

	@Override
	public IType<?> getType() {
		return Types.get(MentalStateType.id);
	}

	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		return modality + "(" + predicate +","+strength+")";
	}

	@Override
	public IValue copy(IScope scope) throws GamaRuntimeException {
		return new MentalState(modality,predicate,strength);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		return result;
	}
	
	public boolean equals(final Object obj){
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final MentalState other = (MentalState)obj;
//		if(other.getModality()!=this.modality){return false;}
		if(!other.getPredicate().equals(this.predicate)){return false;}
//		if(other.getStrength()!=this.strength){return false;}
		return true;
	}

}
