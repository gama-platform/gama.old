/*******************************************************************************************************
 *
 * CSVExportationController.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.views.toolbar;

import org.eclipse.swt.SWT;

import ummisco.gama.ui.resources.IGamaIcons;

/**
 * Class ZoomController.
 *
 * @author drogoul
 * @since 9 févr. 2015
 *
 */
public class CSVExportationController {

	/** The view. */
	private final IToolbarDecoratedView.CSVExportable view;

	/**
	 * @param view
	 */
	public CSVExportationController(final IToolbarDecoratedView.CSVExportable view) {
		this.view = view;
	}

	/**
	 * @param tb
	 */
	public void install(final GamaToolbar2 tb) {
		tb.button(IGamaIcons.DISPLAY_TOOLBAR_CSVEXPORT, "CSV Export", "CSV Export", e -> view.saveAsCSV(), SWT.RIGHT);

	}

}
