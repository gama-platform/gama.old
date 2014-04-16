/*********************************************************************************************
 * 
 *
 * 'ILayer.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.common.interfaces;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.Set;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Written by drogoul Modified on 26 nov. 2009
 * 
 * @todo Description
 * 
 */
public interface ILayer extends INamed, Comparable<ILayer> {
	// hqnghi : getter and setter of scope for layer output
	public IScope getPrivateScope();
	
	public void setPrivateScope(IScope privateScope);
	//end-hqnghi
	String getMenuName();

	void drawDisplay(IScope scope, IGraphics simGraphics) throws GamaRuntimeException;

	Set<IAgent> collectAgentsAt(int x, int y, IDisplaySurface g);

	GamaPoint getModelCoordinatesFrom(int x, int y, IDisplaySurface g);

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

}
