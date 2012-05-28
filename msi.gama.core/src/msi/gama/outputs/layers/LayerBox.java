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
package msi.gama.outputs.layers;

import java.awt.geom.Rectangle2D;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.*;
import msi.gaml.operators.Cast;

/**
 * Written by drogoul Modified on 16 nov. 2010
 * 
 * @todo Description
 * 
 */
public class LayerBox implements IDisplayLayerBox {

	IExpression transparency = new JavaConstExpression(0d);
	IExpression position = new JavaConstExpression(new GamaPoint(0, 0));
	IExpression extent = new JavaConstExpression(new GamaPoint(1, 1));
	IExpression elevation = new JavaConstExpression(0d);

	Double currentTransparency;
	ILocation currentPosition;
	ILocation currentExtent;
	Double currentElevation;

	ILocation constantPosition = null;
	ILocation constantExtent = null;
	Double constantTransparency = null;
	Double constantElevation = null;

	Rectangle2D.Double currentBoundingBox = new Rectangle2D.Double();
	boolean constantBoundingBox = false;

	public LayerBox(final IExpression transp, final IExpression pos, final IExpression ext, final IExpression elev)
		throws GamaRuntimeException {
		if ( transp != null ) {
			transparency = transp;
		}
		if ( pos != null ) {
			position = pos;
		}
		if ( ext != null ) {
			extent = ext;
		}
		if ( elev != null ) {
			elevation = elev;
		}
		setTransparency(transparency);
		setPosition(position);
		setExtent(extent);
		setElevation(elevation);
	}

	public LayerBox(final Double transp, final GamaPoint pos, final GamaPoint ext,final Double elev) {
		setTransparency(transp);
		setPosition(pos);
		setExtent(ext);
		setElevation(elev);
	}

	public LayerBox(final Double transp, final Double posx, final Double posy, final Double extx,
		final Double exty,final Double elev) {
		setTransparency(transp);
		setPosition(posx, posy);
		setExtent(extx, exty);
		setElevation(elev);
	}

	@Override
	public void compute(final IScope sim) throws GamaRuntimeException {
		try {
			currentTransparency =
				constantTransparency == null ? 1d - Math.min(
					Math.max(Cast.asFloat(sim, transparency.value(sim)), 0d), 1d)
					: constantTransparency;
			if ( !constantBoundingBox ) {
				currentPosition =
					constantPosition == null ? Cast.asPoint(sim, position.value(sim))
						: constantPosition;
				currentExtent =
					constantExtent == null ? Cast.asPoint(sim, extent.value(sim)) : constantExtent;
				if ( currentPosition != null && currentExtent != null ) {
					computeBoundingBox();
				}
			currentElevation =
				constantElevation== null ? Cast.asFloat(sim, elevation.value(sim))
					: constantElevation;
			}
		} catch (Exception e) {
			throw new GamaRuntimeException(e);
		}

	}

	@Override
	public void setTransparency(final IExpression t) throws GamaRuntimeException {
		if ( t != null ) {
			transparency = t;
			if ( t.isConst() ) {
				setTransparency(Cast.asFloat(GAMA.getDefaultScope(),
					t.value(GAMA.getDefaultScope())));
			}
		}
	}

	@Override
	public void setPosition(final IExpression p) throws GamaRuntimeException {
		if ( p != null ) {
			position = p;
			if ( p.isConst() ) {
				setPosition(Cast.asPoint(GAMA.getDefaultScope(),
					position.value(GAMA.getDefaultScope())));
			}
		}
	}

	@Override
	public void setExtent(final IExpression e) throws GamaRuntimeException {
		if ( e != null ) {
			extent = e;
			if ( e.isConst() ) {
				setExtent(Cast
					.asPoint(GAMA.getDefaultScope(), extent.value(GAMA.getDefaultScope())));
			}
		}
	}
	
	@Override
	public void setElevation(final IExpression e) throws GamaRuntimeException {
		if ( e != null ) {
			elevation = e;
			if ( e.isConst() ) {
				setElevation(Cast.asFloat(GAMA.getDefaultScope(),
					e.value(GAMA.getDefaultScope())));
			}
		}
	}

	@Override
	public void setTransparency(final double f) {
		currentTransparency = constantTransparency = 1d - Math.min(Math.max(f, 0d), 1d);
	}

	@Override
	public void setExtent(final ILocation p) {
		setExtent(p.getX(), p.getY());
	}

	@Override
	public void setExtent(final double width, final double height) {
		currentExtent = constantExtent = new GamaPoint(width, height);
		if ( constantPosition != null ) {
			constantBoundingBox = true;
			computeBoundingBox();
		}
	}

	@Override
	public void setPosition(final ILocation p) {
		setPosition(p.getX(), p.getY());
	}

	@Override
	public void setPosition(final double x, final double y) {
		currentPosition = constantPosition = new GamaPoint(x, y);
		if ( constantExtent != null ) {
			constantBoundingBox = true;
			computeBoundingBox();
		}
	}
	
	@Override
	public void setElevation(final double e) {
		currentElevation = constantElevation = e;
	}

	@Override
	public final Double getTransparency() {
		return currentTransparency;
	}


	@Override
	public Rectangle2D.Double getBoundingBox() {
		return currentBoundingBox;
	}

	@Override
	public ILocation getPosition() {
		return currentPosition;
	}

	@Override
	public ILocation getExtent() {
		return currentExtent;
	}
	
	@Override
	public final Double getElevation() {
		return currentElevation;
	}

	private Rectangle2D.Double computeBoundingBox() {
		currentBoundingBox.setRect(currentPosition.getX(), currentPosition.getY(),
			Math.abs(currentExtent.getX()), Math.abs(currentExtent.getY()));
		return currentBoundingBox;
	}

}
