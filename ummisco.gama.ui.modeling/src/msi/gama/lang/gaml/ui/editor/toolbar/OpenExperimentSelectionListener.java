/*********************************************************************************************
 *
 * 'OpenExperimentSelectionListener.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.editor.toolbar;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import msi.gama.lang.gaml.ui.AutoStartup;
import msi.gama.lang.gaml.ui.editor.GamlEditor;
import msi.gama.lang.gaml.ui.editor.GamlEditorState;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import ummisco.gama.ui.controls.FlatButton;
import ummisco.gama.ui.interfaces.IModelRunner;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The class CreateExperimentSelectionListener.
 *
 * @author drogoul
 * @since 27 août 2016
 *
 */
public class OpenExperimentSelectionListener implements SelectionListener {

	GamlEditor editor;
	GamlEditorState state;
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
		if (AutoStartup.EDITOR_SAVE.getValue()) {
			WorkbenchHelper.getPage().saveAllEditors(AutoStartup.EDITOR_SAVE_ASK.getValue());
		}
		String name = ((FlatButton) e.widget).getText();
		final int i = state.abbreviations.indexOf(name);
		if (i == -1) {
			return;
		}
		name = state.experiments.get(i);
		runner.runModel(editor.getDocument(), name);

	}

	void gotoEditor(final GamaRuntimeException exception) {
		final EObject o = exception.getEditorContext();
		if (o != null) {
			WorkbenchHelper.asyncRun(() -> GAMA.getGui().editModel(o));
		}

	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {
	}

}
