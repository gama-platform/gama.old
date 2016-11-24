/*********************************************************************************************
 *
 * 'LookAndFeelHandler.java, in plugin ummisco.gama.java2d, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.java2d.swing;

import java.awt.*;
import java.io.PrintStream;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

import ummisco.gama.ui.utils.PlatformHelper;

/**
 * This class deals with the customization of the look&amp;feel
 * of Swing components embedded inside SWT.
 * <p>
 * It is customizable through the "replaceable singleton" design pattern.
 */
public class LookAndFeelHandler {

	// ========================================================================
	// Accessors

	/**
	 * This look&amp;feel choice denotes the default Swing look&amp;feel.
	 * It may be platform dependent.
	 */
	public static final String LAFChoiceSwingDefault = "swing.defaultlaf";

	/**
	 * This look&amp;feel choice denotes the cross-platform Swing
	 * look&amp;feel.
	 * It is not platform dependent.
	 * @see javax.swing.UIManager#getCrossPlatformLookAndFeelClassName()
	 */
	public static final String LAFChoiceCrossPlatform = "swing.crossplatformlaf";

	/**
	 * This look&amp;feel choice denotes the Swing look&amp;feel
	 * that best approximates the system look&amp;feel.
	 * It is platform dependent.
	 * @see javax.swing.UIManager#getSystemLookAndFeelClassName()
	 */
	public static final String LAFChoiceNativeSystem = "swing.systemlaf";

	/**
	 * This look&amp;feel choice denotes the Swing look&amp;feel
	 * that best approximates the system look&amp;feel, except that
	 * the Gtk look&amp;feel is used instead of the cross-platform
	 * look&amp;feel if SWT is based on Gtk and if it works fine.
	 * (This typically affects Linux systems with KDE desktop.)
	 * It is platform dependent.
	 */
	public static final String LAFChoiceNativeSystemPreferGtk = "swing.systemlaf+gtk";

	/**
	 * This look&amp;feel choice denotes the Swing look&amp;feel
	 * that best approximates the system look&amp;feel, except that
	 * the Gtk look&amp;feel is avoided.
	 * (This typically affects Linux systems with Gtk desktop.)
	 * It is platform dependent.
	 */
	public static final String LAFChoiceNativeSystemNoGtk = "swing.systemlaf-gtk";

	private String lafChoice;
	// Set the default look&feel choice.
	{
		// On JDK 1.6, we have to avoid the Swing Gtk look&feel, to work around
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=126931
		if ( PlatformHelper.JAVA_VERSION >= PlatformHelper.javaVersion(1, 6, 0) ) {
			lafChoice = LAFChoiceNativeSystemNoGtk;
		} else {
			// Set the default to LAFChoiceNativeSystemPreferGtk, so that on
			// Unix systems with a GNOME desktop and with themes supported by
			// the Swing Gtk look&feel this Gtk look&feel is used; it resembles
			// the system look&feel more closely than Metal or Nimbus.
			lafChoice = LAFChoiceNativeSystemPreferGtk;
		}
	}

	/**
	 * Returns the look&amp;feel choice. It can be a class name or one
	 * the shorthands defined in this class.
	 */
	public String getLAFChoice() {
		return lafChoice;
	}

	/**
	 * Specifies which look&amp;feel should be used.
	 * @param choice Either a class name, such as
	 *            <code>"javax.swing.plaf.metal.MetalLookAndFeel"</code>,
	 *            <code>"com.sun.java.swing.plaf.windows.WindowsLookAndFeel"</code>,
	 *            <code>"com.sun.java.swing.plaf.gtk.GTKLookAndFeel"</code>,
	 *            <code>"com.sun.java.swing.plaf.motif.MotifLookAndFeel"</code>,
	 *            <code>"apple.laf.AquaLookAndFeel"</code>,
	 *            or one of the shorthands
	 *            {@link #LAFChoiceSwingDefault},
	 *            {@link #LAFChoiceCrossPlatform},
	 *            {@link #LAFChoiceNativeSystem},
	 *            {@link #LAFChoiceNativeSystemPreferGtk},
	 *            {@link #LAFChoiceNativeSystemNoGtk}.
	 */
	public void setLAFChoice(final String choice) {
		lafChoice = choice;
	}

	// -------------------------- Options ----------------------------------------------

