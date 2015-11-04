package msi.gama.gui.swt.controls;

import java.util.*;
import java.util.List;
import msi.gama.gui.swt.GamaColors.GamaUIColor;
import msi.gaml.operators.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class SimpleSlider extends Composite implements IPopupProvider {

	private final Composite parent;

	private final IPositionChangeListener popupListener = new IPositionChangeListener() {

		@Override
		public void positionChanged(final double position) {
			popup.display();
		}
	};

	public static final int SMOOTH_STYLE = 0;

	private final Panel rightRegion;
	private final Thumb thumb;
	private final Panel leftRegion;
	private boolean mouseDown = false;
	private int sliderHeight;

	public class Thumb extends Canvas implements PaintListener {

		final Image image;

		public Thumb(final Composite parent, final Image image) {
			super(parent, SWT.NO_BACKGROUND);
			this.image = image;
			addPaintListener(this);
			GridData d = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
			d.minimumHeight = image.getBounds().height;
			// d.heightHint = image.getBounds().height;
			d.minimumWidth = image.getBounds().width;
			d.widthHint = image.getBounds().width;
			setLayoutData(d);
		}

		@Override
		public Point computeSize(final int w, final int h) {
			return new Point(image.getBounds().width, image.getBounds().height);
		}

		/**
		 * Method paintControl()
		 * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
		 */
		@Override
		public void paintControl(final PaintEvent e) {
			GC gc = e.gc;

			Rectangle r = getClientArea();
			gc.setBackground(getParent().getBackground());
			gc.fillRectangle(r);
			int height = parent.getSize().y;
			double y = (height - image.getBounds().height) / 2 /* + 1 */;
			gc.drawImage(image, 0, (int) y);
		}

	}

	/**
	 * The class implementing this interface will be asked to give a user understandable <code>String</code> to the
	 * slider's current position
	 */
	private IToolTipProvider toolTipInterperter;
	// /** the minimum width of the slider */
	// private final int minWidth;
	// /** the minimum height of the slider */
	// private final int minHeight;
	/** A list of position changed listeners */
	private final List<IPositionChangeListener> positionChangedListeners = new ArrayList<IPositionChangeListener>();
	/** stores the previous position that was sent out to the position changed listeners */
	private double previousPosition = -1;

	private GamaUIColor popupColor;
	private final Popup popup;
	private boolean notify = true;

	public SimpleSlider(final Composite parent, final Color color, final Image thumbImageNormal) {
		super(parent, SWT.DOUBLE_BUFFERED);
		this.parent = parent;
		final GridLayout gl = new GridLayout(3, false);
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		setLayout(gl);
		// minWidth = thumbImageNormal.getBounds().width;
		// minHeight = thumbImageNormal.getBounds().height;

		leftRegion = new Panel(this, color);
		leftRegion.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(final MouseEvent e) {
				mouseDown = true;
				moveThumbHorizontally(e.x);
			}

			@Override
			public void mouseUp(final MouseEvent e) {
				mouseDown = false;
			}
		});
		leftRegion.addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(final MouseEvent e) {
				if ( mouseDown ) {
					moveThumbHorizontally(e.x);
				}
			}
		});

		thumb = new Thumb(this, thumbImageNormal);
		thumb.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(final MouseEvent e) {
				mouseDown = true;
				moveThumbHorizontally(leftRegion.getBounds().width + e.x);
			}

			@Override
			public void mouseUp(final MouseEvent e) {
				mouseDown = false;
			}
		});
		thumb.addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(final MouseEvent e) {
				if ( mouseDown ) {
					moveThumbHorizontally(leftRegion.getBounds().width + e.x);
				}
			}
		});

		rightRegion = new Panel(this, color, true);
		rightRegion.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(final MouseEvent e) {
				mouseDown = true;
				moveThumbHorizontally(leftRegion.getBounds().width + thumb.getBounds().width + e.x);
			}

			@Override
			public void mouseUp(final MouseEvent e) {
				mouseDown = false;
			}
		});
		rightRegion.addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(final MouseEvent e) {
				if ( mouseDown ) {
					moveThumbHorizontally(leftRegion.getBounds().width + thumb.getBounds().width + e.x);

				}
			}
		});

		addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(final ControlEvent e) {
				updateSlider(previousPosition, false);
			}
		});

		addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(final FocusEvent e) {
				thumb.setFocus();
			}
		});

		addTraverseListener(new TraverseListener() {

			@Override
			public void keyTraversed(final TraverseEvent e) {
				e.doit = true;
			}
		});
		addPositionChangeListener(popupListener);
		popup = new Popup(this, leftRegion, thumb, rightRegion);

	}

	public void removePositionChangeListener(final IPositionChangeListener listener) {
		synchronized (positionChangedListeners) {
			positionChangedListeners.remove(listener);
		}
	}

	public void addPositionChangeListener(final IPositionChangeListener listener) {
		synchronized (positionChangedListeners) {
			if ( !positionChangedListeners.contains(listener) ) {
				positionChangedListeners.add(listener);
			}
		}
	}

	/**
	 *
	 * @return the position of the slider in the from of a percentage. Note the range is from 0 to 1
	 */
	public double getCurrentPosition() {
		return previousPosition;
	}

	private void updatePositionListeners(final double perc) {
		if ( !notify ) { return; }
		if ( Comparison.different(previousPosition, -1d) && Comparison.different(perc, previousPosition) ) {
			synchronized (positionChangedListeners) {
				final Iterator<IPositionChangeListener> iter = positionChangedListeners.iterator();
				while (iter.hasNext()) {
					iter.next().positionChanged(perc);
				}
			}
		}
	}

	private void moveThumbHorizontally(final int pos) {
		thumb.setFocus();

		int x = pos - thumb.getBounds().width / 2;
		int width = x < 0 ? 0 : x;
		if ( width > getClientArea().width - thumb.getBounds().width ) {
			width = getClientArea().width - thumb.getBounds().width;
		}
		leftRegion.updatePosition(width);
		layout();
		double percentage = 0;
		double leftWidth = leftRegion.getBounds().width;
		double clientWidth = getClientArea().width;
		double thumbsWidth = thumb.getBounds().width;
		percentage = leftWidth / (clientWidth - thumbsWidth);
		updatePositionListeners(percentage);
		previousPosition = percentage;

	}

	private class Panel extends Canvas implements PaintListener {

		private final GridData gd;
		private final Color color;

		public Panel(final Composite parent, final Color color) {
			this(parent, color, false);
		}

		public Panel(final Composite parent, final Color color, final boolean lastFillerRegion) {
			super(parent, SWT.DOUBLE_BUFFERED | SWT.NO_BACKGROUND);
			gd = new GridData(lastFillerRegion ? SWT.FILL : SWT.BEGINNING, SWT.BEGINNING, lastFillerRegion, false);
			gd.minimumHeight = 4;
			// gd.heightHint = 30;
			this.color = color;
			setLayoutData(gd);
			addPaintListener(this);
		}

		void updatePosition(final int value) {
			gd.minimumWidth = value;
			gd.widthHint = value;
		}

		/**
		 * Method paintControl()
		 * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
		 */
		@Override
		public void paintControl(final PaintEvent e) {
			GC gc = e.gc;
			Rectangle r = getClientArea();
			gc.setBackground(getParent().getBackground());
			gc.fillRectangle(r);
			gc.setBackground(color);
			r.y = parent.getSize().y / 2 - 2;
			r.height = 4;
			gc.fillRoundRectangle(r.x, r.y, r.width, r.height, 3, 3);
		}

	}

	/**
	 * Method to update current position of the slider
	 *
	 * @param percentage between 0 and 1 (i.e 0% to 100%)
	 */
	public void updateSlider(double percentage, final boolean notify) {
		checkWidget();
		this.notify = notify;
		if ( percentage < 0 ) {
			percentage = 0;
		} else if ( percentage > 1 ) {
			percentage = 1;
		}
		//
		int usefulWidth = getClientArea().width - thumb.getBounds().width;
		int width = Maths.round(usefulWidth * percentage + thumb.getBounds().width / 2);
		moveThumbHorizontally(width);
		previousPosition = percentage;
		this.notify = true;
	}

	/**
	 *
	 * @param toolTipInterperter
	 */
	public void setTooltipInterperter(final IToolTipProvider toolTipInterperter) {
		checkWidget();
		this.toolTipInterperter = toolTipInterperter;
	}

	@Override
	public void setBackground(final Color color) {
		checkWidget();
		thumb.setBackground(color);
		rightRegion.setBackground(color);
		leftRegion.setBackground(color);
		super.setBackground(color);
	}

	public void setPopupBackground(final GamaUIColor color) {
		popupColor = color;
	}

	//
	// @Override
	// public Point computeSize(final int wHint, final int hHint) {
	// checkWidget();
	// int width = minWidth;
	// int height = minHeight;
	// if ( wHint != SWT.DEFAULT ) {
	// width = wHint;
	// }
	// if ( hHint != SWT.DEFAULT ) {
	// height = hHint;
	// }
	// return new Point(width, height);
	// }

	@Override
	public void setToolTipText(final String string) {
		super.setToolTipText(string);
		thumb.setToolTipText(string);
		rightRegion.setToolTipText(string);
		leftRegion.setToolTipText(string);
	}

	/**
	 * @see msi.gama.gui.swt.controls.IPopupProvider#getPopupText()
	 */
	@Override
	public String getPopupText() {
		double value = getCurrentPosition();
		return toolTipInterperter == null ? String.valueOf(value) : toolTipInterperter.getToolTipText(value);
	}

	/**
	 * @see msi.gama.gui.swt.controls.IPopupProvider#getPopupBackground()
	 */
	@Override
	public GamaUIColor getPopupBackground() {
		return popupColor;
	}

	@Override
	public Point getAbsoluteOrigin() {
		return leftRegion.toDisplay(new Point(leftRegion.getLocation().x, sliderHeight*2));
	}

	@Override
	public Shell getControllingShell() {
		return leftRegion.getShell();
	}

	public void specifyHeight(int heightsize) {
		sliderHeight = heightsize;		
	}
}
