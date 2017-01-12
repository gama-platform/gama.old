/*********************************************************************************************
 *
 * 'ImageObject.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.image.BufferedImage;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.statements.draw.DrawingAttributes;
import msi.gaml.types.GamaGeometryType;
import ummisco.gama.opengl.Abstract3DRenderer;

public class ImageObject extends GeometryObject {

	final private GamaImageFile file;
	final private BufferedImage image;

	public ImageObject(final GamaImageFile file, final DrawingAttributes attributes, final LayerObject layer) {
		super(attributes, layer, new Texture[1]);
		this.file = file;
		this.image = null;
	}

	public ImageObject(final BufferedImage image, final DrawingAttributes attributes, final LayerObject layer) {
		super(attributes, layer, new Texture[1]);
		this.image = image;
		this.file = null;
	}

	@Override
	public Texture getTexture(final GL gl, final Abstract3DRenderer renderer, final int order) {
		Texture texture = null;
		if (image == null) {
			texture = renderer.getCurrentScene().getTexture(gl, file);
		} else {
			texture = renderer.getCurrentScene().getTexture(gl, image);
		}
		if (getDimensions() == null) {
			attributes.setSize(new GamaPoint(renderer.data.getEnvWidth(), renderer.data.getEnvHeight()));
		}
		return texture;
	}

	public String getImagePath(final IScope scope) {
		if (image == null) {
			return file.getPath(scope);
		} else {
			return null;
		}
	}

	public BufferedImage getBufferedImage() {
		return image;
	}

	@Override
	public boolean isTextured() {
		return true;
	}

	@Override
	public boolean isFilled() {
		return true;
	}

	@Override
	public void preload(final GL2 gl, final Abstract3DRenderer renderer) {
		super.preload(gl, renderer);
		if (getDimensions() == null) {
			attributes.setSize(new GamaPoint(renderer.data.getEnvWidth(), renderer.data.getEnvHeight()));
			if (getLocation() == null)
				attributes.setLocation(attributes.getSize().dividedBy(2));
		}
		geometry = GamaGeometryType
				.buildRectangle(attributes.getSize().x, attributes.getSize().y, attributes.getLocation())
				.getInnerGeometry();

	}

}
