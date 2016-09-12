/**
 * Created by drogoul, 27 août 2016
 * 
 */
package msi.gama.lang.gaml.ui.editor.toolbar;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import msi.gama.lang.gaml.ui.editor.GamlEditor;
import msi.gama.lang.gaml.validation.GamlModelBuilder;

/**
 * The class CreateExperimentSelectionListener.
 *
 * @author drogoul
 * @since 27 août 2016
 *
 */
public class RevalidateModelSelectionListener implements SelectionListener {

	GamlEditor editor;

	/**
	 * 
	 */
	public RevalidateModelSelectionListener(final GamlEditor editor) {
		this.editor = editor;
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetSelected(final SelectionEvent e) {

		editor.getDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {

			@Override
			public void process(final XtextResource state) throws Exception {
				GamlModelBuilder.compile(state.getURI(), null);
			}
		});

	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {
	}

}
