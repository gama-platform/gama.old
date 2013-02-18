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
package msi.gama.metamodel.shape;

import msi.gama.common.interfaces.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gaml.types.IType;
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
@vars({ @var(name = "perimeter", type = IType.FLOAT_STR) })
public interface IShape extends ILocated, IValue {

	public abstract IAgent getAgent();

	public abstract void setAgent(IAgent agent);

	public abstract IShape getGeometry();

	public abstract void setGeometry(IShape g);

	public abstract boolean isPoint();

	public abstract Geometry getInnerGeometry();

	public abstract Envelope getEnvelope();

	public abstract boolean covers(IShape g);
	
	public abstract boolean crosses(IShape g);

	public abstract double euclidianDistanceTo(IShape g);

	public abstract double euclidianDistanceTo(ILocation g);

	public abstract boolean intersects(IShape g);

	@getter( "perimeter")
	public abstract double getPerimeter();

	public abstract void setInnerGeometry(Geometry intersection);

	public abstract void dispose();

}
