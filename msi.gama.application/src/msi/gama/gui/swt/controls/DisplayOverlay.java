/*********************************************************************************************
 *
 *
 * 'DisplayOverlay.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.swt.controls;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchPartSite;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.IOverlayProvider;
import msi.gama.common.interfaces.IUpdaterTarget;
import msi.gama.gui.swt.GamaColors;
import msi.gama.gui.swt.IGamaColors;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.views.LayeredDisplayView;
import msi.gama.outputs.layers.OverlayStatement.OverlayInfo;
import msi.gama.runtime.GAMA;

/**
 * The class DisplayOverlay.
 *
 * @author drogoul
 * @since 19 august 2013
 *
 */
public class DisplayOverlay implements IUpdaterTarget<OverlayInfo> {

	Label coord, zoom, left, center, right;
	Canvas scalebar;
	volatile boolean isBusy;
	private final Shell popup;
	private boolean visible = false;
	private final LayeredDisplayView view;
	protected final Composite referenceComposite;
	// private final Shell parentShell;
	final boolean createExtraInfo;
	Timer timer = new Timer();

	public class FPSTask extends TimerTask {

		@Override
		public void run() {
			GAMA.getGui().asyncRun(new Runnable() {

				@Override
				public void run() {
					if ( !zoom.isDisposed() ) {
						zoom.setText(getView().getOverlayZoomInfo());
					}
				}
			});

		}
	}

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

	public DisplayOverlay(final LayeredDisplayView view, final Composite c, final IOverlayProvider provider) {
		this.createExtraInfo = provider != null;
		this.view = view;
		final IPartService ps = ((IWorkbenchPart) view).getSite().getService(IPartService.class);
		ps.addPartListener(pl2);
		referenceComposite = c;
		// parentShell = c.getShell();
		popup = new Shell(c.getShell(), SWT.NO_TRIM | SWT.NO_FOCUS);
		popup.setAlpha(140);
		final FillLayout layout = new FillLayout();
		layout.type = SWT.VERTICAL;
		layout.spacing = 10;
		popup.setLayout(layout);
		popup.setBackground(IGamaColors.BLACK.color());
		createPopupControl();
		popup.setAlpha(140);
		popup.layout();
		c.getShell().addShellListener(listener);
		// parentShell.addControlListener(listener);
		c.addControlListener(listener);
		if ( provider != null ) {
			provider.setTarget(new ThreadedOverlayUpdater(this), view.getDisplaySurface());
		}
		if ( GamaPreferences.CORE_SHOW_FPS.getValue() ) {
			timer.schedule(new FPSTask(), 0, 1000);
		}
	}

	public void relocateOverlay(final Shell newShell) {
		if ( popup.setParent(newShell) ) {
			System.out.println("Relocating overlay");
			popup.moveAbove(referenceComposite);
		}
	}

	private Label label(final Composite c, final int horizontalAlign) {
		final Label l = new Label(c, SWT.None);
		l.setForeground(IGamaColors.WHITE.color());
		l.setBackground(IGamaColors.BLACK.color());
		l.setText(" ");
		l.setLayoutData(infoData(horizontalAlign));
		l.addMouseListener(toggleListener);
		return l;
	}

	private GridData infoData(final int horizontalAlign) {
		final GridData data = new GridData(horizontalAlign, SWT.CENTER, true, false);
		data.minimumHeight = 24;
		data.heightHint = 24;
		return data;
	}

