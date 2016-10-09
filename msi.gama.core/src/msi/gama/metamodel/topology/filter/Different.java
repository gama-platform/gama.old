/*********************************************************************************************
 * 
 * 
 * 'Different.java', in plugin 'msi.gama.core', is part of the source code of the
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
import msi.gama.util.*;
import msi.gaml.species.ISpecies;

public class Different implements IAgentFilter {

	private static final Different instance = new Different();

	public static Different with() {
		return instance;
	}

	@Override
	public boolean accept(final IScope scope, final IShape source, final IShape a) {
		return a.getGeometry() != source.getGeometry();
	}

	/**
	 * @see msi.gama.metamodel.topology.filter.IAgentFilter#getShapes()
	 */
	@Override
	public IContainer<?, ? extends IAgent> getAgents(final IScope scope) {
		return GamaListFactory.create();
	}

	@Override
	public ISpecies getSpecies() {
		return null;
	}

	/**
	 * Method filter()
	 * @see msi.gama.metamodel.topology.filter.IAgentFilter#filter(java.util.Collection)
	 */
	@Override
	public void filter(final IScope scope, final IShape source, final Collection<? extends IShape> internal_results) {
		internal_results.remove(source);
	}

}