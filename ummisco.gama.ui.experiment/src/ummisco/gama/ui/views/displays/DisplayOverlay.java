/*******************************************************************************************************
 *
 * DisplayOverlay.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.views.displays;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPartSite;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.IOverlayProvider;
import msi.gama.common.interfaces.IUpdaterTarget;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.outputs.layers.OverlayStatement.OverlayInfo;
import msi.gama.runtime.GAMA;
import msi.gaml.operators.Maths;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The class DisplayOverlay.
 *
 * @author drogoul
 * @since 19 august 2013
 *
 */
public class DisplayOverlay implements IUpdaterTarget<OverlayInfo> {

	/**
	 * The Class Label.
	 */
	class Label extends Canvas {

		/** The alignment. */
		final int alignment;

		/** The text. */
		final StringBuilder text = new StringBuilder();

		/** The data. */
		final GridData data;

		/**
		 * Instantiates a new label.
		 *
		 * @param parent
		 *            the parent
		 * @param style
		 *            the style
		 */
		public Label(final Composite parent, final int style) {
			super(parent, SWT.NONE);
			setVisible(true);
			alignment = style;
			addPaintListener(e -> paint(e.gc));
			data = infoData(style);
			setLayoutData(data);
			GamaColors.setBackAndForeground(parent.getBackground(), IGamaColors.WHITE.color(), this);
			addMouseListener(toggleListener);
		}

		/**
		 * Info data.
		 *
		 * @param horizontalAlign
		 *            the horizontal align
		 * @return the grid data
		 */
		private GridData infoData(final int horizontalAlign) {
			final GridData d = new GridData(horizontalAlign, SWT.CENTER, true, false);
			d.minimumHeight = 24;
			d.heightHint = 24;
			d.minimumWidth = 140;
			d.widthHint = 140;
			return d;
		}

		/**
		 * Sets the width.
		 *
		 * @param width
		 *            the new width
		 */
		public void setWidth(final int width) {
			if (width <= data.minimumWidth) return;
			data.minimumWidth = width;
			data.widthHint = width;
			getParent().requestLayout();
		}

		/**
		 * Paint.
		 *
		 * @param gc
		 *            the gc
		 * @return the object
		 */
		private void paint(final GC gc) {
			final int width = getBounds().width;
			gc.setForeground(IGamaColors.WHITE.color());
			String string = text.toString();
			Point extent = gc.stringExtent(string);
			if (extent.x > width) {
				setWidth(extent.x);
				return;
			}
			int height = getBounds().height;
			int y = (height - extent.y) / 2;
			int x = switch (alignment) {
				case SWT.RIGHT -> width - extent.x;
				case SWT.CENTER -> (width - extent.x) / 2;
				default -> 0;
			};
			gc.drawString(string, x, y, true);
		}

		/**
		 * Sets the text.
		 *
		 * @param string
		 *            the new text
		 */
		public void setText(final String string) {
			text.setLength(0);
			text.append(string);
			redraw();
		}

	}

	static {
		DEBUG.OFF();
	}

	/** The right. */
	Label coord, zoom, left, middle, right;

	// /** The text. */
	// StringBuilder csb = new StringBuilder(), zsb = new StringBuilder(), lsb = new StringBuilder(),
	// msb = new StringBuilder(), rsb = new StringBuilder();

	/** The scalebar. */
	Canvas scalebar;

	/** The is busy. */
	volatile boolean isBusy;

	/** The popup. */
	private final Shell popup;

	/** The visible. */
	private boolean visible = false;

	/** The view. */
	final LayeredDisplayView view;

	/** The reference composite. */
	protected final Composite referenceComposite;

	/** The create extra info. */
	// private final Shell parentShell;
	final boolean createExtraInfo;

	/** The timer. */
	Timer timer = new Timer();

	/**
	 * The Class FPSTask.
	 */
	public class FPSTask extends TimerTask {

		@Override
		public void run() {
			WorkbenchHelper.asyncRun(() -> { if (!zoom.isDisposed()) { zoom.redraw(); } });

		}
	}

	/**
	 * The listener interface for receiving overlay events. The class that is interested in processing a overlay event
	 * implements this interface, and the object created with that class is registered with a component using the
	 * component's <code>addOverlayListener<code> method. When the overlay event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see OverlayEvent
	 */
	class OverlayListener extends ShellAdapter implements ControlListener {

		@Override
		public void controlMoved(final ControlEvent e) {
			relocate();
			resize();
		}

		@Override
		public void controlResized(final ControlEvent e) {
			relocate();
			resize();
		}

		@Override
		public void shellClosed(final ShellEvent e) {
			close();
		}

	}

