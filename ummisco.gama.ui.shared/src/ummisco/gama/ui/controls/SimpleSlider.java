/*******************************************************************************************************
 *
 * SimpleSlider.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.controls;

import static org.eclipse.jface.layout.GridDataFactory.swtDefaults;
import static org.eclipse.jface.layout.GridLayoutFactory.fillDefaults;
import static org.eclipse.swt.SWT.BEGINNING;
import static org.eclipse.swt.SWT.DOUBLE_BUFFERED;
import static org.eclipse.swt.SWT.FILL;
import static org.eclipse.swt.SWT.NO_BACKGROUND;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.IGamaColors;

/**
 * The Class SimpleSlider.
 */
public class SimpleSlider extends Composite implements IPopupProvider {

	static {
		DEBUG.OFF();
	}

	/** The Constant THUMB_WIDTH. */
	final static public int THUMB_WIDTH = 6;

	/** The Constant THUMB_HEIGHT. */
	final static public int THUMB_HEIGHT = 13;

	/** The Constant PANEL_HEIGHT. */
	final static public int PANEL_HEIGHT = 3;

	/** The parent. */
	final Composite parent;

	/** The thumb. */
	final Thumb thumb;

	/** The right region. */
	final Panel leftRegion, rightRegion;

	/** The mouse down. */
	boolean mouseDown = false;

	/** The slider height. */
	private int sliderHeight;

	/** The step. */
	private Double step = null;

	/** The tool tip interperter. */
	private IToolTipProvider toolTipInterperter;

	/** The position changed listeners. */
	private final List<IPositionChangeListener> positionChangedListeners = new ArrayList<>();
	/**
	 * stores the previous position that was sent out to the position changed listeners
	 */
	double previousPosition = 0;

	/** The popup color. */
	GamaUIColor popupColor = IGamaColors.GRAY_LABEL;

	/** The popup. */
	Popup2 popup = null;

	/** The notify. */
	private boolean notify = true;

	/** The popup listener. */
	private final IPositionChangeListener popupListener = (slider, position) -> popup.display();

