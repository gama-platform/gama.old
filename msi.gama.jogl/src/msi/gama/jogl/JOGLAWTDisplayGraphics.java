/*********************************************************************************************
 * 
 * 
 * 'JOGLAWTDisplayGraphics.java', in plugin 'msi.gama.jogl', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/

package msi.gama.jogl;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.jogl.scene.ModelScene;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.outputs.display.AbstractDisplayGraphics;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gaml.operators.Cast;
import msi.gaml.types.GamaGeometryType;
import com.vividsolutions.jts.geom.Geometry;

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

	public void reinitFor(final IDisplaySurface surface) {
		super.initFor(surface);
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
		List<String> textures = new ArrayList<String>();
		IShape.Type type = shape.getGeometricalType();
		List<Double> ratio = new ArrayList<Double>();
		List<GamaColor> colors = new ArrayList<GamaColor>();
		final ITopology topo = scope.getTopology();
		if ( shape.hasAttribute(IShape.DEPTH_ATTRIBUTE) ) {
			depth = Cast.asFloat(scope, shape.getAttribute(IShape.DEPTH_ATTRIBUTE));
		}
		if ( shape.hasAttribute(IShape.TEXTURE_ATTRIBUTE) ) {
			textures = Cast.asList(scope, shape.getAttribute(IShape.TEXTURE_ATTRIBUTE));
		}
		if ( shape.hasAttribute(IShape.RATIO_ATTRIBUTE) ) {
			ratio = Cast.asList(scope, shape.getAttribute(IShape.RATIO_ATTRIBUTE));
		}
		if ( shape.hasAttribute(IShape.COLOR_LIST_ATTRIBUTE) ) {
			colors = Cast.asList(scope, shape.getAttribute(IShape.COLOR_LIST_ATTRIBUTE));
		}
		final Color color = highlight ? highlightColor : c;
		if ( topo != null && topo.isTorus() ) {
			java.util.List<Geometry> geoms = topo.listToroidalGeometries(shape.getInnerGeometry());
			Geometry world = scope.getSimulationScope().getInnerGeometry();
			for ( Geometry g : geoms ) {
				Geometry intersect = world.intersection(g);
				if ( !intersect.isEmpty() ) {
					drawSingleShape(scope, intersect, color, fill, border, null, rounded, depth,
						msi.gama.common.util.GeometryUtils.getTypeOf(intersect), textures, ratio, colors);
				}
			}
		} else {
			drawSingleShape(scope, shape.getInnerGeometry(), color, fill, border, null, rounded, depth, type, textures,
				ratio, colors);
		}

		// Add a geometry with a depth and type coming from Attributes
		return rect;
	}

	private void drawSingleShape(final IScope scope, final Geometry geom, final Color color, final boolean fill,
		final Color border, final Integer angle, final boolean rounded, final Double depth, final IShape.Type type,
		final List<String> textures, final List<Double> ratio, final List<GamaColor> colors) {
		renderer.getScene().addGeometry(geom, scope.getAgentScope(), color, fill, border,
			textures.isEmpty() ? false : true, textures, angle, depth.doubleValue(), rounded, type, ratio, colors);

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
		final ILocation sizeInModelUnits, final Color gridColor, final Double angle, final boolean isDynamic,
		final String name) {
		GamaPoint location = new GamaPoint(locationInModelUnits);
		GamaPoint dimensions = new GamaPoint(sizeInModelUnits);
		if ( sizeInModelUnits == null ) {
			dimensions.x = wFromPixelsToModelUnits(widthOfLayerInPixels);
			dimensions.y = hFromPixelsToModelUnits(heightOfLayerInPixels);
		}
		renderer.getScene().addImage(img, scope == null ? null : scope.getAgentScope(), location, dimensions, angle,
			isDynamic, name);

		if ( gridColor != null ) {
			drawGridLine(img, gridColor/* , name */);
		}
		return rect;
	}

	private Envelope3D getWorldEnvelopeWithZ(final double z) {
		return new Envelope3D(0, widthOfEnvironmentInModelUnits, 0, heightOfEnvironmentInModelUnits, 0, z);
	}

	@Override
	public Rectangle2D drawGrid(final IScope scope, final BufferedImage img, final double[] valueMatrix,
		final boolean textured, final boolean triangulated, final boolean isGrayScaled, final boolean showText,
		final Color gridColor, final double cellSize, final String name) {
		Envelope3D env = getWorldEnvelopeWithZ(1);
		IAgent a = scope.getAgentScope();
		renderer.getScene().addDEM(valueMatrix, img, a, textured, triangulated, isGrayScaled, showText, env, cellSize,
			name);
		if ( gridColor != null ) {
			drawGridLine(img, gridColor);
		}
		return rect;
	}

	public void drawGridLine(final BufferedImage image, final Color lineColor) {
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
				renderer.getScene().addGeometry(g, null, lineColor, false, lineColor, false, null, 0, 0, false,
					IShape.Type.GRIDLINE, null, null);
			}
		}
	}

	// Build a dem from a dem.png and a texture.png (used when using the operator dem)
	@Override
	public Rectangle2D drawDEM(final IScope scope, final BufferedImage dem, final BufferedImage texture,
		final Double z_factor) {
		renderer.getScene().addDEMFromPNG(texture, dem, getWorldEnvelopeWithZ(z_factor));
		return null;
	}

	/**
	 * Method drawChart.
	 * 
	 * @param chart
	 *            JFreeChart
	 */
	@Override
	public Rectangle2D drawChart(final IScope scope, final BufferedImage chart, final Double z) {
		return drawImage(scope, chart, new GamaPoint(0, 0), null, null, 0d, true, "chart");
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
		final Boolean bitmap) {
		GamaPoint location = new GamaPoint(locationInModelUnits).yNegated();
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
		renderer.getScene().addString(string, location, size, sizeInModelUnits, stringColor, fontName, styleName,
			angle, bitmap);
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
		renderer.getScene().beginDrawingLayers();
	}

	@Override
	public void setQualityRendering(final boolean quality) {
		if ( renderer != null ) {
			renderer.setAntiAliasing(quality);
		}
	}

	/**
	 * Set the value z of the current Layer. If no value is define is defined
	 * set it to 0. Set the type of the layer weither it's a static layer (refresh:false) or
	 * a dynamic layer (by default or refresh:true)
	 */
	@Override
	public void beginDrawingLayer(final ILayer layer) {
		super.beginDrawingLayer(layer);
		// TODO Correct if and only if the z is given as a percentage
		double currentZLayer = getMaxEnvDim() * layer.getPosition().getZ();

		// get the value of the z scale if positive otherwise set it to 1.
		double z_scale;
		if ( layer.getExtent().getZ() > 0 ) {
			z_scale = layer.getExtent().getZ();
		} else {
			z_scale = 1;
		}

		final Boolean refresh = layer.isDynamic();
		boolean isStatic = refresh == null ? false : !refresh;

		GamaPoint currentOffset =
			new GamaPoint(
				xOffsetInPixels / ((double) widthOfDisplayInPixels / (double) widthOfEnvironmentInModelUnits),
				yOffsetInPixels / ((double) heightOfDisplayInPixels / (double) heightOfEnvironmentInModelUnits),
				currentZLayer);
		GamaPoint currentScale =
			new GamaPoint(widthOfLayerInPixels / (double) widthOfDisplayInPixels, heightOfLayerInPixels /
				(double) heightOfDisplayInPixels, z_scale);

		renderer.getScene().beginDrawingLayer(layer.getName(), layer.getOrder(), currentOffset, currentScale,
			currentAlpha, isStatic, layer.getTrace(), layer.getFading());
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
		renderer.getScene().endDrawingLayers();
	}

}
