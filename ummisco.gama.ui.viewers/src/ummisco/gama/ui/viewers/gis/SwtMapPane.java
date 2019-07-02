/*********************************************************************************************
 *
 * 'SwtMapPane.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.viewers.gis;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.Layer;
import org.geotools.map.MapBoundsEvent;
import org.geotools.map.MapBoundsListener;
import org.geotools.map.MapContent;
import org.geotools.map.MapLayerEvent;
import org.geotools.map.MapLayerListEvent;
import org.geotools.map.MapLayerListListener;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import ummisco.gama.ui.viewers.gis.geotools.styling.Utils;

/**
 * A map display pane that works with a GTRenderer to display features. It supports the use of tool classes to
 * implement, for example, mouse-controlled zooming and panning.
 * <p>
 * Rendering is performed on a background thread and is managed
 * <p>
 * Adapted from original code by Ian Turton.
 *
 * @author Andrea Antonello (www.hydrologis.com)
 * @author Michael Bedward
 * @author Ian Turton
 *
 *
 *
 * @source $URL$
 */
public class SwtMapPane extends Canvas
		implements Listener, MapLayerListListener, MapBoundsListener, MouseListener, MouseMoveListener {

	private static final PaletteData PALETTE_DATA = new PaletteData(0xFF0000, 0xFF00, 0xFF);

	/** RGB value to use as transparent color */
	private static final int TRANSPARENT_COLOR = 0x123456;

	/**
	 * This field is used to cache the full extent of the combined map layers.
	 */
	private ReferencedEnvelope fullExtent;

	MapContent content;
	private GTRenderer renderer;
	private MapLayerComposite layerTable;
	private AffineTransform worldToScreen;
	private AffineTransform screenToWorld;
	Rectangle curPaintArea;
	private BufferedImage baseImage;
	private final Point imageOrigin;
	private boolean redrawBaseImage;

	/**
	 * swt image used to draw
	 */
	private Image swtImage;
	private GC gc;
	private boolean mouseDown = false;
	private int startX;
	private int startY;
	private int endX;
	private int endY;
	private boolean isDragging = false;
	private final org.eclipse.swt.graphics.Point panePos = new org.eclipse.swt.graphics.Point(0, 0);
	boolean panning;

	private int alpha = 255;

	private final Color white;
	private final Color yellow;

	/**
	 * Constructor - creates an instance of JMapPane with the given renderer and map context.
	 *
	 * @param renderer
	 *            a renderer object
	 *
	 */
	public SwtMapPane(final Composite parent, final int style, final GTRenderer renderer, final MapContent content) {
		super(parent, style);
		white = getDisplay().getSystemColor(SWT.COLOR_WHITE);
		yellow = getDisplay().getSystemColor(SWT.COLOR_YELLOW);

		addListener(SWT.Paint, this);
		addListener(SWT.MouseDown, this);
		addListener(SWT.MouseUp, this);

		imageOrigin = new Point(0, 0);

		redrawBaseImage = true;

		setRenderer(renderer);
		setMapContent(content);

		this.addMouseListener(this);
		this.addMouseMoveListener(this);

		addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(final ControlEvent e) {
				curPaintArea = getVisibleRect();
				doSetDisplayArea(SwtMapPane.this.content.getViewport().getBounds());
			}
		});

	}

	@Override
	public void mouseMove(final MouseEvent e) {
		if (panning) {
			moveImage(e.x - panePos.x, e.y - panePos.y);
			panePos.x = e.x;
			panePos.y = e.y;
		}

		if (mouseDown) {
			endX = e.x;
			endY = e.y;
			isDragging = true;
			if (!isDisposed()) {
				redraw();
			}
		}

	}

	@Override
	public void mouseDoubleClick(final MouseEvent arg0) {}

	@Override
	public void mouseDown(final MouseEvent e) {
		panePos.x = e.x;
		panePos.y = e.y;
		panning = true;
	}

	@Override
	public void mouseUp(final MouseEvent e) {
		if (panning) {
			panning = false;
			redraw();
		}
	}

	/**
	 *
	 * @param layerTable
	 *            an instance of MapLayerTable
	 *
	 * @throws IllegalArgumentException
	 *             if layerTable is null
	 */
	public void setMapLayerTable(final MapLayerComposite layerTable) {
		if (layerTable == null) {
			throw new IllegalArgumentException("The argument must not be null"); //$NON-NLS-1$
		}

		this.layerTable = layerTable;
	}

	/**
	 * Get the renderer being used by this map pane
	 *
	 * @return live reference to the renderer being used
	 */
	public GTRenderer getRenderer() {
		return renderer;
	}

	/**
	 * Set the renderer for this map pane.
	 *
	 * @param renderer
	 *            the renderer to use
	 */
	public void setRenderer(final GTRenderer renderer) {
		if (renderer != null) {
			if (renderer instanceof StreamingRenderer) {
				if (this.content != null) {
					renderer.setMapContent(this.content);
				}

			}
		}

		this.renderer = renderer;
	}

	/**
	 * Get the map content associated with this map pane
	 *
	 * @return a live reference to the current map context
	 */
	public MapContent getMapContent() {
		return content;
	}

	/**
	 * Set the map context for this map pane to display
	 *
	 * @param content
	 *            the map context
	 */
	public void setMapContent(final MapContent content) {
		if (this.content != content) {

			if (this.content != null) {
				this.content.removeMapLayerListListener(this);
			}

			this.content = content;

			if (content != null) {
				this.content.addMapLayerListListener(this);
				this.content.addMapBoundsListener(this);

				// set all layers as selected by default for the info tool
				for (final Layer layer : content.layers()) {
					layer.setSelected(true);
				}

				setFullExtent();
			}

			if (renderer != null) {
				renderer.setMapContent(this.content);
			}

		}
	}

	/**
	 * Return a (copy of) the currently displayed map area.
	 * <p>
	 * Note, this will not always be the same as the envelope returned by . For example, when the map is displayed at
	 * the full extent of all layers will return the union of the layer bounds while this method will return an evnelope
	 * that can included extra space beyond the bounds of the layers.
	 *
	 * @return the display area in world coordinates as a new {@code ReferencedEnvelope}
	 */
	public ReferencedEnvelope getDisplayArea() {
		ReferencedEnvelope aoi = null;

		if (curPaintArea != null && screenToWorld != null) {
			final Rectangle2D awtRectangle = Utils.toAwtRectangle(curPaintArea);
			final Point2D p0 = new Point2D.Double(awtRectangle.getMinX(), awtRectangle.getMinY());
			final Point2D p1 = new Point2D.Double(awtRectangle.getMaxX(), awtRectangle.getMaxY());
			screenToWorld.transform(p0, p0);
			screenToWorld.transform(p1, p1);

			aoi = new ReferencedEnvelope(Math.min(p0.getX(), p1.getX()), Math.max(p0.getX(), p1.getX()),
					Math.min(p0.getY(), p1.getY()), Math.max(p0.getY(), p1.getY()),
					content.getCoordinateReferenceSystem());
		}

		return aoi;
	}

	public void setCrs(final CoordinateReferenceSystem crs) {
		try {
			final ReferencedEnvelope rEnv = getDisplayArea();

			final CoordinateReferenceSystem sourceCRS = rEnv.getCoordinateReferenceSystem();
			final CoordinateReferenceSystem targetCRS = crs;

			final MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
			final org.locationtech.jts.geom.Envelope newJtsEnv = JTS.transform(rEnv, transform);

			final ReferencedEnvelope newEnvelope = new ReferencedEnvelope(newJtsEnv, targetCRS);
			content.getViewport().setBounds(newEnvelope);
			fullExtent = null;
			doSetDisplayArea(newEnvelope);

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the area to display by calling the method of this pane's map context. Does nothing if has not been set. If
	 * neither the context or the envelope have coordinate reference systems defined this method does nothing.
	 * <p>
	 * The map area that ends up being displayed will often be larger than the requested display area. For instance, if
	 * the square area is requested, but the map pane's screen area is a rectangle with width greater than height, then
	 * the displayed area will be centred on the requested square but include additional area on each side.
	 * <p>
	 * You can pass any GeoAPI Envelope implementation to this method such as ReferenedEnvelope or Envelope2D.
	 * <p>
	 * Note: This method does <b>not</b> check that the requested area overlaps the bounds of the current map layers.
	 *
	 * @param envelope
	 *            the bounds of the map to display
	 *
	 * @throws IllegalStateException
	 *             if a map context is not set
	 */
	public void setDisplayArea(final Envelope envelope) {
		if (content != null) {
			if (curPaintArea == null || curPaintArea.isEmpty()) {
				return;
			} else {
				doSetDisplayArea(envelope);
				if (!isDisposed()) {
					redraw();
				}
			}

		} else {
			throw new IllegalStateException("Map context must be set before setting the display area");
		}
	}

	/**
	 * Helper method for which is also called by other methods that want to set the display area without provoking
	 * repainting of the display
	 *
	 * @param envelope
	 *            requested display area
	 */
	void doSetDisplayArea(final Envelope envelope) {
		assert content != null && curPaintArea != null && !curPaintArea.isEmpty();

		if (equalsFullExtent(envelope)) {
			setTransforms(fullExtent, curPaintArea);
		} else {
			setTransforms(envelope, curPaintArea);
		}
		final ReferencedEnvelope adjustedEnvelope = getDisplayArea();
		content.getViewport().setBounds(adjustedEnvelope);

	}

	/**
	 * Check if the envelope corresponds to full extent. It will probably not equal the full extent envelope because of
	 * slack space in the display area, so we check that at least one pair of opposite edges are equal to the full
	 * extent envelope, allowing for slack space on the other two sides.
	 * <p>
	 * Note: this method returns {@code false} if the full extent envelope is wholly within the requested envelope (e.g.
	 * user has zoomed out from full extent), only touches one edge, or touches two adjacent edges. In all these cases
	 * we assume that the user wants to maintain the slack space in the display.
	 * <p>
	 * This method is part of the work-around that the map pane needs because of the differences in how raster and
	 * vector layers are treated by the renderer classes.
	 *
	 * @param envelope
	 *            a pending display envelope to compare to the full extent envelope
	 *
	 * @return true if the envelope is coincident with the full extent evenlope on at least two edges; false otherwise
	 *
	 * @todo My logic here seems overly complex - I'm sure there must be a simpler way for the map pane to handle this.
	 */
	private boolean equalsFullExtent(final Envelope envelope) {
		if (fullExtent == null || envelope == null) { return false; }

		final double TOL = 1.0e-6d * (fullExtent.getWidth() + fullExtent.getHeight());

		boolean touch = false;
		if (Math.abs(envelope.getMinimum(0) - fullExtent.getMinimum(0)) < TOL) {
			touch = true;
		}
		if (Math.abs(envelope.getMaximum(0) - fullExtent.getMaximum(0)) < TOL) {
			if (touch) { return true; }
		}
		if (Math.abs(envelope.getMinimum(1) - fullExtent.getMinimum(1)) < TOL) {
			touch = true;
		}
		if (Math.abs(envelope.getMaximum(1) - fullExtent.getMaximum(1)) < TOL) {
			if (touch) { return true; }
		}

		return false;
	}

	/**
	 * Reset the map area to include the full extent of all layers and redraw the display
	 */
	public void reset() {
		if (fullExtent == null) {
			setFullExtent();
		}
		try {
			fullExtent = new ReferencedEnvelope(CRS.transform(fullExtent, content.getCoordinateReferenceSystem()));
		} catch (final Exception e) {
			e.printStackTrace();
		}
		setDisplayArea(fullExtent);
	}

	/**
	 * Retrieve the map pane's current base image.
	 * <p>
	 * The map pane caches the most recent rendering of map layers as an image to avoid time-consuming rendering
	 * requests whenever possible. The base image will be re-drawn whenever there is a change to map layer data, style
	 * or visibility; and it will be replaced by a new image when the pane is resized.
	 * <p>
	 * This method returns a <b>live</b> reference to the current base image. Use with caution.
	 *
	 * @return a live reference to the current base image
	 */
	public RenderedImage getBaseImage() {
		return this.baseImage;
	}

	/**
	 * Get a (copy of) the screen to world coordinate transform being used by this map pane.
	 *
	 * @return a copy of the screen to world coordinate transform
	 */
	public AffineTransform getScreenToWorldTransform() {
		if (screenToWorld != null) {
			return new AffineTransform(screenToWorld);
		} else {
			return null;
		}
	}

	/**
	 * Get a (copy of) the world to screen coordinate transform being used by this map pane. This method can be used to
	 * determine the current drawing scale...
	 *
	 * <pre>
	 *
	 * {
	 * 	&#64;code double scale = mapPane.getWorldToScreenTransform().getScaleX();
	 * }
	 * </pre>
	 *
	 * @return a copy of the world to screen coordinate transform
	 */
	public AffineTransform getWorldToScreenTransform() {
		if (worldToScreen != null) {
			return new AffineTransform(worldToScreen);
		} else {
			return null;
		}
	}

	/**
	 * Move the image currently displayed by the map pane from its current origin (x,y) to (x+dx, y+dy). This method
	 * allows dragging the map without the overhead of redrawing the features during the drag. For example, it is used
	 * by {@link org.geotools.swing.tool.PanTool}.
	 *
	 * @param dx
	 *            the x offset in pixels
	 * @param dy
	 *            the y offset in pixels.
	 */
	public void moveImage(final int dx, final int dy) {
		imageOrigin.translate(dx, dy);
		redrawBaseImage = false;
		if (!isDisposed()) {
			redraw();
		}
	}

	/**
	 * Called after the base image has been dragged. Sets the new map area and transforms
	 *
	 * @param env
	 *            the display area (world coordinates) prior to the image being moved
	 * @param paintArea
	 *            the current drawing area (screen units)
	 */
	private void afterImageMove() {
		final ReferencedEnvelope env = content.getViewport().getBounds();
		if (env == null) { return; }
		final int dx = imageOrigin.x;
		final int dy = imageOrigin.y;
		final DirectPosition2D newPos = new DirectPosition2D(dx, dy);
		screenToWorld.transform(newPos, newPos);

		env.translate(env.getMinimum(0) - newPos.x, env.getMaximum(1) - newPos.y);
		doSetDisplayArea(env);
		imageOrigin.setLocation(0, 0);
		redrawBaseImage = true;
	}

	/**
	 * Called when a new map layer has been added. Sets the layer as selected (for queries) and, if the layer table is
	 * being used, adds the new layer to the table.
	 */
	@Override
	public void layerAdded(final MapLayerListEvent event) {
		final Layer layer = event.getElement();
		if (layerTable != null) {
			layerTable.onAddLayer(layer);
		}
		layer.setSelected(true);
		redrawBaseImage = true;

		final boolean atFullExtent = equalsFullExtent(getDisplayArea());
		final boolean firstLayer = content.layers().size() == 1;
		if (firstLayer || atFullExtent) {
			reset();
			if (firstLayer) {
				setCrs(layer.getBounds().getCoordinateReferenceSystem());
				return;
			}
		}
		if (!isDisposed()) {
			redraw();
		}
	}

	/**
	 * Called when a map layer has been removed
	 */
	@Override
	public void layerRemoved(final MapLayerListEvent event) {
		final Layer layer = event.getElement();
		if (layerTable != null) {
			layerTable.onRemoveLayer(layer);
		}
		redrawBaseImage = true;

		if (content.layers().size() == 0) {
			clearFields();
		} else {
			setFullExtent();
		}
		if (!isDisposed()) {
			redraw();
		}
	}

	/**
	 * Called when a map layer has changed, e.g. features added to a displayed feature collection
	 */
	@Override
	public void layerChanged(final MapLayerListEvent event) {
		if (layerTable != null) {
			layerTable.repaint(event.getElement());
		}
		redrawBaseImage = true;

		final int reason = event.getMapLayerEvent().getReason();

		if (reason == MapLayerEvent.DATA_CHANGED) {
			setFullExtent();
		}

		if (reason != MapLayerEvent.SELECTION_CHANGED) {
			if (!isDisposed()) {
				redraw();
			}
		}
	}

	/**
	 * Called when the bounds of a map layer have changed
	 */
	@Override
	public void layerMoved(final MapLayerListEvent event) {
		redrawBaseImage = true;
		if (!isDisposed()) {
			redraw();
		}
	}

	/**
	 * Called by the map context when its bounds have changed. Used here to watch for a changed CRS, in which case the
	 * map is redisplayed at (new) full extent.
	 */
	@Override
	public void mapBoundsChanged(final MapBoundsEvent event) {
		redrawBaseImage = true;
		final int type = event.getType();
		if ((type & MapBoundsEvent.COORDINATE_SYSTEM_MASK) != 0) {
			/*
			 * The coordinate reference system has changed. Set the map to display the full extent of layer bounds to
			 * avoid the effect of a shrinking map
			 */
			setFullExtent();
			reset();
		}
	}

	/**
	 * Gets the full extent of map context's layers. The only reason this method is defined is to avoid having try-catch
	 * blocks all through other methods.
	 */
	private void setFullExtent() {
		if (content != null && content.layers().size() > 0) {
			try {

				fullExtent = content.getMaxBounds();

				/*
				 * Guard agains degenerate envelopes (e.g. empty map layer or single point feature)
				 */
				if (fullExtent == null) {
					// set arbitrary bounds centred on 0,0
					fullExtent = worldEnvelope();// new ReferencedEnvelope(-1,
													// 1, -1, 1,
													// context.getCoordinateReferenceSystem());

				}

			} catch (final Exception ex) {
				throw new IllegalStateException(ex);
			}
		} else {
			fullExtent = null;
		}
	}

	/**
	 * Calculate the affine transforms used to convert between world and pixel coordinates. The calculations here are
	 * very basic and assume a cartesian reference system.
	 * <p>
	 * Tne transform is calculated such that {@code envelope} will be centred in the display
	 *
	 * @param envelope
	 *            the current map extent (world coordinates)
	 * @param paintArea
	 *            the current map pane extent (screen units)
	 */
	private void setTransforms(final Envelope envelope, final Rectangle paintArea) {
		ReferencedEnvelope refEnv = null;
		if (envelope != null) {
			refEnv = new ReferencedEnvelope(envelope);
		} else {
			refEnv = worldEnvelope();
			// FIXME
			// content.setCoordinateReferenceSystem(DefaultGeographicCRS.WGS84);
		}

		final java.awt.Rectangle awtPaintArea = Utils.toAwtRectangle(paintArea);
		final double xscale = awtPaintArea.getWidth() / refEnv.getWidth();
		final double yscale = awtPaintArea.getHeight() / refEnv.getHeight();

		final double scale = Math.min(xscale, yscale);

		final double xoff = refEnv.getMedian(0) * scale - awtPaintArea.getCenterX();
		final double yoff = refEnv.getMedian(1) * scale + awtPaintArea.getCenterY();

		worldToScreen = new AffineTransform(scale, 0, 0, -scale, -xoff, yoff);
		try {
			screenToWorld = worldToScreen.createInverse();

		} catch (final NoninvertibleTransformException ex) {
			ex.printStackTrace();
		}
	}

	private ReferencedEnvelope worldEnvelope() {
		return new ReferencedEnvelope(-180, 180, -90, 90, DefaultGeographicCRS.WGS84);
	}

	/**
	 * This method is called if all layers are removed from the context.
	 */
	private void clearFields() {
		fullExtent = null;
		worldToScreen = null;
		screenToWorld = null;
	}

	public Rectangle getVisibleRect() {
		return getClientArea();
	}

	/**
	 * Sets the transparency value for the base image (overlays not considered).
	 *
	 * @param alpha
	 *            the transparency value (0 - 255).
	 */
	public void setBaseImageAlpha(final int alpha) {
		this.alpha = alpha;
	}

	@Override
	@SuppressWarnings ("deprecation")
	public void handleEvent(final Event event) {

		curPaintArea = getVisibleRect();

		// DEBUG.LOG("event: " + event.type);
		if (event.type == SWT.MouseDown) {
			startX = event.x;
			startY = event.y;
			// start mouse activity
			mouseDown = true;
		} else if (event.type == SWT.MouseUp) {
			endX = event.x;
			endY = event.y;

			final boolean mouseWasMoved = startX != endX || startY != endY;
			if (mouseWasMoved) {
				// if the tool is able to move draw the moved image
				afterImageMove();
			}
			// stop mouse activity
			mouseDown = false;
			isDragging = false;
		} else if (event.type == SWT.Paint) {
			// DEBUG.LOG("PAINT CALLED (DOESN'T MEAN I'M DRAWING)");

			gc = event.gc;

			/*
			 * if the mouse is dragging and the current tool can move the map we just draw what we already have on white
			 * background. At the end of the moving we will take care of adding the missing pieces.
			 */
			if (isDragging) {
				// DEBUG.LOG("toolCanMove && isDragging");
				if (gc != null && !gc.isDisposed() && swtImage != null) {
					/*
					 * double buffer necessary, since the SWT.NO_BACKGROUND needed by the canvas to properly draw
					 * background, doesn't clean the parts outside the bounds of the moving panned image, giving a
					 * spilling image effect.
					 */
					final Image tmpImage = new Image(getDisplay(), curPaintArea.width, curPaintArea.height);
					final GC tmpGc = new GC(tmpImage);
					tmpGc.setBackground(white);
					tmpGc.fillRectangle(0, 0, curPaintArea.width, curPaintArea.height);
					tmpGc.drawImage(swtImage, imageOrigin.x, imageOrigin.y);
					gc.drawImage(tmpImage, 0, 0);
					tmpImage.dispose();
				}
				return;
			}

			if (curPaintArea == null || content == null || renderer == null) { return; }

			if (content.layers().size() == 0) {
				// if no layers available, return only if there are also no
				// overlays

				gc.setForeground(yellow);
				gc.fillRectangle(0, 0, curPaintArea.width + 1, curPaintArea.height + 1);

			}

			final ReferencedEnvelope mapAOI = content.getViewport().getBounds();
			if (mapAOI == null) { return; }

			if (redrawBaseImage) {

				baseImage =
						new BufferedImage(curPaintArea.width + 1, curPaintArea.height + 1, BufferedImage.TYPE_INT_ARGB);
				final Graphics2D g2d = baseImage.createGraphics();
				g2d.fillRect(0, 0, curPaintArea.width + 1, curPaintArea.height + 1);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				// renderer.setContext(context);
				final java.awt.Rectangle awtRectangle = Utils.toAwtRectangle(curPaintArea);
				renderer.paint(g2d, awtRectangle, mapAOI, getWorldToScreenTransform());
				// swtImage.dispose();

				if (swtImage != null && !swtImage.isDisposed()) {
					swtImage.dispose();
					swtImage = null;
				}
				swtImage =
						new Image(getDisplay(), awtToSwt(baseImage, curPaintArea.width + 1, curPaintArea.height + 1));
			}

			if (swtImage != null) {
				drawFinalImage(swtImage);
			}

			redrawBaseImage = false;
		}
	}

	private void drawFinalImage(final Image swtImage) {
		final Image tmpImage = new Image(getDisplay(), curPaintArea.width, curPaintArea.height);
		final GC tmpGc = new GC(tmpImage);
		tmpGc.setBackground(white);
		tmpGc.fillRectangle(0, 0, curPaintArea.width, curPaintArea.height);
		if (swtImage != null) {
			tmpGc.setAlpha(alpha);
			tmpGc.drawImage(swtImage, imageOrigin.x, imageOrigin.y);
		}
		if (gc != null && !gc.isDisposed()) {
			gc.drawImage(tmpImage, imageOrigin.x, imageOrigin.y);
		}
		tmpGc.dispose();
		tmpImage.dispose();
	}

	/**
	 * Transform a java2d bufferedimage to a swt image.
	 *
	 * @param bufferedImage
	 *            the image to trasform.
	 * @param width
	 *            the image width.
	 * @param height
	 *            the image height.
	 * @return swt image.
	 */
	private ImageData awtToSwt(final BufferedImage bufferedImage, final int width, final int height) {
		final int[] awtPixels = new int[width * height];
		final ImageData swtImageData = new ImageData(width, height, 24, PALETTE_DATA);
		swtImageData.transparentPixel = TRANSPARENT_COLOR;
		final int step = swtImageData.depth / 8;
		final byte[] data = swtImageData.data;
		bufferedImage.getRGB(0, 0, width, height, awtPixels, 0, width);
		for (int i = 0; i < height; i++) {
			int idx = (0 + i) * swtImageData.bytesPerLine + 0 * step;
			for (int j = 0; j < width; j++) {
				final int rgb = awtPixels[j + i * width];
				for (int k = swtImageData.depth - 8; k >= 0; k -= 8) {
					data[idx++] = (byte) (rgb >> k & 0xFF);
				}
			}
		}

		return swtImageData;
	}

	@Override
	public void layerPreDispose(final MapLayerListEvent event) {}

}
