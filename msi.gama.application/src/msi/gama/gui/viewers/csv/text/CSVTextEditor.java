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

import msi.gama.gui.swt.controls.*;
import msi.gama.gui.views.IToolbarDecoratedView;
import msi.gama.gui.views.actions.GamaToolbarFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.editors.text.TextEditor;

/**
 * {@link CSVTextEditor} extends basic {@link TextEditor} adding syntax highlighting
 * for the separated elements
 * @author J. Andres Pizarro Gascon
 */
public class CSVTextEditor extends TextEditor implements IToolbarDecoratedView, IToolbarDecoratedView.Sizable {

	GamaToolbar2 toolbar;

	public CSVTextEditor(final char delimiter) {
		CSVTextSourceViewerConfiguration csvTextConfig =
			new CSVTextSourceViewerConfiguration(delimiter, getPreferenceStore());
		setSourceViewerConfiguration(csvTextConfig);
	}

	/**
	 * Method getToolbarActionsId()
	 * @see msi.gama.gui.views.IToolbarDecoratedView#getToolbarActionsId()
	 */
	@Override
	public Integer[] getToolbarActionsId() {
		return new Integer[] { -32 };
	}

	@Override
	public void createPartControl(final Composite composite) {
		Composite parent = GamaToolbarFactory.createToolbars(this, composite);
		super.createPartControl(parent);
	}

	@Override
	public Control getSizableFontControl() {
		if ( getSourceViewer() == null ) { return null; }
		return getSourceViewer().getTextWidget();
	}

	@Override
	public void createToolItem(final int code, final GamaToolbarSimple tb) {
		switch (code) {
			case -32:
				tb.button("menu.saveas2", "Save as...", "Save as...", new SelectionAdapter() {

					@Override
					public void widgetSelected(final SelectionEvent e) {
						doSaveAs();
					}
				});
				break;

		}
	}

	/**
	 * Method setToolbar()
	 * @see msi.gama.gui.views.IToolbarDecoratedView#setToolbar(msi.gama.gui.swt.controls.GamaToolbar2)
	 */
	@Override
	public void setToolbar(final GamaToolbar2 toolbar) {
		this.toolbar = toolbar;
	}

	/**
	 * Method createToolItem()
	 * @see msi.gama.gui.views.IToolbarDecoratedView#createToolItem(int, msi.gama.gui.swt.controls.GamaToolbar2)
	 */
	@Override
	public void createToolItem(final int code, final GamaToolbar2 tb) {

		switch (code) {
			case -32:
				tb.button("menu.saveas2", "Save as...", "Save as...", new SelectionAdapter() {

					@Override
					public void widgetSelected(final SelectionEvent e) {
						doSaveAs();
					}
				}, SWT.RIGHT);
				break;

		}

	}

}