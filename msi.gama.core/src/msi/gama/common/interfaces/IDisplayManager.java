/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.common.interfaces;

import java.util.List;

/**
 * The class IDisplayManager.
 * 
 * @author drogoul
 * @since 15 déc. 2011
 * 
 */
public interface IDisplayManager extends ItemList<IDisplay> {

	/**
	 * @param abstractDisplay
	 * @param newValue
	 */
	void enableDisplay(IDisplay display, Boolean newValue);

	/**
	 * @param abstractDisplay
	 * @return
	 */
	boolean isEnabled(IDisplay display);

	/**
	 * @param env_width
	 * @param env_height
	 */
	void updateEnvDimensions(double env_width, double env_height);

	/**
	 * @param xc
	 * @param yc
	 * @return
	 */
	List<IDisplay> getDisplays(int xc, int yc);

	/**
	 * @param displayGraphics
	 */
	void drawDisplaysOn(IGraphics displayGraphics);

	/**
	 * 
	 */
	void dispose();

	/**
	 * @param createDisplay
	 * @return
	 */
	IDisplay addDisplay(IDisplay createDisplay);

}
