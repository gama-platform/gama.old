/*******************************************************************************************************
 *
 * GamaIconsLoader.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package ummisco.gama.ui.resources;

import static java.awt.RenderingHints.KEY_ALPHA_INTERPOLATION;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_COLOR_RENDERING;
import static java.awt.RenderingHints.KEY_DITHERING;
import static java.awt.RenderingHints.KEY_FRACTIONALMETRICS;
import static java.awt.RenderingHints.KEY_INTERPOLATION;
import static java.awt.RenderingHints.KEY_RENDERING;
import static java.awt.RenderingHints.KEY_STROKE_CONTROL;
import static java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_COLOR_RENDER_QUALITY;
import static java.awt.RenderingHints.VALUE_DITHER_DISABLE;
import static java.awt.RenderingHints.VALUE_FRACTIONALMETRICS_ON;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC;
import static java.awt.RenderingHints.VALUE_RENDER_QUALITY;
import static java.awt.RenderingHints.VALUE_STROKE_PURE;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
import static java.io.File.separator;
import static org.apache.batik.transcoder.SVGAbstractTranscoder.KEY_HEIGHT;
import static org.apache.batik.transcoder.SVGAbstractTranscoder.KEY_WIDTH;
import static org.apache.batik.util.XMLResourceDescriptor.getXMLParserClassName;
import static org.eclipse.core.runtime.FileLocator.toFileURL;
import static org.eclipse.core.runtime.Platform.getBundle;
import static ummisco.gama.dev.utils.DEBUG.PAD;
import static ummisco.gama.dev.utils.DEBUG.TIMER_WITH_EXCEPTIONS;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.gvt.renderer.ImageRenderer;
import org.apache.batik.gvt.renderer.StaticRenderer;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Point;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import msi.gaml.operators.Cast;
import one.util.streamex.StreamEx;
import ummisco.gama.dev.utils.DEBUG;

/**
 * <p>
 * Mojo which renders SVG icons into PNG format.
 * </p>
 *
 * @goal render-icons
 * @phase generate-resources
 */
public class GamaIconsLoader {

	static {
		DEBUG.ON();
	}

	/** The Constant DEFAULT_PATH. */
	static public final String DEFAULT_PATH = "/icons/";

	/** The Constant DVG_PATH. */
	static public final String SVG_PATH = "/svg/";

	/** The Constant PLUGIN_ID. */
	public static final String PLUGIN_ID = "ummisco.gama.ui.shared";

	/** The Constant DISABLED_SUFFIX. */
	public static final String DISABLED_SUFFIX = "_disabled";

	/** The Constant DISABLED_SUFFIX. */
	public static final String TEMPLATES = "templates";

	/** The svg factory. */
	static final SAXSVGDocumentFactory SVG_FACTORY = new SAXSVGDocumentFactory(getXMLParserClassName());

	/** The png transcoder. */
	static final PNGTranscoder PNG_TRANSCODER = new CustomTranscoder();

	/** The Constant filter. */
	static final DisabledFilter FILTER = new DisabledFilter();

	/** The Constant SCALES. Change it to create new sizes */
	static final String[] SCALES = { "1", "1.5", "2" };

	/**
	 * The Class CustomTranscoder.
	 */
	public static final class CustomTranscoder extends PNGTranscoder {
		@Override
		protected ImageRenderer createRenderer() {
			ImageRenderer renderer = new StaticRenderer();
			RenderingHints renderHints = renderer.getRenderingHints();
			renderHints.add(new RenderingHints(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_OFF));
			renderHints.add(new RenderingHints(KEY_RENDERING, VALUE_RENDER_QUALITY));
			renderHints.add(new RenderingHints(KEY_DITHERING, VALUE_DITHER_DISABLE));
			renderHints.add(new RenderingHints(KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC));
			renderHints.add(new RenderingHints(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_QUALITY));
			renderHints.add(new RenderingHints(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON));
			renderHints.add(new RenderingHints(KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_QUALITY));
			renderHints.add(new RenderingHints(KEY_STROKE_CONTROL, VALUE_STROKE_PURE));
			renderHints.add(new RenderingHints(KEY_FRACTIONALMETRICS, VALUE_FRACTIONALMETRICS_ON));
			renderer.setRenderingHints(renderHints);
			return renderer;
		}
	}

