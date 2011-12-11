/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
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
package msi.gama.outputs.layers;

import java.awt.geom.Rectangle2D;
import msi.gama.interfaces.*;
import msi.gama.java.JavaConstExpression;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.*;

/**
 * Written by drogoul Modified on 16 nov. 2010
 * 
 * @todo Description
 * 
 */
public class LayerBox {

	IExpression transparency = new JavaConstExpression(0d);
	IExpression position = new JavaConstExpression(new GamaPoint(0, 0));
	IExpression extent = new JavaConstExpression(new GamaPoint(1, 1));

	Double currentTransparency;
	GamaPoint currentPosition;
	GamaPoint currentExtent;

	GamaPoint constantPosition = null;
	GamaPoint constantExtent = null;
	Double constantTransparency = null;

	Rectangle2D.Double currentBoundingBox = new Rectangle2D.Double();
	boolean constantBoundingBox = false;

	public LayerBox(final IExpression transp, final IExpression pos, final IExpression ext)
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
		setTransparency(transparency);
		setPosition(position);
		setExtent(extent);
	}

	public LayerBox(final Double transp, final GamaPoint pos, final GamaPoint ext) {
		setTransparency(transp);
		setPosition(pos);
		setExtent(ext);
	}

	public LayerBox(final Double transp, final Double posx, final Double posy, final Double extx,
		final Double exty) {
		setTransparency(transp);
		setPosition(posx, posy);
		setExtent(extx, exty);
	}

	public void compute(final IScope sim) throws GamaRuntimeException {
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
		}

	}

	public void setTransparency(final IExpression t) throws GamaRuntimeException {
		if ( t != null ) {
			transparency = t;
			if ( t.isConst() ) {
				setTransparency(Cast.asFloat(t.value(GAMA.getDefaultScope())));
			}
		}
	}

	public void setPosition(final IExpression p) throws GamaRuntimeException {
		if ( p != null ) {
			position = p;
			if ( p.isConst() ) {
				setPosition(Cast.asPoint(position.value(GAMA.getDefaultScope())));
			}
		}
	}

	public void setExtent(final IExpression e) throws GamaRuntimeException {
		if ( e != null ) {
			extent = e;
			if ( e.isConst() ) {
				setExtent(Cast.asPoint(extent.value(GAMA.getDefaultScope())));
			}
		}
	}

	public void setTransparency(final double f) {
		currentTransparency = constantTransparency = 1d - Math.min(Math.max(f, 0d), 1d);
	}

	public void setExtent(final GamaPoint p) {
		setExtent(p.x, p.y);
	}

	public void setExtent(final double width, final double height) {
		currentExtent = constantExtent = new GamaPoint(width, height);
		if ( constantPosition != null ) {
			constantBoundingBox = true;
			computeBoundingBox();
		}
	}

	public void setPosition(final GamaPoint p) {
		setPosition(p.x, p.y);
	}

	public void setPosition(final double x, final double y) {
		currentPosition = constantPosition = new GamaPoint(x, y);
		if ( constantExtent != null ) {
			constantBoundingBox = true;
			computeBoundingBox();
		}
	}

	public final Double getTransparency() {
		return currentTransparency;
	}

	public Rectangle2D.Double getBoundingBox() {
		return currentBoundingBox;
	}

	public GamaPoint getPosition() {
		return currentPosition;
	}

	public GamaPoint getExtent() {
		return currentExtent;
	}

	private Rectangle2D.Double computeBoundingBox() {
		currentBoundingBox.setRect(currentPosition.x, currentPosition.y, Math.abs(currentExtent.x),
			Math.abs(currentExtent.y));
		return currentBoundingBox;
	}

}
