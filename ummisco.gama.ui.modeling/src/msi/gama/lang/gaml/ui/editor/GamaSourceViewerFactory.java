/**
 * Created by drogoul, 12 août 2016
 * 
 */
package msi.gama.lang.gaml.ui.editor;

import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.xtext.ui.editor.XtextSourceViewer;

/**
 * The class GamaSourceViewerFactory.
 *
 * @author drogoul
 * @since 12 août 2016
 *
 */
public class GamaSourceViewerFactory implements XtextSourceViewer.Factory {

	/**
	 * @see org.eclipse.xtext.ui.editor.XtextSourceViewer.Factory#createSourceViewer(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.jface.text.source.IVerticalRuler,
	 *      org.eclipse.jface.text.source.IOverviewRuler, boolean, int)
	 */
	@Override
	public XtextSourceViewer createSourceViewer(final Composite parent, final IVerticalRuler ruler,
			final IOverviewRuler overviewRuler, final boolean showsAnnotationOverview, final int styles) {
		return new GamaSourceViewer(parent, ruler, overviewRuler, showsAnnotationOverview, styles);
	}

}
