/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */

package msi.gama.jogl;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.io.IOException;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.ImageUtils;
import msi.gama.gui.displays.awt.AbstractDisplayGraphics;
import msi.gama.jogl.scene.MyTexture;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
import msi.gama.util.file.GamaFile;
import msi.gaml.operators.Cast;
import msi.gaml.types.GamaGeometryType;
import org.jfree.chart.JFreeChart;
import com.vividsolutions.jts.geom.*;

/**
 * 
 * Simplifies the drawing of circles, rectangles, and so forth. Rectangles are
 * generally faster to draw than circles. The Displays should take care of
 * layouts while objects that wish to be drawn as a shape need only call the
 * appropriate method.
 * 
 * @author Arnaud Grignard, Alexis Drogoul, Patrick Taillandier
 * @version $Revision: 1.13 $ $Date: 2010-03-19 07:12:24 $
 */

public class JOGLAWTDisplayGraphics extends AbstractDisplayGraphics implements IGraphics.OpenGL {

	// GLRenderer.
	// TODO remove references to renderer
	private final JOGLAWTGLRenderer renderer;
	// private final ModelScene renderer.getScene();
	// All the geometry of the same layer are drawn in the same z plan.
	private double currentZLayer = 0.0f;
	private int currentLayerId = 0;
	// Is the layer static data or dynamic geometry that has to be redrawn every iteration
	private boolean currentLayerIsStatic = false;
	private GamaPoint currentOffset;
	private GamaPoint currentScale;
	private boolean highlight = false;

	// OpenGL list ID
	// private final int listID = -1;

	/**
	 * @param JOGLAWTDisplaySurface displaySurface
	 */
	public JOGLAWTDisplayGraphics(final JOGLAWTDisplaySurface surface, final JOGLAWTGLRenderer r) {
		super(surface);
		renderer = r;
	}

	/**
	 * Method drawGeometry. Add a given JTS Geometry in the list of all the
	 * existing geometry that will be displayed by openGl.
	 */
	@Override
	public Rectangle2D drawGamaShape(final IScope scope, final IShape geometry, final Color c, final boolean fill,
		final Color border, final Integer angle, final boolean rounded) {

		Geometry geom = null;
		if ( geometry == null ) { return null; }
		final ITopology topo = scope.getTopology();
		if ( topo != null && topo.isTorus() ) {
			geom = topo.returnToroidalGeom(geometry.getInnerGeometry());
		} else {
			geom = geometry.getInnerGeometry();
		}
		final Color color = highlight ? highlightColor : c;
		Double depth = 0d;
		String type = "none";
		// Add a geometry with a depth and type coming from Attributes
		if ( geometry.hasAttribute("depth") ) {
			depth = Cast.asFloat(scope, geometry.getAttribute("depth"));
			type = "JTS";
		}
		if ( geometry.hasAttribute("type") ) {
			type = Cast.asString(scope, geometry.getAttribute("type"));
		}
		renderer.getScene().addGeometry(geom, scope.getAgentScope(), currentZLayer, currentLayerId, color, fill,
			border, false, angle, depth.floatValue(), currentOffset, currentScale, rounded, type, currentLayerIsStatic,
			getCurrentAlpha());
		return rect;
	}

	// FIXME: Won't work for a rectangle grid inverse buildline height on x and with width on y
	public void drawGridLine(final BufferedImage image, final Color lineColor) {
		double stepX, stepY;

		for ( int i = 0; i <= image.getWidth(); i++ ) {
			stepX = i / (double) image.getWidth() * image.getWidth();
			final Geometry g =
				GamaGeometryType.buildLine(new GamaPoint(stepX, 0), new GamaPoint(stepX, image.getWidth()))
					.getInnerGeometry();
			renderer.getScene().addGeometry(g, null, currentZLayer, currentLayerId, lineColor, true, null, false, 0, 0,
				currentOffset, currentScale, false, "grid", currentLayerIsStatic, getCurrentAlpha());
		}

		for ( int i = 0; i <= image.getHeight(); i++ ) {
			stepY = i / (double) image.getHeight() * image.getHeight();;
			final Geometry g =
				GamaGeometryType.buildLine(new GamaPoint(0, stepY), new GamaPoint(image.getHeight(), stepY))
					.getInnerGeometry();
			renderer.getScene().addGeometry(g, null, currentZLayer, currentLayerId, lineColor, true, null, false, 0, 0,
				currentOffset, currentScale, false, "grid", currentLayerIsStatic, getCurrentAlpha());
		}
	}

