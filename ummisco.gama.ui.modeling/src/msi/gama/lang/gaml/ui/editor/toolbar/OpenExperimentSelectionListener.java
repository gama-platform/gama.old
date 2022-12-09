/*******************************************************************************************************
 *
 * OpenExperimentSelectionListener.java, in ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling
 * and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.editor.toolbar;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.events.SelectionEvent;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.lang.gaml.ui.editor.GamlEditor;
import msi.gama.lang.gaml.ui.editor.GamlEditorState;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import ummisco.gama.ui.interfaces.IModelRunner;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.toolbar.Selector;

/**
 * The class CreateExperimentSelectionListener.
 *
 * @author drogoul
 * @since 27 août 2016
 *
 */
public class OpenExperimentSelectionListener implements Selector {

	/** The editor. */
	GamlEditor editor;

	/** The state. */
	GamlEditorState state;

	/** The runner. */
	final IModelRunner runner;

	/**
	 *
	 */
	public OpenExperimentSelectionListener(final GamlEditor editor, final GamlEditorState state,
			final IModelRunner runner) {
		this.editor = editor;
		this.state = state;
		this.runner = runner;
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetSelected(final SelectionEvent e) {

		// final IGui gui = GAMA.getRegularGui();
		// We refuse to run if there is no XtextGui available.
		editor.doSave(null);
		if (GamaPreferences.Modeling.EDITOR_SAVE.getValue()) {
			WorkbenchHelper.getPage().saveAllEditors(GamaPreferences.Modeling.EDITOR_SAVE_ASK.getValue());
		}
		String name = (String) e.widget.getData("exp");
		final int i = state.abbreviations.indexOf(name);
		if (i == -1) return;
		name = state.experiments.get(i);
		runner.runModel(editor.getDocument(), name);

	}

	/**
	 * Goto editor.
	 *
	 * @param exception
	 *            the exception
	 */
	void gotoEditor(final GamaRuntimeException exception) {
		final EObject o = exception.getEditorContext();
		if (o != null) { WorkbenchHelper.asyncRun(() -> GAMA.getGui().editModel(null, o)); }

	}

}
