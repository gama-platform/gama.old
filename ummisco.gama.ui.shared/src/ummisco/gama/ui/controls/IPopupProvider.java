/*********************************************************************************************
 *
 *
 * 'IPopupProvider.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.controls;

import java.util.ArrayList;
import java.util.List;

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

	public static class PopupText {

		public static PopupText with(final GamaUIColor color, final String text) {
			final PopupText p = new PopupText();
			p.add(color, text);
			return p;
		}

		public void add(final GamaUIColor color, final String text) {
			contents.add(new Pair(color, text));
		}

		final List<Pair> contents = new ArrayList();

		public class Pair {
			GamaUIColor color;
			String text;

			public Pair(final GamaUIColor color, final String text) {
				super();
				this.color = color;
				this.text = text;
			}

		}

		public boolean isEmpty() {
			return contents.isEmpty();
		}

		public int size() {
			return contents.size();
		}

	}

	public PopupText getPopupText();

	public Shell getControllingShell();

	public Point getAbsoluteOrigin();

}
