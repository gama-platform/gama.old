/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.metamodel.topology.filter;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gaml.species.ISpecies;

public abstract class In implements IAgentFilter {

	public static In list(final IScope scope, final IContainer<?, IShape> targets)
		throws GamaRuntimeException {
		return new InList(new HashSet(targets.listValue(scope)));
	}

	public static In species(final ISpecies species) {
		return new InSpecies(species);
	}

	@Override
	public abstract boolean accept(IShape source, IShape a);

	private static class InList extends In {

		final Set<IShape> agents;

		InList(final Set list) {
			agents = list;
		}

		@Override
		public boolean accept(final IShape source, final IShape a) {
			return a.getGeometry() != source.getGeometry() && agents.contains(a);
		}

	}

	private static class InSpecies extends In {

		final ISpecies species;

		InSpecies(final ISpecies s) {
			species = s;
		}

		@Override
		public boolean accept(final IShape source, final IShape a) {
			return a.getGeometry() != source.getGeometry() &&
				((IAgent) a).isInstanceOf(species, true);
		}

	}

}
