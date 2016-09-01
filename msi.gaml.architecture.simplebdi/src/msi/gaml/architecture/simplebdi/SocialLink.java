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
	@var(name = "appreciation", type = IType.FLOAT),@var(name = "dominance", type = IType.FLOAT),
	@var(name="solidarity", type = IType.FLOAT), @var(name="familiarity", type = IType.FLOAT)})

public class SocialLink implements IValue {

	IAgent agent;
	Double appreciation=0.0;
	Double dominance = 0.0;
	Double solidarity = 0.0;
	Double familiarity = 0.0;
	
	@getter("agent")
	public IAgent getAgent(){
		return agent;
	}
	
	@getter("appreciation")
	public Double getAppreciation(){
		return appreciation;
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
	
	public void setAgent(IAgent ag){
		this.agent = ag;
	}
	
	public void setAppreciation(Double appre){
		this.appreciation = appre;
	}
	
	public void setDominance(Double domi){
		this.dominance = domi;
	}
	
	public void setSolidarity(Double solid){
		this.solidarity = solid;
	}
	
	public void setFamiliarity(Double fami){
		this.familiarity = fami;
	}
	
	public SocialLink(IAgent ag){
		this.agent = ag;
	}
	
	public SocialLink(IAgent ag, Double appre, Double domi, Double solid, Double fami){
		this.agent=ag;
		this.appreciation=appre;
		this.dominance=domi;
		this.solidarity=solid;
		this.familiarity=fami;
	}
	
	@Override
	public String toString() {
		return serialize(true);
	}
	
	@Override
	public String serialize(boolean includingBuiltIn) {
		return "(" + agent +","+ appreciation +","+ dominance +","+ solidarity +","+ familiarity +")";
	}

	@Override
	public IType getType() {
		return Types.get(SocialLinkType.id);
	}

	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		return "(" + agent +","+ appreciation +","+ dominance +","+ solidarity +","+ familiarity +")";
	}

	@Override
	public IValue copy(IScope scope) throws GamaRuntimeException {
		return new SocialLink(agent,appreciation,dominance,solidarity,familiarity);
	}
	
	@Override
	public boolean equals(final Object obj){
		if ( this == obj ) { return true; }
		if ( obj == null ) { return false; }
		if ( getClass() != obj.getClass() ) { return false; }
		SocialLink other = (SocialLink)obj;
		if(!agent.equals(other.getAgent())){return false;}
		return true;
	}

}
