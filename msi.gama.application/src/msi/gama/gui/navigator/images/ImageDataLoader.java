/**
 * Created by drogoul, 24 janv. 2015
 *
 */
package msi.gama.gui.navigator.images;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.*;
import org.eclipse.core.resources.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import com.sun.media.imageioimpl.plugins.tiff.TIFFImageReader;
import it.geosolutions.imageioimpl.plugins.tiff.TIFFImageReaderSpi;
import msi.gama.common.util.ImageUtils;

/**
 * Class ImageDataLoader.
 *
 * @author drogoul
 * @since 24 janv. 2015
 *
 */
public class ImageDataLoader {

	// When the file is not known... The input stream must be kept open
	public static ImageData getImageData(final InputStream stream) {
		ImageData imageData = null;
		stream.mark(2000);
		Scanner scanner = new Scanner(stream);
		try {
			if ( scanner.hasNext("p2") || scanner.hasNext("P2") ) {
				stream.reset();
				return readPGM(stream);
			}
			if ( scanner.hasNext("ncols") ) {
				stream.reset();
				return readASC(stream);
			}

			stream.reset();
			imageData = new ImageData(stream);

		} catch (Exception e) {
			System.out.println(e);
			return null;
		} finally {
			// scanner.close();
		}
		return imageData;
	}

