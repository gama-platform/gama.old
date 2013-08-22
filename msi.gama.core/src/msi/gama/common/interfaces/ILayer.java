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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.common.interfaces;

import java.awt.Point;
import java.util.Set;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
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

	GamaPoint getModelCoordinatesFrom(int x, int y, IDisplaySurface g);

	public boolean containsScreenPoint(final int x, final int y);

	void dispose();

	Point getPositionInPixels();

	Point getSizeInPixels();

	// double getXScale();

	// double getYScale();

	void setOpacity(Double value);

	void setElevation(Double value);

	// void updateEnvDimensions(double env_width, double env_height);

	void setOrder(Integer i);

	Integer getOrder();

	public abstract boolean stayProportional();

	void outputChanged();

	double getZPosition();

	Boolean isDynamic();

	public abstract String getType();

}