	/**
	 * Instantiates a new display overlay.
	 *
	 * @param view
	 *            the view
	 * @param c
	 *            the c
	 * @param provider
	 *            the provider
	 */
	public DisplayOverlay(final LayeredDisplayView view, final Composite c,
			final IOverlayProvider<OverlayInfo> provider) {
		this.createExtraInfo = provider != null;
		this.view = view;
		referenceComposite = c;
		// parentShell = c.getShell();
		popup = new Shell(c.getShell(), SWT.NO_TRIM | SWT.NO_FOCUS);
		popup.setAlpha(140);
		final FillLayout layout = new FillLayout();
		layout.type = SWT.VERTICAL;
		layout.spacing = 10;
		popup.setLayout(layout);
		popup.setBackground(
				GamaColors.toSwtColor(view.getDisplaySurface().getData().getBackgroundColor().darker().darker()));
		createPopupControl();
		popup.setAlpha(140);
		popup.layout();
		c.getShell().addShellListener(listener);
		// parentShell.addControlListener(listener);
		c.addControlListener(listener);
		if (provider != null) { provider.setTarget(new ThreadedOverlayUpdater(this), view.getDisplaySurface()); }
		// if (GamaPreferences.Displays.CORE_SHOW_FPS.getValue()) {
		timer.schedule(new FPSTask(), 0, 1000);
		// }
	}

	// public void relocateOverlay(final Shell newShell) {
	// if (popup.setParent(newShell)) {
	// // DEBUG.LOG("Relocating overlay");
	// popup.moveAbove(referenceComposite);
	// }
	// }

	/**
	 * Info data.
	 *
	 * @param horizontalAlign
	 *            the horizontal align
	 * @return the grid data
	 */

	/**
	 * Creates the popup control.
	 */
	protected void createPopupControl() {
		// overall panel
		final Shell top = getPopup();
		final GridLayout layout = new GridLayout(3, true);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		top.setLayout(layout);
		// top.setBackground(IGamaColors.BLACK.color());
		if (createExtraInfo) {
			// left overlay info
			left = new Label(top, SWT.LEFT);
			// center overlay info
			middle = new Label(top, SWT.CENTER);
			// right overlay info
			right = new Label(top, SWT.RIGHT);
		}
		// coordinates overlay info
		coord = new Label(top, SWT.LEFT);
		// zoom overlay info
		zoom = new Label(top, SWT.CENTER);
		// scalebar overlay info
		scalebar = new Canvas(top, SWT.None);
		scalebar.setVisible(true);
		final GridData scaleData = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
		scaleData.minimumWidth = 140;
		scaleData.widthHint = 140;
		scaleData.minimumHeight = 24;
		scaleData.heightHint = 24;
		scalebar.setLayoutData(scaleData);
		scalebar.setBackground(top.getBackground());
		scalebar.addPaintListener(e -> paintScale(e.gc));
		top.addMouseListener(toggleListener);
		scalebar.addMouseListener(toggleListener);
		top.layout();
	}

	/**
	 * Paint scale.
	 *
	 * @param gc
	 *            the gc
	 */
	void paintScale(final GC gc) {
		gc.setBackground(IGamaColors.BLACK.color());
		final int BAR_WIDTH = 1;
		final int BAR_HEIGHT = 8;
		final int x = 0;
		final int y = 0;
		final int margin = 20;
		final int width = scalebar.getBounds().width - 2 * margin;
		final int height = scalebar.getBounds().height;
		final int barStartX = x + 1 + BAR_WIDTH / 2 + margin;
		final int barStartY = y + height - BAR_HEIGHT / 2;

		final Path path = new Path(WorkbenchHelper.getDisplay());
		path.moveTo(barStartX, barStartY - BAR_HEIGHT + 2);
		path.lineTo(barStartX, barStartY + 2);
		path.moveTo(barStartX, barStartY - BAR_HEIGHT / 2 + 2);
		path.lineTo(barStartX + width, barStartY - BAR_HEIGHT / 2 + 2);
		path.moveTo(barStartX + width, barStartY - BAR_HEIGHT + 2);
		path.lineTo(barStartX + width, barStartY + 2);

		gc.setForeground(IGamaColors.WHITE.color());
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.setLineWidth(BAR_WIDTH);
		gc.drawPath(path);
		gc.setFont(coord.getFont());
		drawStringCentered(gc, "0", barStartX, barStartY - 6);
		drawStringCentered(gc, getScaleRight(), barStartX + width, barStartY - 6);
		path.dispose();
	}

