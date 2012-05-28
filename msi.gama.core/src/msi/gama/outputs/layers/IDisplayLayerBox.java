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
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.outputs.layers;

import java.awt.geom.Rectangle2D;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;

/**
 * The class IDisplayLayerBox.
 * 
 * @author drogoul
 * @since 14 déc. 2011
 * 
 */
public interface IDisplayLayerBox {

	public abstract void compute(final IScope sim) throws GamaRuntimeException;

	public abstract void setTransparency(final IExpression t) throws GamaRuntimeException;

	public abstract void setPosition(final IExpression p) throws GamaRuntimeException;

	public abstract void setExtent(final IExpression e) throws GamaRuntimeException;
	
	public abstract void setElevation(final IExpression e) throws GamaRuntimeException;

	public abstract void setTransparency(final double f);

	public abstract void setExtent(final ILocation p);

	public abstract void setExtent(final double width, final double height);

	public abstract void setPosition(final ILocation p);

	public abstract void setPosition(final double x, final double y);
	
	public abstract void setElevation(final double e);

	public abstract Double getTransparency();

	public abstract Rectangle2D.Double getBoundingBox();

	public abstract ILocation getPosition();

	public abstract ILocation getExtent();
	
	public abstract Double getElevation();

}