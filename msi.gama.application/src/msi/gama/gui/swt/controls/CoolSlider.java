package msi.gama.gui.swt.controls;

import java.util.*;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SWT.HORIZONTAL</dd>
 * <dd>SWT.VERTICAL</dd>
 * <dd>SMOOTH_STYLE</dd>
 * <dd>SNAP_STYLE</dd>
 * </dl>
 * <p>
 * 
 * The <code>CoolSlider</code> is a slider class created to provide a dynamic way to have a really (cool) looking
 * slider. The look, graphical behaviour and shape is totally up to the developer that makes use of this class. <br>
 * <br>
 * 
 * The <code>CoolSlider</code> makes use of a minimum of 5 <code>org.eclipse.swt.graphics.Image</code> classes to give
 * it a custom appearance. 2 of the images are needed to be used as tiles. 2 more images are needed as leftmost and
 * rightmost (or topmost and bottom most, depending on the style used) these 2 images are not used as tiles. <br>
 * To create the thumb of the slider at least one image will need to be specified for this. The thumb is really a
 * <code>CoolButton</code> (with style SWT.PUSH) so one can specify 3 images for each state of the button. To indicate
 * focus on the slider one can provide 3 images for the focus images of the <code>CoolButton</code>.<br>
 * <br>
 * 
 * The <code>CoolSlider</code> can behave like a horizontal slider (using: <code>SWT.HORIZONTAL</code> or a vertical
 * slider (using: <code>SWT.VERTICAL</code>). <br>
 * <br>
 * 
 * The <code>CoolSlider</code> can move its thumb smoothly (SMOOTH STYLE) or in fixed increments (SNAP STYLE). When
 * using SNAP_STYLE make sure that one calls the <code>setSnapValues<code> method. 
 * 
 * 
 * 
 * <br><br>
 * Sample on how to use the <code>CoolSlider</code> is provided in the samples package. <br>
 * <br>
 * 
 * @author Code Crofter & Alexis Drogoul (popup contribution)
 * 
 * @since RCP Toolbox v0.1 <br>
 * 
 */
public class CoolSlider extends Composite implements IPopupProvider {

	private final IPositionChangeListener popupListener = new IPositionChangeListener() {

		@Override
		public void positionChanged(final double position) {
			popup.display();
		}
	};

	public static final int SMOOTH_STYLE = 0;
	private static final int SNAP_STYLE = 1;
	/**
	 * Right most region, or bottom most region (SWT.VERTICAL). Image used in this region is not
	 * tiled
	 */
	private final CoolSliderPanel rightmostRegion;
	/** Right region, or bottom region (SWT.VERTICAL). Image used in this region is tiled */
	private final CoolSliderPanel rightRegion;
	/** Thumb coolButton */
	private final CoolButton thumb;
	/** Left region, or top region (SWT.VERTICAL). Image used in this region is tiled */
	private final CoolSliderPanel leftRegion;
	/** Left most region, or topmost region (SWT.VERTICAL). Image used in this region is not tiled */
	private final CoolSliderPanel leftmostRegion;
	/** Flag indicating that the mouse is down */
	private boolean mouseDown = false;
	/**
	 * Flag indicating that the style is horizontal. If not horizontal the vertical is assumed and
	 * visa versa
	 */
	private final boolean horizontal;
	/** The percentage of the slider that is used when the position of the slider updated */
	// private double jumpSize = 0;
	/** A lock object for the <code>CoolSliderToolTipInterperter</code> */
	private final Object tooltipIntLock = new Object();
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

	private int maximum = 100;
	private int minimum = 0;
	private int incrementValue = 1;
	private final boolean snapStyle;
	private final List<Integer> snapPoints = new ArrayList<Integer>();
	// private int jumpIncrement = 1;

	private Color popupColor;
	private final Popup popup;

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
	public CoolSlider(final Composite parent, final int style, final Image leftmost, final Image left,
		final Image thumbImageNormal, final Image thumbImageHover, final Image thumbImagePressed, final Image right,
		final Image rightmost) {
		this(parent, style, leftmost, left, thumbImageNormal, thumbImageHover, thumbImagePressed, thumbImageNormal,
			thumbImageHover, thumbImagePressed, right, rightmost);
	}

	/**
	 * The constructor for the <code>CoolSlider</code> to
	 * create slider either in vertical or horizontal position. <br>
	 * 
	 * An additional option is given here to specify the focus images for the thumb.
	 * 
	 * @param parent
	 * @param style <code>SWT.VERTICAL</code> or <code>SWT.HORIZONTAL</code>
	 * @param leftmost
	 * @param left
	 * @param thumbImageNormal
	 * @param thumbImageHover
	 * @param thumbImagePressed
	 * @param thumbImageFocusedNormal
	 * @param thumbImageFocusedHover
	 * @param thumbImageFocusedPressed
	 * @param right
	 * @param rightmost
	 */
	public CoolSlider(final Composite parent, final int style, final Image leftmost, final Image left,
		final Image thumbImageNormal, final Image thumbImageHover, final Image thumbImagePressed,
		final Image thumbImageFocusedNormal, final Image thumbImageFocusedHover, final Image thumbImageFocusedPressed,
		final Image right, final Image rightmost) {
		super(parent, SWT.DOUBLE_BUFFERED);
		horizontal = SWT.VERTICAL != (style & SWT.VERTICAL);
		snapStyle = SNAP_STYLE == (style & SNAP_STYLE);
		final GridLayout gl = new GridLayout(horizontal ? 5 : 1, false);
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		setLayout(gl);
		minWidth = leftmost.getBounds().width + rightmost.getBounds().width + thumbImageNormal.getBounds().width;
		minHeight = leftmost.getBounds().height + rightmost.getBounds().height + thumbImageNormal.getBounds().height;
		leftmostRegion = new CoolSliderPanel(this, leftmost, horizontal, false, true);
		leftmostRegion.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(final MouseEvent e) {
				mouseDown = true;
				if ( CoolSlider.this.horizontal ) {
					moveThumbHorizontally(e.x);
				} else {
					moveThumbVertically(e.y);
				}
			}

			@Override
			public void mouseUp(final MouseEvent e) {
				mouseDown = false;
			}
		});
		leftmostRegion.addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(final MouseEvent e) {
				if ( mouseDown ) {
					if ( CoolSlider.this.horizontal ) {
						moveThumbHorizontally(e.x);
					} else {
						moveThumbVertically(e.y);
					}
				}
			}
		});

		leftRegion = new CoolSliderPanel(this, left, horizontal);
		leftRegion.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(final MouseEvent e) {
				mouseDown = true;
				if ( CoolSlider.this.horizontal ) {
					moveThumbHorizontally(leftmostRegion.getBounds().width + e.x);
				} else {
					moveThumbVertically(leftmostRegion.getBounds().height + e.y);
				}
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
					if ( CoolSlider.this.horizontal ) {
						moveThumbHorizontally(leftmostRegion.getBounds().width + e.x);
					} else {
						moveThumbVertically(leftmostRegion.getBounds().height + e.y);
					}
				}
			}
		});

		thumb =
			new CoolButton(this, thumbImageNormal, thumbImageHover, thumbImagePressed, thumbImageNormal,
				thumbImageFocusedNormal, thumbImageFocusedHover, thumbImageFocusedPressed);

		thumb.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(final MouseEvent e) {
				mouseDown = true;
				if ( CoolSlider.this.horizontal ) {
					moveThumbHorizontally(leftmostRegion.getBounds().width + leftRegion.getBounds().width + e.x);
				} else {
					moveThumbVertically(leftmostRegion.getBounds().height + leftRegion.getBounds().height + e.y);
				}
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
					if ( CoolSlider.this.horizontal ) {
						moveThumbHorizontally(leftmostRegion.getBounds().width + leftRegion.getBounds().width + e.x);
					} else {
						moveThumbVertically(leftmostRegion.getBounds().height + leftRegion.getBounds().height + e.y);
					}
				}
			}
		});

		thumb.addMouseTrackListener(new MouseTrackAdapter() {

			@Override
			public void mouseHover(final MouseEvent e) {
				double perc = -1;
				if ( CoolSlider.this.horizontal ) {
					perc =
						(leftmostRegion.getBounds().width * 1.0 + leftRegion.getBounds().width) /
							(getClientArea().width - thumb.getBounds().width);
				} else {
					perc =
						(leftmostRegion.getBounds().height + leftRegion.getBounds().height * 1.0) /
							(getClientArea().height - thumb.getBounds().height);
				}
				synchronized (tooltipIntLock) {
					if ( toolTipInterperter != null ) {
						final String tooltip = toolTipInterperter.getToolTipText(perc);
						if ( tooltip != null ) {
							thumb.setToolTipText(tooltip);
						}
					}
				}
			}
		});

		rightRegion = new CoolSliderPanel(this, right, horizontal, true, false);
		rightRegion.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(final MouseEvent e) {
				mouseDown = true;
				if ( CoolSlider.this.horizontal ) {
					moveThumbHorizontally(leftmostRegion.getBounds().width + leftRegion.getBounds().width +
						thumb.getBounds().width + e.x);
				} else {
					moveThumbVertically(leftmostRegion.getBounds().height + leftRegion.getBounds().height +
						thumb.getBounds().height + e.y);
				}
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
					if ( CoolSlider.this.horizontal ) {
						moveThumbHorizontally(leftmostRegion.getBounds().width + leftRegion.getBounds().width +
							thumb.getBounds().width + e.x);
					} else {
						moveThumbVertically(leftmostRegion.getBounds().height + leftRegion.getBounds().height +
							thumb.getBounds().height + e.y);
					}
				}
			}
		});

		rightmostRegion = new CoolSliderPanel(this, rightmost, horizontal, false, true);
		rightmostRegion.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(final MouseEvent e) {
				mouseDown = true;
				if ( CoolSlider.this.horizontal ) {
					moveThumbHorizontally(leftmostRegion.getBounds().width + leftRegion.getBounds().width +
						thumb.getBounds().width + rightRegion.getBounds().width + e.x);
				} else {
					moveThumbVertically(leftmostRegion.getBounds().height + leftRegion.getBounds().height +
						thumb.getBounds().height + rightRegion.getBounds().height + e.y);
				}
			}

			@Override
			public void mouseUp(final MouseEvent e) {
				mouseDown = false;
			}
		});
		rightmostRegion.addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(final MouseEvent e) {
				if ( mouseDown ) {
					if ( CoolSlider.this.horizontal ) {
						moveThumbHorizontally(leftmostRegion.getBounds().width + leftRegion.getBounds().width +
							thumb.getBounds().width + rightRegion.getBounds().width + e.x);
					} else {
						moveThumbVertically(leftmostRegion.getBounds().height + leftRegion.getBounds().height +
							thumb.getBounds().height + rightRegion.getBounds().height + e.y);
					}
				}
			}
		});

		addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(final ControlEvent e) {
				if ( snapStyle ) {
					updateSnapPoints();
				}
				if ( CoolSlider.this.horizontal ) {
					moveThumbHorizontally((int) (getClientArea().width * previousPosition), false);
				} else {
					moveThumbVertically((int) (getClientArea().height * previousPosition), false);
				}
			}
		});

		addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(final FocusEvent e) {
				thumb.setFocus();
			}
		});

		// thumb.addKeyListener(new KeyAdapter() {
		//
		// @Override
		// public void keyPressed(final KeyEvent e) {
		// if ( CoolSlider.this.horizontal ) {
		// final int currentThumbPos =
		// leftmostRegion.getBounds().width + leftRegion.getBounds().width +
		// thumb.getBounds().width / 2;
		// if ( e.keyCode == SWT.ARROW_LEFT ) {
		// int newPos = 0;
		// if ( snapStyle ) {
		// newPos =
		// currentThumbPos -
		// (int) (getClientArea().width / ((maximum - minimum) * 1.0 / jumpIncrement));
		// } else {
		// newPos = currentThumbPos - (int) (getClientArea().width * jumpSize);
		// }
		// if ( newPos < 0 ) {
		// newPos = 0;
		// }
		// moveThumbHorizontally(newPos);
		// } else if ( e.keyCode == SWT.ARROW_RIGHT ) {
		// int newPos = 0;
		// if ( snapStyle ) {
		// newPos =
		// currentThumbPos +
		// (int) (getClientArea().width / ((maximum - minimum) * 1.0 / jumpIncrement));
		// } else {
		// newPos = currentThumbPos + (int) (getClientArea().width * jumpSize);
		// }
		// if ( newPos > getClientArea().width ) {
		// newPos = getClientArea().width;
		// }
		// moveThumbHorizontally(newPos);
		// }
		// } else {
		// final int currentThumbPos =
		// leftmostRegion.getBounds().height + leftRegion.getBounds().height +
		// thumb.getBounds().height / 2;
		// if ( e.keyCode == SWT.ARROW_UP ) {
		// int newPos = 0;
		// if ( snapStyle ) {
		// newPos =
		// currentThumbPos -
		// (int) (getClientArea().height / ((maximum - minimum) * 1.0 / jumpIncrement));
		// } else {
		// newPos = currentThumbPos - (int) (getClientArea().height * jumpSize);
		// }
		// if ( newPos < 0 ) {
		// newPos = 0;
		// }
		// moveThumbVertically(newPos);
		// } else if ( e.keyCode == SWT.ARROW_DOWN ) {
		// int newPos = 0;
		// if ( snapStyle ) {
		// newPos =
		// currentThumbPos +
		// (int) (getClientArea().height / ((maximum - minimum) * 1.0 / jumpIncrement));
		// } else {
		// newPos = currentThumbPos + (int) (getClientArea().height * jumpSize);
		// }
		// if ( newPos > getClientArea().height ) {
		// newPos = getClientArea().height;
		// }
		// moveThumbVertically(newPos);
		// }
		// }
		// }
		// });

		addTraverseListener(new TraverseListener() {

			@Override
			public void keyTraversed(final TraverseEvent e) {
				e.doit = true;
			}
		});
		addPositionChangeListener(popupListener);
		popup = new Popup(this, leftmostRegion, leftRegion, thumb, rightRegion, rightmostRegion);

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
		if ( horizontal ) { return (leftmostRegion.getBounds().width + leftRegion.getBounds().width * 1.0) /
			(getClientArea().width - thumb.getBounds().width); }
		return (leftmostRegion.getBounds().height + leftRegion.getBounds().height * 1.0) /
			(getClientArea().height - thumb.getBounds().height);
	}

	private void updatePostionListeners(final double perc) {
		if ( previousPosition != -1 && perc != previousPosition ) {
			synchronized (positionChangedListeners) {
				final Iterator<IPositionChangeListener> iter = positionChangedListeners.iterator();
				while (iter.hasNext()) {
					iter.next().positionChanged(perc);
				}
			}
		}
	}

	private void updateTooltipMoving(final double perc) {
		synchronized (tooltipIntLock) {
			if ( toolTipInterperter != null ) {
				final String tooltip = toolTipInterperter.getToolTipText(perc);
				if ( tooltip != null ) {
					thumb.setToolTipText(tooltip);
				}
			}
		}
	}

	private void moveThumbHorizontally(int x) {
		if ( x < 0 ) {
			x = 0;
		}
		if ( snapStyle && getClientArea().width > 0 ) {
			x = getClosestSnapPosition(x);
		}
		moveThumbHorizontally(x, true);
	}

	private void moveThumbHorizontally(final int x, final boolean update) {
		thumb.setFocus();
		int width = x - thumb.getBounds().width / 2 - leftmostRegion.getBounds().width;
		if ( width > getClientArea().width - thumb.getBounds().width - rightmostRegion.getBounds().width ) {
			width = getClientArea().width - thumb.getBounds().width - rightmostRegion.getBounds().width;
		}
		if ( width < 0 ) {
			int mW = leftmostRegion.getBounds().width + width;
			if ( mW < 0 ) {
				mW = 0;
			}
			leftmostRegion.updatePosition(mW);
			width = 0;
		} else {
			leftmostRegion.resetToDefault();
		}
		leftRegion.updatePosition(width);
		layout();
		if ( update ) {
			double percentage = 0;
			if ( snapStyle ) {
				percentage = x * 1.0 / getClientArea().width;
				percentage = Math.round((maximum - minimum) * percentage) + minimum;
			} else {
				percentage =
					(leftmostRegion.getBounds().width + leftRegion.getBounds().width * 1.0) /
						(getClientArea().width - thumb.getBounds().width);
			}
			updateTooltipMoving(percentage);
			updatePostionListeners(percentage);
			previousPosition = percentage;
		}
	}

	private int getClosestSnapPosition(final int distance) {
		final int len = snapPoints.size();
		int cnt = 0;
		int val = snapPoints.get(cnt).intValue();
		int prevVal = snapPoints.get(cnt).intValue();
		while (cnt < len) {
			if ( val == distance || distance < val && distance > prevVal ) {
				final int mid = (val - prevVal) / 2 + prevVal;
				if ( distance >= mid ) { return val; }
				return prevVal;
			}
			prevVal = val;
			cnt++;
			if ( cnt < len ) {
				val = snapPoints.get(cnt).intValue();
			}
		}
		return snapPoints.get(len - 1).intValue();
	}

	private void moveThumbVertically(int y) {
		if ( y < 0 ) {
			y = 0;
		}
		if ( snapStyle && getClientArea().height > 0 ) {
			y = getClosestSnapPosition(y);
		}
		moveThumbVertically(y, true);
	}

	private void moveThumbVertically(final int y, final boolean update) {
		thumb.setFocus();
		int height = y - thumb.getBounds().height / 2 - leftmostRegion.getBounds().height;
		if ( height > getClientArea().height - thumb.getBounds().height - rightmostRegion.getBounds().height ) {
			height = getClientArea().height - thumb.getBounds().height - rightmostRegion.getBounds().height;
		}
		if ( height < 0 ) {
			int mH = leftmostRegion.getBounds().height + height;
			if ( mH < 0 ) {
				mH = 0;
			}
			leftmostRegion.updatePosition(mH);
			height = 0;
		} else {
			leftmostRegion.resetToDefault();
		}
		leftRegion.updatePosition(height);
		layout();
		if ( update ) {
			double percentage = 0;
			if ( snapStyle ) {
				percentage = y * 1.0 / getClientArea().height;
				percentage = Math.round((maximum - minimum) * percentage) + minimum;
			} else {
				percentage =
					(leftmostRegion.getBounds().height + leftRegion.getBounds().height * 1.0) /
						(getClientArea().height - thumb.getBounds().height);
			}
			updateTooltipMoving(percentage);
			updatePostionListeners(percentage);
			previousPosition = percentage;
		}
	}

	private class CoolSliderPanel extends Composite {

		private final GridData gd;
		private final GridData gdDefault;
		private boolean layoutDatachanged = false;

		public CoolSliderPanel(final Composite parent, final Image image, final boolean horizontal) {
			this(parent, image, horizontal, false, false);
		}

		public CoolSliderPanel(final Composite parent, final Image image, final boolean horizontal,
			final boolean lastFillerRegion, final boolean lastmostRegion) {
			super(parent, SWT.NONE);
			gd =
				new GridData(lastFillerRegion && horizontal && !lastmostRegion ? SWT.FILL : SWT.BEGINNING,
					lastFillerRegion && !horizontal && !lastmostRegion ? SWT.FILL : SWT.BEGINNING, lastFillerRegion &&
						horizontal && !lastmostRegion, lastFillerRegion && !horizontal && !lastmostRegion);
			gdDefault =
				new GridData(lastFillerRegion && horizontal && !lastmostRegion ? SWT.FILL : SWT.BEGINNING,
					lastFillerRegion && !horizontal && !lastmostRegion ? SWT.FILL : SWT.BEGINNING, lastFillerRegion &&
						horizontal && !lastmostRegion, lastFillerRegion && !horizontal && !lastmostRegion);
			if ( horizontal ) {
				gd.minimumHeight = image.getBounds().height;
				gd.heightHint = image.getBounds().height;
				gdDefault.minimumHeight = image.getBounds().height;
				gdDefault.heightHint = image.getBounds().height;
			} else {
				gd.minimumWidth = image.getBounds().width;
				gd.widthHint = image.getBounds().width;
				gdDefault.minimumWidth = image.getBounds().width;
				gdDefault.widthHint = image.getBounds().width;
			}
			if ( lastmostRegion ) {
				gd.minimumHeight = image.getBounds().height;
				gd.heightHint = image.getBounds().height;
				gd.minimumWidth = image.getBounds().width;
				gd.widthHint = image.getBounds().width;
				gdDefault.minimumHeight = image.getBounds().height;
				gdDefault.heightHint = image.getBounds().height;
				gdDefault.minimumWidth = image.getBounds().width;
				gdDefault.widthHint = image.getBounds().width;
			}
			setLayoutData(gd);
			setBackgroundImage(image);
		}

		void updatePosition(final int value) {
			layoutDatachanged = true;
			if ( horizontal ) {
				gd.minimumWidth = value;
				gd.widthHint = value;
			} else {
				gd.minimumHeight = value;
				gd.heightHint = value;
			}
			setLayoutData(gd);
		}

		void resetToDefault() {
			if ( layoutDatachanged ) {
				setLayoutData(gdDefault);
				layoutDatachanged = false;
			}
		}
	}

	/**
	 * This will set the max and min of the range of integer values that the slider will snap to.
	 * The incrementValue will be used to indicate the smallest possible increment before it will
	 * "snap" to
	 * a position.
	 * @param max
	 * @param min
	 * @param incrementValue
	 */
	public void setSnapValues(final int max, final int min, final int incrementValue) {
		checkWidget();
		if ( !snapStyle ) { return; }
		if ( min > max ) { throw new IllegalArgumentException("The maximum must be greater than the minimum"); }
		if ( max < incrementValue ) { throw new IllegalArgumentException(
			"The maximum must be greater than the increment value"); }
		if ( max - min < incrementValue ) { throw new IllegalArgumentException(
			"The difference of the maximum and minimum must be greater than the increment value"); }
		if ( max < 0 ) { throw new IllegalArgumentException("The maximum must be a positive integer"); }
		if ( min < 0 ) { throw new IllegalArgumentException("The minimum must be a positive integer"); }
		if ( incrementValue < 0 ) { throw new IllegalArgumentException("The increment value must be a positive integer"); }
		maximum = max;
		minimum = min;
		this.incrementValue = incrementValue;
		if ( getClientArea().height > 0 && !horizontal || getClientArea().width > 0 && horizontal ) {
			updateSnapPoints();
		}
	}

	private void updateSnapPoints() {
		snapPoints.clear();
		final int increments = (int) ((maximum - minimum) * 1.0 / incrementValue);
		final int total = horizontal ? getClientArea().width : getClientArea().height;
		final double ratio = total * 1.0 / increments;
		for ( int i = 0; i < increments; i++ ) {
			snapPoints.add(new Integer((int) (ratio * i)));
		}
		snapPoints.add(new Integer(total));
	}

	/**
	 * Method to update current position of the slider
	 * 
	 * @param percentage between 0 and 1 (i.e 0% to 100%)
	 */
	public void updateSlider(double percentage) {
		checkWidget();
		if ( percentage < 0 ) {
			percentage = 0;
		} else if ( percentage > 1 ) {
			percentage = 1;
		}
		if ( CoolSlider.this.horizontal ) {
			moveThumbHorizontally((int) (getClientArea().width * percentage));
		} else {
			moveThumbVertically((int) (getClientArea().height * percentage));
		}
	}

	/**
	 * 
	 * @param toolTipInterperter
	 */
	public void setTooltipInterperter(final IToolTipProvider toolTipInterperter) {
		checkWidget();
		synchronized (tooltipIntLock) {
			this.toolTipInterperter = toolTipInterperter;
		}
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
		leftmostRegion.setBackground(color);
		rightmostRegion.setBackground(color);
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
		leftmostRegion.setToolTipText(string);
		rightmostRegion.setToolTipText(string);
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
	 * @see msi.gama.gui.swt.controls.IPopupProvider#getPositionControl()
	 */
	@Override
	public Control getPositionControl() {
		return leftmostRegion;
	}

	/**
	 * @see msi.gama.gui.swt.controls.IPopupProvider#getPopupBackground()
	 */
	@Override
	public Color getPopupBackground() {
		return popupColor;
	}
}
