/*******************************************************************************************************
 *
 * MeshLayer.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import java.util.Arrays;

import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.IImageProvider;
import msi.gama.runtime.IScope.IGraphicsScope;
import msi.gama.util.matrix.IField;
import msi.gaml.statements.draw.MeshDrawingAttributes;

/**
 * The Class MeshLayer.
 */
public class MeshLayer extends AbstractLayer {

	/**
	 * Instantiates a new mesh layer.
	 *
	 * @param layer
	 *            the layer
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

		final MeshLayerData data = getData();
		final IField values = data.getElevationMatrix(scope);
		final IImageProvider textureFile = data.textureFile();
		final MeshDrawingAttributes attributes = new MeshDrawingAttributes("", false);
		attributes.setGrayscaled(data.isGrayScaled());
		attributes.setEmpty(data.isWireframe());
		attributes.setBorder(data.drawLines() ? data.getLineColor() : null);
		if (textureFile != null) { attributes.setTextures(Arrays.asList(textureFile)); }
		attributes.setLocation(data.getPosition());
		attributes.setTriangulated(data.isTriangulated());
		attributes.setWithText(data.isShowText());
		attributes.setXYDimension(data.getDimension());
		attributes.setSize(Scaling3D.of(data.getSize()));
		attributes.setScale(data.getScale());
		attributes.setColors(data.getColor());
		attributes.setSmooth(data.getSmooth());
		attributes.setNoData(data.getNoDataValue());
		attributes.setAbove(data.getAbove());
		dg.drawField(values, attributes);

	}

	@Override
	public String getType() { return "Field layer"; }

}
