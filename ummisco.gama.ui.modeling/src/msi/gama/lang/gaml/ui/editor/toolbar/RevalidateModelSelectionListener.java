/*********************************************************************************************
 *
 * 'RevalidateModelSelectionListener.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA
 * modeling and simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.editor.toolbar;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.concurrent.CancelableUnitOfWork;

import msi.gama.lang.gaml.ui.editor.GamlEditor;
import msi.gama.lang.gaml.validation.GamlModelBuilder;
import ummisco.gama.ui.views.toolbar.Selector;

/**
 * The class CreateExperimentSelectionListener.
 *
 * @author drogoul
 * @since 27 août 2016
 *
 */
public class RevalidateModelSelectionListener implements Selector {

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

		editor.getDocument().readOnly(new CancelableUnitOfWork<Object, XtextResource>() {

			@Override
			public Object exec(final XtextResource state, final CancelIndicator c) throws Exception {
				return GamlModelBuilder.compile(state.getURI(), null);
			}
		});

	}

}
