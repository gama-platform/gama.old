/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.metamodel.topology.filter;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.IScope;
import msi.gama.util.*;
import msi.gaml.species.ISpecies;

public interface IAgentFilter {

	public static class Not implements IAgentFilter {

		IAgentFilter filter;

		public Not(final IAgentFilter other) {
			filter = other;
		}

		@Override
		public boolean accept(final IScope scope, final IShape source, final IShape a) {
			return !filter.accept(scope, source, a);
		}

		@Override
		public boolean accept(final IScope scope, final ILocation source, final IShape a) {
			return !filter.accept(scope, source, a);
		}

		@Override
		public ISpecies speciesFiltered() {
			return null;
		}

		@Override
		public boolean filterSpecies(final ISpecies species) {
			return !filter.filterSpecies(species);
		}

		@Override
		public IContainer<?, ? extends IShape> getShapes(final IScope scope) {
			IList<IAgent> agents = scope.getSimulationScope().getAgents();
			for ( IShape s : filter.getShapes(scope) ) {
				agents.remove(s);
			}
			// agents.removeAll((Collection<?>) filter.getShapes(scope).iterable(scope));
			return agents;
		}

	}

	public static class And implements IAgentFilter {

		IAgentFilter a, b;

		public And(final IAgentFilter a, final IAgentFilter b) {
			this.a = a;
			this.b = b;
		}

		@Override
		public boolean accept(final IScope scope, final IShape source, final IShape agent) {
			return a.accept(scope, source, agent) && b.accept(scope, source, agent);
		}

		@Override
		public boolean accept(final IScope scope, final ILocation source, final IShape agent) {
			return a.accept(scope, source, agent) && b.accept(scope, source, agent);
		}

		@Override
		public boolean filterSpecies(final ISpecies s) {
			return a.filterSpecies(s) && b.filterSpecies(s);
		}

		/**
		 * @see msi.gama.metamodel.topology.filter.IAgentFilter#getShapes()
		 */
		@Override
		public IContainer<?, ? extends IShape> getShapes(final IScope scope) {
			IList<IShape> result = new GamaList();
			for ( IShape s : a.getShapes(scope) ) {
				result.add(s);
			}
			for ( IShape s : b.getShapes(scope) ) {
				result.add(s);
			}
			return result;
		}

		@Override
		public ISpecies speciesFiltered() {
			ISpecies s1 = a.speciesFiltered();
			ISpecies s2 = b.speciesFiltered();
			if ( s1 == s2 ) { return s1; }
			return null;
		}
	}

	public ISpecies speciesFiltered();

	public boolean filterSpecies(ISpecies species);

	public IContainer<?, ? extends IShape> getShapes(IScope scope);

	/**
	 * @param scope
	 * @param source
	 * @param a
	 * @return
	 */
	boolean accept(IScope scope, IShape source, IShape a);

	/**
	 * @param scope
	 * @param source
	 * @param a
	 * @return
	 */
	boolean accept(IScope scope, ILocation source, IShape a);

}