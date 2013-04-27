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
package msi.gama.gui.displays.awt;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.SwingUtilities;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.*;
import msi.gama.gui.displays.layers.LayerManager;
import msi.gama.metamodel.shape.*;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.layers.ILayerStatement;
import msi.gama.precompiler.GamlAnnotations.display;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.ISymbol;
import com.vividsolutions.jts.geom.Envelope;

@display("java2D")
public final class AWTDisplaySurface extends AbstractDisplaySurface {

	private Point snapshotDimension;
	private Point mousePosition;
	private volatile boolean isPainting;
	private final Thread animationThread = new Thread(new Runnable() {

		@Override
		public void run() {
			boolean doIt = true;
			while (doIt) {
				try {
					paintingNeeded.acquire();
				} catch (InterruptedException e) {
					doIt = false;
				}
				if ( doIt ) {
					// GuiUtils.debug("AWTDisplaySurface.animationThread repaint");
					repaint(true);
				}
			}
		}
	});

	private AWTDisplaySurfaceMenu menuManager;

	private class DisplayMouseListener extends MouseAdapter {

		boolean dragging;

		@Override
		public void mouseDragged(final MouseEvent e) {
			if ( SwingUtilities.isLeftMouseButton(e) ) {
				dragging = true;
				canBeUpdated(false);
				Point p = e.getPoint();
				if ( mousePosition == null ) {
					mousePosition = new Point(getWidth() / 2, getHeight() / 2);
				}
				setOrigin(origin.x + p.x - mousePosition.x, origin.y + p.y - mousePosition.y);
				mousePosition = p;
				repaint(true);
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
			boolean zoomIn = e.getWheelRotation() < 0;
			mousePosition = e.getPoint();
			setZoom(zoomIn ? 1.0 + zoomIncrement : 1.0 - zoomIncrement, mousePosition);
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

	public AWTDisplaySurface() {
		displayBlock = new Runnable() {

			@Override
			public void run() {
				if ( !canBeUpdated() ) { return; }
				canBeUpdated(false);
				drawDisplaysWithoutRepainting();
				paintingNeeded.release();
				canBeUpdated(true);
				Toolkit.getDefaultToolkit().sync();
			}

		};
	}

	@Override
	public void initialize(final double env_width, final double env_height, final IDisplayOutput layerDisplayOutput) {
		setOutputName(layerDisplayOutput.getName());
		outputChanged(env_width, env_height, layerDisplayOutput);
		setOpaque(true);
		setDoubleBuffered(false);
		setCursor(createCursor());
		menuManager = new AWTDisplaySurfaceMenu(this);
		DisplayMouseListener d = new DisplayMouseListener();
		addMouseListener(d);
		addMouseMotionListener(d);
		addMouseWheelListener(d);
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(final ComponentEvent e) {
				if ( buffImage == null ) {
					zoomFit();
				} else {
					if ( isFullImageInPanel() ) {
						centerImage();
					} else if ( isImageEdgeInPanel() ) {
						scaleOrigin();
					} else {
						getIGraphics().setClipping(getImageClipBounds());
					}
				}
				updateDisplay();
				previousPanelSize = getSize();
			}
		});
		animationThread.start();
		// GuiOutputManager.decInitializingViews(outputName);
	}

	@Override
	public void outputChanged(final double env_width, final double env_height, final IDisplayOutput output) {
		GuiUtils.debug("AWTDisplaySurface.outputChanged");
		bgColor = output.getBackgroundColor();
		this.setBackground(bgColor);
		widthHeightConstraint = env_height / env_width;
		if ( manager == null ) {
			manager = new LayerManager(this);
			final List<? extends ISymbol> layers = output.getChildren();
			for ( final ISymbol layer : layers ) {
				manager.addLayer(LayerManager.createLayer((ILayerStatement) layer, env_width, env_height,
					getIGraphics()));
			}

		} else {
			manager.updateEnvDimensions(env_width, env_height);
		}
		paintingNeeded.release();
	}

	@Override
	public int[] computeBoundsFrom(final int vwidth, final int vheight) {
		if ( !manager.stayProportional() ) { return new int[] { vwidth, vheight }; }
		int[] dim = new int[2];
		if ( widthHeightConstraint < 1 ) {
			dim[1] = Math.min(vheight, (int) (vwidth * widthHeightConstraint));
			dim[0] = Math.min(vwidth, (int) (dim[1] / widthHeightConstraint));
		} else {
			dim[0] = Math.min(vwidth, (int) (vheight / widthHeightConstraint));
			dim[1] = Math.min(vheight, (int) (dim[0] * widthHeightConstraint));
		}
		return dim;
	}

	public void selectAgents(final int mousex, final int mousey) {
		int xc = mousex - origin.x;
		int yc = mousey - origin.y;
		final List<ILayer> displays = manager.getLayersIntersecting(xc, yc);
		menuManager.selectAgents(mousex, mousey, xc, yc, displays);
	}

	@Override
	public void forceUpdateDisplay() {
		boolean old = synchronous;
		setSynchronized(false);
		updateDisplay();
		setSynchronized(old);
	}

