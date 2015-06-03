package msi.gaml.extensions.genstar;

import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.IList;

public interface IGamaPopulationsLinker {

	public abstract void setTotalRound(final int totalRound); 
	
	public abstract int getTotalRound();
	
	public abstract int getCurrentRound();
	
	public abstract void establishRelationship(final IScope scope, final IList<IList<IMacroAgent>> populations);
}
