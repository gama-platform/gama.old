/*******************************************************************************************************
 *
 * GamaIconsProducer.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
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
import static java.lang.System.out;
import static org.apache.batik.util.XMLResourceDescriptor.getXMLParserClassName;

import java.awt.Dimension;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.gvt.renderer.ImageRenderer;
import org.apache.batik.gvt.renderer.StaticRenderer;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGDocument;

/**
 * <p>
 * Mojo which renders SVG icons into PNG format.
 * </p>
 *
 * @goal render-icons
 * @phase generate-resources
 */
public class GamaIconsProducer {

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
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(final String args[]) {
		String input = args[0];
		String output = args[1];
		Path inputPath = Paths.get(input);
		Path outputPath = Paths.get(output);
		int number;
		long start = System.currentTimeMillis();
		number = produceIcons(inputPath, outputPath);
		long stop = System.currentTimeMillis();
		System.out.println("Produced " + number + " icons for GAMA in " + (stop - start) / 1000f + " seconds");
	}

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
	 * Builds the icons and returns the number of icons produced
	 *
	 * @throws Exception
	 */
	public static int produceIcons(final Path inputPath, final Path outputPath) {
		int[] counter = { 0 };
		try {
			Files.walk(inputPath).filter(n -> n.toString().endsWith(".svg")).sorted().forEach(p -> {
				String iconPathAndName = inputPath.relativize(p).toString().replace(".svg", "");
				try {
					SVGDocument svg = SVG_FACTORY.createSVGDocument(p.toUri().toURL().toString());
					Dimension dim = correctSizeOf(iconPathAndName, svg);
					TranscoderInput input = new TranscoderInput(svg);
					input.setURI(svg.getDocumentURI());
					for (String ss : SCALES) {
						float scale = Float.parseFloat(ss);
						PNG_TRANSCODER.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, dim.height * scale);
						PNG_TRANSCODER.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, dim.width * scale);
						File outputFile = buildOutputFile(outputPath, iconPathAndName, ss);
						File outputDisabledFile = buildOutputFile(outputPath, iconPathAndName + DISABLED_SUFFIX, ss);
						outputFile.getParentFile().mkdirs();
						try (FileOutputStream output = new FileOutputStream(outputFile.getAbsolutePath())) {
							PNG_TRANSCODER.transcode(input, new TranscoderOutput(output));
							output.flush();
						}
						counter[0]++;
						BufferedImage image = ImageIO.read(outputFile);
						ImageProducer prod = new FilteredImageSource(image.getSource(), FILTER);
						Image gray = Toolkit.getDefaultToolkit().createImage(prod);
						ImageIO.write(toBufferedImage(gray), "png", outputDisabledFile);
						counter[0]++;
					}
				} catch (IOException | TranscoderException e) {
					System.out
							.println("Exception when exporting icon: " + iconPathAndName + " (" + e.getMessage() + ")");
				}

			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return counter[0];
	}

	/**
	 * Builds the output file.
	 *
	 * @param path
	 *            the output path
	 * @param name
	 *            the icon path and name
	 * @param ss
	 *            the ss
	 * @param scale
	 *            the scale
	 * @return the file
	 */
	private static File buildOutputFile(final Path path, final String name, final String scale) {
		return new File(path.toString() + separator + name + ("1".equals(scale) ? "" : "@" + scale + "x") + ".png");
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
	private static Dimension correctSizeOf(final String name, final SVGDocument svg) {
		Element node = svg.getDocumentElement();
		NodeList list = svg.getElementsByTagName("g");
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			Node id = n.getAttributes().getNamedItem("id");
			if (id != null && "ICONES".equals(id.getTextContent())) {
				out.println("==> WARNING: " + name + " can be optimized. ");
			}
		}
		String nativeWidthStr = node.getAttribute("width");
		String nativeHeightStr = node.getAttribute("height");
		String nativeViewBoxX = "0", nativeViewBoxY = "0";
		String viewBoxStr = node.getAttribute("viewBox");
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
		float floatWidth = Float.parseFloat(nativeWidthStr);
		float floatHeight = Float.parseFloat(nativeHeightStr);
		if (floatWidth != (int) floatWidth) {
			out.println("==> WARNING: width of " + name + " is " + floatWidth + " (its height is " + floatHeight + ")");
		} else if (floatHeight != (int) floatHeight) {
			out.println("==> WARNING: height of " + name + " is " + floatHeight + " (its width is " + floatWidth + ")");
		}
		nativeWidth = Math.round(floatWidth);
		nativeHeight = Math.round(floatHeight);
		node.setAttribute("width", String.valueOf(nativeWidth));
		node.setAttribute("height", String.valueOf(nativeHeight));
		node.setAttribute("viewBox", nativeViewBoxX + " " + nativeViewBoxY + " " + nativeWidth + " " + nativeHeight);
		out.println("Processing " + name + " in " + nativeWidth + "x" + nativeHeight);
		return new Dimension(nativeWidth, nativeHeight);
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

}
