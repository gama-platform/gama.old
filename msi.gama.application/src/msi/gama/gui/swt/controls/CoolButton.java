package msi.gama.gui.swt.controls;

import java.util.*;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SWT.PUSH</dd>
 * <dd>SWT.TOGGLE</dd>
 * </dl>
 * <p>
 * The <code>CoolButton</code> is a button class created to provide a dynamic way to have a really
 * good (cool) looking button. The look, graphical behaviour and shape is totally up to the
 * developer that makes use of this class. <br>
 * <br>
 * 
 * The <code>CoolButton</code> makes use of a number of <code>org.eclipse.swt.graphics.Image</code>
 * classes to give it a custom appearance. <br>
 * <br>
 * 
 * The <code>CoolButton</code> can behave like a push button (using: <code>SWT.PUSH</code>) or a
 * toggle button (using: <code>SWT.TOGGLE</code>). In the case of the toggle button its important to
 * provide the set of images that will be needed for it to behave as such. <br>
 * <br>
 * 
 * A hot region, an area on the <code>CoolButton</code> image that represents the mouse active
 * region of the button, can be specified by calling the
 * <code>setHotRegionMask(org.eclipse.swt.graphics.Image)</code>.<br>
 * This image must be black and white, the black region represents the hot region and the white
 * region represents the region that will not be considered during mouse events.<br>
 * Note this image should preferably be the same size as the image used to create the
 * <code>CoolButton</code>.<br>
 * <br>
 * 
 * Known limitations: The only known limitation of this implementation of the
 * <code>CoolButton</code> is the following: When the mouse is hovering over the button and the
 * button has focus (i.e tab key pressed until the button has focus); pressing and releasing the
 * space bar key (or any key that can select a button when it has focus) will cause the button to go
 * in its pressed state and back to the normal state not the hover state that it was previously in.
 * It will go back to the hover state once the mouse has been moved that will send a mouse hover
 * event to the <code>CoolButton</code>
 * 
 * 
 * <br>
 * <br>
 * Sample on how to use the <code>CoolButton</code> is provided in the samples package. <br>
 * <br>
 * 
 * @author Code Crofter
 *         On behalf Polymorph Systems
 * 
 * @since RCP Toolbox v0.1 <br>
 * 
 */
public class CoolButton extends Canvas {

	/** A flag indicating whether or not this button is a toggle button. */
	private final boolean isToggleButton;
	/**
	 * A flag indicating whether or not this button has been toggled.
	 * This flag is only useful if the <code>isToggleButton</code> flag
	 * is set.
	 */
	private boolean isToggled = false;

	/**
	 * The current state of the button.
	 */
	private ButtonState currentState;
	/** The normal state of the button */
	private final ButtonState normalState;
	/** The hover state of the button */
	private final ButtonState hoverState;
	/** The pressed state of the button */
	private final ButtonState pressedState;

	/** The image displayed during the normal state */
	private final Image normalStateImage;
	/** The image displayed during the normal state when the button has focus */
	private final Image normalStateFocusedImage;
	/** The image displayed during the hover state */
	private final Image hoverStateImage;
	/** The image displayed during the hover state when the button has focus */
	private final Image hoverStateFocusedImage;
	/** The image displayed during the pressed state */
	private final Image pressedStateImage;
	/** The image displayed during the pressed state when the button has focus */
	private final Image pressedStateFocusedImage;
	/** The image displayed during the normal state when the button is disabled */
	private final Image normalStateDisabledImage;
	/** The image displayed during the normal toggled state */
	private final Image normalToggledStateImage;
	/** The image displayed during the normal toggled state when the button has focus */
	private final Image normalToggledStateFocusedImage;
	/** The image displayed during the hover toggled state */
	private final Image hoverToggledStateImage;
	/** The image displayed during the hover toggled state when the button has focus */
	private final Image hoverToggledStateFocusedImage;
	/** The image displayed during the pressed toggled state */
	private final Image pressedToggledStateImage;
	/** The image displayed during the pressed toggled state when the button has focus */
	private final Image pressedToggledStateFocusedImage;
	/** The image displayed during the pressed toggled state when the button is disabled */
	private final Image normalToggledDisabledState;
	/** The tool tip reserved for the normal state */
	private String tooltipText;
	/** The tool tip reserved for the toggled state */
	private String toggledTooltipText;
	/** The flag indicating that the button has focus */
	private boolean focused = false;
	/** The list of <code>CoolButtonSelectionListener</code> listeners */
	private final List<CoolButtonSelectionListener> listeners =
		new ArrayList<CoolButtonSelectionListener>();
	/** Flag indicating that the CoolButton is set up with a hot spot */
	private boolean hotSpot = false;
	/** Flag indicating that the CoolButton is set up with a hot spot on its toggled state */
	private boolean hotSpotToggled = false;
	/** The image data used to calculate the hot spot */
	private ImageData hotSpotMatrix;
	/** The image data used to calculate the hot spot during the toggled state */
	private ImageData hotSpotMatrixToggled;
	/** Flag indicating that the mouse is in the hot spot */
	private boolean inHotSpot = true;

