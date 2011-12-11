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
package msi.gama.interfaces;

import msi.gama.util.GamaGeometry;
import com.vividsolutions.jts.geom.*;

/**
 * Interface for objects that can be provided with a geometry (or which can be translated to
 * a GamaGeometry)
 * 
 * @author Alexis Drogoul
 * @since 16 avr. 2011
 * @modified November 2011 to include isPoint(), getInnerGeometry() and getEnvelope()
 * 
 */

public interface IGeometry extends ILocation, IValue {

	public abstract IAgent getAgent();

	public abstract void setAgent(IAgent agent);

	public abstract GamaGeometry getGeometry();

	public abstract void setGeometry(GamaGeometry g);

	public abstract boolean isPoint();

	public abstract Geometry getInnerGeometry();

	public abstract Envelope getEnvelope();

	public abstract boolean covers(IGeometry g);

	public abstract double euclidianDistanceTo(IGeometry g);

	public abstract boolean intersects(IGeometry g);

}
