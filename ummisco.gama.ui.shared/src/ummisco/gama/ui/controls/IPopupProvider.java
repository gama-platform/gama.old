/*********************************************************************************************
 *
 * 'IPopupProvider.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.controls;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

import msi.gama.util.TOrderedHashMap;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;

/**
 * The class IPopupProvider.
 *
 * @author drogoul
 * @since 19 janv. 2012
 *
 */
public interface IPopupProvider {

	public static class PopupText extends TOrderedHashMap<String, GamaUIColor> {

		public static PopupText with(final GamaUIColor color, final String text) {
			final PopupText p = new PopupText();
			p.add(color, text);
			return p;
		}

		public void add(final GamaUIColor color, final String text) {
			put(text, color);
		}

	}

	public PopupText getPopupText();

	public Shell getControllingShell();

	public Point getAbsoluteOrigin();

}
