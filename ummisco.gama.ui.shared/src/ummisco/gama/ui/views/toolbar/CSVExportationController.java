/**
 * Created by drogoul, 9 févr. 2015
 *
 */
package ummisco.gama.ui.views.toolbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaIcons;

/**
 * Class ZoomController.
 *
 * @author drogoul
 * @since 9 févr. 2015
 *
 */
public class CSVExportationController {

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
		tb.button(GamaIcons.create(IGamaIcons.DISPLAY_TOOLBAR_CSVEXPORT).getCode(), "CSV Export", "CSV Export",
				new SelectionAdapter() {

					@Override
					public void widgetSelected(final SelectionEvent e) {
						view.saveAsCSV();
					}
				}, SWT.RIGHT);

	}

}
