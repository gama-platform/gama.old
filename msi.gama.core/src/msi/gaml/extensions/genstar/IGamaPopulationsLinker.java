/*********************************************************************************************
 *
 * 'IGamaPopulationsLinker.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
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
