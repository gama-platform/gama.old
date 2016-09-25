package ummisco.gama.opengl.vaoGenerator;

import java.awt.Font;
import java.awt.image.BufferedImage;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

import msi.gama.runtime.IScope;
import ummisco.gama.modernOpenGL.DrawingEntity;
import ummisco.gama.modernOpenGL.font.fontMeshCreator.FontTextureCache;
import ummisco.gama.modernOpenGL.font.fontMeshCreator.TextMeshData;
import ummisco.gama.opengl.Abstract3DRenderer;
import ummisco.gama.opengl.scene.AbstractObject;
import ummisco.gama.opengl.scene.GeometryObject;
import ummisco.gama.opengl.scene.ImageObject;
import ummisco.gama.opengl.scene.StringObject;

/*
 * This class takes as input a geometry and a drawing attribute and returns a structure
 * readable by OpenGL, composed with vertex array.
 */

public class DrawingEntityGenerator {

	private final Abstract3DRenderer renderer;
	private final FontTextureCache fontTextCache;

	public DrawingEntityGenerator(final Abstract3DRenderer renderer) {
		this.renderer = renderer;
		this.fontTextCache = new FontTextureCache();
	}

	public DrawingEntity[] GenerateDrawingEntities(final IScope scope, final AbstractObject object, final GL2 gl) {
		return GenerateDrawingEntities(scope, object, true, gl);
	}

	private String getFontName(final StringObject strObj) {
		final Font font = strObj.getFont();
		if (font != null) {
			return font.getName();
		} else
			return "Helvetica";
	}

	private String getStyle(final StringObject strObj) {
		final Font font = strObj.getFont();
		if (font != null) {
			return font.isBold() ? font.isItalic() ? " bold italic" : " bold" : font.isItalic() ? " italic" : "";
		} else
			return "";
	}

	private int getFontSize(final StringObject strObj) {
		final Font font = strObj.getFont();
		if (font != null) {
			return font.getSize();
		} else
			return 18;
	}

	public DrawingEntity[] GenerateDrawingEntities(final IScope scope, final AbstractObject object,
			final boolean computeTextureIds, final GL2 gl) {
		// if this function is called to create a simpleScene, we don't compute
		// the texture IDs (the only thing that interest us in this case is the
		// texture Path)
		DrawingEntity[] result = null;
		AbstractTransformer transformer = null;
		if (object instanceof StringObject) {
			final StringObject strObj = (StringObject) object;
			final Texture[] textures = new Texture[1];
			final String fontName = getFontName(strObj);
			final String style = getStyle(strObj);
			final int fontSize = getFontSize(strObj);
			textures[0] = fontTextCache.getFontTexture(fontName + style);
			final TextMeshData textMeshData = fontTextCache.getTextMeshData(fontName + style, strObj.string,
					(int) renderer.getGlobalYRatioBetweenPixelsAndModelUnits(), fontSize);
			final String[] texturePaths = new String[1];
			texturePaths[0] = fontName + style;
			final int[] textureIds = new int[1];
			textureIds[0] = textures[0].getTextureObject();
			transformer = new StringObjectTransformer(strObj, textureIds, texturePaths, textMeshData,
					renderer.data.isTriangulation());
		} else if (object instanceof GeometryObject) {
			final GeometryObject geomObj = (GeometryObject) object;

			final String[] texturePaths = geomObj.getTexturePaths(scope); // returns
																			// null
																			// if
																			// no
																			// texture
																			// for
																			// this
																			// entity
			final int[] textureIDs = texturePaths == null ? null : new int[texturePaths.length];
			if (computeTextureIds && texturePaths != null) {
				final Texture[] textures = geomObj.getTextures(gl, renderer);
				for (int i = 0; i < textures.length; i++) {
					textureIDs[i] = textures[i].getTextureObject();
				}
			}
			transformer = new GeometryObjectTransformer(geomObj, textureIDs, texturePaths,
					renderer.data.isTriangulation());
		} else if (object instanceof ImageObject) {
			final ImageObject imObj = (ImageObject) object;

			String[] texturePaths = null;
			int[][][] bufferedImageValue = null;
			final String texturePath = imObj.getImagePath(scope); // returns
																	// null if
																	// no
																	// texture
																	// for this
																	// entity
			if (texturePath != null) {
				texturePaths = new String[1];
				texturePaths[0] = texturePath;
			} else {
				// the image contains no path : it is just a buffered image
				final BufferedImage buffImg = imObj.getBufferedImage();
				if (buffImg != null) {
					final int widthNb = buffImg.getWidth();
					final int heightNb = buffImg.getHeight();
					bufferedImageValue = new int[widthNb][heightNb][4];
					for (int i = 0; i < widthNb; i++) {
						for (int j = 0; j < heightNb; j++) {
							final int clr = buffImg.getRGB(i, j);
							final int red = (clr & 0x00ff0000) >> 16;
							final int green = (clr & 0x0000ff00) >> 8;
							final int blue = clr & 0x000000ff;
							bufferedImageValue[i][j][0] = red;
							bufferedImageValue[i][j][1] = green;
							bufferedImageValue[i][j][2] = blue;
							bufferedImageValue[i][j][3] = 255;
						}
					}
				}
			}
			final int[] textureIDs = texturePath == null && imObj.getBufferedImage() == null ? null : new int[1];
			if (computeTextureIds && (texturePath != null || imObj.getBufferedImage() != null)) {
				final Texture[] textures = object.getTextures(gl, renderer);
				for (int i = 0; i < textures.length; i++) {
					textureIDs[i] = textures[i].getTextureObject();
				}
			}
			transformer = new ImageObjectTransformer(imObj, textureIDs, texturePaths, bufferedImageValue,
					renderer.data.isTriangulation());
		}
		result = transformer.getDrawingEntities();
		return result;
	}

}
