/*********************************************************************************************
 *
 * 'QuadTreeLayer.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.outputs.layers;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.runtime.IScope;

/**
 * Written by drogoul Modified on 9 nov. 2009
 *
 * @todo Description
 *
 */
@Deprecated
public class QuadTreeLayer extends AbstractLayer {

	public QuadTreeLayer(final ILayerStatement layer) {
		super(layer);
	}

	@Override
	public void privateDrawDisplay(final IScope scope, final IGraphics dg) {
		// BufferedImage image = ((QuadTreeLayerStatement) definition).getSupportImage();
		// dg.drawImage(scope, image, null, null, null, null, true, null);
	}

	@Override
	public String getType() {
		return "Quadtree layer";
	}

}
