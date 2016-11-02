/*********************************************************************************************
 *
 * 'FontTextureCache.java, in plugin ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.modernOpenGL.font.fontMeshCreator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class FontTextureCache {
	
	private HashMap<String,Texture> textureMap = new HashMap<String,Texture>();
	
	public TextMeshData getTextMeshData(String textureName, String content, double yRatioBetweenPixelsAndModelUnits, int textSize) {
		String absolutePathToFontTextureFolder = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath() 
				+ "res" + File.separator + "font" + File.separator;
		String fontFile = absolutePathToFontTextureFolder + textureName + ".fnt";
		FontType font = new FontType(fontFile);
		float scale = (10f / (float)yRatioBetweenPixelsAndModelUnits) * textSize;
		GUIText text = new GUIText(content, scale, font, -1, true);
		return font.loadText(text);
	}

	public Texture getFontTexture(String textureName) {
		if (textureMap.containsKey(textureName)) {
			return textureMap.get(textureName);
		}
		else {
			String absolutePathToFontTextureFolder = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath() 
					+ "res" + File.separator + "font" + File.separator;
			String fontImage = absolutePathToFontTextureFolder + textureName + ".png";
			Texture fontTexture = null;
			try {
				fontTexture = TextureIO.newTexture(new File(fontImage), false);
			} catch (GLException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			textureMap.put(textureName, fontTexture);
			return fontTexture;
		}
	}
}
