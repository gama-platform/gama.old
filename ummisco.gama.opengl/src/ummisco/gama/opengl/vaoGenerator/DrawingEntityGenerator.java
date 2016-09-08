package ummisco.gama.opengl.vaoGenerator;

import java.awt.Font;
import java.awt.image.BufferedImage;

import com.jogamp.opengl.util.texture.Texture;

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
	
	private Abstract3DRenderer renderer;
	private FontTextureCache fontTextCache;
	
	public DrawingEntityGenerator(Abstract3DRenderer renderer) {
		this.renderer = renderer;
		this.fontTextCache = new FontTextureCache();
	}
	
	public DrawingEntity[] GenerateDrawingEntities(AbstractObject object) {
		return GenerateDrawingEntities(object, true);
	}
	
	private String getFontName(StringObject strObj) {
		Font font = strObj.getFont();
		if (font != null) {
			return font.getName();
		}
		else return "Helvetica";
	}
	
	private String getStyle(StringObject strObj) {
		Font font = strObj.getFont();
		if (font != null) {
			return (font.isBold()) ? (font.isItalic()) ? " bold italic" : " bold" : (font.isItalic()) ? " italic" : "";
		}
		else return "";
	}
	
	private int getFontSize(StringObject strObj) {
		Font font = strObj.getFont();
		if (font != null) {
			return font.getSize();
		}
		else return 18;
	}
	
	public DrawingEntity[] GenerateDrawingEntities(AbstractObject object, boolean computeTextureIds) {
		// if this function is called to create a simpleScene, we don't compute the texture IDs (the only thing that interest us in this case is the texture Path)
		DrawingEntity[] result = null;
		AbstractTransformer transformer = null;
		if (object instanceof StringObject) {
			StringObject strObj = (StringObject)object;
			Texture[] textures = new Texture[1];
			String fontName = getFontName(strObj);
			String style = getStyle(strObj);
			int fontSize = getFontSize(strObj);
			textures[0] = fontTextCache.getFontTexture(fontName + style);
			TextMeshData textMeshData = fontTextCache.getTextMeshData(fontName + style, strObj.string, (int)renderer.getGlobalYRatioBetweenPixelsAndModelUnits(), fontSize);
			String[] texturePaths = new String[1];
			texturePaths[0] = fontName + style;
			int[] textureIds = new int[1];
			textureIds[0] = textures[0].getTextureObject();
			transformer = new StringObjectTransformer(strObj,textureIds,texturePaths,textMeshData,renderer.data.isTriangulation());
		}
		else if (object instanceof GeometryObject) {
			GeometryObject geomObj = (GeometryObject)object;
			
			String[] texturePaths = geomObj.getTexturePaths(); // returns null if no texture for this entity
			int[] textureIDs = (texturePaths == null) ? null : new int[texturePaths.length];
			if (computeTextureIds && (texturePaths != null)) {
				Texture[] textures = geomObj.getTextures(renderer.getContext(), renderer);
				for (int i = 0 ; i < textures.length ; i++) {
					textureIDs[i] = textures[i].getTextureObject();
				}
			}
			transformer = new GeometryObjectTransformer(geomObj,textureIDs,texturePaths,renderer.data.isTriangulation());	
		}
		else if (object instanceof ImageObject) {
			ImageObject imObj = (ImageObject)object;
			
			String[] texturePaths = null;
			int[][][] bufferedImageValue = null;
			String texturePath = imObj.getImagePath(); // returns null if no texture for this entity
			if (texturePath != null) {
				texturePaths = new String[1];
				texturePaths[0] = texturePath;
			}
			else {
				// the image contains no path : it is just a buffered image
				BufferedImage buffImg = imObj.getBufferedImage();
				if (buffImg != null) {
					int widthNb = buffImg.getWidth();
					int heightNb = buffImg.getHeight();
					bufferedImageValue = new int[widthNb][heightNb][4];
					for (int i = 0 ; i < widthNb ; i++)
					{
						for (int j = 0 ; j < heightNb ; j++)
						{
							int clr = buffImg.getRGB(i,j);
							int  red   = (clr & 0x00ff0000) >> 16;
							int  green = (clr & 0x0000ff00) >> 8;
							int  blue  =  clr & 0x000000ff;
							bufferedImageValue[i][j][0] = red;
							bufferedImageValue[i][j][1] = green;
							bufferedImageValue[i][j][2] = blue;
							bufferedImageValue[i][j][3] = 255;
						}
					}
				}
			}
			int[] textureIDs = (texturePath == null && imObj.getBufferedImage() == null) ? null : new int[1];
			if (computeTextureIds && ((texturePath != null) || (imObj.getBufferedImage() != null))) {
				Texture[] textures = object.getTextures(renderer.getContext(), renderer);
				for (int i = 0 ; i < textures.length ; i++) {
					textureIDs[i] = textures[i].getTextureObject();
				}
			}
			transformer = new ImageObjectTransformer(imObj,textureIDs,texturePaths,bufferedImageValue,renderer.data.isTriangulation());	
		}
		result = transformer.getDrawingEntities();
		return result;
	}

}
