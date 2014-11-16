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
import msi.gama.common.interfaces.*;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.views.LayeredDisplayView;
import msi.gama.outputs.layers.OverlayStatement.OverlayInfo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * The class DisplayOverlay.
 * 
 * @author drogoul
 * @since 19 august 2013
 * 
 */
public class DisplayOverlay extends AbstractOverlay implements IUpdaterTarget<OverlayInfo> {

	Label coord, zoom, left, center, right;
	Canvas scalebar;

	public DisplayOverlay(final LayeredDisplayView view, final IOverlayProvider provider) {
		super(view, provider != null);
		if ( provider != null ) {
			provider.setTarget(new ThreadedOverlayUpdater(this));
		}
	}

	private Label label(final Composite c, final int horizontalAlign) {
		Label l = new Label(c, SWT.None);
		l.setForeground(WHITE);
		l.setBackground(BLACK);
		l.setText(" ");
		l.setLayoutData(infoData(horizontalAlign));
		l.addMouseListener(toggleListener);
		return l;
	}

	private GridData infoData(final int horizontalAlign) {
		GridData data = new GridData(horizontalAlign, SWT.CENTER, true, false);
		data.minimumHeight = 24;
		data.heightHint = 24;
		return data;
	}

	@Override
	protected void createPopupControl() {
		// overall panel
		Shell top = getPopup();
		GridLayout layout = new GridLayout(3, true);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		top.setLayout(layout);
		top.setBackground(BLACK);
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
		GridData scaleData = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
		scaleData.minimumWidth = 140;
		scaleData.widthHint = 140;
		scaleData.minimumHeight = 24;
		scaleData.heightHint = 24;
		scalebar.setLayoutData(scaleData);
		scalebar.setBackground(BLACK);
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
		gc.setBackground(BLACK);
		int BAR_WIDTH = 1;
		int BAR_HEIGHT = 8;
		int x = 0;
		int y = 0;
		int margin = 20;
		int width = scalebar.getBounds().width - 2 * margin;
		int height = scalebar.getBounds().height;
		int barStartX = x + 1 + BAR_WIDTH / 2 + margin;
		int barStartY = y + height - BAR_HEIGHT / 2;

		Path path = new Path(SwtGui.getDisplay());
		path.moveTo(barStartX, barStartY - BAR_HEIGHT + 2);
		path.lineTo(barStartX, barStartY + 2);
		path.moveTo(barStartX, barStartY - BAR_HEIGHT / 2 + 2);
		path.lineTo(barStartX + width, barStartY - BAR_HEIGHT / 2 + 2);
		path.moveTo(barStartX + width, barStartY - BAR_HEIGHT + 2);
		path.lineTo(barStartX + width, barStartY + 2);

		gc.setForeground(WHITE);
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.setLineWidth(BAR_WIDTH);
		gc.drawPath(path);
		gc.setFont(coord.getFont());
		drawStringCentered(gc, "0", barStartX, barStartY - 6, false);
		drawStringCentered(gc, getScaleRight(), barStartX + width, barStartY - 6, false);
		path.dispose();
	}

	private String getScaleRight() {
		double real = getView().getValueOfOnePixelInModelUnits() * 100;
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

	@Override
	public void update() {
		if ( getPopup().isDisposed() ) { return; }
		if ( !coord.isDisposed() ) {
			try {
				coord.setText(getView().getOverlayCoordInfo());
			} catch (Exception e) {
				// GuiUtils.debug("Error in updating overlay: " + e.getMessage());
				coord.setText("Not initialized yet");
			}
		}
		if ( !zoom.isDisposed() ) {
			try {
				zoom.setText(getView().getOverlayZoomInfo());
			} catch (Exception e) {
				// GuiUtils.debug("Error in updating overlay: " + e.getMessage());
				zoom.setText("Not initialized yet");
			}
		}
		if ( !scalebar.isDisposed() ) {
			scalebar.redraw();
		}
		getPopup().layout(true);
	}

	@Override
	protected Point getLocation() {
		Composite surfaceComposite = getView().getComponent();
		Rectangle r = surfaceComposite.getClientArea();
		Point p = surfaceComposite.toDisplay(r.x, r.y);
		int x = p.x;
		int y = p.y + r.height - (createExtraInfo ? 56 : 32);
		return new Point(x, y);
	}

	@Override
	protected Point getSize() {
		Point s = getView().getComponent().getSize();
		return new Point(s.x, -1);
	}

	public static void drawStringCentered(final GC gc, final String string, final int xCenter, final int yBase,
		final boolean filled) {
		Point extent = gc.textExtent(string);
		int xx = xCenter - extent.x / 2;
		gc.drawText(string, xx, yBase - extent.y, !filled);
	}

	public void displayScale(final Boolean newValue) {
		scalebar.setVisible(newValue);
		// ((GridData) scalebar.getLayoutData()).exclude = !newValue;
	}

	//
	// @Override
	// public void appear() {
	// Composite surfaceComposite = getView().getComponent();
	// Point loc = surfaceComposite.toDisplay(surfaceComposite.getLocation());
	// Point s = surfaceComposite.getSize();
	// Point extent = new Point(s.x, 10);
	// slidingShell.setBounds(loc.x, loc.y + s.y - 10, extent.x, extent.y);
	// super.appear();
	// }

	/**
	 * @param left2
	 * @param createColor
	 */
	private void setForeground(final Label label, final Color color) {
		if ( label == null || label.isDisposed() ) { return; }
		Color c = label.getForeground();
		label.setForeground(color);
		if ( c != WHITE && c != color ) {
			c.dispose();
		}
	}

	/**
	 * Method updateWith()
	 * @see msi.gama.gui.swt.controls.IUpdaterTarget#updateWith(java.lang.Object)
	 */
	@Override
	public void updateWith(final OverlayInfo m) {
		String[] infos = m.infos;
		List<int[]> colors = m.colors;
		if ( infos[0] != null ) {
			left.setText(infos[0]);
			if ( colors != null ) {
				setForeground(left, SwtGui.getColor(colors.get(0)));
			}
		}
		if ( infos[1] != null ) {
			center.setText(infos[1]);
			if ( colors != null ) {
				setForeground(center, SwtGui.getColor(colors.get(1)));
			}
		}
		if ( infos[2] != null ) {
			right.setText(infos[2]);
			if ( colors != null ) {
				setForeground(right, SwtGui.getColor(colors.get(2)));
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

}