	/**
	 * The constructor for the <code>CoolButton</code> to
	 * create a simple push button. <code>SWT.PUSH</code> does not need to be specified. <br>
	 * <br>
	 * NOTE: The disable image will be the same as <code>normalStateImage</code> The focus image
	 * will be the same as the <code>normalStateImage</code>
	 * 
	 * @param parent
	 * @param normalStateImage
	 * @param hoverStateImage
	 * @param pressedStateImage
	 */
	public CoolButton(final Composite parent, final Image normalStateImage,
		final Image hoverStateImage, final Image pressedStateImage) {
		this(parent, SWT.PUSH, normalStateImage, hoverStateImage, pressedStateImage,
			normalStateImage, // disable image
			null, null, null, // toggle images
			null, // disable toggle image
			normalStateImage, hoverStateImage, pressedStateImage, // focus images
			null, null, null); // toggle focus images
	}

	/**
	 * The constructor for the <code>CoolButton</code> to
	 * create a simple push button. <code>SWT.PUSH</code> does not need to be specified. <br>
	 * <br>
	 * PUSH Button with disable image needed
	 * 
	 * <br>
	 * <br>
	 * NOTE: The focus image will be the same as the <code>normalStateImage</code>
	 * 
	 * @param parent
	 * @param normalStateImage
	 * @param hoverStateImage
	 * @param pressedStateImage
	 * @param normalStateDisabledImage
	 */
	public CoolButton(final Composite parent, final Image normalStateImage,
		final Image hoverStateImage, final Image pressedStateImage,
		final Image normalStateDisabledImage) {
		this(parent, SWT.PUSH, normalStateImage, hoverStateImage, pressedStateImage,
			normalStateDisabledImage, // disable image
			null, null, null, // toggle images
			null, // disable toggle image
			normalStateImage, hoverStateImage, pressedStateImage, // focus images
			null, null, null); // toggle focus images
	}

	/**
	 * The constructor for the <code>CoolButton</code> to
	 * create a simple push button. <code>SWT.PUSH</code> does not need to be specified.
	 * 
	 * PUSH Button with focus images and disable image needed
	 * 
	 * <br>
	 * <br>
	 * 
	 * @param parent
	 * @param normalStateImage
	 * @param hoverStateImage
	 * @param pressedStateImage
	 * @param normalStateDisabledImage
	 * @param normalStateFocusedImage
	 * @param hoverStateFocusedImage
	 * @param pressedStateFocusedImage
	 */
	public CoolButton(final Composite parent, final Image normalStateImage,
		final Image hoverStateImage, final Image pressedStateImage,
		final Image normalStateDisabledImage, final Image normalStateFocusedImage,
		final Image hoverStateFocusedImage, final Image pressedStateFocusedImage) {
		this(parent, SWT.PUSH, normalStateImage, hoverStateImage, pressedStateImage,
			normalStateDisabledImage, // disable image
			null, null, null, // toggle images
			null, // disable toggle image
			normalStateFocusedImage, hoverStateFocusedImage, pressedStateFocusedImage, // focus
																						// images
																						// for
																						// normal
																						// state
			null, null, null); // toggle focus images
	}

