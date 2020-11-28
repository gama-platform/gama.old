/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.ILayerData.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import java.awt.Point;

import org.locationtech.jts.geom.Envelope;

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

	void compute(final IScope sim, IGraphics g) throws GamaRuntimeException;

	void setTransparency(final double f);

	Double getTransparency(final IScope scope);

	void setSize(final ILocation p);

	void setSize(final double width, final double height, final double depth);

	boolean isRelativePosition();

	boolean isRelativeSize();

	void setPosition(final ILocation p);

	void setPosition(final double x, final double y, final double z);

	ILocation getPosition();

	ILocation getSize();

	Boolean getRefresh();

	Integer getTrace();

	Boolean getFading();

	Boolean isSelectable();

	void setSelectable(Boolean b);

	Point getPositionInPixels();

	Point getSizeInPixels();

	void computePixelsDimensions(IGraphics g);

	void addElevation(double currentElevation);

	void setVisibleRegion(Envelope e);

	Envelope getVisibleRegion();

	double getAddedElevation();

	/**
	 * Whether the layer is to be refreshed dynamically everytime the surface displays itself
	 *
	 * @return true if the layer is dynamic, false otherwise
	 */
	default boolean isDynamic() {
		return getRefresh() == null || getRefresh();
	}

}