	/**
	 * Gets the scale right.
	 *
	 * @return the scale right
	 */
	private String getScaleRight() {
		final double real = getValueOfOnePixelInModelUnits() * 100;
		// DEBUG.LOG("GetScaleRight " + real);
		if (real > 1000) return String.format("%.1fkm", real / 1000d);
		if (real < 0.001) return String.format("%dmm", (int) real * 1000);
		if (real < 0.01) return String.format("%dcm", (int) (real * 100));
		return String.format("%dm", (int) real);
	}

	/** The do hide. */
	Runnable doHide = this::hide;

	/** The do display. */
	Runnable doDisplay = this::display;

	/** The listener. */
	OverlayListener listener = new OverlayListener();

	/** The toggle listener. */
	protected final MouseListener toggleListener = new MouseAdapter() {

		@Override
		public void mouseUp(final MouseEvent e) {
			setVisible(false);
		}

	};

	@Override
	public boolean isBusy() { return isBusy; }

	/**
	 * Update.
	 */
	public void update() {
		if (isBusy) return;
		isBusy = true;
		try {
			if (getPopup().isDisposed()) return;
			if (!coord.isDisposed()) {
				try {
					getOverlayCoordInfo(coord.text);
					coord.redraw();
				} catch (final Exception e) {
					coord.setText("Not initialized yet");
				}
			}
			if (!zoom.isDisposed()) {
				try {
					getOverlayZoomInfo(zoom.text);
					zoom.redraw();
				} catch (final Exception e) {
					zoom.setText("Not initialized yet");
				}
			}
			if (!scalebar.isDisposed()) { scalebar.redraw(); }
			getPopup().layout(true);
		} finally {
			isBusy = false;
		}
	}

	/**
	 * Gets the location.
	 *
	 * @return the location
	 */
	protected Point getLocation() {
		final Rectangle r = referenceComposite.getClientArea();
		final Point p = referenceComposite.toDisplay(r.x, r.y);
		final int x = p.x;
		final int y = p.y + r.height - (createExtraInfo ? 56 : 32);
		DEBUG.OUT("Location of overlay = " + x + " " + y + " <> client area dans Display : " + r);
		return new Point(x, y);
	}

	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	protected Point getSize() {
		final Point s = referenceComposite.getSize();
		return new Point(s.x, -1);
	}

	/**
	 * Draw string centered.
	 *
	 * @param gc
	 *            the gc
	 * @param string
	 *            the string
	 * @param xCenter
	 *            the x center
	 * @param yBase
	 *            the y base
	 * @param filled
	 *            the filled
	 */
	private void drawStringCentered(final GC gc, final String string, final int xCenter, final int yBase) {
		final Point extent = gc.textExtent(string);
		final int xx = xCenter - extent.x / 2;
		gc.drawText(string, xx, yBase - extent.y, true);
	}

	/**
	 * @param left2
	 * @param createColor
	 */
	private void setForeground(final Label label, final Color color) {
		if (label == null || label.isDisposed()) return;
		final Color c = label.getForeground();
		label.setForeground(color);
		if (c != IGamaColors.WHITE.color() && c != color) { c.dispose(); }
	}

	/**
	 * Method updateWith()
	 *
	 * @see msi.gama.gui.swt.controls.IUpdaterTarget#updateWith(java.lang.Object)
	 */
	@Override
	public void updateWith(final OverlayInfo m) {
		final String[] infos = m.infos;
		final List<int[]> colors = m.colors;
		if (infos[0] != null) {
			if (colors != null) { setForeground(left, GamaColors.get(colors.get(0)).color()); }
			left.setText(infos[0]);
		}
		if (infos[1] != null) {
			if (colors != null) { setForeground(middle, GamaColors.get(colors.get(1)).color()); }
			middle.setText(infos[1]);
		}
		if (infos[2] != null) {
			if (colors != null) { setForeground(right, GamaColors.get(colors.get(2)).color()); }
			right.setText(infos[2]);
		}

		getPopup().layout(true);
	}

	/**
	 * Method getCurrentState()
	 *
	 * @see msi.gama.common.interfaces.IUpdaterTarget#getCurrentState()
	 */
	@Override
	public int getCurrentState() { return IGui.NEUTRAL; }

	/**
	 * Method resume()
	 *
	 * @see msi.gama.common.interfaces.IUpdaterTarget#resume()
	 */
	@Override
	public void resume() {}

	/**
	 * Gets the popup.
	 *
	 * @return the popup
	 */
	public Shell getPopup() { return popup; }

	// protected LayeredDisplayView getView() {
	// return view;
	// }

	/**
	 * Display.
	 */
	public void display() {
		// We first verify that the popup is still ok
		if (!isVisible() || popup.isDisposed()) return;
		update();
		relocate();
		resize();
		if (!popup.isVisible()) { popup.setVisible(true); }
	}

