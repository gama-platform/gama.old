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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.displays.layers;

import java.awt.*;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.outputs.layers.ILayerStatement;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Written by drogoul Modified on 9 nov. 2009
 * 
 * @todo Description
 * 
 */
public class PauseLayer extends AbstractLayer {

	/**
	 * @param env_width
	 * @param env_height
	 * @param layer
	 * @param dg
	 */
	protected PauseLayer(final double env_width, final double env_height,
		final ILayerStatement layer, final IGraphics dg) {
		super(env_width, env_height, layer, dg);
	}

	/**
	 * @see msi.gama.gui.displays.AbstractDisplay#privateDrawDisplay(msi.gama.common.interfaces.IGraphics)
	 */
	@Override
	protected void privateDrawDisplay(final IGraphics g) throws GamaRuntimeException {
		g.fill(Color.DARK_GRAY, 0.5);
		g.setFont(new Font("Helvetica", Font.BOLD, 18));
		g.setDrawingOffset(0, 0);
		g.setDrawingCoordinates(20, 20);
		g.setOpacity(1);
		g.drawString(null,"Display paused", Color.white, 0,0.0f);
	}

	/**
	 * @see msi.gama.gui.displays.AbstractDisplay#getType()
	 */
	@Override
	protected String getType() {
		return "Pause screen";
	}
}
