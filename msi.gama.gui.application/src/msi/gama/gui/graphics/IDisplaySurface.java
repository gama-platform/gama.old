/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
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
package msi.gama.gui.graphics;

import java.awt.Color;
import java.awt.image.BufferedImage;
import msi.gama.gui.displays.IDisplay;
import msi.gama.interfaces.*;

/**
 * Written by drogoul Modified on 26 nov. 2009
 * 
 * @todo Description
 * 
 */
public interface IDisplaySurface {

	public static final double SELECTION_SIZE = 20; // pixels
	public static final int MAX_SIZE = 4000; // pixels

	BufferedImage getImage();

	void dispose();

	void updateDisplay();

	int[] computeBoundsFrom(int width, int height);

	boolean resizeImage(int width, int height);

	void outputChanged(final double env_width, final double env_height, final IDisplayOutput output);

	void zoomIn();

	void zoomOut();

	void zoomFit();

	DisplayManager getManager();

	void fireSelectionChanged(Object a);

	void focusOn(IGeometry geometry, IDisplay display);

	boolean canBeUpdated();

	void canBeUpdated(boolean ok);

	void setBackgroundColor(Color background);

}
