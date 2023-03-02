/*******************************************************************************************************
 *
 * GamaIcons.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.resources;

import java.awt.RenderingHints;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.gvt.renderer.ImageRenderer;
import org.apache.batik.gvt.renderer.StaticRenderer;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.osgi.framework.Bundle;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import msi.gama.application.workbench.IIconProvider;
import msi.gaml.operators.Cast;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * Class GamaIcons.
 *
 * @author drogoul
 * @since 12 sept. 2013
 *
 */
public class GamaIcons implements IIconProvider {

	/**
	 * The Class CustomTranscoder.
	 */
	public static final class CustomTranscoder extends PNGTranscoder {
		@Override
		protected ImageRenderer createRenderer() {
			ImageRenderer renderer = new StaticRenderer();
			RenderingHints renderHints = renderer.getRenderingHints();
			renderHints.add(
					new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF));
			renderHints.add(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
			renderHints.add(new RenderingHints(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE));
			renderHints.add(
					new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC));
			renderHints.add(new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION,
					RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY));
			renderHints.add(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
			renderHints.add(
					new RenderingHints(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY));
			renderHints.add(new RenderingHints(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE));
			renderHints.add(new RenderingHints(RenderingHints.KEY_FRACTIONALMETRICS,
					RenderingHints.VALUE_FRACTIONALMETRICS_ON));
			renderer.setRenderingHints(renderHints);
			return renderer;
		}
	}

	/** The icon cache. */
	private static Cache<String, GamaIcon> ICON_CACHE = CacheBuilder.newBuilder().build();

	/** The Constant PLUGIN_ID. */
	public static final String PLUGIN_ID = "ummisco.gama.ui.shared";

	/** The instance. */
	static private GamaIcons instance = new GamaIcons();

	/**
	 * Gets the single instance of GamaIcons.
	 *
	 * @return single instance of GamaIcons
	 */
	public static GamaIcons getInstance() { return instance; }

	/** The Constant DEFAULT_PATH. */
	static public final String DEFAULT_PATH = "/icons/";

	/** The Constant DVG_PATH. */
	static public final String SVG_PATH = "/svg/";

	/** The Constant SIZER_PREFIX. */
	static final String SIZER_PREFIX = "sizer_";

	/** The Constant COLOR_PREFIX. */
	static final String COLOR_PREFIX = "color_";

	static {

		DEBUG.ON();
		// Turn it on to have simpler overlays
		// createColorOverlay(IGamaIcons.OVERLAY_OK, IGamaColors.OK);
		// createColorOverlay(IGamaIcons.OVERLAY_CLOSED, GamaColors.get(204, 204, 204));
		// createColorOverlay(IGamaIcons.OVERLAY_CLOUD, GamaColors.get(124, 195, 249));
		// createColorOverlay(IGamaIcons.OVERLAY_ERROR, IGamaColors.ERROR);
		// createColorOverlay(IGamaIcons.OVERLAY_WARNING, GamaColors.get(254, 199, 0));

	}

	/**
	 * Creates a sizer of a given color
	 *
	 * @param color
	 *            the color
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @return the gama icon
	 */
	public static GamaIcon createSizer(final Color color, final int width, final int height) {
		final String name = SIZER_PREFIX + width + "x" + height + color.hashCode();
		try {
			return ICON_CACHE.get(name, () -> {
				final Image sizerImage = new Image(WorkbenchHelper.getDisplay(), width, height);
				final GC gc = new GC(sizerImage);
				gc.setBackground(color);
				gc.fillRectangle(0, 0, width, height);
				gc.dispose();
				return new GamaIcon(name, sizerImage);
			});
		} catch (ExecutionException e) {
			return null;
		}

	}

	/**
	 * Creates the.
	 *
	 * @param s
	 *            the s
	 * @return the gama icon
	 */
	public static GamaIcon create(final String s) {
		try {
			return ICON_CACHE.get(s, () -> new GamaIcon(s));
		} catch (ExecutionException e) {
			return null;
		}
	}

	/**
	 * Creates the.
	 *
	 * @param code
	 *            the code
	 * @param stream
	 *            the stream
	 * @return the gama icon
	 */
	private static GamaIcon create(final String code, final InputStream stream) {
		try {
			return ICON_CACHE.get(code, () -> new GamaIcon(code, stream));
		} catch (ExecutionException e) {
			return null;
		}
	}

	/**
	 * Creates the color icon.
	 *
	 * @param s
	 *            the s
	 * @param gcolor
	 *            the gcolor
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @return the gama icon
	 */
	public static GamaIcon createColorIcon(final String s, final GamaUIColor gcolor, final int width,
			final int height) {
		final String name = COLOR_PREFIX + s;
		try {
			return ICON_CACHE.get(name, () -> {
				final Image image = new Image(WorkbenchHelper.getDisplay(), width, height);
				final GC gc = new GC(image);
				gc.setAntialias(SWT.ON);
				gc.setBackground(gcolor.color());
				gc.fillRectangle(0, 0, width, height);
				gc.dispose();
				return new GamaIcon(name, image);
			});
		} catch (ExecutionException e) {
			return null;
		}
	}

	/**
	 * Creates the color overlay 2.
	 *
	 * @param s
	 *            the s
	 * @param gcolor
	 *            the gcolor
	 * @return the gama icon
	 */
	public static GamaIcon createColorOverlay(final String s, final GamaUIColor gcolor) {
		try {
			return ICON_CACHE.get(s, () -> {
				// DEBUG.OUT("Building Icon " + s);
				int dim = 6;
				Image image = new Image(null, dim, dim);
				final GC gc = new GC(image);
				gc.setAntialias(SWT.ON);
				gc.setBackground(gcolor.color());
				gc.fillRectangle(0, 0, dim, dim);
				gc.setForeground(gcolor.lighter());
				gc.drawRectangle(0, 0, dim, dim);
				gc.dispose();
				return new GamaIcon(s, image);
			});
		} catch (ExecutionException e) {
			return null;
		}
	}

	/**
	 * Creates an icon that needs to be disposed afterwards
	 *
	 * @param gcolor
	 * @param width
	 * @param height
	 * @return
	 */
	public static Image createTempColorIcon(final GamaUIColor gcolor) {
		final String name = "color" + gcolor.getRGB().toString();
		try {
			return ICON_CACHE.get(name, () -> {
				int size = 16;
				ImageData imData = new ImageData(size, size, 24, new PaletteData(0xff0000, 0x00ff00, 0x0000ff));
				Image image = new Image(WorkbenchHelper.getDisplay(), imData);
				final GC gc = new GC(image);
				gc.setAntialias(SWT.ON);
				gc.setBackground(IGamaColors.GRAY.color());
				gc.fillRectangle(0, 0, size, size);
				gc.setBackground(IGamaColors.WHITE.color());
				gc.setForeground(IGamaColors.BLACK.color());
				gc.fillRoundRectangle(0, 0, size - 1, size - 1, 4, 4);
				gc.drawRoundRectangle(0, 0, size - 1, size - 1, 4, 4);
				gc.setBackground(gcolor.color());
				gc.fillRoundRectangle(size / 4, size / 4, size / 2, size / 2, 4, 4);
				// See Issue #3138 about weird artefacts in handmade icons.
				gc.dispose();
				ImageData imageData = image.getImageData();
				imageData.transparentPixel = imageData.palette.getPixel(IGamaColors.GRAY.getRGB());
				image = new Image(WorkbenchHelper.getDisplay(), imageData);
				return new GamaIcon(name, image);
			}).image();
		} catch (ExecutionException e) {
			return null;
		}

	}

	/**
	 * Creates the temp round color icon.
	 *
	 * @param gcolor
	 *            the gcolor
	 * @return the image
	 */
	public static Image createTempRoundColorIcon(final GamaUIColor gcolor) {
		final String name = "roundcolor" + gcolor.getRGB().toString();

		try {
			return ICON_CACHE.get(name, () -> {
				// The mask gray value needs to be > tolerance, < 255 - tolerance, < gcolor.red - tolerance, >
				// gcolor.red + tolerance
				int tolerance = 10;
				int gray = gcolor.getRGB().red > 127 ? 63 : 190;
				Color mask = GamaColors.get(gray, gray, gray).color();
				int size = 24;
				ImageData imData = new ImageData(size, size, 24, new PaletteData(0xff0000, 0x00ff00, 0x0000ff));
				Image image = new Image(WorkbenchHelper.getDisplay(), imData);
				final GC gc = new GC(image);
				gc.setAntialias(SWT.ON);
				gc.setBackground(mask);
				gc.fillRectangle(0, 0, size, size);
				gc.setBackground(IGamaColors.WHITE.color());
				gc.setForeground(IGamaColors.BLACK.color());
				gc.fillOval(0, 0, size - 1, size - 1);
				gc.drawOval(0, 0, size - 1, size - 1);
				gc.setBackground(gcolor.color());
				gc.fillOval(size / 4, size / 4, size / 2, size / 2);
				// See Issue #3138 about weird artefacts in handmade icons.
				gc.dispose();

				imData = image.getImageData();

				int grayPixelValue = imData.palette.getPixel(mask.getRGB());
				int[] lineData = new int[imData.width];
				for (int y = 0; y < imData.height; y++) {
					imData.getPixels(0, y, size, lineData, 0);
					// Analyze each pixel value in the line
					for (int x = 0; x < lineData.length; x++) {
						// Extract the red, green and blue component
						int p = lineData[x];
						int r = (p & 0xff0000) >> 16;
						int g = (p & 0x00ff00) >> 8;
						int b = (p & 0x0000ff) >> 0;
						if (r > gray - tolerance && r < gray + tolerance && g > gray - tolerance && g < gray + tolerance
								&& b > gray - tolerance && b < gray + tolerance) {
							imData.setPixel(x, y, grayPixelValue);
						}
					}
				}
				// mask.dispose();
				imData.transparentPixel = grayPixelValue;
				image = new Image(WorkbenchHelper.getDisplay(), imData);

				return new GamaIcon(name, image);
			}).image();
		} catch (ExecutionException e) {
			return null;
		}

	}

	@Override
	public ImageDescriptor desc(final String name) {
		final GamaIcon icon = create(name);
		return icon.descriptor();
	}

	/**
	 * Preload icons.
	 *
	 * @param bundle
	 *            the bundle
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static void preloadIcons() {
		// CompletableFuture.runAsync(() -> {
		Bundle bundle = Platform.getBundle(PLUGIN_ID);
		final URL fileURL = bundle.getEntry(GamaIcons.DEFAULT_PATH);
		URL resolvedFileURL;
		try {
			resolvedFileURL = FileLocator.toFileURL(fileURL);
		} catch (IOException e) {
			return;
		}
		// We need to use the 3-arg constructor of URI in order to properly escape file system chars
		URI resolvedURI;
		try {
			resolvedURI = new URI(resolvedFileURL.getProtocol(), resolvedFileURL.getPath(), null).normalize();
		} catch (URISyntaxException e) {
			return;
		}
		java.nio.file.Path path = new File(resolvedURI).toPath();
		List<String> files;
		try {
			files = Files.walk(path).map(f -> path.relativize(f).toString()).filter(n -> n.endsWith(".png")).toList();
		} catch (IOException e) {
			return;
		}
		DEBUG.TIMER_WITH_EXCEPTIONS(
				DEBUG.PAD("> GAMA: Preloading " + files.size() + " icons", 55, ' ') + DEBUG.PAD(" done in", 15, '_'),
				() -> files.forEach(n -> create(n.replace(".png", "")).image()));
		// });
	}

	/**
	 * Strip off px.
	 *
	 * @param dimensionString
	 *            the dimension string
	 * @return the string
	 */
	private static String stripOffPx(final String dimensionString) {
		if (dimensionString.endsWith("px")) return dimensionString.substring(0, dimensionString.length() - 2);
		return dimensionString;
	}

	/**
	 * Builds the icons.
	 */
	public static void buildIcons() {
		Bundle bundle = Platform.getBundle(PLUGIN_ID);
		final URL fileURL = bundle.getEntry(GamaIcons.SVG_PATH);
		URL resolvedFileURL;
		try {
			resolvedFileURL = FileLocator.toFileURL(fileURL);
		} catch (IOException e) {
			return;
		}
		// We need to use the 3-arg constructor of URI in order to properly escape file system chars
		URI resolvedURI;
		try {
			resolvedURI = new URI(resolvedFileURL.getProtocol(), resolvedFileURL.getPath(), null).normalize();
		} catch (URISyntaxException e) {
			return;
		}

		File entry = new File(resolvedURI);

		java.nio.file.Path path = entry.toPath();
		List<java.nio.file.Path> files;
		try {
			files = new ArrayList<>(Files.walk(path).filter(n -> n.toString().endsWith(".svg")).toList());
			files.sort(Path::compareTo);
		} catch (IOException e) {
			return;
		}
		PNGTranscoder pt = new CustomTranscoder();

		DEBUG.TIMER_WITH_EXCEPTIONS(
				DEBUG.PAD("> GAMA: Building " + files.size() + " icons", 55, ' ') + DEBUG.PAD(" done in", 15, '_'),
				() -> {
					for (java.nio.file.Path n : files) {

						try {
							String svgUriInputLocation = n.toUri().toURL().toString();
							String p = path.relativize(n).toString().replace(".svg", "");
							if (ICON_CACHE.getIfPresent(p) != null) { continue; }
							SVGDocument svg = null;
							try {
								String parser = XMLResourceDescriptor.getXMLParserClassName();
								SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
								svg = f.createSVGDocument(svgUriInputLocation);
							} catch (Exception e3) {
								DEBUG.OUT("Error parsing SVG icon : " + e3.getMessage());
								continue;
							}

							Element node = svg.getDocumentElement();
							String nativeWidthStr = node.getAttribute("width");
							String nativeHeightStr = node.getAttribute("height");
							String nativeViewBoxX = "0", nativeViewBoxY = "0";
							String viewBoxStr = node.getAttribute("viewBox");
							// DEBUG.OUT("Icon " + p + " Original Viewbox= " + viewBoxStr);
							String[] splitted = viewBoxStr.split(" ");
							if (splitted.length > 1) {
								nativeViewBoxX = splitted[0];
								nativeViewBoxY = splitted[1];
							}
							int nativeWidth = -1;
							int nativeHeight = -1;

							if (("".equals(nativeWidthStr) || "".equals(nativeHeightStr)) && splitted.length == 4) {
								nativeWidthStr = splitted[2];
								nativeHeightStr = splitted[3];
							}
							nativeWidthStr = stripOffPx(nativeWidthStr);
							nativeHeightStr = stripOffPx(nativeHeightStr);
							double floatWidth = Cast.asFloat(null, nativeWidthStr);
							double floatHeight = Cast.asFloat(null, nativeHeightStr);
							nativeWidth = (int) Math.round(floatWidth);
							nativeHeight = (int) Math.round(floatHeight);
							node.setAttribute("width", String.valueOf(nativeWidth));
							node.setAttribute("height", String.valueOf(nativeHeight));
							node.setAttribute("viewBox",
									nativeViewBoxX + " " + nativeViewBoxY + " " + nativeWidth + " " + nativeHeight);
							// DEBUG.OUT(" --- > Dimensions set to " + nativeWidth + "x" + nativeHeight + " from "
							// + nativeWidthStr + "x" + nativeHeightStr, false);
							// DEBUG.OUT(" -- New Viewbox= " + node.getAttribute("viewBox"));

							/// SCALE
							double outputScale = 1d;
							float outputWidth = (float) (nativeWidth * outputScale);
							float outputHeight = (float) (nativeHeight * outputScale);
							pt.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, outputWidth);
							pt.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, outputWidth);

							// Guesstimate the PNG size in memory, BAOS will enlarge if necessary.
							int outputInitSize = (int) (outputWidth * outputHeight * 4 + 1024);
							TranscoderInput input = new TranscoderInput(svg);
							input.setURI(svg.getDocumentURI());
							// Define OutputStream Location
							try (ByteArrayOutputStream iconOutput = new ByteArrayOutputStream(outputInitSize)) {
								TranscoderOutput transcoderOutput = new TranscoderOutput(iconOutput);
								pt.transcode(input, transcoderOutput);
								// Clean Up
								iconOutput.flush();
								InputStream is = new ByteArrayInputStream(iconOutput.toByteArray());
								create(p, is);
							}
						} catch (IOException | TranscoderException ex) {
							System.out.println("Exception Thrown: " + ex);
						}

					}
				});

	}

}
