/*******************************************************************************************************
 *
 * GridLayerData.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import static msi.gama.runtime.exceptions.GamaRuntimeException.error;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Collection;

import org.locationtech.jts.geom.Envelope;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.ImageUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix;
import msi.gama.metamodel.topology.grid.IGrid;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.file.GamaImageFile;
import msi.gama.util.matrix.GamaFloatMatrix;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class GridLayerData.
 */
public class GridLayerData extends LayerData {

	/** The default line color. */
	static GamaColor defaultLineColor = GamaColor.getInt(Color.black.getRGB());

	/** The grid. */
	IGrid grid;

	/** The name. */
	final String name;

	/** The turn grid on. */
	Boolean turnGridOn;

	/** The should compute image. */
	private final boolean shouldComputeImage;

	/** The line. */
	Attribute<GamaColor> line;

	/** The texture. */
	Attribute<GamaImageFile> texture;

	/** The elevation. */
	Attribute<double[]> elevation;

	/** The smooth. */
	Attribute<Boolean> smooth;

	/** The triangulation. */
	Attribute<Boolean> triangulation;

	/** The grayscale. */
	Attribute<Boolean> grayscale;

	/** The text. */
	Attribute<Boolean> text;

	/** The cell size. */
	private GamaPoint cellSize;

	/** The wireframe. */
	Attribute<Boolean> wireframe;

	/** The image. */
	BufferedImage image;

	/** The dim. */
	private final GamaPoint dim = new GamaPoint();

	/**
	 * Instantiates a new grid layer data.
	 *
	 * @param def
	 *            the def
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@SuppressWarnings ("unchecked")
	public GridLayerData(final ILayerStatement def) throws GamaRuntimeException {
		super(def);
		shouldComputeImage = !def.hasFacet("hexagonal");
		name = def.getFacet(IKeyword.SPECIES).literalValue();
		line = create(IKeyword.BORDER, Types.COLOR, null);
		wireframe = create(IKeyword.WIREFRAME, Types.BOOL, false);
		turnGridOn = def.hasFacet(IKeyword.BORDER);
		elevation = create(IKeyword.ELEVATION, (scope, exp) -> {
			if (exp != null) {
				switch (exp.getGamlType().id()) {
					case IType.MATRIX:
						return GamaFloatMatrix.from(scope, Cast.asMatrix(scope, exp.value(scope))).getMatrix();
					case IType.FLOAT:
					case IType.INT:
						return grid.getGridValueOf(scope, exp);
					case IType.BOOL:
						if ((Boolean) exp.value(scope)) return grid.getGridValue();
						return null;
				}
			}
			return null;
		}, Types.NO_TYPE, (double[]) null, null);
		triangulation = create(IKeyword.TRIANGULATION, Types.BOOL, false);
		smooth = create(IKeyword.SMOOTH, Types.BOOL, false);
		grayscale = create(IKeyword.GRAYSCALE, Types.BOOL, false);
		text = create(IKeyword.TEXT, Types.BOOL, false);
		texture = create(IKeyword.TEXTURE, (scope, exp) -> {
			final Object result = exp.value(scope);
			if (result instanceof GamaImageFile) return (GamaImageFile) exp.value(scope);
			throw GamaRuntimeException.error("The texture of a grid must be an image file", scope);
		}, Types.FILE, null, null);
	}

	@Override
	public boolean compute(final IScope scope, final IGraphics g) throws GamaRuntimeException {
		if (grid == null) {
			final IPopulation<? extends IAgent> gridPop = scope.getAgent().getPopulationFor(name);
			if (gridPop == null) throw error("No grid species named " + name + " can be found", scope);
			if (!gridPop.isGrid()) throw error("Species named " + name + " is not a grid", scope);
			grid = (IGrid) gridPop.getTopology().getPlaces();
			// final Envelope env = grid.getEnvironmentFrame().getEnvelope();
			final Envelope env2 = scope.getSimulation().getEnvelope();
			final double width = env2.getWidth();
			final double height = env2.getHeight();
			// final double width2 = env2.getWidth();
			// final double height2 = env2.getHeight();
			final double cols = grid.getCols(scope);
			final double rows = grid.getRows(scope);
			cellSize = new GamaPoint(width / cols, height / rows);
			dim.setLocation(grid.getDimensions());
		}
		boolean result = super.compute(scope, g);
		if (shouldComputeImage) { computeImage(scope, g); }
		return result;
	}

	/**
	 * Checks if is triangulated.
	 *
	 * @return the boolean
	 */
	public Boolean isTriangulated() { return triangulation.get(); }

	/**
	 * Checks if is gray scaled.
	 *
	 * @return the boolean
	 */
	public Boolean isGrayScaled() { return grayscale.get(); }

	/**
	 * Checks if is show text.
	 *
	 * @return the boolean
	 */
	public Boolean isShowText() { return text.get(); }

	/**
	 * Texture file.
	 *
	 * @return the gama image file
	 */
	public GamaImageFile textureFile() {
		return texture.get();
	}

	/**
	 * Gets the line color.
	 *
	 * @return the line color
	 */
	public GamaColor getLineColor() { return line.get() == null ? defaultLineColor : line.get(); }

	/**
	 * Draw lines.
	 *
	 * @return true, if successful
	 */
	public boolean drawLines() {
		return line.get() != null && turnGridOn;
	}

	/**
	 * Sets the draw lines.
	 *
	 * @param newValue
	 *            the new draw lines
	 */
	public void setDrawLines(final Boolean newValue) { turnGridOn = newValue; }

	/**
	 * Gets the grid.
	 *
	 * @return the grid
	 */
	public IGrid getGrid() { return grid; }

	/**
	 * Gets the agents to display.
	 *
	 * @return the agents to display
	 */
	public Collection<IAgent> getAgentsToDisplay() { return grid.getAgents(); }

	/**
	 * Gets the cell size.
	 *
	 * @return the cell size
	 */
	public GamaPoint getCellSize() { return cellSize; }

	/**
	 * Gets the image.
	 *
	 * @return the image
	 */
	public BufferedImage getImage() { return image; }

	/**
	 * Checks if is wireframe.
	 *
	 * @return the boolean
	 */
	public Boolean isWireframe() { return wireframe.get(); }

	/**
	 * Sets the image.
	 *
	 * @param im
	 *            the new image
	 */
	public void setImage(final BufferedImage im) {
		if (image != null) { image.flush(); }
		image = im;
	}

	/**
	 * Compute image.
	 *
	 * @param scope
	 *            the scope
	 * @param g
	 *            the g
	 */
	protected void computeImage(final IScope scope, final IGraphics g) {
		if (image == null) {
			final GamaSpatialMatrix m = (GamaSpatialMatrix) grid;
			final GamaPoint p = m.getDimensions();
			image = ImageUtils.createCompatibleImage((int) p.getX(), (int) p.getY(), !g.is2D());
		}
	}

	/**
	 * Gets the elevation matrix.
	 *
	 * @param scope
	 *            the scope
	 * @return the elevation matrix
	 */
	public double[] getElevationMatrix(final IScope scope) {
		return elevation.get();
	}

	/**
	 * Gets the dimensions.
	 *
	 * @return the dimensions
	 */
	public GamaPoint getDimensions() { return dim; }

	/**
	 * Checks if is smooth.
	 *
	 * @return the boolean
	 */
	public Boolean isSmooth() { return smooth.get(); }

}
