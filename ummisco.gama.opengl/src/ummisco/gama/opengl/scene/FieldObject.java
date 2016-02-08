/*********************************************************************************************
 *
 *
 * 'DEMObject.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
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

	// final public BufferedImage textureImage;
	// final public BufferedImage demImg;
	// final public IAgent agent;
	// final public boolean isTriangulated, isShowText, fromImage, isGrayScaled;
	// The height of the envelope represents the z_factor (between 0 and 1).
	// final public Envelope3D envelope;
	// final public Envelope3D cellSize;

	// FIXME AD: This class has not been reworked correctly to work with the new API of SceneObjects. Basically, it has been made compatible, but not more.

	public FieldObject(final double[] dem, final FieldDrawingAttributes attributes, final LayerObject layer) {
		super(attributes, layer);
		// attributes.agent = agent;
		// attributes.speciesName = name;
		// attributes.empty = demTexture == null;
		this.values = dem;
		// this.textureImage = demTexture;
		// this.demImg = demImg;
		// if ( demImg != null ) {
		// ImageUtil.flipImageVertically(this.demImg);
		// }
		// this.isTriangulated = isTriangulated;
		// this.isGrayScaled = isGrayScaled;
		// this.isShowText = isShowText;
		// this.fromImage = fromImage;
		// this.envelope = env;
		// this.cellSize = cellSize;
	}

	/**
	 * @return
	 */
	public GamaPoint getCellSize() {
		return ((FieldDrawingAttributes) attributes).cellSize;
	}

	/**
	 * @return
	 */
	public double getZFactor() {
		return attributes.getDepth();
	}

	/**
	 * @return
	 */
	public boolean isGrayScaled() {
		return ((FieldDrawingAttributes) attributes).grayScaled;
	}

	/**
	 * @return
	 */
	public boolean isTriangulated() {
		return ((FieldDrawingAttributes) attributes).triangulated;
	}

	/**
	 * @return
	 */
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

	//
	// @Override
	// public Texture getTexture(final GL gl, final JOGLRenderer renderer, final int order) {
	// if ( textureImage == null ) { return null; }
	// return renderer.getCurrentScene().getTexture(gl, textureImage);
	// }

}
