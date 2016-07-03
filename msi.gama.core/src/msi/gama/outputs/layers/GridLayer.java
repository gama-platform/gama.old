/*********************************************************************************************
 *
 *
 * 'GridLayer.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.outputs.layers;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import com.vividsolutions.jts.geom.Envelope;

import gnu.trove.set.hash.THashSet;
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.util.ImageUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.statements.draw.FieldDrawingAttributes;

// FIXME This class nees to be entirely rewritten ...

public class GridLayer extends ImageLayer {

	static GamaColor defaultLineColor = GamaColor.getInt(Color.black.getRGB());

	public boolean turnGridOn;
	private final GamaPoint cellSize;
	BufferedImage image;

	@Override
	public Rectangle2D focusOn(final IShape geometry, final IDisplaySurface s) {
		final GridLayerStatement g = (GridLayerStatement) definition;
		final IAgent a = geometry.getAgent();
		if (a == null || a.getSpecies() != g.getEnvironment().getCellSpecies()) {
			return null;
		}
		return super.focusOn(a, s);
	}

	public GridLayer(final IScope scope, final ILayerStatement layer) {
		super(scope, layer);
		turnGridOn = ((GridLayerStatement) layer).drawLines();
		final GamaSpatialMatrix m = (GamaSpatialMatrix) ((GridLayerStatement) layer).getEnvironment();
		final ILocation p = m.getDimensions();
		final Envelope env = scope.getRoot().getGeometry().getEnvelope();
		cellSize = new GamaPoint(env.getWidth() / m.numCols, env.getHeight() / m.numRows);
	}

	@Override
	public void reloadOn(final IDisplaySurface surface) {
		super.reloadOn(surface);
		if (image != null)
			image.flush();
	}

	@Override
	protected void buildImage(final IScope scope) {
		final IGraphics g = scope.getGraphics();
		if (g == null)
			return;
		if (image == null) {
			final GamaSpatialMatrix m = (GamaSpatialMatrix) ((GridLayerStatement) definition).getEnvironment();
			final ILocation p = m.getDimensions();
			if (g.is2D()) {
				image = ImageUtils.createCompatibleImage((int) p.getX(), (int) p.getY());
			} else {
				image = ImageUtils.createPremultipliedBlankImage((int) p.getX(), (int) p.getY());
			}
		}

	}

	@Override
	public void privateDrawDisplay(final IScope scope, final IGraphics dg) {
		if (dg == null || dg.cannotDraw())
			return;
		buildImage(scope);
		final GridLayerStatement g = (GridLayerStatement) definition;
		GamaColor lineColor = null;
		if (turnGridOn) {
			lineColor = g.getLineColor();
			if (lineColor == null) {
				lineColor = defaultLineColor;
			}
		}
		final double[] gridValueMatrix = g.getElevationMatrix(scope);
		final GamaImageFile textureFile = g.textureFile();
		final FieldDrawingAttributes attributes = new FieldDrawingAttributes(getName(), lineColor);
		attributes.grayScaled = g.isGrayScaled;
		attributes.fieldSize = g.getEnvironment().getDimensions();
		attributes.depth = 1.0;
		if (textureFile != null) {
			attributes.textures = Arrays.asList(textureFile);
		} else if (image != null) {

			final int[] data = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
			System.arraycopy(g.getEnvironment().getDisplayData(), 0, data, 0, data.length);
			attributes.textures = Arrays.asList(image);
		}
		attributes.triangulated = g.isTriangulated;
		attributes.withText = g.showText;
		attributes.cellSize = cellSize;
		attributes.border = lineColor;

		if (gridValueMatrix == null) {
			dg.drawImage(image, attributes);
		} else {
			dg.drawField(gridValueMatrix, attributes);
		}
	}

	private IAgent getPlaceAt(final ILocation loc) {
		return ((GridLayerStatement) definition).getEnvironment().getAgentAt(loc);
	}

	@Override
	public Set<IAgent> collectAgentsAt(final int x, final int y, final IDisplaySurface g) {
		final Set<IAgent> result = new THashSet();
		result.add(getPlaceAt(this.getModelCoordinatesFrom(x, y, g)));
		return result;
	}

	@Override
	public String getType() {
		return "Grid layer";
	}

	@Override
	public Collection<IAgent> getAgentsForMenu(final IScope scope) {
		return ((GridLayerStatement) definition).getAgentsToDisplay();
	}

	/**
	 * @param newValue
	 */
	public void setDrawLines(final Boolean newValue) {
		turnGridOn = newValue;
	}

}
