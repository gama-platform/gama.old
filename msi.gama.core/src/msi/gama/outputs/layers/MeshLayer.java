/*******************************************************************************************************
 *
 * MeshLayer.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import java.util.Arrays;

import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.runtime.IScope.IGraphicsScope;
import msi.gama.util.GamaColor;
import msi.gama.util.file.GamaImageFile;
import msi.gama.util.matrix.IField;
import msi.gaml.statements.draw.MeshDrawingAttributes;

/**
 * The Class MeshLayer.
 */
public class MeshLayer extends AbstractLayer {

	/**
	 * Instantiates a new mesh layer.
	 *
	 * @param layer the layer
	 */
	public MeshLayer(final ILayerStatement layer) {
		super(layer);
	}

	@Override
	protected ILayerData createData() {
		return new MeshLayerData(definition);
	}

	@Override
	public MeshLayerData getData() { return (MeshLayerData) super.getData(); }

	@Override
	public void privateDraw(final IGraphicsScope scope, final IGraphics dg) {
		GamaColor lineColor = null;
		final MeshLayerData data = getData();
		if (data.drawLines()) { lineColor = data.getLineColor(); }
		final IField values = data.getElevationMatrix(scope);
		final GamaImageFile textureFile = data.textureFile();
		final MeshDrawingAttributes attributes = new MeshDrawingAttributes("", lineColor, false);
		attributes.setGrayscaled(data.isGrayScaled());
		attributes.setEmpty(data.isWireframe());
		if (textureFile != null) { attributes.setTextures(Arrays.asList(textureFile)); }
		attributes.setLocation(data.getPosition());
		attributes.setTriangulated(data.isTriangulated());
		attributes.setWithText(data.isShowText());
		attributes.setCellSize(data.getCellSize());
		attributes.setBorder(lineColor);
		attributes.setXYDimension(data.getDimension());
		attributes.setSize(Scaling3D.of(data.getSize()));
		attributes.setScale(data.getScale());
		attributes.setColors(data.getColor());
		attributes.setSmooth(data.getSmooth());
		attributes.setNoData(data.getNoDataValue());
		dg.drawField(values, attributes);

	}

	@Override
	public String getType() { return "Field layer"; }

}
