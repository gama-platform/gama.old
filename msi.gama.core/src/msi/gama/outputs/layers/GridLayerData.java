package msi.gama.outputs.layers;

import static msi.gama.runtime.exceptions.GamaRuntimeException.error;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Collection;

import com.vividsolutions.jts.geom.Envelope;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.ImageUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
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

public class GridLayerData extends LayerData {

	static GamaColor defaultLineColor = GamaColor.getInt(Color.black.getRGB());

	IGrid grid;
	final String name;
	Boolean turnGridOn;
	private final boolean shouldComputeImage;
	Attribute<GamaColor> line;
	Attribute<GamaImageFile> texture;
	Attribute<double[]> elevation;
	Attribute<Boolean> triangulation;
	Attribute<Boolean> grayscale;
	Attribute<Boolean> text;
	private GamaPoint cellSize;
	BufferedImage image;

	@SuppressWarnings ("unchecked")
	public GridLayerData(final ILayerStatement def) throws GamaRuntimeException {
		super(def);
		shouldComputeImage = !def.hasFacet("hexagonal");
		name = def.getFacet(IKeyword.SPECIES).literalValue();
		line = create(IKeyword.LINES, Types.COLOR, null);
		turnGridOn = def.hasFacet(IKeyword.LINES);
		elevation = create(IKeyword.ELEVATION, (scope, exp) -> {
			if (exp != null) {
				switch (exp.getGamlType().id()) {
					case IType.MATRIX:
						return GamaFloatMatrix.from(scope, Cast.asMatrix(scope, exp.value(scope))).getMatrix();
					case IType.FLOAT:
					case IType.INT:
						return grid.getGridValueOf(scope, elevation);
					case IType.BOOL:
						if ((Boolean) elevation.value(scope)) {
							return grid.getGridValue();
						} else {
							return null;
						}
				}
			}
			return null;
		}, Types.NO_TYPE, (double[]) null);
		triangulation = create(IKeyword.TRIANGULATION, Types.BOOL, false);
		grayscale = create(IKeyword.GRAYSCALE, Types.BOOL, false);
		text = create(IKeyword.TEXT, Types.BOOL, false);
		texture = create(IKeyword.TEXTURE, (scope, exp) -> {
			final Object result = exp.value(scope);
			if (result instanceof GamaImageFile) {
				return (GamaImageFile) exp.value(scope);
			} else {
				throw GamaRuntimeException.error("The texture of grids must be an image file", scope);
			}
		}, Types.FILE, null);
	}

	@Override
	public void compute(final IScope scope, final IGraphics g) throws GamaRuntimeException {
		if (grid == null) {
			final IPopulation<? extends IAgent> gridPop = scope.getAgent().getPopulationFor(name);
			if (gridPop == null) {
				throw error("No grid species named " + name + " can be found", scope);
			} else if (!gridPop.isGrid()) { throw error("Species named " + name + " is not a grid", scope); }
			grid = (IGrid) gridPop.getTopology().getPlaces();
			final Envelope env = grid.getEnvironmentFrame().getEnvelope();
			cellSize = new GamaPoint(env.getWidth() / grid.getCols(scope), env.getHeight() / grid.getRows(scope));
		}
		super.compute(scope, g);
		if (shouldComputeImage) {
			computeImage(scope, g);
		}
	}

	public Boolean isTriangulated() {
		return triangulation.get();
	}

	public Boolean isGrayScaled() {
		return grayscale.get();
	}

	public Boolean isShowText() {
		return text.get();
	}

	public GamaImageFile textureFile() {
		return texture.get();
	}

	public GamaColor getLineColor() {
		return line.get() == null ? defaultLineColor : line.get();
	}

	public boolean drawLines() {
		return line.get() != null && turnGridOn;
	}

	public void setDrawLines(final Boolean newValue) {
		turnGridOn = newValue;
	}

	public IGrid getGrid() {
		return grid;
	}

	public Collection<IAgent> getAgentsToDisplay() {
		return grid.getAgents();
	}

	public GamaPoint getCellSize() {
		return cellSize;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(final BufferedImage im) {
		if (image != null) {
			image.flush();
		}
		image = im;
	}

	protected void computeImage(final IScope scope, final IGraphics g) {
		if (image == null) {
			final GamaSpatialMatrix m = (GamaSpatialMatrix) grid;
			final ILocation p = m.getDimensions();
			image = ImageUtils.createCompatibleImage((int) p.getX(), (int) p.getY(), !g.is2D());
		}
	}

	public double[] getElevationMatrix(final IScope scope) {
		return elevation.get();
	}

}
