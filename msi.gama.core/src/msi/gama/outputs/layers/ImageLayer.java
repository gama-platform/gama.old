/*********************************************************************************************
 *
 * 'ImageLayer.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.outputs.layers;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gama.util.file.GamaGridFile;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.statements.draw.FileDrawingAttributes;
import msi.gaml.types.GamaFileType;

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
	Envelope3D env;

	public ImageLayer(final IScope scope, final ILayerStatement layer) {
		super(layer);
		buildImage(scope);
	}

	protected Envelope3D buildImage(final IScope scope) {
		final String newImage = ((ImageLayerStatement) definition).getImageFileName();
		if (imageFileName != null && imageFileName.equals(newImage)) { return env; }
		imageFileName = newImage;
		if (imageFileName == null || imageFileName.length() == 0) {
			file = null;
			grid = null;
		} else {
			@SuppressWarnings ("rawtypes") final GamaImageFile f =
					GamaFileType.createImageFile(scope, imageFileName, null);
			if (f != null) {
				file = f;
				env = file.getGeoDataFile(scope) == null ? scope.getSimulation().getEnvelope()
						: file.computeEnvelope(scope);
			}
		}
		return env;
	}

	@Override
	public void privateDrawDisplay(final IScope scope, final IGraphics dg) {
		buildImage(scope);
		if (file == null) { return; }
		final FileDrawingAttributes attributes = new FileDrawingAttributes(null, true);
		if (env != null) {
			final GamaPoint loc = new GamaPoint(env.getMinX(), env.getMinY());
			attributes.setLocation(loc);
			attributes.setSize(Scaling3D.of(env.getWidth(), env.getHeight(), 0));
		}
		dg.drawFile(file, attributes);
	}

	@Override
	public String getType() {
		return "Image layer";
	}

}