	@Override
	public Rectangle2D drawGrid(final IScope scope, final BufferedImage img, final double[] gridValueMatrix,
		final boolean isTextured, final boolean isTriangulated, final boolean isShowText,
		final ILocation locationInModelUnits, final ILocation sizeInModelUnits, final Color gridColor,
		final Integer angle, final Double z, final boolean isDynamic, final int cellSize) {
		// FIXME : need to chek the drawGridLIne method
		if ( gridColor != null ) {
			drawGridLine(img, gridColor);
		}
		return drawDEM2(gridValueMatrix, img, isTextured, isTriangulated, isShowText, scope.getSimulationScope()
			.getEnvelope(), 1.0, getCurrentAlpha(), currentOffset, currentScale, cellSize);
	}

	// Build a grid with a dem corresponding to the value in gridValue and textured by texture
	public Rectangle2D drawDEM2(final double[] dem, final BufferedImage texture, final boolean isTextured,
		final boolean isTriangulated, final boolean isShowText, final Envelope env, final Double z_factor,
		final Double alpha, final GamaPoint offset, final GamaPoint scale, final int cellSize) {
		MyTexture _texture = null;
		if ( !renderer.getScene().getTextures().containsKey(texture) ) {
			_texture = renderer.createTexture(texture, false);
		}
		renderer.getScene().addDEM(dem, texture, null, isTextured, isTriangulated, isShowText, false, env, z_factor,
			alpha, offset, scale, cellSize);
		return null;
	}

	// Build a dem from a dem.png and a texture.png (used when using the operator dem)
	@Override
	public Rectangle2D drawDEM(final GamaFile demFileName, final GamaFile textureFileName, final Envelope env,
		final Double z_factor) {
		BufferedImage dem = null;
		BufferedImage texture = null;
		try {
			dem = ImageUtils.getInstance().getImageFromFile(demFileName.getPath());
		} catch (final IOException e) {
			e.printStackTrace();
		}
		try {
			texture = ImageUtils.getInstance().getImageFromFile(textureFileName.getPath());
		} catch (final IOException e) {
			e.printStackTrace();
		}
		// FIXME: Need to flip vertically the image (need excactly to know why)
		texture = FlipRightSideLeftImage(texture);
		MyTexture _texture = null;
		if ( !renderer.getScene().getTextures().containsKey(texture) ) {
			_texture = renderer.createTexture(texture, false);
		}

		// getASCfromImg(dem);
		// FIXME: alpha,scale,offset not taken in account when using the operator dem
		renderer.getScene().addDEM(null, texture, dem, false, false, false, true, env, z_factor, null, null, null, 1);
		return null;
	}

	public void getASCfromImg(final BufferedImage dem) {
		double value;
		System.out.println("asc");
		for ( int i = 0; i < dem.getWidth(); i++ ) {
			System.out.println();
			for ( int j = 0; j < dem.getHeight(); j++ ) {
				value = dem.getRGB(j, i) & 255;
				System.out.print(value / 6);
				System.out.print(" ");
			}
		}
	}

	/**
	 * Method drawImage.
	 * 
	 * @param img
	 *            Image
	 * @param angle
	 *            Integer
	 */
	@Override
	public Rectangle2D drawImage(final IScope scope, final BufferedImage img, final ILocation locationInModelUnits,
		final ILocation sizeInModelUnits, final Color gridColor, final Integer angle, final Double z,
		final boolean isDynamic) {
		double curX, curY;
		if ( locationInModelUnits == null ) {
			curX = 0d;
			curY = 0d;
		} else {
			curX = locationInModelUnits.getX();
			curY = locationInModelUnits.getY();
		}
		double curWidth, curHeight;
		if ( sizeInModelUnits == null ) {
			curWidth = wFromPixelsToModelUnits(widthOfLayerInPixels);
			curHeight = hFromPixelsToModelUnits(heightOfLayerInPixels);
		} else {
			curWidth = sizeInModelUnits.getX();
			curHeight = sizeInModelUnits.getY();
		}
		MyTexture texture = null;
		if ( !renderer.getScene().getTextures().containsKey(img) ) {
			texture = renderer.createTexture(img, isDynamic);
		}

		renderer.getScene().addImage(img, scope == null ? null : scope.getAgentScope(), curX, curY, z, curWidth,
			curHeight, angle, currentOffset, currentScale, isDynamic, getCurrentAlpha(), texture);

		if ( gridColor != null ) {
			drawGridLine(img, gridColor);
		}

		return rect;
	}