	/**
	 * Builds the icons.
	 *
	 * @throws Exception
	 */
	public static void buildIconsOnDisk() throws Exception {
		Map<String, SVGDocument> icons = gatherAllIcons();
		Path outputPath = outputPath();

		TIMER_WITH_EXCEPTIONS(
				PAD("> GAMA: Exporting " + icons.size() * SCALES.length + " icons", 55, ' ') + PAD(" done in", 15, '_'),
				() -> {
					icons.forEach((iconPathAndName, svg) -> {
						try {
							Point dim = correctSizeOf(svg);
							TranscoderInput input = new TranscoderInput(svg);
							input.setURI(svg.getDocumentURI());
							for (String ss : SCALES) {
								float scale = Float.parseFloat(ss);
								PNG_TRANSCODER.addTranscodingHint(KEY_HEIGHT, dim.y * scale);
								PNG_TRANSCODER.addTranscodingHint(KEY_WIDTH, dim.x * scale);
								File outputFile = new File(outputPath.toString() + separator + iconPathAndName
										+ (scale == 1f ? "" : "@" + ss + "x") + ".png");
								File outputDisabledFile = new File(outputPath.toString() + separator + iconPathAndName
										+ DISABLED_SUFFIX + (scale == 1f ? "" : "@" + ss + "x") + ".png");
								outputFile.getParentFile().mkdirs();
								try (FileOutputStream output = new FileOutputStream(outputFile.getAbsolutePath())) {
									DEBUG.OUT("Exporting " + outputFile);
									PNG_TRANSCODER.transcode(input, new TranscoderOutput(output));
									output.flush();
								}
								BufferedImage image = ImageIO.read(outputFile);
								ImageProducer prod = new FilteredImageSource(image.getSource(), FILTER);
								Image gray = Toolkit.getDefaultToolkit().createImage(prod);
								DEBUG.OUT("Exporting " + outputDisabledFile);
								ImageIO.write(toBufferedImage(gray), "png", outputDisabledFile);
							}
						} catch (Exception ex) {
							DEBUG.OUT(
									"Exception when exporting icon: " + iconPathAndName + " (" + ex.getMessage() + ")");
						}
					});
				});

	}

	/**
	 * To buffered image.
	 *
	 * @param img
	 *            the img
	 * @return the buffered image
	 */
	public static BufferedImage toBufferedImage(final Image img) {
		if (img instanceof BufferedImage) return (BufferedImage) img;
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();
		return bimage;
	}

	/**
	 * The Class DisabledFilter.
	 */
	private static class DisabledFilter extends RGBImageFilter {

		/** The min. */
		private final float min;

		/** The factor. */
		private final float factor;

		/**
		 * Instantiates a new disabled filter.
		 *
		 * @param min
		 *            the min
		 * @param max
		 *            the max
		 */
		DisabledFilter() {
			canFilterIndexColorModel = true;
			this.min = 160;
			this.factor = (255 - min) / 255f;
		}

		@Override
		public int filterRGB(final int x, final int y, final int rgb) {
			// Coefficients are from the sRGB color space:
			int gray = Math.min(255,
					(int) ((0.2125f * (rgb >> 16 & 0xFF) + 0.7154f * (rgb >> 8 & 0xFF) + 0.0721f * (rgb & 0xFF) + .5f)
							* factor + min));
			return rgb & 0xff000000 | gray << 16 | gray << 8 | gray << 0;
		}
	}

	/**
	 * Correct size of.
	 *
	 * @param svg
	 *            the svg
	 * @return the dimension
	 */
	private static Point correctSizeOf(final SVGDocument svg) {
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
		node.setAttribute("viewBox", nativeViewBoxX + " " + nativeViewBoxY + " " + nativeWidth + " " + nativeHeight);
		return new Point(nativeWidth, nativeHeight);
	}

	/**
	 * Gather all icons.
	 *
	 * @param path
	 *            the path
	 * @return the map
	 */
	private static Map<String, SVGDocument> gatherAllIcons() throws Exception {
		URL inputFolderURL = toFileURL(getBundle(PLUGIN_ID).getEntry(SVG_PATH));
		Path inputPath = Path.of(new URI(inputFolderURL.getProtocol(), inputFolderURL.getPath(), null).normalize());
		Map<String, SVGDocument> result = new LinkedHashMap<>();
		StreamEx.of(Files.walk(inputPath)).filter(n -> n.toString().endsWith(".svg")).forEach(p -> {
			try {
				result.put(inputPath.relativize(p).toString().replace(".svg", ""),
						SVG_FACTORY.createSVGDocument(p.toUri().toURL().toString()));
			} catch (IOException e) {}
		});
		return result;
	}

	/**
	 * Output path.
	 *
	 * @return the path
	 */
	private static Path outputPath() throws Exception {
		URL outputFolderURL = toFileURL(getBundle(PLUGIN_ID).getEntry(DEFAULT_PATH));
		return Path.of(new URI(outputFolderURL.getProtocol(), outputFolderURL.getPath(), null).normalize());
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
	 * Preload icons.
	 *
	 * @param bundle
	 *            the bundle
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static void preloadIcons() {
		try {
			URL url = FileLocator.toFileURL(Platform.getBundle(PLUGIN_ID).getEntry(DEFAULT_PATH));
			Path path = new File(new URI(url.getProtocol(), url.getPath(), null).normalize()).toPath();
			List<String> files = Files.walk(path).map(f -> path.relativize(f).toString())
					.filter(n -> n.endsWith(".png") && !n.contains("@")).toList();
			TIMER_WITH_EXCEPTIONS(
					PAD("> GAMA: Preloading " + files.size() + " icons", 55, ' ') + PAD(" done in", 15, '_'),
					() -> files.forEach(n -> GamaIcon.named(n.replace(".png", "")).image()));
		} catch (IOException | URISyntaxException e) {
			DEBUG.ERR("Error when loading GAMA icons ", e);
		}
	}

}
