/*******************************************************************************************************
 *
 * IPopupProvider.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.controls;

import java.util.LinkedHashMap;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

import ummisco.gama.ui.resources.GamaColors.GamaUIColor;

/**
 * The class IPopupProvider.
 *
 * @author drogoul
 * @since 19 janv. 2012
 *
 */
public interface IPopupProvider {

	/**
	 * The Class PopupText.
	 */
	public static class PopupText extends LinkedHashMap<String, GamaUIColor> {

		/**
		 * With.
		 *
		 * @param color the color
		 * @param text the text
		 * @return the popup text
		 */
		public static PopupText with(final GamaUIColor color, final String text) {
			final PopupText p = new PopupText();
			p.add(color, text);
			return p;
		}

		/**
		 * Adds the.
		 *
		 * @param color the color
		 * @param text the text
		 */
		public void add(final GamaUIColor color, final String text) {
			put(text, color);
		}

	}

	/**
	 * Gets the popup text.
	 *
	 * @return the popup text
	 */
	PopupText getPopupText();

	/**
	 * Gets the controlling shell.
	 *
	 * @return the controlling shell
	 */
	Shell getControllingShell();

	/**
	 * Gets the absolute origin.
	 *
	 * @return the absolute origin
	 */
	Point getAbsoluteOrigin();

	/**
	 * Gets the popup width.
	 *
	 * @return the popup width
	 */
	default int getPopupWidth() {
		return 0;
	}

}
