/*********************************************************************************************
 *
 *
 * 'ImageLayer.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.outputs.layers;

import java.awt.image.BufferedImage;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.util.ImageUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gaml.statements.draw.DrawingData.DrawingAttributes;

/**
 * Written by drogoul Modified on 9 nov. 2009
 *
 * @todo Description
 *
 */
public class ImageLayer extends AbstractLayer {

	BufferedImage image = null;
	private String imageFileName = "";

	public ImageLayer(final IScope scope, final ILayerStatement layer) {
		super(layer);
		buildImage(scope);
	}

	protected void buildImage(final IScope scope) {
		String newImage = ((ImageLayerStatement) definition).getImageFileName();
		if ( imageFileName != null && imageFileName.equals(newImage) ) { return; }
		imageFileName = newImage;
		if ( imageFileName == null || imageFileName.length() == 0 ) {
			image = null;
		} else {
			try {
				image = ImageUtils.getInstance().getImageFromFile(scope, imageFileName);
			} catch (Exception e) {
				image = null;
				e.printStackTrace();
			}
		}
	}

	@Override
	public void privateDrawDisplay(final IScope scope, final IGraphics dg) {
		buildImage(scope);
		if ( image == null ) { return; }
		DrawingAttributes attributes = new DrawingAttributes(new GamaPoint(0, 0), null, null);
		dg.drawImage(image, attributes);
	}

	@Override
	public String getType() {
		return "Image layer";
	}

}
