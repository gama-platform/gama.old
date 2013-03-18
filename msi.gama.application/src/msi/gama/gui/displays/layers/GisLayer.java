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

import java.awt.Color;
import msi.gama.common.interfaces.*;
import msi.gama.gui.parameters.EditorFactory;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.outputs.layers.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import org.eclipse.swt.widgets.Composite;

public class GisLayer extends AbstractLayer {

	public GisLayer(final double env_width, final double env_height, final ILayerStatement layer,
		final IGraphics dg) {
		super(env_width, env_height, layer, dg);

	}

	@Override
	public void privateDrawDisplay(final IGraphics g) {
		if ( disposed ) { return; }
		IScope scope = GAMA.obtainNewScope();
		Color color = ((ImageLayerStatement) definition).getGisLayer().getColor();
		for ( GamaShape geom : ((ImageLayerStatement) definition).getGisLayer().getObjects() ) {
			if ( geom != null ) {
				g.drawGamaShape(scope, geom, color, true, Color.black, null, false);
				// g.drawGeometry(geom, color, true, Color.black, null);
			}
		}
	}

	@Override
	public void fillComposite(final Composite compo, final IDisplaySurface container) {
		super.fillComposite(compo, container);
		EditorFactory.createFile(compo, "Shapefile:",
			((ImageLayerStatement) definition).getImageFileName(), new EditorListener<String>() {

				@Override
				public void valueModified(final String newValue) throws GamaRuntimeException {
					((ImageLayerStatement) definition).setGisLayerName(newValue);
				}

			});
	}

	@Override
	protected String getType() {
		return "Gis layer";
	}

}