	/**
	 * The constructor for the <code>CoolButton</code> to
	 * create a simple toggle button. <code>SWT.TOGGLE</code> does not need to be specified.
	 * 
	 * 
	 * <br>
	 * <br>
	 * NOTE: Focus and disable buttons are not specified the images for these states will
	 * be used from the normal and toggle state images.
	 * 
	 * <br>
	 * <br>
	 * @param parent
	 * @param normalStateImage
	 * @param hoverStateImage
	 * @param pressedStateImage
	 * @param normalToggledStateImage
	 * @param hoverToggledStateImage
	 * @param pressedToggledStateImage
	 */
	public CoolButton(final Composite parent, final Image normalStateImage,
		final Image hoverStateImage, final Image pressedStateImage,
		final Image normalToggledStateImage, final Image hoverToggledStateImage,
		final Image pressedToggledStateImage) {
		this(parent, SWT.TOGGLE, normalStateImage, hoverStateImage, pressedStateImage,
			normalStateImage, // disable image
			normalToggledStateImage, hoverToggledStateImage, pressedToggledStateImage, // toggle
																						// images
			normalToggledStateImage, // disable toggle image
			normalStateImage, hoverStateImage, pressedStateImage, // focus images for normal state
			normalToggledStateImage, hoverToggledStateImage, pressedToggledStateImage); // toggle
																						// focus
																						// images
	}

	/**
	 * The constructor for the <code>CoolButton</code> to
	 * create a simple toggle button. <code>SWT.TOGGLE</code> does not need to be specified.
	 * 
	 * <br>
	 * <br>
	 * 
	 * TOGGLE Button with disable images specified
	 * 
	 * <br>
	 * <br>
	 * NOTE: Focus images are not specified the images for these states will
	 * be used from the normal and toggle state images.
	 * 
	 * <br>
	 * <br>
	 * @param parent
	 * @param normalStateImage
	 * @param hoverStateImage
	 * @param pressedStateImage
	 * @param normalStateDisabledImage
	 * @param normalToggledStateImage
	 * @param hoverToggledStateImage
	 * @param pressedToggledStateImage
	 * @param normalToggledDisabledState
	 */
	public CoolButton(final Composite parent, final Image normalStateImage,
		final Image hoverStateImage, final Image pressedStateImage,
		final Image normalStateDisabledImage, final Image normalToggledStateImage,
		final Image hoverToggledStateImage, final Image pressedToggledStateImage,
		final Image normalToggledDisabledState) {
		this(parent, SWT.TOGGLE, normalStateImage, hoverStateImage, pressedStateImage,
			normalStateImage, // disable image
			normalToggledStateImage, hoverToggledStateImage, pressedToggledStateImage, // toggle
																						// images
			normalToggledDisabledState, // disable toggle image
			normalStateImage, hoverStateImage, pressedStateImage, // focus images for normal state
			normalToggledStateImage, hoverToggledStateImage, pressedToggledStateImage); // toggle
																						// focus
																						// images
	}

