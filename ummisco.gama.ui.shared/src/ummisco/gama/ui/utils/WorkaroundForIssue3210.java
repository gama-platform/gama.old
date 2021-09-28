/*******************************************************************************************************
 *
 * WorkaroundForIssue3210.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.utils;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.internal.CoolBarToTrimManager;

import msi.gama.common.interfaces.IGamaView;
import msi.gama.runtime.PlatformHelper;
import ummisco.gama.ui.views.toolbar.GamaToolbarFactory;

/**
 * The Class WorkaroundForIssue3210.
 */
public class WorkaroundForIssue3210 {

	/**
	 * Run.
	 */
	public static void run(final CoolBarToTrimManager cm) {
		// Workaround for issue #3210. Only visible on macOS so far
		if (PlatformHelper.isMac()) {
			ToolBar tb = findToolBar(cm.getTopTrim().getWidget());
			GamaToolbarFactory.visuallyUpdate(tb);
			for (IViewPart p : WorkbenchHelper.getPage().getViews()) {
				if (p instanceof IGamaView) { ((IGamaView) p).updateToolbarState(); }
			}
		}
	}

	/**
	 * Find tool bar.
	 *
	 * @param top
	 *            the top
	 * @return the tool bar
	 */
	static ToolBar findToolBar(final Object top) {
		if (!(top instanceof Composite)) return null;
		if (top instanceof ToolBar) return (ToolBar) top;
		for (Control c : ((Composite) top).getChildren()) {
			if (c.isDisposed()) { continue; }
			ToolBar result = findToolBar(c);
			if (result != null) return result;
		}
		return null;
	}
}
