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
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaList;
import msi.gaml.species.ISpecies;

public interface IAgentFilter {

	public static class Or implements IAgentFilter {

		IAgentFilter a, b;

		public Or(final IAgentFilter a, final IAgentFilter b) {
			this.a = a;
			this.b = b;
		}

		@Override
		public boolean accept(final IShape source, final IShape agent) {
			return a.accept(source, agent) || b.accept(source, agent);
		}

		@Override
		public List<? extends IShape> filter(final IShape source, final List<? extends IShape> ags) {
			List<IShape> list = (List<IShape>) a.filter(source, ags);
			list.addAll(b.filter(source, ags));
			return new GamaList(new HashSet(list));
		}

		@Override
		public boolean filterSpecies(final ISpecies s) {
			return a.filterSpecies(s) || b.filterSpecies(s);
		}
	}

	public static class And implements IAgentFilter {

		IAgentFilter a, b;

		public And(final IAgentFilter a, final IAgentFilter b) {
			this.a = a;
			this.b = b;
		}

		@Override
		public boolean accept(final IShape source, final IShape agent) {
			return a.accept(source, agent) && b.accept(source, agent);
		}

		@Override
		public List<? extends IShape> filter(final IShape source, final List<? extends IShape> ags) {
			return b.filter(source, a.filter(source, ags));
		}

		@Override
		public boolean filterSpecies(final ISpecies s) {
			return a.filterSpecies(s) && b.filterSpecies(s);
		}
	}

	public boolean accept(IShape source, IShape a);

	public List<? extends IShape> filter(final IShape source, final List<? extends IShape> ags);

	/**
	 * @param cellSpecies
	 * @return
	 */
	public boolean filterSpecies(ISpecies species);

}