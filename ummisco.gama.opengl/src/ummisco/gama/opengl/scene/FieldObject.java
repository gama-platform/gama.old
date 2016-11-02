/*********************************************************************************************
 *
 * 'FieldObject.java, in plugin ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.image.BufferedImage;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.statements.draw.FieldDrawingAttributes;

public class FieldObject extends AbstractObject {

	final double[] values;

	// FIXME AD: This class has not been reworked correctly to work with the new API of SceneObjects. Basically, it has been made compatible, but not more.

	public FieldObject(final double[] dem, final FieldDrawingAttributes attributes, final LayerObject layer) {
		super(attributes, layer);
		this.values = dem;
	}

	public GamaPoint getCellSize() {
		return ((FieldDrawingAttributes) attributes).cellSize;
	}

	public double getZFactor() {
		return attributes.getDepth();
	}

	public boolean isGrayScaled() {
		return ((FieldDrawingAttributes) attributes).grayScaled;
	}

	public boolean isTriangulated() {
		return ((FieldDrawingAttributes) attributes).triangulated;
	}

	public boolean isShowText() {
		return ((FieldDrawingAttributes) attributes).withText;
	}

	public BufferedImage getDirectImage(final int order) {
		FieldDrawingAttributes a = (FieldDrawingAttributes) attributes;
		if ( a.textures == null || a.textures.size() > order + 1 ) { return null; }
		Object t = a.textures.get(order);
		if ( t instanceof BufferedImage ) { return (BufferedImage) t; }
		if ( t instanceof GamaImageFile ) { return ((GamaImageFile) t).getImage(null); }
		return null;
	}

}
