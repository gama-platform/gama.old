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
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.metamodel.topology;

import java.awt.Graphics2D;
import java.util.Iterator;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gaml.species.ISpecies;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Written by drogoul Modified on 23 févr. 2011
 * 
 * @todo Description
 * 
 */
public interface ISpatialIndex {

	public final static Envelope ENVELOPE = new Envelope();

	public abstract void insert(IShape agent);

	public abstract void remove(final IShape previous, final IShape agent);

	public abstract Iterator<IShape> allAtDistance(final IShape source, final double dist, final IAgentFilter f);

	public abstract IShape firstAtDistance(final IShape source, final double dist, final IAgentFilter f);

	public abstract Iterator<IShape> allInEnvelope(final IShape source, final Envelope envelope, final IAgentFilter f,
		boolean contained);

	public abstract void drawOn(Graphics2D g2, int width, int height);

	public interface Compound extends ISpatialIndex {

		public abstract void add(ISpatialIndex index, ISpecies species);

		public abstract void dispose();
	}

}