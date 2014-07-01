package msi.gama.gui.swt.controls;

import java.util.*;
import java.util.List;
import msi.gaml.operators.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class CoolSlider2 extends Composite implements IPopupProvider {

	private final IPositionChangeListener popupListener = new IPositionChangeListener() {

		@Override
		public void positionChanged(final double position) {
			popup.display();
		}
	};

	public static final int SMOOTH_STYLE = 0;

	/** Right region, or bottom region (SWT.VERTICAL). Image used in this region is tiled */
	private final CoolSliderPanel2 rightRegion;
	/** Thumb coolButton */
	private final CoolButton thumb;
	/** Left region, or top region (SWT.VERTICAL). Image used in this region is tiled */
	private final CoolSliderPanel2 leftRegion;
	/** Left most region, or topmost region (SWT.VERTICAL). Image used in this region is not tiled */
	// private final CoolSliderPanel leftmostRegion;
	/** Flag indicating that the mouse is down */
	private boolean mouseDown = false;

	/** A lock object for the <code>CoolSliderToolTipInterperter</code> */
	// private final Object tooltipIntLock = new Object();
	/**
	 * The class implementing this interface will be asked to give a user understandable <code>String</code> to the
	 * slider's current position
	 */
	private IToolTipProvider toolTipInterperter;
	/** the minimum width of the slider */
	private final int minWidth;
	/** the minimum height of the slider */
	private final int minHeight;
	/** A list of position changed listeners */
	private final List<IPositionChangeListener> positionChangedListeners = new ArrayList<IPositionChangeListener>();
	/** stores the previous position that was sent out to the position changed listeners */
	private double previousPosition = -1;

	private Color popupColor;
	private final Popup popup;
	private boolean notify = true;

	/**
	 * The constructor for the <code>CoolSlider</code> to
	 * create slider either in vertical or horizontal position.
	 * 
	 * @param parent
	 * @param style <code>SWT.VERTICAL</code> or <code>SWT.HORIZONTAL</code>
	 * @param leftmost
	 * @param left
	 * @param thumbImageNormal
	 * @param thumbImageHover
	 * @param thumbImagePressed
	 * @param right
	 * @param rightmost
	 */
	public CoolSlider2(final Composite parent, final Image left, final Image thumbImageNormal,
		final Image thumbImagePressed) {
		super(parent, SWT.DOUBLE_BUFFERED);
		final GridLayout gl = new GridLayout(3, false);
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		setLayout(gl);
		minWidth = thumbImageNormal.getBounds().width;
		minHeight = thumbImageNormal.getBounds().height;

		leftRegion = new CoolSliderPanel2(this, left);
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

		thumb =
			new CoolButton(this, thumbImageNormal, thumbImagePressed, thumbImagePressed, thumbImageNormal,
				thumbImagePressed, thumbImagePressed, thumbImagePressed);

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
					// java.lang.System.out.println("left region width:" + leftRegion.getBounds().width + " Mouse x : " +
					// e.x);
					// guard for a very strange behavior of the SWT MouseEvent (when reaching the left end).
					// if ( e.x < 0 ) { return; }
					moveThumbHorizontally(leftRegion.getBounds().width + e.x);
				}
			}
		});

		thumb.addMouseTrackListener(new MouseTrackAdapter() {

			@Override
			public void mouseHover(final MouseEvent e) {
				double perc = -1;
				perc = leftRegion.getBounds().width / (getClientArea().width - thumb.getBounds().width);
				// synchronized (tooltipIntLock) {
				// if ( toolTipInterperter != null ) {
				// final String tooltip = toolTipInterperter.getToolTipText(perc);
				// if ( tooltip != null ) {
				// thumb.setToolTipText(tooltip);
				// }
				// }
				// }
			}
		});

		rightRegion = new CoolSliderPanel2(this, left, true);
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
				// moveThumbHorizontally((int) (getClientArea().width * previousPosition), false);

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
		// return leftRegion.getBounds().width * 1.0 / (getClientArea().width - thumb.getBounds().width);
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

	// private void updateTooltipMoving(final double perc) {
	// synchronized (tooltipIntLock) {
	// if ( toolTipInterperter != null ) {
	// final String tooltip = toolTipInterperter.getToolTipText(perc);
	// if ( tooltip != null ) {
	// thumb.setToolTipText(tooltip);
	// }
	// }
	// }
	// }

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

	private class CoolSliderPanel2 extends Composite {

		private final GridData gd;
		private final GridData gdDefault;

		public CoolSliderPanel2(final Composite parent, final Image image) {
			this(parent, image, false);
		}

		public CoolSliderPanel2(final Composite parent, final Image image, final boolean lastFillerRegion) {
			super(parent, SWT.NONE);
			gd = new GridData(lastFillerRegion ? SWT.FILL : SWT.BEGINNING, SWT.BEGINNING, lastFillerRegion, false);
			gdDefault =
				new GridData(lastFillerRegion ? SWT.FILL : SWT.BEGINNING, SWT.BEGINNING, lastFillerRegion, false);

			gd.minimumHeight = image.getBounds().height;
			gd.heightHint = image.getBounds().height;
			gdDefault.minimumHeight = image.getBounds().height;
			gdDefault.heightHint = image.getBounds().height;

			setLayoutData(gd);
			setBackgroundImage(image);
		}

		void updatePosition(final int value) {
			// System.out.println("Width of the left region: " + value + " : client area width: " +
			// CoolSlider2.this.getClientArea().width);
			gd.minimumWidth = value;
			gd.widthHint = value;
			setLayoutData(gd);
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
		// synchronized (tooltipIntLock) {
		this.toolTipInterperter = toolTipInterperter;
		// }
	}

	@Override
	public final void setLayout(final Layout layout) {
		super.setLayout(layout);
	}

	@Override
	public void setBackground(final Color color) {
		checkWidget();
		thumb.setBackground(color);
		rightRegion.setBackground(color);
		leftRegion.setBackground(color);
		super.setBackground(color);
	}

	public void setPopupBackground(final Color color) {
		popupColor = color;
	}

	@Override
	public Point computeSize(final int wHint, final int hHint) {
		checkWidget();
		int width = minWidth;
		int height = minHeight;
		if ( wHint != SWT.DEFAULT ) {
			width = wHint;
		}
		if ( hHint != SWT.DEFAULT ) {
			height = hHint;
		}
		return new Point(width, height);
	}

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
	public Color getPopupBackground() {
		return popupColor;
	}

	@Override
	public Point getAbsoluteOrigin() {
		return leftRegion.toDisplay(new Point(leftRegion.getLocation().x, leftRegion.getSize().y));
	}

	@Override
	public Shell getControllingShell() {
		return leftRegion.getShell();
	}
}
