/*********************************************************************************************
 *
 * 'IDisplayLayerBox.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.outputs.layers;

import java.awt.Point;

import com.vividsolutions.jts.geom.Envelope;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * The class IDisplayLayerBox.
 *
 * @author drogoul
 * @since 14 d�c. 2011
 *
 */
public interface ILayerData {

	public abstract void compute(final IScope sim, IGraphics g) throws GamaRuntimeException;

	public abstract void setTransparency(final double f);

	public abstract void setSize(final ILocation p);

	public abstract void setSize(final double width, final double height, final double depth);

	public abstract boolean isRelativePosition();

	public abstract boolean isRelativeSize();

	public abstract void setPosition(final ILocation p);

	public abstract void setPosition(final double x, final double y, final double z);

	public abstract void setRefresh(final Boolean r);

	public abstract Double getTransparency();

	public abstract ILocation getPosition();

	public abstract ILocation getSize();

	public abstract Boolean getRefresh();

	public abstract Integer getTrace();

	public abstract Boolean getFading();

	public abstract Boolean isSelectable();

	public abstract void setSelectable(Boolean b);

	Point getPositionInPixels();

	Point getSizeInPixels();

	void computePixelsDimensions(IGraphics g);

	public abstract void addElevation(double currentElevation);

	void setVisibleRegion(Envelope e);

	Envelope getVisibleRegion();

	public abstract double getAddedElevation();

	/**
	 * Whether the layer is to be refreshed dynamically everytime the surface displays itself
	 * 
	 * @return true if the layer is dynamic, false otherwise
	 */
	default boolean isDynamic() {
		return getRefresh() == null || getRefresh();
	}

}