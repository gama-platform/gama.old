package msi.gaml.architecture.simplebdi;

import msi.gama.common.interfaces.IValue;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IVarExpression.Agent;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@vars({/*@var(name = "name", type = IType.STRING),*/@var(name = "agent", type = IType.AGENT),
	@var(name = "liking", type = IType.FLOAT),@var(name = "dominance", type = IType.FLOAT),
	@var(name="solidarity", type = IType.FLOAT), @var(name="familiarity", type = IType.FLOAT)})

public class SocialLink implements IValue {

	IAgent agent;
	Double liking = 0.0;
	Double dominance = 0.0;
	Double solidarity = 0.0;
	Double familiarity = 0.0;
	private Boolean noLiking = true;
	private Boolean noDominance = true;
	private Boolean noSolidarity = true;
	private Boolean noFamiliarity = true;
	
	@getter("agent")
	public IAgent getAgent(){
		return agent;
	}
	
	@getter("appreciation")
	public Double getLiking(){
		return liking;
	}
	
	@getter("dominance")
	public Double getDominance(){
		return dominance;
	}
	
	@getter("solidarity")
	public Double getSolidarity(){
		return solidarity;
	}

	@getter("familiarity")
	public Double getFamiliarity(){
		return familiarity;
	}
	
	public Boolean getNoLiking(){
		return noLiking;
	}
	
	public Boolean getNoDominance(){
		return noDominance;
	}
	
	public Boolean getNoSolidarity(){
		return noSolidarity;
	}
	
	public Boolean getNoFamiliarity(){
		return noFamiliarity;
	}
	
	public void setAgent(IAgent ag){
		this.agent = ag;
	}
	
	public void setLiking(Double appre){
		this.liking = appre;
		this.noLiking = false;
	}
	
	public void setDominance(Double domi){
		this.dominance = domi;
		this.noDominance = false;
	}
	
	public void setSolidarity(Double solid){
		this.solidarity = solid;
		this.noSolidarity = false;
	}
	
	public void setFamiliarity(Double fami){
		this.familiarity = fami;
		this.noFamiliarity = false;
	}
	
	public SocialLink(){
		this.agent=null;
	}
	
	public SocialLink(IAgent ag){
		this.agent = ag;
	}
	
	public SocialLink(IAgent ag, Double appre, Double domi, Double solid, Double fami){
		this.agent=ag;
		this.liking=appre;
		this.noLiking = false;
		this.dominance=domi;
		this.noDominance = false;
		this.solidarity=solid;
		this.noDominance = false;
		this.familiarity=fami;
		this.noFamiliarity = false;
	}
	
	@Override
	public String toString() {
		return serialize(true);
	}
	
	@Override
	public String serialize(boolean includingBuiltIn) {
		return "(" + agent +","+ liking +","+ dominance +","+ solidarity +","+ familiarity +")";
	}

	@Override
	public IType getType() {
		return Types.get(SocialLinkType.id);
	}

	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		return "(" + agent +","+ liking +","+ dominance +","+ solidarity +","+ familiarity +")";
	}

	@Override
	public IValue copy(IScope scope) throws GamaRuntimeException {
		return new SocialLink(agent,liking,dominance,solidarity,familiarity);
	}
	
	@Override
	public boolean equals(final Object obj){
		if ( this == obj ) { return true; }
		if ( obj == null ) { return false; }
		if ( getClass() != obj.getClass() ) { return false; }
		SocialLink other = (SocialLink)obj;
		if(this.agent!=null && other.getAgent()!=null){
			if(!agent.equals(other.getAgent())){return false;}
		}
		if(!noLiking && !other.getNoLiking()){
			if(!liking.equals(other.getLiking())){return false;}
		}
		if(!noDominance && !other.getNoDominance()){
			if(!dominance.equals(other.getDominance())){return false;}
		}
		if(!noSolidarity && !other.getNoSolidarity()){
			if(!solidarity.equals(other.getSolidarity())){return false;}
		}
		if(!noFamiliarity && !other.getNoFamiliarity()){
			if(!familiarity.equals(other.getFamiliarity())){return false;}
		}
		return true;
	}
	
	public boolean equalsInAgent(final Object obj){
		if ( this == obj ) { return true; }
		if ( obj == null ) { return false; }
		if ( getClass() != obj.getClass() ) { return false; }
		SocialLink other = (SocialLink)obj;
		if(agent!=null || other.getAgent()!=null){
			if(!agent.equals(other.getAgent())){return false;}
		}
		return true;
	}

}
