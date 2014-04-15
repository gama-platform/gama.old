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
package msi.gama.gui.displays.layers;

import java.awt.image.BufferedImage;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.ImageUtils;
import msi.gama.gui.parameters.EditorFactory;
import msi.gama.outputs.layers.*;
import msi.gama.runtime.IScope;
import org.eclipse.swt.widgets.Composite;

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
	public void fillComposite(final Composite compo, final IDisplaySurface container) {
		super.fillComposite(compo, container);

		if ( definition instanceof ImageLayerStatement ) {
			EditorFactory.create(compo, "Image:", ((ImageLayerStatement) definition).getImageFileName(), false,
				new EditorListener<String>() {

					@Override
					public void valueModified(final String newValue) {
						((ImageLayerStatement) definition).setImageFileName(newValue);
						if ( isPaused(container) ) {
							container.forceUpdateDisplay();
						}
					}

				});
		}
	}

	@Override
	public void privateDrawDisplay(final IScope scope, final IGraphics dg) {
		buildImage(scope);
		if ( image == null ) { return; }
		dg.drawImage(scope, image, null, null, null, null, false, null);
	}

	@Override
	public String getType() {
		return "Image layer";
	}

}
