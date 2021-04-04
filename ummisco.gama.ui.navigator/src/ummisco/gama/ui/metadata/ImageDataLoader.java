/*********************************************************************************************
 *
 * 'ImageDataLoader.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.metadata;

import static javax.imageio.ImageIO.createImageInputStream;
import static javax.imageio.ImageIO.read;
import static msi.gama.common.util.ImageUtils.toCompatibleImage;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.map.GridCoverageLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.ColorMap;
import org.geotools.styling.ColorMapEntry;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.opengis.filter.FilterFactory2;

import it.geosolutions.imageioimpl.plugins.tiff.TIFFImageReaderSpi;
import msi.gama.common.util.ImageUtils;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Class ImageDataLoader.
 *
 * @author drogoul
 * @since 24 janv. 2015
 *
 */
public class ImageDataLoader {

	private final static TIFFImageReaderSpi READER_SPI = new TIFFImageReaderSpi();

	public static final int IMAGE_ASC = 8, IMAGE_PGM = 9;

	public static ImageData getImageData(final IFile file) {
		ImageData imageData = null;
		final String ext = file.getFileExtension();
		try {
			file.refreshLocal(IResource.DEPTH_ONE, null);
		} catch (final CoreException e1) {
			e1.printStackTrace();
		}
		if (ext != null) {
			try (InputStream in = file.getContents(true);) {
				if ("asc".equals(ext)) {
					imageData = readASC(in);
				} else if ("pgm".equals(ext)) {
					imageData = readPGM(in);
				} else if (ext.contains("tif")) {
					try {
						imageData = new ImageData(in);
						final PaletteData palette = imageData.palette;
						if (!((imageData.depth == 1 || imageData.depth == 2 || imageData.depth == 4
								|| imageData.depth == 8) && !palette.isDirect || imageData.depth == 8
								|| (imageData.depth == 16 || imageData.depth == 24 || imageData.depth == 32)
										&& palette.isDirect)) {
							imageData = null;
						}
						if (imageData == null) {
							final BufferedImage tif = ImageIO.read(in);
							imageData = convertToSWT(tif);
						}
						if (imageData == null) {
							try (ImageInputStream is =
									createImageInputStream(new File(file.getLocation().toFile().getAbsolutePath()))) {
								final ImageReader reader = READER_SPI.createReaderInstance();
								reader.setInput(is);
								final BufferedImage image = toCompatibleImage(reader.read(0));
								imageData = convertToSWT(image);
								image.flush();
								imageData.type = SWT.IMAGE_TIFF;
							} catch (final IOException e1) {
								e1.printStackTrace();
							}
						}
					} catch (Exception ex) {
						AbstractGridCoverage2DReader reader =
								new GeoTiffReader(file.getLocation().toFile().getAbsolutePath());// format.getReader(file,
																									// hints);
						GridCoverage2D grid = reader.read(null);
						reader.dispose();

						BufferedImage image = new BufferedImage(grid.getGridGeometry().getGridRange2D().width,
								grid.getGridGeometry().getGridRange2D().height, BufferedImage.TYPE_4BYTE_ABGR);

						MapContent mapContent = new MapContent();
						mapContent.getViewport().setCoordinateReferenceSystem(grid.getCoordinateReferenceSystem());
						Layer rasterLayer = new GridCoverageLayer(grid, createStyle(1, -0.4, 0.2));
						mapContent.addLayer(rasterLayer);
						GTRenderer draw = new StreamingRenderer();
						draw.setMapContent(mapContent);
						Graphics2D graphics = image.createGraphics();
						draw.paint(graphics, grid.getGridGeometry().getGridRange2D(), mapContent.getMaxBounds());
						imageData = convertToSWT(image);
						image.flush();
						mapContent.dispose();
						imageData.type = SWT.IMAGE_TIFF;
					}

					// AbstractGridFormat format = GridFormatFinder.findFormat(file);
					// Hints hints = null;
					// if (format instanceof GeoTiffFormat) {
					// hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
					// }

				} else {
					try {
						imageData = new ImageData(in);
					} catch (final SWTException e) {
						// Bad format. Can happen for PNG. See #2825
						if ("png".equals(ext) || "jpg".equals(ext)) {
							try {
								final BufferedImage image = toCompatibleImage(
										read(new File(file.getLocation().toFile().getAbsolutePath())));
								imageData = convertToSWT(image);
								image.flush();
								imageData.type = "png".equals(ext) ? SWT.IMAGE_PNG : SWT.IMAGE_JPEG;
							} catch (final IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				}
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
		}
		if (imageData == null) { DEBUG.ERR("null image data"); }
		return imageData;

	}

	private static Style createStyle(final int band, final double min, final double max) {

		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
		StyleFactory sf = CommonFactoryFinder.getStyleFactory();

		RasterSymbolizer sym = sf.getDefaultRasterSymbolizer();
		ColorMap cMap = sf.createColorMap();
		ColorMapEntry start = sf.createColorMapEntry();
		start.setColor(ff.literal("#ff0000"));
		start.setQuantity(ff.literal(min));
		ColorMapEntry end = sf.createColorMapEntry();
		end.setColor(ff.literal("#0000ff"));
		end.setQuantity(ff.literal(max));

		cMap.addColorMapEntry(start);
		cMap.addColorMapEntry(end);
		sym.setColorMap(cMap);
		Style style = SLD.wrapSymbolizers(sym);

		return style;
	}

	private static ImageData readPGM(final InputStream filename) {
		int[][] pixels;
		try (Scanner infile = new Scanner(filename);) {

			// process the top 4 header lines
			final String filetype = infile.nextLine();
			if (!filetype.equalsIgnoreCase("p2")) {
				DEBUG.ERR("Not a PGM");
				infile.close();
				return null;
			}
			// infile.nextLine();
			final int cols = infile.nextInt();
			final int rows = infile.nextInt();
			// skip reading maxValue
			infile.nextInt();
			int maxValue = Integer.MIN_VALUE;
			pixels = new int[rows][cols];
			// process the rest lines that hold the actual pixel values
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					final int n = infile.nextInt();
					if (n > maxValue) { maxValue = n; }
					pixels[r][c] = n * 255;
				}
			}
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					pixels[r][c] = (int) (pixels[r][c] / (double) maxValue);
				}
			}
		} catch (final Exception e) {
			DEBUG.ERR(e.toString());
			return null;
		}
		int g;
		final PaletteData palette = new PaletteData(16711680, 65280, 255);
		final ImageData data = new ImageData(pixels[0].length, pixels.length, 24, palette);
		for (int row = 0; row < pixels.length; ++row) {
			for (int col = 0; col < pixels[row].length; ++col) {
				g = pixels[row][col];
				data.setPixel(col, row, g << 16 | g << 8 | g);
			}
		}
		final ImageData result = data;
		result.type = IMAGE_PGM;
		return result;
	}

