/*******************************************************************************************************
 *
 * CSVTextSourceViewerConfiguration.java, in ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.viewers.csv.text;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

/**
 * The Class CSVTextSourceViewerConfiguration.
 */
class CSVTextSourceViewerConfiguration extends TextSourceViewerConfiguration
{

    /** Delimiter */
    private final char m_delimiter;

    /**
     * Constructor
     * @param prefStore the {@link IPreferenceStore} used by the base class constructor
     */
    public CSVTextSourceViewerConfiguration(char delimiter, IPreferenceStore prefStore) {
        super(prefStore);
        m_delimiter = delimiter;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getPresentationReconciler(org.eclipse.jface.text.source.ISourceViewer)
     */
    @Override
    public IPresentationReconciler getPresentationReconciler(
            ISourceViewer sourceViewer) {
        PresentationReconciler reconciler = new PresentationReconciler();
        /*
         * Reconciler configuration
         */
        DefaultDamagerRepairer dr = new DefaultDamagerRepairer(new CSVTokenScanner(m_delimiter));
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
        return reconciler;
    }
}