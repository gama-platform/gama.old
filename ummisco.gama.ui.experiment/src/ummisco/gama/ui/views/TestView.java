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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.jgrapht.alg.util.Pair;

import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.ItemList;
import msi.gama.runtime.GAMA;
import msi.gama.util.GamaColor;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.statements.test.TestStatement;
import msi.gaml.statements.test.TestStatement.TestSummary;
import ummisco.gama.ui.parameters.AbstractEditor;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.utils.SwtGui;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.inspectors.ExpandableItemsView;

public class TestView extends ExpandableItemsView<TestSummary> implements IGamaView.Test {

	public final static int EDITORS_SPACING = 0;
	public final Map<TestSummary, Map<String, AssertEditor>> editors = new TOrderedHashMap<>();

	class ItemComposite extends Composite {

		public ItemComposite(final Composite parent, final int style) {
			super(parent, style);
			final GridLayout layout = new GridLayout(2, false);
			layout.verticalSpacing = EDITORS_SPACING;
			setLayout(layout);
		}

		public void addEditor(final AbstractEditor<?> editor) {
			editor.createComposite(this);
		}

	}

	public static String ID = IGui.TEST_VIEW_ID;

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		if (!SwtGui.PERSISTENT_TEST_VIEW) {
			editors.clear();
			super.reset();
		}
	}

	@Override
	protected boolean areItemsClosable() {
		return false;
	}

	@Override
	public void addTestResult(final TestSummary e) {
		if (!editors.containsKey(e))
			editors.put(e, new TOrderedHashMap<>());
		reset();
	}

	@Override
	public boolean addItem(final TestSummary e) {
		final Map<String, AssertEditor> inside = editors.get(e);
		if (inside == null || inside.isEmpty())
			createItem(parent, e, false, GamaColors.get(getItemDisplayColor(e)));
		else
			updateItem(e);
		return true;
	}

	public void updateItem(final TestSummary test) {
		final Map<String, AssertEditor> inside = editors.get(test);
		final Collection<Pair<String, TestStatement.State>> assertions = test.getAssertions();
		for (final Pair<String, TestStatement.State> assertion : assertions) {
			final AssertEditor ed = inside.get(assertion.getFirst());
			ed.updateValueWith(assertion);
		}
	}

	@Override
	public void ownCreatePartControl(final Composite view) {}

	@Override
	protected Composite createItemContentsFor(final TestSummary test) {
		final Collection<Pair<String, TestStatement.State>> assertions = test.getAssertions();
		final ItemComposite compo = new ItemComposite(getViewer(), SWT.NONE);
		compo.setBackground(getViewer().getBackground());
		for (final Pair<String, TestStatement.State> assertion : assertions) {
			final AssertEditor ed = new AssertEditor(GAMA.getRuntimeScope(), assertion);
			editors.get(test).put(assertion.getFirst(), ed);
			compo.addEditor(ed);
		}
		return compo;
	}

	@Override
	public void setFocus() {}

	@Override
	public void removeItem(final TestSummary obj) {}

	@Override
	public void pauseItem(final TestSummary obj) {}

	@Override
	public void resumeItem(final TestSummary obj) {}

	@Override
	public String getItemDisplayName(final TestSummary obj, final String previousName) {
		final StringBuilder sb = new StringBuilder(300);
		final String name = obj.testName;
		sb.append(obj.getState()).append(ItemList.SEPARATION_CODE).append(obj.testName).append(" in ")
				.append(obj.modelName).append(" ");
		return sb.toString();
	}

	@Override
	protected boolean shouldBeClosedWhenNoExperiments() {
		return !SwtGui.PERSISTENT_TEST_VIEW;
	}

	@Override
	public GamaColor getItemDisplayColor(final TestSummary t) {
		return t.getState().getColor();
	}

	@Override
	public void focusItem(final TestSummary data) {}

	@Override
	public List<TestSummary> getItems() {
		return new ArrayList<TestSummary>(editors.keySet());
	}

	@Override
	public void updateItemValues() {
		this.getViewer().updateItemNames();
		this.getViewer().updateItemColors();
	}

	@Override
	public void reset() {
		WorkbenchHelper.run(() -> {
			// TestView.super.reset();
			displayItems();
			updateItemValues();
			parent.layout(true, true);
		});

	}

	/**
	 * Method handleMenu()
	 * 
	 * @see msi.gama.common.interfaces.ItemList#handleMenu(java.lang.Object)
	 */
	@Override
	public Map<String, Runnable> handleMenu(final TestSummary item, final int x, final int y) {
		final Map<String, Runnable> result = new HashMap<>();
		result.put("Copy summary to clipboard", () -> {
			final Clipboard clipboard = new Clipboard(parent.getDisplay());
			final String data = item.toString();
			clipboard.setContents(new Object[] { data }, new Transfer[] { TextTransfer.getInstance() });
			clipboard.dispose();
		});
		result.put("Show in editor", () -> GAMA.getGui().editModel(null, item.uri));
		return result;
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
