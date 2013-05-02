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
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
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
import java.awt.image.BufferedImage;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.displays.awt.AbstractDisplayGraphics;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
import msi.gama.util.file.GamaFile;
import msi.gaml.types.GamaGeometryType;
import org.jfree.chart.JFreeChart;
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
	private final JOGLAWTGLRenderer renderer;
	// All the geometry of the same layer are drawn in the same z plan.
	private double currentZLayer = 0.0f;
	private int currentLayerId = 0;
	// Is the layer static data or dynamic geometry that has to be redrawn every iteration
	private boolean currentLayerIsStatic = false;
	private GamaPoint currentOffset;
	private GamaPoint currentScale;

	// OpenGL list ID
	// private final int listID = -1;

	/**
	 * @param JOGLAWTDisplaySurface displaySurface
	 */
	public JOGLAWTDisplayGraphics(final JOGLAWTDisplaySurface surface, JOGLAWTGLRenderer r) {
		super(surface);
		renderer = r;
	}

	/**
	 * Method drawGeometry. Add a given JTS Geometry in the list of all the
	 * existing geometry that will be displayed by openGl.
	 */
	@Override
	public Rectangle2D drawGamaShape(IScope scope, IShape geometry, Color color, boolean fill, Color border,
		Integer angle, boolean rounded) {

		// Check if the geometry has a height value (3D Shape or Volume)
		Geometry geom = null;
		if ( geometry == null ) { return null; }
		ITopology topo = scope.getTopology();
		if ( topo != null && topo.isTorus() ) {
			geom = topo.returnToroidalGeom(geometry.getInnerGeometry());
		} else {
			geom = geometry.getInnerGeometry();
		}

		// GamaPoint offset = new GamaPoint(xOffsetInPixels, yOffsetInPixels);

		// Add a geometry with a depth and type coming from Attributes
		if ( geometry.getAttribute("depth") != null && geometry.getAttribute("type") != null ) {
			Double depth = (Double) geometry.getAttribute("depth");
			String type = (String) geometry.getAttribute("type");
			renderer.addJTSGeometry(geom, scope.getAgentScope().getAgent(), currentZLayer, currentLayerId, color, fill,
				border, false, angle, depth.floatValue(), currentOffset, currentScale, rounded, type.toString(),
				currentLayerIsStatic, getCurrentAlpha());
		}

		else {
			// Add a geometry with a depth and type coming from getUSerData (with add_z operator)
			if ( geometry.getInnerGeometry().getUserData() != null ) {
				float height = new Float(geom.getUserData().toString());
				renderer.addJTSGeometry(geom, scope.getAgentScope().getAgent(), currentZLayer, currentLayerId, color,
					fill, border, false, angle, height, currentOffset, currentScale, rounded, "JTS",
					currentLayerIsStatic, getCurrentAlpha());
			} else {
				// add a 2D geometry without any 3D data.
				renderer.addJTSGeometry(geom, scope.getAgentScope().getAgent(), currentZLayer, currentLayerId, color,
					fill, border, false, angle, 0, currentOffset, currentScale, rounded, "none", currentLayerIsStatic,
					getCurrentAlpha());
			}

		}

		// FIXME : Here should check the value of the Property3D of the GamaShape
		/*
		 * if ( geom.getUserData() != null ) {
		 * float height = new Float(geom.getUserData().toString());
		 * this.AddJTSGeometryInJTSGeometries(geom, scope.getAgentScope().getAgent(),
		 * currentZLayer, currentLayerId, color, fill, border, false, angle, height,
		 * offSet,rounded);
		 * } else {
		 * this.AddJTSGeometryInJTSGeometries(geom, scope.getAgentScope().getAgent(),
		 * currentZLayer, currentLayerId, color, fill, border, false, angle, 0, offSet,rounded);
		 * }
		 */
		// FIXME: Need to remove the use of sw.
		return rect;
		// return sw.toShape(geom).getBounds2D();
	}

	public void drawGrid(final BufferedImage image, final Color lineColor) {
		// TODO AD Pas du tout testée
		double stepX, stepY;
		for ( int i = 0; i <= image.getWidth(); i++ ) {
			stepX = i / (double) image.getWidth() * image.getWidth();
			Geometry g =
				GamaGeometryType.buildLine(new GamaPoint(stepX, 0), new GamaPoint(stepX, image.getWidth()))
					.getInnerGeometry();
			renderer.addJTSGeometry(g, null, currentZLayer, currentLayerId, lineColor, true, null, false, 0, 0,
				currentOffset, currentScale, false, "grid", currentLayerIsStatic, getCurrentAlpha());
		}

		for ( int i = 0; i <= image.getHeight(); i++ ) {
			stepY = i / (double) image.getHeight() * image.getHeight();;
			Geometry g =
				GamaGeometryType.buildLine(new GamaPoint(0, stepY), new GamaPoint(image.getHeight(), stepY))
					.getInnerGeometry();
			renderer.addJTSGeometry(g, null, currentZLayer, currentLayerId, lineColor, true, null, false, 0, 0,
				currentOffset, currentScale, false, "grid", currentLayerIsStatic, getCurrentAlpha());
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
	public Rectangle2D drawImage(IScope scope, BufferedImage img, ILocation locationInModelUnits,
		ILocation sizeInModelUnits, Color gridColor, Integer angle, Double z, boolean isDynamic) {
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

		renderer.addImages(img, scope == null ? null : scope.getAgentScope(), curX, curY, z, curWidth, curHeight,
			angle, currentOffset, currentScale, isDynamic, getCurrentAlpha());
		// rect.setRect(curX, curY, curWidth, curHeight);
		// }

		return rect;
	}

	/**
	 * Method drawChart.
	 * 
	 * @param chart
	 *            JFreeChart
	 */
	@Override
	public Rectangle2D drawChart(final IScope scope, final JFreeChart chart, final Double z) {
		BufferedImage im = chart.createBufferedImage(widthOfLayerInPixels, heightOfLayerInPixels);
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
	public Rectangle2D drawString(String string, Color stringColor, ILocation locationInModelUnits,
		java.lang.Double heightInModelUnits, String fontName, Integer styleName, Integer angle, Double z) {
		// FIXME fontName ? Angle ? Height ?
		double curX, curY;
		if ( locationInModelUnits == null ) {
			curX = 0d;
			curY = 0d;
		} else {
			curX = locationInModelUnits.getX();
			curY = locationInModelUnits.getY();
		}
		double curHeight;
		if ( heightInModelUnits == null ) {
			curHeight = hFromPixelsToModelUnits(heightOfLayerInPixels);
		} else {
			curHeight = heightInModelUnits;
		}
		// TODO Do not suppress the "-" !
		renderer.addString(string, curX, -curY, z, curHeight, currentOffset, currentScale, stringColor, fontName,
			styleName, angle);

		// }
		// setDrawingColor(stringColor);
		// Rectangle2D r = null;
		// return null;
		return null;
	}

	@Override
	public void fillBackground(final Color bgColor, final double opacity) {
		setOpacity(opacity);
	}

	//
	// public void draw(final GL gl) {
	//
	// if ( listID == -1 ) {
	// createDisplayList(gl);
	// }
	// gl.glCallList(listID);
	//
	// }

	// private void createDisplayList(final GL gl) {
	// GLU glu = new GLU();
	// GLUquadric quadric = glu.gluNewQuadric();
	// listID = gl.glGenLists(1);
	// gl.glNewList(listID, GL.GL_COMPILE);
	// gl.glPushMatrix();
	// gl.glColor3d(1, 1, 1);
	// glu.gluSphere(quadric, 1 + 0.4, 30, 30);
	// gl.glPopMatrix();
	// gl.glEndList();
	// }

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
	public void beginDrawingLayer(ILayer layer) {
		super.beginDrawingLayer(layer);
		this.currentZLayer = (float) (getMaxEnvDim() * layer.getZPosition());
		Boolean refresh = layer.isDynamic();
		// If refresh: false -> draw static geometry -> currentLayerIsStatic=true
		if ( refresh != null ) {
			this.currentLayerIsStatic = !refresh;
		} else {
			this.currentLayerIsStatic = false;
		}
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
		GuiUtils.debug("JOGLAWTDisplayGraphics.beginDrawingLayer currentScale: " + currentScale);
		GuiUtils.debug("JOGLAWTDisplayGraphics.beginDrawingLayer currentOffset: " + currentOffset);
	}

	@Override
	public void drawDEM(GamaFile demFileName, GamaFile textureFileName) {
		System.out.println("drawDEM in JOGLGraphics " + demFileName.getPath() + "with " + textureFileName.getPath());
		renderer.dem.init(this.renderer.gl);// , demFileName.getPath(), textureFileName.getPath());
	}

	private double getMaxEnvDim() {
		return widthOfEnvironmentInModelUnits > heightOfEnvironmentInModelUnits ? widthOfEnvironmentInModelUnits
			: heightOfEnvironmentInModelUnits;
	}

	@Override
	public void beginHighlight() {
		renderer.turnHighlight(true);
	}

	@Override
	public void endHighlight() {
		renderer.turnHighlight(false);
	}

}
