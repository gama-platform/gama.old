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
package msi.gama.gui.displays;

import msi.gama.gui.graphics.IGraphics;
import msi.gama.outputs.layers.*;

/**
 * Written by drogoul Modified on 9 nov. 2009
 * 
 * @todo Description
 * 
 */
public class QuadTreeDisplay extends AbstractDisplay {

	public QuadTreeDisplay(final double env_width, final double env_height,
		final AbstractDisplayLayer layer, final IGraphics dg) {
		super(env_width, env_height, layer, dg);
	}

	@Override
	public void privateDrawDisplay(final IGraphics dg) {
		if ( disposed ) { return; }
		dg.drawImage(((QuadTreeDisplayLayer) model).getSupportImage(), null);
	}

	@Override
	protected String getType() {
		return "Quadtree layer";
	}

}
