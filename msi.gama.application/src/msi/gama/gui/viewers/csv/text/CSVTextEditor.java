/*
 * Copyright 2011 csvedit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package msi.gama.gui.viewers.csv.text;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.editors.text.TextEditor;
import msi.gama.gui.swt.controls.GamaToolbar2;
import msi.gama.gui.views.IToolbarDecoratedView;
import msi.gama.gui.views.actions.GamaToolbarFactory;

/**
 * {@link CSVTextEditor} extends basic {@link TextEditor} adding syntax highlighting
 * for the separated elements
 * @author J. Andres Pizarro Gascon
 */
public class CSVTextEditor extends TextEditor implements IToolbarDecoratedView, IToolbarDecoratedView.Sizable {

	// GamaToolbar2 toolbar;
	Composite ancestor;
	Composite parent;

	public CSVTextEditor(final char delimiter) {
		CSVTextSourceViewerConfiguration csvTextConfig =
			new CSVTextSourceViewerConfiguration(delimiter, getPreferenceStore());
		setSourceViewerConfiguration(csvTextConfig);
	}

	@Override
	public void createPartControl(final Composite composite) {
		ancestor = composite;
		parent = new Composite(composite, SWT.NONE);
		Composite compo = GamaToolbarFactory.createToolbars(this, parent);
		super.createPartControl(compo);
	}

	public void setDelimiter(final char delimiter) {
		CSVTextSourceViewerConfiguration csvTextConfig =
			new CSVTextSourceViewerConfiguration(delimiter, getPreferenceStore());
		setSourceViewerConfiguration(csvTextConfig);
		parent.dispose();
		createPartControl(ancestor);
	}

	@Override
	public Control getSizableFontControl() {
		if ( getSourceViewer() == null ) { return null; }
		return getSourceViewer().getTextWidget();
	}

	/**
	 * Method createToolItem()
	 * @see msi.gama.gui.views.IToolbarDecoratedView#createToolItem(int, msi.gama.gui.swt.controls.GamaToolbar2)
	 */
	@Override
	public void createToolItems(final GamaToolbar2 tb) {

		tb.button("menu.saveas2", "Save as...", "Save as...", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				doSaveAs();
			}
		}, SWT.RIGHT);

	}

	@Override
	public void setToogle(final Action toggle) {}

}