	protected void createPopupControl() {
		// overall panel
		final Shell top = getPopup();
		final GridLayout layout = new GridLayout(3, true);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		top.setLayout(layout);
		top.setBackground(IGamaColors.BLACK.color());
		if ( createExtraInfo ) {
			// left overlay info
			left = label(top, SWT.LEFT);
			// center overlay info
			center = label(top, SWT.CENTER);
			// right overlay info
			right = label(top, SWT.RIGHT);
		}
		// coordinates overlay info
		coord = label(top, SWT.LEFT);
		// zoom overlay info
		zoom = label(top, SWT.CENTER);
		// scalebar overlay info
		scalebar = new Canvas(top, SWT.None);
		scalebar.setVisible(getView().getOutput().shouldDisplayScale());
		final GridData scaleData = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
		scaleData.minimumWidth = 140;
		scaleData.widthHint = 140;
		scaleData.minimumHeight = 24;
		scaleData.heightHint = 24;
		scalebar.setLayoutData(scaleData);
		scalebar.setBackground(IGamaColors.BLACK.color());
		scalebar.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(final PaintEvent e) {
				paintScale(e.gc);
			}
		});
		top.addMouseListener(toggleListener);
		scalebar.addMouseListener(toggleListener);
		top.layout();
	}

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

		final Path path = new Path(SwtGui.getDisplay());
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
		drawStringCentered(gc, "0", barStartX, barStartY - 6, false);
		drawStringCentered(gc, getScaleRight(), barStartX + width, barStartY - 6, false);
		path.dispose();
	}

	private String getScaleRight() {
		final double real = getView().getValueOfOnePixelInModelUnits() * 100;
		// System.out.println("GetScaleRight " + real);
		if ( real > 1000 ) {
			return String.format("%.1fkm", real / 1000d);
		} else if ( real < 0.001 ) {
			return String.format("%dmm", (int) real * 1000);
		} else if ( real < 0.01 ) {
			return String.format("%dcm", (int) (real * 100));
		} else {
			return String.format("%dm", (int) real);
		}
	}

	Runnable doHide = new Runnable() {

		@Override
		public void run() {
			hide();
		}
	};
	Runnable doDisplay = new Runnable() {

		@Override
		public void run() {
			display();
		}
	};
	private final IPartListener2 pl2 = new IPartListener2() {

		@Override
		public void partActivated(final IWorkbenchPartReference partRef) {

			final IWorkbenchPart part = partRef.getPart(false);
			if ( view.equals(part) ) {
				run(doDisplay);
			}
		}

		@Override
		public void partBroughtToTop(final IWorkbenchPartReference partRef) {}

		@Override
		public void partClosed(final IWorkbenchPartReference partRef) {
			final IWorkbenchPart part = partRef.getPart(false);
			if ( view.equals(part) ) {
				close();
			}
		}

		@Override
		public void partDeactivated(final IWorkbenchPartReference partRef) {
			final IWorkbenchPart part = partRef.getPart(false);
			if ( view.equals(part) && !referenceComposite.isVisible() ) {
				run(doHide);
			}
		}

		@Override
		public void partOpened(final IWorkbenchPartReference partRef) {}

		@Override
		public void partHidden(final IWorkbenchPartReference partRef) {
			final IWorkbenchPart part = partRef.getPart(false);
			if ( view.equals(part) ) {
				run(doHide);
			}
		}

		@Override
		public void partVisible(final IWorkbenchPartReference partRef) {
			final IWorkbenchPart part = partRef.getPart(false);
			if ( view.equals(part) ) {
				run(doDisplay);
			}
		}

		@Override
		public void partInputChanged(final IWorkbenchPartReference partRef) {}
	};
	OverlayListener listener = new OverlayListener();
	protected final MouseListener toggleListener = new MouseAdapter() {

		@Override
		public void mouseUp(final MouseEvent e) {
			setVisible(false);
		}

	};

	@Override
	public boolean isBusy() {
		return isBusy;
	}

	public void update() {
		if ( isBusy )
			return;
		isBusy = true;
		try {
			if ( getPopup().isDisposed() ) { return; }
			if ( !coord.isDisposed() ) {
				try {
					coord.setText(getView().getOverlayCoordInfo());
				} catch (final Exception e) {
					coord.setText("Not initialized yet");
				}
			}
			if ( !zoom.isDisposed() ) {
				try {
					zoom.setText(getView().getOverlayZoomInfo());
				} catch (final Exception e) {
					GAMA.getGui().debug("Error in updating overlay: " + e.getMessage());
					zoom.setText("Not initialized yet");
				}
			}
			if ( !scalebar.isDisposed() ) {
				scalebar.redraw();
				scalebar.update();
			}
			getPopup().layout(true);
		} finally {
			isBusy = false;
		}
	}

	protected Point getLocation() {
		final Rectangle r = referenceComposite.getClientArea();
		final Point p = referenceComposite.toDisplay(r.x, r.y);
		final int x = p.x;
		final int y = p.y + r.height - (createExtraInfo ? 56 : 32);
		return new Point(x, y);
	}

	protected Point getSize() {
		final Point s = referenceComposite.getSize();
		return new Point(s.x, -1);
	}

	private void drawStringCentered(final GC gc, final String string, final int xCenter, final int yBase,
		final boolean filled) {
		final Point extent = gc.textExtent(string);
		final int xx = xCenter - extent.x / 2;
		gc.drawText(string, xx, yBase - extent.y, !filled);
	}

	public void displayScale(final Boolean newValue) {
		scalebar.setVisible(newValue);
	}

	/**
	 * @param left2
	 * @param createColor
	 */
	private void setForeground(final Label label, final Color color) {
		if ( label == null || label.isDisposed() ) { return; }
		final Color c = label.getForeground();
		label.setForeground(color);
		if ( c != IGamaColors.WHITE.color() && c != color ) {
			c.dispose();
		}
	}

	/**
	 * Method updateWith()
	 * @see msi.gama.gui.swt.controls.IUpdaterTarget#updateWith(java.lang.Object)
	 */
	@Override
	public void updateWith(final OverlayInfo m) {
		final String[] infos = m.infos;
		final List<int[]> colors = m.colors;
		if ( infos[0] != null ) {
			left.setText(infos[0]);
			if ( colors != null ) {
				setForeground(left, GamaColors.get(colors.get(0)).color());
			}
		}
		if ( infos[1] != null ) {
			center.setText(infos[1]);
			if ( colors != null ) {
				setForeground(center, GamaColors.get(colors.get(1)).color());
			}
		}
		if ( infos[2] != null ) {
			right.setText(infos[2]);
			if ( colors != null ) {
				setForeground(right, GamaColors.get(colors.get(2)).color());
			}
		}

		getPopup().layout(true);
	}

	/**
	 * Method getCurrentState()
	 * @see msi.gama.common.interfaces.IUpdaterTarget#getCurrentState()
	 */
	@Override
	public int getCurrentState() {
		return IGui.NEUTRAL;
	}

	/**
	 * Method resume()
	 * @see msi.gama.common.interfaces.IUpdaterTarget#resume()
	 */
	@Override
	public void resume() {}

	protected void run(final Runnable r) {
		GAMA.getGui().run(r);
	}

	public Shell getPopup() {
		return popup;
	}

	protected LayeredDisplayView getView() {
		return view;
	}

	public void display() {
		if ( !isVisible() ) { return; }
		// We first verify that the popup is still ok
		if ( popup.isDisposed() ) { return; }
		update();
		relocate();
		resize();
		if ( !popup.isVisible() ) {
			popup.setVisible(true);
		}
	}

	public void relocate() {
		if ( !isVisible() ) { return; }
		if ( !popup.isDisposed() ) {
			popup.setLocation(getLocation());
		}
	}

	public void resize() {
		if ( !isVisible() ) { return; }
		if ( !popup.isDisposed() ) {
			final Point size = getSize();
			popup.setSize(popup.computeSize(size.x, size.y));
		}
	}

	public void hide() {
		if ( !popup.isDisposed() && popup.isVisible() ) {
			popup.setSize(0, 0);
			popup.update();
			popup.setVisible(false);
		}
	}

	@Override
	public boolean isDisposed() {
		return popup.isDisposed() || viewIsDetached();
	}

	public void close() {
		if ( !popup.isDisposed() ) {
			// Composite c = view.getComponent();
			if ( referenceComposite != null && !referenceComposite.isDisposed() ) {
				referenceComposite.removeControlListener(listener);
			}
			final IPartService ps = ((IWorkbenchPart) view).getSite().getService(IPartService.class);
			if ( ps != null ) {
				ps.removePartListener(pl2);
			}
			if ( !popup.getParent().isDisposed() ) {
				popup.getParent().removeControlListener(listener);
				popup.getParent().getShell().removeShellListener(listener);
			}
			timer.cancel();
			popup.dispose();
		}
	}

	@Override
	public boolean isVisible() {
		// AD: Temporary fix for Issue 548. When a view is detached, the overlays are not displayed
		return visible && !isDisposed();
	}

	private boolean viewIsDetached() {
		// Uses the trick from http://eclipsesource.com/blogs/2010/06/23/tip-how-to-detect-that-a-view-was-detached/
		final boolean[] result = new boolean[] { false };
		GAMA.getGui().run(new Runnable() {

			@Override
			public void run() {
				final IWorkbenchPartSite site = view.getSite();
				if ( site == null ) { return; }
				final Shell shell = site.getShell();
				if ( shell == null ) { return; }
				final String text = shell.getText();
				result[0] = text == null || text.isEmpty();
			}
		});
		return result[0];

	}

	public void setVisible(final boolean visible) {
		this.visible = visible;
		if ( !visible ) {
			hide();
		} else if ( !viewIsDetached() ) {
			display();
		}
		getView().overlayChanged();
	}

	public void dispose() {
		popup.dispose();

	}

}
