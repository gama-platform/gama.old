/*******************************************************************************************************
 *
 * CSVTextEditor.java, in ummisco.gama.ui.viewers, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.viewers.csv.text;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.editors.text.TextEditor;

import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;
import ummisco.gama.ui.views.toolbar.GamaToolbarFactory;
import ummisco.gama.ui.views.toolbar.IToolbarDecoratedView;

/**
 * {@link CSVTextEditor} extends basic {@link TextEditor} adding syntax highlighting for the separated elements
 *
 * @author J. Andres Pizarro Gascon
 */
public class CSVTextEditor extends TextEditor implements IToolbarDecoratedView.Sizable {

	/** The ancestor. */
	// GamaToolbar2 toolbar;
	Composite ancestor;

	/** The parent. */
	Composite parent;

	/**
	 * Instantiates a new CSV text editor.
	 *
	 * @param delimiter
	 *            the delimiter
	 */
	public CSVTextEditor(final char delimiter) {
		final CSVTextSourceViewerConfiguration csvTextConfig =
				new CSVTextSourceViewerConfiguration(delimiter, getPreferenceStore());
		setSourceViewerConfiguration(csvTextConfig);
	}

	@Override
	public void createPartControl(final Composite composite) {
		ancestor = composite;
		parent = new Composite(composite, SWT.NONE);
		final Composite compo = GamaToolbarFactory.createToolbars(this, parent);
		super.createPartControl(compo);
	}

	/**
	 * Sets the delimiter.
	 *
	 * @param delimiter
	 *            the new delimiter
	 */
	public void setDelimiter(final char delimiter) {
		final CSVTextSourceViewerConfiguration csvTextConfig =
				new CSVTextSourceViewerConfiguration(delimiter, getPreferenceStore());
		setSourceViewerConfiguration(csvTextConfig);
		parent.dispose();
		createPartControl(ancestor);
	}

	@Override
	public Control getSizableFontControl() {
		if (getSourceViewer() == null) return null;
		return getSourceViewer().getTextWidget();
	}

	/**
	 * Method createToolItem()
	 *
	 * @see ummisco.gama.ui.views.toolbar.IToolbarDecoratedView#createToolItem(int,
	 *      ummisco.gama.ui.views.toolbar.GamaToolbar2)
	 */
	@Override
	public void createToolItems(final GamaToolbar2 tb) {

		tb.button(IGamaIcons.SAVE_AS, "Save as...", "Save as...", e -> doSaveAs(), SWT.RIGHT);

	}
	//
	// @Override
	// public void setToogle(final Action toggle) {}

}