	/**
	 * Relocate.
	 */
	public void relocate() {
		if (!isVisible()) return;
		if (!popup.isDisposed()) { popup.setLocation(getLocation()); }
	}

	/**
	 * Resize.
	 */
	public void resize() {
		if (!isVisible()) return;
		if (!popup.isDisposed()) {
			final Point size = getSize();
			popup.setSize(popup.computeSize(size.x, size.y));
			DEBUG.OUT("Size of overlay = " + popup.getSize().x + " " + popup.getSize().y);
		}
	}

	/**
	 * Hide.
	 */
	public void hide() {
		if (!popup.isDisposed() && popup.isVisible()) {
			popup.setSize(0, 0);
			popup.update();
			popup.setVisible(false);
		}
	}

	@Override
	public boolean isDisposed() { return popup.isDisposed() || viewIsDetached(); }

	/**
	 * Close.
	 */
	public void close() {
		if (!popup.isDisposed()) {
			// Composite c = view.getComponent();
			if (referenceComposite != null && !referenceComposite.isDisposed()) {
				referenceComposite.removeControlListener(listener);
			}
			if (!popup.getParent().isDisposed()) {
				popup.getParent().removeControlListener(listener);
				popup.getParent().getShell().removeShellListener(listener);
			}
			timer.cancel();
			popup.dispose();
		}
	}

	@Override
	public boolean isVisible() {
		// AD: Temporary fix for Issue 548. When a view is detached, the
		// overlays are not displayed
		return visible && !isDisposed();
	}

	/**
	 * View is detached.
	 *
	 * @return true, if successful
	 */
	private boolean viewIsDetached() {
		// Uses the trick from
		// http://eclipsesource.com/blogs/2010/06/23/tip-how-to-detect-that-a-view-was-detached/
		final boolean[] result = { false };
		WorkbenchHelper.run(() -> {
			final IWorkbenchPartSite site = view.getSite();
			if (site == null) return;
			final Shell shell = site.getShell();
			if (shell == null) return;
			final String text = shell.getText();
			result[0] = text == null || text.isEmpty();
		});
		return result[0];

	}

	/**
	 * Sets the visible.
	 *
	 * @param visible
	 *            the new visible
	 */
	public void setVisible(final boolean visible) {
		this.visible = visible;
		if (!visible) {
			hide();
		} else if (!viewIsDetached()) { display(); }
	}

	/**
	 * Dispose.
	 */
	public void dispose() {
		popup.dispose();

	}

	/**
	 * Gets the value of one pixel in model units.
	 *
	 * @return the value of one pixel in model units
	 */
	public double getValueOfOnePixelInModelUnits() {
		final IDisplaySurface s = view.getDisplaySurface();
		if (s == null) return 1;
		final double displayWidth = s.getDisplayWidth();
		final double envWidth = s.getEnvWidth();
		return envWidth / displayWidth;
	}

	/**
	 * Gets the overlay coord info.
	 *
	 * @param sb
	 *            the sb
	 * @return the overlay coord info
	 */
	public void getOverlayCoordInfo(final StringBuilder sb) {
		final LayeredDisplayOutput output = view.getOutput();
		if (output == null) return;
		sb.setLength(0);
		final boolean paused = output.isPaused();
		final boolean synced = GAMA.isSynchronized();
		final IDisplaySurface surface = view.getDisplaySurface();
		if (surface != null) { surface.getModelCoordinatesInfo(sb); }
		if (paused) { sb.append(" | Paused"); }
		if (synced) { sb.append(" | Synchronized"); }
	}

	/**
	 * Gets the overlay zoom info.
	 *
	 * @param sb
	 *            the sb
	 * @return the overlay zoom info
	 */
	public void getOverlayZoomInfo(final StringBuilder sb) {
		final IDisplaySurface surface = view.getDisplaySurface();
		if (surface == null) return;
		sb.setLength(0);
		// if (CORE_SHOW_FPS.getValue()) {
		sb.append(surface.getFPS());
		sb.append(" fps | ");
		// }
		int zl = 0;
		if (view.getOutput() != null) {
			final Double dataZoom = view.getOutput().getData().getZoomLevel();
			if (dataZoom == null) {
				zl = 1;
			} else {
				zl = (int) (dataZoom * 100);
			}
		}
		sb.append("Zoom ").append(zl).append("%");
		if (view.isOpenGL()) {
			final Envelope3D roi = ((IDisplaySurface.OpenGL) surface).getROIDimensions();
			if (roi != null) {
				sb.append(" ROI [");
				sb.append(Maths.round(roi.getWidth(), 2));
				sb.append(" x ");
				sb.append(Maths.round(roi.getHeight(), 2));
				sb.append("]");
			}
		}
	}

}