	public void drawDisplaysWithoutRepainting() {
		if ( getIGraphics() == null ) { return; }
		ex[0] = null;
		getIGraphics().fill(bgColor, 1);
		manager.drawLayersOn(getIGraphics());
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		((Graphics2D) g).drawRenderedImage(buffImage, translation);
		if ( autosave ) {
			snapshot();
		}
		redrawNavigator();
	}

	@Override
	public void dispose() {
		// GuiUtils.debug("AWTDisplaySurface.dispose: " + outputName);
		// javax.swing.SwingUtilities.invokeLater(new Runnable() {
		//
		// @Override
		// public void run() {
		// removeAll();
		// }
		// });

		if ( manager != null ) {
			manager.dispose();
		}

		animationThread.interrupt();
		// try {
		// animationThread.join();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		buffImage.flush();
		if ( navigator == null || navigator.isDisposed() ) { return; }
		navigator.dispose();
	}

	@Override
	public BufferedImage getImage() {
		return buffImage;
	}

	@Override
	public void zoomIn() {
		mousePosition = new Point(origin.x + bWidth / 2, origin.y + bHeight / 2);
		setZoom(1.0 + zoomIncrement, mousePosition);

	}

	@Override
	public void zoomOut() {
		mousePosition = new Point(origin.x + bWidth / 2, origin.y + bHeight / 2);;
		setZoom(1.0 - zoomIncrement, mousePosition);

	}

	public void setZoom(final double factor, final Point c) {
		if ( resizeImage(Math.max(1, (int) Math.round(bWidth * factor)),
			Math.max(1, (int) Math.round(bHeight * factor))) ) {
			int imagePX = c.x < origin.x ? 0 : c.x >= bWidth + origin.x ? bWidth - 1 : c.x - origin.x;
			int imagePY = c.y < origin.y ? 0 : c.y >= bHeight + origin.y ? bHeight - 1 : c.y - origin.y;
			zoomFactor = factor;
			setOrigin(c.x - (int) Math.round(imagePX * zoomFactor), c.y - (int) Math.round(imagePY * zoomFactor));
			updateDisplay();
		}
	}

	@Override
	public void zoomFit() {
		mousePosition = new Point(getWidth() / 2, getHeight() / 2);
		if ( resizeImage(getWidth(), getHeight()) ) {
			centerImage();
			updateDisplay();
		}
	}

	@Override
	public void focusOn(final IShape geometry, final ILayer display) {
		Envelope env = geometry.getEnvelope();
		double minX = env.getMinX();
		double minY = env.getMinY();
		double maxX = env.getMaxX();
		double maxY = env.getMaxY();

		int leftX = display.getPosition().x + (int) (display.getXScale() * minX + 0.5);
		int leftY = display.getPosition().y + (int) (display.getYScale() * minY + 0.5);
		int rightX = display.getPosition().x + (int) (display.getXScale() * maxX + 0.5);
		int rightY = display.getPosition().y + (int) (display.getYScale() * maxY + 0.5);
		Rectangle envelop = new Rectangle(leftX + origin.x, leftY + origin.y, rightX - leftX, rightY - leftY);
		double xScale = (double) getWidth() / (rightX - leftX);
		double yScale = (double) getHeight() / (rightY - leftY);
		double zoomFactor = Math.min(xScale, yScale);
		if ( bWidth * zoomFactor > MAX_SIZE ) {
			zoomFactor = (double) MAX_SIZE / bWidth;
		}
		setZoom(zoomFactor, new Point((int) envelop.getCenterX(), (int) envelop.getCenterY()));
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
			save(GAMA.getDefaultScope(), buffImage);
			return;
		}
		BufferedImage newImage = ImageUtils.createCompatibleImage(snapshotDimension.x, snapshotDimension.y);
		IGraphics tempGraphics = new AWTDisplayGraphics(newImage);
		tempGraphics.fill(bgColor, 1);
		manager.drawLayersOn(tempGraphics);
		save(GAMA.getDefaultScope(), newImage);
		newImage.flush();
	}

	/**
	 * This method does nothing for JAVA2D display
	 */
	@Override
	public void addShapeFile() {
		// TODO Auto-generated method stub
	}

	@Override
	public void initOutput3D(boolean output3d, ILocation output3dNbCycles) {
		;
	}

	@Override
	public void repaint() {
		GuiUtils.debug("AWTDisplaySurface.repaint not transmitted");
		// super.repaint();
	}

	public void repaint(boolean fromInside) {
		// GuiUtils.debug("AWTDisplaySurface.repaint transmitted from inside");
		if ( fromInside && !isPainting ) {
			super.repaint();

		}
	}

	@Override
	public void paintChildren(Graphics g) {
		// GuiUtils.debug("AWTDisplaySurface.paintChildren");
		// super.paintChildren(g);
	}

	@Override
	public void paintBorder(Graphics g) {
		// GuiUtils.debug("AWTDisplaySurface.paintBorder");
		// super.paintBorder(g);
	}

	//
	// @Override
	// public void update(Graphics g) {
	// GuiUtils.debug("AWTDisplaySurface.update");
	// super.update(g);
	// }

	@Override
	public void paint(Graphics g) {
		// GuiUtils.debug("AWTDisplaySurface.paint");
		isPainting = true;
		super.paint(g);
		isPainting = false;
	}

}
