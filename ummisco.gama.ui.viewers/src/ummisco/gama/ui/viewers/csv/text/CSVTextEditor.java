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
package ummisco.gama.ui.viewers.csv.text;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.editors.text.TextEditor;

import ummisco.gama.ui.views.toolbar.GamaToolbar2;
import ummisco.gama.ui.views.toolbar.GamaToolbarFactory;
import ummisco.gama.ui.views.toolbar.IToolbarDecoratedView;

/**
 * {@link CSVTextEditor} extends basic {@link TextEditor} adding syntax
 * highlighting for the separated elements
 * 
 * @author J. Andres Pizarro Gascon
 */
public class CSVTextEditor extends TextEditor implements IToolbarDecoratedView.Sizable {

	// GamaToolbar2 toolbar;
	Composite ancestor;
	Composite parent;

	public CSVTextEditor(final char delimiter) {
		final CSVTextSourceViewerConfiguration csvTextConfig = new CSVTextSourceViewerConfiguration(delimiter,
				getPreferenceStore());
		setSourceViewerConfiguration(csvTextConfig);
	}

	@Override
	public void createPartControl(final Composite composite) {
		ancestor = composite;
		parent = new Composite(composite, SWT.NONE);
		final Composite compo = GamaToolbarFactory.createToolbars(this, parent);
		super.createPartControl(compo);
	}

	public void setDelimiter(final char delimiter) {
		final CSVTextSourceViewerConfiguration csvTextConfig = new CSVTextSourceViewerConfiguration(delimiter,
				getPreferenceStore());
		setSourceViewerConfiguration(csvTextConfig);
		parent.dispose();
		createPartControl(ancestor);
	}

	@Override
	public Control getSizableFontControl() {
		if (getSourceViewer() == null) {
			return null;
		}
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

		tb.button("menu.saveas2", "Save as...", "Save as...", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				doSaveAs();
			}
		}, SWT.RIGHT);

	}
	//
	// @Override
	// public void setToogle(final Action toggle) {}

}