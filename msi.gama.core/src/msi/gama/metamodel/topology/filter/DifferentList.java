package msi.gama.metamodel.topology.filter;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;

public class DifferentList implements IAgentFilter {

	
	final Set<IShape> agents;
	final IType contentType;

	public DifferentList(final IList<? extends IShape> list) {
		agents = new LinkedHashSet<IShape>(list);
		contentType = list.getType().getContentType();
	}

	@Override
	public boolean accept(final IScope scope, final IShape source, final IShape a) {
		return a.getGeometry() != source.getGeometry() && !agents.contains(a);
	}

	@Override
	public IContainer<?, ? extends IShape> getAgents(final IScope scope) {
		return GamaListFactory.createWithoutCasting(contentType, agents);
	}

	@Override
	public ISpecies getSpecies() {
		return null;
	}

	@Override
	public void filter(final IScope scope, final IShape source, final Collection<? extends IShape> results) {
		agents.remove(source);
		results.removeAll(agents);
	}

}
