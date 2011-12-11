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

import java.awt.image.BufferedImage;
import msi.gama.gui.graphics.DisplayManager.DisplayItem;
import msi.gama.gui.graphics.*;
import msi.gama.gui.parameters.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.outputs.layers.*;
import org.eclipse.swt.widgets.Composite;

/**
 * Written by drogoul Modified on 9 nov. 2009
 * 
 * @todo Description
 * 
 */
public class ImageDisplay extends AbstractDisplay {

	BufferedImage image = null;
	private String imageFileName = "";

	public ImageDisplay(final double env_width, final double env_height,
		final AbstractDisplayLayer layer, final IGraphics dg) {
		super(env_width, env_height, layer, dg);
		buildImage();
	}

	protected void buildImage() {
		String newImage = ((ImageDisplayLayer) model).getImageFileName();
		if ( imageFileName != null && imageFileName.equals(newImage) ) { return; }
		imageFileName = newImage;
		if ( imageFileName == null || imageFileName.length() == 0 ) {
			image = null;
		} else {
			try {
				image = ((ImageDisplayLayer) model).getImage(imageFileName);
			} catch (Exception e) {
				image = null;
				e.printStackTrace();
			}
		}
	}

	@Override
	public void fillComposite(final Composite compo, final DisplayItem item,
		final IDisplaySurface container) throws GamaRuntimeException {
		super.fillComposite(compo, item, container);

		if ( model instanceof ImageDisplayLayer ) {
			EditorFactory.createFile(compo, "Image:",
				((ImageDisplayLayer) model).getImageFileName(), new EditorListener<String>() {

					@Override
					public void valueModified(final String newValue) {
						((ImageDisplayLayer) model).setImageFileName(newValue);
					}

				});
		}
	}

	@Override
	public void privateDrawDisplay(final IGraphics dg) {
		if ( disposed ) { return; }
		buildImage();
		if ( image == null ) { return; }
		dg.drawImage(image, null);
	}

	@Override
	protected String getType() {
		return "Image layer";
	}

}
