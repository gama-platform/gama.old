/*********************************************************************************************
 *
 * 'FieldObject.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.image.BufferedImage;
import java.util.List;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.statements.draw.FieldDrawingAttributes;

public class FieldObject extends AbstractObject {

	final double[] values;

	public FieldObject(final double[] dem, final FieldDrawingAttributes attributes) {
		super(attributes);
		this.values = dem;
	}

	public GamaPoint getCellSize() {
		return ((FieldDrawingAttributes) attributes).getCellSize();
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
		final FieldDrawingAttributes a = (FieldDrawingAttributes) attributes;
		final List<?> textures = a.getTextures();
		if (textures == null || textures.size() > order + 1) { return null; }
		final Object t = textures.get(order);
		if (t instanceof BufferedImage) { return (BufferedImage) t; }
		if (t instanceof GamaImageFile) { return ((GamaImageFile) t).getImage(null, true); }
		return null;
	}

	@Override
	public DrawerType getDrawerType() {
		return DrawerType.FIELD;
	}

}
