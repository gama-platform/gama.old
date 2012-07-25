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
package msi.gama.outputs.layers;

import java.awt.geom.Rectangle2D;
import msi.gama.common.interfaces.INamed;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * The class IDisplayLayer.
 * 
 * @author drogoul
 * @since 14 déc. 2011
 * 
 */
public interface ILayerStatement extends INamed {

	public final static short GRID = 1;
	public final static short AGENTS = 2;
	public final static short SPECIES = 3;
	public final static short TEXT = 4;
	public final static short IMAGE = 5;
	public final static short GIS = 6;
	public final static short CHART = 7;
	public final static short QUADTREE = 8;

	public abstract void prepare(final IDisplayOutput out, final IScope sim)
		throws GamaRuntimeException;

	public abstract short getType();

	public abstract void compute(final IScope scope, final long cycle) throws GamaRuntimeException;

	public abstract Double getTransparency();
	
	public abstract Double getElevation();
	
	public abstract Boolean getRefresh();
	
	public abstract Rectangle2D.Double getBoundingBox();

	public abstract IDisplayLayerBox getBox();

	// public abstract void setPhysicalLayer(IDisplay abstractDisplay);
	
	public abstract void setElevation(Double elevation);

	public abstract void setRefresh(Boolean refresh);
	
	public abstract void setOpacity(Double opacity);

}