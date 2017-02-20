/*********************************************************************************************
 *
 * 'GamaSourceViewer.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.editor;

import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextSourceViewer;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.concurrent.CancelableUnitOfWork;

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
	private boolean isOverviewVisible;

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
		isOverviewVisible = showsAnnotationOverview && overviewRuler != null;
	}

	@Override
	protected void handleDispose() {
		GamlResourceServices.removeResourceListener(resourceListener);
		super.handleDispose();
	}

	@Override
	public void showAnnotationsOverview(final boolean show) {
		super.showAnnotationsOverview(show);
		isOverviewVisible = show;
	}

	public boolean isOverviewVisible() {
		return isOverviewVisible;
	}

	/**
	 * @param gamlEditor
	 */
	public void setResourceListener(final IGamlBuilderListener listener) {
		this.resourceListener = listener;
		((IXtextDocument) getDocument()).readOnly(new CancelableUnitOfWork<Object, XtextResource>() {

			@Override
			public Object exec(final XtextResource state, final CancelIndicator cancelIndicator) throws Exception {
				if (state != null)
					GamlResourceServices.addResourceListener(state.getURI(), listener);
				return null;
			}
		});
	}

}
