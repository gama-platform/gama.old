/*******************************************************************************************************
 *
 * CreateExperimentSelectionListener.java, in ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling
 * and simulation platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.editor.toolbar;

import org.eclipse.jface.text.templates.Template;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;

import msi.gama.lang.gaml.ui.editor.GamlEditor;
import ummisco.gama.ui.menus.GamaMenu;
import ummisco.gama.ui.views.toolbar.Selector;

/**
 * The class CreateExperimentSelectionListener.
 *
 * @author drogoul
 * @since 27 août 2016
 *
 */

@SuppressWarnings ("deprecation")
public class CreateExperimentSelectionListener implements Selector {

	/** The editor. */
	GamlEditor editor;

	/** The control. */
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
				final String[] paths = { "Experiment.Gui.1", "Experiment.Batch.2", "Experiment.Batch.1" };
				for (final String path : paths) {
					final Template t = editor.getTemplateStore().getTemplateData(path).getTemplate();
					action(t.getDescription(), e1 -> editor.applyTemplateAtTheEnd(t));
				}

			}
		};
		menu.open(control, e, 32);

	}

}
