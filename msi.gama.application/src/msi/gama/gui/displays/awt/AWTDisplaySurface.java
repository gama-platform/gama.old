/*********************************************************************************************
 * 
 * 
 * 'AWTDisplaySurface.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.displays.awt;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.SwingUtilities;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.outputs.display.AWTDisplayGraphics;
import msi.gama.precompiler.GamlAnnotations.display;
import msi.gama.runtime.*;
import msi.gaml.operators.Cast;

@display("java2D")
public final class AWTDisplaySurface extends AbstractAWTDisplaySurface {

	private Point snapshotDimension, mousePosition;
	private BufferedImage buffImage;

	private class DisplayMouseListener extends MouseAdapter {

		boolean dragging;

		@Override
		public void mouseDragged(final MouseEvent e) {
			if ( SwingUtilities.isLeftMouseButton(e) ) {
				dragging = true;
				canBeUpdated(false);
				final Point p = e.getPoint();
				if ( mousePosition == null ) {
					mousePosition = new Point(getWidth() / 2, getHeight() / 2);
				}
				setOrigin(origin.x + p.x - mousePosition.x, origin.y + p.y - mousePosition.y);
				mousePosition = p;
				repaint();
			}
		}

		@Override
		public void mouseMoved(final MouseEvent e) {
			// we need the mouse position so that after zooming
			// that position of the image is maintained
			mousePosition = e.getPoint();
		}

		@Override
		public void mouseWheelMoved(final MouseWheelEvent e) {
			final boolean zoomIn = e.getWheelRotation() < 0;
			mousePosition = e.getPoint();
			Point p = new Point(mousePosition.x, mousePosition.y);
			double zoomFactor = applyZoom(zoomIn ? 1.0 + zoomIncrement : 1.0 - zoomIncrement);
			double newx = Math.round(zoomFactor * (p.x - origin.x) - p.x + getWidth() / 2);
			double newy = Math.round(zoomFactor * (p.y - origin.y) - p.y + getHeight() / 2);
			centerOnDisplayCoordinates(new Point((int) newx, (int) newy));
			updateDisplay();
		}

		@Override
		public void mouseClicked(final MouseEvent evt) {
			if ( evt.getClickCount() == 2 ) {
				zoomFit();
			} else if ( evt.isControlDown() || evt.isMetaDown() || evt.isPopupTrigger() ) {
				selectAgents(evt.getX(), evt.getY());
			}
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			if ( dragging ) {
				updateDisplay();
				dragging = false;
			}
			canBeUpdated(true);
		}

	}

	@Override
	public void updateDisplay() {
		super.updateDisplay();
		// EXPERIMENTAL

		if ( temp_focus != null ) {
			IShape geometry = Cast.asGeometry(scope, temp_focus.value(scope));
			if ( geometry != null ) {
				Rectangle2D r = this.getManager().focusOn(geometry, this);
				System.out.println("Rectangle = " + r);
				if ( r == null ) { return; }
				double xScale = getWidth() / r.getWidth();
				double yScale = getHeight() / r.getHeight();
				double zoomFactor = Math.min(xScale, yScale);
				Point center = new Point((int) Math.round(r.getCenterX()), (int) Math.round(r.getCenterY()));

				zoomFactor = applyZoom(zoomFactor);
				center.setLocation(center.x * zoomFactor, center.y * zoomFactor);
				centerOnDisplayCoordinates(center);
			}
			temp_focus = null;
			// canBeUpdated(true);
			super.updateDisplay();
			// canBeUpdated(false);
		}

		// EXPERIMENTAL
	}

	@Override
	public void zoomIn() {
		mousePosition = new Point(getWidth() / 2, getHeight() / 2);
		double zoomFactor = applyZoom(1.0 + zoomIncrement);
		double newx = Math.round(zoomFactor * (getWidth() / 2 - origin.x));
		double newy = Math.round(zoomFactor * (getHeight() / 2 - origin.y));
		centerOnDisplayCoordinates(new Point((int) newx, (int) newy));
		updateDisplay();
	}

	@Override
	public void zoomOut() {
		mousePosition = new Point(getWidth() / 2, getHeight() / 2);
		double oldx = Math.round(getWidth() / 2d - origin.x);
		double oldy = Math.round(getHeight() / 2d - origin.y);
		double zoomFactor = applyZoom(1.0 - zoomIncrement);

		// setOrigin((int) (origin.x + origin.x * (1 - zoomFactor) / 2),
		// (int) (origin.y + origin.y * (1 - zoomFactor) / 2));
		double newx = Math.round(zoomFactor * (getWidth() / 2 - origin.x));
		double newy = Math.round(zoomFactor * (getHeight() / 2 - origin.y));
		// jd
		// Point center = new Point((int) newx, (int) newy);
		// center.setLocation(center.x * zoomFactor, center.y * zoomFactor);

		centerOnDisplayCoordinates(new Point((int) newx, (int) newy));
		updateDisplay();
	}

	public AWTDisplaySurface(final Object ... args) {
		displayBlock = new Runnable() {

			@Override
			public void run() {
				// System.err.println("Display surface entering displayBlock");
				canBeUpdated(false);
				drawDisplaysWithoutRepainting();
				repaint();
				canBeUpdated(true);
				// System.err.println("Display surface leaving displayBlock");
			}
		};
	}

	@Override
	protected void createIGraphics() {
		iGraphics = new AWTDisplayGraphics(this, buffImage.createGraphics());
		iGraphics.setQualityRendering(qualityRendering);
	}

	@Override
	protected void createNewImage(final int width, final int height) {

		final BufferedImage newImage = ImageUtils.createCompatibleImage(width, height);
		if ( buffImage != null ) {
			newImage.getGraphics().drawImage(buffImage, 0, 0, width, height, null);
			buffImage.flush();
		}
		buffImage = newImage;
		super.createNewImage(width, height);
	}

	@Override
	public void initialize(final IScope scope, final double env_width, final double env_height,
		final LayeredDisplayOutput layerDisplayOutput) {
		super.initialize(scope, env_width, env_height, layerDisplayOutput);
		final DisplayMouseListener d = new DisplayMouseListener();
		addMouseListener(d);
		addMouseMotionListener(d);
		addMouseWheelListener(d);
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(final ComponentEvent e) {
				// System.out.println("Display surface entering a resize event =" + e);
				if ( buffImage == null || zoomFit ) {
					zoomFit();
				} else {
					if ( isFullImageInPanel() ) {
						centerImage();
					} else if ( isImageEdgeInPanel() ) {
						scaleOrigin();
					}
					updateDisplay();
				}
				// System.out.println("Display surface leaving a resize event");
				final double newZoom =
					Math.min(getWidth() / (double) getDisplayWidth(), getHeight() / (double) getDisplayHeight());
				setZoomLevel(1 / newZoom);
				previousPanelSize = getSize();
			}
		});
		// OutputSynchronizer.decInitializingViews(getOutputName());
	}

	@Override
	public void outputChanged(final IScope scope, final double env_width, final double env_height,
		final LayeredDisplayOutput output) {
		super.outputChanged(scope, env_width, env_height, output);
		bgColor = output.getBackgroundColor();
		this.setBackground(bgColor);
		repaint();
	}

	@Override
	public int[] computeBoundsFrom(final int vwidth, final int vheight) {
		// GuiUtils.debug("AWTDisplaySurface.computeBoundsFrom " + vwidth + " " + vheight);
		if ( !manager.stayProportional() ) { return new int[] { vwidth, vheight }; }
		final int[] dim = new int[2];
		if ( widthHeightConstraint < 1 ) {
			dim[1] = Math.min(vheight, (int) Math.round(vwidth * widthHeightConstraint));
			dim[0] = Math.min(vwidth, (int) Math.round(dim[1] / widthHeightConstraint));
		} else {
			dim[0] = Math.min(vwidth, (int) Math.round(vheight / widthHeightConstraint));
			dim[1] = Math.min(vheight, (int) Math.round(dim[0] * widthHeightConstraint));
		}
		return dim;
	}

	public void selectAgents(final int mousex, final int mousey) {
		final int xc = mousex - origin.x;
		final int yc = mousey - origin.y;
		final List<ILayer> displays = manager.getLayersIntersecting(xc, yc);

		GuiUtils.run(new Runnable() {

			@Override
			public void run() {
				menuManager.buildMenu(mousex, mousey, xc, yc, displays);
			}
		});
	}

	@Override
	public GamaPoint getModelCoordinates() {
		if ( mousePosition == null ) { return null; }
		final int xc = mousePosition.x - origin.x;
		final int yc = mousePosition.y - origin.y;
		List<ILayer> layers = manager.getLayersIntersecting(xc, yc);
		if ( layers.isEmpty() ) { return null; }
		return layers.get(0).getModelCoordinatesFrom(xc, yc, this);
	}

	@Override
	public void forceUpdateDisplay() {
		final boolean old = synchronous;
		setSynchronized(false);
		canBeUpdated(true);
		updateDisplay();
		setSynchronized(old);
	}

	public void drawDisplaysWithoutRepainting() {
		if ( iGraphics == null ) { return; }
		iGraphics.fillBackground(bgColor, 1);
		manager.drawLayersOn(iGraphics);
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		((Graphics2D) g).drawRenderedImage(buffImage, translation);
		if ( autosave ) {
			snapshot();
		}
	}

	@Override
	public void dispose() {
		if ( manager != null ) {
			manager.dispose();
		}
		if ( buffImage != null ) {
			buffImage.flush();
		}
		GAMA.releaseScope(scope);
		scope = null;
	}

	@Override
	public void focusOn(final IShape geometry) {
		Rectangle2D r = this.getManager().focusOn(geometry, this);
		System.out.println("Rectangle = " + r);
		if ( r == null ) { return; }
		double xScale = getWidth() / r.getWidth();
		double yScale = getHeight() / r.getHeight();
		double zoomFactor = Math.min(xScale, yScale);
		Point center = new Point((int) Math.round(r.getCenterX()), (int) Math.round(r.getCenterY()));

		zoomFactor = applyZoom(zoomFactor);
		center.setLocation(center.x * zoomFactor, center.y * zoomFactor);
		centerOnDisplayCoordinates(center);

		updateDisplay();
	}

	public void centerOnViewCoordinates(final Point p) {
		int translationX = p.x - Math.round(getWidth() / (float) 2);
		int translationY = p.y - Math.round(getHeight() / (float) 2);
		setOrigin(origin.x - translationX, origin.y - translationY);

	}

	public void centerOnDisplayCoordinates(final Point p) {
		centerOnViewCoordinates(new Point(p.x + origin.x, p.y + origin.y));
	}

	public double applyZoom(final double factor) {
		double real_factor = Math.min(factor, 10 / zoomLevel);
		boolean success = false;

		try {
			success =
				resizeImage(Math.max(1, (int) Math.round(getDisplayWidth() * real_factor)),
					Math.max(1, (int) Math.round(getDisplayHeight() * real_factor)));
		} catch (Exception e) {
			// System.gc();
			// GuiUtils.debug("AWTDisplaySurface.applyZoom: not enough memory available to zoom at :" + real_factor);
			real_factor = MAX_ZOOM_FACTOR;
			try {
				success =
					resizeImage(Math.max(1, (int) Math.round(getDisplayWidth() * real_factor)),
						Math.max(1, (int) Math.round(getDisplayHeight() * real_factor)));
			} catch (Exception e1) {
				// GuiUtils.debug("AWTDisplaySurface.applyZoom : not enough memory available to zoom at :" +
				// real_factor);
				real_factor = 1;
				success = true;
			} catch (Error e1) {
				// GuiUtils.debug("AWTDisplaySurface.applyZoom : not enough memory available to zoom at :" +
				// real_factor);
				real_factor = 1;
				success = true;
			}
		} catch (Error e) {
			System.gc();
			// GuiUtils.debug("AWTDisplaySurface.applyZoom: not enough memory available to zoom at :" + real_factor);
			real_factor = MAX_ZOOM_FACTOR;
			try {
				success =
					resizeImage(Math.max(1, (int) Math.round(getDisplayWidth() * real_factor)),
						Math.max(1, (int) Math.round(getDisplayHeight() * real_factor)));
			} catch (Exception e1) {
				// GuiUtils.debug("AWTDisplaySurface.applyZoom : not enough memory available to zoom at :" +
				// real_factor);
				real_factor = 1;
				success = true;
			} catch (Error e1) {
				// GuiUtils.debug("AWTDisplaySurface.applyZoom : not enough memory available to zoom at :" +
				// real_factor);
				real_factor = 1;
				success = true;
			}
		}

		if ( success ) {
			zoomFit = false;
			if ( widthHeightConstraint < 1 ) {
				setZoomLevel((double) getDisplayWidth() / (double) getWidth());
			} else {
				setZoomLevel((double) getDisplayHeight() / (double) getHeight());
			}
		}
		return real_factor;
	}

	@Override
	public void zoomFit() {
		mousePosition = new Point(getWidth() / 2, getHeight() / 2);
		if ( resizeImage(getWidth(), getHeight()) ) {
			super.zoomFit();
			centerImage();
			updateDisplay();
		}
	}

	Point getScreenCoordinatesFrom(final double x, final double y) {
		final double xFactor = x / getEnvWidth();
		final double yFactor = y / getEnvHeight();
		final int xOnDisplay = (int) (xFactor * getWidth());
		final int yOnDisplay = (int) (yFactor * getHeight());
		return new Point(xOnDisplay, yOnDisplay);
	}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#setAutoSave(boolean)
	 */
	@Override
	public void setAutoSave(final boolean autosave, final int x, final int y) {
		super.setAutoSave(autosave, x, y);
		snapshotDimension = new Point(x, y);
	}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#snapshot()
	 */
	@Override
	public void snapshot() {
		if ( snapshotDimension.x == -1 && snapshotDimension.y == -1 ) {
			super.snapshot();
			return;
		}

		final BufferedImage newImage = ImageUtils.createCompatibleImage(snapshotDimension.x, snapshotDimension.y);
		final IGraphics tempGraphics = new AWTDisplayGraphics(this, (Graphics2D) newImage.getGraphics());
		tempGraphics.fillBackground(bgColor, 1);
		manager.drawLayersOn(tempGraphics);
		save(scope, newImage);
		newImage.flush();

	}

	/**
	 * These methods do nothing yet for JAVA2D display
	 */
	@Override
	public void initOutput3D(final boolean output3d, final ILocation output3dNbCycles) {}

	/**
	 * Method followAgent()
	 * @see msi.gama.common.interfaces.IDisplaySurface#followAgent(msi.gama.metamodel.agent.IAgent)
	 */
	@Override
	public void followAgent(final IAgent a) {}

	/**
	 * Method computeInitialZoomLevel()
	 * @see msi.gama.gui.displays.awt.AbstractAWTDisplaySurface#computeInitialZoomLevel()
	 */
	@Override
	protected Double computeInitialZoomLevel() {
		return 1.0;
	}

	@Override
	public void setBounds(final int arg0, final int arg1, final int arg2, final int arg3) {
		// GuiUtils.debug("Set bounds called with " + arg2 + " " + arg3);
		if ( arg2 == 0 && arg3 == 0 ) { return; }
		super.setBounds(arg0, arg1, arg2, arg3);
	}

	@Override
	public void setBounds(final Rectangle r) {
		// GuiUtils.debug("Set bounds called with " + r);
		if ( r.width < 1 && r.height < 1 ) { return; }
		super.setBounds(r);
	}

}
