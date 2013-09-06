/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.swt.controls;

import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.views.LayeredDisplayView;
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
public class DisplayOverlay extends AbstractOverlay {

	Label text;
	Canvas scalebar;

	public DisplayOverlay(final LayeredDisplayView view) {
		super(view);
	}

	@Override
	protected void createPopupControl() {
		Composite panel = new Composite(getPopup(), SWT.None);
		panel.setBackground(BLACK);
		GridLayout layout = new GridLayout(2, false);
		// layout.verticalSpacing = 2;
		panel.setLayout(layout);

		GridData textData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		text = new Label(panel, SWT.None);
		text.setLayoutData(textData);
		text.setBackground(BLACK);
		text.setForeground(WHITE);
		scalebar = new Canvas(panel, SWT.None);
		scalebar.setVisible(getView().getOutput().shouldDisplayScale());
		GridData scaleData = new GridData(SWT.RIGHT, SWT.FILL, false, false);
		scaleData.widthHint = 140;
		scaleData.minimumWidth = 140;
		scaleData.heightHint = 24;
		scaleData.minimumHeight = 24;
		scalebar.setLayoutData(scaleData);
		scalebar.setBackground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		scalebar.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(final PaintEvent e) {
				paintScale(e.gc);
			}
		});
		text.addMouseListener(toggleListener);
		getPopup().addMouseListener(toggleListener);
		scalebar.addMouseListener(toggleListener);

		scalebar.setSize(140, 24);
	}

	void paintScale(final GC gc) {
		int BAR_WIDTH = 1;
		int BAR_HEIGHT = 8;
		int x = 0;
		int y = 0;
		int margin = 20;
		int width = scalebar.getBounds().width - 2 * margin;
		int height = scalebar.getBounds().height;
		int barStartX = x + 1 + BAR_WIDTH / 2 + margin;
		int barStartY = y - 2 + height - BAR_HEIGHT;

		Path path = new Path(SwtGui.getDisplay());
		path.moveTo(barStartX, barStartY);
		path.lineTo(barStartX, barStartY + BAR_HEIGHT);
		path.lineTo(barStartX + width, barStartY + BAR_HEIGHT);
		path.lineTo(barStartX + width, barStartY);

		gc.setForeground(WHITE);
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.setLineWidth(BAR_WIDTH);
		gc.drawPath(path);
		gc.setFont(text.getFont());
		drawStringCentered(gc, "0", barStartX, barStartY);
		drawStringCentered(gc, String.valueOf(Math.round(getView().getValueOfOnePixelInModelUnits() * 100)), barStartX +
			width, barStartY);
		drawStringCentered(gc, "meters", barStartX + width / 2, barStartY + BAR_HEIGHT - 2);
		path.dispose();
	}

	@Override
	public void update() {
		if ( !text.isDisposed() ) {
			text.setText(getView().getOverlayText());
		}
		if ( !scalebar.isDisposed() ) {
			scalebar.redraw();
		}
	}

	@Override
	protected Point getLocation() {
		Composite surfaceComposite = getView().getComponent();
		Point p = surfaceComposite.toDisplay(surfaceComposite.getLocation());
		Point s = surfaceComposite.getSize();
		int x = p.x;
		int y = p.y + s.y - 32;
		return new Point(x, y);
	}

	@Override
	protected Point getSize() {
		Point s = getView().getComponent().getSize();
		return new Point(s.x, 32);
	}

	public static void drawStringCentered(final GC gc, final String string, final int xCenter, final int yBase) {
		Point extent = gc.textExtent(string);
		int xx = xCenter - extent.x / 2;
		gc.drawText(string, xx, yBase - extent.y, true);
	}

	public void displayScale(final Boolean newValue) {
		scalebar.setVisible(newValue);
	}

	@Override
	public void appear() {
		Composite surfaceComposite = getView().getComponent();
		Point loc = surfaceComposite.toDisplay(surfaceComposite.getLocation());
		Point s = surfaceComposite.getSize();
		Point extent = new Point(s.x, 10);
		slidingShell.setBounds(loc.x, loc.y + s.y - 10, extent.x, extent.y);
		super.appear();
	}
}
