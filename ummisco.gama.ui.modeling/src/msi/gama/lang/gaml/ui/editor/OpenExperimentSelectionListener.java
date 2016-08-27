/**
 * Created by drogoul, 27 août 2016
 * 
 */
package msi.gama.lang.gaml.ui.editor;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import msi.gama.common.interfaces.IGui;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.ui.AutoStartup;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GAML;
import ummisco.gama.ui.controls.FlatButton;
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

	/**
	 * 
	 */
	public OpenExperimentSelectionListener(final GamlEditor editor, final GamlEditorState state) {
		this.editor = editor;
		this.state = state;
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetSelected(final SelectionEvent e) {

		final IGui gui = GAMA.getRegularGui();
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
		IModel model = null;
		try {
			model = editor.getDocument().readOnly(new IUnitOfWork<IModel, XtextResource>() {

				@Override
				public IModel exec(final XtextResource state) throws Exception {
					return GAML.getModelFactory().compile(state);
				}

			});
		} catch (final GamaRuntimeException ex) {
			gotoEditor(ex);
			GAMA.getGui().error("Experiment " + name + " cannot be instantiated because of the following error: "
					+ ex.getMessage());
		}
		if (model == null) {
			return;
		}
		GAMA.getGui().runModel(model, name);

	}

	void gotoEditor(final GamaRuntimeException exception) {
		final EObject o = exception.getEditorContext();
		if (o != null) {
			WorkbenchHelper.asyncRun(new Runnable() {

				@Override
				public void run() {
					GAMA.getGui().editModel(o);
				}
			});
		}

	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {
	}

}
