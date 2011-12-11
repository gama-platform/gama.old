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

import java.awt.Color;
import msi.gama.gui.graphics.DisplayManager.DisplayItem;
import msi.gama.gui.graphics.*;
import msi.gama.gui.parameters.*;
import msi.gama.internal.compilation.*;
import msi.gama.kernel.exceptions.*;
import msi.gama.outputs.layers.*;
import org.eclipse.swt.widgets.Composite;
import com.vividsolutions.jts.geom.Geometry;

public class GisDisplay extends AbstractDisplay {

	public GisDisplay(final double env_width, final double env_height,
		final AbstractDisplayLayer layer, final IGraphics dg) {
		super(env_width, env_height, layer, dg);

	}

	@Override
	public void privateDrawDisplay(final IGraphics g) {
		if ( disposed ) { return; }
		Color color = ((ImageDisplayLayer) model).getGisLayer().getColor();
		for ( Geometry geom : ((ImageDisplayLayer) model).getGisLayer().getObjects() ) {
			if ( geom != null ) {
				g.drawGeometry(geom, color, true, null);
			}
		}
	}

	@Override
	public void fillComposite(final Composite compo, final DisplayItem item,
		final IDisplaySurface container) throws GamaRuntimeException {
		super.fillComposite(compo, item, container);
		EditorFactory.createFile(compo, "Shapefile:",
			((ImageDisplayLayer) model).getImageFileName(), new EditorListener<String>() {

				@Override
				public void valueModified(final String newValue) throws GamaRuntimeException,
					GamlException {
					((ImageDisplayLayer) model).setGisLayerName(newValue);
				}

			});
	}

	@Override
	protected String getType() {
		return "Gis layer";
	}

}
