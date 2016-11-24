/*********************************************************************************************
 *
 * 'ILayer.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.common.interfaces;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.*;
import com.vividsolutions.jts.geom.Envelope;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.outputs.layers.ILayerStatement;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Written by drogoul Modified on 26 nov. 2009
 *
 * @todo Description
 *
 */
public interface ILayer extends INamed, Comparable<ILayer> {

	String getMenuName();

	void drawDisplay(IScope scope, IGraphics simGraphics) throws GamaRuntimeException;

	Set<IAgent> collectAgentsAt(int x, int y, IDisplaySurface g);

	ILocation getModelCoordinatesFrom(int x, int y, IDisplaySurface g);

	Point getScreenCoordinatesFrom(double x, double y, IDisplaySurface g);

	public boolean containsScreenPoint(final int x, final int y);

	void dispose();

	Point getPositionInPixels();

	Point getSizeInPixels();

	void setTransparency(Double value);

	void setElevation(Double value);

	void setOrder(Integer i);

	Integer getOrder();

	public abstract boolean stayProportional();

	void reloadOn(IDisplaySurface surface);

	void firstLaunchOn(IDisplaySurface surface);

	ILocation getExtent();

	Boolean isDynamic();

	public abstract String getType();

	void enableOn(IDisplaySurface surface);

	void disableOn(IDisplaySurface surface);

	ILocation getPosition();

	Integer getTrace();

	Boolean getFading();

	Rectangle2D focusOn(IShape geometry, IDisplaySurface s);

	/**
	 * @return
	 */
	ILayerStatement getDefinition();

	/**
	 * @param newValue
	 */
	void setPosition(ILocation newValue);

	/**
	 * @param newValue
	 */
	void setExtent(ILocation newValue);

	public Collection<IAgent> getAgentsForMenu(final IScope scope);

	/**
	 * @return
	 */
	Boolean isSelectable();

	/**
	 * @param e
	 */
	void setVisibleRegion(Envelope e);

	Envelope getVisibleRegion();

	/**
	 * @param xc
	 * @param yc
	 * @param java2dDisplaySurface
	 * @return
	 */
	String getModelCoordinatesInfo(int xc, int yc, IDisplaySurface java2dDisplaySurface);

	boolean isProvidingCoordinates();

	boolean isProvidingWorldCoordinates();

}
