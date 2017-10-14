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

import static msi.gama.common.preferences.GamaPreferences.Modeling.TESTS_SORTED;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.jgrapht.alg.util.Pair;

import com.google.common.primitives.Ints;

import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.ItemList;
import msi.gama.runtime.GAMA;
import msi.gama.util.GamaColor;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.statements.test.TestStatement;
import msi.gaml.statements.test.TestStatement.State;
import msi.gaml.statements.test.TestStatement.TestSummary;
import ummisco.gama.ui.controls.ParameterExpandItem;
import ummisco.gama.ui.parameters.AbstractEditor;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.utils.SwtGui;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.inspectors.ExpandableItemsView;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;

public class TestView extends ExpandableItemsView<TestSummary> implements IGamaView.Test {

	static final Comparator<TestSummary> BY_ORDER = (o1, o2) -> Ints.compare(o1.number, o2.number);
	static final Comparator<TestSummary> BY_SEVERITY = (o1, o2) -> {
		final State s1 = o1.getState();
		final State s2 = o2.getState();
		if (s1 == s2)
			return BY_ORDER.compare(o1, o2);
		else
			return s1.compareTo(s2);
	};
	public final Map<TestSummary, Map<String, AssertEditor>> editors = new HashMap<>();
	public final List<TestSummary> sortedEditors = new ArrayList<>();
	public static final GridLayout layout = new GridLayout(2, false);
	static {
		layout.verticalSpacing = 0;
	}

	public static String ID = IGui.TEST_VIEW_ID;

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		if (!SwtGui.ALL_TESTS_RUNNING) {
			editors.clear();
			sortedEditors.clear();
			super.reset();
		}
	}

	@Override
	protected boolean areItemsClosable() {
		return false;
	}

	protected void resortTests() {
		final Comparator<TestSummary> comp = TESTS_SORTED.getValue() ? BY_SEVERITY : BY_ORDER;
		sortedEditors.sort(comp);
	}

	@Override
	public void addTestResult(final TestSummary e) {
		if (e == TestSummary.FINISHED || e == TestSummary.INDIVIDUAL_TEST_FINISHED) {
			TestView.super.reset();
			editors.clear();
			reset();
			return;
		}
		if (e == TestSummary.BEGINNING) {
			editors.clear();
			sortedEditors.clear();
			super.reset();
			return;
		}
		if (!editors.containsKey(e)) {
			editors.put(e, null);
			sortedEditors.add(e);
		}
		if (!SwtGui.ALL_TESTS_RUNNING)
			reset();
		WorkbenchHelper.run(() -> {
			if (toolbar != null)
				toolbar.status(null, createTestsSummary(), null, IGamaColors.BLUE, SWT.LEFT);
		});
	}

	protected String createTestsSummary() {
		final Map<State, Integer> map = new TreeMap<>();
		sortedEditors.forEach(t -> {
			final State s = t.getState();
			if (map.containsKey(s)) {
				map.put(s, map.get(s) + 1);
			} else
				map.put(s, 1);
		});
		for (final State s : map.keySet()) {
			if (map.get(s) == 0)
				map.remove(s);
		}
		String message = "" + editors.size() + " tests";
		for (final Object s : map.keySet()) {
			message += ", " + (map.size() == 1 ? "all" : String.valueOf(map.get(s))) + " " + s;
		}
		return message;
	}

	@Override
	public boolean addItem(final TestSummary e) {
		final Map<String, AssertEditor> inside = editors.get(e);
		if (inside == null)
			createItem(parent, e, false, GamaColors.get(getItemDisplayColor(e)));
		else
			updateItem(e);
		return true;
	}

	public void updateItem(final TestSummary test) {
		// System.out.println("test " + test.testName + " is already present. Just updating it");
		final ParameterExpandItem item = getViewer().getItem(test);
		item.setText(this.getItemDisplayName(test, item.getText()));
		item.setColor(this.getItemDisplayColor(test));
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
		// System.out.println("test " + test.testName + " is not present. Creating it");
		final Collection<Pair<String, TestStatement.State>> assertions = test.getAssertions();
		final Composite compo = new Composite(getViewer(), SWT.NONE);
		compo.setLayout(layout);
		compo.setBackground(getViewer().getBackground());
		editors.put(test, new TOrderedHashMap<>());
		for (final Pair<String, TestStatement.State> assertion : assertions) {
			final AssertEditor ed = new AssertEditor(GAMA.getRuntimeScope(), assertion);
			editors.get(test).put(assertion.getFirst(), ed);
			((AbstractEditor<?>) ed).createComposite(compo);
		}
		return compo;
	}

	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		super.createToolItems(tb);
		final ToolItem t = tb.check(GamaIcons.create("test.sort2").getCode(), "Sort by severity",
				"When checked, sort the tests by their decreasing state severity (i.e. aborted > failed > warning > passed > not run). Otherwise they are sorted by their order of execution.",
				new SelectionAdapter() {

					@Override
					public void widgetSelected(final SelectionEvent e) {
						TESTS_SORTED.set(!TESTS_SORTED.getValue());
						TestView.super.reset();
						editors.clear();
						reset();
					}

				}, SWT.RIGHT);
		t.setSelection(TESTS_SORTED.getValue());
		TESTS_SORTED.onChange(v -> t.setSelection(v));
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
		return !SwtGui.ALL_TESTS_RUNNING;
	}

	@Override
	public GamaColor getItemDisplayColor(final TestSummary t) {
		return t.getState().getColor();
	}

	@Override
	public void focusItem(final TestSummary data) {}

	@Override
	public List<TestSummary> getItems() {
		return sortedEditors;
	}

	@Override
	public void updateItemValues() {}

	@Override
	public void reset() {
		WorkbenchHelper.run(() -> {
			if (!parent.isDisposed()) {
				resortTests();
				displayItems();
				parent.layout(true, false);
			}
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

}
