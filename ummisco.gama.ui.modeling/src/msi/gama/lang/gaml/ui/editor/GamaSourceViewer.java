/**
 * Created by drogoul, 12 août 2016
 * 
 */
package msi.gama.lang.gaml.ui.editor;

import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextSourceViewer;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import msi.gama.lang.gaml.resource.GamlResourceServices;
import msi.gama.lang.gaml.validation.IGamlBuilderListener;

/**
 * The class GamaSourceViewer.
 *
 * @author drogoul
 * @since 12 août 2016
 *
 */
public class GamaSourceViewer extends XtextSourceViewer {

	private IGamlBuilderListener resourceListener;

	/**
	 * @param parent
	 * @param ruler
	 * @param overviewRuler
	 * @param showsAnnotationOverview
	 * @param styles
	 */
	public GamaSourceViewer(final Composite parent, final IVerticalRuler ruler, final IOverviewRuler overviewRuler,
			final boolean showsAnnotationOverview, final int styles) {
		super(parent, ruler, overviewRuler, showsAnnotationOverview, styles);
	}

	@Override
	protected void handleDispose() {
		GamlResourceServices.removeResourceListener(resourceListener);
		super.handleDispose();
	}

	/**
	 * @param gamlEditor
	 */
	public void setResourceListener(final IGamlBuilderListener listener) {
		this.resourceListener = listener;
		((IXtextDocument) getDocument()).readOnly(new IUnitOfWork.Void<XtextResource>() {

			@Override
			public void process(final XtextResource state) throws Exception {
				if(state!=null)
				GamlResourceServices.addResourceListener(state.getURI(), listener);
			}
		});
	}

}