	public static ImageData getImageData(final IFile file) {
		InputStream in = null;
		ImageInputStream is = null;
		ImageData imageData = null;
		String ext = file.getFileExtension();
		try {
			file.refreshLocal(IResource.DEPTH_ONE, null);
			in = file.getContents(true);
			if ( "asc".equals(ext) ) {
				imageData = readASC(in);
			} else if ( "pgm".equals(ext) ) {
				imageData = readPGM(in);
			} else {
				imageData = new ImageData(in);
			}
		} catch (Exception ex) {

			// System.out.println("Error in loading " + file.getLocation().toString());
			if ( ext.contains("tif") ) {
				TIFFImageReader reader;
				ImageReaderSpi spi = new TIFFImageReaderSpi();
				reader = new TIFFImageReader(spi);
				try {
					is = new FileImageInputStream(new File(file.getLocation().toFile().getAbsolutePath()));
				} catch (IOException e) {
					e.printStackTrace();
				}
				if ( is != null ) {
					reader.setInput(is);
					BufferedImage image;
					try {
						image = reader.read(0);
						imageData = ImageDataLoader.convertToSWT(image);
						image.flush();
						imageData.type = SWT.IMAGE_TIFF;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}


			}

		} finally {
			if ( in != null ) {
				try {
					in.close();
				} catch (IOException e) {}
			}
			if ( is != null ) {
				try {
					is.close();
				} catch (IOException e) {}
			}
		}
		if ( imageData == null ) {
			System.out.println("null image data");
		}
		return imageData;

	}

	private static ImageData readPGM(final InputStream filename) {
		int[][] pixels;
		Scanner infile = null;

		try {
			infile = new Scanner(filename);
			// process the top 4 header lines
			String filetype = infile.nextLine();
			if ( !filetype.equalsIgnoreCase("p2") ) {
				System.out.println("Not a PGM");
				infile.close();
				return null;
			}
			// infile.nextLine();
			int cols = infile.nextInt();
			int rows = infile.nextInt();
			// skip reading maxValue
			infile.nextInt();
			int maxValue = Integer.MIN_VALUE;
			pixels = new int[rows][cols];
			// process the rest lines that hold the actual pixel values
			for ( int r = 0; r < rows; r++ ) {
				for ( int c = 0; c < cols; c++ ) {
					int n = infile.nextInt();
					if ( n > maxValue ) {
						maxValue = n;
					}
					pixels[r][c] = n * 255;
				}
			}
			for ( int r = 0; r < rows; r++ ) {
				for ( int c = 0; c < cols; c++ ) {
					pixels[r][c] = (int) (pixels[r][c] / (double) maxValue);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
			if ( infile != null ) {
				infile.close();
			}
			return null;
		} finally {
			if ( infile != null ) {
				infile.close();
			}
		}
		int g;
		PaletteData palette = new PaletteData(16711680, 65280, 255);
		ImageData data = new ImageData(pixels[0].length, pixels.length, 24, palette);
		for ( int row = 0; row < pixels.length; ++row ) {
			for ( int col = 0; col < pixels[row].length; ++col ) {
				g = pixels[row][col];
				data.setPixel(col, row, g << 16 | g << 8 | g);
			}
		}
		ImageData result = data;
		result.type = ImagePropertyPage.IMAGE_PGM;
		return result;
	}

	private static ImageData readASC(final InputStream filename) {
		int[][] pixels;
		Scanner infile = null;
		try {
			infile = new Scanner(filename);
			// Not a ASC file
			if ( !infile.hasNext("ncols") ) {
				System.out.println("Not an ASC");
				infile.close();
				return null;
			}

			infile.useLocale(Locale.US);
			int cols = 0;
			int rows = 0;
			double nodata = 0d;

			// process the top 6 or 5 header lines
			// ncols
			String s = infile.next();
			cols = infile.nextInt();
			// nrows
			s = infile.next();
			rows = infile.nextInt();
			s = infile.nextLine();
			// xllcorner
			s = infile.nextLine();
			// yllcorner
			s = infile.nextLine();
			// cellsize
			s = infile.nextLine();
			// NODATA_value
			if ( infile.hasNext("NODATA_value") ) {
				s = infile.next();
				nodata = infile.nextDouble();
			}
			int maxValue = 0;
			int minValue = Integer.MAX_VALUE;
			pixels = new int[rows][cols];
			// process the rest lines that hold the actual pixel values
			for ( int r = 0; r < rows; r++ ) {
				for ( int c = 0; c < cols; c++ ) {
					double d = infile.nextDouble();
					if ( d == nodata ) {
						d = 0d;
					}
					if ( d > maxValue ) {
						maxValue = (int) d;
					} else if ( d < minValue ) {
						minValue = (int) d;
					}
					pixels[r][c] = (int) d;
				}
			}
			int range = maxValue - minValue;
			double ratio = 1d;
			if ( range < 255 ) {
				ratio = 255d / range;
			} else {
				ratio = range / 255d;
			}
			for ( int r = 0; r < rows; r++ ) {
				for ( int c = 0; c < cols; c++ ) {
					pixels[r][c] = (int) ((pixels[r][c] - minValue) * ratio);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
			if ( infile != null ) {
				infile.close();
			}
			return null;
		}
		int g;
		PaletteData palette = new PaletteData(16711680, 65280, 255);
		ImageData data = new ImageData(pixels[0].length, pixels.length, 24, palette);
		for ( int row = 0; row < pixels.length; ++row ) {
			for ( int col = 0; col < pixels[row].length; ++col ) {
				g = pixels[row][col];
				data.setPixel(col, row, g << 16 | g << 8 | g);
			}
		}

		ImageData result = data;
		result.type = ImagePropertyPage.IMAGE_ASC;
		infile.close();
		return result;
	}

	public static ImageData convertToSWT(final java.awt.image.BufferedImage image) {
		if ( image.getColorModel() instanceof java.awt.image.DirectColorModel ) {
			java.awt.image.DirectColorModel colorModel = (java.awt.image.DirectColorModel) image.getColorModel();
			PaletteData palette =
				new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(), colorModel.getBlueMask());
			ImageData data =
				new ImageData(image.getWidth(), image.getHeight(), colorModel.getPixelSize(), palette);
			for ( int y = 0; y < data.height; y++ ) {
				for ( int x = 0; x < data.width; x++ ) {
					int rgb = image.getRGB(x, y);
					int pixel = palette.getPixel(new RGB(rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF));
					data.setPixel(x, y, pixel);
					if ( colorModel.hasAlpha() ) {
						data.setAlpha(x, y, rgb >> 24 & 0xFF);
					}
				}
			}
			return data;
		} else if ( image.getColorModel() instanceof java.awt.image.IndexColorModel ) {
			java.awt.image.IndexColorModel colorModel = (java.awt.image.IndexColorModel) image.getColorModel();
			int size = colorModel.getMapSize();
			byte[] reds = new byte[size];
			byte[] greens = new byte[size];
			byte[] blues = new byte[size];
			colorModel.getReds(reds);
			colorModel.getGreens(greens);
			colorModel.getBlues(blues);
			RGB[] rgbs = new RGB[size];
			for ( int i = 0; i < rgbs.length; i++ ) {
				rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
			}
			PaletteData palette = new PaletteData(rgbs);
			ImageData data =
				new ImageData(image.getWidth(), image.getHeight(), colorModel.getPixelSize(), palette);
			data.transparentPixel = colorModel.getTransparentPixel();
			java.awt.image.WritableRaster raster = image.getRaster();
			int[] pixelArray = new int[1];
			for ( int y = 0; y < data.height; y++ ) {
				for ( int x = 0; x < data.width; x++ ) {
					raster.getPixel(x, y, pixelArray);
					data.setPixel(x, y, pixelArray[0]);
				}
			}
			return data;
		} else if (image.getColorModel() instanceof java.awt.image.ComponentColorModel) {

			java.awt.image. ComponentColorModel colorModel = (java.awt.image.ComponentColorModel)image.getColorModel();
			if ( colorModel.getPixelSize() > 32 ) {
				BufferedImage newImage = ImageUtils.toCompatibleImage(image);
				return convertToSWT(newImage);
			}
			//ASSUMES: 3 BYTE BGR IMAGE TYPE

			PaletteData palette = new PaletteData(0x0000FF, 0x00FF00,0xFF0000);
			ImageData data = new ImageData(image.getWidth(), image.getHeight(), colorModel.getPixelSize(), palette);

			//This is valid because we are using a 3-byte Data model with no transparent pixels
			data.transparentPixel = -1;

			java.awt.image.WritableRaster raster = image.getRaster();
			int[] pixelArray = colorModel.getComponentSize();
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					raster.getPixel(x, y, pixelArray);
					int pixel = palette.getPixel(new RGB(pixelArray[0], pixelArray[1], pixelArray[2]));
					data.setPixel(x, y, pixel);
				}
			}
			return data;
		}
		return null;
	}

}
