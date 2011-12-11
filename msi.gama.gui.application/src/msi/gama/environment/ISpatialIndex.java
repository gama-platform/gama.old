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

import java.awt.Graphics2D;
import msi.gama.interfaces.*;
import msi.gama.util.GamaList;
import com.vividsolutions.jts.geom.*;

/**
 * Written by drogoul Modified on 23 févr. 2011
 * 
 * @todo Description
 * 
 */
public interface ISpatialIndex {

	public abstract void insert(final Envelope bounds, final IAgent o);

	public abstract void insert(final Coordinate location, final IAgent agent);

	public abstract void remove(final Envelope bounds, final IAgent o);

	public abstract void remove(final Coordinate previousLoc, final IAgent agent);

	public abstract GamaList<IAgent> allAtDistance(final IGeometry source, final double dist,
		final IAgentFilter f);

	public abstract IAgent firstAtDistance(final IGeometry source, final double dist,
		final IAgentFilter f);

	public abstract GamaList<IAgent> allInEnvelope(final IGeometry source, final Envelope envelope,
		final IAgentFilter f, boolean contained);

	public abstract void drawOn(Graphics2D g2, int width, int height);

	public abstract void update();

}