	private boolean isDefaultSwtFontPropagated = true;
	private boolean isTooltipAlwaysShown = true;

	/**
	 * Returns whether SWT default font changes are automatically propagated to
	 * Swing. See {@link #setSwtDefaultFontPropagated(boolean)} for more
	 * information.
	 * 
	 * @return boolean
	 */
	public boolean isSwtDefaultFontPropagated() {
		return isDefaultSwtFontPropagated;
	}

	/**
	 * Configures automatic propagation of changes to the SWT default font to
	 * the underlying Swing look and feel.
	 * <p>
	 * The default value of this flag is <code>true</code>.
	 * Normally, this ensures that Swing fonts will closely match the
	 * corresponding SWT fonts, and that changes to the system font settings
	 * will be obeyed by both Swing and SWT. However, Swing does not render
	 * certain fonts very well (e.g. the default "Segoe UI" font in Windows
	 * Vista). Setting the flag to <code>false</code> will disable the
	 * propagation in cases where the Swing rendering is not acceptable, and
	 * Swing will use its default fonts. Note, however, that if propagation is
	 * disabled, changes to system font settings may not be detected by Swing.
	 * 
	 * @param val boolean flag. If <code>true</code>, default fonts will be
	 *            propagated to Swing.
	 */
	public void setSwtDefaultFontPropagated(final boolean val) {
		isDefaultSwtFontPropagated = val;
	}

	/**
	 * Returns whether Swing's default tooltip behavior will be changed to
	 * be more consistent with SWT. See {@link #setTooltipAlwaysShown(boolean)}
	 * for more information.
	 * 
	 * @return the current isTooltipAlwaysShown flag
	 */
	public boolean isTooltipAlwaysShown() {
		return isTooltipAlwaysShown;
	}

	/**
	 * Configures the tooltip behavior for Swing components. On some platforms,
	 * Swing tooltips are not shown for inactive windows (including embedded
	 * frames). By setting the flag to <code>true</code>, (the default value)
	 * this Swing behavior is changed and tooltips are shown, whether or not
	 * the window is inactive. This makes the Swing tooltips more consistent
	 * with SWT tooltips.
	 * <p>
	 * Note: This change of behavior currently works only on JDK 1.6 and higher.
	 * See sun bug <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6178004">
	 * 6178004</a>
	 * 
	 * @param isTooltipAlwaysShown the new isTooltipAlwaysShown value to set
	 */
	public void setTooltipAlwaysShown(final boolean isTooltipAlwaysShown) {
		this.isTooltipAlwaysShown = isTooltipAlwaysShown;
	}

	// ========================================================================
	// Overridable API

	private static final String GTK_LOOK_AND_FEEL_NAME = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel"; //$NON-NLS-1$

	/**
	 * Sets the desired look&amp;feel.
	 * @see #setLAFChoice(String)
	 */
	public void setLookAndFeel()
		throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		assert EventQueue.isDispatchThread(); // On AWT event thread

		if ( isTooltipAlwaysShown ) {
			// Try to turn on tooltips for inactive AWT windows. This is not the default
			// behavior on some platforms. Also, note that it will only work in
			// JDK1.6+
			UIManager.put("ToolTipManager.enableToolTipMode", "allWindows");
		}

		// If the user has specified the Swing look&feel through the
		// system property, and the application has also specified the
		// look&feel, obey the user. The user is always right.
		if ( System.getProperty("swing.defaultlaf") != null ) { return; }

		String laf = getLAFChoice();
		if ( LAFChoiceSwingDefault.equals(laf) ) {
			return;
		} else if ( LAFChoiceCrossPlatform.equals(laf) ) {
			laf = UIManager.getCrossPlatformLookAndFeelClassName();
		} else if ( LAFChoiceNativeSystem.equals(laf) ) {
			laf = UIManager.getSystemLookAndFeelClassName();
		} else if ( LAFChoiceNativeSystemPreferGtk.equals(laf) ) {
			laf = UIManager.getSystemLookAndFeelClassName();
			if ( PlatformHelper.isGtk() && laf.equals(UIManager.getCrossPlatformLookAndFeelClassName()) ) {

				laf = GTK_LOOK_AND_FEEL_NAME;
			}
			if ( laf.equals(GTK_LOOK_AND_FEEL_NAME) ) {
				// Try the Gtk look&feel.
				try {
					doSetLookAndFeel(GTK_LOOK_AND_FEEL_NAME);
					return;
				} catch (ClassNotFoundException e) {} catch (InstantiationException e) {} catch (IllegalAccessException e) {} catch (UnsupportedLookAndFeelException e) {}
				// Second try: Use cross platform look and feel
				laf = UIManager.getCrossPlatformLookAndFeelClassName();
			}
		} else if ( LAFChoiceNativeSystemNoGtk.equals(laf) ) {
			laf = UIManager.getSystemLookAndFeelClassName();
			if ( GTK_LOOK_AND_FEEL_NAME.equals(laf) ) {
				laf = UIManager.getCrossPlatformLookAndFeelClassName();
			}
		}

