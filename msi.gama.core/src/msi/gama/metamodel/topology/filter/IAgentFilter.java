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

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IContainer;
import msi.gaml.species.ISpecies;

public interface IAgentFilter {

	public static final IAgentFilter NONE = new IAgentFilter() {

		@Override
		public ISpecies getSpecies() {
			return null;
		}

		@Override
		public IContainer<?, ? extends IAgent> getAgents(final IScope scope) {
			return GamaListFactory.create();
		}

		@Override
		public boolean accept(final IScope scope, final IShape source, final IShape a) {
			return true;
		}

		@Override
		public void filter(final IScope scope, final IShape source, final Collection<? extends IShape> results) {
		}
	};

	public ISpecies getSpecies();

	public IContainer<?, ? extends IAgent> getAgents(IScope scope);

	boolean accept(IScope scope, IShape source, IShape a);

	public void filter(IScope scope, IShape source, Collection<? extends IShape> results);

}