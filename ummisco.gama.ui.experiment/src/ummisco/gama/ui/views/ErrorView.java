/*********************************************************************************************
 *
 * 'ErrorView.java, in plugin ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.IRuntimeExceptionHandler;
import msi.gama.common.interfaces.ItemList;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaFonts;
import ummisco.gama.ui.utils.PreferencesHelper;
import ummisco.gama.ui.utils.WebHelper;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class ErrorView extends ExpandableItemsView<GamaRuntimeException> implements IGamaView.Error {

	public static String ID = IGui.ERROR_VIEW_ID;
	int numberOfDisplayedErrors = GamaPreferences.Runtime.CORE_ERRORS_NUMBER.getValue();
	boolean mostRecentFirst = GamaPreferences.Runtime.CORE_RECENT.getValue();

	@Override
	protected boolean areItemsClosable() {
		return true;
	}

	@Override
	public boolean addItem(final GamaRuntimeException e) {
		createItem(parent, e, false, null);

		return true;
	}

	@Override
	public void displayErrors() {
		reset();
	}

	@Override
	public void ownCreatePartControl(final Composite view) {}

	@Override
	protected Composite createItemContentsFor(final GamaRuntimeException exception) {
		final ScrolledComposite compo = new ScrolledComposite(getViewer(), SWT.H_SCROLL);
		final GridLayout layout = new GridLayout(1, false);
		final GridData firstColData = new GridData(SWT.FILL, SWT.FILL, true, true);
		layout.verticalSpacing = 5;
		compo.setLayout(layout);
		final Table t = new Table(compo, SWT.H_SCROLL);
		t.setFont(GamaFonts.getExpandfont());

		t.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				GAMA.getGui().editModel(null, exception.getEditorContext());
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {}
		});
		t.setLayoutData(firstColData);
		final java.util.List<String> strings = exception.getContextAsList();
		t.setForeground(exception.isWarning() ? GamaColors.get(PreferencesHelper.WARNING_TEXT_COLOR.getValue()).color()
				: GamaColors.get(PreferencesHelper.ERROR_TEXT_COLOR.getValue()).color());
		final TableColumn c = new TableColumn(t, SWT.NONE);
		c.setResizable(true);
		final TableColumn column2 = new TableColumn(t, SWT.NONE);
		for (int i = 0; i < strings.size(); i++) {
			final TableItem item = new TableItem(t, SWT.NONE);
			item.setText(new String[] { String.valueOf(i), strings.get(i) });
		}
		c.pack();
		column2.pack();
		t.setSize(t.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		t.pack();
		compo.setContent(t);
		compo.pack();
		return compo;
	}

	@Override
	public void setFocus() {

	}

	private IRuntimeExceptionHandler getExceptionHandler() {
		return WorkbenchHelper.getService(IRuntimeExceptionHandler.class);
	}

	@Override
	public void removeItem(final GamaRuntimeException obj) {
		getExceptionHandler().remove(obj);
	}

	@Override
	public void pauseItem(final GamaRuntimeException obj) {}

	@Override
	public void resumeItem(final GamaRuntimeException obj) {}

	@Override
	public String getItemDisplayName(final GamaRuntimeException obj, final String previousName) {
		final StringBuilder sb = new StringBuilder(300);
		final String a = obj.getAgentSummary();
		if (a != null) {
			sb.append(a).append(" at ");
		}
		sb.append("cycle ").append(obj.getCycle()).append(ItemList.SEPARATION_CODE)
				.append(obj.isWarning() ? ItemList.WARNING_CODE : ItemList.ERROR_CODE).append(obj.getMessage());
		return sb.toString();
	}

	@Override
	public void focusItem(final GamaRuntimeException data) {
		// gotoEditor(data);
	}

	@Override
	public List<GamaRuntimeException> getItems() {
		final List<GamaRuntimeException> errors = new ArrayList<>();
		final List<GamaRuntimeException> exceptions = getExceptionHandler().getCleanExceptions();
		final int size = exceptions.size();
		if (size == 0) { return errors; }
		final int end = size;
		int begin = end - numberOfDisplayedErrors;
		begin = begin < 0 ? 0 : begin;
		final List<GamaRuntimeException> except = new ArrayList<>(exceptions);
		errors.addAll(except.subList(begin, end));
		if (mostRecentFirst) {
			Collections.reverse(errors);
		}
		return errors;
	}

	@Override
	public void updateItemValues() {
		this.getViewer().updateItemNames();
	}

	@Override
	public void reset() {
		WorkbenchHelper.run(() -> {
			ErrorView.super.reset();
			displayItems();
			parent.layout(true, true);
		});

	}

	/**
	 * Method handleMenu()
	 * 
	 * @see msi.gama.common.interfaces.ItemList#handleMenu(java.lang.Object)
	 */
	@Override
	public Map<String, Runnable> handleMenu(final GamaRuntimeException item, final int x, final int y) {
		final Map<String, Runnable> result = new HashMap<>();
		result.put("Copy error to clipboard", () -> {
			final Clipboard clipboard = new Clipboard(parent.getDisplay());
			final String data = item.getAllText();
			clipboard.setContents(new Object[] { data }, new Transfer[] { TextTransfer.getInstance() });
			clipboard.dispose();
		});
		result.put("Show in editor", () -> GAMA.getGui().editModel(null, item.getEditorContext()));
		result.put("Report issue on GitHub", () -> this.reportError(item));
		return result;
	}

	private void reportError(final GamaRuntimeException item) {
		final String data = item.getAllText();
		WebHelper.openPage("https://github.com/gama-platform/gama/issues/new");
	}

	@Override
	protected boolean needsOutput() {
		return false;
	}

	@Override
	public void dispose() {
		super.dispose();
	}

}
