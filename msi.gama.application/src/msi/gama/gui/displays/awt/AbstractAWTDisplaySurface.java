/*********************************************************************************************
 * 
 * 
 * 'AbstractAWTDisplaySurface.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.displays.awt;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.FileUtils;
import msi.gama.gui.swt.swing.OutputSynchronizer;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.*;
import msi.gama.outputs.display.LayerManager;
import msi.gama.outputs.layers.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Files;

public abstract class AbstractAWTDisplaySurface extends JPanel implements IDisplaySurface {

	protected static final String SNAPSHOT_FOLDER_NAME = "snapshots";
	protected static final int MAX_ZOOM_FACTOR = 2;

	protected boolean qualityRendering = GamaPreferences.CORE_ANTIALIAS.getValue();

	private final LayeredDisplayOutput output;
	protected final LayeredDisplayData data;
	protected final Rectangle viewPort = new Rectangle();
	protected final ILayerManager manager;
	protected final Runnable displayBlock;
	protected final AffineTransform translation = new AffineTransform();

	protected IGraphics iGraphics;

	protected DisplaySurfaceMenu menuManager;
	protected volatile boolean canBeUpdated = true;
	protected IExpression temp_focus;

	protected Dimension previousPanelSize;
	protected double zoomIncrement = 0.1;
	protected Double zoomLevel = null;
	protected boolean zoomFit = true;
	private IZoomListener zoomListener;

	private IScope scope;

	protected AbstractAWTDisplaySurface(final Object ... args) {
		output = (LayeredDisplayOutput) args[0];
		data = output.getData();
		temp_focus = output.getFacet(IKeyword.FOCUS);
		setOpaque(true);
		setDoubleBuffered(false);
		setLayout(new BorderLayout());
		setBackground(data.getBackgroundColor());
		setName(output.getName());
		setHighlightColor(data.getHighlightColor());
		manager = new LayerManager(this);
		final List<AbstractLayerStatement> layers = output.getLayers();
		for ( final AbstractLayerStatement layer : layers ) {
			manager.addLayer(AbstractLayer.createLayer(scope, layer));
		}
		displayBlock = new Runnable() {

			@Override
			public void run() {
				internalDisplayUpdate();
			}
		};
	}

	protected abstract void internalDisplayUpdate();

	@Override
	public void outputReloaded() {
		// We first copy the scope
		setDisplayScope(output.getScope().copy());
		// We disable error reporting
		getDisplayScope().disableErrorReporting();
		if ( iGraphics != null ) {
			iGraphics.initFor(this);
		}
		manager.outputChanged();

		resizeImage(getWidth(), getHeight(), true);
		if ( zoomFit ) {
			zoomFit();
		}
	}

	@Override
	public IScope getDisplayScope() {
		return scope;
	}

	// FIXME Ugly code. The hack must be better written
	public void setSWTMenuManager(final Object manager) {
		menuManager = (DisplaySurfaceMenu) manager;
	}

	@Override
	public ILayerManager getManager() {
		return manager;
	}

	public Point getOrigin() {
		return viewPort.getLocation();
	}

	@Override
	public void setFont(final Font f) {
		// super.setFont(null);
	}

	//
	// @Override
	// public void setBackgroundColor(final Color c) {
	// set
	// }

	@Override
	public void snapshot() {
		save(getDisplayScope(), getImage());
	}

	/**
	 * Save this surface into an image passed as a parameter
	 * @param scope
	 * @param image
	 */
	public final void save(final IScope scope, final RenderedImage image) {
		// Intentionnaly passing GAMA.getRuntimeScope() to errors in order to prevent the exceptions from being masked.
		if ( image == null ) { return; }
		try {
			Files.newFolder(scope, SNAPSHOT_FOLDER_NAME);
		} catch (GamaRuntimeException e1) {
			e1.addContext("Impossible to create folder " + SNAPSHOT_FOLDER_NAME);
			GAMA.reportError(GAMA.getRuntimeScope(), e1, false);
			e1.printStackTrace();
			return;
		}
		String snapshotFile =
			FileUtils.constructAbsoluteFilePath(scope, SNAPSHOT_FOLDER_NAME + "/" + GAMA.getModel().getName() +
				"_display_" + output.getName(), false);

		String file =
			snapshotFile + "_size_" + image.getWidth() + "x" + image.getHeight() + "_cycle_" +
				scope.getClock().getCycle() + "_time_" + java.lang.System.currentTimeMillis() + ".png";
		DataOutputStream os = null;
		try {
			os = new DataOutputStream(new FileOutputStream(file));
			ImageIO.write(image, "png", os);
		} catch (java.io.IOException ex) {
			GamaRuntimeException e = GamaRuntimeException.create(ex, scope);
			e.addContext("Unable to create output stream for snapshot image");
			GAMA.reportError(GAMA.getRuntimeScope(), e, false);
		} finally {
			try {
				if ( os != null ) {
					os.close();
				}
			} catch (Exception ex) {
				GamaRuntimeException e = GamaRuntimeException.create(ex, scope);
				e.addContext("Unable to close output stream for snapshot image");
				GAMA.reportError(GAMA.getRuntimeScope(), e, false);
			}
		}
	}

	@Override
	public void removeNotify() {
		// GuiUtils.debug("AWTDisplaySurface.removeNotify: BEGUN" + outputName);
		super.removeNotify();
		OutputSynchronizer.decClosingViews(getOutput().getName());
		// GuiUtils.debug("AWTDisplaySurface.removeNotify: FINISHED" + outputName);
	}

	protected void scaleOrigin() {
		Point origin = getOrigin();
		setOrigin(origin.x * getWidth() / previousPanelSize.width, origin.y * getHeight() / previousPanelSize.height);
		repaint();
	}

	protected void centerImage() {
		setOrigin((getWidth() - getDisplayWidth()) / 2, (getHeight() - getDisplayHeight()) / 2);
	}

	@Override
	public int getImageWidth() {
		return getDisplayWidth();
	}

	@Override
	public int getImageHeight() {
		return getDisplayHeight();
	}

	@Override
	public int getOriginX() {
		return getOrigin().x;
	}

	@Override
	public int getOriginY() {
		return getOrigin().y;
	}

	@Override
	public Color getHighlightColor() {
		return getForeground();
	}

	@Override
	public void setHighlightColor(final Color h) {
		setForeground(h);
		if ( iGraphics != null ) {
			iGraphics.setHighlightColor(h);
		}
	}

	@Override
	public BufferedImage getImage() {
		Robot screenRobot;
		try {
			screenRobot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
			return null;
		}
		if ( isDisplayable() && isShowing() ) {
			Rectangle rectangle = new Rectangle(this.getLocationOnScreen(), this.getSize());
			final BufferedImage buffImage = screenRobot.createScreenCapture(rectangle);
			return buffImage;
		}
		return null;
		// = renderer.getScreenShot();

	}

	@Override
	public void setQualityRendering(final boolean quality) {
		qualityRendering = quality;
		if ( iGraphics == null ) { return; }
		iGraphics.setQualityRendering(quality);
		updateDisplay(true);
	}

	@Override
	public boolean getQualityRendering() {
		return qualityRendering;
	}

	@Override
	public void canBeUpdated(final boolean canBeUpdated) {
		this.canBeUpdated = canBeUpdated;
	}

	public boolean canBeUpdated() {
		return canBeUpdated && iGraphics != null;
	}

	@Override
	public void setOrigin(final int x, final int y) {
		viewPort.setLocation(x, y);
		translation.setToTranslation(x, y);
	}

	private final Runnable displayRunnable = new Runnable() {

		@Override
		public void run() {
			displayBlock.run();
			canBeUpdated(true);
		}
	};

	protected void runDisplay() {
		canBeUpdated(false);
		if ( GAMA.isPaused() || EventQueue.isDispatchThread() ) {
			EventQueue.invokeLater(displayRunnable);
		} else if ( output.isSynchronized() ) {
			try {
				EventQueue.invokeAndWait(displayRunnable);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} else {
			EventQueue.invokeLater(displayRunnable);
		}
	}

	@Override
	public void updateDisplay(final boolean force) {

		if ( !canBeUpdated() ) { return; }
		runDisplay();

	}

	// Used when the image is resized.
	public boolean isImageEdgeInPanel() {
		if ( previousPanelSize == null ) { return false; }
		Point origin = getOrigin();
		return origin.x > 0 && origin.x < previousPanelSize.width || origin.y > 0 &&
			origin.y < previousPanelSize.height;
	}

	// Tests whether the image is displayed in its entirety in the panel.
	public boolean isFullImageInPanel() {
		Point origin = getOrigin();
		return origin.x >= 0 && origin.x + getDisplayWidth() < getWidth() && origin.y >= 0 &&
			origin.y + getDisplayHeight() < getHeight();
	}

	@Override
	public boolean resizeImage(final int x, final int y, final boolean force) {
		if ( !force && x == viewPort.width && y == viewPort.height ) { return true; }
		if ( getWidth() <= 0 && getHeight() <= 0 ) { return false; }
		canBeUpdated(false);
		int[] point = computeBoundsFrom(x, y);
		int imageWidth = Math.max(1, point[0]);
		int imageHeight = Math.max(1, point[1]);
		createNewImage(imageWidth, imageHeight);
		createIGraphics();
		canBeUpdated(true);
		return true;

	}

	protected void createNewImage(final int width, final int height) {
		setDisplayHeight(height);
		setDisplayWidth(width);

	}

	protected abstract void createIGraphics();

	protected void setIGraphics(final IGraphics iGraphics) {
		this.iGraphics = iGraphics;
	}

	@Override
	public double getEnvWidth() {
		return data.getEnvWidth();
	}

	@Override
	public double getEnvHeight() {
		return data.getEnvHeight();
	}

	@Override
	public int getDisplayWidth() {
		return viewPort.width;
	}

	protected void setDisplayWidth(final int displayWidth) {
		viewPort.setSize(displayWidth, viewPort.height);
	}

	@Override
	public int getDisplayHeight() {
		return viewPort.height;
	}

	protected void setDisplayHeight(final int displayHeight) {
		viewPort.setSize(viewPort.width, displayHeight);
	}

	@Override
	public LayeredDisplayOutput getOutput() {
		return output;
	}

	public void setZoomLevel(final Double newZoomLevel) {
		zoomLevel = newZoomLevel;
		if ( zoomListener != null ) {
			zoomListener.newZoomLevel(zoomLevel);
		}
	}

	@Override
	public double getZoomLevel() {
		if ( zoomLevel == null ) {
			zoomLevel = computeInitialZoomLevel();
		}
		return zoomLevel;
	}

	/**
	 * @return
	 */
	protected abstract Double computeInitialZoomLevel();

	@Override
	public void setZoomListener(final IZoomListener listener) {
		zoomListener = listener;
	}

	@Override
	public void zoomFit() {
		setZoomLevel(1d);
		zoomFit = true;
	}

	@Override
	public int[] computeBoundsFrom(final int vwidth, final int vheight) {
		if ( !manager.stayProportional() ) { return new int[] { vwidth, vheight }; }
		final int[] dim = new int[2];
		double widthHeightConstraint = getEnvWidth() / getEnvHeight();
		if ( widthHeightConstraint < 1 ) {
			dim[1] = Math.min(vheight, (int) Math.round(vwidth * widthHeightConstraint));
			dim[0] = Math.min(vwidth, (int) Math.round(dim[1] / widthHeightConstraint));
		} else {
			dim[0] = Math.min(vwidth, (int) Math.round(vheight / widthHeightConstraint));
			dim[1] = Math.min(vheight, (int) Math.round(dim[0] * widthHeightConstraint));
		}
		return dim;
	}

	@Override
	public GamaPoint getModelCoordinatesFrom(final int xOnScreen, final int yOnScreen, final Point sizeInPixels,
		final Point positionInPixels) {
		final double xScale = sizeInPixels.x / getEnvWidth();
		final double yScale = sizeInPixels.y / getEnvHeight();
		final int xInDisplay = xOnScreen - positionInPixels.x;
		final int yInDisplay = yOnScreen - positionInPixels.y;
		final double xInModel = xInDisplay / xScale;
		final double yInModel = yInDisplay / yScale;
		return new GamaPoint(xInModel, yInModel);
	}

	@Override
	public Collection<IAgent> selectAgent(final int x, final int y) {
		int xc = x - getOriginX();
		int yc = y - getOriginY();
		List<IAgent> result = new ArrayList();
		final List<ILayer> layers = getManager().getLayersIntersecting(xc, yc);
		for ( ILayer layer : layers ) {
			Set<IAgent> agents = layer.collectAgentsAt(xc, yc, this);
			if ( !agents.isEmpty() ) {
				result.addAll(agents);
			}
		}
		return result;
	}

	protected void setDisplayScope(final IScope scope) {
		if ( this.scope != null ) {
			GAMA.releaseScope(this.scope);
		}
		this.scope = scope;
	}

	@Override
	public void waitForUpdateAndRun(final Runnable r) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (!canBeUpdated()) {
					try {
						Thread.sleep(10);
					} catch (final InterruptedException e) {}
				}
				r.run();
			}
		}).start();
	}

	@Override
	public void setBackground(final Color bg) {
		super.setBackground(bg);
		if ( data != null ) {
			data.setBackgroundColor(bg);
		}
	}

}