	private static ImageData readASC(final InputStream filename) {
		int[][] pixels;
		try (Scanner infile = new Scanner(filename);) {
			// Not a ASC file
			if (!infile.hasNext("ncols")) {
				DEBUG.ERR("Not an ASC");
				infile.close();
				return null;
			}

			infile.useLocale(Locale.US);
			int cols = 0;
			int rows = 0;
			double nodata = 0d;

			// process the top 6 or 5 header lines
			// ncols
			infile.next();
			cols = infile.nextInt();
			// nrows
			infile.next();
			rows = infile.nextInt();
			infile.nextLine();
			// xllcorner
			infile.nextLine();
			// yllcorner
			infile.nextLine();
			// cellsize
			String cellsize = infile.nextLine();
			if (cellsize.startsWith("dx") || cellsize.startsWith("dy")) { infile.nextLine(); }

			// NODATA_value
			if (infile.hasNext("NODATA_value")) {
				infile.next();
				nodata = infile.nextDouble();
			}
			int maxValue = 0;
			int minValue = Integer.MAX_VALUE;
			pixels = new int[rows][cols];
			// process the rest lines that hold the actual pixel values
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					double d = infile.nextDouble();
					if (d == nodata) { d = 0d; }
					if (d > maxValue) {
						maxValue = (int) d;
					} else if (d < minValue) { minValue = (int) d; }
					pixels[r][c] = (int) d;
				}
			}
			final int range = maxValue - minValue;
			double ratio = 1d;
			if (range < 255) {
				ratio = 255d / range;
			} else {
				ratio = range / 255d;
			}
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					pixels[r][c] = (int) ((pixels[r][c] - minValue) * ratio);
				}
			}
		} catch (final Exception e) {
			DEBUG.ERR(e.toString());
			return null;
		}
		int g;
		final PaletteData palette = new PaletteData(16711680, 65280, 255);
		final ImageData data = new ImageData(pixels[0].length, pixels.length, 24, palette);
		for (int row = 0; row < pixels.length; ++row) {
			for (int col = 0; col < pixels[row].length; ++col) {
				g = pixels[row][col];
				data.setPixel(col, row, g << 16 | g << 8 | g);
			}
		}

		final ImageData result = data;
		result.type = IMAGE_ASC;
		return result;
	}

	public static ImageData convertToSWT(final java.awt.image.BufferedImage image) {
		if (image == null) return null;
		if (image.getColorModel() instanceof java.awt.image.DirectColorModel) {
			final java.awt.image.DirectColorModel colorModel = (java.awt.image.DirectColorModel) image.getColorModel();
			final PaletteData palette =
					new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(), colorModel.getBlueMask());
			final ImageData data =
					new ImageData(image.getWidth(), image.getHeight(), colorModel.getPixelSize(), palette);
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					final int rgb = image.getRGB(x, y);
					final int pixel = palette.getPixel(new RGB(rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF));
					data.setPixel(x, y, pixel);
					if (colorModel.hasAlpha()) { data.setAlpha(x, y, rgb >> 24 & 0xFF); }
				}
			}
			return data;
		} else if (image.getColorModel() instanceof java.awt.image.IndexColorModel) {
			final java.awt.image.IndexColorModel colorModel = (java.awt.image.IndexColorModel) image.getColorModel();
			final int size = colorModel.getMapSize();
			final byte[] reds = new byte[size];
			final byte[] greens = new byte[size];
			final byte[] blues = new byte[size];
			colorModel.getReds(reds);
			colorModel.getGreens(greens);
			colorModel.getBlues(blues);
			final RGB[] rgbs = new RGB[size];
			for (int i = 0; i < rgbs.length; i++) {
				rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
			}
			final PaletteData palette = new PaletteData(rgbs);
			final ImageData data =
					new ImageData(image.getWidth(), image.getHeight(), colorModel.getPixelSize(), palette);
			data.transparentPixel = colorModel.getTransparentPixel();
			final java.awt.image.WritableRaster raster = image.getRaster();
			final int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					raster.getPixel(x, y, pixelArray);
					data.setPixel(x, y, pixelArray[0]);
				}
			}
			return data;
		} else if (image.getColorModel() instanceof java.awt.image.ComponentColorModel) {

			final java.awt.image.ComponentColorModel colorModel =
					(java.awt.image.ComponentColorModel) image.getColorModel();
			if (colorModel.getPixelSize() > 32) {
				final BufferedImage newImage = ImageUtils.toCompatibleImage(image);
				return convertToSWT(newImage);
			}
			// ASSUMES: 3 BYTE BGR IMAGE TYPE

			final PaletteData palette = new PaletteData(0x0000FF, 0x00FF00, 0xFF0000);
			final ImageData data =
					new ImageData(image.getWidth(), image.getHeight(), colorModel.getPixelSize(), palette);

			// This is valid because we are using a 3-byte Data model with no
			// transparent pixels
			data.transparentPixel = -1;

			final java.awt.image.WritableRaster raster = image.getRaster();
			final int[] pixelArray = colorModel.getComponentSize();
			if (pixelArray.length == 1) {
				int maxVal = Integer.MIN_VALUE;
				int minVal = Integer.MAX_VALUE;
				for (int y = 0; y < data.height; y++) {
					for (int x = 0; x < data.width; x++) {
						raster.getPixel(x, y, pixelArray);
						final int val = pixelArray[0];
						if (val > maxVal) { maxVal = val; }
						if (val < minVal) { minVal = val; }
					}
				}
				for (int y = 0; y < data.height; y++) {
					for (int x = 0; x < data.width; x++) {
						raster.getPixel(x, y, pixelArray);
						final int val =
								(int) (maxVal == minVal ? 0 : (pixelArray[0] - minVal) / (0.0 + maxVal - minVal) * 255);
						final int pixel = palette.getPixel(new RGB(val, val, val));
						data.setPixel(x, y, pixel);
					}
				}
			} else {
				for (int y = 0; y < data.height; y++) {
					for (int x = 0; x < data.width; x++) {
						raster.getPixel(x, y, pixelArray);
						final int pixel = palette.getPixel(new RGB(pixelArray[0], pixelArray[1], pixelArray[2]));
						data.setPixel(x, y, pixel);
					}
				}
			}

			return data;
		}
		return null;
	}

}
