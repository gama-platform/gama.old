/**
 * Created by drogoul, 27 août 2016
 * 
 */
package msi.gama.lang.gaml.ui.editor.toolbar;

import org.eclipse.jface.text.templates.Template;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;

import msi.gama.lang.gaml.ui.editor.GamlEditor;
import ummisco.gama.ui.menus.GamaMenu;

/**
 * The class CreateExperimentSelectionListener.
 *
 * @author drogoul
 * @since 27 août 2016
 *
 */
public class CreateExperimentSelectionListener implements SelectionListener {

	GamlEditor editor;
	Control control;

	/**
	 * 
	 */
	public CreateExperimentSelectionListener(final GamlEditor editor, final Control toolbar) {
		this.editor = editor;
		this.control = toolbar;
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetSelected(final SelectionEvent e) {

		final GamaMenu menu = new GamaMenu() {

			@Override
			protected void fillMenu() {
				final String[] paths = new String[] { "Experiment.Gui.1", "Experiment.Batch.2", "Experiment.Batch.1" };
				for (final String path : paths) {
					final Template t = editor.getTemplateStore().getTemplateData(path).getTemplate();
					action(t.getDescription(), new SelectionAdapter() {

						@Override
						public void widgetSelected(final SelectionEvent e) {
							editor.applyTemplateAtTheEnd(t);
						}

					});
				}

			}
		};
		menu.open(control, e, 32);

	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {
	}

}
