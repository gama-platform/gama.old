/**
 * Created by drogoul, 2 févr. 2016
 *
 */
package ummisco.gama.opengl.scene;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.texture.ImageType;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import com.jogamp.opengl.util.texture.spi.TextureProvider;
import com.jogamp.opengl.util.texture.spi.TextureProvider.SupportsImageTypes;

import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.util.file.GamaImageFile;

/**
 * Class PGMTextureProvider.
 *
 * @author drogoul
 * @since 2 févr. 2016
 *
 */
public class PGMTextureProvider implements TextureProvider, SupportsImageTypes {

	static ImageType[] types = new ImageType[] { new ImageType(ImageType.T_PGM) };

	/**
	 * Method getImageTypes()
	 * 
	 * @see com.jogamp.opengl.util.texture.spi.TextureProvider.SupportsImageTypes#getImageTypes()
	 */
	@Override
	public ImageType[] getImageTypes() {
		return null;
	}

	/**
	 * Method newTextureData()
	 * 
	 * @see com.jogamp.opengl.util.texture.spi.TextureProvider#newTextureData(com.jogamp.opengl.GLProfile,
	 *      java.io.File, int, int, boolean, java.lang.String)
	 */
	@Override
	public TextureData newTextureData(final GLProfile glp, final File file, final int internalFormat,
			final int pixelFormat, final boolean mipmap, final String fileSuffix) throws IOException {
		final IScope scope = GAMA.getRuntimeScope();
		final GamaImageFile f = new GamaImageFile(scope, file.getAbsolutePath());
		if (f.getExtension(scope).equals("pgm")) {
			final BufferedImage image = f.getImage(scope);
			return AWTTextureIO.newTextureData(glp, image, internalFormat, pixelFormat, mipmap);
		} else {
			return null;
		}
	}

	/**
	 * Method newTextureData()
	 * 
	 * @see com.jogamp.opengl.util.texture.spi.TextureProvider#newTextureData(com.jogamp.opengl.GLProfile,
	 *      java.io.InputStream, int, int, boolean, java.lang.String)
	 */
	@Override
	public TextureData newTextureData(final GLProfile glp, final InputStream stream, final int internalFormat,
			final int pixelFormat, final boolean mipmap, final String fileSuffix) throws IOException {
		return null;
	}

	/**
	 * Method newTextureData()
	 * 
	 * @see com.jogamp.opengl.util.texture.spi.TextureProvider#newTextureData(com.jogamp.opengl.GLProfile,
	 *      java.net.URL, int, int, boolean, java.lang.String)
	 */
	@Override
	public TextureData newTextureData(final GLProfile glp, final URL url, final int internalFormat,
			final int pixelFormat, final boolean mipmap, final String fileSuffix) throws IOException {
		return null;
	}

}
