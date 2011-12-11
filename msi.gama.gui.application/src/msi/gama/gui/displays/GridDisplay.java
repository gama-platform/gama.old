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

import java.awt.*;
import java.awt.event.*;
import msi.gama.gui.graphics.DisplayManager.DisplayItem;
import msi.gama.gui.graphics.*;
import msi.gama.gui.parameters.*;
import msi.gama.interfaces.IAgent;
import msi.gama.kernel.exceptions.*;
import msi.gama.outputs.layers.*;
import msi.gama.util.GamaPoint;
import org.eclipse.swt.widgets.Composite;

public class GridDisplay extends ImageDisplay {

	private boolean turnGridOn;
	private Color lineColor;
	private IAgent placeSelected;
	private ActionListener placeMenuListener;

	public GridDisplay(final double env_width, final double env_height,
		final AbstractDisplayLayer model, final IGraphics dg) {
		super(env_width, env_height, model, dg);
	}

	@Override
	public void fillComposite(final Composite compo, final DisplayItem item,
		final IDisplaySurface container) throws GamaRuntimeException {
		super.fillComposite(compo, item, container);
		EditorFactory.create(compo, "Draw grid:", turnGridOn, new EditorListener<Boolean>() {

			@Override
			public void valueModified(final Boolean newValue) throws GamaRuntimeException,
				GamlException {
				turnGridOn = newValue;
				container.updateDisplay();
			}
		});
	}

	@Override
	protected void buildImage() {
		// GridDisplayLayer g = (GridDisplayLayer) model;
		// image = g.getSupportImage();
		// turnGridOn = g.drawLines();
		// if ( turnGridOn ) {
		// lineColor = ((GridDisplayLayer) model).getLineColor();
		// }
	}

	@Override
	public void privateDrawDisplay(final IGraphics dg) {
		if ( disposed ) { return; }
		GridDisplayLayer g = (GridDisplayLayer) model;
		image = g.getSupportImage();
		dg.drawImage(image, null);
		if ( g.drawLines() ) {
			lineColor = g.getLineColor();
			displayGrid(dg);
		}
	}

	private void displayGrid(final IGraphics dg) {
		double stepx = size.x / (double) image.getWidth();
		for ( int i = 1, end = image.getWidth(); i <= end; i++ ) {
			double step = i * stepx;
			dg.setDrawingCoordinates(step, 0);
			dg.drawLine(lineColor, step, size.y);
		}
		double stepy = size.y / (double) image.getHeight();
		for ( int i = 1, end = image.getHeight(); i <= end; i++ ) {
			double step = i * stepy;
			dg.setDrawingCoordinates(0, step);
			dg.drawLine(lineColor, size.x, step);
		}
	}

	private IAgent getPlaceAt(final GamaPoint loc) {
		return ((GridDisplayLayer) model).getEnvironment().getPlaceAt(loc).getAgent();
	}

	@Override
	public void putMenuItemsIn(final Menu inMenu, final int x, final int y) {
		super.putMenuItemsIn(inMenu, x, y);
		placeSelected = getPlaceAt(this.getModelCoordinatesFrom(x, y));
		if ( placeSelected != null ) {
			inMenu.addSeparator();
			MenuItem mi = new MenuItem(placeSelected.getName());
			inMenu.add(mi);
			mi.addActionListener(placeMenuListener);
		}
	}

	@Override
	public void initMenuItems(final IDisplaySurface surface) {
		placeMenuListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				surface.fireSelectionChanged(placeSelected);
				placeSelected = null;
			}

		};
	}

	@Override
	protected String getType() {
		return "Grid layer";
	}

}
