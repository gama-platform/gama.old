/*********************************************************************************************
 *
 * 'GamlQuickOutlinePopup.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.editor.toolbar;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.xtext.ui.editor.outline.quickoutline.QuickOutlinePopup;

import msi.gama.lang.gaml.ui.editor.GamlEditor;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;

/**
 * The class GamlQuickOutlinePopup.
 * 
 * @author drogoul
 * @since 3 mars 2015
 * 
 */
public class GamlQuickOutlinePopup extends QuickOutlinePopup {

	private final Control textWidget;
	private final GamaToolbar2 toolbar;

	public GamlQuickOutlinePopup(final GamlEditor editor, final GamaToolbar2 toolbar) {
		super(editor.getEditorSite().getShell());
		setEditor(editor);
		this.textWidget = editor.getInternalSourceViewer().getTextWidget();
		this.toolbar = toolbar;
		setInput(editor.getDocument());
		setEvent(new Event());
	}

	@Override
	protected TreeViewer createTreeViewer(final Composite parent, final int style) {
		final TreeViewer viewer = super.createTreeViewer(parent, style);
		viewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);
		return viewer;
	}

	@Override
	protected Point getDefaultLocation(final Point initialSize) {
		final Point loc = textWidget.getLocation();
		final Point popupLocation = new Point(loc.x, 24);
		return toolbar.toDisplay(popupLocation);
	}

	@Override
	protected Point getInitialLocation(final Point initialSize) {
		final Point loc = textWidget.getLocation();
		final Point popupLocation = new Point(loc.x, 24);
		return toolbar.toDisplay(popupLocation);
	}

	@Override
	protected Point getInitialSize() {
		final Point size = textWidget.getSize();
		return new Point(size.x, size.y / 2);
	}

	@Override
	protected Point getDefaultSize() {
		final Point size = textWidget.getSize();
		return new Point(size.x, size.y / 2);
	}

	@Override
	protected Color getBackground() {
		return IGamaColors.WHITE.color();
	}

	@Override
	protected Color getForeground() {
		return IGamaColors.BLACK.color();
	}

	@Override
	protected Text createFilterText(final Composite parent) {
		final Text filterText = new Text(parent, SWT.SEARCH | SWT.ICON_SEARCH);
		filterText.setMessage("Search keyword");
		Dialog.applyDialogFont(filterText);

		final GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.CENTER;
		filterText.setLayoutData(data);

		filterText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				if (e.keyCode == 0x0D) {
					gotoSelectedElement();
				}
				if (e.keyCode == SWT.ARROW_DOWN) {
					getTreeViewer().getTree().setFocus();
				}
				if (e.keyCode == SWT.ARROW_UP) {
					getTreeViewer().getTree().setFocus();
				}
				if (e.character == 0x1B) {
					dispose();
				}
			}
		});
		return filterText;
	}

}
