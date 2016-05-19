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

import com.vividsolutions.jts.geom.Envelope;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gama.util.file.GamaGridFile;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.statements.draw.FileDrawingAttributes;

/**
 * Written by drogoul Modified on 9 nov. 2009
 *
 * @todo Description
 *
 */
public class ImageLayer extends AbstractLayer {

	GamaImageFile file = null;
	GamaGridFile grid = null;
	private String imageFileName = "";
	Envelope env = null;

	public ImageLayer(final IScope scope, final ILayerStatement layer) {
		super(layer);
		buildImage(scope);
	}

	protected void buildImage(final IScope scope) {
		final String newImage = ((ImageLayerStatement) definition).getImageFileName();
		if (imageFileName != null && imageFileName.equals(newImage)) {
			return;
		}
		imageFileName = newImage;
		if (imageFileName == null || imageFileName.length() == 0) {
			file = null;
			grid = null;
		} else {
			file = new GamaImageFile(scope, imageFileName);
			env = file.getGeoDataFile() == null ? null : file.computeEnvelope(scope);
			if (!file.isGeoreferenced()) {
				env = null;
			}
		}
	}

	@Override
	public void privateDrawDisplay(final IScope scope, final IGraphics dg) {
		if (dg.cannotDraw())
			return;
		buildImage(scope);
		if (file == null) {
			return;
		}
		final GamaPoint loc = env == null ? new GamaPoint(0, 0) : new GamaPoint(env.getMinX(), env.getMinY());
		final FileDrawingAttributes attributes = new FileDrawingAttributes(loc);
		if (env != null) {
			attributes.size = new GamaPoint(env.getWidth(), env.getHeight());
		}
		dg.drawFile(file, attributes);
	}

	@Override
	public String getType() {
		return "Image layer";
	}

}
