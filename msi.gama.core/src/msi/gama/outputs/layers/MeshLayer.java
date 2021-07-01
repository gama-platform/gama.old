/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.GridLayer.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import java.util.Arrays;

import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gama.util.file.GamaImageFile;
import msi.gama.util.matrix.IField;
import msi.gaml.statements.draw.MeshDrawingAttributes;

public class MeshLayer extends AbstractLayer {

	public MeshLayer(final ILayerStatement layer) {
		super(layer);
	}

	@Override
	protected ILayerData createData() {
		return new MeshLayerData(definition);
	}

	@Override
	public MeshLayerData getData() {
		return (MeshLayerData) super.getData();
	}

	@Override
	public void privateDraw(final IScope scope, final IGraphics dg) {
		GamaColor lineColor = null;
		final MeshLayerData data = getData();
		if (data.drawLines()) { lineColor = data.getLineColor(); }
		final IField values = data.getElevationMatrix(scope);
		// final double[] bis = Arrays.copyOf(values, values.length);
		// double min = Doubles.min(bis);
		// for (int i = 0; i < bis.length; ++i) {
		// bis[i] -= min;
		// }
		// double max = Doubles.max(bis) / 100d;
		// for (int i = 0; i < bis.length; ++i) {
		// bis[i] /= max;
		// }
		final GamaImageFile textureFile = data.textureFile();
		final MeshDrawingAttributes attributes = new MeshDrawingAttributes("", lineColor, false);
		attributes.setGrayscaled(data.isGrayScaled());
		attributes.setEmpty(data.isWireframe());
		if (textureFile != null) { attributes.setTextures(Arrays.asList(textureFile)); }
		// See later to directly interpret an image from the values
		// else if (image != null) {
		// final int[] imageData = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		// System.arraycopy(data.getGrid().getDisplayData(), 0, imageData, 0, imageData.length);
		// attributes.setTextures(Arrays.asList(image));
		// }
		attributes.setLocation(data.getPosition().toGamaPoint());
		attributes.setTriangulated(data.isTriangulated());
		attributes.setWithText(data.isShowText());
		attributes.setCellSize(data.getCellSize());
		attributes.setBorder(lineColor);
		attributes.setXYDimension(data.getDimension());
		attributes.setSize(Scaling3D.of(data.getSize()));
		attributes.setScale(data.getScale());
		attributes.setColors(data.getColor());
		attributes.setSmooth(data.isSmooth());
		attributes.setNoData(data.getNoDataValue());
		dg.drawField(values, attributes);

	}

	@Override
	public String getType() {
		return "Field layer";
	}

}
