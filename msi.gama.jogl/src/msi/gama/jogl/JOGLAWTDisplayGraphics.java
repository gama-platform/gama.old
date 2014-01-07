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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
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
import msi.gama.gui.displays.layers.AbstractLayer;
import msi.gama.jogl.scene.*;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
import msi.gama.util.*;
import msi.gama.util.file.GamaFile;
import msi.gaml.operators.Cast;
import msi.gaml.types.GamaGeometryType;
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
		fillBackground(surface.getBackgroundColor(), 1);
	}

	// This method is normally called either when the graphics is created or when the output is changed
	@Override
	public void initFor(final IDisplaySurface surface) {
		super.initFor(surface);
		if ( renderer != null ) {
			// renderer can be temporarily null while the graphics is initialized, as this method is also called from
			// the constructor
			ModelScene s = renderer.getScene();
			if ( s != null ) {
				s.reload();
			}
		}
	}

	/**
	 * Method drawGeometry. Add a given JTS Geometry in the list of all the
	 * existing geometry that will be displayed by openGl.
	 */
	@Override
	public Rectangle2D drawGamaShape(final IScope scope, final IShape shape, final Color c, final boolean fill,
		final Color border, final boolean rounded) {
		if ( shape == null ) { return null; }
		Double depth = 0d;
		IList<String> textures = new GamaList<String>();
		IShape.Type type = shape.getGeometricalType();
		final ITopology topo = scope.getTopology();
		if ( shape.hasAttribute(IShape.DEPTH_ATTRIBUTE) ) {
			depth = Cast.asFloat(scope, shape.getAttribute(IShape.DEPTH_ATTRIBUTE));
			// type = "JTS";
		}
		if ( shape.hasAttribute(IShape.TEXTURE_ATTRIBUTE) ) {
			textures = Cast.asList(scope, shape.getAttribute(IShape.TEXTURE_ATTRIBUTE));
		}
		// if ( shape.hasAttribute(IShape.TYPE_ATTRIBUTE) ) {
		// type = Cast.asString(scope, shape.getAttribute(IShape.TYPE_ATTRIBUTE));
		// }
		final Color color = highlight ? highlightColor : c;
		if ( topo != null && topo.isTorus() ) {
			java.util.List<Geometry> geoms = topo.listToroidalGeometries(shape.getInnerGeometry());
			Geometry world = scope.getSimulationScope().getInnerGeometry();
			for ( Geometry g : geoms ) {
				Geometry intersect = world.intersection(g);
				if ( !intersect.isEmpty() ) {
					drawSingleShape(scope, intersect, color, fill, border, null, rounded, depth,
						msi.gama.common.util.GeometryUtils.getTypeOf(intersect), textures);
				}
			}
		} else {
			drawSingleShape(scope, shape.getInnerGeometry(), color, fill, border, null, rounded, depth, type, textures);
		}

		// Add a geometry with a depth and type coming from Attributes
		return rect;
	}

	private void drawSingleShape(final IScope scope, final Geometry geom, final Color color, final boolean fill,
		final Color border, final Integer angle, final boolean rounded, final Double depth, final IShape.Type type,
		final IList<String> textures) {
		renderer.getScene().addGeometry(geom, scope.getAgentScope(), currentZLayer, currentLayerId, color, fill,
			border, textures.isEmpty() ? false : true, textures, angle, depth.floatValue(), currentOffset,
			currentScale, rounded, type, getCurrentAlpha());

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
		final ILocation sizeInModelUnits, final Color gridColor, final Double angle, final Double z,
		final boolean isDynamic, final String name) {
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
			if ( scope == null ) {
				texture = renderer.createTexture(img, isDynamic, 0);
			} else {
				texture = renderer.createTexture(img, isDynamic, scope.getAgentScope().getIndex());
			}
		}
		renderer.getScene().addImage(img, scope == null ? null : scope.getAgentScope(), currentZLayer, currentLayerId,
			curX, curY, z, curWidth, curHeight, angle, currentOffset, currentScale, isDynamic, getCurrentAlpha(),
			texture, name);

		if ( gridColor != null ) {
			drawGridLine(img, gridColor/* , name */);
		}
		return rect;
	}

	@Override
	public Rectangle2D drawGrid(final IScope scope, final BufferedImage img, final double[] gridValueMatrix,
		final boolean isTextured, final boolean isTriangulated, final boolean isShowText,
		final ILocation locationInModelUnits, final ILocation sizeInModelUnits, final Color gridColor,
		final Double angle, final Double z, final double cellSize, final String name) {

		MyTexture texture = null;
		Envelope env = null;
		// String popName = null;
		if ( !renderer.getScene().getTextures().containsKey(img) ) {
			if ( scope == null ) {
				texture = renderer.createTexture(img, true, 0);
				env = new Envelope(0, 0, widthOfEnvironmentInModelUnits, heightOfEnvironmentInModelUnits);
				// popName = "";
			} else {
				IAgent a = scope.getAgentScope();
				texture = renderer.createTexture(img, true, a.getIndex());
				env = scope.getSimulationScope().getEnvelope();
				// popName = a.getPopulation().getName();
			}
		}

		renderer.getScene().addDEM(gridValueMatrix, img, null, scope == null ? null : scope.getAgentScope(),
			isTextured, isTriangulated, isShowText, false, env, 1.0, getCurrentAlpha(), currentOffset, currentScale,
			cellSize, texture, name, currentLayerId);

		if ( gridColor != null ) {
			drawGridLine(img, gridColor/* r, popName */);
		}

		return rect;
	}

	public void drawGridLine(final BufferedImage image, final Color lineColor/* , final String popName */) {
		double stepX, stepY;

		double wRatio = (double) this.getEnvironmentWidth() / (double) image.getWidth();
		double hRatio = (double) this.getEnvironmentHeight() / (double) image.getHeight();
		for ( int i = 0; i < image.getWidth(); i++ ) {
			for ( int j = 0; j < image.getHeight(); j++ ) {
				stepX = (i + 0.5) / image.getWidth() * image.getWidth();
				stepY = (j + 0.5) / image.getHeight() * image.getHeight();
				final Geometry g =
					GamaGeometryType.buildRectangle(wRatio, hRatio, new GamaPoint(stepX * wRatio, stepY * hRatio))
						.getInnerGeometry();
				renderer.getScene().addGeometry(g, null, currentZLayer, currentLayerId, lineColor, false, lineColor,
					false, null, 0, 0, currentOffset, currentScale, false, IShape.Type.GRIDLINE, getCurrentAlpha());
			}
		}
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
			_texture = renderer.createTexture(texture, false, 0);
		}

		// getASCfromImg(dem);
		// FIXME: alpha,scale,offset not taken in account when using the operator dem
		renderer.getScene().addDEM(null, texture, dem, null, false, false, false, true, env, z_factor, null, null,
			null, 1, null, null, 0);
		return null;
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
	public Rectangle2D drawChart(final IScope scope, final BufferedImage chart, final Double z) {
		return drawImage(scope, chart, new GamaPoint(0, 0), null, null, 0d, z, true, "chart");
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
		final Double heightInModelUnits, final String fontName, final Integer styleName, final Double angle,
		final Double z, final Boolean bitmap) {
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
			stringColor, fontName, styleName, angle, getCurrentAlpha(), bitmap);
		return null;
	}

	@Override
	public void fillBackground(final Color bgColor, final double opacity) {
		renderer.setBackground(bgColor);
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
		if ( renderer != null ) {
			renderer.setAntiAliasing(quality);
		}
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

		this.currentZLayer = (float) (getMaxEnvDim() * ((AbstractLayer) layer).getZPosition());
		// get the value of the position
		if ( this.currentZLayer == 0 ) {
			this.currentZLayer = ((AbstractLayer) layer).getPosition().getZ();
		}

		// get the value of the z scale if positive otherwise set it to 1.
		float z_scale;
		if ( ((AbstractLayer) layer).getExtent().getZ() > 0 ) {
			z_scale = (float) ((AbstractLayer) layer).getExtent().getZ();
		} else {
			z_scale = 1;
		}

		final Boolean refresh = layer.isDynamic();
		renderer.getScene().enterStaticMode(refresh == null ? false : !refresh);

		// TODO Pourquoi ne pas utiliser l'ordre des layers ? layer.getOrder() ??
		this.currentLayerId = currentLayerId + 1;
		currentOffset =
			new GamaPoint(
				xOffsetInPixels / ((double) widthOfDisplayInPixels / (double) widthOfEnvironmentInModelUnits),
				yOffsetInPixels / ((double) heightOfDisplayInPixels / (double) heightOfEnvironmentInModelUnits),
				currentZLayer);
		currentScale =
			new GamaPoint(widthOfLayerInPixels / (double) widthOfDisplayInPixels, heightOfLayerInPixels /
				(double) heightOfDisplayInPixels, z_scale);
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
