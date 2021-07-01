/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.GridLayerData.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import java.awt.Color;

import org.locationtech.jts.geom.Envelope;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.file.GamaImageFile;
import msi.gama.util.matrix.IField;
import msi.gaml.operators.Cast;
import msi.gaml.types.GamaFieldType;
import msi.gaml.types.Types;

public class MeshLayerData extends LayerData {

	static GamaColor defaultLineColor = GamaColor.getInt(Color.black.getRGB());
	boolean shouldComputeValues = true;
	IField values;
	Attribute<GamaColor> line;
	Attribute<GamaImageFile> texture;
	Attribute<Boolean> smooth;
	Attribute<IField> elevation;
	Attribute<Boolean> triangulation;
	Attribute<Boolean> grayscale;
	Attribute<Boolean> text;
	Attribute<Boolean> wireframe;
	Attribute<Double> noData;
	Attribute<Object> color;
	Attribute<Double> scale;
	private GamaPoint cellSize;
	private final GamaPoint dim = new GamaPoint();

	@SuppressWarnings ("unchecked")
	public MeshLayerData(final ILayerStatement def) throws GamaRuntimeException {
		super(def);
		size = create(IKeyword.SIZE, (scope, exp) -> {
			Object result = exp.value(scope);
			if (result instanceof Number)
				return new GamaPoint(1, 1, ((Number) result).doubleValue());
			else
				return Cast.asPoint(scope, result);
		}, Types.POINT, new GamaPoint(1, 1, 1), (e) -> {
			Object v = e.getConstValue();
			return v instanceof Number ? new GamaPoint(1, 1, ((Number) v).doubleValue()) : Cast.asPoint(null, v);
		});
		line = create(IKeyword.BORDER, Types.COLOR, null);
		elevation = create(IKeyword.SOURCE, (scope, exp) -> {
			if (exp != null) return buildValues(scope, exp.value(scope));
			return null;
		}, Types.NO_TYPE, (IField) null, null);
		triangulation = create(IKeyword.TRIANGULATION, Types.BOOL, false);
		smooth = create(IKeyword.SMOOTH, Types.BOOL, false);
		grayscale = create(IKeyword.GRAYSCALE, Types.BOOL, false);
		wireframe = create(IKeyword.WIREFRAME, Types.BOOL, false);
		text = create(IKeyword.TEXT, Types.BOOL, false);
		color = create(IKeyword.COLOR, Types.NO_TYPE, null);
		scale = create(IKeyword.SCALE, Types.FLOAT, null);
		noData = create("no_data", Types.FLOAT, null);
		texture = create(IKeyword.TEXTURE, (scope, exp) -> {
			final Object result = exp.value(scope);
			if (result instanceof GamaImageFile)
				return (GamaImageFile) exp.value(scope);
			else
				throw GamaRuntimeException.error("The texture of a field must be an image file", scope);
		}, Types.FILE, null, null);
	}

	@Override
	public void compute(final IScope scope, final IGraphics g) throws GamaRuntimeException {
		final Envelope env2 = scope.getSimulation().getEnvelope();
		final double width = env2.getWidth();
		final double height = env2.getHeight();
		super.compute(scope, g);
		shouldComputeValues = super.getRefresh();
		cellSize = new GamaPoint(width / dim.x, height / dim.y);
	}

	private IField buildValues(final IScope scope, final Object from) {
		if (values == null || shouldComputeValues) {
			values = GamaFieldType.buildField(scope, from);
			dim.setLocation(values.getCols(scope), values.getRows(scope), 0);
		}
		return values;
	}

	public Boolean isTriangulated() {
		return triangulation.get();
	}

	public Boolean isGrayScaled() {
		return grayscale.get();
	}

	public Boolean isWireframe() {
		return wireframe.get();
	}

	public Boolean isShowText() {
		return text.get();
	}

	public GamaImageFile textureFile() {
		return texture.get();
	}

	public GamaColor getLineColor() {
		return line.get() == null && wireframe.get() ? defaultLineColor : line.get();
	}

	public boolean drawLines() {
		return line.get() != null || wireframe.get();
	}

	public GamaPoint getCellSize() {
		return cellSize;
	}

	public GamaPoint getDimension() {
		return dim;
	}

	public IField getElevationMatrix(final IScope scope) {
		return elevation.get();
	}

	public Object getColor() {
		// Should be a bit more complex in the future when color scales / palettes are introduced
		return color.get();
	}

	public Boolean isSmooth() {
		return smooth.get();
	}

	public Double getScale() {
		return scale.get();
	}

	public Double getNoDataValue() {
		return noData.get();
	}

}
