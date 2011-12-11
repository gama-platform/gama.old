/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.displays;

import java.awt.*;
import msi.gama.gui.graphics.DisplayManager.DisplayItem;
import msi.gama.gui.graphics.*;
import msi.gama.interfaces.INamed;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

/**
 * Written by drogoul Modified on 26 nov. 2009
 * 
 * @todo Description
 * 
 */
public interface IDisplay extends INamed {

	public final static short GRID = 1;
	public final static short AGENTS = 2;
	public final static short SPECIES = 3;
	public final static short TEXT = 4;
	public final static short IMAGE = 5;
	public final static short GIS = 6;
	public final static short CHART = 7;
	public final static short QUADTREE = 8;

	String getMenuName();

	Image getMenuImage();

	void drawDisplay(IGraphics simGraphics) throws GamaRuntimeException;

	void collectAgentsAt(int x, int y);

	public boolean containsScreenPoint(final int x, final int y);

	void dispose();

	public void putMenuItemsIn(final Menu inMenu, int x, int y);

	void initMenuItems(IDisplaySurface displaySurface);

	Point getSize();

	Point getPosition();

	double getXScale();

	double getYScale();

	void fillComposite(Composite compo, final DisplayItem item, IDisplaySurface container)
		throws GamaRuntimeException;

	void setOpacity(Double value);

	void updateEnvDimensions(double env_width, double env_height);

}
