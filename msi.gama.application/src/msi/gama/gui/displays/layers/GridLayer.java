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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.displays.layers;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.ImageUtils;
import msi.gama.gui.parameters.EditorFactory;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.grid.IGrid;
import msi.gama.outputs.layers.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.GamaImageFile;
import org.eclipse.swt.widgets.Composite;
import com.vividsolutions.jts.geom.Envelope;

public class GridLayer extends ImageLayer {

	@Override
	public Rectangle2D focusOn(final IShape geometry, final IDisplaySurface s) {
		final GridLayerStatement g = (GridLayerStatement) definition;
		IAgent a = geometry.getAgent();
		if ( a == null ) { return null; }
		if ( a.getSpecies() != g.getEnvironment().getCellSpecies() ) { return null; }
		Envelope env = a.getEnvelope();
		Point min = this.getScreenCoordinatesFrom(env.getMinX(), env.getMinY(), s);
		Point max = this.getScreenCoordinatesFrom(env.getMaxX(), env.getMaxY(), s);
		return new Rectangle2D.Double(min.x, min.y, max.x - min.x, max.y - min.y);
	}

	private boolean turnGridOn;
	private double cellSize;

	public GridLayer(final ILayerStatement layer) {
		super(layer);
		turnGridOn = ((GridLayerStatement) layer).drawLines();
	}

	@Override
	public void reloadOn(final IDisplaySurface surface) {
		super.reloadOn(surface);
		if ( image != null ) {
			image.flush();
			image = null;
		}
	}

	@Override
	public void fillComposite(final Composite compo, final IDisplaySurface container) {
		super.fillComposite(compo, container);
		EditorFactory.create(compo, "Draw grid:", turnGridOn, new EditorListener<Boolean>() {

			@Override
			public void valueModified(final Boolean newValue) throws GamaRuntimeException {
				turnGridOn = newValue;
				if ( isPaused(container) ) {
					container.forceUpdateDisplay();
				}
			}
		});
	}

	@Override
	protected void buildImage(final IScope scope) {
		if ( scope == null ) { return; }
		final GridLayerStatement g = (GridLayerStatement) definition;
		final IGrid m = g.getEnvironment();
		final ILocation p = m.getDimensions();
		// in case the agents have been killed
		if ( m.getAgents().size() > 0 ) {
			cellSize = m.getAgents().get(0).getGeometry().getEnvelope().getWidth();
		}

		if ( image == null ) {
			image = ImageUtils.createCompatibleImage(p.getX(), p.getY());
		}
		image.setRGB(0, 0, (int) p.getX(), (int) p.getY(), m.getDisplayData(), 0, (int) p.getX());
	}

	@Override
	public void privateDrawDisplay(final IScope scope, final IGraphics dg) {
		buildImage(scope);
		final GridLayerStatement g = (GridLayerStatement) definition;
		if ( image == null ) { return; }
		Color lineColor = null;
		if ( turnGridOn ) {
			lineColor = g.getLineColor();
			if ( lineColor == null ) {
				lineColor = Color.black;
			}
		}
		double[] gridValueMatrix = g.getElevationMatrix(scope);
		if ( gridValueMatrix != null ) {
			GamaImageFile textureFile = g.textureFile();
			if ( textureFile != null ) { // display grid dem:texturefile
				BufferedImage texture = textureFile.getImage(scope);
				dg.drawGrid(scope, texture, gridValueMatrix, true, g.isTriangulated(), g.isGrayScaled(),
					g.isShowText(), lineColor, cellSize, this.getName());
			} else {
				dg.drawGrid(scope, image, gridValueMatrix, g.isTextured(), g.isTriangulated(), g.isGrayScaled(),
					g.isShowText(), lineColor, cellSize, this.getName());
			}

		} else {
			dg.drawImage(scope, image, null, null, lineColor, null, true, this.getName());
		}
	}

	private IAgent getPlaceAt(final GamaPoint loc) {
		return ((GridLayerStatement) definition).getEnvironment().getAgentAt(loc);
	}

	@Override
	public Set<IAgent> collectAgentsAt(final int x, final int y, final IDisplaySurface g) {
		final Set<IAgent> result = new HashSet();
		result.add(getPlaceAt(this.getModelCoordinatesFrom(x, y, g)));
		return result;
	}

	@Override
	public String getType() {
		return "Grid layer";
	}

}
