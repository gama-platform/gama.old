/*******************************************************************************************************
 *
 * LogExportationController.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.views.toolbar;

import org.eclipse.swt.SWT;

import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.views.toolbar.IToolbarDecoratedView.LogExportable;

/**
 * Class ZoomController.
 *
 * @author drogoul
 * @since 9 févr. 2015
 *
 */
public class LogExportationController {

	/** The view. */
	private final IToolbarDecoratedView.LogExportable view;

	/**
	 * @param view2
	 */
	public LogExportationController(final LogExportable view2) {
		this.view = view2;
	}

	/**
	 * @param tb
	 */
	public void install(final GamaToolbar2 tb) {
		tb.button(IGamaIcons.DISPLAY_TOOLBAR_CSVEXPORT, "Export to log file", "Export to log file",
				e -> view.saveAsLog(), SWT.RIGHT);

	}

}