		doSetLookAndFeel(laf);
	}

	private static void doSetLookAndFeel(final String laf)
		throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		// Although UIManager.setLookAndFeel is specified to throw an
		// UnsupportedLookAndFeelException when the look&feel is not supported,
		// the com.sun.java.swing.plaf.gtk.GTKParser of JDK 1.5.0 reports
		// failures by doing System.err.println() and instead sets an unthemed
		// Gtk look&feel, which is very ugly.
		// Typically this happens on Linux systems with KDE desktop. The
		// $HOME/.gtkrc-2.0 file created by the KDE control center refers to
		// /opt/gnome/share/themes/Qt/gtk-2.0/gtkrc, which specifies a theming
		// engine "qtengine", which exists in C++ code but not in the
		// com.sun.java.swing.plaf.gtk mockup. The error message in this case
		// reads:
		// "/opt/gnome/share/themes/Qt/gtk-2.0/gtkrc:5: Engine "qtengine" is unsupported, ignoring"
		//
		// This can also happens even when running the GNOME desktop, when certain themes are selected.
		PrintStream origSystemErr = System.err;
		try {
			System.setErr(new PrintStream(origSystemErr) {

				@Override
				public void print(final String s) {
					throw new UnsupportedLookAndFeelRuntimeException(s);
				}

				@Override
				public void println(final String s) {
					throw new UnsupportedLookAndFeelRuntimeException(s);
				}
			});
			UIManager.setLookAndFeel(laf);
		} catch (UnsupportedLookAndFeelRuntimeException e) {
			UnsupportedLookAndFeelException newExc = new UnsupportedLookAndFeelException(e.getMessage());
			newExc.initCause(e);
			throw newExc;
		} finally {
			System.setErr(origSystemErr);
		}
	}

	/**
	 * This exception signals an unsupported look&feel.
	 */
	static class UnsupportedLookAndFeelRuntimeException extends RuntimeException {

		UnsupportedLookAndFeelRuntimeException(final String message) {
			super(message);
		}
	}

	// --------------------------- Font Management ---------------------------

	private Font lastPropagatedSwtFont;

	/**
	 * Propagates the default SWT font to the Swing look&feel,
	 * if allowed by {@link #isSwtDefaultFontPropagated()}.
	 * In this implementation, this method calls the
	 * {@link #updateLookAndFeelFonts(java.awt.Font)} method.
	 * @param swtFont The default SWT font.
	 * @param swtFontData Result of <code>swtFont.getFontData()</code>,
	 *            obtained on the SWT event thread.
	 * @return The corresponding AWT font.
	 * @see #isSwtDefaultFontPropagated()
	 * @see #updateLookAndFeelFonts(java.awt.Font)
	 */
	public java.awt.Font propagateSwtFont(final Font swtFont, final FontData[] swtFontData) {
		assert EventQueue.isDispatchThread(); // On AWT event thread

		java.awt.Font awtFont = ResourceConverter.getInstance().convertFont(swtFont, swtFontData);
		if ( isSwtDefaultFontPropagated() && !swtFont.getDevice().isDisposed() && lastPropagatedSwtFont != swtFont ) {
			lastPropagatedSwtFont = swtFont;

			// Update the look and feel defaults to use new font.
			// Swing should take care of this on its own, but it does not seem
			// to do it when mixed with SWT.
			updateLookAndFeelFonts(awtFont);
		}
		return awtFont;
	}

	/**
	 * Changes the currently active Swing look&feel to use the given font
	 * as primary font for everything.
	 * @param awtFont A font.
	 */
	protected void updateLookAndFeelFonts(final java.awt.Font awtFont) {
		assert awtFont != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		// The FontUIResource class marks the font as replaceable by the look and feel
		// implementation if font settings are later changed.
		FontUIResource fontResource = new FontUIResource(awtFont);

		// Assign the new font to the relevant L&F font properties. These are
		// the properties that are initially assigned to the system font
		// under the Windows look and feel.
		// TODO: It's possible that other platforms will need other assignments.
		// TODO: This does not handle fonts other than the "system" font.
		// TODO: Swing does not render the Vista default Segoe UI font well.
		// Other fonts may change, and the Swing L&F may not be adjusting.

		UIManager.put("Button.font", fontResource); //$NON-NLS-1$
		UIManager.put("CheckBox.font", fontResource); //$NON-NLS-1$
		UIManager.put("ComboBox.font", fontResource); //$NON-NLS-1$
		UIManager.put("EditorPane.font", fontResource); //$NON-NLS-1$
		UIManager.put("Label.font", fontResource); //$NON-NLS-1$
		UIManager.put("List.font", fontResource); //$NON-NLS-1$
		UIManager.put("Panel.font", fontResource); //$NON-NLS-1$
		UIManager.put("ProgressBar.font", fontResource); //$NON-NLS-1$
		UIManager.put("RadioButton.font", fontResource); //$NON-NLS-1$
		UIManager.put("ScrollPane.font", fontResource); //$NON-NLS-1$
		UIManager.put("TabbedPane.font", fontResource); //$NON-NLS-1$
		UIManager.put("Table.font", fontResource); //$NON-NLS-1$
		UIManager.put("TableHeader.font", fontResource); //$NON-NLS-1$
		UIManager.put("TextField.font", fontResource); //$NON-NLS-1$
		UIManager.put("TextPane.font", fontResource); //$NON-NLS-1$
		UIManager.put("TitledBorder.font", fontResource); //$NON-NLS-1$
		UIManager.put("ToggleButton.font", fontResource); //$NON-NLS-1$
		UIManager.put("TreeFont.font", fontResource); //$NON-NLS-1$
		UIManager.put("ViewportFont.font", fontResource); //$NON-NLS-1$
	}

	// --------------------------- Color Management ---------------------------

	/**
	 * Propagates the foreground color from SWT to a given AWT/Swing component.
	 * @param component An AWT/Swing component.
	 * @param foreground The SWT foreground color.
	 * @param preserveDefaults If true, the color will not be set on the
	 *            component if its foreground (whether specified
	 *            or inherited) is already the same as the given
	 *            foreground color.
	 */
	public void propagateSwtForeground(final Component component, final Color foreground,
		final boolean preserveDefaults) {
		assert EventQueue.isDispatchThread();
		assert component != null;

		ResourceConverter converter = ResourceConverter.getInstance();
		java.awt.Color fg = converter.convertColor(foreground);

		if ( !fg.equals(component.getForeground()) || !preserveDefaults ) {
			component.setForeground(fg);
		}
	}

	/**
	 * Propagates the background color from SWT to a given AWT/Swing component.
	 * @param component An AWT/Swing component.
	 * @param background The SWT background color.
	 * @param preserveDefaults If true, the color will not be set on the
	 *            component if its background (whether specified
	 *            or inherited) is already the same as the given
	 *            background color.
	 */
	public void propagateSwtBackground(final Component component, final Color background,
		final boolean preserveDefaults) {
		assert EventQueue.isDispatchThread();
		assert component != null;

		ResourceConverter converter = ResourceConverter.getInstance();
		java.awt.Color bg = converter.convertColor(background);

		if ( !bg.equals(component.getBackground()) || !preserveDefaults ) {
			component.setBackground(bg);
		}
	}

	// ========================================================================
	// Singleton design pattern

	private static LookAndFeelHandler theHandler = new LookAndFeelHandler();

	/**
	 * Returns the currently active singleton of this class.
	 */
	public static LookAndFeelHandler getInstance() {
		return theHandler;
	}

	/**
	 * Replaces the singleton of this class.
	 * @param instance An instance of this class or of a customized subclass.
	 */
	public static void setInstance(final LookAndFeelHandler instance) {
		theHandler = instance;
	}

}
