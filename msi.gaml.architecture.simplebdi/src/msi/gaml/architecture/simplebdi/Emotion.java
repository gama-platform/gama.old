package msi.gaml.architecture.simplebdi;

import java.util.LinkedHashMap;

import msi.gama.common.interfaces.IValue;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@vars({@var(name = "name", type = IType.STRING),@var(name = "intensity", type = IType.FLOAT),
	@var(name = "about", type = IType.NONE),@var(name = "decay", type = IType.FLOAT),
	@var(name="agentCause", type=IType.AGENT)})
public class Emotion implements IValue {

	String name;
	Double intensity=-1.0;
	Predicate about;
	Double decay = 0.0;
	IAgent agentCause;
	private boolean noAgentCause = true;
	private boolean noIntensity = true; 
	private boolean noAbout = true;
	
	@getter("name")
	public String getName() {
		return name;
	}
	
	@getter("intensity")
	public Double getIntensity(){
		return intensity;
	}
	
	@getter("about")
	public Predicate getAbout(){
		return about;
	}
	
	@getter("decay")
	public Double getDecay(){
		return decay;
	}
	
	@getter("agentCause")
	public IAgent getAgentCause(){
		return agentCause;
	}
	
	public boolean getNoIntensity(){
		return this.noIntensity;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setIntensity(Double intens){
		this.intensity = intens;
		this.noIntensity = false;
	}
	
	public void setAbout(Predicate ab){
		this.about = ab;
		this.noAbout = false;
	}
	
	public void setDecay(Double de){
		this.decay = de;
	}
	
	public void setAgentCause(IAgent ag){
		this.agentCause = ag;
		this.noAgentCause = false;
	}
	
	public Emotion(){
		this.name="";
		this.about = null;
		this.agentCause = null;
	}
	
	public Emotion(String name){
		this.name=name;
		this.about=null;
		this.agentCause = null;
	}
	
	public Emotion(String name, Double intensity2){
		this.name=name;
		this.intensity=intensity2;
		this.about=null;
		this.agentCause=null;
		this.noIntensity = false;
	}
	
	public Emotion(String name,Predicate ab){
		this.name=name;
		this.about=ab;
		this.agentCause=null;
		this.noAbout = false;
	}
	
	public Emotion(String name,IAgent ag){
		this.name=name;
		this.about=null;
		this.agentCause=ag;
		this.noAgentCause= ag==null;
	}
	
	public Emotion(String name, Double intens, Double de){
		this.name=name;
		this.intensity=intens;
		this.about=null;
		this.agentCause=null;
		this.decay=de;
		this.noIntensity = false;
		this.noAbout = false;
	}
	
	public Emotion(String name, Double intens, Predicate ab){
		this.name=name;
		this.intensity=intens;
		this.about=ab;
		this.agentCause=null;
		this.noIntensity = false;
		this.noAbout = false;
	}
	
	public Emotion(String name, Predicate ab, IAgent ag){
		this.name=name;
		this.about=ab;
		this.agentCause=ag;
		this.noAbout = false;
		this.noAgentCause= ag==null;
	}
	
	public Emotion(String name, Double intens, IAgent ag){
		this.name=name;
		this.intensity=intens;
		this.about=null;
		this.agentCause=ag;
		this.noIntensity = false;
		this.noAgentCause= ag==null;
	}
	
	public Emotion(String name, Double intens, Predicate ab, Double de){
		this.name=name;
		this.intensity=intens;
		this.about=ab;
		this.agentCause=null;
		this.decay=de;
		this.noIntensity = false;
		this.noAbout = false;
	}
	
	public Emotion(String name, Double intens, Double de, IAgent ag){
		this.name=name;
		this.intensity=intens;
		this.about=null;
		this.agentCause=ag;
		this.decay=de;
		this.noIntensity = false;
		this.noAgentCause= ag==null;
	}
	
	public Emotion(String name, Double intens, Predicate ab, IAgent ag){
		this.name=name;
		this.intensity=intens;
		this.about=ab;
		this.agentCause=ag;
		this.noIntensity = false;
		this.noAgentCause= ag==null;
		this.noAbout=false;
	}
	
	public Emotion(String name, Double intens, Predicate ab, Double de, IAgent ag){
		this.name=name;
		this.intensity=intens;
		this.about=ab;
		this.agentCause=ag;
		this.decay=de;
		this.noIntensity = false;
		this.noAbout = false;
		this.noAgentCause= ag==null;
	}
	
	public void decayIntensity(){
		this.intensity=this.intensity-this.decay;
	}
	
	@Override
	public String toString() {
		return serialize(true);
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		if(intensity>0){
			return "emotion(" + name +","+ intensity +","+ about +","+ decay+","+ agentCause+")";
		}
		else{
			return "emotion(" + name +","+ about +","+ decay+","+ agentCause +")";
		}
	}
	
	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return name ;
	}

	@Override
	public IValue copy(final IScope scope) throws GamaRuntimeException {
		return new Emotion(name,intensity,about,decay);
	}

	@Override
	public boolean equals(final Object obj) {
		if ( this == obj ) { return true; }
		if ( obj == null ) { return false; }
		if ( getClass() != obj.getClass() ) { return false; }
		Emotion other = (Emotion)obj;
		if(name==null){
			if(other.name!=null){return false;}
		}else if(!name.equals(other.name)){return false;}
		if((noAbout && noAgentCause )|| (other.noAbout && other.noAgentCause)){
			return true;
		}
		if(about==null){
			if(other.about!=null){return false;}			
		}else if(!about.equals(other.about)){return false;}
		if(agentCause==null){
			if(other.agentCause!=null){return false;}
		}else if(!agentCause.equals(other.agentCause)){return false;}
		return true;
	}
	
	@Override
	public IType getType() {
		return Types.get(EmotionType.id);
	}
	
	
}