	/**
	 * The <code>CoolButton</code> can be specified in many various ways
	 * focus images and disable images can be specified to give the <code>CoolButton</code> a more
	 * complete look. <br>
	 * <br>
	 * Using <code>null</code> for images that are used for PUSH AND TOGGLE Styles is not allowed.
	 * These images are
	 * shown below in the parameter list. <br>
	 * 
	 * Using <code>null</code> for images that are used for TOGGLE ONLY will be fine as long as the
	 * style is not set to <code>SWT.TOGGLE</code>. <br>
	 * In fact if the style is not set to <code>SWT.TOGGLE</code> the coolButton will assume that is
	 * a PUSH button.
	 * 
	 * @param parent
	 * @param style <br>
	 * <br>
	 * @param normalStateImage - <code>null</code> not allowed
	 * @param hoverStateImage - <code>null</code> not allowed
	 * @param pressedStateImage - <code>null</code> not allowed<br>
	 * <br>
	 * @param normalStateDisabledImage - if <code>null</code> assigned to
	 *            <code>normalStateImage</code>
	 * @param normalToggledStateImage - <code>null</code> not allowed if style == SWT.TOGGLE
	 * @param hoverToggledStateImage - <code>null</code> not allowed if style == SWT.TOGGLE
	 * @param pressedToggledStateImage - <code>null</code> not allowed if style == SWT.TOGGLE<br>
	 * <br>
	 * @param normalToggledDisabledState - if <code>null</code> and style == SWT.TOGGLE assigned to
	 *            <code>normalToggledStateImage</code>
	 * @param normalStateFocusedImage - if <code>null</code> assigned to
	 *            <code>normalStateImage</code>
	 * @param hoverStateFocusedImage - if <code>null</code> assigned to <code>hoverStateImage</code>
	 * @param pressedStateFocusedImage - if <code>null</code> assigned to
	 *            <code>pressedStateImage</code><br>
	 * <br>
	 * @param normalToggledStateFocusedImage - if <code>null</code> and style == SWT.TOGGLE assigned
	 *            to <code>normalToggledStateImage</code>
	 * @param hoverToggledStateFocusedImage - if <code>null</code> and style == SWT.TOGGLE assigned
	 *            to <code>hoverToggledStateImage</code>
	 * @param pressedToggledStateFocusedImage - if <code>null</code> and style == SWT.TOGGLE
	 *            assigned to <code>pressedToggledStateImage</code>
	 */
	public CoolButton(final Composite parent, final int style, final Image normalStateImage,
		final Image hoverStateImage, final Image pressedStateImage,
		final Image normalStateDisabledImage, final Image normalToggledStateImage,
		final Image hoverToggledStateImage, final Image pressedToggledStateImage,
		final Image normalToggledDisabledState, final Image normalStateFocusedImage,
		final Image hoverStateFocusedImage, final Image pressedStateFocusedImage,
		final Image normalToggledStateFocusedImage, final Image hoverToggledStateFocusedImage,
		final Image pressedToggledStateFocusedImage) {
		super(parent, style | SWT.DOUBLE_BUFFERED);

		// Normal state images
		this.normalStateImage = normalStateImage;
		this.hoverStateImage = hoverStateImage;
		this.pressedStateImage = pressedStateImage;

		// Normal disabled state images
		if ( normalStateDisabledImage == null ) {
			this.normalStateDisabledImage = normalStateImage;
		} else {
			this.normalStateDisabledImage = normalStateDisabledImage;
		}

		// Toggled state images
		this.normalToggledStateImage = normalToggledStateImage;
		this.hoverToggledStateImage = hoverToggledStateImage;
		this.pressedToggledStateImage = pressedToggledStateImage;

		// Toggled disabled state images
		if ( normalToggledDisabledState == null ) {
			this.normalToggledDisabledState = normalToggledStateImage;
		} else {
			this.normalToggledDisabledState = normalToggledDisabledState;
		}

		// Focused normal state images
		if ( normalStateFocusedImage == null ) {
			this.normalStateFocusedImage = normalStateImage;
		} else {
			this.normalStateFocusedImage = normalStateFocusedImage;
		}
		if ( hoverStateFocusedImage == null ) {
			this.hoverStateFocusedImage = hoverStateImage;
		} else {
			this.hoverStateFocusedImage = hoverStateFocusedImage;
		}
		if ( pressedStateFocusedImage == null ) {
			this.pressedStateFocusedImage = pressedStateImage;
		} else {
			this.pressedStateFocusedImage = pressedStateFocusedImage;
		}

		// Focused toggle state images
		if ( normalToggledStateFocusedImage == null ) {
			this.normalToggledStateFocusedImage = normalToggledStateImage;
		} else {
			this.normalToggledStateFocusedImage = normalToggledStateFocusedImage;
		}
		if ( hoverToggledStateFocusedImage == null ) {
			this.hoverToggledStateFocusedImage = hoverToggledStateImage;
		} else {
			this.hoverToggledStateFocusedImage = hoverToggledStateFocusedImage;
		}
		if ( pressedToggledStateFocusedImage == null ) {
			this.pressedToggledStateFocusedImage = pressedToggledStateImage;
		} else {
			this.pressedToggledStateFocusedImage = pressedToggledStateFocusedImage;
		}

		setLayout(new GridLayout(1, false));
		isToggleButton = SWT.TOGGLE == (style & SWT.TOGGLE);
		if ( !isToggleButton &&
			(normalStateImage == null || hoverStateImage == null || pressedStateImage == null) ) { throw new IllegalArgumentException(
			"Any of 3 images parameters: normal, hover and pressed, may not be null"); }
		if ( isToggleButton &&
			(normalToggledStateImage == null || hoverStateFocusedImage == null || pressedStateFocusedImage == null) ) { throw new IllegalArgumentException(
			"If the style SWT.TOGGLE is specified the images for the toggle button may not be null"); }

		normalState = new NormalButtonState();
		hoverState = new HoverButtonState();
		pressedState = new PressedButtonState();
		currentState = normalState;

		addPaintListener(new PaintListener() {

			@Override
			public void paintControl(final PaintEvent e) {
				e.gc.drawImage(currentState.getImage(), 0, 0);
			}
		});

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(final MouseEvent e) {
				if ( isToggled ) {
					if ( hotSpotToggled && inHotSpot || !hotSpotToggled ) {
						if ( hotSpot && inHotSpot || !hotSpot ) {
							currentState.mouseDown();
						}
					}
				} else {
					if ( hotSpot && inHotSpot || !hotSpot ) {
						currentState.mouseDown();
					}
				}
			}

			@Override
			public void mouseUp(final MouseEvent e) {
				if ( isToggled ) {
					if ( hotSpotToggled && inHotSpot || !hotSpotToggled ) {
						if ( hotSpot && inHotSpot || !hotSpot ) {
							currentState.mouseUp();
						}
					}
				} else {
					if ( hotSpot && inHotSpot || !hotSpot ) {
						currentState.mouseUp();
					}
				}
			}
		});

		addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(final MouseEvent e) {
				if ( isToggled && !hotSpotToggled && !hotSpot ) { return; }
				if ( !isToggled && !hotSpot ) { return; }
				final boolean prevHotSpot = inHotSpot;
				inHotSpot = isInHotSpot(e.x, e.y);
				if ( prevHotSpot && !inHotSpot ) {
					currentState.mouseExit();
				} else if ( !prevHotSpot && inHotSpot ) {
					currentState.mouseHover();
				}
			}
		});

		addMouseTrackListener(new MouseTrackListener() {

			@Override
			public void mouseEnter(final MouseEvent e) {
				if ( isToggled ) {
					if ( hotSpotToggled && inHotSpot || !hotSpotToggled ) {
						if ( hotSpot && inHotSpot || !hotSpot ) {
							currentState.mouseHover();
						}
					}
				} else {
					if ( hotSpot && inHotSpot || !hotSpot ) {
						currentState.mouseHover();
					}
				}
			}

			@Override
			public void mouseExit(final MouseEvent e) {
				if ( isToggled ) {
					if ( hotSpotToggled && inHotSpot || !hotSpotToggled ) {
						if ( hotSpot && inHotSpot || !hotSpot ) {
							currentState.mouseExit();
						}
					}
				} else {
					if ( hotSpot && inHotSpot || !hotSpot ) {
						currentState.mouseExit();
					}
				}
			}

			@Override
			public void mouseHover(final MouseEvent e) {
				if ( isToggled ) {
					if ( hotSpotToggled && inHotSpot || !hotSpotToggled ) {
						if ( hotSpot && inHotSpot || !hotSpot ) {
							currentState.mouseHover();
						}
					}
				} else {
					if ( hotSpot && inHotSpot || !hotSpot ) {
						currentState.mouseHover();
					}
				}
			}
		});

		// addKeyListener(new KeyListener() {
		//
		// @Override
		// public void keyPressed(final KeyEvent e) {
		// currentState.mouseDown();
		// }
		//
		// @Override
		// public void keyReleased(final KeyEvent e) {
		// if ( isToggleButton ) {
		// isToggled = !isToggled;
		// }
		// doReleaseEvents();
		// currentState.mouseExit();
		// }
		// });

		addFocusListener(new FocusListener() {

			@Override
			public void focusGained(final FocusEvent e) {
				focused = true;
				redraw();
			}

			@Override
			public void focusLost(final FocusEvent e) {
				focused = false;
				redraw();
			}
		});

		addTraverseListener(new TraverseListener() {

			@Override
			public void keyTraversed(final TraverseEvent e) {
				e.doit = true;
			}
		});
	}

	private boolean isInHotSpot(final int x, final int y) {
		if ( isToggled && hotSpotToggled ) {
			if ( y < 0 || y >= hotSpotMatrixToggled.height || x >= hotSpotMatrixToggled.width ||
				x < 0 ) { return false; }
			return hotSpotMatrixToggled.getPixel(x, y) < 10;
		}
		if ( y < 0 || y >= hotSpotMatrix.height || x >= hotSpotMatrix.width || x < 0 ) { return false; }
		return hotSpotMatrix.getPixel(x, y) < 10;
	}

	@Override
	public void setToolTipText(final String string) {
		checkWidget();
		tooltipText = string;
		super.setToolTipText(string);
	}

	/**
	 * This will set the hot region. The use of an image allows the user to create
	 * dynamic hot regions. NB: The image must be black and white. The black region of the image
	 * will be interpreted as the hot region.
	 * @param maskImage
	 */
	public void setHotRegionMask(final Image maskImage) {
		checkWidget();
		hotSpot = maskImage != null;
		if ( maskImage == null ) { return; }
		hotSpotMatrix = maskImage.getImageData();
	}

	/**
	 * This will set the hot region for when the image is in the toggled state. The use of an image
	 * allows the user to create
	 * dynamic hot regions. NB: The image must be black and white. The black region of the image
	 * will be interpreted as the hot region.
	 * @param maskImage
	 */
	public void setHotToggledRegionMask(final Image maskImage) {
		checkWidget();
		hotSpotToggled = maskImage != null;
		if ( maskImage == null ) { return; }
		hotSpotMatrixToggled = maskImage.getImageData();
	}

	/**
	 * Set the toolTip text for the toggled state.
	 * 
	 * Note this tool tip text will only be displayed once
	 * the button is toggled and the mouse is hovering over the
	 * button.
	 * 
	 * @param string
	 */
	public void setToolTipTextToggled(final String string) {
		checkWidget();
		toggledTooltipText = string;
	}

	@Override
	public final void setEnabled(final boolean enabled) {
		checkWidget();
		if ( enabled ) {
			changeState(normalState);
		}
		redraw();
		super.setEnabled(enabled);
	}

	@Override
	public final void setLayout(final Layout layout) {
		super.setLayout(layout);
	}

	/**
	 * Helper method to set the button's tool tip internally.
	 * 
	 * @param string
	 */
	private void setParentTooltip(final String string) {
		super.setToolTipText(string);
	}

	/**
	 * The method used to change the state of the button is a consistent fashion
	 * @param state
	 */
	private synchronized void changeState(final ButtonState state) {
		currentState = state;
		currentState.enter();
	}

	/**
	 * Every ButtonState must have two types of icons. One for when the button acts as a push button
	 * and one for when it acts as a toggle button.
	 * 
	 * Depending on the button state one can also display a disabled state for when it acts as a
	 * push
	 * button and when it acts as a toggle button.
	 * 
	 */
	private abstract class ButtonState {

		void enter() {
			redraw();
		}

		final Image getImage() {
			if ( isToggleButton && isToggled ) {
				if ( !focused ) { return getEnabled() ? getToggleImage()
					: getDisabledToggledImage(); }
				return getEnabled() ? getToggledFocusedImage() : getDisabledToggledImage();
			}
			if ( !focused ) { return getEnabled() ? getDefaultImage() : getDefaultDisabledImage(); }
			return getEnabled() ? getFocusedImage() : getDefaultDisabledImage();
		}

		abstract Image getFocusedImage();

		abstract Image getToggledFocusedImage();

		abstract Image getToggleImage();

		abstract Image getDisabledToggledImage();

		abstract Image getDefaultImage();

		abstract Image getDefaultDisabledImage();

		void mouseUp() {}

		void mouseDown() {}

		void mouseHover() {}

		void mouseExit() {}
	}

	private class NormalButtonState extends ButtonState {

		@Override
		Image getToggleImage() {
			return normalToggledStateImage;
		}

		@Override
		Image getDisabledToggledImage() {
			return normalToggledDisabledState;
		}

		@Override
		Image getDefaultImage() {
			return normalStateImage;
		}

		@Override
		Image getDefaultDisabledImage() {
			return normalStateDisabledImage;
		}

		@Override
		Image getFocusedImage() {
			return normalStateFocusedImage;
		}

		@Override
		Image getToggledFocusedImage() {
			return normalToggledStateFocusedImage;
		}

		@Override
		void mouseDown() {
			changeState(pressedState);
		}

		@Override
		void mouseHover() {
			changeState(hoverState);
		}
	}

	private class HoverButtonState extends ButtonState {

		@Override
		void enter() {
			super.enter();
			setParentTooltip(isToggled ? toggledTooltipText : tooltipText);
		}

		@Override
		Image getToggleImage() {
			return hoverToggledStateImage;
		}

		@Override
		Image getDisabledToggledImage() {
			return normalToggledDisabledState;
		}

		@Override
		Image getDefaultImage() {
			return hoverStateImage;
		}

		@Override
		Image getDefaultDisabledImage() {
			return normalStateDisabledImage;
		}

		@Override
		Image getFocusedImage() {
			return hoverStateFocusedImage;
		}

		@Override
		Image getToggledFocusedImage() {
			return hoverToggledStateFocusedImage;
		}

		@Override
		void mouseDown() {
			changeState(pressedState);
		}

		@Override
		void mouseExit() {
			changeState(normalState);
		}
	}

	private class PressedButtonState extends ButtonState {

		@Override
		Image getToggleImage() {
			return pressedToggledStateImage;
		}

		@Override
		Image getDisabledToggledImage() {
			return normalToggledDisabledState;
		}

		@Override
		Image getDefaultImage() {
			return pressedStateImage;
		}

		@Override
		Image getDefaultDisabledImage() {
			return normalStateDisabledImage;
		}

		@Override
		Image getFocusedImage() {
			return pressedStateFocusedImage;
		}

		@Override
		Image getToggledFocusedImage() {
			return pressedToggledStateFocusedImage;
		}

		@Override
		void enter() {
			super.enter();
			doPressEvents();
		}

		@Override
		void mouseUp() {
			if ( isToggleButton ) {
				isToggled = !isToggled;
			}
			doReleaseEvents();
			changeState(hoverState);
		}

		@Override
		void mouseExit() {
			changeState(normalState);
		}
	}

	/**
	 * This only applies if the coolButton is a toggle button. This will
	 * toggle the button on/off depending on the parameter.
	 * 
	 * @param toggle
	 */
	public void setSelection(final boolean toggle) {
		checkWidget();
		if ( isToggleButton ) {
			isToggled = toggle;
			redraw();
		}
	}

	/**
	 * This will return whether or not the button is
	 * toggled or not.
	 * 
	 * @return boolean
	 */
	public boolean getSelection() {
		checkWidget();
		return isToggleButton && isToggled;
	}

	/**
	 * Add CoolButtonSelectionListener
	 * @param listener
	 */
	public void addSelectionListener(final CoolButtonSelectionListener listener) {
		synchronized (listeners) {
			if ( !listeners.contains(listener) ) {
				listeners.add(listener);
			}
		}
	}

	/**
	 * Remove CoolButtonSelectionListener
	 * @param listener
	 */
	public void removeSelectionListener(final CoolButtonSelectionListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	private void doReleaseEvents() {
		synchronized (listeners) {
			final Iterator<CoolButtonSelectionListener> iter = listeners.iterator();
			while (iter.hasNext()) {
				iter.next().selectionOnRelease(new CoolButtonSelectionEvent(this, 0, 0));
			}
		}
	}

	private void doPressEvents() {
		synchronized (listeners) {
			final Iterator<CoolButtonSelectionListener> iter = listeners.iterator();
			while (iter.hasNext()) {
				iter.next().selectionOnPress(new CoolButtonSelectionEvent(this, 0, 0));
			}
		}
	}

	@Override
	public Point computeSize(final int wHint, final int hHint, final boolean changed) {
		checkWidget();
		int width = 0, height = 0;
		if ( currentState.getImage() != null && !currentState.getImage().isDisposed() ) {
			final Rectangle bounds = currentState.getImage().getBounds();
			width = bounds.width;
			height = bounds.height;
		}
		if ( wHint != SWT.DEFAULT ) {
			width = wHint;
		}
		if ( hHint != SWT.DEFAULT ) {
			height = hHint;
		}
		return new Point(width, height);
	}

}