	private BufferedImage FlipRightSideLeftImage(BufferedImage img) {
		final java.awt.geom.AffineTransform tx = java.awt.geom.AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-img.getWidth(null), 0);
		final AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		img = op.filter(img, null);
		return img;

	}

	/**
	 * Method drawChart.
	 * 
	 * @param chart
	 *            JFreeChart
	 */
	@Override
	public Rectangle2D drawChart(final IScope scope, final JFreeChart chart, final Double z) {
		final BufferedImage im =
		// ImageUtils.toCompatibleImage(chart.createBufferedImage(widthOfLayerInPixels, heightOfLayerInPixels));
			chart.createBufferedImage(widthOfLayerInPixels, heightOfLayerInPixels);
		return drawImage(scope, im, new GamaPoint(0, 0), null, null, 0, z, true);
	}

	/**
	 * Method drawString.
	 * 
	 * @param string
	 *            String
	 * @param stringColor
	 *            Color
	 * @param angle
	 *            Integer
	 */
	@Override
	public Rectangle2D drawString(final String string, final Color stringColor, final ILocation locationInModelUnits,
		final Double heightInModelUnits, final String fontName, final Integer styleName, final Integer angle,
		final Double z) {
		double curX, curY;
		if ( locationInModelUnits == null ) {
			curX = 0d;
			curY = 0d;
		} else {
			curX = locationInModelUnits.getX();
			curY = locationInModelUnits.getY();
		}
		Integer size;
		Double sizeInModelUnits;
		if ( heightInModelUnits == null ) {
			size = heightOfLayerInPixels;
			sizeInModelUnits =
				heightOfDisplayInPixels / (double) heightOfEnvironmentInModelUnits * heightOfLayerInPixels;
		} else {
			sizeInModelUnits = heightInModelUnits;
			size =
				(int) ((double) heightOfDisplayInPixels / (double) heightOfEnvironmentInModelUnits * heightInModelUnits);
		}
		renderer.getScene().addString(string, curX, -curY, z, size, sizeInModelUnits, currentOffset, currentScale,
			stringColor, fontName, styleName, angle, getCurrentAlpha());
		return null;
	}

	@Override
	public void fillBackground(final Color bgColor, final double opacity) {
		setOpacity(opacity);
	}

	/**
	 * Each new step the Z value of the first layer is set to 0.
	 */
	@Override
	public void beginDrawingLayers() {
		this.currentLayerId = 0;
		this.currentZLayer = 0.0f;
	}

	@Override
	public void setQualityRendering(final boolean quality) {
		renderer.setAntiAliasing(quality);
	}

	/**
	 * Set the value z of the current Layer. If no value is define is defined
	 * set it to 0.
	 * Set the type of the layer weither it's a static layer (refresh:false) or
	 * a dynamic layer (by default or refresh:true)
	 */
	@Override
	public void beginDrawingLayer(final ILayer layer) {
		super.beginDrawingLayer(layer);
		this.currentZLayer = (float) (getMaxEnvDim() * layer.getZPosition());
		final Boolean refresh = layer.isDynamic();
		currentLayerIsStatic = refresh == null ? false : !refresh;

		// TODO Pourquoi ne pas utiliser l'ordre des layers ? layer.getOrder() ??
		this.currentLayerId = currentLayerId + 1;
		currentOffset =
			new GamaPoint(
				xOffsetInPixels / ((double) widthOfDisplayInPixels / (double) widthOfEnvironmentInModelUnits),
				yOffsetInPixels / ((double) heightOfDisplayInPixels / (double) heightOfEnvironmentInModelUnits),
				currentZLayer);
		currentScale =
			new GamaPoint(widthOfLayerInPixels / (double) widthOfDisplayInPixels, heightOfLayerInPixels /
				(double) heightOfDisplayInPixels, 1);
		// GuiUtils.debug("JOGLAWTDisplayGraphics.beginDrawingLayer currentScale: " + currentScale);
		// GuiUtils.debug("JOGLAWTDisplayGraphics.beginDrawingLayer currentOffset: " + currentOffset);
	}

	private double getMaxEnvDim() {
		return widthOfEnvironmentInModelUnits > heightOfEnvironmentInModelUnits ? widthOfEnvironmentInModelUnits
			: heightOfEnvironmentInModelUnits;
	}

	@Override
	public void beginHighlight() {
		highlight = true;
	}

	@Override
	public void endHighlight() {
		highlight = false;
	}

	/**
	 * Method endDrawingLayers()
	 * @see msi.gama.common.interfaces.IGraphics#endDrawingLayers()
	 */
	@Override
	public void endDrawingLayers() {
		renderer.getScene().lockStaticObjects();
	}

}
