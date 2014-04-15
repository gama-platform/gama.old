/*********************************************************************************************
 * 
 *
 * 'IAgentFilter.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.metamodel.topology.filter;

import java.util.Collection;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.util.IContainer;
import msi.gaml.species.ISpecies;

public interface IAgentFilter {

	public ISpecies getSpecies();

	public IContainer<?, ? extends IShape> getAgents();

	boolean accept(IScope scope, IShape source, IShape a);

	public void filter(IScope scope, IShape source, Collection<? extends IShape> results);

}