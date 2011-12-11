/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.environment;

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;

public abstract class In implements IAgentFilter {

	public static In list(final IScope scope, final IGamaContainer<?, IGeometry> targets)
		throws GamaRuntimeException {
		return new InList(new HashSet(targets.listValue(scope)));
	}

	public static In species(final ISpecies species) {
		return new InSpecies(species);
	}

	@Override
	public abstract boolean accept(IGeometry source, IGeometry a);

	private static class InList extends In {

		final Set<IGeometry> agents;

		InList(final Set list) {
			agents = list;
		}

		@Override
		public boolean accept(final IGeometry source, final IGeometry a) {
			return a.getGeometry() != source.getGeometry() && agents.contains(a);
		}

	}

	private static class InSpecies extends In {

		final ISpecies species;

		InSpecies(final ISpecies s) {
			species = s;
		}

		@Override
		public boolean accept(final IGeometry source, final IGeometry a) {
			return a.getGeometry() != source.getGeometry() &&
				((IAgent) a).isInstanceOf(species, true);
		}

	}

}