	/**
	 * Instantiates a new simple slider.
	 *
	 * @param parent
	 *            the parent
	 * @param leftColor
	 *            the left color
	 * @param rightColor
	 *            the right color
	 * @param thumbColor
	 *            the thumb color
	 * @param withPopup
	 *            the with popup
	 */
	public SimpleSlider(final Composite parent, final Color leftColor, final Color rightColor, final Color thumbColor,
			final boolean withPopup) {
		super(parent, SWT.DOUBLE_BUFFERED | SWT.INHERIT_DEFAULT);
		this.parent = parent;
		fillDefaults().numColumns(3).spacing(0, 0).applyTo(this);
		leftRegion = new Panel(this, leftColor);
		leftRegion.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(final MouseEvent e) {
				mouseDown = true;
				moveThumbHorizontally(e.x - THUMB_WIDTH / 2);
			}

			@Override
			public void mouseUp(final MouseEvent e) {
				mouseDown = false;
			}
		});
		leftRegion.addMouseMoveListener(e -> { if (mouseDown) { moveThumbHorizontally(e.x - THUMB_WIDTH / 2); } });
		thumb = new Thumb(this, thumbColor);
		thumb.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(final MouseEvent e) {
				mouseDown = true;
				moveThumbHorizontally(leftRegion.getBounds().width + e.x - THUMB_WIDTH / 2);
			}

			@Override
			public void mouseUp(final MouseEvent e) {
				mouseDown = false;
			}
		});
		thumb.addMouseMoveListener(e -> {
			if (mouseDown) { moveThumbHorizontally(leftRegion.getBounds().width + e.x - THUMB_WIDTH / 2); }
		});

		rightRegion = new Panel(this, rightColor, true);
		rightRegion.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(final MouseEvent e) {
				mouseDown = true;
				moveThumbHorizontally(leftRegion.getBounds().width + thumb.getBounds().width / 2 + e.x);
			}

			@Override
			public void mouseUp(final MouseEvent e) {
				mouseDown = false;
			}
		});
		rightRegion.addMouseMoveListener(e -> {
			if (mouseDown) {
				moveThumbHorizontally(leftRegion.getBounds().width + thumb.getBounds().width / 2 + e.x);

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

		addTraverseListener(e -> e.doit = true);
		if (withPopup) {
			addPositionChangeListener(popupListener);
			popup = new Popup2(this, leftRegion, thumb, rightRegion);
		} else {
			popup = null;
		}
		// if (DEBUG.IS_ON()) {
		// addPositionChangeListener((slider, position) -> DEBUG.OUT("Position changed to : " + position));
		// }
	}

	/**
	 * Adds the position change listener.
	 *
	 * @param listener
	 *            the listener
	 */
	public void addPositionChangeListener(final IPositionChangeListener listener) {
		synchronized (positionChangedListeners) {
			if (!positionChangedListeners.contains(listener)) { positionChangedListeners.add(listener); }
		}
	}

	/**
	 *
	 * @return the position of the slider in the form of a percentage. Note the range is from 0 to 1
	 */
	public double getCurrentPosition() { return previousPosition; }

	/**
	 * Update position listeners.
	 *
	 * @param perc
	 *            the perc
	 */
	private void updatePositionListeners(final double perc) {
		if (!notify) return;
		if (Math.abs(perc - previousPosition) > 0.000001) {
			final Iterator<IPositionChangeListener> iter = positionChangedListeners.iterator();
			while (iter.hasNext()) { iter.next().positionChanged(SimpleSlider.this, perc); }
		}
	}

	/**
	 * Move thumb horizontally.
	 *
	 * @param x
	 *            the x
	 */
	void moveThumbHorizontally(final int x) {
		final int width = getClientArea().width - THUMB_WIDTH;
		int pos = x < 0 ? 0 : x > width ? width : x;
		double percentage = pos / (double) width;
		if (step != null) { percentage = Math.round(percentage / step) * step; }
		pos = (int) (percentage * width);
		thumb.setFocus();
		leftRegion.updatePosition(pos);
		layout();
		updatePositionListeners(percentage);
		previousPosition = percentage;
	}

	/**
	 * Method to update current position of the slider
	 *
	 * @param percentage
	 *            between 0 and 1 (i.e 0% to 100%)
	 */
	public void updateSlider(final double p, final boolean n) {
		double percentage = p;
		if (step != null) { percentage = Math.round(percentage / step) * step; }
		this.notify = n;
		if (percentage < 0) {
			percentage = 0;
		} else if (percentage > 1) { percentage = 1; }
		final int usefulWidth = getClientArea().width - THUMB_WIDTH;
		final int width = (int) Math.round(usefulWidth * percentage);
		moveThumbHorizontally(width);
		previousPosition = percentage;
		this.notify = true;
	}

	/**
	 *
	 * @param toolTipInterperter
	 */
	public void setTooltipInterperter(final IToolTipProvider toolTipInterperter) {
		this.toolTipInterperter = toolTipInterperter;
	}

	@Override
	public void setBackground(final Color color) {
		if (color != null) { GamaColors.setBackground(color, thumb, rightRegion, leftRegion); }
		super.setBackground(color);
	}

	/**
	 * Sets the left background.
	 *
	 * @param color
	 *            the new left background
	 */
	public void setLeftBackground(final Color color) {
		leftRegion.setBackground(color);
	}

	/**
	 * Sets the right background.
	 *
	 * @param color
	 *            the new right background
	 */
	public void setRightBackground(final Color color) {
		rightRegion.setBackground(color);
	}

	/**
	 * Sets the popup background.
	 *
	 * @param color
	 *            the new popup background
	 */
	public void setPopupBackground(final GamaUIColor color) { popupColor = color; }

	@Override
	public void setToolTipText(final String string) {
		super.setToolTipText(string);
		thumb.setToolTipText(string);
		rightRegion.setToolTipText(string);
		leftRegion.setToolTipText(string);
	}

	/**
	 * @see ummisco.gama.ui.controls.IPopupProvider#getPopupText()
	 */
	@Override
	public PopupText getPopupText() {
		final double value = getCurrentPosition();
		final String text =
				toolTipInterperter == null ? String.valueOf(value) : toolTipInterperter.getToolTipText(value);
		return PopupText.with(popupColor, text);
	}

	@Override
	public Point getAbsoluteOrigin() {
		return leftRegion.toDisplay(new Point(leftRegion.getLocation().x, -sliderHeight));
	}

	@Override
	public Shell getControllingShell() { return leftRegion.getShell(); }

	/**
	 * Specify height.
	 *
	 * @param heightsize
	 *            the heightsize
	 */
	public void specifyHeight(final int heightsize) {
		sliderHeight = heightsize;
	}

	/**
	 * Sets the step.
	 *
	 * @param realStep
	 *            the new step
	 */
	public void setStep(final Double realStep) {
		if (realStep != null && realStep > 0d) { step = realStep; }
	}

	/**
	 * The Class Thumb.
	 */
	public class Thumb extends Canvas implements PaintListener {

		/** The color. */
		final Color color;

		/**
		 * Instantiates a new thumb.
		 *
		 * @param parent
		 *            the parent
		 * @param thumbColor
		 *            the thumb color
		 */
		public Thumb(final Composite parent, final Color thumbColor) {
			super(parent, NO_BACKGROUND);
			color = thumbColor;
			addPaintListener(this);
			swtDefaults().hint(THUMB_WIDTH, THUMB_HEIGHT).minSize(THUMB_WIDTH, THUMB_HEIGHT).align(BEGINNING, SWT.FILL)
					.grab(false, true).applyTo(this);
		}

		@Override
		public boolean forceFocus() {
			return true;
		}

		@Override
		public Point computeSize(final int w, final int h) {
			return new Point(THUMB_WIDTH, THUMB_HEIGHT);
		}

		@Override
		public void paintControl(final PaintEvent e) {
			final GC gc = e.gc;
			// DEBUG.OUT("Thumb bounds " + getBounds() + " client area: " + getClientArea() + " gc clipping: "
			// + gc.getClipping());
			final Rectangle r = gc.getClipping();
			gc.setBackground(parent.getBackground());
			gc.fillRectangle(r);
			gc.setBackground(color);
			gc.fillRoundRectangle(0, (r.height - THUMB_HEIGHT) / 2 + 1, THUMB_WIDTH, THUMB_HEIGHT, 3, 3);
		}
	}

	/**
	 * The Class Panel.
	 */
	private class Panel extends Canvas implements PaintListener {

		/** The gd. */
		private final GridData gd;

		/** The color. */
		private final Color color;

		/**
		 * Instantiates a new panel.
		 *
		 * @param parent
		 *            the parent
		 * @param color
		 *            the color
		 */
		public Panel(final Composite parent, final Color color) {
			this(parent, color, false);
		}

		/**
		 * Instantiates a new panel.
		 *
		 * @param parent
		 *            the parent
		 * @param color
		 *            the color
		 * @param last
		 *            the last
		 */
		public Panel(final Composite parent, final Color color, final boolean last) {
			super(parent, DOUBLE_BUFFERED | NO_BACKGROUND);
			gd = swtDefaults().minSize(0, PANEL_HEIGHT).align(last ? FILL : BEGINNING, BEGINNING).grab(last, false)
					.create();
			this.color = color;
			setLayoutData(gd);
			addPaintListener(this);
		}

		/**
		 * Update position.
		 *
		 * @param value
		 *            the value
		 */
		void updatePosition(final int value) {
			gd.minimumWidth = value;
			gd.widthHint = value;
		}

		/**
		 * Method paintControl()
		 *
		 * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
		 */
		@Override
		public void paintControl(final PaintEvent e) {
			final GC gc = e.gc;
			// DEBUG.OUT("Panel bounds " + getBounds() + " client area: " + getClientArea() + " gc clipping: "
			// + gc.getClipping() + " parent bounds " + parent.getBounds() + " parent size " + parent.getSize());
			final Rectangle r = gc.getClipping();
			gc.setBackground(parent.getBackground());
			gc.fillRectangle(r);
			gc.setBackground(color);
			gc.fillRoundRectangle(r.x, (int) ((double) r.height / 2 - 1d), r.width, PANEL_HEIGHT, 3, 3);
		}

	}

}
