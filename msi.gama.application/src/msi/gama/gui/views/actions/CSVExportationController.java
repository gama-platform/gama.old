/**
 * Created by drogoul, 9 févr. 2015
 *
 */
package msi.gama.gui.views.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import msi.gama.gui.swt.controls.GamaToolbar2;
import msi.gama.gui.views.IToolbarDecoratedView.CSVExportable;
import ummisco.gama.ui.resources.IGamaIcons;

/**
 * Class ZoomController.
 *
 * @author drogoul
 * @since 9 févr. 2015
 *
 */
public class CSVExportationController {

	private final CSVExportable view;

	/**
	 * @param view
	 */
	public CSVExportationController(final CSVExportable view) {
		this.view = view;
	}

	/**
	 * @param tb
	 */
	public void install(final GamaToolbar2 tb) {
		tb.button(IGamaIcons.DISPLAY_TOOLBAR_CSVEXPORT.getCode(), "CSV Export", "CSV Export", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				view.saveAsCSV();
			}
		}, SWT.RIGHT);

	}

}
